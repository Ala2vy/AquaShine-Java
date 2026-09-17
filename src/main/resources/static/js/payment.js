// AquaShine - Payment client logic

// ============ Payment page ============
if (document.getElementById("paymentForm")) {
    const params = new URLSearchParams(window.location.search);
    const bookingId = params.get("bookingId");
    if (!bookingId) {
        window.location.href = "/auth/bookings.html";
    }

    let originalAmount = 0;
    let appliedDiscount = 0;

    // Fetch booking total price from bookings list (find our booking)
    fetch("/api/bookings", { credentials: "include" })
        .then(res => res.ok ? res.json() : [])
        .then(bookings => {
            const b = bookings.find(x => x.id == bookingId);
            if (b) {
                originalAmount = b.totalPrice;
                document.getElementById("origAmt").textContent = "₹" + originalAmount;
                document.getElementById("finalAmt").textContent = "₹" + originalAmount;
            }
        });

    // Card display
    document.getElementById("cardNumber").addEventListener("input", (e) => {
        let v = e.target.value.replace(/\D/g, "").slice(0, 16);
        let formatted = v.replace(/(.{4})/g, "$1 ").trim();
        e.target.value = formatted;
        document.getElementById("cardDisplay").textContent =
            (formatted || "•••• •••• •••• ••••").padEnd(19, "•");
    });

    // Validate promo
    document.getElementById("validatePromo").addEventListener("click", async () => {
        const code = document.getElementById("promoCode").value.trim();
        const msg = document.getElementById("promoMsg");
        msg.textContent = "";
        if (!code) {
            msg.textContent = "Enter a promo code";
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
                msg.textContent = data.error || "Invalid code";
                msg.className = "error-message";
                appliedDiscount = 0;
                document.getElementById("discountRow").style.display = "none";
                document.getElementById("finalAmt").textContent = "₹" + originalAmount;
            } else {
                msg.textContent = `✅ Applied! You save ₹${data.discount}`;
                msg.className = "success-message";
                appliedDiscount = data.discount;
                document.getElementById("discountRow").style.display = "flex";
                document.getElementById("discAmt").textContent = "-₹" + data.discount;
                document.getElementById("finalAmt").textContent = "₹" + data.finalAmount;
            }
        } catch {
            msg.textContent = "Network error";
        }
    });

    // Submit payment
    document.getElementById("paymentForm").addEventListener("submit", async (e) => {
        e.preventDefault();
        const err = document.getElementById("error");
        err.textContent = "";
        const cardNumber = document.getElementById("cardNumber").value;
        const promoCode = document.getElementById("promoCode").value.trim();

        const btn = document.getElementById("payBtn");
        btn.disabled = true;
        btn.textContent = "Processing...";

        try {
            const res = await fetch("/api/payments", {
                method: "POST",
                headers: { "Content-Type": "application/json" },
                credentials: "include",
                body: JSON.stringify({ bookingId: parseInt(bookingId), cardNumber, promoCode: promoCode || null })
            });
            const data = await res.json();
            if (!res.ok) {
                err.textContent = data.error || "Payment failed";
                showToast(data.error || "Payment failed", "error");
                btn.disabled = false;
                btn.textContent = "Pay Now";
            } else {
                showToast("Payment successful!", "success");
                // Pass receipt data via sessionStorage
                sessionStorage.setItem("receipt", JSON.stringify(data));
                setTimeout(() => window.location.href = "/auth/receipt.html?bookingId=" + bookingId, 700);
            }
        } catch {
            err.textContent = "Network error";
            btn.disabled = false;
            btn.textContent = "Pay Now";
        }
    });
}

// ============ Receipt page ============
if (document.getElementById("txnId")) {
    const stored = sessionStorage.getItem("receipt");
    if (stored) {
        try {
            const data = JSON.parse(stored);
            document.getElementById("txnId").textContent = data.transactionId || "—";
            document.getElementById("origAmt").textContent = "₹" + data.originalAmount;
            document.getElementById("finalAmt").textContent = "₹" + data.finalAmount;
            if (data.discountAmount > 0) {
                document.getElementById("discRow").style.display = "flex";
                document.getElementById("discAmt").textContent = "-₹" + data.discountAmount;
            }
        } catch (e) {
            console.error(e);
        }
    }
}