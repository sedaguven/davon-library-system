import apiClient from './api';
import { Loan } from '@/lib/types';

export const loanService = {
  async getLoansByUserId(userId: number): Promise<Loan[]> {
    try {
      const response = await apiClient.get<Loan[]>(`/loans/user/${userId}`);
      return response.data;
    } catch (error) {
      console.error('Failed to fetch loans:', error);
      return [];
    }
  },

  async getRecentLoans(): Promise<Loan[]> {
    try {
      const response = await apiClient.get<Loan[]>('/loans/recent');
      return response.data;
    } catch (error) {
      console.error('Failed to fetch recent loans:', error);
      return [];
    }
  },

  async getLoanedOutCount(): Promise<number> {
    try {
      const response = await apiClient.get<number>('/loans/loaned-out/count');
      return response.data;
    } catch (error) {
      console.error('Failed to fetch loaned out count:', error);
      return 0;
    }
  },

  async getOverdueCount(): Promise<number> {
    try {
      const response = await apiClient.get<number>('/loans/overdue/count');
      return response.data;
    } catch (error) {
      console.error('Failed to fetch overdue count:', error);
      return 0;
    }
  },

  async returnBook(loanId: number): Promise<void> {
    try {
      await apiClient.put(`/loans/${loanId}/return`);
    } catch (error) {
      console.error('Failed to return book:', error);
    }
  },
}; 