import { store } from '../state/store.js';
import { localCache } from '../state/localCache.js';
import { showToast, formatDateHungarian } from '../utils/domHelpers.js';

export function renderTripsView(container) {
  const state = store.getState();
  const trips = state.trips;
  const activeTrip = state.activeTrip;

  container.innerHTML = `
    <div class="trips-view-container">
      <div class="trips-view-header">
        <div>
          <h2 class="trips-view-title">Saját Utazások</h2>
        </div>
        <button id="btn-create-trip" class="btn-primary" type="button">
          <img src="assets/icons/plus.svg" alt="" width="16" height="16" style="filter: brightness(0) invert(1);" />
          <span>Új utazás létrehozása</span>
        </button>
      </div>

      ${trips.length === 0 ? `
        <div class="trips-empty-card">
          <img src="assets/icons/calendar.svg" alt="" width="48" height="48" style="opacity: 0.4; margin-bottom: 16px;" />
          <h3 style="font-size: 18px; font-weight: 700; color: var(--color-primary); margin-bottom: 8px;">Még nincs létrehozott utazásod</h3>
          <button id="btn-create-trip-empty" class="btn-primary" type="button">
            <img src="assets/icons/plus.svg" alt="" width="16" height="16" style="filter: brightness(0) invert(1);" />
            <span>Első utazás létrehozása</span>
          </button>
        </div>
      ` : `
        <div class="trips-cards-grid">
          ${trips.map(trip => {
            const isActive = activeTrip && activeTrip.id === trip.id;
            const tripItems = localCache.getTripItems(trip.id);
            const dayCount = (trip.dates && Array.isArray(trip.dates)) ? trip.dates.length : 1;
            return `
              <div class="trip-manage-card ${isActive ? 'active-trip-card' : ''}" data-trip-id="${trip.id}">
                <div class="trip-manage-card-header">
                  <div>
                    <h3 class="trip-manage-card-title">${trip.title}</h3>
                    <span class="trip-manage-destination">${trip.destination}</span>
                  </div>
                </div>

                <div class="trip-manage-card-body">
                  <div class="trip-meta-row">
                    <img src="assets/icons/calendar.svg" alt="" width="14" height="14" />
                    <span>${trip.startDate}${trip.endDate && trip.endDate !== trip.startDate ? ` - ${trip.endDate}` : ''} (${dayCount} nap)</span>
                  </div>
                  <div class="trip-meta-row">
                    <img src="assets/icons/map-pin.svg" alt="" width="14" height="14" />
                    <span>${tripItems.length} hozzáadott program</span>
                  </div>
                </div>

                <div class="trip-manage-card-actions" id="actions-trip-${trip.id}">
                  <button class="btn-primary btn-open-trip" data-action="open" data-trip-id="${trip.id}" type="button">
                    <span>Megnyitás a tervezőben</span>
                  </button>
                  <button class="btn-secondary btn-edit-trip" data-action="edit" data-trip-id="${trip.id}" type="button">
                    <span>Szerkesztés</span>
                  </button>
                  <button class="btn-secondary btn-delete-trip" data-action="delete-step-1" data-trip-id="${trip.id}" type="button" style="color: var(--color-danger);">
                    <span>Törlés</span>
                  </button>
                </div>

                <div class="trip-delete-confirm-box" id="confirm-trip-${trip.id}" style="display: none;">
                  <div class="delete-confirm-text">Biztosan törölni szeretnéd ezt az utazást?</div>
                  <div class="delete-confirm-actions">
                    <button class="btn-secondary btn-cancel-delete" data-trip-id="${trip.id}" type="button">Mégse</button>
                    <button class="btn-primary btn-confirm-delete" data-trip-id="${trip.id}" type="button" style="background-color: var(--color-danger);">Végleges törlés</button>
                  </div>
                </div>
              </div>
            `;
          }).join('')}
        </div>
      `}
    </div>
  `;

  const createBtn = container.querySelector('#btn-create-trip');
  if (createBtn) {
    createBtn.addEventListener('click', () => openTripModal());
  }

  const createEmptyBtn = container.querySelector('#btn-create-trip-empty');
  if (createEmptyBtn) {
    createEmptyBtn.addEventListener('click', () => openTripModal());
  }

  container.querySelectorAll('[data-action="open"]').forEach(btn => {
    btn.addEventListener('click', () => {
      const tripId = Number(btn.dataset.tripId);
      store.setActiveTrip(tripId);
      showToast('Utazás megnyitva a tervezőben.');
    });
  });

  container.querySelectorAll('[data-action="edit"]').forEach(btn => {
    btn.addEventListener('click', () => {
      const tripId = Number(btn.dataset.tripId);
      const tripToEdit = trips.find(t => t.id === tripId);
      if (tripToEdit) {
        openTripModal(tripToEdit);
      }
    });
  });

  container.querySelectorAll('[data-action="delete-step-1"]').forEach(btn => {
    btn.addEventListener('click', () => {
      const tripId = btn.dataset.tripId;
      const actionsEl = container.querySelector(`#actions-trip-${tripId}`);
      const confirmEl = container.querySelector(`#confirm-trip-${tripId}`);
      if (actionsEl && confirmEl) {
        actionsEl.style.display = 'none';
        confirmEl.style.display = 'block';
      }
    });
  });

  container.querySelectorAll('.btn-cancel-delete').forEach(btn => {
    btn.addEventListener('click', () => {
      const tripId = btn.dataset.tripId;
      const actionsEl = container.querySelector(`#actions-trip-${tripId}`);
      const confirmEl = container.querySelector(`#confirm-trip-${tripId}`);
      if (actionsEl && confirmEl) {
        confirmEl.style.display = 'none';
        actionsEl.style.display = 'flex';
      }
    });
  });

  container.querySelectorAll('.btn-confirm-delete').forEach(btn => {
    btn.addEventListener('click', () => {
      const tripId = Number(btn.dataset.tripId);
      const tripObj = trips.find(t => t.id === tripId);
      store.deleteTrip(tripId);
      showToast(`"${tripObj?.title || 'Utazás'}" véglegesen törölve.`);
      renderTripsView(container);
    });
  });
}

export function openTripModal(tripToEdit = null) {
  let modalOverlay = document.querySelector('#trip-modal-overlay');
  if (!modalOverlay) {
    modalOverlay = document.createElement('div');
    modalOverlay.id = 'trip-modal-overlay';
    modalOverlay.className = 'modal-overlay';
    document.body.appendChild(modalOverlay);
  }

  const isEditing = !!tripToEdit;
  const today = new Date().toISOString().split('T')[0];

  modalOverlay.innerHTML = `
    <div class="modal-box">
      <div class="modal-header">
        <h3 class="modal-title">${isEditing ? 'Utazás szerkesztése' : 'Új utazás létrehozása'}</h3>
        <button class="modal-close-btn" id="trip-modal-close" type="button">
          <img src="assets/icons/close.svg" alt="" width="16" height="16" />
        </button>
      </div>
      <form id="form-trip-modal">
        <div class="modal-body">
          <div class="form-group">
            <label class="form-label" for="trip-name">Utazás neve</label>
            <input class="form-input" id="trip-name" type="text" required placeholder="Utazás neve" value="${tripToEdit ? tripToEdit.title : ''}" />
          </div>
          <div class="form-group">
            <label class="form-label" for="trip-destination">Fő úti cél</label>
            <input class="form-input" id="trip-destination" type="text" placeholder="Város, ország" value="${tripToEdit ? tripToEdit.destination : ''}" />
          </div>
          <div class="form-group">
            <label class="form-label" for="trip-start-date">Kezdő dátum</label>
            <input class="form-input" id="trip-start-date" type="date" required value="${tripToEdit ? tripToEdit.startDate : today}" />
          </div>
          <div class="form-group">
            <label class="form-label" for="trip-end-date">Befejező dátum (opcionális)</label>
            <input class="form-input" id="trip-end-date" type="date" value="${tripToEdit && tripToEdit.endDate ? tripToEdit.endDate : ''}" />
            <div class="form-help">Ha megadod a befejező dátumot, a rendszer automatikusan legenerálja a köztes napokat.</div>
          </div>
        </div>
        <div class="modal-footer">
          <button class="btn-secondary" type="button" id="trip-modal-cancel">Mégse</button>
          <button class="btn-primary" type="submit">${isEditing ? 'Módosítások mentése' : 'Utazás létrehozása'}</button>
        </div>
      </form>
    </div>
  `;

  const closeBtn = modalOverlay.querySelector('#trip-modal-close');
  if (closeBtn) {
    closeBtn.addEventListener('click', () => modalOverlay.classList.remove('active'));
  }

  const cancelBtn = modalOverlay.querySelector('#trip-modal-cancel');
  if (cancelBtn) {
    cancelBtn.addEventListener('click', () => modalOverlay.classList.remove('active'));
  }

  const form = modalOverlay.querySelector('#form-trip-modal');
  form.addEventListener('submit', async (e) => {
    e.preventDefault();
    const title = form.querySelector('#trip-name').value;
    const destination = form.querySelector('#trip-destination').value || 'Egyéb';
    const startDate = form.querySelector('#trip-start-date').value;
    const endDate = form.querySelector('#trip-end-date').value;

    if (isEditing) {
      let dates = tripToEdit.dates ? [...tripToEdit.dates] : [tripToEdit.startDate || today];
      if (startDate !== tripToEdit.startDate || endDate !== tripToEdit.endDate) {
        dates = [];
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

      const updated = {
        ...tripToEdit,
        title: title.trim(),
        destination: destination.trim(),
        startDate,
        endDate: endDate || startDate,
        dates
      };

      await store.updateTrip(updated);
      showToast('Utazás sikeresen frissítve.');
    } else {
      const created = await store.createTrip({ title, destination, startDate, endDate });
      const tripTitle = (created && created.title) ? created.title : title.trim();
      showToast(`"${tripTitle}" sikeresen létrehozva! Kezdődhet a tervezés.`);
    }

    modalOverlay.classList.remove('active');
  });

  modalOverlay.classList.add('active');
}
