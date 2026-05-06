import { describe, it, expect, vi, beforeEach } from 'vitest';
import { render, screen, waitFor } from '@testing-library/react';
import { BrowserRouter } from 'react-router-dom';
import LearnerHomePage from '../pages/LearnerHomePage';
import { listingService } from '../services/listingService';

vi.mock('../services/listingService', () => ({
  listingService: {
    getListings: vi.fn(),
    getListingById: vi.fn(),
    createListing: vi.fn(),
  },
}));

vi.mock('@shared/components/MarketplaceNavbar', () => ({
  default: ({ title }: { title: string }) => <div data-testid="navbar">{title}</div>,
}));

vi.mock('@features/auth', () => ({
  useAuth: () => ({ user: { role: 'LEARNER', userId: '1' }, isAuthenticated: true, logout: vi.fn() }),
}));

const renderPage = () => render(<BrowserRouter><LearnerHomePage /></BrowserRouter>);

describe('LearnerHomePage', () => {
  beforeEach(() => vi.clearAllMocks());

  it('renders the page with navbar title', async () => {
    vi.mocked(listingService.getListings).mockResolvedValue([]);
    renderPage();
    expect(screen.getByText('Find a Tutor')).toBeInTheDocument();
  });

  it('shows empty state when no listings', async () => {
    vi.mocked(listingService.getListings).mockResolvedValue([]);
    renderPage();
    await waitFor(() => {
      expect(screen.getByText('No listings yet. Check back soon.')).toBeInTheDocument();
    });
  });

  it('renders listing cards from API', async () => {
    vi.mocked(listingService.getListings).mockResolvedValue([
      { id: 1, tutorId: 't1', tutorName: 'Juan', subject: 'Math', availableTime: 'Mon 3PM', contactInfo: 'fb', proofOfCompetenceUrl: 'url' },
    ]);
    renderPage();
    await waitFor(() => {
      expect(screen.getByText('Math')).toBeInTheDocument();
      expect(screen.getByText('Juan')).toBeInTheDocument();
    });
  });
});
