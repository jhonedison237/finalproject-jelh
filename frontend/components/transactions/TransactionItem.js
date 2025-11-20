'use client';

import { Edit, Trash2, ArrowUpRight, ArrowDownRight } from 'lucide-react';
import { formatCurrency, formatDate } from '@/lib/utils/formatters';
import { PAYMENT_METHOD_ICONS, PAYMENT_METHOD_LABELS } from '@/lib/utils/constants';
import { getEmojiFromIcon } from '@/lib/utils/iconMapper';
import { Button } from '../common/Button';

/**
 * Componente para mostrar un item de transacción en la lista
 */
export const TransactionItem = ({ transaction, onEdit, onDelete }) => {
  const isIncome = transaction.transactionType === 'INCOME';
  const Icon = isIncome ? ArrowUpRight : ArrowDownRight;

  return (
    <div className="flex items-center justify-between p-4 hover:bg-gray-50 rounded-lg transition-colors border-b border-gray-100 last:border-b-0">
      {/* Icono y detalles */}
      <div className="flex items-center gap-4 flex-1">
        <div
          className={`p-3 rounded-full ${
            isIncome ? 'bg-success-100' : 'bg-danger-100'
          }`}
        >
          <Icon
            className={`h-6 w-6 ${
              isIncome ? 'text-success-600' : 'text-danger-600'
            }`}
          />
        </div>

        <div className="flex-1">
          <div className="flex items-center gap-2">
            <h4 className="font-medium text-gray-900">
              {transaction.description}
            </h4>
            <span className="text-sm px-2 py-0.5 rounded-full bg-gray-100 text-gray-600">
              {getEmojiFromIcon(transaction.categoryIcon)} {transaction.categoryName}
            </span>
          </div>

          <div className="flex items-center gap-4 mt-1 text-sm text-gray-500">
            <span>{formatDate(transaction.transactionDate)}</span>
            <span className="flex items-center gap-1">
              <span>{PAYMENT_METHOD_ICONS[transaction.paymentMethod] || '💳'}</span>
              {PAYMENT_METHOD_LABELS[transaction.paymentMethod]}
            </span>
          </div>

          {transaction.notes && (
            <p className="text-sm text-gray-600 mt-1 italic">
              {transaction.notes}
            </p>
          )}
        </div>
      </div>

      {/* Monto y acciones */}
      <div className="flex items-center gap-4">
        <div className="text-right">
          <p
            className={`text-xl font-semibold ${
              isIncome ? 'text-success-600' : 'text-danger-600'
            }`}
          >
            {isIncome ? '+' : ''}
            {formatCurrency(transaction.amount)}
          </p>
        </div>

        <div className="flex gap-2">
          <Button
            variant="ghost"
            size="sm"
            onClick={() => onEdit(transaction)}
            className="p-2"
          >
            <Edit className="h-4 w-4" />
          </Button>
          <Button
            variant="ghost"
            size="sm"
            onClick={() => onDelete(transaction)}
            className="p-2 text-danger-600 hover:bg-danger-50"
          >
            <Trash2 className="h-4 w-4" />
          </Button>
        </div>
      </div>
    </div>
  );
};

