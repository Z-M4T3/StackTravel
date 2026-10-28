export function qs(selector, parent = document) {
  return parent.querySelector(selector);
}

export function qsa(selector, parent = document) {
  return Array.from(parent.querySelectorAll(selector));
}

export function createElement(tag, attributes = {}, children = []) {
  const el = document.createElement(tag);

  for (const [key, val] of Object.entries(attributes)) {
    if (key === 'className') {
      el.className = val;
    } else if (key === 'innerHTML') {
      el.innerHTML = val;
    } else if (key === 'textContent') {
      el.textContent = val;
    } else if (key.startsWith('on') && typeof val === 'function') {
      el.addEventListener(key.substring(2).toLowerCase(), val);
    } else if (key.startsWith('data-')) {
      el.setAttribute(key, val);
    } else {
      el.setAttribute(key, val);
    }
  }

  for (const child of children) {
    if (typeof child === 'string') {
      el.appendChild(document.createTextNode(child));
    } else if (child instanceof Node) {
      el.appendChild(child);
    }
  }

  return el;
}

export function showToast(message, duration = 3000) {
  let toast = qs('#toast-notice');
  if (!toast) {
    toast = createElement('div', { id: 'toast-notice', className: 'toast-notice' });
    document.body.appendChild(toast);
  }

  toast.textContent = message;
  toast.classList.add('show');

  if (window._toastTimeout) {
    clearTimeout(window._toastTimeout);
  }

  window._toastTimeout = setTimeout(() => {
    toast.classList.remove('show');
  }, duration);
}

export function formatDateHungarian(dateStr, index = 0) {
  const parts = dateStr.split('-');
  if (parts.length !== 3) return dateStr;
  const year = parts[0];
  const month = parseInt(parts[1], 10);
  const day = parseInt(parts[2], 10);

  const monthNames = [
    'január', 'február', 'március', 'április', 'május', 'június',
    'július', 'augusztus', 'szeptember', 'október', 'november', 'december'
  ];

  const mName = monthNames[month - 1] || `${month}.`;
  return `${year}. ${mName} ${day}. (${index + 1}. nap)`;
}
