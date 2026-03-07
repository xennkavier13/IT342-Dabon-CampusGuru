import React, { useState } from 'react';
import { authService } from '../services/authService';
import Input from '../components/Input';
import Button from '../components/Button';
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
    <div className="min-h-screen flex items-center justify-center bg-gray-100">
      <div className="bg-white p-8 rounded-lg shadow-md w-full max-w-md">
        <h2 className="text-3xl font-bold text-center mb-6 text-gray-800">
          Login to CampusGuru
        </h2>

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

        <form onSubmit={handleSubmit}>
          <Input
            label="Username"
            type="text"
            name="username"
            value={formData.username}
            onChange={handleChange}
            error={errors.username}
            placeholder="Enter your username"
            autoComplete="username"
          />

          <Input
            label="Password"
            type="password"
            name="password"
            value={formData.password}
            onChange={handleChange}
            error={errors.password}
            placeholder="Enter your password"
            autoComplete="current-password"
          />

          <div className="flex items-center justify-between mb-4">
            <Button
              type="submit"
              isLoading={isLoading}
              className="w-full"
            >
              Login
            </Button>
          </div>

          <div className="text-center">
            <p className="text-gray-600 text-sm">
              Don't have an account?{' '}
              <button
                type="button"
                onClick={onNavigateToRegister}
                className="text-blue-500 hover:text-blue-700 font-semibold"
              >
                Register here
              </button>
            </p>
          </div>
        </form>
      </div>
    </div>
  );
};

export default LoginPage;
