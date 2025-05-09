document.addEventListener("DOMContentLoaded", async () => {
  const params = new URLSearchParams(window.location.search);
  const id = params.get("id");
  if (!id) return;

  try {
    const resp = await fetch(`${window.location.origin}/api/recipes/${id}`, {
      headers: { Authorization: `Bearer ${localStorage.getItem("token")}` }
    });

    if (!resp.ok) {
      document.getElementById("detail-name").textContent = "Recipe not found.";
      return;
    }
    const recipe = await resp.json();


    document.getElementById("detail-name").textContent = recipe.name;
    document.getElementById("detail-image").src = recipe.imageUrl;
    document.getElementById("detail-category").textContent = recipe.category;
    document.getElementById("detail-time").textContent = recipe.cookingTime;
    document.getElementById("detail-instructions").textContent = recipe.instructions;

    // Format created date as DD-MM-YYYY
    const dateStr = recipe.createdAt?.split("-").reverse().join("-") || "Unknown";
    document.getElementById("detail-date").textContent = dateStr;

    document.getElementById("detail-owner").textContent =
      recipe.ownerUsername?.trim() || "Unknown";

    const ul = document.getElementById("detail-ingredients");
    recipe.ingredients.forEach(i => {
      const li = document.createElement("li");
      li.textContent = i;
      ul.appendChild(li);
    });

  } catch (err) {
    console.error("Failed to load recipe details", err);
  }
});