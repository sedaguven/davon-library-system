'use client';

import { useState, useEffect, useContext } from 'react';
import { useParams } from 'next/navigation';
import { bookService, borrowBook, reserveBook } from '../../services/bookService';
import { Book } from '@/lib/types';
import { AuthContext } from '../../context/AuthContext';

export default function BookDetailPage() {
  const params = useParams();
  const bookId = Number(params.id);
  const [book, setBook] = useState<Book | null>(null);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState<string | null>(null);
  const { user } = useContext(AuthContext);

  useEffect(() => {
    const fetchBook = async () => {
      if (!bookId || isNaN(bookId)) {
        setError('Invalid book ID');
        setLoading(false);
        return;
      }

      try {
        setLoading(true);
        const fetchedBook = await bookService.getBookById(bookId);
        
        if (fetchedBook) {
          setBook(fetchedBook);
          setError(null);
        } else {
          setError('Book not found');
        }
      } catch (err) {
        setError('Failed to load book details. Please try again later.');
        console.error('Error fetching book:', err);
      } finally {
        setLoading(false);
      }
    };

    fetchBook();
  }, [bookId]);

  const handleBorrow = async () => {
    if (user && book) {
      try {
        await borrowBook(user.id, book.id);
        alert('Book borrowed successfully!');
      } catch (error) {
        alert('Failed to borrow book.');
      }
    }
  };

  const handleReserve = async () => {
    if (user && book) {
      try {
        await reserveBook(user.id, book.id);
        alert('Book reserved successfully!');
      } catch (error) {
        alert('Failed to reserve book.');
      }
    }
  };

  if (loading) {
    return (
      <div className="flex justify-center items-center min-h-screen">
        <div className="text-xl">Loading book details...</div>
      </div>
    );
  }

  if (error || !book) {
    return (
      <div className="flex justify-center items-center min-h-screen">
        <div className="text-red-600 text-xl">{error || 'Book not found'}</div>
      </div>
    );
  }

  return (
    <div className="max-w-4xl mx-auto p-6">
      <div className="bg-white rounded-lg shadow-lg overflow-hidden">
        <div className="md:flex">
          {/* Book Cover */}
          <div className="md:w-1/3">
            <div className="h-64 md:h-full bg-gray-200 flex items-center justify-center">
              {book.coverImage ? (
                <img
                  src={book.coverImage}
                  alt={book.title}
                  className="w-full h-full object-cover"
                />
              ) : (
                <div className="text-gray-500 text-center">
                  <div className="text-6xl mb-2">📚</div>
                  <p>No cover image</p>
                </div>
              )}
            </div>
          </div>

          {/* Book Details */}
          <div className="md:w-2/3 p-6">
            <h1 className="text-3xl font-bold text-gray-900 mb-2">{book.title}</h1>
            <p className="text-xl text-gray-600 mb-4">by {book.author}</p>
            
            <div className="mb-6">
              <span className={`inline-flex items-center px-3 py-1 rounded-full text-sm font-medium ${
                book.available 
                  ? 'bg-green-100 text-green-800' 
                  : 'bg-red-100 text-red-800'
              }`}>
                {book.available ? 'Available' : 'Not Available'}
              </span>
            </div>

            {book.description && (
              <div className="mb-6">
                <h3 className="text-lg font-semibold text-gray-900 mb-2">Description</h3>
                <p className="text-gray-700 leading-relaxed">{book.description}</p>
              </div>
            )}

            <div className="border-t pt-6">
              <div className="flex space-x-4">
                <button
                  onClick={handleBorrow}
                  className={`flex-1 py-3 px-6 rounded-lg font-medium transition-colors ${
                    book.available
                      ? 'bg-blue-600 text-white hover:bg-blue-700'
                      : 'bg-gray-300 text-gray-500 cursor-not-allowed'
                  }`}
                  disabled={!book.available}
                >
                  Borrow Book
                </button>
                
                <button 
                  onClick={handleReserve}
                  className={`flex-1 py-3 px-6 rounded-lg font-medium transition-colors ${
                    !book.available
                      ? 'bg-blue-600 text-white hover:bg-blue-700'
                      : 'bg-gray-300 text-gray-500 cursor-not-allowed'
                  }`}
                  disabled={book.available}
                >
                  Reserve Book
                </button>
              </div>
            </div>
          </div>
        </div>
      </div>
    </div>
  );
} 