'use client';

import { useState } from 'react';
import { TransactionItem } from './TransactionItem';
import { Card, CardBody } from '../common/Card';
import { Button } from '../common/Button';
import { Loading } from '../common/Loading';
import { groupTransactionsByDate } from '@/lib/utils/helpers';
import { formatDate } from '@/lib/utils/formatters';

/**
 * Componente para mostrar lista de transacciones agrupadas por fecha
 */
export const TransactionList = ({
  transactions = [],
  loading = false,
  pagination,
  onEdit,
  onDelete,
  onPageChange,
}) => {
  if (loading) {
    return (
      <Card>
        <CardBody>
          <Loading text="Cargando transacciones..." />
        </CardBody>
      </Card>
    );
  }

  if (!transactions || transactions.length === 0) {
    return (
      <Card>
        <CardBody>
          <div className="flex flex-col items-center justify-center py-12 text-gray-500">
            <svg
              className="h-16 w-16 mb-4 text-gray-300"
              fill="none"
              viewBox="0 0 24 24"
              stroke="currentColor"
            >
              <path
                strokeLinecap="round"
                strokeLinejoin="round"
                strokeWidth={2}
                d="M9 5H7a2 2 0 00-2 2v12a2 2 0 002 2h10a2 2 0 002-2V7a2 2 0 00-2-2h-2M9 5a2 2 0 002 2h2a2 2 0 002-2M9 5a2 2 0 012-2h2a2 2 0 012 2"
              />
            </svg>
            <p className="text-lg font-medium">No hay transacciones</p>
            <p className="text-sm mt-1">
              Crea tu primera transacción para empezar
            </p>
          </div>
        </CardBody>
      </Card>
    );
  }

  // Agrupar transacciones por fecha
  const groupedTransactions = groupTransactionsByDate(transactions);
  const dates = Object.keys(groupedTransactions).sort((a, b) =>
    new Date(b) - new Date(a)
  );

  return (
    <div className="space-y-6">
      {/* Lista de transacciones agrupadas */}
      <Card>
        <CardBody className="p-0">
          {dates.map((date) => (
            <div key={date} className="mb-6 last:mb-0">
              <div className="px-4 py-3 bg-gray-50 border-b border-gray-200">
                <h3 className="font-medium text-gray-700">
                  {formatDate(date, 'EEEE, dd MMMM yyyy')}
                </h3>
              </div>
              <div className="divide-y divide-gray-100">
                {groupedTransactions[date].map((transaction) => (
                  <TransactionItem
                    key={transaction.id}
                    transaction={transaction}
                    onEdit={onEdit}
                    onDelete={onDelete}
                  />
                ))}
              </div>
            </div>
          ))}
        </CardBody>
      </Card>

      {/* Paginación */}
      {pagination && pagination.totalPages > 1 && (
        <div className="flex items-center justify-between">
          <div className="text-sm text-gray-600">
            Mostrando {transactions.length} de {pagination.totalElements}{' '}
            transacciones
          </div>
          <div className="flex gap-2">
            <Button
              variant="outline"
              size="sm"
              onClick={() => onPageChange(pagination.page - 1)}
              disabled={pagination.page === 0}
            >
              ← Anterior
            </Button>
            <span className="px-4 py-2 text-sm text-gray-700">
              Página {pagination.page + 1} de {pagination.totalPages}
            </span>
            <Button
              variant="outline"
              size="sm"
              onClick={() => onPageChange(pagination.page + 1)}
              disabled={pagination.page >= pagination.totalPages - 1}
            >
              Siguiente →
            </Button>
          </div>
        </div>
      )}
    </div>
  );
};

