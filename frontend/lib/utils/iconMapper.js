/**
 * Mapeo de íconos de Material Design a emojis
 * Convierte los nombres de íconos almacenados en la BD a emojis visuales
 */

export const ICON_MAP = {
  // Ingresos
  attach_money: '💰',
  work: '💼',
  trending_up: '📈',
  
  // Gastos
  restaurant: '🍽️',
  directions_car: '🚗',
  movie: '🎬',
  local_hospital: '🏥',
  school: '🎓',
  home: '🏠',
  build: '🔧',
  checkroom: '👔',
  fitness_center: '💪',
  pets: '🐾',
  card_giftcard: '🎁',
  more_horiz: '📦',
  
  // Fallback por si hay un ícono no mapeado
  default: '📌',
};

/**
 * Convierte un nombre de ícono de Material Design a emoji
 * @param {string} iconName - Nombre del ícono (ej: 'attach_money')
 * @returns {string} Emoji correspondiente
 */
export const getEmojiFromIcon = (iconName) => {
  if (!iconName) return ICON_MAP.default;
  return ICON_MAP[iconName] || ICON_MAP.default;
};

/**
 * Formatea una categoría con su emoji
 * @param {Object} category - Objeto de categoría con icon y name
 * @returns {string} Categoría formateada con emoji
 */
export const formatCategoryLabel = (category) => {
  if (!category) return '';
  const emoji = getEmojiFromIcon(category.icon);
  return `${emoji} ${category.name}`;
};

