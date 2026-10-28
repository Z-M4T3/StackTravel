#!/usr/bin/env bash
set -euo pipefail

# ==============================================================================
# Univerzális Szerveroldali Deploy Script
# Támogatja a direct in-place és az atomi releases (symlink) kiadásokat is
# ==============================================================================

STAGE_DIR="${1:-}"
if [ -z "${STAGE_DIR}" ] || [ ! -d "${STAGE_DIR}" ]; then
    echo "HIBA: Érvénytelen stage könyvtár: ${STAGE_DIR}"
    exit 1
fi

TAR_FILE="${STAGE_DIR}/release.tar.gz"
if [ ! -f "${TAR_FILE}" ]; then
    echo "HIBA: A release.tar.gz nem található itt: ${STAGE_DIR}"
    exit 1
fi

# 1. Konfiguráció kicsomagolása és betöltése (ha még nincs kint)
if [ ! -f "${STAGE_DIR}/.deploy.conf" ]; then
    tar -xzf "${TAR_FILE}" -C "${STAGE_DIR}" ./.deploy.conf 2>/dev/null || tar -xzf "${TAR_FILE}" -C "${STAGE_DIR}" .deploy.conf 2>/dev/null || true
fi

if [ ! -f "${STAGE_DIR}/.deploy.conf" ]; then
    echo "HIBA: A .deploy.conf nem található a csomagban!"
    exit 1
fi

# shellcheck disable=SC1091
source "${STAGE_DIR}/.deploy.conf"

echo "========================================================="
echo " Deploy: ${PROJECT_NAME} | Típus: ${PROJECT_TYPE}"
echo " Célkönyvtár: ${DEPLOY_PATH}"
echo " Szolgáltatás: ${SERVICE_NAME:-[Nincs megadva]}"
echo "========================================================="

# 2. Célkönyvtár létrehozása és jogosultságok ellenőrzése
sudo mkdir -p "${DEPLOY_PATH}"
sudo chown -R "$(whoami)":"$(whoami)" "${DEPLOY_PATH}"

# 3. Kicsomagolás (Releases mód vagy Direct mód)
USE_RELEASES="${USE_RELEASES:-false}"
if [ -d "${DEPLOY_PATH}/releases" ]; then
    USE_RELEASES="true"
fi

if [ "${USE_RELEASES}" = "true" ]; then
    TIMESTAMP=$(date +"%Y%m%d%H%M%S")
    RELEASES_DIR="${DEPLOY_PATH}/releases"
    CURRENT_LINK="${DEPLOY_PATH}/current"
    NEW_RELEASE_DIR="${RELEASES_DIR}/${TIMESTAMP}"
    PREVIOUS_RELEASE=""
    if [ -L "${CURRENT_LINK}" ]; then
        PREVIOUS_RELEASE=$(readlink -f "${CURRENT_LINK}" || true)
    fi

    mkdir -p "${NEW_RELEASE_DIR}"
    echo "--> Új kiadás kibontása: ${NEW_RELEASE_DIR}..."
    tar -xzf "${TAR_FILE}" -C "${NEW_RELEASE_DIR}"

    # Artifactok átvétele
    for file in "${STAGE_DIR}"/*; do
        fname=$(basename "$file")
        if [ "$fname" != "release.tar.gz" ] && [ "$fname" != "deploy.sh" ] && [ "$fname" != "deploy.env" ] && [ "$fname" != ".deploy.conf" ]; then
            echo "--> Artifact átvétele: $fname"
            cp -r "$file" "${NEW_RELEASE_DIR}/"
        fi
    done

    # Környezeti fájl (.env) kezelése
    if [ -f "${STAGE_DIR}/deploy.env" ]; then
        DEST_ENV="${NEW_RELEASE_DIR}/${ENV_DESTINATION:-.env}"
        echo "--> Új környezeti fájl (.env) elhelyezése: ${DEST_ENV}"
        mkdir -p "$(dirname "${DEST_ENV}")"
        cp "${STAGE_DIR}/deploy.env" "${DEST_ENV}"
        chmod 600 "${DEST_ENV}"
        if [ -d "${DEPLOY_PATH}/shared" ]; then
            cp "${STAGE_DIR}/deploy.env" "${DEPLOY_PATH}/shared/karakterlap.env"
            chmod 600 "${DEPLOY_PATH}/shared/karakterlap.env"
        fi
    elif [ -f "${DEPLOY_PATH}/shared/karakterlap.env" ]; then
        echo "--> Meglévő shared/karakterlap.env bekötése..."
        mkdir -p "${NEW_RELEASE_DIR}/api"
        ln -sfn "${DEPLOY_PATH}/shared/karakterlap.env" "${NEW_RELEASE_DIR}/api/.env"
    fi

    APP_DIR="${NEW_RELEASE_DIR}"
else
    echo "--> Új verzió kicsomagolása a célkönyvtárba: ${DEPLOY_PATH}..."
    tar -xzf "${TAR_FILE}" -C "${DEPLOY_PATH}"

    # Artifactok átvétele
    for file in "${STAGE_DIR}"/*; do
        fname=$(basename "$file")
        if [ "$fname" != "release.tar.gz" ] && [ "$fname" != "deploy.sh" ] && [ "$fname" != "deploy.env" ] && [ "$fname" != ".deploy.conf" ]; then
            echo "--> Artifact átvétele: $fname"
            cp -r "$file" "${DEPLOY_PATH}/"
        fi
    done

    # Környezeti fájl (.env) kezelése
    if [ -f "${STAGE_DIR}/deploy.env" ]; then
        DEST_ENV="${DEPLOY_PATH}/${ENV_DESTINATION:-.env}"
        echo "--> Új környezeti fájl (.env) elhelyezése: ${DEST_ENV}"
        mkdir -p "$(dirname "${DEST_ENV}")"
        cp "${STAGE_DIR}/deploy.env" "${DEST_ENV}"
        chmod 600 "${DEST_ENV}"
    fi

    APP_DIR="${DEPLOY_PATH}"
fi

# 4. Függőségek kezelése
VENV_DIR="${VENV_PATH:-${DEPLOY_PATH}/venv}"

case "${PROJECT_TYPE}" in
    python)
        echo "--> Python virtuális környezet kezelése (${VENV_DIR})..."
        if [ ! -d "${VENV_DIR}" ]; then
            python3 -m venv "${VENV_DIR}"
        fi
        "${VENV_DIR}/bin/pip" install --upgrade pip --quiet

        if [ -f "${APP_DIR}/requirements.txt" ]; then
            "${VENV_DIR}/bin/pip" install -r "${APP_DIR}/requirements.txt" --quiet
        elif [ -f "${APP_DIR}/api/requirements.txt" ]; then
            "${VENV_DIR}/bin/pip" install -r "${APP_DIR}/api/requirements.txt" --quiet
        fi
        ;;

    node|javascript)
        if [ -f "${APP_DIR}/package.json" ]; then
            echo "--> Node.js függőségek telepítése..."
            (cd "${APP_DIR}" && npm install --omit=dev --quiet)
        fi
        ;;

    java)
        echo "--> Java futtatási környezet előkészítve."
        ;;

    c|cpp)
        find "${APP_DIR}" -maxdepth 2 -type f -executable -exec chmod +x {} + 2>/dev/null || true
        ;;

    dotnet|csharp)
        find "${APP_DIR}" -maxdepth 2 -type f -name "${PROJECT_NAME}" -exec chmod +x {} + 2>/dev/null || true
        ;;
esac

# 5. Atomi átkapcsolás (ha Releases mód)
if [ "${USE_RELEASES}" = "true" ]; then
    echo "--> Atomi átkapcsolás az új kiadásra..."
    ln -sfn "${NEW_RELEASE_DIR}" "${CURRENT_LINK}"
fi

# 6. Szolgáltatás újraindítása
if [ -n "${SERVICE_NAME:-}" ]; then
    echo "--> Szolgáltatás újraindítása: ${SERVICE_NAME}"
    sudo systemctl restart "${SERVICE_NAME}"
    sleep 2

    if sudo systemctl is-active --quiet "${SERVICE_NAME}"; then
        echo "--> SIKER: A(z) ${SERVICE_NAME} aktív és fut!"
    else
        echo "--> HIBA: A(z) ${SERVICE_NAME} nem tudott elindulni!"
        if [ "${USE_RELEASES}" = "true" ] && [ -n "${PREVIOUS_RELEASE:-}" ] && [ -d "${PREVIOUS_RELEASE}" ]; then
            echo "--> Automatikus rollback az előző verzióra: ${PREVIOUS_RELEASE}..."
            ln -sfn "${PREVIOUS_RELEASE}" "${CURRENT_LINK}"
            sudo systemctl restart "${SERVICE_NAME}"
        fi
        sudo journalctl -u "${SERVICE_NAME}" -n 25 --no-pager
        exit 1
    fi
fi

# 7. Régi kiadások tisztítása (ha Releases mód)
if [ "${USE_RELEASES}" = "true" ] && [ -d "${RELEASES_DIR}" ]; then
    echo "--> Régi kiadások tisztítása (utolsó 5 megtartása)..."
    cd "${RELEASES_DIR}"
    ls -dt 20* 2>/dev/null | tail -n +6 | xargs -r rm -rf || true
fi

# 8. Healthcheck (opcionális)
if [ -n "${HEALTHCHECK_URL:-}" ]; then
    echo "--> Healthcheck ellenőrzése: ${HEALTHCHECK_URL}"
    SUCCESS=0
    for i in {1..5}; do
        if curl -fsSL -H "X-Forwarded-Proto: https" "${HEALTHCHECK_URL}" > /dev/null 2>&1; then
            echo "--> Healthcheck sikeres!"
            SUCCESS=1
            break
        fi
        echo "Várakozás a válaszra... ($i/5)"
        sleep 2
    done

    if [ "$SUCCESS" -eq 0 ]; then
        echo "HIBA: A szerver nem válaszolt a healthcheck URL-en!"
        exit 1
    fi
fi

echo "========================================================="
echo " Deploy sikeresen befejeződött!"
echo "========================================================="
