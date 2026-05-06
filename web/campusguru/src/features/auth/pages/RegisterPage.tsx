import React, { useState } from 'react';
import { authService } from '../services/authService';
import Input from '@shared/components/Input';
import Button from '@shared/components/Button';
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

    const payload: RegisterRequest = {
      ...formData,
      username: formData.institutionalEmail.trim().toLowerCase(),
    };

    try {
      const response = await authService.register(payload);
      
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
    <div className="flex min-h-screen items-center justify-center bg-gray-100 px-4 py-8">
      <div className="w-full max-w-[360px] rounded-md border border-gray-200 bg-white p-5 shadow-sm sm:p-6">
        <div className="mb-5 text-center">
          <div className="mb-4 flex items-center justify-center gap-1.5">
            <div className="flex h-6 w-6 items-center justify-center rounded-md bg-blue-600 text-xs font-bold text-white">C</div>
            <span className="text-lg font-semibold text-blue-600">CampusGuru</span>
          </div>
          <h2 className="text-[34px] font-bold leading-none text-gray-900">Create Account</h2>
          <p className="mt-2 text-xs text-gray-500">Join as a learner to find tutors or as a tutor to offer your services</p>
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

        <form onSubmit={handleSubmit}>
          <div className="grid grid-cols-2 gap-2">
            <Input
              label="First Name"
              type="text"
              name="firstName"
              value={formData.firstName}
              onChange={handleChange}
              error={errors.firstName}
              placeholder="Juan"
              autoComplete="given-name"
              containerClassName="mb-3"
            />

            <Input
              label="Last Name"
              type="text"
              name="lastName"
              value={formData.lastName}
              onChange={handleChange}
              error={errors.lastName}
              placeholder="Dela Cruz"
              autoComplete="family-name"
              containerClassName="mb-3"
            />
          </div>

          <Input
            label="Institutional Email"
            type="email"
            name="institutionalEmail"
            value={formData.institutionalEmail}
            onChange={handleChange}
            error={errors.institutionalEmail}
            placeholder="juan@cit.edu"
            autoComplete="email"
            helperText="Must be a university email e.g. juan@cit.edu"
            containerClassName="mb-3"
          />

          <Input
            label="Password"
            type="password"
            name="password"
            value={formData.password}
            onChange={handleChange}
            error={errors.password}
            placeholder="••••••••"
            autoComplete="new-password"
            containerClassName="mb-3"
          />

          <Input
            label="Confirm Password"
            type="password"
            name="confirmPassword"
            value={confirmPassword}
            onChange={handleChange}
            error={errors.confirmPassword}
            placeholder="••••••••"
            autoComplete="new-password"
            containerClassName="mb-3"
          />

          <div className="mb-3">
            <p className="mb-1.5 text-xs font-semibold text-gray-800">I am a</p>
            <div className="grid grid-cols-2 gap-0.5 rounded-md bg-gray-100 p-0.5">
              <Button
                type="button"
                variant={formData.role === 'LEARNER' ? 'primary' : 'outline'}
                className="w-full"
                onClick={() => setFormData((prev) => ({ ...prev, role: 'LEARNER' }))}
              >
                {formData.role === 'LEARNER' ? '✓ Learner' : 'Learner'}
              </Button>
              <Button
                type="button"
                variant={formData.role === 'TUTOR' ? 'primary' : 'outline'}
                className="w-full"
                onClick={() => setFormData((prev) => ({ ...prev, role: 'TUTOR' }))}
              >
                {formData.role === 'TUTOR' ? '✓ Tutor' : 'Tutor'}
              </Button>
            </div>
          </div>

          <div className="mb-3">
            <Button
              type="submit"
              isLoading={isLoading}
              className="w-full"
            >
              Sign Up
            </Button>
          </div>

          <div className="text-center text-xs text-gray-500">
            <p>
              Already have an account?{' '}
              <button
                type="button"
                onClick={onNavigateToLogin}
                className="font-semibold text-blue-600 hover:text-blue-700"
              >
                Log In
              </button>
            </p>
          </div>
        </form>
      </div>
    </div>
  );
};

export default RegisterPage;
