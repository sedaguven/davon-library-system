'use client';

import { useState, useEffect } from 'react';
import { getReservationsByUserId, cancelReservation } from '../services/reservationService';
import { returnBook } from '../services/loanService';
import { useAuth } from '../context/AuthContext';
import { Reservation } from '@/lib/types';

const BorrowPage = () => {
    const { user } = useAuth();
    const [reservations, setReservations] = useState<Reservation[]>([]);

    useEffect(() => {
        if (user) {
            fetchReservations(user.id);
        }
    }, [user]);

    const fetchReservations = async (userId: number) => {
        try {
            const userReservations = await getReservationsByUserId(userId);
            setReservations(userReservations);
        } catch (error) {
            console.error('Error fetching reservations:', error);
        }
    };

    const handleCancelReservation = async (reservationId: number) => {
        try {
            await cancelReservation(reservationId);
            if(user) {
                fetchReservations(user.id);
            }
        } catch (error) {
            console.error('Error canceling reservation:', error);
        }
    };

    const handleReturnBook = async (loanId: number) => {
        try {
            await returnBook(loanId);
        } catch (error) {
            console.error('Error returning book:', error);
        }
    };

    return (
        <div className="container mx-auto p-4">
            <h1 className="text-2xl font-bold mb-4">My Borrows and Reservations</h1>

            <div className="mb-8">
                <h2 className="text-xl font-semibold mb-2">My Reservations</h2>
                {reservations.length > 0 ? (
                    <ul>
                        {reservations.map((reservation: Reservation) => (
                            <li key={reservation.id} className="mb-2 p-2 border rounded">
                                <p>Book: {reservation.book.title}</p>
                                <p>Status: {reservation.status}</p>
                                <button onClick={() => handleCancelReservation(reservation.id)} className="bg-red-500 text-white px-2 py-1 rounded">Cancel</button>
                            </li>
                        ))}
                    </ul>
                ) : (
                    <p>You have no reservations.</p>
                )}
            </div>

        </div>
    );
};

export default BorrowPage; 