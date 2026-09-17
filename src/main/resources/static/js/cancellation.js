// AquaShine - Cancellation client logic

// ============ Auto-inject Cancel/Reschedule on booking cards ============
if (document.getElementById("bookingList")) {
    const observer = new MutationObserver(() => {
        document.querySelectorAll(".booking-card").forEach(card => {
            if (card.querySelector(".booking-actions")) return;
            const id = card.dataset.bookingId;
            const status = card.dataset.status;
            if (status !== "CONFIRMED") return;
            const actions = document.createElement("div");
            actions.className = "booking-actions";
            actions.style.marginTop = "1rem";
            actions.style.display = "flex";
            actions.style.gap = ".5rem";
            actions.innerHTML = `
                <a href="/auth/reschedule.html?id=${id}" class="btn btn-ghost btn-sm" style="flex:1;">🔄 Reschedule</a>
                <a href="/auth/cancel.html?id=${id}" class="btn btn-danger btn-sm" style="flex:1;">❌ Cancel</a>
            `;
            card.appendChild(actions);
        });
    });
    observer.observe(document.getElementById("bookingList"), { childList: true, subtree: true });
}

// ============ Cancel page ============
if (document.getElementById("cancelBtn")) {
    const bookingId = new URLSearchParams(window.location.search).get("id");
    if (!bookingId) window.location.href = "/auth/bookings.html";

    fetch(`/api/bookings/${bookingId}/refund-preview`, { credentials: "include" })
        .then(res => res.json())
        .then(data => {
            if (data.error) {
                document.getElementById("refundDetails").textContent = data.error;
                return;
            }
            document.getElementById("refundDetails").innerHTML = `
                <p style="margin-bottom:.35rem;"><strong>Total paid:</strong> ₹${data.totalPrice}</p>
                <p style="margin-bottom:.35rem;"><strong>Refund rate:</strong> ${data.refundPercent}%</p>
                <p style="font-size:1.25rem;font-weight:700;color:#d97706;">You'll get back ₹${data.refundAmount}</p>
            `;
        })
        .catch(() => document.getElementById("refundDetails").textContent = "Failed to load refund info.");

    document.getElementById("cancelBtn").addEventListener("click", async () => {
        const reason = document.getElementById("reason").value;
        const btn = document.getElementById("cancelBtn");
        btn.disabled = true;
        btn.textContent = "Cancelling…";
        try {
            const res = await fetch(`/api/bookings/${bookingId}/cancel`, {
                method: "POST",
                headers: { "Content-Type": "application/json" },
                credentials: "include",
                body: JSON.stringify({ reason })
            });
            const data = await res.json();
            if (!res.ok) {
                document.getElementById("error").textContent = data.error || "Cancel failed";
                showToast(data.error || "Cancel failed", "error");
                btn.disabled = false;
                btn.textContent = "Confirm Cancellation";
            } else {
                showToast("Booking cancelled", "success");
                setTimeout(() => window.location.href = "/auth/bookings.html", 700);
            }
        } catch {
            document.getElementById("error").textContent = "Network error";
        }
    });
}

// ============ Reschedule page ============
if (document.getElementById("rescheduleBtn")) {
    const bookingId = new URLSearchParams(window.location.search).get("id");
    if (!bookingId) window.location.href = "/auth/bookings.html";

    const dateInput = document.getElementById("newDate");
    const today = new Date().toISOString().split("T")[0];
    dateInput.value = today;
    dateInput.min = today;

    const grid = document.getElementById("slotGrid");
    let selectedTime = null;
    const slots = [];
    for (let h = 8; h < 20; h++) {
        slots.push(`${String(h).padStart(2, "0")}:00`);
        slots.push(`${String(h).padStart(2, "0")}:30`);
    }
    grid.innerHTML = slots.map(t => `<button type="button" class="slot-btn" data-time="${t}">${t}</button>`).join("");
    grid.querySelectorAll(".slot-btn").forEach(btn => {
        btn.addEventListener("click", () => {
            grid.querySelectorAll(".slot-btn").forEach(b => b.classList.remove("selected"));
            btn.classList.add("selected");
            selectedTime = btn.dataset.time;
        });
    });

    document.getElementById("rescheduleBtn").addEventListener("click", async () => {
        if (!selectedTime) { showToast("Pick a time slot", "warning"); return; }
        try {
            const res = await fetch(`/api/bookings/${bookingId}/reschedule`, {
                method: "POST",
                headers: { "Content-Type": "application/json" },
                credentials: "include",
                body: JSON.stringify({ slotDate: dateInput.value, slotTime: selectedTime + ":00" })
            });
            const data = await res.json();
            if (!res.ok) {
                document.getElementById("error").textContent = data.error || "Reschedule failed";
                showToast(data.error || "Reschedule failed", "error");
            } else {
                showToast("Booking rescheduled", "success");
                setTimeout(() => window.location.href = "/auth/bookings.html", 700);
            }
        } catch {
            document.getElementById("error").textContent = "Network error";
        }
    });
}