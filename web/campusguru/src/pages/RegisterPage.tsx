import React, { useState } from 'react';
import { authService } from '../services/authService';
import Input from '../components/Input';
import Button from '../components/Button';
import type { RegisterRequest } from '../types/auth.types';

interface RegisterPageProps {
  onRegisterSuccess?: () => void;
  onNavigateToLogin?: () => void;
}

const RegisterPage: React.FC<RegisterPageProps> = ({ onRegisterSuccess, onNavigateToLogin }) => {
  const [formData, setFormData] = useState<RegisterRequest>({
    username: '',
    institutionalEmail: '',
    password: '',
    firstName: '',
    lastName: '',
    role: 'LEARNER',
  });
  const [confirmPassword, setConfirmPassword] = useState('');
  const [errors, setErrors] = useState<Partial<RegisterRequest & { confirmPassword: string }>>({});
  const [isLoading, setIsLoading] = useState(false);
  const [apiError, setApiError] = useState('');
  const [successMessage, setSuccessMessage] = useState('');

  const handleChange = (e: React.ChangeEvent<HTMLInputElement | HTMLSelectElement>) => {
    const { name, value } = e.target;
    
    if (name === 'confirmPassword') {
      setConfirmPassword(value);
    } else {
      setFormData((prev) => ({ ...prev, [name]: value }));
    }
    
    // Clear error for this field
    if (errors[name as keyof typeof errors]) {
      setErrors((prev) => ({ ...prev, [name]: '' }));
    }
    setApiError('');
  };

  const validate = (): boolean => {
    const newErrors: Partial<RegisterRequest & { confirmPassword: string }> = {};

    if (!formData.username.trim()) {
      newErrors.username = 'Username is required';
    } else if (formData.username.length < 3) {
      newErrors.username = 'Username must be at least 3 characters';
    }

    if (!formData.institutionalEmail.trim()) {
      newErrors.institutionalEmail = 'Email is required';
    } else if (!/\S+@\S+\.\S+/.test(formData.institutionalEmail)) {
      newErrors.institutionalEmail = 'Email is invalid';
    }

    if (!formData.firstName.trim()) {
      newErrors.firstName = 'First name is required';
    }

    if (!formData.lastName.trim()) {
      newErrors.lastName = 'Last name is required';
    }

    if (!formData.password) {
      newErrors.password = 'Password is required';
    } else if (formData.password.length < 6) {
      newErrors.password = 'Password must be at least 6 characters';
    }

    if (!confirmPassword) {
      newErrors.confirmPassword = 'Please confirm your password';
    } else if (formData.password !== confirmPassword) {
      newErrors.confirmPassword = 'Passwords do not match';
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
      const response = await authService.register(formData);
      
      if (response.userId) {
        setSuccessMessage('Registration successful! Redirecting to login...');
        setTimeout(() => {
          onRegisterSuccess?.();
        }, 2000);
      } else {
        setApiError(response.message || 'Registration failed');
      }
    } catch (error: any) {
      setApiError(error.message || 'An error occurred during registration');
    } finally {
      setIsLoading(false);
    }
  };

  return (
    <div className="min-h-screen flex items-center justify-center bg-gray-100 py-8">
      <div className="bg-white p-8 rounded-lg shadow-md w-full max-w-md">
        <h2 className="text-3xl font-bold text-center mb-6 text-gray-800">
          Register for CampusGuru
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
            placeholder="Choose a username"
            autoComplete="username"
          />

          <Input
            label="Institutional Email"
            type="email"
            name="institutionalEmail"
            value={formData.institutionalEmail}
            onChange={handleChange}
            error={errors.institutionalEmail}
            placeholder="your.email@university.edu"
            autoComplete="email"
          />

          <div className="grid grid-cols-2 gap-4">
            <Input
              label="First Name"
              type="text"
              name="firstName"
              value={formData.firstName}
              onChange={handleChange}
              error={errors.firstName}
              placeholder="First name"
              autoComplete="given-name"
            />

            <Input
              label="Last Name"
              type="text"
              name="lastName"
              value={formData.lastName}
              onChange={handleChange}
              error={errors.lastName}
              placeholder="Last name"
              autoComplete="family-name"
            />
          </div>

          <div className="mb-4">
            <label className="block text-gray-700 text-sm font-bold mb-2">
              Role
            </label>
            <select
              name="role"
              value={formData.role}
              onChange={handleChange}
              className="shadow appearance-none border rounded w-full py-2 px-3 text-gray-700 leading-tight focus:outline-none focus:shadow-outline"
            >
              <option value="LEARNER">Learner</option>
              <option value="TUTOR">Tutor</option>
            </select>
          </div>

          <Input
            label="Password"
            type="password"
            name="password"
            value={formData.password}
            onChange={handleChange}
            error={errors.password}
            placeholder="Create a password"
            autoComplete="new-password"
          />

          <Input
            label="Confirm Password"
            type="password"
            name="confirmPassword"
            value={confirmPassword}
            onChange={handleChange}
            error={errors.confirmPassword}
            placeholder="Confirm your password"
            autoComplete="new-password"
          />

          <div className="flex items-center justify-between mb-4">
            <Button
              type="submit"
              isLoading={isLoading}
              className="w-full"
            >
              Register
            </Button>
          </div>

          <div className="text-center">
            <p className="text-gray-600 text-sm">
              Already have an account?{' '}
              <button
                type="button"
                onClick={onNavigateToLogin}
                className="text-blue-500 hover:text-blue-700 font-semibold"
              >
                Login here
              </button>
            </p>
          </div>
        </form>
      </div>
    </div>
  );
};

export default RegisterPage;
