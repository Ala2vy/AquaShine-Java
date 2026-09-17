// AquaShine - Auth client logic

let selectedRole = "USER";  // default

// ==================== ROLE TABS ====================
const roleTabs = document.getElementById("roleTabs");
if (roleTabs) {
    const hints = {
        USER: "Sign in as a customer to book a wash.",
        ADMIN: "Sign in as an administrator to manage the platform."
    };

    roleTabs.querySelectorAll(".role-tab").forEach(tab => {
        tab.addEventListener("click", () => {
            roleTabs.querySelectorAll(".role-tab").forEach(t => t.classList.remove("active"));
            tab.classList.add("active");
            selectedRole = tab.dataset.role;
            document.getElementById("roleHint").textContent = hints[selectedRole];
            document.getElementById("error").textContent = "";
        });
    });
}

// ==================== LOGIN ====================
const loginForm = document.getElementById("loginForm");
if (loginForm) {
    loginForm.addEventListener("submit", async (e) => {
        e.preventDefault();
        const email = document.getElementById("email").value.trim();
        const password = document.getElementById("password").value;
        const err = document.getElementById("error");
        err.textContent = "";

        const btn = document.getElementById("loginBtn");
        btn.disabled = true;
        btn.textContent = "Signing in…";

        try {
            const res = await fetch("/api/auth/login", {
                method: "POST",
                headers: { "Content-Type": "application/json" },
                body: JSON.stringify({ email, password })
            });
            const data = await res.json();

            if (!res.ok) {
                err.textContent = data.error || "Login failed";
                showToast(data.error || "Login failed", "error");
                btn.disabled = false;
                btn.textContent = "Login";
                return;
            }

            // Verify selected role matches account role
            const accountRole = data.role || "USER";
            if (accountRole !== selectedRole) {
                err.textContent = accountRole === "ADMIN"
                    ? "This account is an admin. Switch to the Admin tab."
                    : "This account is a customer. Switch to the Customer tab.";
                showToast("Wrong role selected", "warning");
                btn.disabled = false;
                btn.textContent = "Login";
                return;
            }

            showToast("Welcome back!", "success");
            const dest = data.redirect || (accountRole === "ADMIN" ? "/admin/index.html" : "/dashboard.html");
            setTimeout(() => window.location.href = dest, 300);

        } catch {
            err.textContent = "Network error";
            btn.disabled = false;
            btn.textContent = "Login";
        }
    });
}

// ==================== REGISTER ====================
const registerForm = document.getElementById("registerForm");
if (registerForm) {
    registerForm.addEventListener("submit", async (e) => {
        e.preventDefault();
        const email = document.getElementById("email").value;
        const fullName = document.getElementById("fullName").value;
        const phone = document.getElementById("phone").value;
        const password = document.getElementById("password").value;
        document.getElementById("error").textContent = "";
        const btn = document.getElementById("registerBtn");
        btn.disabled = true;
        btn.textContent = "Creating…";

        try {
            const res = await fetch("/api/auth/register", {
                method: "POST",
                headers: { "Content-Type": "application/json" },
                body: JSON.stringify({ email, password, fullName, phone })
            });
            const data = await res.json();
            if (!res.ok) {
                document.getElementById("error").textContent = data.error || "Registration failed";
                showToast(data.error || "Registration failed", "error");
                btn.disabled = false;
                btn.textContent = "Create Account";
            } else {
                showToast("Account created!", "success");
                setTimeout(() => window.location.href = "/dashboard.html", 500);
            }
        } catch {
            document.getElementById("error").textContent = "Network error";
            btn.disabled = false;
            btn.textContent = "Create Account";
        }
    });
}