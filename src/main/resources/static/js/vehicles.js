// AquaShine - Vehicles client logic

// List vehicles
if (document.getElementById("vehicleGrid")) {
    fetch("/api/vehicles", { credentials: "include" })
        .then(res => {
            if (!res.ok) { window.location.href = "/auth/login.html"; return null; }
            return res.json();
        })
        .then(data => {
            if (!data) return;
            const grid = document.getElementById("vehicleGrid");
            if (data.length === 0) {
                grid.innerHTML = `
                    <div class="empty-state" style="grid-column:1/-1;">
                        <div class="empty-icon">🚗</div>
                        <h3>No vehicles yet</h3>
                        <p>Add your first car to start booking washes.</p>
                        <a href="/auth/add-vehicle.html" class="btn btn-primary">Add Vehicle</a>
                    </div>
                `;
                return;
            }

            const icon = (t) => t === "HATCHBACK" ? "🚗" : t === "SEDAN" ? "🚙" : "🚐";

            grid.innerHTML = data.map(v => `
                <div class="card" style="padding:1.25rem;">
                    <div style="font-size:2rem;margin-bottom:.5rem;">${icon(v.type)}</div>
                    <h3 style="font-size:1.15rem;margin-bottom:.15rem;">${v.plateNumber}</h3>
                    <p class="text-muted" style="font-size:.85rem;">${v.type}</p>
                    ${v.make || v.model ? `<p class="text-muted" style="font-size:.85rem;">${v.make || ""} ${v.model || ""}</p>` : ""}
                    <p class="text-muted" style="font-size:.85rem;">Year: ${v.year || "—"}</p>
                    <button onclick="deleteVehicle(${v.id})" class="btn btn-danger btn-sm" style="margin-top:1rem;width:100%;">Delete</button>
                </div>
            `).join("");
        })
        .catch(() => window.location.href = "/auth/login.html");
}

async function deleteVehicle(id) {
    if (!confirm("Delete this vehicle?")) return;
    try {
        const res = await fetch(`/api/vehicles/${id}`, {
            method: "DELETE",
            credentials: "include"
        });
        if (res.ok) {
            showToast("Vehicle deleted", "success");
            setTimeout(() => window.location.reload(), 500);
        } else {
            const data = await res.json();
            showToast(data.error || "Delete failed", "error");
        }
    } catch {
        showToast("Network error", "error");
    }
}

// Add vehicle form
const addVehicleForm = document.getElementById("addVehicleForm");
if (addVehicleForm) {
    addVehicleForm.addEventListener("submit", async (e) => {
        e.preventDefault();
        document.getElementById("error").textContent = "";
        const body = {
            plateNumber: document.getElementById("plateNumber").value,
            type: document.getElementById("type").value,
            make: document.getElementById("make").value,
            model: document.getElementById("model").value,
            year: parseInt(document.getElementById("year").value)
        };

        const btn = document.getElementById("addBtn");
        btn.disabled = true;
        btn.textContent = "Adding…";

        try {
            const res = await fetch("/api/vehicles", {
                method: "POST",
                headers: { "Content-Type": "application/json" },
                credentials: "include",
                body: JSON.stringify(body)
            });
            const data = await res.json();
            if (!res.ok) {
                document.getElementById("error").textContent = data.error || "Add failed";
                showToast(data.error || "Add failed", "error");
                btn.disabled = false;
                btn.textContent = "Add Vehicle";
            } else {
                showToast("Vehicle added!", "success");
                const returnTo = window.__returnTo;
                const newId = data.id;
                setTimeout(() => {
                    if (returnTo === "book") {
                        window.location.href = "/auth/book.html";
                    } else {
                        window.location.href = "/auth/vehicles.html";
                    }
                }, 700);
            }
        } catch {
            showToast("Network error", "error");
            btn.disabled = false;
            btn.textContent = "Add Vehicle";
        }
    });
}