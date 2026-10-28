import { store } from '../state/store.js';
import { setupDragAndDrop } from '../utils/dragAndDrop.js';
import { formatDateHungarian, showToast } from '../utils/domHelpers.js';
import { openTripModal } from './tripsView.js';

export function renderTripPanel(container) {
  const state = store.getState();
  const trip = state.activeTrip;
  const trips = state.trips;
  const items = state.tripItems;

  if (!trip) {
    container.innerHTML = `
      <div class="logo-container">
        <img src="assets/images/logo.svg" alt="StackTravel Logo" width="250" height="32" />
      </div>
      <div class="trip-empty-panel">
        <div class="trip-empty-title">Nincs aktív utazás</div>
        <p class="trip-empty-text">A tervezés megkezdéséhez hozz létre egy utazást!</p>
        <button id="btn-create-first-trip" class="btn-primary" type="button" style="width: 100%; justify-content: center;">
          <img src="assets/icons/plus.svg" alt="" width="16" height="16" style="filter: brightness(0) invert(1);" />
          <span>Új utazás létrehozása</span>
        </button>
      </div>
    `;

    const createBtn = container.querySelector('#btn-create-first-trip');
    if (createBtn) {
      createBtn.addEventListener('click', () => {
        openTripModal();
      });
    }
    return;
  }

  const tripOptionsHtml = trips.map(t => `
    <option value="${t.id}" ${t.id === trip.id ? 'selected' : ''}>
      ${t.title} (${t.startDate || ''})
    </option>
  `).join('');

  const tripDates = (trip.dates && Array.isArray(trip.dates) && trip.dates.length > 0)
    ? trip.dates
    : [trip.startDate || new Date().toISOString().split('T')[0]];

  container.innerHTML = `
    <div class="logo-container">
      <img src="assets/images/logo.svg" alt="StackTravel Logo" width="250" height="32" />
    </div>
    
    <div class="trip-header">
      <div class="trip-switcher-wrapper">
        <label for="trip-switcher-select" class="trip-switcher-label">Kiválasztott utazás:</label>
        <div style="display: flex; gap: 6px; align-items: center;">
          <select id="trip-switcher-select" class="trip-picker-dropdown">
            ${tripOptionsHtml}
          </select>
          <button id="btn-new-trip-shortcut" class="btn-icon-shortcut" type="button" title="Új utazás létrehozása">
            <img src="assets/icons/plus.svg" alt="" width="14" height="14" />
          </button>
        </div>
      </div>

      <div class="trip-selector-row">
        <div>
          <h2 class="trip-title">${trip.title}</h2>
          <span class="trip-badge">${trip.destination}</span>
        </div>
      </div>
      
      <div class="trip-actions">
        <button id="btn-add-date" class="btn-sm" type="button">
          <img src="assets/icons/plus.svg" alt="" width="14" height="14" />
          <span>Új nap</span>
        </button>
      </div>
    </div>

    <div class="itinerary-scroll" id="itinerary-scroll-container">
      ${tripDates.map((dateStr, idx) => {
        const dateItems = items
          .filter(it => it.date === dateStr)
          .sort((a, b) => a.index - b.index);

        const isLastDate = idx === tripDates.length - 1 && tripDates.length > 1;

        return `
          <div class="date-group" data-date="${dateStr}">
            <div class="date-group-header">
              <span class="date-group-title">
                <img src="assets/icons/calendar.svg" alt="" width="14" height="14" />
                ${formatDateHungarian(dateStr, idx)}
              </span>
              <div style="display: flex; align-items: center; gap: 8px;">
                <span class="date-group-count">${dateItems.length} program</span>
                ${isLastDate ? `
                  <button class="btn-icon-remove" data-action="delete-last-day" title="Nap törlése (egy lépésben)">
                    <img src="assets/icons/trash.svg" alt="" width="12" height="12" />
                  </button>
                ` : ''}
              </div>
            </div>
            <div class="date-card-list" data-date="${dateStr}">
              ${dateItems.length === 0 ? `
                <div class="empty-date-msg">Húzz ide kártyákat!</div>
              ` : dateItems.map(item => `
                <div class="itinerary-card" data-item-id="${item.id}" draggable="true">
                  <span class="drag-handle" title="Húzás az átrendezéshez">
                    <img src="assets/icons/drag-handle.svg" alt="" width="14" height="14" />
                  </span>
                  <img src="${(item.card && item.card.picture) ? item.card.picture : 'assets/icons/map-pin.svg'}" class="itinerary-thumb" alt="${item.card ? item.card.name : ''}" />
                  <div class="itinerary-info">
                    <div class="itinerary-title">${item.card ? item.card.name : ''}</div>
                    <div class="itinerary-meta">
                      <span class="category-tag-sm">${(item.card && item.card.category) ? item.card.category : 'Látványosság'}</span>
                      ${(item.card && item.card.duration) ? `<span class="duration-tag-sm">${item.card.duration}</span>` : ''}
                      ${(item.card && item.card.webpage) ? `
                        <a href="${item.card.webpage}" target="_blank" rel="noopener noreferrer" class="itinerary-link" title="Weboldal megnyitása" onclick="event.stopPropagation()">
                          link
                        </a>
                      ` : ''}
                    </div>
                  </div>
                  <button class="btn-icon-remove" data-action="remove" data-item-id="${item.id}" title="Eltávolítás">
                    <img src="assets/icons/trash.svg" alt="" width="14" height="14" />
                  </button>
                </div>
              `).join('')}
            </div>
          </div>
        `;
      }).join('')}
    </div>
  `;

  const switcher = container.querySelector('#trip-switcher-select');
  if (switcher) {
    switcher.addEventListener('change', (e) => {
      const selectedId = Number(e.target.value);
      store.setActiveTrip(selectedId);
      showToast('Utazás sikeresen kiválasztva.');
    });
  }

  const shortcutBtn = container.querySelector('#btn-new-trip-shortcut');
  if (shortcutBtn) {
    shortcutBtn.addEventListener('click', () => {
      openTripModal();
    });
  }

  const addDateBtn = container.querySelector('#btn-add-date');
  if (addDateBtn) {
    addDateBtn.addEventListener('click', () => {
      const dates = (trip.dates && Array.isArray(trip.dates)) ? trip.dates : [];
      let nextDateStr = '2026-10-28';
      if (dates.length > 0) {
        const lastDate = new Date(dates[dates.length - 1]);
        lastDate.setDate(lastDate.getDate() + 1);
        const y = lastDate.getFullYear();
        const m = String(lastDate.getMonth() + 1).padStart(2, '0');
        const d = String(lastDate.getDate()).padStart(2, '0');
        nextDateStr = `${y}-${m}-${d}`;
      }
      store.addTripDate(nextDateStr);
      showToast(`Új nap hozzáadva a tervhez: ${nextDateStr}`);
    });
  }

  container.querySelectorAll('[data-action="delete-last-day"]').forEach(btn => {
    btn.addEventListener('click', async (e) => {
      e.stopPropagation();
      const removed = await store.deleteLastTripDate();
      if (removed) {
        showToast(`Nap sikeresen törölve a tervből: ${removed}`);
      }
    });
  });

  container.querySelectorAll('[data-action="remove"]').forEach(btn => {
    btn.addEventListener('click', (e) => {
      e.stopPropagation();
      const itemId = Number(btn.dataset.itemId);
      store.removeTripItem(itemId);
      showToast('Program eltávolítva a tervből.');
    });
  });

  setupDragAndDrop('#itinerary-scroll-container', (reorderedItems) => {
    const currentItemsMap = new Map(store.getState().tripItems.map(it => [it.id, it]));
    const updatedFullList = [];

    reorderedItems.forEach(reordered => {
      const original = currentItemsMap.get(reordered.itemId);
      if (original) {
        updatedFullList.push({
          ...original,
          date: reordered.date,
          index: reordered.index
        });
      }
    });

    store.reorderTripItems(updatedFullList);
    showToast('A napirend sorrendje frissítve.');
  });
}
