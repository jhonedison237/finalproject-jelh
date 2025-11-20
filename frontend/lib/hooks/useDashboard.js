'use client';

import { useState, useEffect, useCallback } from 'react';
import * as transactionsApi from '../api/transactions';
import { formatDateForInput } from '../utils/formatters';
import { getDateRange } from '../utils/helpers';

/**
 * Custom Hook para manejar datos del dashboard
 */
export const useDashboard = (initialDateRange = 'thisMonth') => {
  const [totals, setTotals] = useState({
    totalIncome: 0,
    totalExpenses: 0,
    balance: 0,
  });
  
  const [expensesByCategory, setExpensesByCategory] = useState([]);
  const [recentTransactions, setRecentTransactions] = useState([]);
  const [loading, setLoading] = useState(false);
  const [error, setError] = useState(null);

  const [dateRange, setDateRange] = useState(() => {
    const range = getDateRange(initialDateRange);
    return {
      startDate: formatDateForInput(range.startDate),
      endDate: formatDateForInput(range.endDate),
    };
  });

  // Cargar todos los datos del dashboard
  const fetchDashboardData = useCallback(async () => {
    try {
      setLoading(true);
      setError(null);

      // Cargar en paralelo: totales, gastos por categoría, transacciones recientes
      const [totalsData, categoryData, recentData] = await Promise.all([
        transactionsApi.getTransactionTotals(dateRange.startDate, dateRange.endDate),
        transactionsApi.getExpensesByCategory(dateRange.startDate, dateRange.endDate),
        transactionsApi.getRecentTransactions(5),
      ]);

      setTotals(totalsData || { totalIncome: 0, totalExpenses: 0, balance: 0 });
      
      // Transformar Map a Array si es necesario
      let formattedCategoryData = [];
      if (categoryData) {
        if (Array.isArray(categoryData)) {
          formattedCategoryData = categoryData;
        } else if (typeof categoryData === 'object') {
          // Convertir objeto/mapa a array
          const categoryArray = Object.entries(categoryData).map(([categoryName, totalAmount]) => ({
            categoryName,
            totalAmount: Math.abs(Number(totalAmount)),
          }));
          
          // Calcular total para porcentajes
          const total = categoryArray.reduce((sum, item) => sum + item.totalAmount, 0);
          
          // Agregar porcentajes
          formattedCategoryData = categoryArray.map(item => ({
            ...item,
            percentage: total > 0 ? ((item.totalAmount / total) * 100).toFixed(1) : 0,
          }));
        }
      }
      
      setExpensesByCategory(formattedCategoryData);
      setRecentTransactions(Array.isArray(recentData?.content) ? recentData.content : []);
    } catch (err) {
      setError(err.message || 'Error al cargar datos del dashboard');
      console.error('Error fetching dashboard data:', err);
    } finally {
      setLoading(false);
    }
  }, [dateRange]);

  // Actualizar rango de fechas
  const updateDateRange = (newStartDate, newEndDate) => {
    setDateRange({
      startDate: newStartDate,
      endDate: newEndDate,
    });
  };

  // Cargar datos al montar y cuando cambien las fechas
  useEffect(() => {
    fetchDashboardData();
  }, [dateRange.startDate, dateRange.endDate]);

  return {
    totals,
    expensesByCategory,
    recentTransactions,
    loading,
    error,
    dateRange,
    updateDateRange,
    refreshDashboard: fetchDashboardData,
  };
};

