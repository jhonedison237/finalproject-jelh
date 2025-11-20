import axios from 'axios';

// Crear instancia de axios configurada
const api = axios.create({
  baseURL: process.env.NEXT_PUBLIC_API_BASE_URL || 'http://localhost:8080/api/v1',
  headers: {
    'Content-Type': 'application/json',
  },
  timeout: 10000, // 10 segundos
});

// Interceptor para agregar el user ID en cada request
api.interceptors.request.use(
  (config) => {
    // Agregar el header X-User-Id (usuario hardcodeado por ahora)
    const userId = process.env.NEXT_PUBLIC_DEFAULT_USER_ID || '1';
    config.headers['X-User-Id'] = userId;
    
    return config;
  },
  (error) => {
    return Promise.reject(error);
  }
);

// Interceptor para manejar errores de respuesta
api.interceptors.response.use(
  (response) => {
    return response;
  },
  (error) => {
    // Manejo centralizado de errores
    if (error.response) {
      // El servidor respondió con un código de error
      const { status, data } = error.response;
      
      switch (status) {
        case 400:
          console.error('Bad Request:', data.message || 'Datos inválidos');
          break;
        case 401:
          console.error('Unauthorized:', data.message || 'No autorizado');
          break;
        case 404:
          console.error('Not Found:', data.message || 'Recurso no encontrado');
          break;
        case 422:
          console.error('Validation Error:', data.message || 'Error de validación');
          break;
        case 500:
          console.error('Server Error:', data.message || 'Error del servidor');
          break;
        default:
          console.error('Error:', data.message || 'Error desconocido');
      }
      
      // Propagar el error con información formateada
      error.message = data.message || error.message;
      error.validationErrors = data.validationErrors || data.details;
    } else if (error.request) {
      // La petición se hizo pero no hubo respuesta
      console.error('Network Error: No se pudo conectar con el servidor');
      error.message = 'No se pudo conectar con el servidor. Verifica tu conexión.';
    } else {
      // Algo pasó al configurar la petición
      console.error('Request Error:', error.message);
    }
    
    return Promise.reject(error);
  }
);

export default api;

