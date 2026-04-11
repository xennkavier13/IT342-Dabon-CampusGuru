import { useEffect, useState } from 'react';
import { authService } from '../services/authService';
import type { AuthResponse } from '../types/auth.types';

const AUTH_CHANGED_EVENT = 'auth-changed';

export const useAuth = () => {
  const [user, setUser] = useState<AuthResponse | null>(authService.getCurrentUser());

  useEffect(() => {
    const handleAuthChanged = () => setUser(authService.getCurrentUser());
    window.addEventListener(AUTH_CHANGED_EVENT, handleAuthChanged);
    window.addEventListener('storage', handleAuthChanged);

    return () => {
      window.removeEventListener(AUTH_CHANGED_EVENT, handleAuthChanged);
      window.removeEventListener('storage', handleAuthChanged);
    };
  }, []);

  return {
    user,
    isAuthenticated: !!user,
    logout: () => authService.logout(),
  };
};
