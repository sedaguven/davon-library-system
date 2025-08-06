'use client';
import React, { useContext } from 'react';
import { AuthContext } from '../context/AuthContext';
import { useRouter } from 'next/navigation';
import { Users, Book, BarChart2, AlertCircle, RefreshCw } from 'lucide-react';
import { Loan } from '@/lib/types';
import { userService } from '../services/userService';
import { bookService } from '../services/bookService';
import { loanService } from '../services/loanService';
import { activityService } from '../services/activityService';
import { ActivityDTO } from '@/lib/types';

interface StatCardProps {
    icon: React.ReactNode;
    title: string;
    value: string | number;
    color: string;
}

const StatCard: React.FC<StatCardProps> = ({ icon, title, value, color }) => (
  <div className={`bg-white p-6 rounded-2xl shadow-lg flex items-center border-l-4 ${color}`}>
    <div className="mr-4">{icon}</div>
    <div>
      <p className="text-gray-500 text-sm font-medium">{title}</p>
      <p className="text-3xl font-bold text-gray-800">{value}</p>
    </div>
  </div>
);

const AdminDashboardPage = () => {
    const authContext = useContext(AuthContext);
    const user = authContext?.user;
    const router = useRouter();

    // Real data will be fetched from API
    const [stats, setStats] = React.useState({
        totalUsers: 0,
        totalBooks: 0,
        booksLoaned: 0,
        overdueBooks: 0,
    });
    const [recentActivity, setRecentActivity] = React.useState<ActivityDTO[]>([]);
    const [loading, setLoading] = React.useState(true);

    React.useEffect(() => {
        if (user && user.role !== 'admin') {
            router.push('/');
        }
        // Also handle the case where user data is not yet loaded
        if (user === null) {
            // You might want to show a loading spinner, but for now, redirecting is fine
            // router.push('/login');
        }
    }, [user, router]);

    // Fetch real data for admin dashboard
    React.useEffect(() => {
        if (user && user.role === 'admin') {
            setLoading(true);

            const fetchData = async () => {
                try {
                    const [usersResponse, booksResponse, loanedOutCount, overdueCount, recentActivities] = await Promise.all([
                        userService.getAllUsers(),
                        bookService.getAllBooks(),
                        loanService.getLoanedOutCount(),
                        loanService.getOverdueCount(),
                        activityService.getRecentActivities()
                    ]);

                    if (usersResponse) {
                        setStats(prev => ({ ...prev, totalUsers: usersResponse.total }));
                    }
                    if (booksResponse) {
                        setStats(prev => ({ ...prev, totalBooks: booksResponse.total }));
                    }
                    if (loanedOutCount) {
                        setStats(prev => ({ ...prev, booksLoaned: loanedOutCount }));
                    }
                    if (overdueCount) {
                        setStats(prev => ({ ...prev, overdueBooks: overdueCount }));
                    }
                    if (Array.isArray(recentActivities)) {
                        setRecentActivity(recentActivities);
                    }
                } catch (error) {
                    console.error('Failed to fetch admin data:', error);
                } finally {
                    setLoading(false);
                }
            };

            fetchData();
        }
    }, [user]);
    
    if (!user || user.role !== 'admin') {
        // Render a loading/access denied state while redirecting
        return (
            <div className="flex flex-col items-center justify-center h-96">
                <h1 className="text-3xl font-bold text-red-600">Access Denied</h1>
                <p className="mt-4 text-gray-600">You do not have permission to view this page.</p>
            </div>
        );
    }

    return (
        <div className="space-y-12">
            <div>
                <h1 className="text-4xl font-bold text-gray-800">Admin Dashboard</h1>
                <p className="mt-2 text-gray-500">Welcome back, {user.name}. Here's an overview of your library.</p>
            </div>

            {/* Stats Grid */}
            <div className="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-4 gap-6">
                <StatCard icon={<Users size={32} className="text-blue-500" />} title="Total Users" value={stats.totalUsers.toLocaleString()} color="border-blue-500" />
                <StatCard icon={<Book size={32} className="text-green-500" />} title="Total Books" value={stats.totalBooks.toLocaleString()} color="border-green-500" />
                <StatCard icon={<BarChart2 size={32} className="text-purple-500" />} title="Books Loaned Out" value={stats.booksLoaned.toLocaleString()} color="border-purple-500" />
                <StatCard icon={<AlertCircle size={32} className="text-red-500" />} title="Overdue Books" value={stats.overdueBooks.toLocaleString()} color="border-red-500" />
            </div>

            {/* Management Section */}
            <div className="grid grid-cols-1 md:grid-cols-2 gap-8">
                {/* User Management */}
                <div className="bg-white p-8 rounded-2xl shadow-lg">
                    <h2 className="text-2xl font-bold text-gray-800 mb-4">User Management</h2>
                    <p className="text-gray-500 mb-6">View, add, edit, or remove user accounts.</p>
                    <button 
                        onClick={() => router.push('/admin/users')}
                        className="w-full bg-blue-600 text-white font-bold py-3 rounded-lg hover:bg-blue-700 transition-colors"
                    >
                        View All Users ({stats.totalUsers})
                    </button>
                </div>

                {/* Book Management */}
                <div className="bg-white p-8 rounded-2xl shadow-lg">
                    <h2 className="text-2xl font-bold text-gray-800 mb-4">Book Management</h2>
                    <p className="text-gray-500 mb-6">Manage the library's collection of books.</p>
                    <button 
                        onClick={() => router.push('/admin/books')}
                        className="w-full bg-green-600 text-white font-bold py-3 rounded-lg hover:bg-green-700 transition-colors"
                    >
                        View All Books ({stats.totalBooks})
                    </button>
                </div>
            </div>

            {/* Recent Activity Section */}
            <div className="bg-white p-8 rounded-2xl shadow-lg">
                <div className="flex justify-between items-center mb-6">
                    <h2 className="text-2xl font-bold text-gray-800">Recent Activity</h2>
                    <button className="text-gray-500 hover:text-gray-700">
                        <RefreshCw size={20} />
                    </button>
                </div>
                <div className="space-y-4">
                    {loading ? (
                        <p>Loading activity...</p>
                    ) : recentActivity.length > 0 ? (
                        recentActivity.map((activity, index) => (
                            <div key={index} className="flex items-center justify-between p-3 bg-gray-50 rounded-lg">
                                <div>
                                    <p className="font-semibold text-gray-700">{activity.description}</p>
                                    <p className="text-sm text-gray-500">{new Date(activity.timestamp).toLocaleDateString()}</p>
                                </div>
                                <div className="text-right">
                                    <span className="px-3 py-1 text-xs font-semibold text-blue-800 bg-blue-100 rounded-full">
                                        {activity.type}
                                    </span>
                                </div>
                            </div>
                        ))
                    ) : (
                        <p>No recent activity found.</p>
                    )}
                </div>
            </div>
        </div>
    );
};

export default AdminDashboardPage; 