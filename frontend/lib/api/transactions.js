import api from './axios.config';

/**
 * API Client para operaciones de transacciones
 */

// Obtener todas las transacciones con paginación
export const getAllTransactions = async (params = {}) => {
  const { page = 0, size = 20, sortBy = 'transactionDate', sortDir = 'DESC' } = params;
  const response = await api.get('/transactions', {
    params: { page, size, sortBy, sortDir }
  });
  return response.data;
};

// Obtener transacciones por rango de fechas
export const getTransactionsByDateRange = async (startDate, endDate, params = {}) => {
  const { page = 0, size = 20, sortBy = 'transactionDate', sortDir = 'DESC' } = params;
  const response = await api.get('/transactions/date-range', {
    params: { startDate, endDate, page, size, sortBy, sortDir }
  });
  return response.data;
};

// Obtener una transacción por ID
export const getTransactionById = async (id) => {
  const response = await api.get(`/transactions/${id}`);
  return response.data;
};

// Crear nueva transacción
export const createTransaction = async (data) => {
  const response = await api.post('/transactions', data);
  return response.data;
};

// Actualizar transacción
export const updateTransaction = async (id, data) => {
  const response = await api.put(`/transactions/${id}`, data);
  return response.data;
};

// Eliminar transacción (soft delete)
export const deleteTransaction = async (id) => {
  const response = await api.delete(`/transactions/${id}`);
  return response.data;
};

// Obtener totales (ingresos, gastos, balance)
export const getTransactionTotals = async (startDate, endDate) => {
  const response = await api.get('/transactions/summary/totals', {
    params: { startDate, endDate }
  });
  return response.data;
};

// Obtener gastos agrupados por categoría
export const getExpensesByCategory = async (startDate, endDate) => {
  const response = await api.get('/transactions/summary/by-category', {
    params: { startDate, endDate }
  });
  return response.data;
};

// Obtener transacciones recientes
export const getRecentTransactions = async (limit = 5) => {
  const response = await api.get('/transactions', {
    params: { page: 0, size: limit, sortBy: 'transactionDate', sortDir: 'DESC' }
  });
  return response.data;
};

