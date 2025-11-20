'use client';

import { cn } from '@/lib/utils/helpers';

/**
 * Componente Input reutilizable
 */
export const Input = ({
  label,
  error,
  helperText,
  className = '',
  containerClassName = '',
  ...props
}) => {
  return (
    <div className={cn('w-full', containerClassName)}>
      {label && (
        <label className="block text-sm font-medium text-gray-700 mb-1">
          {label}
          {props.required && <span className="text-danger-500 ml-1">*</span>}
        </label>
      )}
      <input
        className={cn(
          'w-full px-3 py-2 border rounded-lg shadow-sm',
          'focus:outline-none focus:ring-2 focus:ring-primary-500 focus:border-transparent',
          'disabled:bg-gray-100 disabled:cursor-not-allowed',
          error
            ? 'border-danger-500 focus:ring-danger-500'
            : 'border-gray-300',
          className
        )}
        {...props}
      />
      {error && (
        <p className="mt-1 text-sm text-danger-600">{error}</p>
      )}
      {helperText && !error && (
        <p className="mt-1 text-sm text-gray-500">{helperText}</p>
      )}
    </div>
  );
};

