
/* ==========================================
   ARTFOLIO!
   CREATIVE UNIVERSE INTERACTIONS
   Hybrid Retro Kawaii Experience
   ========================================== */

document.addEventListener("DOMContentLoaded", () => {

    "use strict";

    const reducedMotion = window.matchMedia(
        "(prefers-reduced-motion: reduce)"
    ).matches;

    const canUseMouse = window.matchMedia(
        "(hover: hover) and (pointer: fine)"
    ).matches;


    // ==========================================
    // 01. SCROLL REVEAL
    // ==========================================

    const revealElements = document.querySelectorAll(
        ".section-label, .section-heading, " +
        ".projects-heading, .discipline-card, " +
        ".gallery-card, .about-layout, .contact, " +
        ".hero-copy, .hero-universe"
    );

    if ("IntersectionObserver" in window && !reducedMotion) {

        const revealObserver = new IntersectionObserver(
            (entries, observer) => {

                entries.forEach(entry => {

                    if (entry.isIntersecting) {

                        entry.target.classList.add("is-visible");

                        observer.unobserve(entry.target);

                    }

                });

            },
            {
                threshold: 0.12
            }
        );

        revealElements.forEach(element => {

            element.classList.add("kawaii-reveal");

            revealObserver.observe(element);

        });

    } else {

        revealElements.forEach(element => {
            element.classList.add("is-visible");
        });

    }


    // ==========================================
    // 02. ACTIVE NAVIGATION
    // ==========================================

    const navigationLinks = document.querySelectorAll(
        ".navigation a"
    );

    const sections = document.querySelectorAll(
        "main section[id]"
    );

    if ("IntersectionObserver" in window) {

        const sectionObserver = new IntersectionObserver(
            entries => {

                entries.forEach(entry => {

                    if (entry.isIntersecting) {

                        navigationLinks.forEach(link => {

                            link.classList.toggle(
                                "active",
                                link.getAttribute("href") ===
                                "#" + entry.target.id
                            );

                        });

                    }

                });

            },
            {
                rootMargin: "-30% 0px -60% 0px"
            }
        );

        sections.forEach(section => {
            sectionObserver.observe(section);
        });

    }


    // ==========================================
    // 03. MOBILE NAVIGATION
    // ==========================================

    const header = document.querySelector(".header");

    if (header && navigationLinks.length > 0) {

        const navigation = header.querySelector(".navigation");

        if (navigation) {

            const menuButton = document.createElement("button");

            menuButton.className = "mobile-menu-button";
            menuButton.type = "button";
            menuButton.setAttribute(
                "aria-label",
                "Open navigation menu"
            );
            menuButton.setAttribute(
                "aria-expanded",
                "false"
            );
            menuButton.textContent = "☰";

            header.appendChild(menuButton);

            menuButton.addEventListener("click", () => {

                const isOpen = navigation.classList.toggle(
                    "menu-open"
                );

                menuButton.setAttribute(
                    "aria-expanded",
                    String(isOpen)
                );

                menuButton.textContent = isOpen ? "✕" : "☰";

            });

            navigationLinks.forEach(link => {

                link.addEventListener("click", () => {

                    navigation.classList.remove("menu-open");

                    menuButton.setAttribute(
                        "aria-expanded",
                        "false"
                    );

                    menuButton.textContent = "☰";

                });

            });

        }

    }


    // ==========================================
    // 04. KAWAII CURSOR SPARKLES
    // ==========================================

    if (canUseMouse && !reducedMotion) {

        const sparkles = ["✦", "♡", "✧", "✿", "⋆"];

        let lastSparkle = 0;

        document.addEventListener("pointermove", event => {

            const now = Date.now();

            if (now - lastSparkle < 85) {
                return;
            }

            lastSparkle = now;

            const sparkle = document.createElement("span");

            sparkle.className = "cursor-sparkle";

            sparkle.textContent = sparkles[
                Math.floor(Math.random() * sparkles.length)
            ];

            sparkle.style.left = event.clientX + "px";
            sparkle.style.top = event.clientY + "px";

            document.body.appendChild(sparkle);

            sparkle.addEventListener(
                "animationend",
                () => sparkle.remove(),
                { once: true }
            );

        });

    }


    // ==========================================
    // 05. CARD INTERACTIONS
    // ==========================================

    const cards = document.querySelectorAll(
        ".discipline-card, .gallery-card"
    );

    cards.forEach(card => {

        card.addEventListener("pointerenter", () => {

            card.classList.add("is-hovered");

        });

        card.addEventListener("pointerleave", () => {

            card.classList.remove("is-hovered");

        });

    });


    // ==========================================
    // 06. SMOOTH ANCHOR NAVIGATION
    // ==========================================

    document.querySelectorAll('a[href^="#"]').forEach(link => {

        link.addEventListener("click", event => {

            const targetId = link.getAttribute("href");

            if (!targetId || targetId === "#") {
                return;
            }

            const target = document.querySelector(targetId);

            if (!target) {
                return;
            }

            event.preventDefault();

            target.scrollIntoView({
                behavior: reducedMotion ? "auto" : "smooth",
                block: "start"
            });

        });

    });


    // ==========================================
    // 07. CURRENT YEAR
    // ==========================================

    const yearElements = document.querySelectorAll(
        "[data-current-year]"
    );

    yearElements.forEach(element => {

        element.textContent = new Date().getFullYear();

    });


    // ==========================================
    // 08. INITIALIZATION
    // ==========================================

    document.documentElement.classList.add(
        "artfolio-ready"
    );

    console.log(
        "✳ Artfolio Creative Universe initialized!"
    );

});

/* ARTFOLIO - GALLERY FILTERS */

document.addEventListener("DOMContentLoaded", () => {

    const buttons = document.querySelectorAll(
        ".gallery-filters .filter-btn"
    );

    const cards = document.querySelectorAll(
        ".gallery-grid .gallery-card"
    );

    const counter = document.querySelector(".gallery-count");

    buttons.forEach(button => {

        button.addEventListener("click", () => {

            const filter = button.dataset.filter;
            let visible = 0;

            buttons.forEach(btn => {
                const selected = btn === button;

                btn.classList.toggle("active", selected);
                btn.setAttribute("aria-pressed", selected);
            });

            cards.forEach(card => {

                const show = filter === "all" ||
                    card.dataset.category === filter;

                card.hidden = !show;

                if (show) {
                    visible++;
                }
            });

            if (counter) {
                counter.textContent =
                    `SHOWING ${visible} CREATIVE WORLD` +
                    `${visible === 1 ? "" : "S"} ♡`;
            }
        });
    });
});
