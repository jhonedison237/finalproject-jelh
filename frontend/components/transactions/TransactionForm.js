'use client';

import { useState, useEffect } from 'react';
import { Input } from '../common/Input';
import { Select } from '../common/Select';
import { Button } from '../common/Button';
import { Alert } from '../common/Alert';
import {
  TRANSACTION_TYPES,
  TRANSACTION_TYPE_LABELS,
  PAYMENT_METHODS,
  PAYMENT_METHOD_LABELS,
} from '@/lib/utils/constants';
import { formatDateForInput, formatCurrency } from '@/lib/utils/formatters';
import { normalizeAmount, isValidAmount } from '@/lib/utils/helpers';
import { formatCategoryLabel } from '@/lib/utils/iconMapper';

/**
 * Formulario para crear/editar transacciones
 */
export const TransactionForm = ({
  transaction = null,
  categories = [],
  onSubmit,
  onCancel,
  loading = false,
}) => {
  const [formData, setFormData] = useState({
    description: '',
    amount: '',
    categoryId: '',
    transactionType: 'EXPENSE',
    paymentMethod: 'CASH',
    transactionDate: formatDateForInput(new Date()),
    notes: '',
  });

  const [errors, setErrors] = useState({});
  const [submitError, setSubmitError] = useState(null);

  // Cargar datos de transacción si es edición
  useEffect(() => {
    if (transaction) {
      setFormData({
        description: transaction.description || '',
        amount: Math.abs(transaction.amount).toString(),
        categoryId: transaction.categoryId?.toString() || '',
        transactionType: transaction.transactionType || 'EXPENSE',
        paymentMethod: transaction.paymentMethod || 'CASH',
        transactionDate: formatDateForInput(transaction.transactionDate),
        notes: transaction.notes || '',
      });
    }
  }, [transaction]);

  const handleChange = (e) => {
    const { name, value } = e.target;
    setFormData((prev) => ({
      ...prev,
      [name]: value,
    }));
    // Limpiar error del campo
    if (errors[name]) {
      setErrors((prev) => ({
        ...prev,
        [name]: null,
      }));
    }
  };

  const validate = () => {
    const newErrors = {};

    if (!formData.description.trim()) {
      newErrors.description = 'La descripción es requerida';
    }

    if (!isValidAmount(formData.amount)) {
      newErrors.amount = 'Ingrese un monto válido mayor a 0';
    }

    if (!formData.categoryId) {
      newErrors.categoryId = 'Seleccione una categoría';
    }

    if (!formData.transactionDate) {
      newErrors.transactionDate = 'La fecha es requerida';
    }

    setErrors(newErrors);
    return Object.keys(newErrors).length === 0;
  };

  const handleSubmit = async (e) => {
    e.preventDefault();
    setSubmitError(null);

    if (!validate()) {
      return;
    }

    try {
      // Normalizar el monto según el tipo de transacción
      const normalizedAmount = normalizeAmount(
        formData.amount,
        formData.transactionType
      );

      const data = {
        description: formData.description.trim(),
        amount: normalizedAmount,
        categoryId: parseInt(formData.categoryId),
        transactionType: formData.transactionType,
        paymentMethod: formData.paymentMethod,
        transactionDate: formData.transactionDate,
        notes: formData.notes.trim() || null,
      };

      await onSubmit(data);
    } catch (error) {
      setSubmitError(error.message || 'Error al guardar la transacción');
    }
  };

  const transactionTypeOptions = Object.keys(TRANSACTION_TYPES).map((key) => ({
    value: TRANSACTION_TYPES[key],
    label: TRANSACTION_TYPE_LABELS[key],
  }));

  const paymentMethodOptions = Object.keys(PAYMENT_METHODS).map((key) => ({
    value: PAYMENT_METHODS[key],
    label: PAYMENT_METHOD_LABELS[key],
  }));

  const categoryOptions = categories.map((cat) => ({
    value: cat.id.toString(),
    label: formatCategoryLabel(cat),
  }));

  return (
    <form onSubmit={handleSubmit} className="space-y-4">
      {submitError && (
        <Alert type="error" message={submitError} onClose={() => setSubmitError(null)} />
      )}

      {/* Tipo de transacción */}
      <Select
        label="Tipo de Transacción"
        name="transactionType"
        value={formData.transactionType}
        onChange={handleChange}
        options={transactionTypeOptions}
        required
        error={errors.transactionType}
      />

      {/* Descripción */}
      <Input
        label="Descripción"
        name="description"
        value={formData.description}
        onChange={handleChange}
        placeholder="Ej: Compra de supermercado"
        maxLength={200}
        required
        error={errors.description}
      />

      {/* Monto */}
      <Input
        label="Monto"
        name="amount"
        type="number"
        step="0.01"
        min="0.01"
        value={formData.amount}
        onChange={handleChange}
        placeholder="0.00"
        required
        error={errors.amount}
      />

      {/* Categoría */}
      <Select
        label="Categoría"
        name="categoryId"
        value={formData.categoryId}
        onChange={handleChange}
        options={categoryOptions}
        placeholder="Seleccionar categoría..."
        required
        error={errors.categoryId}
      />

      {/* Método de pago */}
      <Select
        label="Método de Pago"
        name="paymentMethod"
        value={formData.paymentMethod}
        onChange={handleChange}
        options={paymentMethodOptions}
        required
        error={errors.paymentMethod}
      />

      {/* Fecha */}
      <Input
        label="Fecha"
        name="transactionDate"
        type="date"
        value={formData.transactionDate}
        onChange={handleChange}
        required
        error={errors.transactionDate}
      />

      {/* Notas (opcional) */}
      <div>
        <label className="block text-sm font-medium text-gray-700 mb-1">
          Notas (opcional)
        </label>
        <textarea
          name="notes"
          value={formData.notes}
          onChange={handleChange}
          placeholder="Notas adicionales..."
          rows={3}
          maxLength={500}
          className="w-full px-3 py-2 border border-gray-300 rounded-lg shadow-sm focus:outline-none focus:ring-2 focus:ring-primary-500 focus:border-transparent"
        />
      </div>

      {/* Botones */}
      <div className="flex justify-end gap-3 pt-4">
        <Button 
          type="button" 
          variant="outline" 
          onClick={onCancel}
          className="!border-2 !border-gray-400 !text-gray-700 hover:!bg-gray-100 hover:!border-gray-500 !font-semibold"
        >
          Cancelar
        </Button>
        <Button 
          type="submit" 
          variant="success" 
          loading={loading}
          className="!bg-green-600 !text-white hover:!bg-green-700 !font-bold !shadow-lg"
        >
          {transaction ? 'Actualizar' : 'Guardar'} Transacción
        </Button>
      </div>
    </form>
  );
};

