'use client';

import { TrendingUp, TrendingDown, DollarSign } from 'lucide-react';
import { Card, CardBody } from '../common/Card';
import { formatCurrency } from '@/lib/utils/formatters';

/**
 * Componente para mostrar tarjetas de resumen (ingresos, gastos, balance)
 */
export const SummaryCard = ({ title, amount, type = 'balance', trend }) => {
  const getIcon = () => {
    switch (type) {
      case 'income':
        return <TrendingUp className="h-6 w-6 text-success-600" />;
      case 'expense':
        return <TrendingDown className="h-6 w-6 text-danger-600" />;
      default:
        return <DollarSign className="h-6 w-6 text-primary-600" />;
    }
  };

  const getTextColor = () => {
    switch (type) {
      case 'income':
        return 'text-success-600';
      case 'expense':
        return 'text-danger-600';
      default:
        return amount >= 0 ? 'text-success-600' : 'text-danger-600';
    }
  };

  const getBgColor = () => {
    switch (type) {
      case 'income':
        return 'bg-gradient-to-br from-green-50 to-green-100 border-green-300';
      case 'expense':
        return 'bg-gradient-to-br from-red-50 to-red-100 border-red-300';
      default:
        return amount >= 0 
          ? 'bg-gradient-to-br from-blue-50 to-blue-100 border-blue-300'
          : 'bg-gradient-to-br from-orange-50 to-orange-100 border-orange-300';
    }
  };

  return (
    <Card className={`hover:shadow-xl transition-all duration-300 border-2 ${getBgColor()}`}>
      <CardBody>
        <div className="flex items-center justify-between">
          <div>
            <p className="text-sm font-bold text-gray-700 uppercase tracking-wide">{title}</p>
            <p className={`text-3xl font-extrabold mt-2 ${getTextColor()}`}>
              {formatCurrency(Math.abs(amount))}
            </p>
            {trend && (
              <p className="text-xs font-medium text-gray-600 mt-1">
                {trend > 0 ? '↑' : '↓'} {Math.abs(trend)}% vs mes anterior
              </p>
            )}
          </div>
          <div className={`p-4 rounded-xl shadow-lg ${
            type === 'income' ? 'bg-green-100' : 
            type === 'expense' ? 'bg-red-100' : 'bg-blue-100'
          }`}>
            {getIcon()}
          </div>
        </div>
      </CardBody>
    </Card>
  );
};

