'use client';

import React, { useState, useContext } from 'react';
import Pagination from './Pagination';
import Link from 'next/link';
import { AuthContext } from '../context/AuthContext';
import { Book } from '@/lib/types';

interface BookListProps {
    books: Book[];
}

const BookList: React.FC<BookListProps> = ({ books }) => {
  const [currentPage, setCurrentPage] = useState(1);
  const booksPerPage = 12;
  const authContext = useContext(AuthContext);
  const user = authContext?.user;

  const indexOfLastBook = currentPage * booksPerPage;
  const indexOfFirstBook = indexOfLastBook - booksPerPage;
  const currentBooks = books.slice(indexOfFirstBook, indexOfLastBook);

  const totalPages = Math.ceil(books.length / booksPerPage);

  React.useEffect(() => {
    setCurrentPage(1);
  }, [books]);

  return (
    <div className="flex flex-col" style={{ minHeight: 'calc(100vh - 250px)' }}> {/* Adjust 250px based on nav/header/footer height */}
        <div className="flex-grow">
            {currentBooks.length > 0 ? (
                <div className="grid grid-cols-1 md:grid-cols-2 gap-8">
                    {currentBooks.map((book) => (
                        <div key={book.id} className="bg-white rounded-lg shadow-md overflow-hidden flex flex-col">
                            <div className="p-6 flex flex-col flex-grow">
                                <h2 className="font-bold text-lg text-gray-800">{book.title}</h2>
                                <p className="text-gray-600 mb-4 mt-1">by {book.author}</p>
                                <div className="flex justify-between items-center mt-auto pt-4">
                                    <span className={`text-sm font-medium px-3 py-1 rounded-full ${
                                        book.available ? 'bg-green-100 text-green-800' : 'bg-red-100 text-red-800'
                                    }`}>
                                        {book.available ? 'Available' : 'Unavailable'}
                                    </span>
                                    <div>
                                        <Link href={`/catalog/${book.id}`} passHref>
                                            <button className="text-white bg-blue-600 hover:bg-blue-700 font-semibold text-sm px-4 py-2 rounded-lg transition-transform hover:scale-105 mr-2">
                                                View Details
                                            </button>
                                        </Link>
                                    </div>
                                </div>
                            </div>
                        </div>
                    ))}
                </div>
            ) : (
                <div className="text-center py-12 flex flex-col items-center justify-center h-full">
                    <h2 className="text-2xl font-bold text-gray-700">No Books Found</h2>
                    <p className="mt-2 text-gray-500">Try adjusting your search or filter criteria.</p>
                </div>
            )}
        </div>
      
        {totalPages > 1 &&
            <div className="pt-8">
                <Pagination
                currentPage={currentPage}
                totalPages={totalPages}
                onPageChange={(page) => setCurrentPage(page)}
                />
            </div>
        }
    </div>
  );
};

export default BookList; 