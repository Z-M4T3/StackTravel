import { localCache } from './localCache.js';
import { tripsService } from '../api/tripsService.js';
import { authService } from '../api/authService.js';

export const syncManager = {
  async syncOnLogin(activeTripId = 1) {
    if (!authService.isAuthenticated()) {
      return { synced: false, reason: 'unauthenticated' };
    }

    const localItems = localCache.getTripItems(activeTripId);
    if (!localItems || localItems.length === 0) {
      return { synced: true, count: 0 };
    }

    try {
      const result = await tripsService.syncTripItems(activeTripId, localItems);
      return { synced: true, count: localItems.length, result };
    } catch (err) {
      return { synced: false, error: err.message };
    }
  }
};
