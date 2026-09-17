// AquaShine - Shared utilities
console.log("AquaShine loaded");

// Helper: show error using toast
function showError(elementId, message) {
    if (window.showToast) {
        showToast(message, 'error');
    }
    const el = document.getElementById(elementId);
    if (el) {
        el.textContent = message;
        el.classList.add('error-message');
    }
}

// Helper: fetch wrapper with credentials
async function apiCall(url, method = 'GET', body = null) {
    const options = {
        method,
        headers: { 'Content-Type': 'application/json' },
        credentials: 'include'
    };
    if (body) options.body = JSON.stringify(body);
    const res = await fetch(url, options);
    const data = await res.json().catch(() => ({}));
    return { status: res.status, ok: res.ok, data };
}