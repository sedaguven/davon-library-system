import apiClient from './api';
import { Book } from '@/lib/types'; // Assuming a central types file

// This DTO (Data Transfer Object) should match your Java Book entity
export interface BookDTO {
  id: number;
  title: string;
  isbn: string;
  author: string; // Database returns author as string
  availableCopies?: number;
  totalCopies?: number;
  status?: 'AVAILABLE' | 'UNAVAILABLE';
}

const mapBookDTOToBook = (dto: BookDTO): Book => ({
  id: dto.id,
  title: dto.title,
  author: dto.author, // Use the author string directly
  status: dto.status ?? (dto.availableCopies !== undefined ? (dto.availableCopies > 0 ? 'AVAILABLE' : 'UNAVAILABLE') : 'AVAILABLE'),
  available: (dto.status ? dto.status === 'AVAILABLE' : (dto.availableCopies ? dto.availableCopies > 0 : true)),
  availableCopies: dto.availableCopies ?? 0,
  totalCopies: dto.totalCopies ?? 0,
  isbn: dto.isbn,
  description: '',
  coverImage: ''
});

export const bookService = {
  async getAllBooks(page = 1, limit = 10, sort = 'title', order = 'asc'): Promise<{ books: Book[], total: number }> {
    try {
      // The endpoint '/books' - apiClient already has baseURL with /api
      const response = await apiClient.get<{ books: BookDTO[], total: number }>(`/books?page=${page}&limit=${limit}&sort=${sort}&order=${order}`);
      const mapped = response.data.books.map(mapBookDTOToBook);
      return { books: mapped, total: response.data.total };
    } catch (error) {
      console.error('Failed to fetch book:', error);

      return { books: [], total: 0 };
    }
  },

  // Example of another function to get a single book by ID
  async getBookById(id: number): Promise<Book | null> {
    try {
      const response = await apiClient.get<BookDTO>(`/books/${id}`);
      return mapBookDTOToBook(response.data);
    } catch (error) {
      console.error(`Failed to fetch book with id ${id}:`, error);
      return null;
    }
  },
};

export const borrowBook = async (userId: number, bookId: number) => {
    try {
        const response = await apiClient.post('/library/borrow', { userId, bookId });
        return response.data;
    } catch (error) {
        console.error('Failed to borrow book:', error);
        throw error;
    }
};

export const reserveBook = async (userId: number, bookId: number) => {
    try {
        const response = await apiClient.post('/library/reserve', { userId, bookId });
        return response.data;
    } catch (error) {
        console.error('Failed to reserve book:', error);
        throw error;
    }
};

export interface BookCopyDTO {
  id: number;
  status: 'AVAILABLE' | 'CHECKED_OUT' | 'MAINTENANCE' | 'DAMAGED' | 'LOST';
}

export const getBookCopiesByBook = async (bookId: number): Promise<BookCopyDTO[]> => {
  try {
    const response = await apiClient.get(`/book-copies/by-book/${bookId}`);
    return response.data as BookCopyDTO[];
  } catch (error) {
    console.error('Failed to fetch book copies:', error);
    return [];
  }
}; 