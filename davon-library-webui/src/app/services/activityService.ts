import apiClient from './api';
import { ActivityDTO } from '@/lib/types';

export const activityService = {
  async getRecentActivities(limit = 10): Promise<ActivityDTO[]> {
    try {
      const response = await apiClient.get<ActivityDTO[]>(`/activities/recent?limit=${limit}`);
      return response.data;
    } catch (error) {
      console.error('Failed to fetch recent activities:', error);
      return [];
    }
  },
}; 