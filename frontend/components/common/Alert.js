'use client';

import { XCircle, CheckCircle, AlertTriangle, Info } from 'lucide-react';
import { cn } from '@/lib/utils/helpers';

/**
 * Componente Alert reutilizable
 */
export const Alert = ({ 
  type = 'info', 
  title, 
  message, 
  onClose,
  className = '' 
}) => {
  const config = {
    success: {
      icon: CheckCircle,
      bgColor: 'bg-green-50',
      borderColor: 'border-green-200',
      iconColor: 'text-green-600',
      titleColor: 'text-green-800',
      messageColor: 'text-green-700',
    },
    error: {
      icon: XCircle,
      bgColor: 'bg-red-50',
      borderColor: 'border-red-200',
      iconColor: 'text-red-600',
      titleColor: 'text-red-800',
      messageColor: 'text-red-700',
    },
    warning: {
      icon: AlertTriangle,
      bgColor: 'bg-yellow-50',
      borderColor: 'border-yellow-200',
      iconColor: 'text-yellow-600',
      titleColor: 'text-yellow-800',
      messageColor: 'text-yellow-700',
    },
    info: {
      icon: Info,
      bgColor: 'bg-blue-50',
      borderColor: 'border-blue-200',
      iconColor: 'text-blue-600',
      titleColor: 'text-blue-800',
      messageColor: 'text-blue-700',
    },
  };

  const { icon: Icon, bgColor, borderColor, iconColor, titleColor, messageColor } = config[type];

  return (
    <div
      className={cn(
        'rounded-lg border p-4',
        bgColor,
        borderColor,
        className
      )}
    >
      <div className="flex items-start">
        <Icon className={cn('h-5 w-5 mt-0.5', iconColor)} />
        <div className="ml-3 flex-1">
          {title && (
            <h3 className={cn('text-sm font-medium', titleColor)}>
              {title}
            </h3>
          )}
          {message && (
            <p className={cn('text-sm mt-1', messageColor)}>
              {message}
            </p>
          )}
        </div>
        {onClose && (
          <button
            onClick={onClose}
            className={cn('ml-3 text-sm font-medium', titleColor, 'hover:opacity-70')}
          >
            ✕
          </button>
        )}
      </div>
    </div>
  );
};

