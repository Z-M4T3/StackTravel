import { store } from '../state/store.js';
import { authService } from '../api/authService.js';
import { showToast } from '../utils/domHelpers.js';

export function renderNavMenu(container) {
  const state = store.getState();
  const user = state.user;
  const activeView = state.activeView;

  container.innerHTML = `
    <div class="nav-section">
      <div class="nav-section-title">Utazás</div>
      <ul class="nav-list">
        <li>
          <button class="nav-item-btn ${activeView === 'trips' ? 'active' : ''}" data-view="trips" type="button">
            <span>Utazások</span>
            <span class="nav-badge-pill">${state.trips.length}</span>
          </button>
        </li>
        <li>
          <button class="nav-item-btn ${activeView === 'planner' ? 'active' : ''}" data-view="planner" type="button">
            <span>Tervezés</span>
            <span class="nav-badge-pill">${state.tripItems.length}</span>
          </button>
        </li>
        <li>
          <button class="nav-item-btn ${activeView === 'calendar' ? 'active' : ''}" data-view="calendar" type="button">
            <span>Naptár</span>
          </button>
        </li>
      </ul>
    </div>

    <div class="nav-section">
      <div class="nav-section-title">Fiók</div>
      ${user ? `
        <div class="user-profile-box">
          <img src="${user.picture || 'assets/icons/user.svg'}" class="user-avatar-img" alt="${user.username}" />
          <div class="user-info-text">
            <div class="user-name-label">${user.username}</div>
            <div class="user-email-label">${user.email}</div>
          </div>
        </div>
        <ul class="nav-list">
          <li>
            <button class="nav-item-btn" id="btn-open-profile" type="button">
              <span>Profil</span>
              <img src="assets/icons/user.svg" alt="" width="14" height="14" />
            </button>
          </li>
          <li>
            <button class="nav-item-btn" id="btn-logout" type="button">
              <span style="color: var(--color-danger);">Kijelentkezés</span>
              <img src="assets/icons/close.svg" alt="" width="14" height="14" />
            </button>
          </li>
        </ul>
      ` : `
        <ul class="nav-list">
          <li>
            <button class="nav-item-btn" id="btn-open-login" type="button">
              <span>Belépés</span>
              <img src="assets/icons/user.svg" alt="" width="14" height="14" />
            </button>
          </li>
          <li>
            <button class="nav-item-btn" id="btn-open-register" type="button">
              <span>Regisztráció</span>
              <img src="assets/icons/plus.svg" alt="" width="14" height="14" />
            </button>
          </li>
        </ul>
      `}
    </div>
  `;

  container.querySelectorAll('[data-view]').forEach(btn => {
    btn.addEventListener('click', () => {
      const view = btn.dataset.view;
      store.setActiveView(view);
    });
  });

  const quickToggleBtn = container.querySelector('#btn-quick-toggle-auth');
  if (quickToggleBtn) {
    quickToggleBtn.addEventListener('click', async () => {
      if (user) {
        await store.handleLogout();
        showToast('Kijelentkezve.');
      } else {
        openAuthModal('login');
      }
    });
  }

  const logoutBtn = container.querySelector('#btn-logout');
  if (logoutBtn) {
    logoutBtn.addEventListener('click', async () => {
      await store.handleLogout();
      showToast('Sikeres kijelentkezés.');
    });
  }

  const loginBtn = container.querySelector('#btn-open-login');
  if (loginBtn) {
    loginBtn.addEventListener('click', () => {
      openAuthModal('login');
    });
  }

  const registerBtn = container.querySelector('#btn-open-register');
  if (registerBtn) {
    registerBtn.addEventListener('click', () => {
      openAuthModal('register');
    });
  }

  const profileBtn = container.querySelector('#btn-open-profile');
  if (profileBtn) {
    profileBtn.addEventListener('click', () => {
      openAuthModal('profile');
    });
  }
}

export function openAuthModal(mode) {
  let modalOverlay = document.querySelector('#auth-modal-overlay');
  if (!modalOverlay) {
    modalOverlay = document.createElement('div');
    modalOverlay.id = 'auth-modal-overlay';
    modalOverlay.className = 'modal-overlay';
    document.body.appendChild(modalOverlay);
  }

  const user = store.getState().user;

  if (mode === 'login') {
    modalOverlay.innerHTML = `
      <div class="modal-box">
        <div class="modal-header">
          <h3 class="modal-title">Belépés StackTravelbe</h3>
          <button class="modal-close-btn" id="modal-close" type="button">
            <img src="assets/icons/close.svg" alt="" width="16" height="16" />
          </button>
        </div>
        <form id="form-login">
          <div class="modal-body">
            <div class="form-group">
              <label class="form-label" for="login-username">Felhasználónév vagy E-mail</label>
              <input class="form-input" id="login-username" type="text" required placeholder="Felhasználónév vagy e-mail cím" />
            </div>
            <div class="form-group">
              <label class="form-label" for="login-password">Jelszó</label>
              <input class="form-input" id="login-password" type="password" required placeholder="Jelszó" />
            </div>
          </div>
          <div class="modal-footer">
            <button class="btn-secondary" type="button" id="modal-cancel">Mégse</button>
            <button class="btn-primary" type="submit">Belépés</button>
          </div>
        </form>
      </div>
    `;

    const form = modalOverlay.querySelector('#form-login');
    form.addEventListener('submit', async (e) => {
      e.preventDefault();
      const usernameOrEmail = form.querySelector('#login-username').value;
      const password = form.querySelector('#login-password').value;
      try {
        const loggedUser = await authService.login({ usernameOrEmail, password });
        await store.handleLogin(loggedUser);
        modalOverlay.classList.remove('active');
        showToast(`Üdvözlünk, ${loggedUser.username}!`);
      } catch (err) {
        showToast(err.message || 'Sikertelen bejelentkezés.');
      }
    });
  } else if (mode === 'register') {
    modalOverlay.innerHTML = `
      <div class="modal-box">
        <div class="modal-header">
          <h3 class="modal-title">Új fiók létrehozása</h3>
          <button class="modal-close-btn" id="modal-close" type="button">
            <img src="assets/icons/close.svg" alt="" width="16" height="16" />
          </button>
        </div>
        <form id="form-register">
          <div class="modal-body">
            <div class="form-group">
              <label class="form-label" for="reg-username">Felhasználónév</label>
              <input class="form-input" id="reg-username" type="text" required placeholder="Felhasználónév" />
            </div>
            <div class="form-group">
              <label class="form-label" for="reg-email">E-mail cím</label>
              <input class="form-input" id="reg-email" type="email" required placeholder="E-mail cím" />
            </div>
            <div class="form-group">
              <label class="form-label" for="reg-password">Jelszó</label>
              <input class="form-input" id="reg-password" type="password" required placeholder="Jelszó (legalább 8 karakter)" />
            </div>
            <div class="form-group">
              <label class="form-label" for="reg-birth">Születési dátum</label>
              <input class="form-input" id="reg-birth" type="date" />
            </div>
          </div>
          <div class="modal-footer">
            <button class="btn-secondary" type="button" id="modal-cancel">Mégse</button>
            <button class="btn-primary" type="submit">Regisztráció</button>
          </div>
        </form>
      </div>
    `;

    const form = modalOverlay.querySelector('#form-register');
    form.addEventListener('submit', async (e) => {
      e.preventDefault();
      const username = form.querySelector('#reg-username').value;
      const email = form.querySelector('#reg-email').value;
      const password = form.querySelector('#reg-password').value;
      const birthDate = form.querySelector('#reg-birth').value;

      try {
        const newUser = await authService.register({ username, email, password, birthDate });
        await store.handleLogin(newUser);
        modalOverlay.classList.remove('active');
        showToast(`Sikeres regisztráció: ${newUser.username}!`);
      } catch (err) {
        showToast(err.message || 'Sikertelen regisztráció.');
      }
    });
  } else if (mode === 'profile') {
    modalOverlay.innerHTML = `
      <div class="modal-box">
        <div class="modal-header">
          <h3 class="modal-title">Felhasználói Profil</h3>
          <button class="modal-close-btn" id="modal-close" type="button">
            <img src="assets/icons/close.svg" alt="" width="16" height="16" />
          </button>
        </div>
        <div class="modal-body">
          <div style="display: flex; align-items: center; gap: 16px; margin-bottom: 20px;">
            <img src="${user?.picture || 'assets/icons/user.svg'}" class="user-avatar-img" style="width: 56px; height: 56px;" alt="" />
            <div>
              <h4 style="font-size: 16px; font-weight: 700; color: var(--color-primary);">${user?.username}</h4>
              <div style="font-size: 13px; color: var(--color-text-muted);">${user?.email}</div>
            </div>
          </div>
          <div class="form-group">
            <label class="form-label">Születési idő</label>
            <input class="form-input" type="text" readonly value="${user?.birthDate || 'Nincs megadva'}" />
          </div>
        </div>
        <div class="modal-footer">
          <button class="btn-primary" type="button" id="modal-close-bottom">Bezárás</button>
        </div>
      </div>
    `;

    const closeBottom = modalOverlay.querySelector('#modal-close-bottom');
    if (closeBottom) {
      closeBottom.addEventListener('click', () => modalOverlay.classList.remove('active'));
    }
  }

  const closeBtn = modalOverlay.querySelector('#modal-close');
  if (closeBtn) {
    closeBtn.addEventListener('click', () => modalOverlay.classList.remove('active'));
  }

  const cancelBtn = modalOverlay.querySelector('#modal-cancel');
  if (cancelBtn) {
    cancelBtn.addEventListener('click', () => modalOverlay.classList.remove('active'));
  }

  modalOverlay.classList.add('active');
}
