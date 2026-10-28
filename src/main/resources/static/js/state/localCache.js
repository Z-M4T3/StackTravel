import { CONFIG } from '../config.js';

export const localCache = {
  getTrips() {
    const raw = localStorage.getItem(CONFIG.STORAGE_KEYS.TRIPS);
    if (!raw) {
      return [];
    }
    try {
      return JSON.parse(raw);
    } catch {
      return [];
    }
  },

  saveTrips(trips) {
    localStorage.setItem(CONFIG.STORAGE_KEYS.TRIPS, JSON.stringify(trips));
  },

  addTrip(trip) {
    const trips = this.getTrips();
    trips.push(trip);
    this.saveTrips(trips);
    return trips;
  },

  updateTrip(updatedTrip) {
    const trips = this.getTrips();
    const index = trips.findIndex(t => t.id === updatedTrip.id);
    if (index !== -1) {
      trips[index] = updatedTrip;
      this.saveTrips(trips);
    }
    return trips;
  },

  deleteTrip(tripId) {
    const trips = this.getTrips();
    const filtered = trips.filter(t => t.id !== tripId);
    this.saveTrips(filtered);
    this.clearTripItems(tripId);

    const active = this.getActiveTrip();
    if (active && active.id === tripId) {
      if (filtered.length > 0) {
        this.saveActiveTrip(filtered[0]);
      } else {
        localStorage.removeItem(CONFIG.STORAGE_KEYS.ACTIVE_TRIP);
      }
    }
    return filtered;
  },

  getActiveTrip() {
    const raw = localStorage.getItem(CONFIG.STORAGE_KEYS.ACTIVE_TRIP);
    if (!raw) {
      const trips = this.getTrips();
      if (trips.length > 0) {
        this.saveActiveTrip(trips[0]);
        return trips[0];
      }
      return null;
    }
    try {
      const parsed = JSON.parse(raw);
      const trips = this.getTrips();
      const exists = trips.find(t => t.id === parsed.id);
      return exists || (trips.length > 0 ? trips[0] : null);
    } catch {
      return null;
    }
  },

  saveActiveTrip(trip) {
    if (!trip) {
      localStorage.removeItem(CONFIG.STORAGE_KEYS.ACTIVE_TRIP);
      return;
    }
    localStorage.setItem(CONFIG.STORAGE_KEYS.ACTIVE_TRIP, JSON.stringify(trip));
  },

  getTripItems(tripId) {
    if (!tripId) return [];
    const raw = localStorage.getItem(`${CONFIG.STORAGE_KEYS.TRIP_ITEMS}_${tripId}`);
    if (!raw) {
      return [];
    }
    try {
      return JSON.parse(raw);
    } catch {
      return [];
    }
  },

  saveTripItems(tripId, items) {
    if (!tripId) return;
    localStorage.setItem(`${CONFIG.STORAGE_KEYS.TRIP_ITEMS}_${tripId}`, JSON.stringify(items));
  },

  addItem(tripId, item) {
    const items = this.getTripItems(tripId);
    items.push(item);
    this.saveTripItems(tripId, items);
    return items;
  },

  removeItem(tripId, itemId) {
    const items = this.getTripItems(tripId);
    const filtered = items.filter(it => it.id !== itemId);
    this.saveTripItems(tripId, filtered);
    return filtered;
  },

  clearTripItems(tripId) {
    if (!tripId) return;
    localStorage.removeItem(`${CONFIG.STORAGE_KEYS.TRIP_ITEMS}_${tripId}`);
  }
};
