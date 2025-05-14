'use client';

import { useEffect, useState } from 'react';

function validateEmail(email) {
  return /^[^\s@]+@[\w.-]+\.[^\s@]+$/.test(email);
}

export default function ProfilePage() {
  const [user, setUser] = useState(null);
  const [editMode, setEditMode] = useState(false);
  const [username, setUsername] = useState('');
  const [email, setEmail] = useState('');
  const [password, setPassword] = useState('');
  const [message, setMessage] = useState('');
  const [error, setError] = useState('');

  useEffect(() => {
    const currentUser = JSON.parse(localStorage.getItem('currentUser'));
    if (currentUser) {
      setUser(currentUser);
      setUsername(currentUser.username);
      setEmail(currentUser.email);
      setPassword(currentUser.password);
    }
  }, []);

  if (!user) {
    return (
      <div className="min-h-screen bg-gradient-to-b from-gray-900 to-black text-white flex items-center justify-center">
        <div className="w-full max-w-md bg-gray-800 p-8 rounded-xl shadow-md">
          <p>No user is currently logged in.</p>
        </div>
      </div>
    );
  }

  const handleEdit = (e) => {
    e.preventDefault();
    setError('');
    setMessage('');
    // Validation
    if (!username.trim() || !email.trim() || !password.trim()) {
      setError('All fields are required.');
      return;
    }
    if (username.length < 3) {
      setError('Username must be at least 3 characters long');
      return;
    }
    if (!validateEmail(email)) {
      setError('Invalid email format.');
      return;
    }
    if (password.length < 6) {
      setError('Password must be at least 6 characters long');
      return;
    }
    // Prevent duplicate email/username (except for self)
    const users = JSON.parse(localStorage.getItem('users')) || [];
    const duplicate = users.some(u => (u.email === email || u.username === username) && u.email !== user.email);
    if (duplicate) {
      setError('Email or username already exists.');
      return;
    }
    // Update user in users array
    const updatedUsers = users.map(u =>
      u.email === user.email ? { ...u, username, email, password } : u
    );
    localStorage.setItem('users', JSON.stringify(updatedUsers));
    // Update currentUser
    const updatedUser = { ...user, username, email, password };
    localStorage.setItem('currentUser', JSON.stringify(updatedUser));
    setUser(updatedUser);
    setEditMode(false);
    setMessage('Profile updated!');
    setError('');
  };

  const handleDelete = () => {
    if (!window.confirm('Are you sure you want to delete your account?')) return;
    // Remove user from users array
    const users = JSON.parse(localStorage.getItem('users')) || [];
    const updatedUsers = users.filter(u => u.email !== user.email);
    localStorage.setItem('users', JSON.stringify(updatedUsers));
    // Remove currentUser
    localStorage.removeItem('currentUser');
    setUser(null);
    setMessage('Account deleted.');
    // Optionally, redirect to home or login
    // window.location.href = '/login';
  };

  return (
    <div className="min-h-screen bg-gradient-to-b from-gray-900 to-black text-white flex items-center justify-center">
      <div className="w-full max-w-md bg-gray-800 p-8 rounded-xl shadow-md">
        <h1 className="text-3xl font-bold mb-6 text-center">Profile</h1>
        {message && <div className="mb-4 p-2 bg-green-600/80 rounded text-white text-center">{message}</div>}
        {error && <div className="mb-4 p-2 bg-red-600/80 rounded text-white text-center">{error}</div>}
        {editMode ? (
          <form onSubmit={handleEdit} className="space-y-4">
            <input
              type="text"
              value={username}
              onChange={e => setUsername(e.target.value)}
              className="w-full px-4 py-2 rounded-lg bg-gray-700 border border-gray-600 focus:outline-none focus:ring-2 focus:ring-blue-500"
              required
            />
            <input
              type="email"
              value={email}
              onChange={e => setEmail(e.target.value)}
              className="w-full px-4 py-2 rounded-lg bg-gray-700 border border-gray-600 focus:outline-none focus:ring-2 focus:ring-blue-500"
              required
            />
            <input
              type="password"
              value={password}
              onChange={e => setPassword(e.target.value)}
              className="w-full px-4 py-2 rounded-lg bg-gray-700 border border-gray-600 focus:outline-none focus:ring-2 focus:ring-blue-500"
              required
            />
            <div className="flex gap-2">
              <button type="submit" className="flex-1 py-2 bg-blue-600 hover:bg-blue-700 transition rounded-lg font-semibold">Save</button>
              <button type="button" onClick={() => setEditMode(false)} className="flex-1 py-2 bg-gray-600 hover:bg-gray-700 transition rounded-lg font-semibold">Cancel</button>
            </div>
          </form>
        ) : (
          <>
            <p className="mb-2"><strong>Username:</strong> {user.username}</p>
            <p className="mb-4"><strong>Email:</strong> {user.email}</p>
            <div className="flex gap-2">
              <button onClick={() => setEditMode(true)} className="flex-1 py-2 bg-blue-600 hover:bg-blue-700 transition rounded-lg font-semibold">Edit Profile</button>
              <button onClick={handleDelete} className="flex-1 py-2 bg-red-600 hover:bg-red-700 transition rounded-lg font-semibold">Delete Account</button>
            </div>
          </>
        )}
      </div>
    </div>
  );
}
