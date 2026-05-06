import React from 'react';

interface InputProps extends React.InputHTMLAttributes<HTMLInputElement> {
  label: string;
  error?: string;
  helperText?: string;
  containerClassName?: string;
  labelRight?: React.ReactNode;
}

const Input: React.FC<InputProps> = ({
  label,
  error,
  helperText,
  className = '',
  containerClassName = '',
  labelRight,
  ...props
}) => {
  return (
    <div className={`mb-4 ${containerClassName}`}>
      <div className="mb-1.5 flex items-center justify-between gap-2">
        <label className="block text-xs font-semibold text-gray-800">{label}</label>
        {labelRight}
      </div>
      <input
        className={`h-9 w-full rounded-md border bg-white px-3 text-sm text-gray-900 outline-none transition focus:border-blue-500 focus:ring-2 focus:ring-blue-100 ${
          error ? 'border-red-500' : 'border-gray-300'
        } ${className}`}
        {...props}
      />
      {!error && helperText && <p className="mt-1 text-[10px] text-gray-500">{helperText}</p>}
      {error && <p className="text-red-500 text-xs italic mt-1">{error}</p>}
    </div>
  );
};

export default Input;
