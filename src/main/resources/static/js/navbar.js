// AquaShine - Dynamic Navbar

(async function renderNavbar() {
    const path = window.location.pathname;
    const isActive = (p) => path.includes(p) ? 'active' : '';

    const brand = '<a href="/dashboard.html" class="navbar-brand"><span class="logo-mark">💧</span>AquaShine</a>';
    let links = '';

    try {
        const res = await fetch('/api/auth/me', { credentials: 'include' });
        if (res.ok) {
            const user = await res.json();
            const shortEmail = user.email.length > 22 ? user.email.slice(0, 19) + '…' : user.email;
            links = `
                <a href="/dashboard.html" class="${isActive('dashboard')}">Dashboard</a>
                <a href="/auth/services.html" class="${isActive('services')}">Services</a>
                <a href="/auth/vehicles.html" class="${isActive('vehicles')}">Vehicles</a>
                <a href="/auth/bookings.html" class="${isActive('bookings')}">Bookings</a>
                <a href="/auth/profile.html" class="${isActive('profile')}">Profile</a>
                <span class="navbar-user">${shortEmail}</span>
                <a href="#" id="navLogout">Logout</a>
            `;
        } else {
            links = `
                <a href="/auth/login.html" class="${isActive('login')}">Login</a>
                <a href="/auth/register.html" class="btn btn-primary btn-sm">Get Started</a>
            `;
        }
    } catch {
        links = `
            <a href="/auth/login.html">Login</a>
            <a href="/auth/register.html" class="btn btn-primary btn-sm">Get Started</a>
        `;
    }

    const nav = document.getElementById('navbar');
    if (nav) {
        nav.className = 'navbar';
        nav.innerHTML = brand + `<div class="navbar-links">${links}</div>`;
        const logoutBtn = document.getElementById('navLogout');
        if (logoutBtn) {
            logoutBtn.addEventListener('click', async (e) => {
                e.preventDefault();
                await fetch('/api/auth/logout', { method: 'POST', credentials: 'include' });
                window.location.href = '/';
            });
        }
    }
})();