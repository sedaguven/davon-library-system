'use client';

import { useEffect, useState } from 'react';

function validateEmail(email) {
  return /^[^\s@]+@[^\s@]+\.[^\s@]+$/.test(email);
}

export default function AdminPage() {
  const [users, setUsers] = useState([]);
  const [editIndex, setEditIndex] = useState(null);
  const [editUser, setEditUser] = useState({ username: '', email: '', password: '', role: 'user' });
  const [message, setMessage] = useState('');
  const [error, setError] = useState('');
  const [isAdmin, setIsAdmin] = useState(false);
  const [loading, setLoading] = useState(true);

  useEffect(() => {
    const currentUser = JSON.parse(localStorage.getItem('currentUser'));
    if (!currentUser || currentUser.role !== 'admin') {
      setIsAdmin(false);
      setMessage('Access denied. Admins only.');
      setLoading(false);
      return;
    }
    setIsAdmin(true);
    const storedUsers = JSON.parse(localStorage.getItem('users')) || [];
    setUsers(storedUsers);
    setLoading(false);
  }, []);

  const startEdit = (index) => {
    setEditIndex(index);
    setEditUser({ ...users[index] });
    setError('');
    setMessage('');
  };

  const handleEditChange = (e) => {
    setEditUser({ ...editUser, [e.target.name]: e.target.value });
    setError('');
    setMessage('');
  };

  const validateEdit = () => {
    if (!editUser.username.trim() || !editUser.email.trim() || !editUser.password.trim()) {
      setError('All fields are required.');
      return false;
    }
    if (!validateEmail(editUser.email)) {
      setError('Invalid email format.');
      return false;
    }
    // Prevent duplicate email/username (except for the user being edited)
    const duplicate = users.some((u, i) =>
      i !== editIndex && (u.email === editUser.email || u.username === editUser.username)
    );
    if (duplicate) {
      setError('Email or username already exists.');
      return false;
    }
    return true;
  };

  const saveEdit = (index) => {
    if (!validateEdit()) return;
    const updatedUsers = users.map((user, i) =>
      i === index ? { ...editUser } : user
    );
    setUsers(updatedUsers);
    localStorage.setItem('users', JSON.stringify(updatedUsers));
    setEditIndex(null);
    setMessage('User updated!');
    setError('');
  };

  const deleteUser = (emailToDelete) => {
    // Prevent admin from deleting themselves
    const currentUser = JSON.parse(localStorage.getItem('currentUser'));
    if (currentUser && currentUser.email === emailToDelete) {
      setError("Admins can't delete themselves.");
      setMessage('');
      return;
    }
    if (!window.confirm('Are you sure you want to delete this user?')) return;
    const updatedUsers = users.filter(user => user.email !== emailToDelete);
    localStorage.setItem('users', JSON.stringify(updatedUsers));
    setUsers(updatedUsers);
    setMessage('User deleted.');
    setError('');
  };

  if (!isAdmin) {
    return <div className="min-h-screen bg-gradient-to-b from-gray-900 to-black text-white flex items-center justify-center"><div className="w-full max-w-2xl bg-gray-800 p-8 rounded-xl shadow-md text-center"><h1 className="text-3xl font-bold mb-6">Admin Panel</h1><p className="mb-4 p-2 bg-red-600/80 rounded text-white">{message}</p></div></div>;
  }

  if (loading) {
    return <div className="min-h-screen bg-gradient-to-b from-gray-900 to-black text-white flex items-center justify-center"><div className="w-full max-w-2xl bg-gray-800 p-8 rounded-xl shadow-md text-center">Loading users...</div></div>;
  }

  return (
    <div className="min-h-screen bg-gradient-to-b from-gray-900 to-black text-white flex items-center justify-center">
      <div className="w-full max-w-2xl bg-gray-800 p-8 rounded-xl shadow-md">
        <div className="flex justify-between items-center mb-4">
          <h1 className="text-3xl font-bold text-center">Admin Panel</h1>
          <a href="/profile" className="py-2 px-4 bg-blue-600 hover:bg-blue-700 rounded-lg font-semibold text-white transition">Profile</a>
        </div>
        {message && <div className="mb-4 p-2 bg-green-600/80 rounded text-white text-center">{message}</div>}
        {error && <div className="mb-4 p-2 bg-red-600/80 rounded text-white text-center">{error}</div>}
        {users.length === 0 ? (
          <p className="text-center">No users found.</p>
        ) : (
          <div className="overflow-x-auto">
            <table className="min-w-full border border-gray-700 rounded-lg overflow-hidden">
              <thead className="bg-gray-700">
                <tr>
                  <th className="px-4 py-2">Username</th>
                  <th className="px-4 py-2">Email</th>
                  <th className="px-4 py-2">Password</th>
                  <th className="px-4 py-2">Role</th>
                  <th className="px-4 py-2">Actions</th>
                </tr>
              </thead>
              <tbody>
                {users.map((user, i) => {
                  const currentUser = JSON.parse(localStorage.getItem('currentUser'));
                  const isSelf = currentUser && currentUser.email === user.email;
                  return (
                    <tr key={user.email} className="even:bg-gray-700/40">
                      {editIndex === i ? (
                        <>
                          <td className="px-2 py-1"><input name="username" value={editUser.username} onChange={handleEditChange} className="w-full px-2 py-1 rounded bg-gray-700 border border-gray-600 focus:outline-none focus:ring-2 focus:ring-blue-500" required /></td>
                          <td className="px-2 py-1"><input name="email" value={editUser.email} onChange={handleEditChange} className="w-full px-2 py-1 rounded bg-gray-700 border border-gray-600 focus:outline-none focus:ring-2 focus:ring-blue-500" required /></td>
                          <td className="px-2 py-1"><input name="password" value={editUser.password} onChange={handleEditChange} className="w-full px-2 py-1 rounded bg-gray-700 border border-gray-600 focus:outline-none focus:ring-2 focus:ring-blue-500" required /></td>
                          <td className="px-2 py-1"><select name="role" value={editUser.role} onChange={handleEditChange} className="w-full px-2 py-1 rounded bg-gray-700 border border-gray-600 focus:outline-none focus:ring-2 focus:ring-blue-500"><option value="user">user</option><option value="admin">admin</option></select></td>
                          <td className="px-2 py-1 flex gap-2"><button onClick={() => saveEdit(i)} disabled={!editUser.username.trim() || !editUser.email.trim() || !editUser.password.trim() || !validateEmail(editUser.email)} className="flex-1 py-1 bg-blue-600 hover:bg-blue-700 transition rounded-lg font-semibold">Save</button><button onClick={() => setEditIndex(null)} className="flex-1 py-1 bg-gray-600 hover:bg-gray-700 transition rounded-lg font-semibold">Cancel</button></td>
                        </>
                      ) : (
                        <>
                          <td className="px-2 py-1">{user.username}</td>
                          <td className="px-2 py-1">{user.email}</td>
                          <td className="px-2 py-1">{user.password}</td>
                          <td className="px-2 py-1">{user.role}</td>
                          <td className="px-2 py-1 flex gap-2">
                            <button onClick={() => startEdit(i)} className="flex-1 py-1 bg-blue-600 hover:bg-blue-700 transition rounded-lg font-semibold">Edit</button>
                            <button
                              onClick={() => !isSelf && deleteUser(user.email)}
                              className={`flex-1 py-1 ${isSelf ? 'bg-gray-500 cursor-not-allowed' : 'bg-red-600 hover:bg-red-700'} transition rounded-lg font-semibold`}
                              disabled={isSelf}
                              title={isSelf ? "You can't delete yourself" : "Delete"}
                            >
                              {isSelf ? "Can't Delete" : "Delete"}
                            </button>
                          </td>
                        </>
                      )}
                    </tr>
                  );
                })}
              </tbody>
            </table>
          </div>
        )}
      </div>
    </div>
  );
}
