// feed.js
document.addEventListener('DOMContentLoaded', () => {
    const baseUrl = window.location.origin;
    const grid = document.getElementById('recipe-grid');
    const searchInput = document.getElementById('search-input');

    const favoritesBtn = document.getElementById('favorites-btn');
    const prevBtn = document.getElementById('prev-btn');
    const nextBtn = document.getElementById('next-btn');
    const categoryFilter = document.getElementById('category-filter');
    const sortFilter = document.getElementById('sort-filter');
    const favoritesFilterBtn = document.getElementById('favorites-filter');
    let showingFavoritesOnly = false;

    favoritesFilterBtn.addEventListener('click', () => {
      showingFavoritesOnly = !showingFavoritesOnly;
      favoritesFilterBtn.classList.toggle('active');
      loadRecipes();
    });

    let currentPage = 0;
    const pageSize = 9;
    let currentQuery = '';

    // Fetch & render recipes with client-side filtering
    async function loadRecipes() {
        // Determine if filtering or searching is active
        const isFiltering = currentQuery || categoryFilter.value || showingFavoritesOnly;
        const fetchUrl = isFiltering
          ? `${baseUrl}/api/recipes?page=0&size=1000`
          : `${baseUrl}/api/recipes?page=${currentPage}&size=${pageSize}`;

        const resp = await fetch(fetchUrl, {
            headers: { Authorization: `Bearer ${localStorage.getItem('token')}` }
        });
        if (!resp.ok) {
            console.error('Failed fetching recipes', resp.status);
            return;
        }
        const all = (await resp.json()).content;
        // Filter locally by name query and category
        let filtered = all.filter(r => {
            const matchesQuery = currentQuery === '' || r.name.toLowerCase().includes(currentQuery.toLowerCase());
            const matchesCategory = categoryFilter.value === '' || r.category === categoryFilter.value;
            return matchesQuery && matchesCategory;
        });
        if (showingFavoritesOnly) {
          const favResp = await fetch(`${baseUrl}/api/recipes/me/favorites`, {
              headers: { Authorization: `Bearer ${localStorage.getItem('token')}` }
          });
          if (!favResp.ok) {
              console.error('Failed fetching user favorites', favResp.status);
              return;
          }
          const favList = await favResp.json();
          const favIds = new Set(favList.map(r => String(r.id)));
          filtered = filtered.filter(r => favIds.has(String(r.id)));
        }
        // Sort filtered results
        const sorted = [...filtered];
        switch (sortFilter.value) {
            case 'time-asc':
                sorted.sort((a, b) => a.cookingTime - b.cookingTime);
                break;
            case 'time-desc':
                sorted.sort((a, b) => b.cookingTime - a.cookingTime);
                break;
            case 'name-asc':
                sorted.sort((a, b) => a.name.localeCompare(b.name));
                break;
            case 'name-desc':
                sorted.sort((a, b) => b.name.localeCompare(a.name));
                break;
        }
        renderGrid(sorted);
        // Step 3: Mark user favorites after rendering
        await markUserFavorites();

        const paginationWrapper = document.querySelector('.pagination-wrapper');
        if (paginationWrapper) {
          paginationWrapper.style.display = isFiltering ? 'none' : 'flex';
        }

        if (!isFiltering) {
          prevBtn.disabled = currentPage === 0;
          nextBtn.disabled = all.length < pageSize;
        } else {
          prevBtn.disabled = true;
          nextBtn.disabled = true;
        }
    }

    // Render helper
    function renderGrid(recipes) {
        grid.innerHTML = '';
        if (recipes.length === 0) {
            grid.innerHTML = '<p class="no-recipes">No Recipes Found</p>';
            return;
        }
        recipes.forEach(r => {
            const card = document.createElement('div');
            card.className = 'recipe-card';
            card.innerHTML = `
        <img src="${r.imageUrl}" alt="${r.name}">
        <div class="recipe-content">
          <h3 class="recipe-title" data-id="${r.id}">${r.name}</h3>
          <p>⏱ ${r.cookingTime} min</p>
          <p>📂 ${r.category}</p>
        </div>
        <button class="favorite-icon" data-id="${String(r.id)}">
          <i class="fa fa-heart"></i>
        </button>
      `;
            grid.appendChild(card);

            // Add click listener to the entire card (except favorite icon)
            card.addEventListener('click', (e) => {
                // Prevent click from toggling favorite icon
                if (e.target.closest('.favorite-icon')) return;
                window.location.href = `recipe.html?id=${r.id}`;
            });

            card.querySelector('.favorite-icon').addEventListener('click', async function() {
                await toggleFavorite(r.id);
            });
        });
    }

    // Toggle favorite using /me endpoint and POST/DELETE
    async function toggleFavorite(recipeId) {
        const btn = document.querySelector(`button.favorite-icon[data-id="${recipeId}"]`);
        const isFav = btn.classList.contains('favorited');
        const resp = await fetch(
            `${window.location.origin}/api/users/me/favorites/${recipeId}`,
            {
                method: isFav ? 'DELETE' : 'POST',
                headers: { Authorization: `Bearer ${localStorage.getItem('token')}` }
            }
        );
        if (!resp.ok) {
            console.error('Favorite toggle failed', resp.status);
            return;
        }
        // Toggle the heart state
        btn.classList.toggle('favorited');
    }

    // Preload and render user favorites
    async function markUserFavorites() {
        const resp = await fetch(`${window.location.origin}/api/recipes/me/favorites`, {
            headers: { Authorization: `Bearer ${localStorage.getItem('token')}` }
        });
        if (!resp.ok) {
            console.error('Failed fetching user favorites', resp.status);
            return;
        }
        const favList = await resp.json();
        const favIds = new Set(favList.map(r => String(r.id)));

        document.querySelectorAll('.favorite-icon').forEach(btn => {
            const recipeId = btn.dataset.id;
            if (favIds.has(recipeId)) {
                btn.classList.add('favorited');
            } else {
                btn.classList.remove('favorited');
            }
        });

    }

    // Search on input
    searchInput.addEventListener('input', () => {
        currentQuery = searchInput.value.trim();
        currentPage = 0;
        if (!prevBtn || !nextBtn) return;
        loadRecipes();
    });

    // Add event listeners for new filters
    categoryFilter.addEventListener('change', () => {
      if (!prevBtn || !nextBtn) return;
      loadRecipes();
    });
    sortFilter.addEventListener('change', () => {
      if (!prevBtn || !nextBtn) return;
      loadRecipes();
    });

    // Pagination
    prevBtn.addEventListener('click', () => {
        if (currentPage > 0) {
            currentPage--;
            if (!prevBtn || !nextBtn) return;
            loadRecipes();
        }
    });
    nextBtn.addEventListener('click', () => {
        currentPage++;
        if (!prevBtn || !nextBtn) return;
        loadRecipes();
    });

    // Go to favorites page
    // (Removed favoritesBtn functionality since button is replaced by username display)

    // Initial load or redirect
    if (!localStorage.getItem('token')) {
        window.location.href = 'index.html';
    } else {
        if (!prevBtn || !nextBtn) return;
        loadRecipes();
    }

    // Post Recipe Modal Logic
    const modal = document.getElementById('post-recipe-modal');
    const postBtn = document.getElementById('post-recipe-btn');
    const closeBtn = document.getElementById('close-post-modal');
    closeBtn.addEventListener('click', () => {
      modal.classList.add('hidden');
    });
    const form = document.getElementById('post-recipe-form');

    postBtn.addEventListener('click', () => {
      modal.classList.remove('hidden');
    });

    form.addEventListener('submit', async (e) => {
      e.preventDefault();

      const formData = new FormData(form);
      const body = {
        name: formData.get("name"),
        ingredients: formData.get("ingredients").split(',').map(i => i.trim()),
        instructions: formData.get("instructions"),
        cookingTime: parseInt(formData.get("cookingTime")),
        category: formData.get("category"),
        imageUrl: formData.get("imageUrl")
      };

      try {
        const resp = await fetch(`${window.location.origin}/api/recipes`, {
          method: "POST",
          headers: {
            "Content-Type": "application/json",
            "Authorization": `Bearer ${localStorage.getItem("token")}`
          },
          body: JSON.stringify(body)
        });

        if (!resp.ok) {
          throw new Error(`Failed to post recipe: ${resp.status}`);
        }

        modal.classList.add("hidden");
        form.reset();
        loadRecipes();
      } catch (err) {
        console.error("Error posting recipe:", err);
      }
    });
});