import apiClient from './api';
import { User } from '@/lib/types';

export const userService = {
  async getAllUsers(): Promise<{ users: User[], total: number }> {
    try {
      const response = await apiClient.get<{ users: User[], total: number }>('/users');
      return response.data;
    } catch (error) {
      console.error('Failed to fetch users:', error);
      return { users: [], total: 0 };
    }
  },
}; 