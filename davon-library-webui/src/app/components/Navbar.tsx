'use client';

import React, { useState, useContext } from 'react';
import Link from 'next/link';
import { Home, User, LogOut, Menu, X } from 'lucide-react';
import { AuthContext } from '../context/AuthContext';
import { usePathname } from 'next/navigation';

export default function Navbar() {
  const authContext = useContext(AuthContext);
  const user = authContext?.user;
  const logout = authContext?.logout;
  const [isOpen, setIsOpen] = useState(false);
  const pathname = usePathname();

  if (!user || pathname === '/landing') {
    return null;
  }

  return (
    <>
      <div className="fixed top-4 left-4 z-50">
        <button
          onClick={() => setIsOpen(!isOpen)}
          className="p-2 rounded-md bg-gray-800 text-white hover:bg-gray-700"
        >
          {isOpen ? <X size={24} /> : <Menu size={24} />}
        </button>
      </div>

      <div
        className={`fixed top-0 left-0 h-full bg-gray-800 text-white transition-all duration-300 z-40 ${
          isOpen ? 'w-64' : 'w-0'
        } overflow-hidden`}
      >
        <div className="flex flex-col p-6 pt-20">
          <Link href={user?.role === 'admin' ? '/admin' : '/catalog'} className="flex items-center p-4 hover:bg-gray-700 rounded-md">
            <Home className="mr-4" />
            <span>Dashboard</span>
          </Link>
          <Link href="/profile" className="flex items-center p-4 hover:bg-gray-700 rounded-md">
            <User className="mr-4" />
            <span>Profile</span>
          </Link>
          <button
            onClick={logout}
            className="flex items-center p-4 hover:bg-gray-700 rounded-md text-left"
          >
            <LogOut className="mr-4" />
            <span>Logout</span>
          </button>
        </div>
      </div>
    </>
  );
} 