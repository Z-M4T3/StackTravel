import { apiClient } from './apiClient.js';
import { CONFIG } from '../config.js';

export const authService = {
  async login(credentials) {
    const payload = {
      usernameOrEmail: credentials.usernameOrEmail || credentials.username || credentials.email,
      password: credentials.password
    };

    const token = await apiClient.post('/users/login', payload);
    if (!token || typeof token !== 'string') {
      throw new Error('Sikertelen bejelentkezés.');
    }

    localStorage.setItem(CONFIG.STORAGE_KEYS.AUTH_TOKEN, token.trim());

    try {
      const user = await apiClient.get('/users/me');
      this.setSession(user, token.trim());
      return user;
    } catch {
      const fallbackUser = {
        username: payload.usernameOrEmail,
        email: payload.usernameOrEmail.includes('@') ? payload.usernameOrEmail : ''
      };
      this.setSession(fallbackUser, token.trim());
      return fallbackUser;
    }
  },

  async register(userData) {
    const registeredUser = await apiClient.post('/users/register', {
      username: userData.username,
      email: userData.email,
      password: userData.password,
      birthDate: userData.birthDate || null
    });

    try {
      return await this.login({
        usernameOrEmail: userData.username,
        password: userData.password
      });
    } catch {
      if (registeredUser) {
        this.setSession(registeredUser, null);
      }
      return registeredUser;
    }
  },

  async getProfile() {
    return await apiClient.get('/users/me');
  },

  async updateProfile(userData) {
    const updated = await apiClient.patch('/users/me', userData);
    const current = this.getCurrentUser() || {};
    const merged = { ...current, ...updated };
    localStorage.setItem(CONFIG.STORAGE_KEYS.AUTH_USER, JSON.stringify(merged));
    return merged;
  },

  async changePassword(password, newPassword) {
    return await apiClient.post('/users/change-password', { password, newPassword });
  },

  async requestPasswordReset(email) {
    return await apiClient.post('/users/password-reset', email);
  },

  async confirmPasswordReset(token, newPassword) {
    return await apiClient.post(`/users/password-reset/confirm?token=${encodeURIComponent(token)}`, {
      newPassword
    });
  },

  async deleteAccount() {
    await apiClient.delete('/users/me');
    await this.logout();
  },

  setSession(user, token) {
    if (user) {
      localStorage.setItem(CONFIG.STORAGE_KEYS.AUTH_USER, JSON.stringify(user));
    }
    if (token) {
      localStorage.setItem(CONFIG.STORAGE_KEYS.AUTH_TOKEN, token);
    }
  },

  getCurrentUser() {
    const raw = localStorage.getItem(CONFIG.STORAGE_KEYS.AUTH_USER);
    if (!raw) return null;
    try {
      return JSON.parse(raw);
    } catch {
      return null;
    }
  },

  async logout() {
    localStorage.removeItem(CONFIG.STORAGE_KEYS.AUTH_USER);
    localStorage.removeItem(CONFIG.STORAGE_KEYS.AUTH_TOKEN);
  },

  isAuthenticated() {
    return !!(this.getCurrentUser() && localStorage.getItem(CONFIG.STORAGE_KEYS.AUTH_TOKEN));
  }
};
