'use client';

import { useState } from 'react';
import { MainLayout } from '@/components/layout/MainLayout';
import { SummaryCard } from '@/components/dashboard/SummaryCard';
import { ExpensesChart } from '@/components/dashboard/ExpensesChart';
import { RecentTransactions } from '@/components/dashboard/RecentTransactions';
import { Button } from '@/components/common/Button';
import { Loading } from '@/components/common/Loading';
import { Alert } from '@/components/common/Alert';
import { Select } from '@/components/common/Select';
import { useDashboard } from '@/lib/hooks/useDashboard';
import { DATE_RANGES, DATE_RANGE_LABELS } from '@/lib/utils/constants';
import { getDateRange } from '@/lib/utils/helpers';
import { formatDateForInput } from '@/lib/utils/formatters';
import { Plus } from 'lucide-react';
import { useRouter } from 'next/navigation';

export default function Home() {
  const router = useRouter();
  const [selectedRange, setSelectedRange] = useState(DATE_RANGES.THIS_MONTH);
  const [customDates, setCustomDates] = useState({ startDate: '', endDate: '' });

  const {
    totals,
    expensesByCategory,
    recentTransactions,
    loading,
    error,
    updateDateRange,
  } = useDashboard(selectedRange);

  const handleRangeChange = (e) => {
    const range = e.target.value;
    setSelectedRange(range);

    if (range !== DATE_RANGES.CUSTOM) {
      const { startDate, endDate } = getDateRange(range);
      updateDateRange(formatDateForInput(startDate), formatDateForInput(endDate));
    }
  };

  const handleCustomDateChange = (field, value) => {
    const newDates = { ...customDates, [field]: value };
    setCustomDates(newDates);

    if (newDates.startDate && newDates.endDate) {
      updateDateRange(newDates.startDate, newDates.endDate);
    }
  };

  const dateRangeOptions = Object.keys(DATE_RANGES).map((key) => ({
    value: DATE_RANGES[key],
    label: DATE_RANGE_LABELS[DATE_RANGES[key]],
  }));

  return (
    <MainLayout>
      <div className="space-y-6">
        {/* Header */}
        <div className="flex flex-col gap-4 sm:flex-row sm:items-center sm:justify-between">
          <div>
            <h1 className="text-3xl font-bold text-gray-900">Dashboard</h1>
            <p className="text-gray-600 mt-1">
              Vista general de tus finanzas personales
            </p>
          </div>
          <Button
            variant="primary"
            onClick={() => router.push('/transactions')}
            className="gap-2 !bg-gradient-to-r !from-green-600 !to-green-700 hover:!from-green-700 hover:!to-green-800 !shadow-xl hover:!shadow-2xl !ring-2 !ring-green-300 !font-bold !text-lg !px-6 !py-3"
          >
            <Plus className="h-6 w-6 font-bold" />
            Nueva Transacción
          </Button>
        </div>

        {/* Filtro de fecha */}
        <div className="flex flex-col gap-4 sm:flex-row sm:items-end">
          <Select
            label="Rango de Fecha"
            value={selectedRange}
            onChange={handleRangeChange}
            options={dateRangeOptions}
            containerClassName="sm:w-64"
          />

          {selectedRange === DATE_RANGES.CUSTOM && (
            <>
              <div className="sm:w-48">
                <label className="block text-sm font-medium text-gray-700 mb-1">
                  Fecha Inicio
                </label>
                <input
                  type="date"
                  value={customDates.startDate}
                  onChange={(e) => handleCustomDateChange('startDate', e.target.value)}
                  className="w-full px-3 py-2 border border-gray-300 rounded-lg"
                />
              </div>
              <div className="sm:w-48">
                <label className="block text-sm font-medium text-gray-700 mb-1">
                  Fecha Fin
                </label>
                <input
                  type="date"
                  value={customDates.endDate}
                  onChange={(e) => handleCustomDateChange('endDate', e.target.value)}
                  className="w-full px-3 py-2 border border-gray-300 rounded-lg"
                />
              </div>
            </>
          )}
        </div>

        {/* Error */}
        {error && <Alert type="error" message={error} />}

        {/* Loading */}
        {loading ? (
          <Loading text="Cargando datos del dashboard..." />
        ) : (
          <>
            {/* Resumen (Cards) */}
            <div className="grid grid-cols-1 gap-6 sm:grid-cols-2 lg:grid-cols-3">
              <SummaryCard
                title="Ingresos"
                amount={totals.totalIncome}
                type="income"
              />
              <SummaryCard
                title="Gastos"
                amount={totals.totalExpenses}
                type="expense"
              />
              <SummaryCard
                title="Balance"
                amount={totals.balance}
                type="balance"
              />
            </div>

            {/* Gráficos y Transacciones Recientes */}
            <div className="grid grid-cols-1 gap-6 lg:grid-cols-2">
              <ExpensesChart data={expensesByCategory} />
              <RecentTransactions transactions={recentTransactions} />
            </div>
          </>
        )}
      </div>
    </MainLayout>
  );
}
