import { store } from '../state/store.js';
import { CONFIG } from '../config.js';

export function renderSearchBar(container, onGenerateCallback) {
  const currentFilters = store.getState().filters;
  const isGenerating = currentFilters.isGenerating;

  container.innerHTML = `
    <div class="search-form-row">
      <div class="search-input-wrapper">
        <img src="assets/icons/search.svg" class="search-icon" alt="" width="18" height="18" />
        <input 
          type="text" 
          id="destination-search-input" 
          class="search-input" 
          placeholder="Írd be a hely nevét (város, ország, pl. Róma, Párizs, Tokió, Budapest)..." 
          value="${currentFilters.destination || ''}"
          ${isGenerating ? 'disabled' : ''}
        />
        ${currentFilters.destination ? `
          <button id="search-clear-btn" class="search-clear-btn" type="button" title="Törlés">
            <img src="assets/icons/close.svg" alt="" width="14" height="14" />
          </button>
        ` : ''}
      </div>
      <button id="btn-generate-cards" class="btn-primary btn-generate" type="button" ${isGenerating ? 'disabled' : ''}>
        <img src="assets/icons/search.svg" alt="" width="16" height="16" style="filter: brightness(0) invert(1);" />
        <span>${isGenerating ? 'Generálás...' : 'Kártyák keresése'}</span>
      </button>
    </div>

    <div class="filter-row" id="category-filter-row">
      ${CONFIG.CATEGORIES.map(cat => `
        <button 
          type="button" 
          class="filter-pill ${currentFilters.category === cat ? 'active' : ''}" 
          data-category="${cat}"
          ${isGenerating ? 'disabled' : ''}>
          ${getCategoryIconHtml(cat)}
          <span>${cat}</span>
        </button>
      `).join('')}
    </div>

  `;

  const input = container.querySelector('#destination-search-input');
  const generateBtn = container.querySelector('#btn-generate-cards');
  const clearBtn = container.querySelector('#search-clear-btn');

  function triggerGeneration(destOverride, catOverride) {
    const dest = destOverride !== undefined ? destOverride : (input ? input.value : '');
    const cat = catOverride !== undefined ? catOverride : store.getState().filters.category;
    store.generateCards(dest, cat);
    if (typeof onGenerateCallback === 'function') {
      onGenerateCallback();
    }
  }

  if (input) {
    input.addEventListener('keydown', (e) => {
      if (e.key === 'Enter') {
        e.preventDefault();
        triggerGeneration();
      }
    });

    input.addEventListener('input', (e) => {
      store.setFilters({ destination: e.target.value });
    });
  }

  if (generateBtn) {
    generateBtn.addEventListener('click', () => {
      triggerGeneration();
    });
  }

  if (clearBtn && input) {
    clearBtn.addEventListener('click', () => {
      input.value = '';
      store.setFilters({ destination: '' });
      input.focus();
    });
  }

  container.querySelectorAll('.filter-pill').forEach(pill => {
    pill.addEventListener('click', () => {
      const selectedCategory = pill.dataset.category;
      const currentCategory = store.getState().filters.category;
      const newCategory = (currentCategory === selectedCategory && selectedCategory !== 'Összes') ? 'Összes' : selectedCategory;

      container.querySelectorAll('.filter-pill').forEach(p => p.classList.remove('active'));
      const activePill = container.querySelector(`.filter-pill[data-category="${newCategory}"]`);
      if (activePill) {
        activePill.classList.add('active');
      }

      store.setFilters({ category: newCategory });
      triggerGeneration(undefined, newCategory);
    });
  });

  container.querySelectorAll('.city-tag').forEach(tag => {
    tag.addEventListener('click', () => {
      const selectedDest = tag.dataset.destination;
      if (input) {
        input.value = selectedDest;
      }
      container.querySelectorAll('.city-tag').forEach(t => t.classList.remove('active'));
      tag.classList.add('active');
      store.setFilters({ destination: selectedDest });
      triggerGeneration(selectedDest);
    });
  });
}

function getCategoryIconHtml(category) {
  let iconName = 'map-pin.svg';
  if (category === 'Múzeum') iconName = 'museum.svg';
  else if (category === 'Szórakozóhely') iconName = 'nightlife.svg';
  else if (category === 'Étterem') iconName = 'restaurant.svg';
  else if (category === 'Park' || category === 'Természet') iconName = 'park.svg';
  else if (category === 'Kultúra') iconName = 'calendar.svg';
  else if (category === 'Strand') iconName = 'map-pin.svg';
  return `<img src="assets/icons/${iconName}" alt="" width="14" height="14" />`;
}
