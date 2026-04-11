import api from './api';
import type { LoginRequest, RegisterRequest, AuthResponse } from '../types/auth.types';

const AUTH_CHANGED_EVENT = 'auth-changed';

export const authService = {
  async login(credentials: LoginRequest): Promise<AuthResponse> {
    try {
      const response = await api.post<AuthResponse>('/auth/login', credentials);
      if (response.data.userId) {
        // Store user data and token (if provided by backend)
        localStorage.setItem('user', JSON.stringify(response.data));
        if (response.data.token) {
          localStorage.setItem('authToken', response.data.token);
        }
        window.dispatchEvent(new Event(AUTH_CHANGED_EVENT));
      }
      return response.data;
    } catch (error: any) {
      throw new Error(error.response?.data?.message || 'Login failed');
    }
  },

  async register(userData: RegisterRequest): Promise<AuthResponse> {
    try {
      const response = await api.post<AuthResponse>('/auth/register', userData);
      if (response.data.userId) {
        // Store user data
        localStorage.setItem('user', JSON.stringify(response.data));
        if (response.data.token) {
          localStorage.setItem('authToken', response.data.token);
        }
        window.dispatchEvent(new Event(AUTH_CHANGED_EVENT));
      }
      return response.data;
    } catch (error: any) {
      throw new Error(error.response?.data?.message || 'Registration failed');
    }
  },

  logout(): void {
    localStorage.removeItem('user');
    localStorage.removeItem('authToken');
    window.dispatchEvent(new Event(AUTH_CHANGED_EVENT));
  },

  getCurrentUser(): AuthResponse | null {
    const userStr = localStorage.getItem('user');
    if (userStr) {
      return JSON.parse(userStr);
    }
    return null;
  },

  isAuthenticated(): boolean {
    return !!this.getCurrentUser();
  },
};
