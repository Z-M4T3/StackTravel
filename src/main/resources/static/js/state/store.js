import { localCache } from './localCache.js';
import { authService } from '../api/authService.js';
import { tripsService } from '../api/tripsService.js';
import { placesService } from '../api/placesService.js';
import { syncManager } from './syncManager.js';
import { CONFIG } from '../config.js';

class Store {
  constructor() {
    this.listeners = new Set();
    this.state = {
      user: null,
      trips: [],
      activeTrip: null,
      tripItems: [],
      filters: {
        destination: CONFIG.DEFAULT_DESTINATION,
        category: 'Összes',
        query: '',
        isGenerating: false
      },
      places: [],
      generationError: null,
      activeView: 'planner',
      isSyncing: false
    };
  }

  getState() {
    return this.state;
  }

  subscribe(listener) {
    this.listeners.add(listener);
    return () => this.listeners.delete(listener);
  }

  notify(changedKey) {
    for (const listener of this.listeners) {
      listener(this.state, changedKey);
    }
  }

  async init() {
    const user = authService.getCurrentUser();
    this.state.user = user;

    let trips = [];
    if (user) {
      try {
        const apiTrips = await tripsService.getTrips();
        if (Array.isArray(apiTrips) && apiTrips.length > 0) {
          const validApiTrips = apiTrips.map(t => {
            const startDate = t.startDate || new Date().toISOString().split('T')[0];
            const dates = Array.isArray(t.dates) && t.dates.length > 0 ? t.dates : [startDate];
            return {
              ...t,
              title: t.title || 'Utazás',
              destination: t.destination || CONFIG.DEFAULT_DESTINATION,
              startDate,
              endDate: t.endDate || startDate,
              dates
            };
          });
          trips = validApiTrips;
          localCache.saveTrips(trips);
        } else {
          trips = localCache.getTrips();
        }
      } catch {
        trips = localCache.getTrips();
      }
    } else {
      trips = localCache.getTrips();
    }

    this.state.trips = trips;

    let activeTrip = localCache.getActiveTrip();
    if (activeTrip && !trips.some(t => t.id === activeTrip.id)) {
      activeTrip = trips.length > 0 ? trips[0] : null;
      localCache.saveActiveTrip(activeTrip);
    }

    this.state.activeTrip = activeTrip;

    if (activeTrip && activeTrip.destination) {
      this.state.filters.destination = activeTrip.destination;
    }

    if (activeTrip) {
      let items = [];
      if (user) {
        try {
          const apiItems = await tripsService.getTripItems(activeTrip.id);
          if (Array.isArray(apiItems) && apiItems.length > 0) {
            items = apiItems;
            localCache.saveTripItems(activeTrip.id, items);
          } else {
            items = localCache.getTripItems(activeTrip.id);
          }
        } catch {
          items = localCache.getTripItems(activeTrip.id);
        }
      } else {
        items = localCache.getTripItems(activeTrip.id);
      }
      this.state.tripItems = items;
      this.state.activeView = 'planner';
    } else {
      this.state.tripItems = [];
      this.state.activeView = trips.length === 0 ? 'trips' : 'planner';
    }

    const cachedCardsData = placesService.getCachedCards();
    if (cachedCardsData && cachedCardsData.cards && cachedCardsData.cards.length > 0) {
      this.state.places = cachedCardsData.cards;
      if (cachedCardsData.destination) {
        this.state.filters.destination = cachedCardsData.destination;
      }
    } else {
      const initialDest = this.state.filters.destination || CONFIG.DEFAULT_DESTINATION;
      await this.generateCards(initialDest, this.state.filters.category);
    }

    this.notify('init');
  }

  async setFilters(filtersUpdate) {
    this.state.filters = {
      ...this.state.filters,
      ...filtersUpdate
    };
    this.notify('filters');
  }

  setPlaces(places) {
    this.state.places = places;
    this.notify('places');
  }

  setActiveView(view) {
    this.state.activeView = view;
    this.notify('activeView');
  }

  async generateCards(destination, category = null) {
    const dest = (destination || this.state.filters.destination || CONFIG.DEFAULT_DESTINATION).trim();
    const cat = category !== undefined ? category : this.state.filters.category;

    this.state.filters.destination = dest;
    this.state.filters.category = cat;
    this.state.filters.isGenerating = true;
    this.state.generationError = null;
    this.notify('generating');

    try {
      const cards = await placesService.generateCards(dest, cat, CONFIG.CARD_GENERATION_COUNT);
      this.state.places = cards;
      this.state.generationError = null;
      this.state.filters.isGenerating = false;
      this.notify('places');
      return cards;
    } catch (err) {
      this.state.places = [];
      this.state.generationError = err.message || 'Hiba történt a kártyák generálása közben.';
      this.state.filters.isGenerating = false;
      this.notify('places');
      return [];
    }
  }

  async createTrip({ title, destination, startDate, endDate }) {
    const dates = [];
    if (startDate) {
      let current = new Date(startDate);
      const end = endDate ? new Date(endDate) : new Date(startDate);

      if (end >= current) {
        while (current <= end) {
          const y = current.getFullYear();
          const m = String(current.getMonth() + 1).padStart(2, '0');
          const d = String(current.getDate()).padStart(2, '0');
          dates.push(`${y}-${m}-${d}`);
          current.setDate(current.getDate() + 1);
        }
      } else {
        dates.push(startDate);
      }
    }

    const tripDestination = (destination || CONFIG.DEFAULT_DESTINATION).trim();
    const newTrip = {
      id: Date.now(),
      title: title.trim(),
      destination: tripDestination,
      startDate,
      endDate: endDate || startDate,
      dates: dates.length > 0 ? dates : [startDate]
    };

    if (this.state.user) {
      try {
        const created = await tripsService.createTrip(newTrip);
        if (created && created.id) {
          newTrip.id = created.id;
        }
      } catch {
      }
    }

    const updatedTrips = localCache.addTrip(newTrip);
    localCache.saveActiveTrip(newTrip);

    this.state.trips = updatedTrips;
    this.state.activeTrip = newTrip;
    this.state.tripItems = [];
    this.state.activeView = 'planner';
    this.state.filters.destination = tripDestination;

    this.notify('trips');
    this.notify('activeTrip');
    this.notify('tripItems');
    this.notify('activeView');

    this.generateCards(tripDestination, this.state.filters.category);

    return newTrip;
  }

  async updateTrip(updatedTrip) {
    if (this.state.user) {
      try {
        await tripsService.updateTrip(updatedTrip.id, updatedTrip);
      } catch {
      }
    }

    const updatedTrips = localCache.updateTrip(updatedTrip);
    this.state.trips = updatedTrips;

    if (this.state.activeTrip && this.state.activeTrip.id === updatedTrip.id) {
      this.state.activeTrip = updatedTrip;
      localCache.saveActiveTrip(updatedTrip);
      this.notify('activeTrip');
    }

    this.notify('trips');
  }

  async deleteTrip(tripId) {
    if (this.state.user) {
      try {
        await tripsService.deleteTrip(tripId);
      } catch {
      }
    }

    const updatedTrips = localCache.deleteTrip(tripId);
    this.state.trips = updatedTrips;

    if (this.state.activeTrip && this.state.activeTrip.id === tripId) {
      const nextActive = updatedTrips.length > 0 ? updatedTrips[0] : null;
      this.state.activeTrip = nextActive;
      this.state.tripItems = nextActive ? localCache.getTripItems(nextActive.id) : [];
      if (!nextActive) {
        this.state.activeView = 'trips';
      } else if (nextActive.destination) {
        this.state.filters.destination = nextActive.destination;
        this.generateCards(nextActive.destination, this.state.filters.category);
      }
      this.notify('activeTrip');
      this.notify('tripItems');
      this.notify('activeView');
    }

    this.notify('trips');
  }

  async setActiveTrip(tripOrId) {
    let trip = tripOrId;
    if (typeof tripOrId === 'number' || typeof tripOrId === 'string') {
      trip = this.state.trips.find(t => t.id === Number(tripOrId)) || null;
    }

    if (!trip) return;

    this.state.activeTrip = trip;

    let items = [];
    if (this.state.user) {
      try {
        const apiItems = await tripsService.getTripItems(trip.id);
        if (Array.isArray(apiItems) && apiItems.length > 0) {
          items = apiItems;
          localCache.saveTripItems(trip.id, items);
        } else {
          items = localCache.getTripItems(trip.id);
        }
      } catch {
        items = localCache.getTripItems(trip.id);
      }
    } else {
      items = localCache.getTripItems(trip.id);
    }

    this.state.tripItems = items;
    this.state.activeView = 'planner';

    if (trip.destination && trip.destination !== this.state.filters.destination) {
      this.state.filters.destination = trip.destination;
      this.generateCards(trip.destination, this.state.filters.category);
    }

    localCache.saveActiveTrip(trip);

    this.notify('activeTrip');
    this.notify('tripItems');
    this.notify('activeView');
  }

  async addTripDate(newDate) {
    if (!this.state.activeTrip) return;
    if (this.state.activeTrip.dates.includes(newDate)) return;

    const dates = [...this.state.activeTrip.dates, newDate].sort();
    const updatedTrip = { ...this.state.activeTrip, dates, endDate: dates[dates.length - 1] };

    this.state.activeTrip = updatedTrip;
    await this.updateTrip(updatedTrip);
    this.notify('activeTrip');
  }

  async deleteLastTripDate() {
    if (!this.state.activeTrip || this.state.activeTrip.dates.length <= 1) {
      return null;
    }

    const dates = [...this.state.activeTrip.dates];
    const removedDate = dates.pop();
    const updatedTrip = {
      ...this.state.activeTrip,
      dates,
      endDate: dates[dates.length - 1]
    };

    const tripId = this.state.activeTrip.id;
    const remainingItems = this.state.tripItems.filter(it => it.date !== removedDate);

    this.state.activeTrip = updatedTrip;
    this.state.tripItems = remainingItems;

    await this.updateTrip(updatedTrip);
    localCache.saveTripItems(tripId, remainingItems);

    this.notify('activeTrip');
    this.notify('tripItems');

    return removedDate;
  }

  async addCardToTrip(card, targetDate) {
    if (!this.state.activeTrip) return null;

    const tripId = this.state.activeTrip.id;
    const sameDateItems = this.state.tripItems.filter(it => it.date === targetDate);
    const nextIndex = sameDateItems.length;

    const cardIdNum = (typeof card.id === 'number') ? card.id : (Number(card.id) || null);

    const newItem = {
      id: Date.now(),
      tripId,
      cardId: cardIdNum || card.id,
      date: targetDate,
      index: nextIndex,
      card: {
        id: cardIdNum || card.id,
        name: card.name,
        description: card.description || '',
        category: card.category || 'Látványosság',
        location: card.location || '',
        destination: card.destination || '',
        duration: card.duration || '',
        webpage: card.webpage || '',
        picture: card.picture
      }
    };

    if (this.state.user) {
      try {
        const res = await tripsService.addTripItem(tripId, newItem);
        if (res && res.id) {
          newItem.id = res.id;
        }
      } catch {
      }
    }

    const updated = [...this.state.tripItems, newItem];
    this.state.tripItems = updated;

    localCache.saveTripItems(tripId, updated);

    this.notify('tripItems');
    return newItem;
  }

  async removeTripItem(itemId) {
    if (!this.state.activeTrip) return;

    const tripId = this.state.activeTrip.id;
    const updated = this.state.tripItems.filter(it => it.id !== itemId);
    this.state.tripItems = updated;

    localCache.saveTripItems(tripId, updated);

    if (this.state.user) {
      tripsService.removeTripItem(tripId, itemId).catch(() => {});
    }

    this.notify('tripItems');
  }

  async reorderTripItems(newItems) {
    if (!this.state.activeTrip) return;

    const tripId = this.state.activeTrip.id;
    this.state.tripItems = newItems;

    localCache.saveTripItems(tripId, newItems);

    if (this.state.user) {
      tripsService.updateItemOrder(tripId, newItems).catch(() => {});
    }

    this.notify('tripItems');
  }

  async handleLogin(user) {
    this.state.user = user;
    this.state.isSyncing = true;
    this.notify('user');

    if (this.state.activeTrip) {
      await syncManager.syncOnLogin(this.state.activeTrip.id);
    }

    try {
      const apiTrips = await tripsService.getTrips();
      if (Array.isArray(apiTrips) && apiTrips.length > 0) {
        const validApiTrips = apiTrips.map(t => {
          const startDate = t.startDate || new Date().toISOString().split('T')[0];
          const dates = Array.isArray(t.dates) && t.dates.length > 0 ? t.dates : [startDate];
          return {
            ...t,
            title: t.title || 'Utazás',
            destination: t.destination || CONFIG.DEFAULT_DESTINATION,
            startDate,
            endDate: t.endDate || startDate,
            dates
          };
        });
        this.state.trips = validApiTrips;
        localCache.saveTrips(validApiTrips);
      }
    } catch {
    }

    this.state.isSyncing = false;
    this.notify('sync');
    this.notify('trips');
  }

  async handleLogout() {
    await authService.logout();
    this.state.user = null;
    this.notify('user');
  }
}

export const store = new Store();
