import { ReservationDTO } from '@/lib/types';
import apiClient from './api';

export const reservationService = {
  async getReservationsByUserId(userId: number): Promise<ReservationDTO[]> {
    try {
      const response = await apiClient.get<ReservationDTO[]>(`/reservations/user/${userId}`);
        return response.data;
    } catch (error) {
      console.error('Failed to fetch reservations:', error);
      return [];
    }
  },
}; 