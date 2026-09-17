// AquaShine - Booking client logic (rewritten for smooth flow)

if (document.getElementById("wizardContent") || document.getElementById("wizardCard")) {
    const params = new URLSearchParams(window.location.search);
    const preselectedServiceId = params.get("serviceId");
    const preselectedVehicleId = params.get("vehicleId");

    let selectedVehicle = null;
    let selectedService = null;
    let selectedTime = null;
    const BASE = { HATCHBACK: 1, SEDAN: 1.5, SUV: 2 };

    // ========== UI helpers ==========
    const showLoading = (show) => {
        document.getElementById("loadingState").style.display = show ? "block" : "none";
    };
    const showEmpty = () => {
        showLoading(false);
        document.getElementById("emptyVehicles").style.display = "block";
        document.getElementById("wizardContent").style.display = "none";
        document.getElementById("wizardProgress").style.display = "none";
    };
    const showWizard = () => {
        showLoading(false);
        document.getElementById("emptyVehicles").style.display = "none";
        document.getElementById("wizardContent").style.display = "block";
        document.getElementById("wizardProgress").style.display = "flex";
    };

    const goToStep = (n) => {
        document.querySelectorAll(".wizard-panel").forEach(p => p.classList.remove("active"));
        document.getElementById("panel" + n).classList.add("active");

        document.querySelectorAll(".wizard-step").forEach(s => {
            const step = parseInt(s.dataset.step);
            s.classList.toggle("active", step === n);
            s.classList.toggle("done", step < n);
            // Update step-num to checkmark when done
            const numSpan = s.querySelector(".step-num");
            if (step < n) {
                numSpan.textContent = "✓";
            } else {
                numSpan.textContent = step;
            }
        });
    };

    const shakeEl = (el) => {
        el.style.transition = "none";
        el.style.transform = "translateX(0)";
        let i = 0;
        const shake = () => {
            el.style.transform = i % 2 ? "translateX(-6px)" : "translateX(6px)";
            i++;
            if (i < 6) setTimeout(shake, 40);
            else el.style.transform = "translateX(0)";
        };
        shake();
    };

    // ========== Load vehicles ==========
    fetch("/api/vehicles", { credentials: "include" })
        .then(res => res.ok ? res.json() : [])
        .then(vehicles => {
            if (!Array.isArray(vehicles) || vehicles.length === 0) {
                showEmpty();
                return;
            }
            showWizard();

            const el = document.getElementById("vehicleOptions");
            const iconFor = (t) => t === "HATCHBACK" ? "🚗" : t === "SEDAN" ? "🚙" : "🚐";

            el.innerHTML = vehicles.map(v => `
                <div class="choice-card vehicle-option" data-vehicle-id="${v.id}" data-vehicle-type="${v.type}">
                    <div class="choice-icon">${iconFor(v.type)}</div>
                    <div class="choice-body">
                        <div class="choice-title">${v.plateNumber}</div>
                        <div class="choice-sub">${v.type}${v.make ? " · " + v.make : ""}${v.model ? " " + v.model : ""}</div>
                    </div>
                </div>
            `).join("");

            el.querySelectorAll(".vehicle-option").forEach(card => {
                card.addEventListener("click", () => {
                    el.querySelectorAll(".vehicle-option").forEach(c => c.classList.remove("selected"));
                    card.classList.add("selected");
                    selectedVehicle = { id: parseInt(card.dataset.vehicleId), type: card.dataset.vehicleType };
                    updateSummary();
                });
            });

            // Auto-select preselected vehicle
            if (preselectedVehicleId) {
                const target = el.querySelector(`[data-vehicle-id="${preselectedVehicleId}"]`);
                if (target) target.click();
            }
        })
        .catch(() => {
            showLoading(false);
            showToast("Failed to load vehicles", "error");
        });

    // ========== Load services ==========
    fetch("/api/services")
        .then(res => res.json())
        .then(services => {
            const el = document.getElementById("serviceOptions");
            const iconFor = (cat) => ({ BASIC: "💧", PREMIUM: "✨", DELUXE: "👑" }[cat] || "💧");

            el.innerHTML = services.map(s => `
                <div class="choice-card service-option" data-service-id="${s.id}" data-service-price="${s.basePrice}" data-service-name="${s.name}">
                    <div class="choice-icon">${iconFor(s.category)}</div>
                    <div class="choice-body">
                        <div class="choice-title">${s.name}</div>
                        <div class="choice-sub">${s.category} · ₹${s.basePrice} base</div>
                    </div>
                </div>
            `).join("");

            el.querySelectorAll(".service-option").forEach(card => {
                card.addEventListener("click", () => {
                    el.querySelectorAll(".service-option").forEach(c => c.classList.remove("selected"));
                    card.classList.add("selected");
                    selectedService = {
                        id: parseInt(card.dataset.serviceId),
                        price: parseInt(card.dataset.servicePrice),
                        name: card.dataset.serviceName
                    };
                    updateSummary();
                });
            });

            // Auto-select preselected service
            if (preselectedServiceId) {
                const target = el.querySelector(`[data-service-id="${preselectedServiceId}"]`);
                if (target) {
                    target.click();
                    // Jump straight to step 1 (user still needs to pick vehicle)
                    setTimeout(() => showToast("Service pre-selected. Now pick a vehicle.", "info", 2500), 400);
                }
            }
        })
        .catch(() => showToast("Failed to load services", "error"));

    // ========== Time slots ==========
    const slotGrid = document.getElementById("slotGrid");
    const slots = [];
    for (let h = 8; h < 20; h++) {
        slots.push(`${String(h).padStart(2, "0")}:00`);
        slots.push(`${String(h).padStart(2, "0")}:30`);
    }
    slotGrid.innerHTML = slots.map(t => `<button type="button" class="slot-btn" data-time="${t}">${t}</button>`).join("");
    slotGrid.querySelectorAll(".slot-btn").forEach(btn => {
        btn.addEventListener("click", () => {
            slotGrid.querySelectorAll(".slot-btn").forEach(b => b.classList.remove("selected"));
            btn.classList.add("selected");
            selectedTime = btn.dataset.time;
            updateSummary();
        });
    });

    // ========== Date ==========
    const dateInput = document.getElementById("slotDate");
    const today = new Date().toISOString().split("T")[0];
    dateInput.value = today;
    dateInput.min = today;
    dateInput.addEventListener("change", updateSummary);

    // ========== Navigation ==========
    document.getElementById("nextToService").addEventListener("click", () => {
        if (!selectedVehicle) {
            shakeEl(document.getElementById("vehicleOptions"));
            showToast("Please select a vehicle", "warning");
            return;
        }
        goToStep(2);
    });

    document.getElementById("nextToSlot").addEventListener("click", () => {
        if (!selectedService) {
            shakeEl(document.getElementById("serviceOptions"));
            showToast("Please select a service", "warning");
            return;
        }
        goToStep(3);
        updateSummary();
    });

    document.getElementById("backToVehicle").addEventListener("click", () => goToStep(1));
    document.getElementById("backToService").addEventListener("click", () => goToStep(2));

    // ========== Live summary ==========
    function updateSummary() {
        if (selectedVehicle) document.getElementById("sumVehicle").textContent = selectedVehicle.type;
        if (selectedService) {
            document.getElementById("sumService").textContent = selectedService.name;
            const mult = selectedVehicle ? BASE[selectedVehicle.type] : 1;
            document.getElementById("sumTotal").textContent = "₹" + Math.round(selectedService.price * mult);
        }
        if (dateInput.value) document.getElementById("sumDate").textContent = dateInput.value;
        if (selectedTime) document.getElementById("sumTime").textContent = selectedTime;
    }

    // ========== Confirm ==========
    document.getElementById("confirmBooking").addEventListener("click", async () => {
        const err = document.getElementById("error");
        err.textContent = "";
        if (!selectedVehicle) { err.textContent = "Select a vehicle"; return; }
        if (!selectedService) { err.textContent = "Select a service"; return; }
        if (!dateInput.value) { err.textContent = "Select a date"; return; }
        if (!selectedTime) { err.textContent = "Select a time slot"; return; }

        const btn = document.getElementById("confirmBooking");
        btn.disabled = true;
        btn.textContent = "Booking…";

        const body = {
            vehicleId: selectedVehicle.id,
            serviceId: selectedService.id,
            slotDate: dateInput.value,
            slotTime: selectedTime + ":00"
        };

        try {
            const res = await fetch("/api/bookings", {
                method: "POST",
                headers: { "Content-Type": "application/json" },
                credentials: "include",
                body: JSON.stringify(body)
            });
            const data = await res.json();
            if (!res.ok) {
                err.textContent = data.error || "Booking failed";
                showToast(data.error || "Booking failed", "error");
                btn.disabled = false;
                btn.textContent = "Confirm Booking";
            } else {
                localStorage.setItem("lastBookingId", data.bookingId);
                showToast("Booking confirmed!", "success");
                // Small delay so user sees the toast
                setTimeout(() => window.location.href = "/auth/booking-success.html", 600);
            }
        } catch {
            err.textContent = "Network error";
            showToast("Network error", "error");
            btn.disabled = false;
            btn.textContent = "Confirm Booking";
        }
    });
}

// ============ Bookings list (unchanged behavior) ============
if (document.getElementById("bookingList")) {
    fetch("/api/bookings", { credentials: "include" })
        .then(res => res.ok ? res.json() : null)
        .then(bookings => {
            if (!bookings) { window.location.href = "/auth/login.html"; return; }
            const el = document.getElementById("bookingList");
            if (bookings.length === 0) {
                el.innerHTML = `
                    <div class="empty-state" style="grid-column:1/-1;">
                        <div class="empty-icon">📋</div>
                        <h3>No bookings yet</h3>
                        <p>Book your first wash to get started.</p>
                        <a href="/auth/services.html" class="btn btn-primary">Browse Services</a>
                    </div>
                `;
                return;
            }
            const statusColor = (s) => ({
                CONFIRMED: "#dcfce7,#16a34a",
                CANCELLED: "#fee2e2,#dc2626",
                COMPLETED: "#dbeafe,#2563eb"
            }[s] || "#f1f5f9,#64748b").split(",");

            el.innerHTML = bookings.map(b => {
                const [bg, fg] = statusColor(b.status);
                return `
                    <div class="card booking-card" data-booking-id="${b.id}" data-status="${b.status}" style="padding:1.25rem;">
                        <div style="display:flex;justify-content:space-between;align-items:start;gap:.5rem;">
                            <div>
                                <h3 style="font-size:1rem;">Booking #${b.id}</h3>
                                <p class="text-muted" style="font-size:.85rem;margin-top:.2rem;">📅 ${b.slotDate} · ${b.slotTime}</p>
                            </div>
                            <span style="background:${bg};color:${fg};padding:.2rem .55rem;border-radius:10px;font-size:.72rem;font-weight:600;white-space:nowrap;">${b.status}</span>
                        </div>
                        <div style="font-size:1.35rem;font-weight:700;color:#0077b6;margin-top:.85rem;font-family:Poppins;">₹${b.totalPrice}</div>
                    </div>
                `;
            }).join("");
        })
        .catch(() => window.location.href = "/auth/login.html");
}