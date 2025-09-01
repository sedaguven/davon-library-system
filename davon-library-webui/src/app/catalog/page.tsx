'use client';

import { useState, useEffect } from 'react';
import BookList from '../components/BookList';
import SearchAndFilterBar from '../components/SearchAndFilterBar';
import { bookService } from '../services/bookService';
import { Book } from '@/lib/types';

export default function CatalogPage() {
  const [books, setBooks] = useState<Book[]>([]);
  const [filteredBooks, setFilteredBooks] = useState<Book[]>([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState<string | null>(null);
  const [searchQuery, setSearchQuery] = useState('');
  const [availabilityFilter, setAvailabilityFilter] = useState('all');

  // Fetch books from the backend API
  useEffect(() => {
    const fetchBooks = async () => {
      try {
        setLoading(true);
        const { books: fetchedBooks } = await bookService.getAllBooks();
        setBooks(fetchedBooks);
        setFilteredBooks(fetchedBooks);
        setError(null);
      } catch (err) {
        setError('Failed to load books. Please try again later.');
        console.error('Error fetching books:', err);
      } finally {
        setLoading(false);
      }
    };

    fetchBooks();
  }, []);

  // Filter books based on search and availability
  useEffect(() => {
    let filtered = books;

    // Apply search filter
    if (searchQuery) {
      filtered = filtered.filter(book =>
        book.title.toLowerCase().includes(searchQuery.toLowerCase()) ||
        book.author.toLowerCase().includes(searchQuery.toLowerCase())
      );
    }

    // Apply availability filter
    if (availabilityFilter === 'available') {
      filtered = filtered.filter(book => book.available);
    } else if (availabilityFilter === 'unavailable') {
      filtered = filtered.filter(book => !book.available);
    }

    setFilteredBooks(filtered);
  }, [books, searchQuery, availabilityFilter]);

  if (loading) {
    return (
      <div className="flex justify-center items-center min-h-screen">
        <div className="text-xl">Loading books...</div>
      </div>
    );
  }

  if (error) {
    return (
      <div className="flex justify-center items-center min-h-screen">
        <div className="text-red-600 text-xl">{error}</div>
      </div>
    );
  }

  return (
    <div className="space-y-6">
      <h1 className="text-3xl font-bold text-center mb-8">Book Catalog</h1>
      <SearchAndFilterBar
        onSearchChange={setSearchQuery}
        onFilterChange={setAvailabilityFilter}
      />
      <BookList books={filteredBooks} />
    </div>
  );
} 