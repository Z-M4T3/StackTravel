#!/usr/bin/env bash
set -euo pipefail

STAGE_DIR="$(realpath -e "${1:?Missing deployment directory}")"
[[ "$STAGE_DIR" =~ ^/home/stacktravel-deploy/deploy\.[[:alnum:]]+$ ]]
# shellcheck disable=SC1091
source "$STAGE_DIR/.deploy.conf"

if [[ "$DEPLOY_PATH" != /opt/stacktravel/backend || "$SERVICE_NAME" != stacktravel.service ]]; then
    echo 'Unexpected StackTravel deployment path or service.' >&2
    exit 1
fi

# Fail before replacing the JAR if production credentials are still missing.
python3 - <<'PY'
from pathlib import Path

path = Path('/etc/stacktravel/stacktravel.env')
settings = {}
for line in path.read_text().splitlines():
    if line.strip() and not line.lstrip().startswith('#') and '=' in line:
        key, value = line.split('=', 1)
        settings[key.strip()] = value.strip().strip('\"\'')
required = ('DB_URL', 'DB_USERNAME', 'DB_PASSWORD', 'MAIL_HOST',
            'MAIL_USERNAME', 'MAIL_PASSWORD', 'MAIL_FROM', 'PASSWORD_RESET_URL')
missing = [key for key in required if not settings.get(key)]
if missing:
    raise SystemExit(f'Fill {path} before deployment. Missing: {", ".join(missing)}')
PY

exec 9>"$DEPLOY_PATH/.deploy.lock"
flock -w 10 9

JAR="$DEPLOY_PATH/stacktravel.jar"
BACKUP="$JAR.previous"
CANDIDATE="$(mktemp "$DEPLOY_PATH/.stacktravel.XXXXXXXX.jar")"
trap 'rm -f -- "$CANDIDATE"' EXIT
install -m 0640 "$STAGE_DIR/stacktravel.jar" "$CANDIDATE"

# Check the archive before changing the deployed application.
python3 - "$CANDIDATE" <<'PY'
import sys
import zipfile

with zipfile.ZipFile(sys.argv[1]) as jar:
    if 'BOOT-INF/classes/hu/unideb/inf/Main.class' not in jar.namelist():
        raise SystemExit('The artifact is not a StackTravel Spring Boot JAR.')
    if jar.testzip() is not None:
        raise SystemExit('The JAR is corrupt.')
PY

WAS_ACTIVE=false
if systemctl is-active --quiet "$SERVICE_NAME"; then
    WAS_ACTIVE=true
fi
HAD_PREVIOUS=false
if [[ -f "$JAR" ]]; then
    cp -p -- "$JAR" "$BACKUP"
    HAD_PREVIOUS=true
fi
mv -f -- "$CANDIDATE" "$JAR"

wait_until_ready() {
    for ((attempt = 1; attempt <= 30; attempt++)); do
        if systemctl is-active --quiet "$SERVICE_NAME" &&
            curl --fail --silent --output /dev/null --connect-timeout 1 --max-time 3 "$HEALTHCHECK_URL"; then
            return 0
        fi
        sleep 2
    done
    return 1
}

if sudo -n systemctl restart "$SERVICE_NAME" && wait_until_ready; then
    sudo -n systemctl enable "$SERVICE_NAME"
    echo 'StackTravel deployed successfully.'
else
    echo 'Startup failed; restoring the previous JAR. Check stacktravel.service logs and server configuration.' >&2
    if [[ "$HAD_PREVIOUS" == true ]]; then
        mv -f -- "$BACKUP" "$JAR"
    else
        rm -f -- "$JAR"
    fi
    if [[ "$WAS_ACTIVE" == true ]]; then
        sudo -n systemctl restart "$SERVICE_NAME"
    else
        sudo -n systemctl stop "$SERVICE_NAME"
    fi
    exit 1
fi
