// AquaShine - Payment v2 (fixed: no top-level return)

// ============ Payment Page ============
(async function initPayment() {
    const form = document.getElementById("paymentForm");
    if (!form) return;   // not on payment page

    const bookingId = new URLSearchParams(window.location.search).get("bookingId");
    if (!bookingId) {
        window.location.href = "/auth/bookings.html";
        return;
    }

    let originalAmount = 0;
    let appliedPromo = null;
    let appliedDiscount = 0;

    // ---- Load booking details ----
    try {
        const res = await fetch("/api/bookings", { credentials: "include" });
        if (!res.ok) {
            if (res.status === 401) { window.location.href = "/auth/login.html"; return; }
            showToast("Failed to load booking", "error");
        } else {
            const bookings = await res.json();
            const booking = bookings.find(b => b.id == bookingId);
            if (booking) {
                originalAmount = booking.totalPrice;
                document.getElementById("origAmt").textContent = `₹${originalAmount}`;
                document.getElementById("finalAmt").textContent = `₹${originalAmount}`;
                document.getElementById("bookingInfo").innerHTML = `
                    <div style="font-size:.85rem;color:var(--gray-600);">
                        <div style="font-weight:700;color:var(--navy-900);font-size:1rem;margin-bottom:.25rem;">Booking #${booking.id}</div>
                        <div>📅 ${booking.slotDate} · ${String(booking.slotTime).substring(0,5)}</div>
                    </div>
                `;
            } else {
                document.getElementById("bookingInfo").innerHTML =
                    '<div style="color:var(--error);font-size:.85rem;">Booking not found</div>';
            }
        }
    } catch (e) {
        document.getElementById("bookingInfo").innerHTML =
            '<div style="color:var(--error);font-size:.85rem;">Network error</div>';
    }

    // ---- Load available promos as chips ----
    try {
        const knownPromos = [
            { code: "WASH50", desc: "50% off (min ₹1000)" },
            { code: "FIRST10", desc: "10% off (min ₹500)" },
            { code: "SAVE20", desc: "20% off (min ₹800)" }
        ];
        const el = document.getElementById("promoList");
        if (el) {
            el.innerHTML = knownPromos.map(p =>
                `<span class="promo-chip" data-code="${p.code}" title="${p.desc}">🎟️ ${p.code}</span>`
            ).join("");
            el.querySelectorAll(".promo-chip").forEach(chip => {
                chip.addEventListener("click", () => {
                    document.getElementById("promoCode").value = chip.dataset.code;
                    document.getElementById("validatePromo").click();
                });
            });
        }
    } catch (e) { /* silent */ }

    // ---- Card number formatting ----
    const cardInput = document.getElementById("cardNumber");
    if (cardInput) {
        cardInput.addEventListener("input", (e) => {
            let v = e.target.value.replace(/\D/g, "").slice(0, 16);
            const formatted = v.replace(/(.{4})/g, "$1 ").trim();
            e.target.value = formatted;
            const display = document.getElementById("cardDisplay");
            if (display) {
                display.textContent = (formatted || "•••• •••• •••• ••••").padEnd(19, "•");
            }
        });
    }

    // ---- Promo validation ----
    const validateBtn = document.getElementById("validatePromo");
    if (validateBtn) {
        validateBtn.addEventListener("click", async () => {
            const code = document.getElementById("promoCode").value.trim().toUpperCase();
            const msg = document.getElementById("promoMsg");
            msg.textContent = "";
            msg.style.color = "";

            if (!code) {
                msg.textContent = "Enter a promo code";
                msg.style.color = "var(--error)";
                return;
            }

            try {
                const res = await fetch("/api/payments/validate-promo", {
                    method: "POST",
                    headers: { "Content-Type": "application/json" },
                    credentials: "include",
                    body: JSON.stringify({ code, amount: originalAmount })
                });
                const data = await res.json();

                if (!res.ok || !data.valid) {
                    msg.textContent = `❌ ${data.error || "Invalid promo code"}`;
                    msg.style.color = "var(--error)";
                    appliedPromo = null;
                    appliedDiscount = 0;
                    document.getElementById("discountRow").style.display = "none";
                    document.getElementById("finalAmt").textContent = `₹${originalAmount}`;
                } else {
                    msg.textContent = `✅ Applied! You save ₹${data.discount}`;
                    msg.style.color = "var(--success)";
                    appliedPromo = code;
                    appliedDiscount = data.discount;
                    document.getElementById("discountRow").style.display = "flex";
                    document.getElementById("discAmt").textContent = `-₹${data.discount}`;
                    document.getElementById("finalAmt").textContent = `₹${data.finalAmount}`;
                }
            } catch {
                msg.textContent = "Network error";
                msg.style.color = "var(--error)";
            }
        });
    }

    // ---- Submit payment ----
    form.addEventListener("submit", async (e) => {
        e.preventDefault();
        const err = document.getElementById("error");
        err.textContent = "";
        const cardNumber = document.getElementById("cardNumber").value;
        const promoCode = appliedPromo || document.getElementById("promoCode").value.trim();

        const btn = document.getElementById("payBtn");
        btn.disabled = true;
        btn.textContent = "Processing…";

        try {
            const res = await fetch("/api/payments", {
                method: "POST",
                headers: { "Content-Type": "application/json" },
                credentials: "include",
                body: JSON.stringify({
                    bookingId: parseInt(bookingId),
                    cardNumber,
                    promoCode: promoCode || null
                })
            });
            const data = await res.json();
            if (!res.ok) {
                err.textContent = data.error || "Payment failed";
                showToast(data.error || "Payment failed", "error");
                btn.disabled = false;
                btn.textContent = "Pay Now";
            } else {
                sessionStorage.setItem("receipt", JSON.stringify(data));
                showToast("Payment successful!", "success");
                setTimeout(() => window.location.href = "/auth/receipt.html?bookingId=" + bookingId, 600);
            }
        } catch {
            err.textContent = "Network error";
            showToast("Network error", "error");
            btn.disabled = false;
            btn.textContent = "Pay Now";
        }
    });
})();

// ============ Receipt Page ============
(function initReceipt() {
    const txnEl = document.getElementById("txnId");
    if (!txnEl) return;

    const stored = sessionStorage.getItem("receipt");
    if (stored) {
        try {
            const data = JSON.parse(stored);
            txnEl.textContent = data.transactionId || "—";
            document.getElementById("origAmt").textContent = `₹${data.originalAmount}`;
            document.getElementById("finalAmt").textContent = `₹${data.finalAmount}`;
            if (data.discountAmount > 0) {
                document.getElementById("discRow").style.display = "flex";
                document.getElementById("discAmt").textContent = `-₹${data.discountAmount}`;
            }
        } catch (e) { console.error(e); }
    }
})();