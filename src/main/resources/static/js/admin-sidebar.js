// AquaShine - Admin Sidebar (dark navy theme)

(function renderAdminSidebar() {
    const path = window.location.pathname;
    const active = (p) => path.includes(p) ? 'active' : '';

    const sidebar = document.getElementById('sidebar');
    if (!sidebar) return;

    sidebar.style.background = 'linear-gradient(180deg, #0a1128 0%, #101b3d 100%)';
    sidebar.style.borderRight = 'none';

    sidebar.innerHTML = `
        <div class="sidebar-section">
            <div class="sidebar-heading" style="color:rgba(255,255,255,.4);">Overview</div>
            <a href="/admin/index.html" class="sidebar-link ${active('index')}"><span class="icon">📊</span> Dashboard</a>
        </div>
        <div class="sidebar-section">
            <div class="sidebar-heading" style="color:rgba(255,255,255,.4);">Operations</div>
            <a href="/admin/bookings.html" class="sidebar-link ${active('bookings')}"><span class="icon">📋</span> Bookings</a>
            <a href="/admin/schedule.html" class="sidebar-link ${active('schedule')}"><span class="icon">📅</span> Schedule</a>
            <a href="/admin/payments.html" class="sidebar-link ${active('payments')}"><span class="icon">💳</span> Payments</a>
            <a href="/admin/refunds.html" class="sidebar-link ${active('refunds')}"><span class="icon">💸</span> Refunds</a>
        </div>
        <div class="sidebar-section">
            <div class="sidebar-heading" style="color:rgba(255,255,255,.4);">Manage</div>
            <a href="/admin/users.html" class="sidebar-link ${active('users')}"><span class="icon">👥</span> Users</a>
            <a href="/admin/vehicles.html" class="sidebar-link ${active('vehicles')}"><span class="icon">🚗</span> Vehicles</a>
            <a href="/admin/services.html" class="sidebar-link ${active('services')}"><span class="icon">💧</span> Services</a>
            <a href="/admin/promos.html" class="sidebar-link ${active('promos')}"><span class="icon">🎟️</span> Promos</a>
        </div>
        <div class="sidebar-section">
            <div class="sidebar-heading" style="color:rgba(255,255,255,.4);">Account</div>
            <a href="/dashboard.html" class="sidebar-link"><span class="icon">👤</span> Customer View</a>
            <a href="#" class="sidebar-link" id="sidebarLogout"><span class="icon">🚪</span> Logout</a>
        </div>
    `;

    sidebar.querySelectorAll('.sidebar-link').forEach(link => {
        link.style.color = 'rgba(255,255,255,.75)';
        if (link.classList.contains('active')) {
            link.style.background = 'rgba(0,212,255,.15)';
            link.style.color = '#00d4ff';
            link.style.fontWeight = '600';
        }
        link.addEventListener('mouseenter', () => {
            if (!link.classList.contains('active')) {
                link.style.background = 'rgba(255,255,255,.05)';
                link.style.color = 'white';
            }
        });
        link.addEventListener('mouseleave', () => {
            if (!link.classList.contains('active')) {
                link.style.background = '';
                link.style.color = 'rgba(255,255,255,.75)';
            }
        });
    });

    const logout = document.getElementById('sidebarLogout');
    if (logout) logout.addEventListener('click', async (e) => {
        e.preventDefault();
        await fetch('/api/auth/logout', { method: 'POST', credentials: 'include' });
        window.location.href = '/';
    });
})();