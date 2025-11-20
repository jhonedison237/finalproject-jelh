'use client';

import { useState } from 'react';
import { MainLayout } from '@/components/layout/MainLayout';
import { TransactionList } from '@/components/transactions/TransactionList';
import { TransactionForm } from '@/components/transactions/TransactionForm';
import { Button } from '@/components/common/Button';
import { Modal, ModalFooter } from '@/components/common/Modal';
import { Alert } from '@/components/common/Alert';
import { Select } from '@/components/common/Select';
import { useTransactions } from '@/lib/hooks/useTransactions';
import { useCategories } from '@/lib/hooks/useCategories';
import { Plus, Filter } from 'lucide-react';
import { DATE_RANGES, DATE_RANGE_LABELS } from '@/lib/utils/constants';
import { getDateRange } from '@/lib/utils/helpers';
import { formatDateForInput } from '@/lib/utils/formatters';

export default function TransactionsPage() {
  const [selectedRange, setSelectedRange] = useState(DATE_RANGES.THIS_MONTH);
  const [customDates, setCustomDates] = useState({ startDate: '', endDate: '' });
  const [showModal, setShowModal] = useState(false);
  const [editingTransaction, setEditingTransaction] = useState(null);
  const [showDeleteConfirm, setShowDeleteConfirm] = useState(null);
  const [successMessage, setSuccessMessage] = useState(null);

  const {
    transactions,
    loading,
    error,
    pagination,
    createTransaction,
    updateTransaction,
    deleteTransaction,
    changePage,
    updateDateRange,
  } = useTransactions(selectedRange);

  const { categories } = useCategories();

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

  const handleCreateNew = () => {
    setEditingTransaction(null);
    setShowModal(true);
  };

  const handleEdit = (transaction) => {
    setEditingTransaction(transaction);
    setShowModal(true);
  };

  const handleCloseModal = () => {
    setShowModal(false);
    setEditingTransaction(null);
  };

  const handleSubmit = async (data) => {
    try {
      if (editingTransaction) {
        await updateTransaction(editingTransaction.id, data);
        setSuccessMessage('Transacción actualizada exitosamente');
      } else {
        await createTransaction(data);
        setSuccessMessage('Transacción creada exitosamente');
      }
      handleCloseModal();
      setTimeout(() => setSuccessMessage(null), 3000);
    } catch (err) {
      console.error('Error submitting transaction:', err);
    }
  };

  const handleDeleteClick = (transaction) => {
    setShowDeleteConfirm(transaction);
  };

  const handleConfirmDelete = async () => {
    if (showDeleteConfirm) {
      try {
        await deleteTransaction(showDeleteConfirm.id);
        setSuccessMessage('Transacción eliminada exitosamente');
        setShowDeleteConfirm(null);
        setTimeout(() => setSuccessMessage(null), 3000);
      } catch (err) {
        console.error('Error deleting transaction:', err);
      }
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
            <h1 className="text-3xl font-bold text-gray-900">Transacciones</h1>
            <p className="text-gray-600 mt-1">
              Gestiona todos tus ingresos y gastos
            </p>
          </div>
          <Button 
            variant="primary" 
            onClick={handleCreateNew} 
            className="gap-2 !bg-gradient-to-r !from-green-600 !to-green-700 hover:!from-green-700 hover:!to-green-800 !shadow-xl hover:!shadow-2xl !ring-2 !ring-green-300 !font-bold !text-lg !px-6 !py-3"
          >
            <Plus className="h-6 w-6 font-bold" />
            Nueva Transacción
          </Button>
        </div>

        {/* Success Message */}
        {successMessage && (
          <Alert
            type="success"
            message={successMessage}
            onClose={() => setSuccessMessage(null)}
          />
        )}

        {/* Error */}
        {error && <Alert type="error" message={error} />}

        {/* Filtros */}
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

        {/* Lista de transacciones */}
        <TransactionList
          transactions={transactions}
          loading={loading}
          pagination={pagination}
          onEdit={handleEdit}
          onDelete={handleDeleteClick}
          onPageChange={changePage}
        />

        {/* Modal de formulario */}
        <Modal
          isOpen={showModal}
          onClose={handleCloseModal}
          title={editingTransaction ? 'Editar Transacción' : 'Nueva Transacción'}
          size="lg"
        >
          <TransactionForm
            transaction={editingTransaction}
            categories={categories}
            onSubmit={handleSubmit}
            onCancel={handleCloseModal}
            loading={loading}
          />
        </Modal>

        {/* Modal de confirmación de eliminación */}
        <Modal
          isOpen={!!showDeleteConfirm}
          onClose={() => setShowDeleteConfirm(null)}
          title="Confirmar Eliminación"
          size="sm"
        >
          <div className="space-y-4">
            <p className="text-gray-700">
              ¿Estás seguro de que deseas eliminar la transacción "
              <strong>{showDeleteConfirm?.description}</strong>"?
            </p>
            <p className="text-sm text-gray-500">
              Esta acción no se puede deshacer.
            </p>
          </div>
          <ModalFooter>
            <Button
              variant="ghost"
              onClick={() => setShowDeleteConfirm(null)}
            >
              Cancelar
            </Button>
            <Button
              variant="danger"
              onClick={handleConfirmDelete}
              loading={loading}
            >
              Eliminar
            </Button>
          </ModalFooter>
        </Modal>
      </div>
    </MainLayout>
  );
}

