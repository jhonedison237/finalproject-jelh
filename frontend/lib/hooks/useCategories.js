'use client';

import { useState, useEffect } from 'react';
import * as categoriesApi from '../api/categories';

/**
 * Custom Hook para manejar categorías
 */
export const useCategories = () => {
  const [categories, setCategories] = useState([]);
  const [loading, setLoading] = useState(false);
  const [error, setError] = useState(null);

  // Cargar categorías
  const fetchCategories = async () => {
    try {
      setLoading(true);
      setError(null);
      const data = await categoriesApi.getAllCategories();
      setCategories(data || []);
    } catch (err) {
      setError(err.message || 'Error al cargar categorías');
      console.error('Error fetching categories:', err);
    } finally {
      setLoading(false);
    }
  };

  // Obtener categoría por ID
  const getCategoryById = (id) => {
    return categories.find(cat => cat.id === id);
  };

  // Cargar categorías al montar
  useEffect(() => {
    fetchCategories();
  }, []);

  return {
    categories,
    loading,
    error,
    getCategoryById,
    refreshCategories: fetchCategories,
  };
};

