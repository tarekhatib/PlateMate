// feed.js

async function loadRecipes() {
    const baseUrl = localStorage.getItem('baseUrl') || '';
    const pageSize = 10;
    let currentPage = 1;
    let all = [];
    const prevBtn = document.getElementById('prev-btn');
    const nextBtn = document.getElementById('next-btn');
    const sortSelect = document.getElementById('sort-select');
    const recipeList = document.getElementById('recipe-list');

    if (!prevBtn || !nextBtn || !sortSelect || !recipeList) return;

    async function fetchRecipes() {
        let url = `${baseUrl}/api/recipes?page=${currentPage}&limit=${pageSize}`;
        const sort = sortSelect.value;

        switch (sort) {
            case 'newest':
                url += '&sort=newest';
                break;
            case 'popular':
                url += '&sort=popular';
                break;
            // Removed 'favorites' case as per instructions
            default:
                break;
        }

        const response = await fetch(url);
        const data = await response.json();
        all = data.recipes;
        renderRecipes(all);

        const isFiltering = sort !== '';
        if (!isFiltering) {
            prevBtn.disabled = currentPage === 0;
            nextBtn.disabled = all.length < pageSize;
        } else {
            prevBtn.disabled = true;
            nextBtn.disabled = true;
        }

        const paginationWrapper = document.querySelector('.pagination-wrapper');
        if (paginationWrapper) {
            paginationWrapper.style.display = isFiltering ? 'none' : 'flex';
        }
    }

    function renderRecipes(recipes) {
        recipeList.innerHTML = '';
        recipes.forEach(recipe => {
            const li = document.createElement('li');
            li.textContent = recipe.title;
            recipeList.appendChild(li);
        });
    }

    if (prevBtn) {
        prevBtn.addEventListener('click', () => {
            if (currentPage > 1) {
                currentPage--;
                fetchRecipes();
            }
        });
    }

    if (nextBtn) {
        nextBtn.addEventListener('click', () => {
            currentPage++;
            fetchRecipes();
        });
    }

    if (sortSelect) {
        sortSelect.addEventListener('change', () => {
            currentPage = 1;
            fetchRecipes();
        });
    }
}

document.addEventListener('DOMContentLoaded', () => {
    loadRecipes();
});

const loginForm = document.getElementById('login-form');
if (loginForm) {
    loginForm.addEventListener('submit', async (e) => {
        e.preventDefault();
        const username = document.getElementById('login-username').value;
        const password = document.getElementById('login-password').value;
        const errorMsg = document.getElementById('login-error');

        try {
            const resp = await fetch('/api/auth/login', {
                method: 'POST',
                headers: { 'Content-Type': 'application/json' },
                body: JSON.stringify({ username, password })
            });

            if (!resp.ok) {
                const msg = resp.status === 401 ? 'Invalid username or password' : 'Login failed';
                errorMsg.textContent = msg;
                errorMsg.classList.remove('hidden');
                return;
            }

            const { token, userId } = await resp.json();
            localStorage.setItem('token', token);
            localStorage.setItem('userId', userId);
            window.location.href = 'feed.html';
        } catch (err) {
            errorMsg.textContent = 'Something went wrong';
            errorMsg.classList.remove('hidden');
        }
    });
}

const registerForm = document.getElementById('register-form');
if (registerForm) {
    registerForm.addEventListener('submit', async (e) => {
        e.preventDefault();
        const username = document.getElementById('reg-username').value;
        const email = document.getElementById('reg-email').value;
        const password = document.getElementById('reg-password').value;

        try {
            const resp = await fetch('/api/auth/register', {
                method: 'POST',
                headers: { 'Content-Type': 'application/json' },
                body: JSON.stringify({ username, email, password })
            });

            if (!resp.ok) {
                alert('Failed to register. Username or email may already exist.');
                return;
            }

            document.getElementById('register-card').classList.add('hidden');
            document.getElementById('login-card').classList.remove('hidden');
        } catch (err) {
            alert('An error occurred during registration.');
        }
    });
}

const showRegister = document.getElementById('show-register');
const showLogin = document.getElementById('show-login');
const loginCard = document.getElementById('login-card');
const registerCard = document.getElementById('register-card');

if (showRegister && showLogin && loginCard && registerCard) {
    showRegister.addEventListener('click', (e) => {
        e.preventDefault();
        loginCard.classList.add('hidden');
        registerCard.classList.remove('hidden');
    });

    showLogin.addEventListener('click', (e) => {
        e.preventDefault();
        registerCard.classList.add('hidden');
        loginCard.classList.remove('hidden');
    });
}