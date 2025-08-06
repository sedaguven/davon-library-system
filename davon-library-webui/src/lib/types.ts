export interface Book {
    id: number;
    title: string;
    author: string;
    description: string;
    coverImage: string;
    available: boolean;
    availableCopies: number;
    totalCopies: number;
    isbn: string;
}

export interface User {
    id: number;
    name: string;
    email: string;
    role: 'admin' | 'user';
}

export interface Loan {
    id: number;
    title: string;
    dueDate: string;
    returnedDate: string | null;
    daysLeft: number;
}

export interface Fine {
    id: number;
    reason: string;
    bookTitle: string;
    amount: number;
}

export interface ReservationDTO {
    id: number;
    book: Book;
    reservationDate: string;
    status: string;
    queuePosition: number;
}

export interface UserProfile {
    personalInfo: {
        name: string;
        email: string;
        joinDate: string;
        membershipStatus: string;
    };
    stats: {
        totalLoans: number;
        booksRead: number;
        activeReservations: number;
    };
    outstandingFines: Fine[];
    currentLoans: Loan[];
    loanHistory: Loan[];
    reservations: ReservationDTO[];
}

export interface Credentials {
    email?: string;
    password?: string;
}

export interface AuthResponseDTO {
    token: string;
    user: User;
}

export interface ActivityDTO {
    type: string;
    description: string;
    timestamp: string;
    user: string;
    bookTitle: string;
} 