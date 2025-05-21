'use client';

import { useState } from 'react';

function validateEmail(email) {
  return /^[^\s@]+@[^\s@]+\.[^\s@]+$/.test(email);
}

export default function RegisterPage() {
  const [username, setUsername] = useState('');
  const [email, setEmail] = useState('');
  const [password, setPassword] = useState('');
  const [error, setError] = useState('');
  const [success, setSuccess] = useState('');

  const handleSubmit = (e) => {
    e.preventDefault();
    setError('');
    setSuccess('');

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

    const newUser = {
      id: Date.now(),
      username,
      email,
      password,
      createdAt: new Date().toISOString(),
      role: 'user'
    };

    try {
      // Get existing users from localStorage
      const existingUsers = JSON.parse(localStorage.getItem("users")) || [];

      // Check if user with same email or username already exists
      const userExists = existingUsers.some(
        user => user.email === email || user.username === username
      );

      if (userExists) {
        setError('⚠️ A user with this email or username already exists.');
        return;
      }

      // Add new user to the list
      const updatedUsers = [...existingUsers, newUser];
      localStorage.setItem("users", JSON.stringify(updatedUsers));
      localStorage.removeItem("user");

      // Clear form and show success
      setUsername("");
      setEmail("");
      setPassword("");
      setSuccess("✅ User registered successfully!");
      // Set as current user and redirect if role is user
      if (newUser.role === 'user') {
        localStorage.setItem('currentUser', JSON.stringify(newUser));
        setTimeout(() => {
          window.location.href = '/profile';
        }, 1200);
      }
    } catch (error) {
      setError('An error occurred during registration. Please try again.');
      console.error('Registration error:', error);
    }
  };

  return (
    <div className="min-h-screen bg-gradient-to-b from-gray-900 to-black text-white flex items-center justify-center">
      <div className="w-full max-w-md bg-gray-800 p-8 rounded-xl shadow-md">
        <h1 className="text-3xl font-bold mb-6 text-center">Register</h1>
        {error && <div className="mb-4 p-2 bg-red-600/80 rounded text-white text-center">{error}</div>}
        {success && <div className="mb-4 p-2 bg-green-600/80 rounded text-white text-center">{success}</div>}
        <form onSubmit={handleSubmit} className="space-y-4">
          <input
            type="text"
            placeholder="Username"
            value={username}
            onChange={(e) => setUsername(e.target.value)}
            className="w-full px-4 py-2 rounded-lg bg-gray-700 border border-gray-600 focus:outline-none focus:ring-2 focus:ring-blue-500"
            required
          />
          <input
            type="email"
            placeholder="Email"
            value={email}
            onChange={(e) => setEmail(e.target.value)}
            className="w-full px-4 py-2 rounded-lg bg-gray-700 border border-gray-600 focus:outline-none focus:ring-2 focus:ring-blue-500"
            required
          />
          <input
            type="password"
            placeholder="Password"
            value={password}
            onChange={(e) => setPassword(e.target.value)}
            className="w-full px-4 py-2 rounded-lg bg-gray-700 border border-gray-600 focus:outline-none focus:ring-2 focus:ring-blue-500"
            required
          />
          <button
            type="submit"
            className="w-full py-2 bg-blue-600 hover:bg-blue-700 transition rounded-lg font-semibold"
          >
            Register
          </button>
        </form>
      </div>
    </div>
  );
}

