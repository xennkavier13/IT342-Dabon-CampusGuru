import React, { useState } from 'react';
import { authService } from '../services/authService';
import Input from '@shared/components/Input';
import Button from '@shared/components/Button';
import type { LoginRequest } from '../types/auth.types';

interface LoginPageProps {
  onLoginSuccess?: () => void;
  onNavigateToRegister?: () => void;
}

const LoginPage: React.FC<LoginPageProps> = ({ onLoginSuccess, onNavigateToRegister }) => {
  const [formData, setFormData] = useState<LoginRequest>({
    username: '',
    password: '',
  });
  const [errors, setErrors] = useState<Partial<LoginRequest>>({});
  const [isLoading, setIsLoading] = useState(false);
  const [apiError, setApiError] = useState('');
  const [successMessage, setSuccessMessage] = useState('');

  const handleChange = (e: React.ChangeEvent<HTMLInputElement>) => {
    const { name, value } = e.target;
    setFormData((prev) => ({ ...prev, [name]: value }));
    // Clear error for this field
    if (errors[name as keyof LoginRequest]) {
      setErrors((prev) => ({ ...prev, [name]: '' }));
    }
    setApiError('');
  };

  const validate = (): boolean => {
    const newErrors: Partial<LoginRequest> = {};

    if (!formData.username.trim()) {
      newErrors.username = 'Username is required';
    }

    if (!formData.password) {
      newErrors.password = 'Password is required';
    } else if (formData.password.length < 6) {
      newErrors.password = 'Password must be at least 6 characters';
    }

    setErrors(newErrors);
    return Object.keys(newErrors).length === 0;
  };

  const handleSubmit = async (e: React.FormEvent) => {
    e.preventDefault();
    setApiError('');
    setSuccessMessage('');

    if (!validate()) {
      return;
    }

    setIsLoading(true);

    try {
      const response = await authService.login(formData);
      
      if (response.userId) {
        setSuccessMessage('Login successful!');
        setTimeout(() => {
          onLoginSuccess?.();
        }, 1000);
      } else {
        setApiError(response.message || 'Login failed');
      }
    } catch (error: any) {
      setApiError(error.message || 'An error occurred during login');
    } finally {
      setIsLoading(false);
    }
  };

  return (
    <div className="flex min-h-screen items-center justify-center bg-gray-100 px-4 py-8">
      <div className="w-full max-w-[360px] rounded-md border border-gray-200 bg-white p-5 shadow-sm sm:p-6">
        <div className="mb-5 text-center">
          <div className="mb-4 flex items-center justify-center gap-1.5">
            <div className="flex h-6 w-6 items-center justify-center rounded-md bg-blue-600 text-xs font-bold text-white">C</div>
            <span className="text-lg font-semibold text-blue-600">CampusGuru</span>
          </div>
          <h2 className="text-[34px] font-bold leading-none text-gray-900">Welcome Back</h2>
          <p className="mt-2 text-xs text-gray-500">Log in to your CampusGuru account</p>
        </div>

        {apiError && (
          <div className="bg-red-100 border border-red-400 text-red-700 px-4 py-3 rounded mb-4">
            {apiError}
          </div>
        )}

        {successMessage && (
          <div className="bg-green-100 border border-green-400 text-green-700 px-4 py-3 rounded mb-4">
            {successMessage}
          </div>
        )}

        <form onSubmit={handleSubmit} className="space-y-0">
          <Input
            label="Email Address"
            type="text"
            name="username"
            value={formData.username}
            onChange={handleChange}
            error={errors.username}
            placeholder="juan@cit.edu"
            autoComplete="username"
          />

          <Input
            label="Password"
            labelRight={(
              <button type="button" className="text-[10px] font-medium text-blue-600 hover:text-blue-700">
                Forgot password?
              </button>
            )}
            type="password"
            name="password"
            value={formData.password}
            onChange={handleChange}
            error={errors.password}
            placeholder="••••••••"
            autoComplete="current-password"
          />

          <div className="mb-3 mt-2">
            <Button
              type="submit"
              isLoading={isLoading}
              className="w-full"
            >
              Log In
            </Button>
          </div>

          <div className="text-center text-xs text-gray-500">
            <p>
              Don&apos;t have an account?{' '}
              <button
                type="button"
                onClick={onNavigateToRegister}
                className="font-semibold text-blue-600 hover:text-blue-700"
              >
                Sign Up
              </button>
            </p>
          </div>
        </form>
      </div>
    </div>
  );
};

export default LoginPage;
