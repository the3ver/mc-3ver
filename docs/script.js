// ==========================================================================
// WorldExplorerMod (MC 3ver) — Modern Frontend & Bilingual Interactions
// ==========================================================================

const TRANSLATIONS = {
  de: {
    "page.title": "WorldExplorerMod — Minecraft 26.2 Fabric Mod",
    "page.description": "Offizielle Website und Dokumentation für die Minecraft Java Mod WorldExplorerMod (Fabric 26.2, Java 25).",
    "nav.features": "Features",
    "nav.install": "Installation",
    "nav.specs": "Anforderungen",
    "nav.faq": "FAQ",
    "nav.download": "Download",
    
    "hero.badge": "Minecraft 26.2 • Fabric • Java 25",
    "hero.title": "Modulares Minecraft-Erlebnis mit <span class=\"gradient-text\">WorldExplorerMod</span>",
    "hero.description": "Eine blitzschnelle, moderne Mod für Minecraft Java Edition 26.2, entwickelt auf Basis des Fabric Mod Loaders mit voller Java 25 Unterstützung.",
    "hero.btn.download": "Neueste Version laden",
    "hero.btn.install": "Anleitung ansehen",
    "hero.stat.mc": "Minecraft Version",
    "hero.stat.loader": "Mod Loader",
    "hero.stat.runtime": "LTS Runtime",
    "hero.stat.license": "Open Source",

    "features.title": "Highlights & Features",
    "features.subtitle": "Spannende Gameplay-Erweiterungen und moderne Architektur.",
    "features.flames.title": "🔥 Transportflammen",
    "features.flames.desc": "Platziere herstellbare Transportflammen (1 Fackel + 4 Bruchstein), erhalte gefärbte Teleport-Karten und teleportiere dich per Channeling jederzeit dimensionsübergreifend zurück.",
    "features.jump.title": "💨 Doppelsprung (Double Jump)",
    "features.jump.desc": "Erneutes Drücken der Sprungtaste in der Luft gewährt einen kraftvollen zweiten Sprung nach vorn mit Wind-Burst-Effekten.",
    "features.wand.title": "🪄 Windstab",
    "features.wand.desc": "Verschieße zielgerichtete Windladungen mit Abklingzeit und Haltbarkeit, um Mobs zurückzustoßen oder Mechanismen auszulösen.",
    "features.peartree.title": "🍐 Birnenbäume & Früchte",
    "features.peartree.desc": "Finde Birnenbäume in Wäldern und Ebenen, baue Birnenholz, Blätter und Setzlinge an und ernte saftige, nahrhafte Birnen.",
    "features.multiplayer.title": "🌐 Client & Server",
    "features.multiplayer.desc": "Volle Unterstützung für Singleplayer-Welten, LAN-Multiplayer und dedizierte Fabric-Server.",
    "features.tech.title": "⚡ Java 25 & Mojang Mappings",
    "features.tech.desc": "Natives Java 25 mit offiziellen Mappings und solider, 100% testgetriebener (TDD) Codebasis.",

    "install.title": "Einfache Installation",
    "install.subtitle": "In nur 3 einfachen Schritten startklar für dein nächstes Abenteuer.",
    "install.step1.title": "Fabric Loader & API installieren",
    "install.step1.desc": "Lade den offiziellen Fabric Installer für Minecraft 26.2 herunter und installiere die passende <strong>Fabric API</strong>.",
    "install.step1.btn": "Fabric Installer herunterladen &rarr;",
    "install.step2.title": "WorldExplorerMod herunterladen",
    "install.step2.desc": "Lade die fertige <code class=\"code-badge\">mc-3ver-0.2.1.jar</code> aus den GitHub Releases herunter.",
    "install.step2.btn": "Zu den Releases &rarr;",
    "install.step3.title": "In den Mods-Ordner verschieben",
    "install.step3.desc": "Platziere die <code class=\"code-badge\">.jar</code>-Datei im Minecraft <code class=\"code-badge\">mods</code>-Ordner deines Systems:",
    "install.copy": "Kopieren",
    "install.copied": "Kopiert! ✓",

    "specs.title": "Technische Spezifikationen",
    "specs.subtitle": "Alles auf einen Blick für Modpack-Ersteller und Entwickler.",
    "specs.th.component": "Komponente",
    "specs.th.version": "Version / Anforderung",
    "specs.th.note": "Hinweis",
    "specs.row.mc": "Minecraft Version",
    "specs.row.loader": "Mod Loader",
    "specs.row.api": "Fabric API",
    "specs.row.java": "Java Runtime",
    "specs.row.modid": "Mod ID",
    "specs.row.license": "Lizenz",
    "specs.note.mc": "Java Edition",
    "specs.note.loader": "Empfohlen: 0.19.3+",
    "specs.note.api": "Erforderliche Basis-Bibliothek",
    "specs.note.java": "z. B. Temurin / Microsoft OpenJDK",
    "specs.note.modid": "Eindeutiger Namespace",
    "specs.note.license": "Frei für Modpacks & Weiterentwicklung",

    "faq.title": "Häufig gestellte Fragen (FAQ)",
    "faq.subtitle": "Antworten auf die wichtigsten Fragen rund um WorldExplorerMod.",
    "faq.q1": "Funktioniert die Mod mit Forge oder NeoForge?",
    "faq.a1": "WorldExplorerMod ist nativ für den <strong>Fabric Mod Loader</strong> gebaut. Mit Kompatibilitätslayern wie Sinytra Connector kann sie in bestimmten Setups laufen, empfohlen wird jedoch die Verwendung von Fabric.",
    "faq.q2": "Darf ich WorldExplorerMod in meinem Modpack verwenden?",
    "faq.a2": "Ja! WorldExplorerMod steht unter der <strong>MIT Lizenz</strong> und kann frei in öffentlichen oder privaten Modpacks auf Modrinth, CurseForge und GitHub eingebunden werden.",
    "faq.q3": "Wie kompiliere ich die Mod aus dem Quellcode selbst?",
    "faq.a3": "Klone das Repository und führe im Terminal einfach <code>./gradlew build</code> (bzw. <code>.\\gradlew.bat build</code> auf Windows) aus. Das fertige JAR findest du anschließend im Verzeichnis <code>build/libs/</code>.",

    "footer.tagline": "Open-Source Minecraft Java Mod",
    "footer.repo": "GitHub Repository",
    "footer.releases": "Releases",
    "footer.issues": "Issue Tracker",
    "footer.license": "MIT Lizenz",
    "footer.disclaimer": "&copy; 2026 WorldExplorerMod (MC 3ver). Kein offizielles Minecraft-Produkt. Nicht von Mojang oder Microsoft genehmigt oder damit verbunden."
  },
  en: {
    "page.title": "WorldExplorerMod — Minecraft 26.2 Fabric Mod",
    "page.description": "Official website and documentation for the Minecraft Java mod WorldExplorerMod (Fabric 26.2, Java 25).",
    "nav.features": "Features",
    "nav.install": "Installation",
    "nav.specs": "Specs",
    "nav.faq": "FAQ",
    "nav.download": "Download",
    
    "hero.badge": "Minecraft 26.2 • Fabric • Java 25",
    "hero.title": "Modular Minecraft Adventure with <span class=\"gradient-text\">WorldExplorerMod</span>",
    "hero.description": "A blazing-fast, modern mod for Minecraft Java Edition 26.2, built on top of the Fabric Mod Loader with full Java 25 support.",
    "hero.btn.download": "Download Latest Version",
    "hero.btn.install": "View Guide",
    "hero.stat.mc": "Minecraft Version",
    "hero.stat.loader": "Mod Loader",
    "hero.stat.runtime": "LTS Runtime",
    "hero.stat.license": "Open Source",

    "features.title": "Highlights & Features",
    "features.subtitle": "Exciting gameplay expansions and modern architecture.",
    "features.flames.title": "🔥 Transport Flames",
    "features.flames.desc": "Place craftable Transport Flames (1 torch + 4 cobblestone), receive dyed teleport maps, and channel anytime to safely return across dimensions.",
    "features.jump.title": "💨 Double Jump",
    "features.jump.desc": "Pressing the jump key in mid-air unleashes a powerful forward boost accompanied by authentic wind-burst particle effects.",
    "features.wand.title": "🪄 Wind Wand",
    "features.wand.desc": "Shoot targeted wind charges with cooldown and durability to knock back mobs or trigger mechanisms from afar.",
    "features.peartree.title": "🍐 Pear Trees & Fruit",
    "features.peartree.desc": "Discover pear trees across forests and plains, cultivate pear wood, leaves and saplings, and harvest delicious, nourishing pears.",
    "features.multiplayer.title": "🌐 Client & Server",
    "features.multiplayer.desc": "Full support for singleplayer worlds, LAN multiplayer, and dedicated Fabric servers.",
    "features.tech.title": "⚡ Java 25 & Mojang Mappings",
    "features.tech.desc": "Native Java 25 with official mappings and a solid, 100% test-driven (TDD) codebase.",

    "install.title": "Simple Installation",
    "install.subtitle": "Ready for your next adventure in just 3 easy steps.",
    "install.step1.title": "Install Fabric Loader & API",
    "install.step1.desc": "Download the official Fabric Installer for Minecraft 26.2 and install the matching <strong>Fabric API</strong>.",
    "install.step1.btn": "Download Fabric Installer &rarr;",
    "install.step2.title": "Download WorldExplorerMod",
    "install.step2.desc": "Download the release <code class=\"code-badge\">mc-3ver-0.2.1.jar</code> from GitHub Releases or via Modrinth / CurseForge.",
    "install.step2.btn": "Go to Releases &rarr;",
    "install.step3.title": "Move to Mods Folder",
    "install.step3.desc": "Place the <code class=\"code-badge\">.jar</code> file in your system's Minecraft <code class=\"code-badge\">mods</code> folder:",
    "install.copy": "Copy",
    "install.copied": "Copied! ✓",

    "specs.title": "Technical Specifications",
    "specs.subtitle": "Everything at a glance for modpack creators and developers.",
    "specs.th.component": "Component",
    "specs.th.version": "Version / Requirement",
    "specs.th.note": "Note",
    "specs.row.mc": "Minecraft Version",
    "specs.row.loader": "Mod Loader",
    "specs.row.api": "Fabric API",
    "specs.row.java": "Java Runtime",
    "specs.row.modid": "Mod ID",
    "specs.row.license": "License",
    "specs.note.mc": "Java Edition",
    "specs.note.loader": "Recommended: 0.19.3+",
    "specs.note.api": "Required base library",
    "specs.note.java": "e.g. Temurin / Microsoft OpenJDK",
    "specs.note.modid": "Unique namespace",
    "specs.note.license": "Free for modpacks & development",

    "faq.title": "Frequently Asked Questions (FAQ)",
    "faq.subtitle": "Answers to the most common questions about WorldExplorerMod.",
    "faq.q1": "Does this mod work with Forge or NeoForge?",
    "faq.a1": "WorldExplorerMod is natively built for the <strong>Fabric Mod Loader</strong>. It may run with compatibility layers like Sinytra Connector, but Fabric is recommended for the best stability.",
    "faq.q2": "Can I include WorldExplorerMod in my modpack?",
    "faq.a2": "Yes! WorldExplorerMod is licensed under the <strong>MIT License</strong> and can be freely included in public or private modpacks on Modrinth, CurseForge, and GitHub.",
    "faq.q3": "How do I compile the mod from source code?",
    "faq.a3": "Clone the repository and run <code>./gradlew build</code> (or <code>.\\gradlew.bat build</code> on Windows) in your terminal. You'll find the finished JAR in <code>build/libs/</code>.",

    "footer.tagline": "Open-Source Minecraft Java Mod",
    "footer.repo": "GitHub Repository",
    "footer.releases": "Releases",
    "footer.issues": "Issue Tracker",
    "footer.license": "MIT License",
    "footer.disclaimer": "&copy; 2026 WorldExplorerMod (MC 3ver). Not an official Minecraft product. Not approved by or associated with Mojang or Microsoft."
  }
};

let currentLanguage = 'de';

document.addEventListener('DOMContentLoaded', () => {
  initLanguageSwitcher();
  initTabs();
  initCopyButtons();
  initReleaseVersion();
  initNavbarScroll();
});

// Language Switcher Logic
function initLanguageSwitcher() {
  const savedLang = localStorage.getItem('worldexplorer_lang');
  const browserLang = navigator.language && navigator.language.startsWith('de') ? 'de' : 'en';
  const initialLang = savedLang || browserLang;

  setLanguage(initialLang);

  const langButtons = document.querySelectorAll('.lang-btn');
  langButtons.forEach(btn => {
    btn.addEventListener('click', () => {
      const selectedLang = btn.dataset.lang;
      if (selectedLang && selectedLang !== currentLanguage) {
        setLanguage(selectedLang);
      }
    });
  });
}

function setLanguage(lang) {
  if (!TRANSLATIONS[lang]) return;
  currentLanguage = lang;
  localStorage.setItem('worldexplorer_lang', lang);

  document.documentElement.lang = lang;

  // Update button active state
  const langButtons = document.querySelectorAll('.lang-btn');
  langButtons.forEach(btn => {
    const isActive = btn.dataset.lang === lang;
    btn.classList.toggle('active', isActive);
    btn.setAttribute('aria-pressed', isActive ? 'true' : 'false');
  });

  // Apply translations
  const dict = TRANSLATIONS[lang];

  // Text content
  document.querySelectorAll('[data-i18n]').forEach(el => {
    const key = el.dataset.i18n;
    if (dict[key] !== undefined) {
      el.textContent = dict[key];
    }
  });

  // HTML content (for elements containing nested markup like <strong>, <span>, <code>)
  document.querySelectorAll('[data-i18n-html]').forEach(el => {
    const key = el.dataset.i18nHtml;
    if (dict[key] !== undefined) {
      el.innerHTML = dict[key];
    }
  });

  // Title and meta description
  if (dict["page.title"]) document.title = dict["page.title"];
  const metaDesc = document.querySelector('meta[name="description"]');
  if (metaDesc && dict["page.description"]) {
    metaDesc.setAttribute('content', dict["page.description"]);
  }
}

// OS Tab Switching
function initTabs() {
  const tabButtons = document.querySelectorAll('.tab-btn');
  const tabPanes = document.querySelectorAll('.tab-pane');

  tabButtons.forEach(button => {
    button.addEventListener('click', () => {
      const targetTab = button.dataset.tab;

      tabButtons.forEach(btn => btn.classList.remove('active'));
      tabPanes.forEach(pane => pane.classList.remove('active'));

      button.classList.add('active');
      const activePane = document.getElementById(`tab-${targetTab}`);
      if (activePane) {
        activePane.classList.add('active');
      }
    });
  });
}

// Copy to Clipboard
function initCopyButtons() {
  const copyButtons = document.querySelectorAll('.copy-btn');

  copyButtons.forEach(button => {
    button.addEventListener('click', async () => {
      const textToCopy = button.dataset.clipboard;
      if (!textToCopy) return;

      try {
        await navigator.clipboard.writeText(textToCopy);
        const copiedText = TRANSLATIONS[currentLanguage]["install.copied"] || 'Kopiert! ✓';
        const originalText = button.textContent;
        button.textContent = copiedText;
        button.style.color = '#10b981';
        button.style.borderColor = '#10b981';

        setTimeout(() => {
          button.textContent = TRANSLATIONS[currentLanguage]["install.copy"] || 'Kopieren';
          button.style.color = '';
          button.style.borderColor = '';
        }, 2000);
      } catch (err) {
        console.error('Kopieren fehlgeschlagen:', err);
      }
    });
  });
}

// Fetch Latest Release Version from GitHub API
async function initReleaseVersion() {
  const versionTag = document.getElementById('releaseVersionTag');
  if (!versionTag) return;

  try {
    const response = await fetch('https://api.github.com/repos/the3ver/mc-3ver/releases/latest');
    if (response.ok) {
      const data = await response.json();
      if (data.tag_name) {
        versionTag.textContent = data.tag_name;
      }
    }
  } catch (e) {
    // Graceful fallback to v0.2.0
    console.debug('Konnte Release-Version nicht von GitHub laden:', e);
  }
}

// Navbar Scroll Blur Effect
function initNavbarScroll() {
  const navbar = document.getElementById('navbar');
  if (!navbar) return;

  window.addEventListener('scroll', () => {
    if (window.scrollY > 20) {
      navbar.style.borderBottomColor = 'rgba(255, 255, 255, 0.12)';
      navbar.style.boxShadow = '0 10px 30px rgba(0, 0, 0, 0.6)';
    } else {
      navbar.style.borderBottomColor = 'rgba(255, 255, 255, 0.08)';
      navbar.style.boxShadow = 'none';
    }
  });
}
