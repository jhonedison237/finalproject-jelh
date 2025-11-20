import api from './axios.config';

/**
 * API Client para operaciones de categorías
 */

// Obtener todas las categorías del usuario
export const getAllCategories = async () => {
  const response = await api.get('/categories');
  return response.data;
};

// Obtener una categoría por ID
export const getCategoryById = async (id) => {
  const response = await api.get(`/categories/${id}`);
  return response.data;
};

