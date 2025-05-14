'use client';

import Link from 'next/link';

export default function Navbar() {
  return (
    <nav style={{
      background: '#4f46e5',
      padding: '1rem',
      display: 'flex',
      justifyContent: 'center',
      gap: '1rem',
    }}>
      <Link href="/register" style={{ color: 'white' }}>Register</Link>
      <Link href="/login" style={{ color: 'white' }}>Login</Link>
      <Link href="/profile" style={{ color: 'white' }}>Profile</Link>
      <Link href="/admin" style={{ color: 'white' }}>Admin</Link>
    </nav>
  );
}
