import {
  getDateRange,
  isValidAmount,
  normalizeAmount,
  calculatePercentage,
  groupTransactionsByDate,
  cn,
} from '@/lib/utils/helpers';
import { DATE_RANGES } from '@/lib/utils/constants';
import { startOfMonth, endOfMonth } from 'date-fns';

describe('helpers', () => {
  describe('getDateRange', () => {
    it('should return today range for TODAY', () => {
      const { startDate, endDate } = getDateRange(DATE_RANGES.TODAY);
      expect(startDate).toBeInstanceOf(Date);
      expect(endDate).toBeInstanceOf(Date);
      expect(startDate.toDateString()).toBe(endDate.toDateString());
    });

    it('should return this month range for THIS_MONTH', () => {
      const { startDate, endDate } = getDateRange(DATE_RANGES.THIS_MONTH);
      const now = new Date();
      expect(startDate.getMonth()).toBe(now.getMonth());
      expect(endDate.getMonth()).toBe(now.getMonth());
    });

    it('should return default range for invalid input', () => {
      const { startDate, endDate } = getDateRange('invalid');
      expect(startDate).toBeInstanceOf(Date);
      expect(endDate).toBeInstanceOf(Date);
    });
  });

  describe('isValidAmount', () => {
    it('should return true for valid positive numbers', () => {
      expect(isValidAmount(100)).toBe(true);
      expect(isValidAmount(0.01)).toBe(true);
      expect(isValidAmount('50')).toBe(true);
    });

    it('should return false for zero or negative', () => {
      expect(isValidAmount(0)).toBe(false);
      expect(isValidAmount(-10)).toBe(false);
    });

    it('should return false for null/undefined/empty', () => {
      expect(isValidAmount(null)).toBe(false);
      expect(isValidAmount(undefined)).toBe(false);
      expect(isValidAmount('')).toBe(false);
    });

    it('should return false for NaN', () => {
      expect(isValidAmount('abc')).toBe(false);
    });
  });

  describe('normalizeAmount', () => {
    it('should return negative for EXPENSE', () => {
      expect(normalizeAmount(100, 'EXPENSE')).toBe(-100);
      expect(normalizeAmount(-100, 'EXPENSE')).toBe(-100);
    });

    it('should return positive for INCOME', () => {
      expect(normalizeAmount(100, 'INCOME')).toBe(100);
      expect(normalizeAmount(-100, 'INCOME')).toBe(100);
    });

    it('should handle string amounts', () => {
      expect(normalizeAmount('100', 'EXPENSE')).toBe(-100);
      expect(normalizeAmount('100', 'INCOME')).toBe(100);
    });
  });

  describe('calculatePercentage', () => {
    it('should calculate percentage correctly', () => {
      expect(calculatePercentage(25, 100)).toBe('25.0');
      expect(calculatePercentage(33, 100)).toBe('33.0');
      expect(calculatePercentage(1, 3)).toBe('33.3');
    });

    it('should return 0 for zero total', () => {
      expect(calculatePercentage(10, 0)).toBe(0);
    });

    it('should return 0 for null/undefined total', () => {
      expect(calculatePercentage(10, null)).toBe(0);
      expect(calculatePercentage(10, undefined)).toBe(0);
    });

    it('should handle decimal results', () => {
      expect(calculatePercentage(1, 7)).toBe('14.3');
    });
  });

  describe('groupTransactionsByDate', () => {
    it('should group transactions by date', () => {
      const transactions = [
        { id: 1, transactionDate: '2024-11-13T10:00:00' },
        { id: 2, transactionDate: '2024-11-13T15:00:00' },
        { id: 3, transactionDate: '2024-11-12T10:00:00' },
      ];

      const grouped = groupTransactionsByDate(transactions);

      expect(grouped['2024-11-13']).toHaveLength(2);
      expect(grouped['2024-11-12']).toHaveLength(1);
    });

    it('should handle empty array', () => {
      const grouped = groupTransactionsByDate([]);
      expect(grouped).toEqual({});
    });

    it('should handle single transaction', () => {
      const transactions = [
        { id: 1, transactionDate: '2024-11-13T10:00:00' },
      ];

      const grouped = groupTransactionsByDate(transactions);
      expect(grouped['2024-11-13']).toHaveLength(1);
    });
  });

  describe('cn', () => {
    it('should combine class names', () => {
      expect(cn('class1', 'class2')).toBe('class1 class2');
    });

    it('should filter falsy values', () => {
      expect(cn('class1', false, 'class2', null, undefined)).toBe('class1 class2');
    });

    it('should handle empty input', () => {
      expect(cn()).toBe('');
    });

    it('should handle conditional classes', () => {
      const isActive = true;
      expect(cn('base', isActive && 'active')).toBe('base active');
    });
  });
});

