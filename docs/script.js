// ==========================================================================
// WorldExplorerMod (MC 3ver) — Modern Frontend & Bilingual Interactions
// ==========================================================================

const TRANSLATIONS = {
  de: {
    "page.title": "WorldExplorerMod — Minecraft 26.2 Fabric Mod",
    "page.description": "Offizielle Website und Dokumentation für die Minecraft Java Mod WorldExplorerMod (Fabric 26.2, Java 25).",
    "nav.features": "Features",
    "nav.recipes": "Rezepte",
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
    "features.lightning.title": "⚡ Kettenblitzstab",
    "features.lightning.desc": "Hold-to-Charge für bis zu 24 Schaden, springt auf bis zu 4 weitere Ziele über, lädt Creeper mit elektrischem Schild auf und setzt brennbare Blöcke in Brand.",
    "features.wand.title": "🪄 Windstab",
    "features.wand.desc": "Verschieße zielgerichtete Windladungen mit Abklingzeit und Haltbarkeit, um Mobs mit Windstößen zurückzustoßen oder Mechanismen auszulösen.",
    "features.peartree.title": "🍐 Schweizer Birnenholz & Früchte",
    "features.peartree.desc": "Vollwertiges Bauset im warmen Schweizer Birnbaum-Design, optisch eigene Setzlinge, saftige Birnen und die mächtige Goldene Birne (Entdeckerbirne).",
    "features.jump.title": "💨 Doppelsprung & Dreifachsprung",
    "features.jump.desc": "Erneutes Drücken der Sprungtaste in der Luft gewährt einen zweiten Sprung nach vorn. Mit Sprungkraft (z. B. durch die Entdeckerbirne) sogar einen Dreifachsprung!",
    "features.multiplayer.title": "🌐 Client & Server",
    "features.multiplayer.desc": "Volle Unterstützung für Singleplayer-Welten, LAN-Multiplayer und dedizierte Fabric-Server.",
    "features.tech.title": "⚡ Java 25 & Mojang Mappings",
    "features.tech.desc": "Natives Java 25 mit offiziellen Mappings und solider, 100% testgetriebener (TDD) Codebasis.",

    "recipes.title": "Rezepte & Crafting",
    "recipes.subtitle": "Offizielle Herstellungsrezepte für Werkbank und Inventar im Überblick.",
    "recipes.ingredients_label": "Zutaten:",
    "recipes.badge.both": "Geformt & Formlos",
    "recipes.flame.name": "🔥 Transportflamme",
    "recipes.flame.desc": "Errichtet einen Teleport-Anker und generiert automatisch eine verknüpfte Teleport-Karte ins Inventar.",
    "recipes.flame.result": "Transportflamme",
    "recipes.flame.ingredients": "4x Bruchstein, 1x Fackel (im Zentrum oder formlos)",
    "recipes.wind.name": "🌪️ Windzauberstab",
    "recipes.wind.desc": "Verschießt gezielte Druckluft-Stöße gegen Mobs mit starkem Rückstoß und Wind-Partikeln.",
    "recipes.wind.result": "Windzauberstab",
    "recipes.wind.ingredients": "7x Diamant, 1x Feder (Mitte), 1x Stock (Mitte unten)",
    "recipes.lightning.name": "⚡ Blitzzauberstab (Kettenblitz)",
    "recipes.lightning.desc": "Entfesselt Kettenblitze auf bis zu 5 Ziele, aufladbar (Hold-to-Charge) bis 24 Schaden, transformiert Mobs & zündet Blöcke an.",
    "recipes.lightning.result": "Kettenblitzstab",
    "recipes.lightning.ingredients": "1x Blitzableiter, 2x Amethyst-Scherbe, 1x Kupferbarren, 1x Stock",
    "recipes.golden_pear.name": "🍐 Goldene Birne (Entdeckerbirne)",
    "recipes.golden_pear.desc": "Verleiht Sprungkraft II (ermöglicht Dreifachsprung!), Tempo II für 60 Sekunden und Sanften Fall für 45 Sekunden.",
    "recipes.golden_pear.result": "Goldene Birne",
    "recipes.golden_pear.ingredients": "8x Goldbarren, 1x Birne (im Zentrum oder formlos)",
    "recipes.item.cobblestone": "Bruchstein",
    "recipes.item.torch": "Fackel",
    "recipes.item.diamond": "Diamant",
    "recipes.item.feather": "Feder",
    "recipes.item.stick": "Stock",
    "recipes.item.amethyst": "Amethyst",
    "recipes.item.lightning_rod": "Blitzableiter",
    "recipes.item.copper": "Kupfer",
    "recipes.item.gold_ingot": "Goldbarren",
    "recipes.item.pear": "Birne",
    "recipes.pear_note": "🪵 <strong>Birnenholz-Bauset:</strong> Birnenholzstämme lassen sich wie gewohnt zu 4x Birnenholzbrettern verarbeiten. Daraus können Treppen, Stufen, Zäune, Tore, Türen mit Glaseinsatz, Falltüren, Druckplatten und Knöpfe gecraftet werden.",

    "install.title": "Download & Installation",
    "install.subtitle": "Finde WorldExplorerMod auf deiner bevorzugten Plattform oder installiere sie mit 1 Klick in deinem Launcher.",
    
    "platform.modrinth.desc": "Direkt in der Modrinth App mit 1 Klick installieren oder die .jar-Datei herunterladen.",
    "platform.modrinth.btn": "Auf Modrinth ansehen &rarr;",
    "platform.curseforge.desc": "Über die CurseForge App für 1-Klick-Profile oder direkter manueller Download.",
    "platform.curseforge.btn": "Auf CurseForge ansehen &rarr;",
    "platform.github.desc": "Direkter Zugriff auf den Quellcode, alle GitHub Releases und den Issue Tracker.",
    "platform.github.btn": "GitHub Releases &rarr;",
    "platform.recommended": "Empfohlen",

    "install.launcher.title": "Methode A: 1-Klick-Installation via Launcher (Empfohlen)",
    "install.launcher.desc": "Die einfachste Methode ohne manuelles Verschieben von Dateien. Moderne Launcher laden alle Abhängigkeiten (wie Fabric API) automatisch mit herunter:",
    "install.launcher.step1": "<strong>Modrinth App / CurseForge App:</strong> Erstelle ein neues Profil für <strong>Minecraft 26.2</strong> mit dem <strong>Fabric Loader</strong>.",
    "install.launcher.step2": "Klicke im Profil auf <em>„Content hinzufügen / Mods suchen“</em> und tippe <code>WorldExplorerMod</code> ein.",
    "install.launcher.step3": "Klicke auf <strong>Installieren</strong> — fertig! Die benötigte <em>Fabric API</em> wird automatisch im Hintergrund installiert.",

    "install.manual.title": "Methode B: Manueller Download (Standard Minecraft Launcher)",
    "install.step1.title": "Fabric Loader & API installieren",
    "install.step1.desc": "Lade den offiziellen Fabric Installer für Minecraft 26.2 herunter und installiere die passende <strong>Fabric API</strong>.",
    "install.step1.btn": "Fabric Installer herunterladen &rarr;",
    "install.step2.title": "WorldExplorerMod herunterladen",
    "install.step2.desc": "Lade die Datei <code class=\"code-badge\">mc-3ver-0.4.1.jar</code> von Modrinth, CurseForge oder GitHub herunter.",
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
    "nav.recipes": "Recipes",
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
    "features.lightning.title": "⚡ Chain Lightning Wand",
    "features.lightning.desc": "Hold-to-charge for up to 24 damage, chains to up to 4 nearby living mobs, charges creepers with electric shields, and ignites flammable blocks.",
    "features.wand.title": "🪄 Wind Wand",
    "features.wand.desc": "Shoot targeted wind charges with cooldown and durability to knock back mobs or trigger mechanisms from afar.",
    "features.peartree.title": "🍐 Swiss Pear Wood & Fruit",
    "features.peartree.desc": "Complete building set in warm Swiss pear wood design, distinct saplings, juicy pears, and the powerful Golden Pear (Explorer's Pear).",
    "features.jump.title": "💨 Double Jump & Triple Jump",
    "features.jump.desc": "Pressing jump in mid-air grants a second forward leap. With Jump Boost (e.g. from the Golden Pear), you can even perform a Triple Jump!",
    "features.multiplayer.title": "🌐 Client & Server",
    "features.multiplayer.desc": "Full support for singleplayer worlds, LAN multiplayer, and dedicated Fabric servers.",
    "features.tech.title": "⚡ Java 25 & Mojang Mappings",
    "features.tech.desc": "Native Java 25 with official mappings and a solid, 100% test-driven (TDD) codebase.",

    "recipes.title": "Recipes & Crafting",
    "recipes.subtitle": "Official crafting recipes for crafting tables and inventory.",
    "recipes.ingredients_label": "Ingredients:",
    "recipes.badge.both": "Shaped & Shapeless",
    "recipes.flame.name": "🔥 Transport Flame",
    "recipes.flame.desc": "Places a teleport anchor and automatically awards a linked teleport card for safe recall.",
    "recipes.flame.result": "Transport Flame",
    "recipes.flame.ingredients": "4x Cobblestone, 1x Torch (centered or shapeless)",
    "recipes.wind.name": "🌪️ Wind Wand",
    "recipes.wind.desc": "Launches focused compressed air charges with strong knockback and wind gust particle bursts.",
    "recipes.wind.result": "Wind Wand",
    "recipes.wind.ingredients": "7x Diamond, 1x Feather (center), 1x Stick (bottom center)",
    "recipes.lightning.name": "⚡ Lightning Wand (Chain)",
    "recipes.lightning.desc": "Unleashes chain lightning across up to 5 entities, hold-to-charge up to 24 HP damage, transforms mobs & ignites blocks.",
    "recipes.lightning.result": "Lightning Wand",
    "recipes.lightning.ingredients": "1x Lightning Rod, 2x Amethyst Shard, 1x Copper Ingot, 1x Stick",
    "recipes.golden_pear.name": "🍐 Golden Pear (Explorer's Pear)",
    "recipes.golden_pear.desc": "Grants Jump Boost II (enables Triple Jump!), Speed II for 60 seconds, and Slow Falling for 45 seconds.",
    "recipes.golden_pear.result": "Golden Pear",
    "recipes.golden_pear.ingredients": "8x Gold Ingot, 1x Pear (centered or shapeless)",
    "recipes.item.cobblestone": "Cobblestone",
    "recipes.item.torch": "Torch",
    "recipes.item.diamond": "Diamond",
    "recipes.item.feather": "Feather",
    "recipes.item.stick": "Stick",
    "recipes.item.amethyst": "Amethyst",
    "recipes.item.lightning_rod": "Lightning Rod",
    "recipes.item.copper": "Copper",
    "recipes.item.gold_ingot": "Gold Ingot",
    "recipes.item.pear": "Pear",
    "recipes.pear_note": "🪵 <strong>Pear Wood:</strong> Logs can be crafted into 4x planks and further into stairs, slabs, fences, gates, doors with glass, trapdoors, pressure plates, and buttons.",

    "install.title": "Download & Installation",
    "install.subtitle": "Get WorldExplorerMod on your favorite modding platform or install with 1-click in your launcher.",

    "platform.modrinth.desc": "Install with 1-click using the Modrinth App or download the compiled .jar file.",
    "platform.modrinth.btn": "View on Modrinth &rarr;",
    "platform.curseforge.desc": "Install directly via the CurseForge App or download for manual profile setups.",
    "platform.curseforge.btn": "View on CurseForge &rarr;",
    "platform.github.desc": "Full access to open-source repository, compiled release binaries and issue tracker.",
    "platform.github.btn": "GitHub Releases &rarr;",
    "platform.recommended": "Recommended",

    "install.launcher.title": "Method A: 1-Click Installation via Launcher (Recommended)",
    "install.launcher.desc": "The easiest way without moving files manually. Modern launchers automatically download all dependencies like Fabric API:",
    "install.launcher.step1": "<strong>Modrinth App / CurseForge App:</strong> Create a new profile for <strong>Minecraft 26.2</strong> using <strong>Fabric Loader</strong>.",
    "install.launcher.step2": "Click on <em>„Add Content / Search Mods“</em> and search for <code>WorldExplorerMod</code>.",
    "install.launcher.step3": "Click <strong>Install</strong> — done! Required <em>Fabric API</em> will be installed automatically in the background.",

    "install.manual.title": "Method B: Manual Download (Default Minecraft Launcher)",
    "install.step1.title": "Install Fabric Loader & API",
    "install.step1.desc": "Download the official Fabric Installer for Minecraft 26.2 and install the matching <strong>Fabric API</strong>.",
    "install.step1.btn": "Download Fabric Installer &rarr;",
    "install.step2.title": "Download WorldExplorerMod",
    "install.step2.desc": "Download the <code class=\"code-badge\">mc-3ver-0.4.1.jar</code> file from Modrinth, CurseForge, or GitHub Releases.",
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
    // Graceful fallback to v0.4.0
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
