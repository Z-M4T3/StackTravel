export function setupDragAndDrop(containerSelector, onReorderCallback) {
  let draggedCard = null;

  function getDragAfterElement(container, y) {
    const draggableElements = [...container.querySelectorAll('.itinerary-card:not(.dragging)')];

    return draggableElements.reduce((closest, child) => {
      const box = child.getBoundingClientRect();
      const offset = y - box.top - box.height / 2;
      if (offset < 0 && offset > closest.offset) {
        return { offset, element: child };
      } else {
        return closest;
      }
    }, { offset: Number.NEGATIVE_INFINITY }).element;
  }

  function handleDragStart(e) {
    draggedCard = this;
    this.classList.add('dragging');
    e.dataTransfer.effectAllowed = 'move';
    e.dataTransfer.setData('text/plain', this.dataset.itemId);
  }

  function handleDragEnd() {
    if (!draggedCard) return;
    draggedCard.classList.remove('dragging');

    document.querySelectorAll('.date-card-list').forEach(list => {
      list.classList.remove('drag-over');
    });

    if (typeof onReorderCallback === 'function') {
      const updatedList = [];
      const dateContainers = document.querySelectorAll('.date-card-list');

      dateContainers.forEach(container => {
        const date = container.dataset.date;
        const cards = container.querySelectorAll('.itinerary-card');

        cards.forEach((cardEl, idx) => {
          const itemId = Number(cardEl.dataset.itemId);
          updatedList.push({
            itemId,
            date,
            index: idx
          });
        });
      });

      onReorderCallback(updatedList);
    }

    draggedCard = null;
  }

  function handleDragOver(e) {
    e.preventDefault();
    e.dataTransfer.dropEffect = 'move';
    this.classList.add('drag-over');

    if (!draggedCard) return;

    const afterElement = getDragAfterElement(this, e.clientY);
    if (!afterElement) {
      this.appendChild(draggedCard);
    } else {
      this.insertBefore(draggedCard, afterElement);
    }
  }

  function handleDragLeave(e) {
    if (!this.contains(e.relatedTarget)) {
      this.classList.remove('drag-over');
    }
  }

  function handleDrop(e) {
    e.preventDefault();
    this.classList.remove('drag-over');
  }

  const cards = document.querySelectorAll(`${containerSelector} .itinerary-card`);
  cards.forEach(card => {
    card.setAttribute('draggable', 'true');
    card.removeEventListener('dragstart', handleDragStart);
    card.removeEventListener('dragend', handleDragEnd);
    card.addEventListener('dragstart', handleDragStart);
    card.addEventListener('dragend', handleDragEnd);
  });

  const dropzones = document.querySelectorAll(`${containerSelector} .date-card-list`);
  dropzones.forEach(zone => {
    zone.removeEventListener('dragover', handleDragOver);
    zone.removeEventListener('dragleave', handleDragLeave);
    zone.removeEventListener('drop', handleDrop);
    zone.addEventListener('dragover', handleDragOver);
    zone.addEventListener('dragleave', handleDragLeave);
    zone.addEventListener('drop', handleDrop);
  });
}
