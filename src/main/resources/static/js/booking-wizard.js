// AquaShine - Professional Booking Wizard

(async function bookingWizard() {
    if (!document.getElementById("wizardView")) return;

    const params = new URLSearchParams(window.location.search);
    const preselectedServiceId = params.get("serviceId");

    // State
    const state = {
        vehicle: null,
        service: null,
        date: null,
        time: null
    };
    const BASE = { HATCHBACK: 1, SEDAN: 1.5, SUV: 2 };
    const MAX_DAYS_AHEAD = 30;

    // Element shortcuts
    const views = {
        loading: document.getElementById("loadingView"),
        empty: document.getElementById("emptyView"),
        wizard: document.getElementById("wizardView")
    };
    const summaryBar = document.getElementById("summaryBar");
    const wizardSteps = document.getElementById("wizardSteps");

    // ---- UI helpers ----
    const show = (view) => {
        Object.values(views).forEach(v => v.style.display = "none");
        view.style.display = "block";
    };

    const goToStep = (n) => {
        document.querySelectorAll(".wpanel").forEach(p => {
            p.classList.toggle("active", parseInt(p.dataset.panel) === n);
        });
        document.querySelectorAll(".wstep").forEach(s => {
            const step = parseInt(s.dataset.step);
            s.classList.toggle("active", step === n);
            s.classList.toggle("done", step < n);
            const numEl = s.querySelector(".num");
            numEl.textContent = step < n ? "✓" : step;
        });
        summaryBar.style.display = n >= 2 ? "flex" : "none";
    };

    const shake = (el) => {
        el.animate([
            { transform: "translateX(0)" },
            { transform: "translateX(-6px)" },
            { transform: "translateX(6px)" },
            { transform: "translateX(-4px)" },
            { transform: "translateX(0)" }
        ], { duration: 300, easing: "ease-in-out" });
    };

    const updateSummary = () => {
        const vLabel = state.vehicle ? `${state.vehicle.type}` : "—";
        const sLabel = state.service ? state.service.name : null;

        document.getElementById("sVehicle").textContent = vLabel;

        const bits = [];
        if (sLabel) bits.push(sLabel);
        if (state.date) bits.push(state.date);
        if (state.time) bits.push(state.time);
        document.getElementById("sMeta").textContent = bits.length ? bits.join(" · ") : "Select your options";

        let total = 0;
        if (state.service && state.vehicle) {
            total = Math.round(state.service.price * (BASE[state.vehicle.type] || 1));
        } else if (state.service) {
            total = state.service.price;
        }
        document.getElementById("sPrice").textContent = `₹${total}`;
    };

    // ---- Load data ----
    let vehicles = [];
    let services = [];

    try {
        const [vRes, sRes] = await Promise.all([
            fetch("/api/vehicles", { credentials: "include" }),
            fetch("/api/services")
        ]);

        if (!vRes.ok) { window.location.href = "/auth/login.html"; return; }

        vehicles = await vRes.json();
        services = await sRes.json();
    } catch {
        showToast("Failed to load data", "error");
        show(views.empty);
        return;
    }

    // ---- Empty state ----
    if (!Array.isArray(vehicles) || vehicles.length === 0) {
        show(views.empty);
        return;
    }

    // ---- Render vehicles ----
    const vehicleList = document.getElementById("vehicleList");
    const vIcon = (t) => t === "HATCHBACK" ? "🚗" : t === "SEDAN" ? "🚙" : "🚐";

    vehicleList.innerHTML = vehicles.map(v => `
        <div class="choice" data-vehicle-id="${v.id}" data-vehicle-type="${v.type}">
            <div class="choice-icon">${vIcon(v.type)}</div>
            <div class="choice-body">
                <div class="choice-title">${v.plateNumber}</div>
                <div class="choice-sub">${v.type}${v.make ? " · " + v.make : ""}${v.model ? " " + v.model : ""}</div>
            </div>
        </div>
    `).join("");

    vehicleList.addEventListener("click", (e) => {
        const card = e.target.closest(".choice");
        if (!card) return;
        vehicleList.querySelectorAll(".choice").forEach(c => c.classList.remove("selected"));
        card.classList.add("selected");
        state.vehicle = {
            id: parseInt(card.dataset.vehicleId),
            type: card.dataset.vehicleType
        };
        updateSummary();
    });

    // ---- Render services ----
    const serviceList = document.getElementById("serviceList");
    const sIcon = (cat) => ({ BASIC: "💧", PREMIUM: "✨", DELUXE: "👑" }[cat] || "💧");

    serviceList.innerHTML = services.map(s => `
        <div class="choice" data-service-id="${s.id}" data-service-price="${s.basePrice}" data-service-name="${s.name}" data-service-category="${s.category}">
            <div class="choice-icon">${sIcon(s.category)}</div>
            <div class="choice-body">
                <div class="choice-title">${s.name}</div>
                <div class="choice-sub">${s.category} · ₹${s.basePrice} base</div>
            </div>
        </div>
    `).join("");

    serviceList.addEventListener("click", (e) => {
        const card = e.target.closest(".choice");
        if (!card) return;
        serviceList.querySelectorAll(".choice").forEach(c => c.classList.remove("selected"));
        card.classList.add("selected");
        state.service = {
            id: parseInt(card.dataset.serviceId),
            price: parseInt(card.dataset.servicePrice),
            name: card.dataset.serviceName
        };
        updateSummary();
    });

    // Preselect service from URL
    if (preselectedServiceId) {
        const target = serviceList.querySelector(`[data-service-id="${preselectedServiceId}"]`);
        if (target) {
            target.click();
            showToast("Service pre-selected — now pick a vehicle", "info", 2500);
        }
    }

    // ---- Date input ----
    const dateInput = document.getElementById("slotDate");
    const today = new Date();
    const maxDate = new Date();
    maxDate.setDate(today.getDate() + MAX_DAYS_AHEAD);

    dateInput.value = today.toISOString().split("T")[0];
    dateInput.min = today.toISOString().split("T")[0];
    dateInput.max = maxDate.toISOString().split("T")[0];
    state.date = dateInput.value;

    dateInput.addEventListener("change", () => {
        state.date = dateInput.value;
        updateSummary();
        refreshSlots();
    });

    // ---- Time slots ----
    const slotGrid = document.getElementById("slotGrid");
    const ALL_SLOTS = [];
    for (let h = 8; h < 20; h++) {
        ALL_SLOTS.push(`${String(h).padStart(2, "0")}:00`);
        ALL_SLOTS.push(`${String(h).padStart(2, "0")}:30`);
    }

    const renderSlots = (takenSlots = []) => {
        slotGrid.innerHTML = ALL_SLOTS.map(t => {
            const taken = takenSlots.includes(t);
            return `<button type="button" class="slot ${taken ? "taken" : ""}" data-time="${t}" ${taken ? "disabled" : ""}>${t}</button>`;
        }).join("");
    };

    // Basic availability check via existing bookings
    async function refreshSlots() {
        try {
            const res = await fetch("/api/bookings", { credentials: "include" });
            if (!res.ok) return;
            const bookings = await res.json();
            const takenToday = bookings
                .filter(b => b.slotDate === state.date && b.status === "CONFIRMED")
                .map(b => b.slotTime.substring(0, 5));
            renderSlots(takenToday);
        } catch {
            renderSlots();
        }
    }

    slotGrid.addEventListener("click", (e) => {
        const btn = e.target.closest(".slot");
        if (!btn || btn.disabled) return;
        slotGrid.querySelectorAll(".slot").forEach(s => s.classList.remove("selected"));
        btn.classList.add("selected");
        state.time = btn.dataset.time;
        updateSummary();
    });

    refreshSlots();

    // ---- Step navigation ----
    const requireSelection = (el, message) => {
        shake(el);
        showToast(message, "warning");
    };

    document.getElementById("next1").addEventListener("click", () => {
        if (!state.vehicle) return requireSelection(vehicleList, "Please select a vehicle");
        goToStep(2);
    });

    document.getElementById("next2").addEventListener("click", () => {
        if (!state.service) return requireSelection(serviceList, "Please pick a service");
        goToStep(3);
    });

    document.getElementById("next3").addEventListener("click", () => {
        if (!state.date) return requireSelection(dateInput.parentElement, "Pick a date");
        goToStep(4);
    });

    document.getElementById("next4").addEventListener("click", () => {
        if (!state.time) return requireSelection(slotGrid, "Pick a time slot");
        // Fill confirm view
        document.getElementById("cVehicle").textContent = state.vehicle.type;
        document.getElementById("cService").textContent = state.service.name;
        document.getElementById("cDate").textContent = state.date;
        document.getElementById("cTime").textContent = state.time;
        goToStep(5);
    });

    document.querySelectorAll("[data-back]").forEach(btn => {
        btn.addEventListener("click", () => goToStep(parseInt(btn.dataset.back)));
    });

    // ---- Confirm ----
    document.getElementById("confirmBtn").addEventListener("click", async () => {
        const err = document.getElementById("error");
        err.textContent = "";
        const btn = document.getElementById("confirmBtn");
        btn.disabled = true;
        btn.textContent = "Booking…";

        try {
            const res = await fetch("/api/bookings", {
                method: "POST",
                headers: { "Content-Type": "application/json" },
                credentials: "include",
                body: JSON.stringify({
                    vehicleId: state.vehicle.id,
                    serviceId: state.service.id,
                    slotDate: state.date,
                    slotTime: state.time + ":00"
                })
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
                setTimeout(() => window.location.href = "/auth/booking-success.html", 500);
            }
        } catch {
            err.textContent = "Network error";
            showToast("Network error", "error");
            btn.disabled = false;
            btn.textContent = "Confirm Booking";
        }
    });

    // ---- Boot ----
    show(views.wizard);
    updateSummary();
})();