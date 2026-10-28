import { apiClient } from './apiClient.js';
import { CONFIG } from '../config.js';

export function getCategoryImage(category) {
  if (!category || typeof category !== 'string') {
    return CONFIG.CATEGORY_IMAGES['default'];
  }

  const normalized = category.toLowerCase().trim();

  if (normalized.includes('múzeum') || normalized.includes('museum') || normalized.includes('galéria') || normalized.includes('kiállítás')) {
    return CONFIG.CATEGORY_IMAGES['Múzeum'];
  }
  if (normalized.includes('szórakozó') || normalized.includes('nightlife') || normalized.includes('bár') || normalized.includes('kocsma') || normalized.includes('klub') || normalized.includes('pub')) {
    return CONFIG.CATEGORY_IMAGES['Szórakozóhely'];
  }
  if (normalized.includes('étterem') || normalized.includes('restaurant') || normalized.includes('gasztro') || normalized.includes('kávé') || normalized.includes('bistr') || normalized.includes('csárda')) {
    return CONFIG.CATEGORY_IMAGES['Étterem'];
  }
  if (normalized.includes('természet') || normalized.includes('nature') || normalized.includes('erdő') || normalized.includes('hegy') || normalized.includes('tó')) {
    return CONFIG.CATEGORY_IMAGES['Természet'];
  }
  if (normalized.includes('park') || normalized.includes('kert') || normalized.includes('arborétum') || normalized.includes('séta')) {
    return CONFIG.CATEGORY_IMAGES['Park'];
  }
  if (normalized.includes('történelmi') || normalized.includes('vár') || normalized.includes('kastély') || normalized.includes('műemlék') || normalized.includes('templom') || normalized.includes('dóm') || normalized.includes('bazilika')) {
    return CONFIG.CATEGORY_IMAGES['Történelmi látványosság'];
  }
  if (normalized.includes('kultúra') || normalized.includes('színház') || normalized.includes('opera') || normalized.includes('művészet')) {
    return CONFIG.CATEGORY_IMAGES['Kultúra'];
  }
  if (normalized.includes('strand') || normalized.includes('fürdő') || normalized.includes('termál') || normalized.includes('spa') || normalized.includes('wellness') || normalized.includes('víz')) {
    return CONFIG.CATEGORY_IMAGES['Strand'];
  }

  return CONFIG.CATEGORY_IMAGES['default'];
}

export const placesService = {
  getCategoryImage,

  async generateCards(destination, category = null, count = CONFIG.CARD_GENERATION_COUNT) {
    const targetDestination = (destination || CONFIG.DEFAULT_DESTINATION).trim();
    const params = {
      destination: targetDestination,
      count
    };

    if (category && category !== 'Összes') {
      params.category = category.trim();
    }

    const response = await apiClient.get('/cards/generate', params);
    const list = Array.isArray(response) ? response : (response.cards || response.data || []);

    if (list.length === 0) {
      return [];
    }

    const generated = list.map((item, index) => {
      const itemCategory = item.category || (category && category !== 'Összes' ? category : 'Látványosság');
      return {
        id: item.id || ('card-' + Date.now() + '-' + index),
        name: item.name || `${targetDestination} látványosság`,
        description: item.description || '',
        category: itemCategory,
        location: item.location || targetDestination,
        destination: item.destination || targetDestination,
        webpage: item.webpage || '',
        duration: item.duration || '1-2 óra',
        picture: item.picture || getCategoryImage(itemCategory)
      };
    });

    this.cacheGeneratedCards(targetDestination, generated);
    return generated;
  },

  cacheGeneratedCards(destination, cards) {
    try {
      const payload = {
        destination,
        timestamp: Date.now(),
        cards
      };
      localStorage.setItem(CONFIG.STORAGE_KEYS.GENERATED_CARDS, JSON.stringify(payload));
    } catch {
    }
  },

  clearCachedCards() {
    try {
      localStorage.removeItem(CONFIG.STORAGE_KEYS.GENERATED_CARDS);
    } catch {
    }
  },

  getCachedCards() {
    try {
      const raw = localStorage.getItem(CONFIG.STORAGE_KEYS.GENERATED_CARDS);
      if (!raw) return null;
      const parsed = JSON.parse(raw);
      if (!parsed || !Array.isArray(parsed.cards)) return null;
      if (parsed.cards.some(c => !c || !c.id || c.id.toString().startsWith('fallback-'))) {
        this.clearCachedCards();
        return null;
      }
      return parsed;
    } catch {
      return null;
    }
  },

  getCategories() {
    return CONFIG.CATEGORIES;
  }
};
