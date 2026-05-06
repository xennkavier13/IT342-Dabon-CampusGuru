import { describe, it, expect, vi, beforeEach } from 'vitest';
import { render, screen, fireEvent, waitFor } from '@testing-library/react';
import { BrowserRouter } from 'react-router-dom';
import TutorDashboardPage from '../pages/TutorDashboardPage';
import { bookingService } from '../services/bookingService';

vi.mock('../services/bookingService', () => ({
  bookingService: {
    getPendingBookings: vi.fn(),
    updateBookingStatus: vi.fn(),
    createBooking: vi.fn(),
  },
}));

vi.mock('@shared/components/MarketplaceNavbar', () => ({
  default: ({ title }: { title: string }) => <div>{title}</div>,
}));

vi.mock('@shared/components/Button', () => ({
  default: ({ children, onClick, ...props }: any) => <button onClick={onClick} {...props}>{children}</button>,
}));

vi.mock('@shared/components/Input', () => ({
  default: ({ label, ...props }: any) => <div><label>{label}</label><input {...props} /></div>,
}));

vi.mock('@features/auth', () => ({
  useAuth: () => ({ user: { role: 'TUTOR', userId: '1' }, isAuthenticated: true, logout: vi.fn() }),
}));

const mockBooking = {
  id: 1, listingId: 1, subject: 'Math', learnerId: 'l1', learnerName: 'Maria',
  requestedTime: 'Tue 4PM', paymentType: 'Cash', status: 'PENDING' as const,
};

const renderPage = () => render(<BrowserRouter><TutorDashboardPage /></BrowserRouter>);

describe('TutorDashboardPage', () => {
  beforeEach(() => vi.clearAllMocks());

  it('renders the dashboard with heading', async () => {
    vi.mocked(bookingService.getPendingBookings).mockResolvedValue([]);
    renderPage();
    expect(screen.getByText('Tutor Dashboard')).toBeInTheDocument();
  });

  it('shows empty state when no pending bookings', async () => {
    vi.mocked(bookingService.getPendingBookings).mockResolvedValue([]);
    renderPage();
    await waitFor(() => {
      expect(screen.getByText('No pending booking requests.')).toBeInTheDocument();
    });
  });

  it('renders pending bookings and allows accept', async () => {
    vi.mocked(bookingService.getPendingBookings).mockResolvedValue([mockBooking]);
    vi.mocked(bookingService.updateBookingStatus).mockResolvedValue({ ...mockBooking, status: 'ACCEPTED' });
    renderPage();
    await waitFor(() => {
      expect(screen.getByText('Maria')).toBeInTheDocument();
      expect(screen.getByText('Math')).toBeInTheDocument();
    });
    fireEvent.click(screen.getByText('Accept'));
    await waitFor(() => {
      expect(screen.getByText('Accept Booking')).toBeInTheDocument();
    });
  });
});
