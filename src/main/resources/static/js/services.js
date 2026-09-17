// AquaShine - Services client logic

if (document.getElementById("serviceGrid")) {
    fetch("/api/services")
        .then(res => res.json())
        .then(data => {
            const grid = document.getElementById("serviceGrid");
            if (!Array.isArray(data) || data.length === 0) {
                grid.innerHTML = "<p class='text-muted'>No services available.</p>";
                return;
            }

            const colorFor = (cat) => ({
                BASIC:   "linear-gradient(135deg,#48cae4,#90e0ef)",
                PREMIUM: "linear-gradient(135deg,#0077b6,#00b4d8)",
                DELUXE:  "linear-gradient(135deg,#023e8a,#0077b6)"
            }[cat] || "linear-gradient(135deg,#0077b6,#00b4d8)");

            const iconFor = (cat) => ({ BASIC: "💧", PREMIUM: "✨", DELUXE: "👑" }[cat] || "💧");

            grid.innerHTML = data.map(s => `
                <div class="service-card" style="
                    background: white;
                    border-radius: 12px;
                    padding: 1.75rem;
                    box-shadow: 0 4px 20px rgba(0,0,0,0.06);
                    cursor: pointer;
                    transition: all 0.2s cubic-bezier(0.4,0,0.2,1);
                    border-top: 4px solid transparent;
                    border-image: ${colorFor(s.category)} 1;
                    display: flex;
                    flex-direction: column;
                ">
                    <div style="font-size:2.5rem;margin-bottom:.5rem;">${iconFor(s.category)}</div>
                    <h3 style="font-size:1.25rem;margin-bottom:.25rem;">${s.name}</h3>
                    <span style="background:rgba(0,119,182,.1);color:#0077b6;padding:.15rem .6rem;border-radius:12px;font-size:.75rem;font-weight:600;align-self:flex-start;">${s.category}</span>
                    <div style="font-size:1.75rem;font-weight:700;color:#0077b6;margin:1rem 0 .25rem;font-family:Poppins;">₹${s.basePrice}</div>
                    <p class="text-muted" style="font-size:.85rem;margin-bottom:1rem;">Base price for hatchback</p>
                    <a href="/auth/book.html?serviceId=${s.id}" class="btn btn-primary" style="margin-top:auto;">Book This Service →</a>
                </div>
            `).join("");
        })
        .catch(() => {
            document.getElementById("serviceGrid").innerHTML = "<p class='text-muted'>Failed to load.</p>";
        });
}