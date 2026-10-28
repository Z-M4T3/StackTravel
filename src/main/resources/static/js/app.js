import { store } from './state/store.js';
import { renderTripPanel } from './components/tripPanel.js';
import { renderSearchBar } from './components/searchBar.js';
import { renderPlaceCard, updateAllPlaceCardDateDropdowns } from './components/placeCard.js';
import { renderNavMenu } from './components/navMenu.js';
import { renderTripsView } from './components/tripsView.js';
import { qs } from './utils/domHelpers.js';

function updatePlacesList() {
  const state = store.getState();
  const filters = state.filters;
  const placesGrid = qs('#places-grid');

  if (!placesGrid) return;

  if (filters.isGenerating) {
    placesGrid.innerHTML = `
      <div style="grid-column: 1 / -1; padding: 48px 20px; text-align: center; color: var(--color-primary);">
        <div style="font-size: 15px; font-weight: 700; margin-bottom: 8px;">Kártyák lekérésel...</div>
        <div style="font-size: 13px; color: var(--color-text-muted);">Helyszín: <strong>${filters.destination}</strong> | Kategória: <strong>${filters.category}</strong></div>
      </div>
    `;
    return;
  }

  if (state.generationError) {
    placesGrid.innerHTML = `
      <div style="grid-column: 1 / -1; padding: 36px 20px; text-align: center; background: #fff5f5; border: 1px solid #fed7d7; border-radius: 12px;">
        <div style="font-size: 15px; font-weight: 700; color: var(--color-danger); margin-bottom: 8px;">Nem sikerült kártyákat generálni</div>
        <div style="font-size: 13px; color: var(--color-text); margin-bottom: 12px;">${state.generationError}</div>
        <button id="btn-retry-generate" class="btn-primary" type="button" style="display: inline-flex; align-items: center; gap: 8px; margin: 0 auto;">
          <img src="assets/icons/search.svg" alt="" width="14" height="14" style="filter: brightness(0) invert(1);" />
          <span>Újrapróbálkozás</span>
        </button>
      </div>
    `;
    const retryBtn = placesGrid.querySelector('#btn-retry-generate');
    if (retryBtn) {
      retryBtn.addEventListener('click', () => {
        store.generateCards(filters.destination, filters.category);
      });
    }
    return;
  }

  const places = state.places;
  placesGrid.innerHTML = '';

  if (places.length === 0) {
    placesGrid.innerHTML = `
      <div style="grid-column: 1 / -1; padding: 40px; text-align: center; color: var(--color-text-muted);">
        Nem található kártya a megadott helyszínhez.
      </div>
    `;
    return;
  }

  const activeTrip = state.activeTrip;
  const availableDates = (activeTrip && activeTrip.dates && Array.isArray(activeTrip.dates)) ? activeTrip.dates : [];

  places.forEach(place => {
    const cardEl = renderPlaceCard(place, availableDates);
    placesGrid.appendChild(cardEl);
  });
}

function syncViews() {
  const activeView = store.getState().activeView;
  const plannerView = qs('#view-planner');
  const tripsView = qs('#view-trips');
  const colLeft = qs('#col-left');
  const colRight = qs('#col-right');

  if (activeView === 'trips') {
    if (plannerView) plannerView.style.display = 'none';
    if (tripsView) {
      tripsView.style.display = 'flex';
      renderTripsView(tripsView);
    }
  } else {
    if (tripsView) tripsView.style.display = 'none';
    if (plannerView) {
      plannerView.style.display = 'flex';
    }
    const dates = (store.getState().activeTrip && store.getState().activeTrip.dates) || [];
    updateAllPlaceCardDateDropdowns(dates);
  }

  if (colLeft) renderTripPanel(colLeft);
  if (colRight) renderNavMenu(colRight);
}

function renderAll() {
  const colLeft = qs('#col-left');
  const searchSec = qs('#search-section');
  const colRight = qs('#col-right');

  if (colLeft) {
    renderTripPanel(colLeft);
  }

  if (searchSec) {
    renderSearchBar(searchSec, () => {
      updatePlacesList();
    });
  }

  if (colRight) {
    renderNavMenu(colRight);
  }
}

async function initApp() {
  await store.init();
  renderAll();
  updatePlacesList();
  syncViews();

  store.subscribe((state, changeKey) => {
    if (changeKey === 'generating' || changeKey === 'places') {
      updatePlacesList();
    } else if (changeKey === 'activeTrip') {
      const colLeft = qs('#col-left');
      if (colLeft) renderTripPanel(colLeft);
      const colRight = qs('#col-right');
      if (colRight) renderNavMenu(colRight);
      const searchSec = qs('#search-section');
      if (searchSec) renderSearchBar(searchSec);
      const dates = (state.activeTrip && state.activeTrip.dates) || [];
      updateAllPlaceCardDateDropdowns(dates);
    } else if (changeKey === 'tripItems') {
      const colLeft = qs('#col-left');
      if (colLeft) renderTripPanel(colLeft);
      const colRight = qs('#col-right');
      if (colRight) renderNavMenu(colRight);
    } else if (changeKey === 'trips') {
      const colRight = qs('#col-right');
      if (colRight) renderNavMenu(colRight);
      const colLeft = qs('#col-left');
      if (colLeft) renderTripPanel(colLeft);
      if (state.activeView === 'trips') {
        const tripsView = qs('#view-trips');
        if (tripsView) renderTripsView(tripsView);
      }
    } else if (changeKey === 'activeView') {
      syncViews();
    } else if (changeKey === 'user') {
      const colRight = qs('#col-right');
      if (colRight) renderNavMenu(colRight);
    }
  });
}

document.addEventListener('DOMContentLoaded', initApp);
