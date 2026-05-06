import { describe, it, expect, vi, beforeEach } from 'vitest';
import { render, screen, fireEvent, waitFor } from '@testing-library/react';
import { BrowserRouter } from 'react-router-dom';
import LoginPage from '../pages/LoginPage';
import { authService } from '../services/authService';

vi.mock('../services/authService', () => ({
  authService: {
    login: vi.fn(),
    getCurrentUser: vi.fn(),
    logout: vi.fn(),
  },
}));

const renderLogin = (props = {}) =>
  render(
    <BrowserRouter>
      <LoginPage onLoginSuccess={vi.fn()} onNavigateToRegister={vi.fn()} {...props} />
    </BrowserRouter>
  );

describe('LoginPage', () => {
  beforeEach(() => vi.clearAllMocks());

  it('renders the login form with all fields', () => {
    renderLogin();
    expect(screen.getByText('Welcome Back')).toBeInTheDocument();
    expect(screen.getByPlaceholderText('juan@cit.edu')).toBeInTheDocument();
    expect(screen.getByText('Log In')).toBeInTheDocument();
  });

  it('shows validation errors on empty submit', async () => {
    renderLogin();
    fireEvent.click(screen.getByText('Log In'));
    await waitFor(() => {
      expect(screen.getByText('Username is required')).toBeInTheDocument();
      expect(screen.getByText('Password is required')).toBeInTheDocument();
    });
  });

  it('calls authService.login on valid submit', async () => {
    const mockResponse = { userId: '1', message: 'OK', role: 'LEARNER' as const };
    vi.mocked(authService.login).mockResolvedValue(mockResponse);
    const onSuccess = vi.fn();
    renderLogin({ onLoginSuccess: onSuccess });

    fireEvent.change(screen.getByPlaceholderText('juan@cit.edu'), { target: { value: 'test@cit.edu' } });
    fireEvent.change(screen.getByPlaceholderText('••••••••'), { target: { value: 'password123' } });
    fireEvent.click(screen.getByText('Log In'));

    await waitFor(() => {
      expect(authService.login).toHaveBeenCalledWith({ username: 'test@cit.edu', password: 'password123' });
    });
  });
});
