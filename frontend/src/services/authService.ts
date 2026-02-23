import api from './api';
import type { AuthResponse } from '../types';

export const authService = {
  register: async (username: string, email: string,
                    password: string): Promise<AuthResponse> => {
    const response = await api.post('/api/auth/register', {
      username,
      email,
      password
    });
    return response.data;
  },

  login: async (username: string,
                 password: string): Promise<AuthResponse> => {
    const response = await api.post('/api/auth/login', {
      username,
      password
    });
    return response.data;
  },

  logout: () => {
    localStorage.removeItem('token');
    localStorage.removeItem('user');
  },

  getCurrentUser: () => {
    const userStr = localStorage.getItem('user');
    return userStr ? JSON.parse(userStr) : null;
  },

  isAuthenticated: () => !!localStorage.getItem('token'),
};
