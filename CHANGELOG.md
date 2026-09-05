# Changelog

Alle wichtigen Änderungen am Projekt **WorldExplorerMod (mc-3ver)** werden in dieser Datei dokumentiert.

Das Format basiert auf [Keep a Changelog](https://keepachangelog.com/de/1.0.0/) und das Projekt hält sich an [Semantic Versioning](https://semver.org/lang/de/).

---

## [0.4.0] - 2026-09-05

### ✨ Neu hinzugefügt
* **Vollständiges Birnenholz-Set (Schweizer Birnbaumholz-Design):**
  * **8 neue Blöcke & Gegenstände:**
    * **Birnenholztreppe (`mc3ver:pear_stairs`)**
    * **Birnenholzstufe (`mc3ver:pear_slab`)**
    * **Birnenholzzaun (`mc3ver:pear_fence`)**
    * **Birnenholzzauntor (`mc3ver:pear_fence_gate`)**
    * **Birnenholztür (`mc3ver:pear_door`)** mit stilvollem Fensterausschnitt
    * **Birnenholzfalltür (`mc3ver:pear_trapdoor`)**
    * **Birnenholzdruckplatte (`mc3ver:pear_pressure_plate`)**
    * **Birnenholzknopf (`mc3ver:pear_button`)**
  * **9 maßgeschneiderte 16x16 Pixel-Art-Texturen:**
    * Edle, warme Schweizer Birnbaum-Farbgebung für Planken, Stämme, entrindete Stämme, Türen und Falltüren.
  * **Vollwertige Holz-Integration:**
    * Alle Standard-Rezepte zum Craften (Werkbank & Steinsäge-kompatible Tags).
    * Integration in alle relevanten Minecraft Block- & Item-Tags (`wooden_doors`, `wooden_stairs`, `wooden_slabs`, `wooden_fences`, `fence_gates`, etc.).
    * Korrekte Loot-Tables und Sound-Eigenschaften.
* **Optisch differenzierter Birnbaum-Setzling (`mc3ver:pear_sapling`):**
  * Eigenständige 16x16 Pixel-Art-Textur mit der charakteristischen Blattfarbe des Birnbaums und birnenförmigem Wuchs zur eindeutigen Unterscheidung von Eichensetzlingen.
* **Website & Rezept-Dokumentation:**
  * GitHub Pages um interaktive Crafting-Grid-Rezepte für Transportflamme, Windzauberstab und Kettenblitzstab erweitert.

---

## [0.3.0] - 2026-09-03

### ✨ Neu hinzugefügt
* **Kettenblitzstab (`mc3ver:lightning_wand`):**
  * **Kettenblitz-Angriff:** Trifft das anvisierte Ziel und springt kettenartig auf bis zu 4 weitere lebende Mobs im Umkreis von 8 Blöcken über.
  * **Auflademechanik (Hold-to-Charge):**
    * Rechtsklick gedrückt halten lädt den Zauberstab bis zu 5 Sekunden (100 Ticks) auf.
    * Dynamische Funken-Partikel und ansteigender Knistersound während des Ladens.
    * Schaden skaliert von **8,0 HP** (4 Herzen) bis zu **24,0 HP** (12 Herzen, 3x Multiplikator).
    * Haltbarkeitskosten skalieren linear von 1 bis 5 Punkten.
  * **Echte Blitzeffekte (`thunderHit`):**
    * Getroffene Creeper verwandeln sich in **geladene Creeper** (Charged Creeper mit blauem Energieschild).
    * Schweine transformieren zu **Zombified Piglins**, Dorfbewohner zu **Hexen**, Pilzkühe wechseln ihre Variante.
  * **Block-Zündung & Interaktion:**
    * Trifft der Strahl einen Block statt eines Mobs, geht dieser in Flammen auf, sofern er brennbar ist (Holz, Laub, Wolle, Zäune, etc.).
    * Unangezündete Lagerfeuer und Kerzen werden entzündet.
    * TNT wird direkt scharfgestellt (`prime`).
    * Bei Blöcken springt der Blitzstrahl nicht weiter.
  * **Crafting-Rezepte:**
    * Geformt (Shaped): Blitzableiter in der Mitte oben, flankiert von 2 Amethyst-Scherben, darunter Kupferbarren und Stock.
    * Formlos (Shapeless): Blitzableiter, Kupferbarren, Stock und 2 Amethyst-Scherben in beliebiger Anordnung.
  * **Grafik & Sound:**
    * Eigene 16x16 Pixel-Art-Textur und Handheld-Modell.
    * Mehrteilige Soundkulisse (Aufladen, Donnerknall, Blitzeinschlag, Funken).
* **Windstab (`mc3ver:wind_wand`):**
  * Entfesselt gezielte Windstöße gegen Feinde mit Knall- und Wind-Partikeln.
  * Geformtes und formloses Crafting-Rezept hinzugefügt.
* **Lokalisierung:** Vollständige deutsche (`de_de.json`) und englische (`en_us.json`) Übersetzungen aller neuen Items.

---

## [0.2.1] - 2026-08-29

### 🐛 Behoben
* Stabilitätsoptimierungen für die Dimensions-Teleportation.
* Fehlerbehebung im Release-Workflow.

---

## [0.2.0] - 2026-08-29

### ✨ Neu hinzugefügt
* **Transportflammen & Teleport-Karten:**
  * Transportflamme platzieren erzeugt automatisch eine verknüpfte Teleportkarte im Inventar.
  * 2,5 Sekunden Channeling teleportiert sicher zur Flamme zurück.
  * Dimensionsübergreifend (Overworld, Nether, End).
* **Birnbaum (Pear Tree):**
  * Birnholzstämme, entrindete Stämme, Bretter, Blätter und Setzlinge.

---

## [0.1.1] - 2026-08-27

### 🔄 Geändert
* Optimierungen an den Partikel-Effekten beim Doppelsprung.

---

## [0.1.0] - 2026-08-26

### ✨ Neu hinzugefügt
* **Doppelsprung (Double Jump):** Zweiter Sprungimpuls mit Windeffekten in der Luft.
* **Willkommensnachricht:** Begrüßung beim Beitreten der Spielwelt mit Mod- und Versionsanzeige.
