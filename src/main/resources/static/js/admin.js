// AquaShine - Admin shared utilities

// Ensures only admins can view admin pages
async function requireAdmin() {
    const res = await fetch('/api/auth/me', { credentials: 'include' });
    if (!res.ok) {
        window.location.href = '/auth/login.html';
        return null;
    }
    // Fetch role via profile
    const pRes = await fetch('/api/profile/me', { credentials: 'include' });
    if (!pRes.ok) {
        window.location.href = '/auth/login.html';
        return null;
    }
    const user = await pRes.json();
    // Try admin endpoint to verify role
    const aRes = await fetch('/api/admin/stats', { credentials: 'include' });
    if (!aRes.ok) {
        showToast('Admin access required', 'error');
        setTimeout(() => window.location.href = '/dashboard.html', 1000);
        return null;
    }
    return user;
}

// Format currency
const inr = (n) => '₹' + (n || 0).toLocaleString('en-IN');

// Format date
const fmtDate = (s) => {
    if (!s) return '—';
    const d = new Date(s);
    return d.toLocaleDateString('en-IN', { day: '2-digit', month: 'short', year: 'numeric' });
};

const fmtDateTime = (s) => {
    if (!s) return '—';
    const d = new Date(s);
    return d.toLocaleString('en-IN', { day: '2-digit', month: 'short', hour: '2-digit', minute: '2-digit' });
};

// Status badge
const statusBadge = (status) => {
    const map = {
        CONFIRMED: { bg: '#dcfce7', fg: '#16a34a' },
        COMPLETED: { bg: '#dbeafe', fg: '#2563eb' },
        CANCELLED: { bg: '#fee2e2', fg: '#dc2626' },
        PENDING:   { bg: '#fef3c7', fg: '#d97706' },
        SUCCESS:   { bg: '#dcfce7', fg: '#16a34a' },
        USER:      { bg: '#f1f5f9', fg: '#64748b' },
        ADMIN:     { bg: '#fef3c7', fg: '#d97706' }
    }[status] || { bg: '#f1f5f9', fg: '#64748b' };
    return `<span style="background:${map.bg};color:${map.fg};padding:.2rem .6rem;border-radius:10px;font-size:.72rem;font-weight:700;letter-spacing:.03em;">${status}</span>`;
};