'use client';

import { useState } from 'react';
import { bookService } from '../services/bookService';
import { authService } from '../services/authService';

export default function TestApiPage() {
  const [booksResult, setBooksResult] = useState<string>('');
  const [authResult, setAuthResult] = useState<string>('');
  const [loading, setLoading] = useState(false);

  const testBooksApi = async () => {
    setLoading(true);
    try {
      const books = await bookService.getAllBooks();
      setBooksResult(JSON.stringify(books, null, 2));
    } catch (error) {
      setBooksResult(`Error: ${error instanceof Error ? error.message : 'Unknown error'}`);
    } finally {
      setLoading(false);
    }
  };

  const testAuthApi = async () => {
    setLoading(true);
    try {
                  const result = await authService.login({ email: 'john.smith@email.com', password: 'test' }); // Admin user
      setAuthResult(JSON.stringify(result, null, 2));
    } catch (error) {
      setAuthResult(`Error: ${error instanceof Error ? error.message : 'Unknown error'}`);
    } finally {
      setLoading(false);
    }
  };

  return (
    <div className="max-w-4xl mx-auto p-6">
      <h1 className="text-3xl font-bold mb-6">API Connection Test</h1>
      
      <div className="space-y-6">
        <div>
          <h2 className="text-xl font-semibold mb-4">Test Books API</h2>
          <button
            onClick={testBooksApi}
            disabled={loading}
            className="px-4 py-2 bg-blue-600 text-white rounded hover:bg-blue-700 disabled:opacity-50"
          >
            {loading ? 'Testing...' : 'Test Books API'}
          </button>
          {booksResult && (
            <pre className="mt-4 p-4 bg-gray-100 rounded overflow-auto max-h-96">
              {booksResult}
            </pre>
          )}
        </div>

        <div>
          <h2 className="text-xl font-semibold mb-4">Test Auth API</h2>
          <button
            onClick={testAuthApi}
            disabled={loading}
            className="px-4 py-2 bg-green-600 text-white rounded hover:bg-green-700 disabled:opacity-50"
          >
            {loading ? 'Testing...' : 'Test Auth API'}
          </button>
          {authResult && (
            <pre className="mt-4 p-4 bg-gray-100 rounded overflow-auto max-h-96">
              {authResult}
            </pre>
          )}
        </div>
      </div>
    </div>
  );
} 