import { store } from '../state/store.js';
import { showToast, formatDateHungarian } from '../utils/domHelpers.js';

export function updateAllPlaceCardDateDropdowns(availableDates = []) {
  const selects = document.querySelectorAll('.date-select-dropdown');
  selects.forEach(select => {
    const prevVal = select.value;
    const parentCard = select.closest('.place-card');
    const addBtn = parentCard ? parentCard.querySelector('.btn-add-trip') : null;

    if (!availableDates || availableDates.length === 0) {
      select.innerHTML = '<option value="">Nincs elérhető nap</option>';
      select.disabled = true;
      if (addBtn) addBtn.disabled = true;
      return;
    }

    select.disabled = false;
    if (addBtn) addBtn.disabled = false;

    const optionsHtml = availableDates.map((d, i) => `
      <option value="${d}">${formatDateHungarian(d, i)}</option>
    `).join('');
    select.innerHTML = optionsHtml;

    if (availableDates.includes(prevVal)) {
      select.value = prevVal;
    } else {
      select.value = availableDates[0];
    }
  });
}

export function renderPlaceCard(place, availableDates = []) {
  const cardElement = document.createElement('div');
  cardElement.className = 'place-card';
  cardElement.dataset.cardId = place.id;

  const hasDates = availableDates && availableDates.length > 0;
  const dateOptionsHtml = hasDates
    ? availableDates.map((d, i) => `<option value="${d}">${formatDateHungarian(d, i)}</option>`).join('')
    : '<option value="">Nincs elérhető nap</option>';

  const webpageHtml = place.webpage ? `
    <div class="place-card-link-row">
      <a href="${place.webpage}" target="_blank" rel="noopener noreferrer" class="place-link-btn" title="Hivatalos weboldal megnyitása">
        <span>Weboldal</span>
        <img src="assets/icons/chevron-down.svg" alt="" width="12" height="12" style="transform: rotate(-90deg);" />
      </a>
    </div>
  ` : '';

  const durationHtml = place.duration ? `
    <div class="place-card-duration">
      <img src="assets/icons/calendar.svg" alt="" width="13" height="13" />
      <span>Javasolt időtartam: <strong>${place.duration}</strong></span>
    </div>
  ` : '';

  cardElement.innerHTML = `
    <div class="place-card-image-wrap">
      <img src="${place.picture}" alt="${place.name}" class="place-card-image" loading="lazy" />
      <span class="place-badge-category">${place.category}</span>
      <span class="place-badge-city">${place.destination}</span>
    </div>
    <div class="place-card-body">
      <h3 class="place-card-title">${place.name}</h3>
      <div class="place-card-location">
        <img src="assets/icons/map-pin.svg" alt="" width="14" height="14" />
        <span>${place.location}</span>
      </div>
      ${durationHtml}
      <p class="place-card-desc">${place.description}</p>
      ${webpageHtml}
      <div class="place-card-actions">
        <select class="date-select-dropdown" aria-label="Nap kiválasztása" ${hasDates ? '' : 'disabled'}>
          ${dateOptionsHtml}
        </select>
        <button class="btn-add-trip" type="button" ${hasDates ? '' : 'disabled'}>
          <img src="assets/icons/plus.svg" alt="" width="14" height="14" />
        </button>
      </div>
    </div>
  `;

  const addBtn = cardElement.querySelector('.btn-add-trip');
  const dateSelect = cardElement.querySelector('.date-select-dropdown');

  addBtn.addEventListener('click', async () => {
    const selectedDate = dateSelect.value;
    if (!selectedDate) {
      showToast('Kérjük, válassz ki egy érvényes napot a hozzáadáshoz!');
      return;
    }

    addBtn.disabled = true;
    addBtn.classList.add('added');
    addBtn.innerHTML = `
      <img src="assets/icons/check.svg" alt="" width="14" height="14" />
    `;

    await store.addCardToTrip(place, selectedDate);
    showToast(`"${place.name}" hozzáadva a kiválasztott naphoz!`);

    setTimeout(() => {
      addBtn.disabled = false;
      addBtn.classList.remove('added');
      addBtn.innerHTML = `
        <img src="assets/icons/plus.svg" alt="" width="14" height="14" />
      `;
    }, 1800);
  });

  return cardElement;
}
