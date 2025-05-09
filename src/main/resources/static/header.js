document.addEventListener('DOMContentLoaded', () => {
  const baseUrl = window.location.origin;
  const usernameSpan = document.getElementById('username-display');
  const userId = localStorage.getItem("userId");

  if (!usernameSpan) return;

  if (userId) {
    fetch(`${baseUrl}/api/users/me`, {
      headers: { Authorization: `Bearer ${localStorage.getItem("token")}` }
    })
      .then(res => {
        if (!res.ok) throw new Error('User fetch failed');
        return res.json();
      })
      .then(user => {
          usernameSpan.innerHTML = `<span><i class="fa-solid fa-user"></i></span> ${user.username} <span class="dropdown-arrow"><i class="fa fa-caret-down"></i></span>`;
          const userMenu = document.getElementById('user-menu');
          usernameSpan.addEventListener('click', () => {
            userMenu.classList.toggle('hidden');
          });

          const logoutBtn = document.getElementById('logout-btn');
          logoutBtn.addEventListener('click', () => {
            localStorage.removeItem('token');
            localStorage.removeItem('userId');
            window.location.href = 'index.html';
          });

          localStorage.setItem("username", user.username);
          localStorage.setItem("userId", user.id);
      })
      .catch(err => {
        console.error('Failed to load username:', err);
        usernameSpan.textContent = "👤 User";
      });
  } else {
    usernameSpan.textContent = "👤 Guest";
  }
});