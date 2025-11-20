'use client';

import { useState, useEffect, useCallback } from 'react';
import * as transactionsApi from '../api/transactions';
import { formatDateForInput } from '../utils/formatters';
import { getDateRange } from '../utils/helpers';

/**
 * Custom Hook para manejar transacciones
 */
export const useTransactions = (initialDateRange = 'thisMonth') => {
  const [transactions, setTransactions] = useState([]);
  const [loading, setLoading] = useState(false);
  const [error, setError] = useState(null);
  const [pagination, setPagination] = useState({
    page: 0,
    size: 20,
    totalElements: 0,
    totalPages: 0,
  });

  const [dateRange, setDateRange] = useState(() => {
    const range = getDateRange(initialDateRange);
    return {
      startDate: formatDateForInput(range.startDate),
      endDate: formatDateForInput(range.endDate),
    };
  });

  // Cargar transacciones
  const fetchTransactions = useCallback(async (page = 0) => {
    try {
      setLoading(true);
      setError(null);

      const data = await transactionsApi.getTransactionsByDateRange(
        dateRange.startDate,
        dateRange.endDate,
        { page, size: pagination.size }
      );

      setTransactions(data.content || []);
      setPagination({
        page: data.page || 0,
        size: data.size || 20,
        totalElements: data.totalElements || 0,
        totalPages: data.totalPages || 0,
      });
    } catch (err) {
      setError(err.message || 'Error al cargar transacciones');
      console.error('Error fetching transactions:', err);
    } finally {
      setLoading(false);
    }
  }, [dateRange, pagination.size]);

  // Crear transacción
  const createTransaction = async (transactionData) => {
    try {
      setLoading(true);
      setError(null);
      const newTransaction = await transactionsApi.createTransaction(transactionData);
      await fetchTransactions(pagination.page); // Recargar lista
      return newTransaction;
    } catch (err) {
      setError(err.message || 'Error al crear transacción');
      throw err;
    } finally {
      setLoading(false);
    }
  };

  // Actualizar transacción
  const updateTransaction = async (id, transactionData) => {
    try {
      setLoading(true);
      setError(null);
      const updatedTransaction = await transactionsApi.updateTransaction(id, transactionData);
      await fetchTransactions(pagination.page); // Recargar lista
      return updatedTransaction;
    } catch (err) {
      setError(err.message || 'Error al actualizar transacción');
      throw err;
    } finally {
      setLoading(false);
    }
  };

  // Eliminar transacción
  const deleteTransaction = async (id) => {
    try {
      setLoading(true);
      setError(null);
      await transactionsApi.deleteTransaction(id);
      await fetchTransactions(pagination.page); // Recargar lista
    } catch (err) {
      setError(err.message || 'Error al eliminar transacción');
      throw err;
    } finally {
      setLoading(false);
    }
  };

  // Cambiar página
  const changePage = (newPage) => {
    if (newPage >= 0 && newPage < pagination.totalPages) {
      fetchTransactions(newPage);
    }
  };

  // Actualizar rango de fechas
  const updateDateRange = (newStartDate, newEndDate) => {
    setDateRange({
      startDate: newStartDate,
      endDate: newEndDate,
    });
  };

  // Cargar transacciones al montar y cuando cambien las fechas
  useEffect(() => {
    fetchTransactions(0);
  }, [dateRange.startDate, dateRange.endDate]);

  return {
    transactions,
    loading,
    error,
    pagination,
    dateRange,
    createTransaction,
    updateTransaction,
    deleteTransaction,
    changePage,
    updateDateRange,
    refreshTransactions: () => fetchTransactions(pagination.page),
  };
};

