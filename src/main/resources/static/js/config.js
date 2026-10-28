export const CONFIG = {
  API_BASE_URL: 'http://localhost:8080/api',
  REQUEST_TIMEOUT: 12000,
  STORAGE_KEYS: {
    TRIPS: 'stacktravel_trips',
    ACTIVE_TRIP: 'stacktravel_active_trip',
    TRIP_ITEMS: 'stacktravel_trip_items',
    AUTH_USER: 'stacktravel_auth_user',
    AUTH_TOKEN: 'stacktravel_auth_token',
    PREFERENCES: 'stacktravel_preferences',
    GENERATED_CARDS: 'stacktravel_generated_cards'
  },
  DEFAULT_TRIPS: [],
  DEFAULT_DESTINATION: 'Budapest',
  CARD_GENERATION_COUNT: 7,
  CATEGORIES: [
    'Összes',
    'Múzeum',
    'Szórakozóhely',
    'Étterem'
  ],

  CATEGORY_IMAGES: {
    'Múzeum': 'https://images.unsplash.com/photo-1565008447742-97f6f38c985c?w=700&auto=format&fit=crop&q=80',
    'Szórakozóhely': 'https://images.unsplash.com/photo-1514933651103-005eec06c04b?w=700&auto=format&fit=crop&q=80',
    'Étterem': 'https://images.unsplash.com/photo-1517248135467-4c7edcad34c4?w=700&auto=format&fit=crop&q=80',
    'default': 'https://images.unsplash.com/photo-1488646953014-85cb44e25828?w=700&auto=format&fit=crop&q=80'
  }
};
