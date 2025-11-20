import { 
  startOfToday, 
  endOfToday, 
  startOfWeek, 
  endOfWeek, 
  startOfMonth, 
  endOfMonth,
  startOfYear,
  endOfYear,
  subMonths,
} from 'date-fns';
import { DATE_RANGES } from './constants';

/**
 * Funciones helper generales
 */

// Obtener rango de fechas según el tipo seleccionado
export const getDateRange = (rangeType) => {
  const today = new Date();

  switch (rangeType) {
    case DATE_RANGES.TODAY:
      return {
        startDate: startOfToday(),
        endDate: endOfToday(),
      };
    
    case DATE_RANGES.THIS_WEEK:
      return {
        startDate: startOfWeek(today, { weekStartsOn: 1 }), // Lunes
        endDate: endOfWeek(today, { weekStartsOn: 1 }),
      };
    
    case DATE_RANGES.THIS_MONTH:
      return {
        startDate: startOfMonth(today),
        endDate: endOfMonth(today),
      };
    
    case DATE_RANGES.LAST_MONTH:
      const lastMonth = subMonths(today, 1);
      return {
        startDate: startOfMonth(lastMonth),
        endDate: endOfMonth(lastMonth),
      };
    
    case DATE_RANGES.THIS_YEAR:
      return {
        startDate: startOfYear(today),
        endDate: endOfYear(today),
      };
    
    default:
      return {
        startDate: startOfMonth(today),
        endDate: endOfMonth(today),
      };
  }
};

// Validar si un monto es válido
export const isValidAmount = (amount) => {
  if (amount === null || amount === undefined || amount === '') return false;
  const numAmount = Number(amount);
  return !isNaN(numAmount) && numAmount > 0;
};

// Normalizar monto a valor positivo (el backend se encarga del signo)
export const normalizeAmount = (amount, transactionType) => {
  const numAmount = Math.abs(Number(amount));
  return numAmount;
};

// Calcular porcentaje
export const calculatePercentage = (value, total) => {
  if (!total || total === 0) return 0;
  return ((value / total) * 100).toFixed(1);
};

// Agrupar transacciones por fecha
export const groupTransactionsByDate = (transactions) => {
  return transactions.reduce((groups, transaction) => {
    const date = transaction.transactionDate.split('T')[0];
    if (!groups[date]) {
      groups[date] = [];
    }
    groups[date].push(transaction);
    return groups;
  }, {});
};

// Clasificar por error
export const cn = (...classes) => {
  return classes.filter(Boolean).join(' ');
};

