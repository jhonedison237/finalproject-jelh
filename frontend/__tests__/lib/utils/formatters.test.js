import {
  formatCurrency,
  formatDate,
  formatDateForInput,
  formatRelativeDate,
  capitalize,
  truncate,
} from '@/lib/utils/formatters';

describe('formatters', () => {
  describe('formatCurrency', () => {
    it('should format positive amounts correctly', () => {
      const result = formatCurrency(1234.56);
      expect(result).toContain('US$');
      expect(result).toContain('1.234,56');
    });

    it('should format negative amounts correctly', () => {
      const result = formatCurrency(-1234.56);
      expect(result).toContain('-');
      expect(result).toContain('1.234,56');
    });

    it('should format zero correctly', () => {
      const result = formatCurrency(0);
      expect(result).toContain('US$');
      expect(result).toContain('0,00');
    });

    it('should handle large numbers', () => {
      const result = formatCurrency(1000000);
      expect(result).toContain('US$');
      expect(result).toContain('1.000.000,00');
    });

    it('should format with two decimal places', () => {
      expect(formatCurrency(10)).toContain('10,00');
      expect(formatCurrency(10.5)).toContain('10,50');
    });
  });

  describe('formatDate', () => {
    it('should format date string with default format', () => {
      const date = '2024-11-13';
      const result = formatDate(date);
      expect(result).toMatch(/\d{2}\/\d{2}\/\d{4}/);
    });

    it('should format date object', () => {
      const date = new Date('2024-11-13');
      const result = formatDate(date);
      expect(result).toMatch(/\d{2}\/\d{2}\/\d{4}/);
    });

    it('should handle custom format', () => {
      const date = '2024-11-13';
      const result = formatDate(date, 'yyyy-MM-dd');
      expect(result).toBe('2024-11-13');
    });

    it('should return empty string for null/undefined', () => {
      expect(formatDate(null)).toBe('');
      expect(formatDate(undefined)).toBe('');
    });
  });

  describe('formatDateForInput', () => {
    it('should format date for input type="date"', () => {
      const date = new Date('2024-11-13T10:30:00');
      expect(formatDateForInput(date)).toBe('2024-11-13');
    });

    it('should format date string', () => {
      const date = '2024-11-13T10:30:00';
      expect(formatDateForInput(date)).toBe('2024-11-13');
    });

    it('should return empty string for null', () => {
      expect(formatDateForInput(null)).toBe('');
    });
  });

  describe('formatRelativeDate', () => {
    it('should return "Hoy" for today', () => {
      const today = new Date();
      expect(formatRelativeDate(today)).toBe('Hoy');
    });

    it('should return "Ayer" for yesterday', () => {
      const yesterday = new Date();
      yesterday.setDate(yesterday.getDate() - 1);
      expect(formatRelativeDate(yesterday)).toBe('Ayer');
    });

    it('should return days ago for recent dates', () => {
      const threeDaysAgo = new Date();
      threeDaysAgo.setDate(threeDaysAgo.getDate() - 3);
      expect(formatRelativeDate(threeDaysAgo)).toBe('Hace 3 días');
    });

    it('should return empty string for null', () => {
      expect(formatRelativeDate(null)).toBe('');
    });
  });

  describe('capitalize', () => {
    it('should capitalize first letter', () => {
      expect(capitalize('hello')).toBe('Hello');
    });

    it('should lowercase rest of string', () => {
      expect(capitalize('HELLO')).toBe('Hello');
    });

    it('should handle single character', () => {
      expect(capitalize('h')).toBe('H');
    });

    it('should return empty string for empty input', () => {
      expect(capitalize('')).toBe('');
      expect(capitalize(null)).toBe('');
    });
  });

  describe('truncate', () => {
    it('should truncate long strings', () => {
      const longString = 'This is a very long string that should be truncated';
      expect(truncate(longString, 20)).toBe('This is a very long ...');
    });

    it('should not truncate short strings', () => {
      const shortString = 'Short';
      expect(truncate(shortString, 20)).toBe('Short');
    });

    it('should use default maxLength of 50', () => {
      const string = 'a'.repeat(100);
      expect(truncate(string)).toHaveLength(53); // 50 + '...'
    });

    it('should return empty string for null/undefined', () => {
      expect(truncate(null)).toBe('');
      expect(truncate(undefined)).toBe('');
    });
  });
});

