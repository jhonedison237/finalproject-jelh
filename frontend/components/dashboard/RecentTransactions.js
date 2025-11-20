'use client';

import Link from 'next/link';
import { ArrowUpRight, ArrowDownRight } from 'lucide-react';
import { Card, CardHeader, CardTitle, CardBody } from '../common/Card';
import { formatCurrency, formatDate } from '@/lib/utils/formatters';
import { PAYMENT_METHOD_ICONS } from '@/lib/utils/constants';
import { getEmojiFromIcon } from '@/lib/utils/iconMapper';

/**
 * Componente para mostrar transacciones recientes
 */
export const RecentTransactions = ({ transactions = [] }) => {
  // Validar que transactions sea un array
  const safeTransactions = Array.isArray(transactions) ? transactions : [];
  
  if (!safeTransactions || safeTransactions.length === 0) {
    return (
      <Card>
        <CardHeader>
          <CardTitle>Transacciones Recientes</CardTitle>
        </CardHeader>
        <CardBody>
          <div className="flex items-center justify-center py-8 text-gray-500">
            No hay transacciones recientes
          </div>
        </CardBody>
      </Card>
    );
  }

  return (
    <Card>
      <CardHeader className="flex items-center justify-between">
        <CardTitle>Transacciones Recientes</CardTitle>
        <Link
          href="/transactions"
          className="text-sm text-primary-600 hover:text-primary-700 font-medium"
        >
          Ver todas →
        </Link>
      </CardHeader>
      <CardBody>
        <div className="space-y-4">
          {safeTransactions.map((transaction) => {
            const isIncome = transaction.transactionType === 'INCOME';
            const Icon = isIncome ? ArrowUpRight : ArrowDownRight;

            return (
              <div
                key={transaction.id}
                className="flex items-center justify-between p-3 hover:bg-gray-50 rounded-lg transition-colors"
              >
                {/* Icono y detalles */}
                <div className="flex items-center gap-3">
                  <div
                    className={`p-2 rounded-full ${
                      isIncome ? 'bg-success-100' : 'bg-danger-100'
                    }`}
                  >
                    <Icon
                      className={`h-5 w-5 ${
                        isIncome ? 'text-success-600' : 'text-danger-600'
                      }`}
                    />
                  </div>
                  <div>
                    <p className="font-medium text-gray-900">
                      {transaction.description}
                    </p>
                    <div className="flex items-center gap-2 mt-1">
                      <span className="text-xs text-gray-500">
                        {formatDate(transaction.transactionDate)}
                      </span>
                      <span className="text-xs">
                        {PAYMENT_METHOD_ICONS[transaction.paymentMethod] || '💳'}
                      </span>
                      <span className="text-xs text-gray-500">
                        {getEmojiFromIcon(transaction.categoryIcon)} {transaction.categoryName}
                      </span>
                    </div>
                  </div>
                </div>

                {/* Monto */}
                <div className="text-right">
                  <p
                    className={`font-semibold ${
                      isIncome ? 'text-success-600' : 'text-danger-600'
                    }`}
                  >
                    {isIncome ? '+' : ''}
                    {formatCurrency(transaction.amount)}
                  </p>
                </div>
              </div>
            );
          })}
        </div>
      </CardBody>
    </Card>
  );
};

