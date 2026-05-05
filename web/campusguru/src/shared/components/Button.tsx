import React from 'react';

interface ButtonProps extends React.ButtonHTMLAttributes<HTMLButtonElement> {
  variant?: 'primary' | 'secondary' | 'danger' | 'outline';
  isLoading?: boolean;
}

const Button: React.FC<ButtonProps> = ({
  children,
  variant = 'primary',
  isLoading = false,
  className = '',
  disabled,
  ...props
}) => {
  const baseClasses =
    'inline-flex h-9 items-center justify-center rounded-md px-4 text-sm font-medium transition focus:outline-none focus:ring-2 focus:ring-blue-100';
  
  const variantClasses = {
    primary: 'border border-blue-600 bg-blue-600 text-white hover:bg-blue-700',
    secondary: 'border border-gray-600 bg-gray-600 text-white hover:bg-gray-700',
    danger: 'border border-red-600 bg-red-600 text-white hover:bg-red-700',
    outline: 'border border-gray-300 bg-white text-gray-800 hover:bg-gray-50',
  };

  return (
    <button
      className={`${baseClasses} ${variantClasses[variant]} ${
        disabled || isLoading ? 'cursor-not-allowed opacity-60' : ''
      } ${className}`}
      disabled={disabled || isLoading}
      {...props}
    >
      {isLoading ? (
        <span className="flex items-center justify-center">
          <svg className="mr-2 h-4 w-4 animate-spin" viewBox="0 0 24 24">
            <circle className="opacity-25" cx="12" cy="12" r="10" stroke="currentColor" strokeWidth="4" fill="none" />
            <path className="opacity-75" fill="currentColor" d="M4 12a8 8 0 018-8V0C5.373 0 0 5.373 0 12h4zm2 5.291A7.962 7.962 0 014 12H0c0 3.042 1.135 5.824 3 7.938l3-2.647z" />
          </svg>
          Loading...
        </span>
      ) : (
        children
      )}
    </button>
  );
};

export default Button;
