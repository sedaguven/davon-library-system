'use client';
import React from 'react';
import { Search } from 'lucide-react';

interface SearchAndFilterBarProps {
  onSearchChange: (query: string) => void;
  onFilterChange: (filter: string) => void;
}

const SearchAndFilterBar: React.FC<SearchAndFilterBarProps> = ({ onSearchChange, onFilterChange }) => {
  return (
    <div className="bg-white p-6 rounded-2xl shadow-lg mb-8">
      <div className="grid grid-cols-1 md:grid-cols-3 gap-6">
        {/* Search Input */}
        <div className="md:col-span-2">
          <label htmlFor="search" className="block text-sm font-medium text-gray-700 mb-2">
            Search by Title or Author
          </label>
          <div className="relative">
            <Search className="absolute left-4 top-1/2 -translate-y-1/2 text-gray-400" size={20} />
            <input
              type="text"
              id="search"
              onChange={(e) => onSearchChange(e.target.value)}
              placeholder="e.g., The Great Gatsby or J.K. Rowling"
              className="w-full pl-12 pr-4 py-3 border border-gray-300 rounded-lg focus:ring-2 focus:ring-blue-500 focus:border-blue-500 outline-none transition"
            />
          </div>
        </div>

        {/* Availability Filter */}
        <div>
          <label className="block text-sm font-medium text-gray-700 mb-2">
            Availability
          </label>
          <select
            onChange={(e) => onFilterChange(e.target.value)}
            className="w-full py-3 px-4 border border-gray-300 rounded-lg focus:ring-2 focus:ring-blue-500 focus:border-blue-500 outline-none transition bg-white"
          >
            <option value="all">All</option>
            <option value="available">Available</option>
            <option value="unavailable">Checked Out</option>
          </select>
        </div>
      </div>
    </div>
  );
};

export default SearchAndFilterBar; 