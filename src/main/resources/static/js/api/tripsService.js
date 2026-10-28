import { apiClient } from './apiClient.js';

export const tripsService = {
  async getTrips() {
    try {
      return await apiClient.get('/trips');
    } catch {
      return null;
    }
  },

  async getActiveTrip() {
    try {
      return await apiClient.get('/trips/active');
    } catch {
      return null;
    }
  },

  async createTrip(tripData) {
    try {
      return await apiClient.post('/trips', tripData);
    } catch {
      return tripData;
    }
  },

  async updateTrip(tripId, tripData) {
    try {
      return await apiClient.put(`/trips/${tripId}`, tripData);
    } catch {
      return tripData;
    }
  },

  async deleteTrip(tripId) {
    try {
      return await apiClient.delete(`/trips/${tripId}`);
    } catch {
      return null;
    }
  },

  async getTripItems(tripId) {
    try {
      return await apiClient.get(`/trips/${tripId}/items`);
    } catch {
      return null;
    }
  },

  async addTripItem(tripId, itemData) {
    try {
      return await apiClient.post(`/trips/${tripId}/items`, itemData);
    } catch {
      return itemData;
    }
  },

  async updateItemOrder(tripId, items) {
    try {
      return await apiClient.put(`/trips/${tripId}/reorder`, { items });
    } catch {
      return items;
    }
  },

  async removeTripItem(tripId, itemId) {
    try {
      return await apiClient.delete(`/trips/${tripId}/items/${itemId}`);
    } catch {
      return null;
    }
  },

  async syncTripItems(tripId, items) {
    try {
      return await apiClient.post(`/trips/${tripId}/sync`, { items });
    } catch {
      return null;
    }
  }
};
