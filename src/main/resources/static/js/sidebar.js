// AquaShine - Sidebar component
// Include on authenticated pages: <script src="/js/sidebar.js"></script>

(function renderSidebar() {
    const path = window.location.pathname;
    const active = (p) => path.includes(p) ? 'active' : '';

    const sidebar = document.getElementById('sidebar');
    if (!sidebar) return;

    sidebar.className = 'sidebar';
    sidebar.innerHTML = `
        <div class="sidebar-section">
            <div class="sidebar-heading">Main</div>
            <a href="/dashboard.html" class="sidebar-link ${active('dashboard')}">
                <span class="icon">🏠</span> Dashboard
            </a>
            <a href="/auth/services.html" class="sidebar-link ${active('services')}">
                <span class="icon">💧</span> Services
            </a>
            <a href="/auth/book.html" class="sidebar-link ${active('book')}">
                <span class="icon">📅</span> Book a Wash
            </a>
        </div>

        <div class="sidebar-section">
            <div class="sidebar-heading">My Account</div>
            <a href="/auth/vehicles.html" class="sidebar-link ${active('vehicles')}">
                <span class="icon">🚗</span> My Vehicles
            </a>
            <a href="/auth/bookings.html" class="sidebar-link ${active('bookings')}">
                <span class="icon">📋</span> My Bookings
            </a>
            <a href="/auth/profile.html" class="sidebar-link ${active('profile')}">
                <span class="icon">👤</span> Profile
            </a>
        </div>

        <div class="sidebar-section">
            <div class="sidebar-heading">Support</div>
            <a href="#" class="sidebar-link" id="sidebarLogout">
                <span class="icon">🚪</span> Logout
            </a>
        </div>
    `;

    const logout = document.getElementById('sidebarLogout');
    if (logout) {
        logout.addEventListener('click', async (e) => {
            e.preventDefault();
            await fetch('/api/auth/logout', { method: 'POST', credentials: 'include' });
            window.location.href = '/';
        });
    }

    // Mobile toggle
    const nav = document.getElementById('navbar');
    if (nav && !document.getElementById('mobileMenuBtn')) {
        const btn = document.createElement('button');
        btn.id = 'mobileMenuBtn';
        btn.innerHTML = '☰';
        btn.style.cssText = 'background:none;border:none;font-size:1.3rem;cursor:pointer;display:none;color:#1e293b;';
        btn.addEventListener('click', () => sidebar.classList.toggle('open'));
        nav.insertBefore(btn, nav.firstChild?.nextSibling);
        // Show on small screens only
        if (window.innerWidth <= 1024) btn.style.display = 'block';
        window.addEventListener('resize', () => {
            btn.style.display = window.innerWidth <= 1024 ? 'block' : 'none';
        });
    }
})();