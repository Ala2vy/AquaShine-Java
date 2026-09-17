// AquaShine - Profile client logic

if (document.getElementById("emailHeader")) {
    fetch("/api/profile/me", { credentials: "include" })
        .then(res => { if (!res.ok) { window.location.href = "/auth/login.html"; return null; } return res.json(); })
        .then(data => {
            if (!data) return;
            document.getElementById("emailHeader").textContent = data.email;
            document.getElementById("fullName").value = data.fullName || "";
            document.getElementById("phone").value = data.phone || "";
            const initial = (data.fullName || data.email || "?").charAt(0).toUpperCase();
            document.getElementById("avatar").textContent = initial;
        })
        .catch(() => window.location.href = "/auth/login.html");
}

const editForm = document.getElementById("editForm");
if (editForm) {
    editForm.addEventListener("submit", async (e) => {
        e.preventDefault();
        const fullName = document.getElementById("fullName").value;
        const phone = document.getElementById("phone").value;
        document.getElementById("editError").textContent = "";
        const btn = document.getElementById("saveBtn");
        btn.disabled = true;
        btn.textContent = "Saving…";
        try {
            const res = await fetch("/api/profile/edit", {
                method: "POST",
                headers: { "Content-Type": "application/json" },
                credentials: "include",
                body: JSON.stringify({ fullName, phone })
            });
            const data = await res.json();
            if (!res.ok) {
                document.getElementById("editError").textContent = data.error || "Update failed";
                showToast(data.error || "Update failed", "error");
            } else {
                showToast("Profile updated", "success");
                document.getElementById("avatar").textContent = (fullName || "?").charAt(0).toUpperCase();
            }
        } catch { showToast("Network error", "error"); }
        finally { btn.disabled = false; btn.textContent = "Save Changes"; }
    });
}

const changePasswordForm = document.getElementById("changePasswordForm");
if (changePasswordForm) {
    changePasswordForm.addEventListener("submit", async (e) => {
        e.preventDefault();
        const oldPassword = document.getElementById("oldPassword").value;
        const newPassword = document.getElementById("newPassword").value;
        document.getElementById("error").textContent = "";
        try {
            const res = await fetch("/api/profile/change-password", {
                method: "POST",
                headers: { "Content-Type": "application/json" },
                credentials: "include",
                body: JSON.stringify({ oldPassword, newPassword })
            });
            const data = await res.json();
            if (!res.ok) {
                document.getElementById("error").textContent = data.error || "Change failed";
                showToast(data.error || "Change failed", "error");
            } else {
                showToast("Password changed", "success");
                changePasswordForm.reset();
            }
        } catch { showToast("Network error", "error"); }
    });
}