'use client';

import { cn } from '@/lib/utils/helpers';

/**
 * Componente Card reutilizable
 */
export const Card = ({ children, className = '', ...props }) => {
  return (
    <div
      className={cn(
        'bg-white rounded-xl border border-gray-300 shadow-md hover:shadow-lg transition-shadow',
        className
      )}
      {...props}
    >
      {children}
    </div>
  );
};

export const CardHeader = ({ children, className = '', ...props }) => {
  return (
    <div
      className={cn('px-6 py-4 border-b border-gray-300 bg-gray-50', className)}
      {...props}
    >
      {children}
    </div>
  );
};

export const CardTitle = ({ children, className = '', ...props }) => {
  return (
    <h3
      className={cn('text-lg font-bold text-gray-900', className)}
      {...props}
    >
      {children}
    </h3>
  );
};

export const CardBody = ({ children, className = '', ...props }) => {
  return (
    <div className={cn('px-6 py-4', className)} {...props}>
      {children}
    </div>
  );
};

export const CardFooter = ({ children, className = '', ...props }) => {
  return (
    <div
      className={cn('px-6 py-4 border-t border-gray-200 bg-gray-50', className)}
      {...props}
    >
      {children}
    </div>
  );
};

