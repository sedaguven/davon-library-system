import apiClient from './api';
import { User } from '@/lib/types';

export interface AuthResponseDTO {
  token: string;
  user: {
    id: number;
    name: string;
    email: string;
    role: 'admin' | 'user';
  };
}

export interface Credentials {
    email: string;
    password: string;
}

const authService = {
  async login(credentials: Credentials): Promise<AuthResponseDTO | null> {
    try {
  const response = await apiClient.post<AuthResponseDTO>('/auth/login', credentials);
      if (response.data && response.data.token) {
        localStorage.setItem('token', response.data.token);
      }
      return response.data;
    } catch (error) {
      console.error('Login failed:', error);
      return null;
    }
  },

  async getProfile(): Promise<User | null> {
    try {
      const response = await apiClient.get<User>('/users/profile');
      return response.data;
    } catch (error) {
      console.error('Failed to fetch profile:', error);
      return null;
    }
  },

  logout() {
    localStorage.removeItem('token');
  },
};

export default authService; 