'use client';

import { useContext, useEffect, useState } from 'react';
import { BarChart, BookCheck, BookA, UserCircle, Wallet, History, BadgeCheck, BookCopy, Clock, ListOrdered } from 'lucide-react';
import { AuthContext } from '../context/AuthContext';
import { useRouter } from 'next/navigation';
import { UserProfile, Fine, Loan, ReservationDTO } from '@/lib/types';
import { loanService } from '../services/loanService';
import { reservationService } from '../services/reservationService';

interface StatCardProps {
    icon: React.ReactNode;
    label: string;
    value: string | number;
}

const StatCard: React.FC<StatCardProps> = ({ icon, label, value }) => (
    <div className="flex items-center p-4 bg-gray-50 rounded-lg">
        <div className="text-blue-600 bg-blue-100 p-3 rounded-full mr-4">
            {icon}
        </div>
        <div>
            <p className="text-sm font-medium text-gray-500">{label}</p>
            <p className="text-xl font-bold text-gray-900">{value}</p>
        </div>
    </div>
);


const ProfilePage = () => {
    const authContext = useContext(AuthContext);
    const user = authContext?.user;
    const router = useRouter();
    const [currentLoans, setCurrentLoans] = useState<Loan[]>([]);
    const [loanHistory, setLoanHistory] = useState<Loan[]>([]);
    const [reservations, setReservations] = useState<ReservationDTO[]>([]);

    useEffect(() => {
        if (authContext?.loading) return;
        if (!user) {
            router.push('/login');
        } else {
            const fetchLoans = async () => {
                try {
                    const loans = await loanService.getLoansByUserId(user.id);
                    if (Array.isArray(loans)) {
                        const activeLoans = loans.filter(loan => !loan.returnedDate);
                        const pastLoans = loans.filter(loan => loan.returnedDate);
                        setCurrentLoans(activeLoans);
                        setLoanHistory(pastLoans);
                    } else {
                        setCurrentLoans([]);
                        setLoanHistory([]);
                    }
                } catch (error) {
                    console.error("Failed to fetch loans:", error);
                    setCurrentLoans([]);
                    setLoanHistory([]);
                }
            };

            const fetchReservations = async () => {
                try {
                    const userReservations = await reservationService.getReservationsByUserId(user.id);
                    if (Array.isArray(userReservations)) {
                        setReservations(userReservations);
                    } else {
                        setReservations([]);
                    }
                } catch (error) {
                    console.error("Failed to fetch reservations:", error);
                    setReservations([]);
                }
            };

            fetchLoans();
            fetchReservations();
        }
    }, [user, router, authContext?.loading]);

    if (authContext?.loading || !user) {
        return <div>Loading...</div>;
    }

    const userProfile: UserProfile = {
        personalInfo: {
            name: user.name || 'User',
            email: user.email,
            joinDate: '2024', 
            membershipStatus: 'Active'
        },
        stats: {
            totalLoans: currentLoans.length + loanHistory.length,
            booksRead: loanHistory.length,
            activeReservations: reservations.length,
        },
        outstandingFines: [],
        currentLoans: currentLoans,
        loanHistory: loanHistory,
        reservations: reservations
    };

  return (
    <div className="grid grid-cols-1 md:grid-cols-3 gap-12">
        {/* Left Column */}
        <div className="md:col-span-1">
            <div className="bg-white p-8 rounded-2xl shadow-lg text-center">
                <UserCircle size={96} className="text-blue-500 mx-auto mb-4" />
                <h1 className="text-3xl font-bold text-gray-800">{userProfile.personalInfo.name}</h1>
                <p className="text-md text-gray-500 mt-1">{userProfile.personalInfo.email}</p>
                <div className="mt-4 flex items-center justify-center text-sm text-gray-400">
                    <p>Member since: {userProfile.personalInfo.joinDate}</p>
                </div>
                <div className="mt-2 flex items-center justify-center gap-2 text-green-600 font-semibold">
                    <BadgeCheck size={20} />
                    <span>{userProfile.personalInfo.membershipStatus}</span>
                </div>
            </div>

            <div className="bg-white p-6 rounded-2xl shadow-lg mt-8">
                 <h2 className="text-xl font-bold text-gray-700 mb-4">Statistics</h2>
                 <div className="space-y-4">
                    <StatCard icon={<BarChart size={20} />} label="Total Loans" value={userProfile.stats.totalLoans} />
                    <StatCard icon={<BookCheck size={20} />} label="Books Returned" value={userProfile.stats.booksRead} />
                    <StatCard icon={<BookCopy size={20} />} label="Active Reservations" value={reservations.length} />
                    <StatCard icon={<Wallet size={20} />} label="Active Fines" value={`$${userProfile.outstandingFines.reduce((acc: number, fine: Fine) => acc + fine.amount, 0).toFixed(2)}`} />
                 </div>
            </div>
        </div>

        {/* Right Column */}
        <div className="md:col-span-2 space-y-8">
             {/* Current Reservations */}
             {reservations.length > 0 &&
                <div className="bg-white p-8 rounded-2xl shadow-lg">
                    <h2 className="text-2xl font-bold text-gray-800 mb-6 flex items-center">
                        <Clock className="mr-3 text-blue-500" />Your Reservations
                    </h2>
                    <div className="space-y-4">
                        {reservations.map((reservation: ReservationDTO) => (
                            <div key={reservation.id} className="bg-gray-50 p-4 rounded-lg flex justify-between items-center transition hover:bg-gray-100">
                                <div>
                                    <p className="font-bold text-lg text-gray-800">{reservation.book.title}</p>
                                    <p className="text-sm text-gray-500 mt-1">Reserved on: {new Date(reservation.reservationDate).toLocaleDateString()}</p>
                                </div>
                                <div className="flex items-center gap-4">
                                    <div className="flex items-baseline text-right">
                                        <p className="text-2xl font-bold text-blue-500">{reservation.queuePosition}</p>
                                        <p className="text-sm text-gray-500 ml-2">in queue</p>
                                    </div>
                                    <ListOrdered className="text-blue-500" />
                                </div>
                            </div>
                        ))}
                    </div>
                </div>
            }

            {/* Current Loans */}
            <div className="bg-white p-8 rounded-2xl shadow-lg">
                <h2 className="text-2xl font-bold text-gray-800 mb-6 flex items-center">
                    <BookA className="mr-3 text-blue-500"/>Current Loans
                </h2>
                <div className="space-y-4">
                    {userProfile.currentLoans.map((loan: Loan) => (
                        <div key={loan.id} className="bg-gray-50 p-4 rounded-lg flex justify-between items-center transition hover:bg-gray-100">
                            <div>
                                <p className="font-bold text-lg text-gray-800">{loan.title}</p>
                                <p className="text-sm text-gray-500 mt-1">Due: {loan.dueDate}</p>
                            </div>
                            <div className="flex items-baseline gap-2 text-right">
                                <p className="text-2xl font-bold text-red-500">{loan.daysLeft}</p>
                                <p className="text-sm text-gray-500">days left</p>
                            </div>
                        </div>
                    ))}
                </div>
            </div>

            {/* Outstanding Fines */}
            {userProfile.outstandingFines.length > 0 &&
                <div className="bg-white p-8 rounded-2xl shadow-lg">
                    <h2 className="text-2xl font-bold text-red-600 mb-6">Outstanding Fines</h2>
                    {userProfile.outstandingFines.map((fine: Fine) => (
                    <div key={fine.id} className="border-l-4 border-red-500 bg-red-50 p-4 rounded-r-lg">
                        <div className="flex justify-between items-start">
                            <div>
                                <p className="font-semibold text-red-800">{fine.reason}</p>
                                <p className="text-sm text-red-600">For book: "{fine.bookTitle}"</p>
                            </div>
                            <p className="text-xl font-bold text-red-800">${fine.amount.toFixed(2)}</p>
                        </div>
                        <button className="mt-4 w-full bg-red-600 text-white font-semibold py-2 rounded-lg hover:bg-red-700 transition-colors">Pay Now</button>
                    </div>
                    ))}
                </div>
            }

            {/* Loan History */}
            <div className="bg-white p-8 rounded-2xl shadow-lg">
                <h2 className="text-2xl font-bold text-gray-800 mb-6 flex items-center">
                    <History className="mr-3 text-blue-500" />Loan History
                </h2>
                <ul className="space-y-3">
                    {userProfile.loanHistory.map((loan: Loan) => (
                        <li key={loan.id} className="text-gray-600 flex justify-between items-center text-sm border-b border-gray-100 pb-2">
                            <span>{loan.title}</span>
                            <span className="text-gray-400">Returned: {loan.returnedDate}</span>
                        </li>
                    ))}
                </ul>
                <button className="mt-6 w-full text-blue-600 font-semibold py-2 rounded-lg hover:bg-blue-50 transition-colors">View All History</button>
            </div>
        </div>
    </div>
  );
};

export default ProfilePage; 