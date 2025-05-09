// Preload user's favorite recipe IDs
async function preloadFavoritesAndRender(data, grid, baseUrl) {
  // Fetch user's favorite recipes
  const favResp = await fetch(`${window.location.origin}/api/recipes/me/favorites`, {
    headers: { Authorization: `Bearer ${localStorage.getItem('token')}` }
  });
  const favList = await favResp.json();
  const favIds = new Set(favList.map(r => String(r.id)));

  data.forEach(r => {
    const card = document.createElement("div");
    card.className = "card";
    card.innerHTML = `
      <img src="${r.image}" alt="${r.title}" />
      <h3>${r.title}</h3>
      <p>${r.description}</p>
      <button class="icon-circle delete-icon"><i class="fa fa-trash-o"></i></button>
    `;
    // Append favorite icon button
    card.innerHTML += `
      <button class="favorite-icon" data-id="${r.id}">
        <i class="fa fa-heart"></i>
      </button>
    `;

    const deleteBtn = card.querySelector(".delete-icon");
    deleteBtn.addEventListener("click", async (e) => {
      e.stopPropagation();
      const confirmDelete = confirm("Are you sure you want to delete this recipe?");
      if (!confirmDelete) return;
      try {
        const resp = await fetch(`${baseUrl}/api/recipes/${r.id}`, {
          method: "DELETE",
          headers: {
            Authorization: `Bearer ${localStorage.getItem("token")}`
          }
        });
        if (resp.ok) {
          card.remove();
        } else {
          alert("Failed to delete recipe.");
        }
      } catch (err) {
        console.error("Delete failed:", err);
        alert("An error occurred while deleting.");
      }
    });

    grid.appendChild(card);

    // Attach favorite icon event listener
    const favoriteBtn = card.querySelector('.favorite-icon');
    // Preload favorite state and red color
    if (favIds.has(String(r.id))) {
      favoriteBtn.classList.add('favorited');
      favoriteBtn.querySelector('i').style.color = 'red';
    }
    favoriteBtn.addEventListener('click', async function (e) {
      e.stopPropagation();
      const isFav = favoriteBtn.classList.contains('favorited');
      const resp = await fetch(
        `${window.location.origin}/api/users/me/favorites/${r.id}`,
        {
          method: isFav ? 'DELETE' : 'POST',
          headers: { Authorization: `Bearer ${localStorage.getItem('token')}` }
        }
      );
      if (!resp.ok) {
        console.error('Favorite toggle failed', resp.status);
        return;
      }
      favoriteBtn.classList.toggle('favorited');
      // Change heart color based on state
      const heartIcon = favoriteBtn.querySelector('i');
      if (favoriteBtn.classList.contains('favorited')) {
        heartIcon.style.color = 'red';
      } else {
        heartIcon.style.color = '';
      }
    });
  });
}