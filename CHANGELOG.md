# Changelog

Alle wichtigen Änderungen am Projekt **WorldExplorerMod (mc-3ver)** werden in dieser Datei dokumentiert.

Das Format basiert auf [Keep a Changelog](https://keepachangelog.com/de/1.0.0/) und das Projekt hält sich an [Semantic Versioning](https://semver.org/lang/de/).

## [0.9.4] - 2026-11-01

### 🔄 Geändert
* **Laubtöne:** Birnbäume unterscheiden sich deutlicher: manche Kronen sind heller und gelblicher, andere dunkler und tiefgrün.

## [0.9.3] - 2026-11-01

### ✨ Neu hinzugefügt
* **Goldene Pollen:** Im Birnenhain schweben vereinzelt goldene Pollen durch die Luft.

## [0.9.2] - 2026-11-01

### ✨ Neu hinzugefügt
* **Birnblüte:** Neue weiß-rosa Blume, die nur rund um den alten Birnbaum im Herz des Hains wächst. Bienen fliegen sie an, sie passt in einen Blumentopf und gibt rosa Farbstoff.

## [0.9.1] - 2026-11-01

### ✨ Neu hinzugefügt
* **Fallobst:** Unter den Bäumen im Birnenhain liegen vereinzelt heruntergefallene Birnen. Rechtsklick oder Abbauen gibt eine Birne, mit Behutsamkeit den Block zum Selbst-Hinlegen.

## [0.9.0] - 2026-10-31

### ✨ Neu hinzugefügt
* **Biom Birnenhain:** Seltenes neues Overworld-Biom rund um den Birnbaum, in warmem Gold-Grün mit türkisfarbenem Wasser und Meadow-Musik. Es ersetzt in einer seltenen TerraBlender-Region Forest und Flower Forest. Zu finden mit `/locate biome mc3ver:pear_grove`.
* **Zonen:** Am Rand stehen einzelne Birnbäume, weiter innen Gruppen aus 3 bis 5 Bäumen, im Inneren dichter Wald mit Farnen und Flecken aus grober Erde und Podsol. Die Zonen richten sich nach dem Abstand zum Biomrand und passen so zu jeder Hain-Form.
* **Pflanzen:** Laubbüsche aus Birnenholz und -laub. Ein eigener Blumen-Mix aus Löwenzahn, Margerite, Kornblume und Mohn, am dichtesten auf der offenen Wiese.
* **Herzstück:** Am tiefsten Punkt des Hains steht ein alter Birnbaum mit 2×2-Stamm, ausladender, lichter Krone, dicken Wurzeln und Bienennestern. Ihn umgibt ein Ring dichten Waldes. Daneben liegen ein kleiner See mit Lehmgrund, Kiesrand und Seerosen, eine Quelle am bemoosten Fels, Moos, Pilze und Trittsteine. Zu finden mit `/locate structure mc3ver:pear_grove_heart`.
* **Truhe am alten Baum:** Zwischen den Wurzeln steht eine Truhe mit Birnen, Setzlingen, Honigwaben und Knochenmehl. Selten liegt dazu eine goldene Birne, ein Freundschaftsstein, ein Windstab oder ein Blitzstab darin.
* **Gartenmauern:** Am Rand des Hains stehen selten Reste verfallener Bruchsteinmauern, die an einen alten Obstgarten erinnern.
* **Laubtöne:** Birnbäume sind je nach Ort leicht heller oder dunkler belaubt.

### 🔄 Geändert
* **Neue Abhängigkeit:** Die Mod benötigt jetzt zusätzlich **TerraBlender** (26.3.0.0.9 oder neuer), auf Client und Server.

### 🐛 Fehlerbehebungen
* **Birnenlaub:** Das Laub nutzt die graue Eichenlaub-Textur, war aber nicht eingefärbt und erschien grau. Jetzt hat es die Laubfarbe des Bioms, auch als Item im Inventar.

### 🔧 Intern
* Die Hain-Logik steckt in testbaren Klassen ohne laufendes Spiel: `GroveShapeLogic` (Tiefe, Herz), `GroveZoneLogic` (Zonen, Dichte), `OldPearTreeLogic` (Bauplan des Herzstücks), `GardenWallLogic`.
* `runServer` nimmt Konsolenbefehle über die Standardeingabe an, für Weltgenerierungs-Tests per Skript.
* Spec und Plan liegen in `tasks/birnenhain-spec.md` und `tasks/birnenhain-plan.md`.

---

## [0.8.2] - 2026-10-31

### 🐛 Fehlerbehebungen

- Die Willkommensnachricht zeigte die Version eines anderen Mods (z. B. `v5.1.20+5af7744b5d` von fabric-dimensions-v1): Alle Mods teilen sich einen Classloader, gelesen wurde die erste `fabric.mod.json`. Jetzt wird die mit der ID `mc3ver` gewählt.

## [0.8.1] - 2026-10-31

### 🐛 Fehlerbehebungen
* **Birnenholz bei Explosionen:** Birnenstämme, -holz, -bretter und Setzlinge droppten bei Explosionen immer. Jetzt geht wie bei Eiche ein Teil verloren.

### 🔧 Intern
* Die Loot-Tabellen des Birnenholz-Sets erzeugt jetzt Fabric Datagen statt sie von Hand zu pflegen.
* Neue automatische Prüfungen: einheitliche Formatierung (Spotless), Error Prone, Architekturregeln (ArchUnit), Vollständigkeit der Ressourcen, Projektregeln (Semgrep) und wöchentliches Mutation Testing (PIT). Die Version steht nur noch in `gradle.properties`.
* GitHub Actions auf Node-24-Versionen aktualisiert. `setup-gradle` bleibt auf v5, weil ab v6 das Caching proprietär ist und die Gradle-Nutzungsbedingungen verlangt. Dependabot ignoriert v6.
* Release und Modrinth-Upload lesen die Minecraft-Version aus `gradle.properties` statt fest `26.2`.
* Spotless 8.10.4, JUnit 6.1.3, ArchUnit 1.5.1, Error-Prone-Plugin 5.1.1, foojay-resolver 1.0.0.
* `runClient` startet mit festem Spielernamen `Frank`. Seit 26.3 gehört eine Einzelspieler-Welt einer festen Spieler-UUID; mit wechselnden Dev-Namen gingen Inventar und Position verloren.

---

## [0.8.0] - 2026-10-31

### ✨ Neu hinzugefügt
* **Minecraft 26.3:** Die Mod läuft jetzt auf Minecraft 26.3 mit Fabric Loader 0.20 und Fabric API 0.165. Für Minecraft 26.2 bleibt Version 0.7.1 verfügbar.

### 🐛 Fehlerbehebungen
* **Transportkarte:** Unter 26.3 zeigte die Karte im Inventar eine lila-schwarze Platzhaltertextur, weil Vanilla die Textur `filled_map_markings` entfernt hat. Das Modell nutzt jetzt nur noch `filled_map`. Ein neuer Test prüft, dass jede Textur und jedes Elternmodell, auf das unsere Modelle verweisen, existiert.

### 🔧 Intern
* Birnbaum-Worldgen auf das neue Format `worldgen/feature` umgestellt, Loot-Tables auf das neue Bedingungsformat (`condition`, `modifier`).
* Kompost und Brennstoff der Birnbaum-Items laufen über die neuen Item-Komponenten, Entrinden über `BlockTransformerHelper` der Fabric API. Brenndauern und Kompostchancen bleiben gleich.
* Gradle 9.8.2 und Loom 1.19.3. Branch `mc/26.2` hält den Stand für 26.2.

---

## [0.7.1] - 2026-10-28

### 🐛 Fehlerbehebungen
* **Dimensionsnamen übersetzt:** Der Tooltip der Transportkarte zeigte auch auf Deutsch "Overworld" oder "The End". Jetzt steht dort "Oberwelt", "Nether" oder "End". Die Aktionsleiste des Freundschaftssteins nennt die Dimension ebenfalls nur noch beim Namen, etwa "Frank: Nether".

### 🔧 Intern
* Aufgeräumt: toter Ordner `loot_tables/` und zwei ungenutzte Klassen entfernt, eine gemeinsame Basis für Transportkarte und Freundschaftsstein, eine Quelle für die 16 Farbstofffarben, Client-Start nach Features aufgeteilt, Kettenblitzstab und Birnenholz-Registrierung in kleine Schritte zerlegt. Am Spiel ändert sich dadurch nichts.

---

## [0.7.0] - 2026-10-27

### ✨ Neu hinzugefügt
* **Freundschaftsstein:** Neues Item für LAN-Partys. Sneak + Rechtsklick prägt einen ungeprägten Stein auf dich. Er heißt dann "Freundschaftsstein von <Name>" und zeigt ein Herz-Icon. Den geprägten Stein gibst du einem Freund.
* **Teleport:** Wer den Stein hält, hält Rechtsklick 2,5 Sekunden gedrückt und landet neben dem Spieler, der ihn geprägt hat. Funktioniert auch in Nether und End. Abklingzeit 30 Sekunden. Ein Treffer während des Kanalisierens bricht den Vorgang ab, das gilt jetzt auch für die Transportkarte.
* **Nachricht:** Wer den Stein geprägt hat, bekommt eine Meldung, dass jemand kommt. Ist er offline, passiert nichts.
* **Aktionsleiste:** Solange der Stein in der Hand liegt, zeigt die Aktionsleiste Entfernung in Blöcken und Himmelsrichtung zum Freund. Ist der Freund in einer anderen Dimension, steht die Dimension dort.
* **Entwerten:** Sneak + Rechtsklick auf einen eigenen geprägten Stein entwertet alle Steine, die du je ausgegeben hast.
* **Rezept:** 4 Amethystscherben um eine Enderperle ergeben 2 Steine. Ungeprägte Steine stapeln bis 16.

---

## [0.6.3] - 2026-10-25

### ✨ Neu hinzugefügt
* **Verlorene Transportkarte nachholen:** Halte mit leerer Hand Rechtsklick auf die Flamme 1,5 Sekunden gedrückt. Ein Fortschrittsbalken über der Hotbar zeigt den Stand, danach gibt es gratis eine neue, mit dieser Flamme verknüpfte Karte in der Originalfarbe. Wer schon eine Karte für die Flamme trägt, auch in einem Beutel, bekommt keine zweite.
* **Namen beim Aufstellen:** Nach dem Platzieren öffnet sich ein kleines Eingabefeld für den Namen der Flamme. Der Name steht auf der Karte und auf jeder später nachgeholten Karte. Wird das Feld ohne Namen geschlossen, öffnet ein Rechtsklick mit leerer Hand auf die Flamme es erneut. Der erste vergebene Name ist endgültig und nur der Aufsteller darf ihn setzen. Ein am Amboss benanntes Flammen-Item übernimmt seinen Namen direkt. Umbenennen geht danach wie bisher nur über die Karte am Amboss.

---

## [0.6.0] - 2026-10-25

### ✨ Neu hinzugefügt
* **Signalfeuer:** Großes Lagerfeuer auf einer 3x3-Fläche. Der Holzstoß besteht aus zwei quer liegenden Lagen unten und acht daran gelehnten Stämmen im Tipi-Stil darüber. Fünf flackernde Flammenzungen lodern oben heraus, die mittlere fast 4 Blöcke hoch. Sie leuchten nachts voll und wachsen mit der Entfernung mit. Brennend strahlt das Feuer Lichtstufe 15 aus und verursacht Feuerschaden.
* **Rauchfahne:** Einzelne Rauchwolken steigen 64 Blöcke auf, werden dabei größer und blasser und treiben im Wind zur Seite. Die Fahne bleibt über die volle Render-Distanz sichtbar. Wind wechselt langsam über einige Minuten und wirkt für alle Spieler gleich.
* **Rauchfarbe:** Rechtsklick mit einem der 16 Farbstoffe auf das Feuer färbt Fahne und Wölkchen. Der Farbstoff wird verbraucht (im Kreativmodus nicht). Die Farben sind leicht aufgehellt, damit dunkle Töne am Himmel sichtbar bleiben. Beim Abbau geht die Farbe verloren.
* **Bedienung:** Rechtsklick auf ebenen Boden mit 3x3 freien Feldern stellt das Feuer brennend auf. Schaufel oder Wassereimer löscht es, Feuerzeug oder Feuerkugel zündet es wieder an. Ein beliebiges Feld abbauen entfernt das ganze Feuer und gibt das Item zurück.
* **Rezept:** Lagerfeuer in der Mitte, Holzstämme in den Ecken, Kohle oder Holzkohle an den Seiten.

---

## [0.5.7] - 2026-10-25

### ⚖️ Balancing
* **Birnenlaub droppt wie Eichenlaub:** Setzlinge (5 %), Stöcke (2 %) und Birnen (0,5 %, wie Äpfel) fallen jetzt so oft wie bei Vanilla-Bäumen, vorher 2- bis 12-mal häufiger. Glück erhöht die Chancen wie bei Vanilla, Behutsamkeit erntet das Laub selbst.

---

## [0.5.6] - 2026-10-25

### ✨ Neu hinzugefügt
* **Weiße Fahne (Pazifist-Modus):** Rechtsklick hisst die Fahne und gibt den Dauer-Effekt *Friedfertig*. Monster nehmen dich dann nicht mehr als Ziel und verursachen keinen Schaden, auch nicht durch verirrte Pfeile oder Explosionen. Ein zweiter Rechtsklick holt die Fahne wieder ein.
* **Frieden bricht:** Greifst du mit aktivem Effekt ein Monster an, verlierst du den Schutz und kannst die Fahne 30 Sekunden lang nicht hissen. Tiere und Spieler zu schlagen bricht den Frieden nicht.
* **Rezept:** 3 Stöcke links als Spalte, 4 weiße Wolle rechts daneben.

---

## [0.5.5] - 2026-10-25

### 🐛 Fehlerbehebungen
* **Regendichtes Zeltdach:** Der dünne Dachfirst galt für Minecraft nicht als solide, deshalb regnete es auf den Schlafsack. Die Zeltplane hält Regen jetzt auf, und Regenspritzer scheinen nicht mehr von innen durch das dünne Dach.

---

## [0.5.4] - 2026-10-24

### ✨ Zelt-Verschönerungen
* **Nähte und dunkleres Dach:** Die Zeltplane zeigt pro Stoffbahn eine Falz und eine Steppnaht. Das Dach ist einen Ton dunkler als die Wände.
* **Hängende Laterne:** Unter der Dachschräge über der Werkbank hängt eine Laterne und beleuchtet das Zelt mit Lichtstufe 14.
* **Eingangsplane:** Der Eingang hat eine Plane in Zeltfarbe. Per Rechtsklick wird sie aufgerollt oder geschlossen, geschlossen hält sie Mobs draußen. Neue Zelte stehen offen.
* **Zelttruhe:** Rechts am Fußende steht eine Truhe mit 27 Plätzen, deren Deckel beim Öffnen mit Truhengeräusch aufklappt. Beim Einpacken wandert ihr Inhalt ins Zelt-Item und kommt beim Aufbau zurück. Umfärben behält den Inhalt und setzt die exakte Farbe des Farbstoffs.
* **Heringe und Abspannleinen:** An allen vier Ecken spannen Leinen die Zeltwände zu Heringen im Boden ab.
* **Wimpel:** Auf dem vorderen Giebel flattert ein Wimpel in Zeltfarbe. So findet man das Zelt von Weitem.

---

## [0.5.3] - 2026-10-24

### ✨ Neu hinzugefügt
* **Zeltofen:** An der linken Zeltwand neben dem Fußende des Schlafsacks steht ein vollwertiger Ofen. Er nutzt originale Ofentexturen mit zeltfarbener Rückwand, vergibt Erfahrung und leuchtet beim Brennen mit Lichtstufe 13.
* **Ofenrohr:** Über dem Ofen führt ein dunkles Metallrohr mit Regenkappe durch die Dachschräge nach draußen. Sobald der Ofen brennt, steigt Rauch aus dem Rohr auf.
* **Zeltabbau:** Beim Einpacken per Sneak-Rechtsklick fallen Ofeninhalt und gesammelte Erfahrung heraus. Bereits aufgestellte Zelte erhalten den Ofen erst nach erneutem Ab- und Aufbauen.

---

## [0.5.2] - 2026-10-24

### 🐛 Fehlerbehebungen
* **Schlafen im Entdeckerzelt:** Der Schlafsack erbt jetzt von `BedBlock`. Vanilla weckt Spieler beim Hinlegen nicht mehr im ersten Tick auf.
* **Heimat-Spawnpunkt:** Schlafen im Zelt verändert den Spawnpunkt weder tagsüber noch nachts. Das Fabric-API-Event `EntitySleepEvents.ALLOW_SETTING_SPAWN` unterbindet das Überschreiben und unterdrückt die Chat-Meldung vollständig.

---

## [0.5.1] - 2026-09-13

### 🔧 Wartung & Dependency-Updates
* **Fabric Loader:** Aktualisiert auf `0.19.5` (vorher `0.19.3`).
* **JUnit Jupiter:** Test-Framework auf `5.12.2` aktualisiert (vorher `5.10.2`).
* Alle Mod-Klassen, Mixins und Unittests erfolgreich mit Java 25 verifiziert.

---

## [0.5.0] - 2026-09-06

### ✨ Neu hinzugefügt & Verbesserungen
* **Mobiles Entdeckerzelt (`mc3ver:explorer_tent`):**
  * Tragbarer, vollwertiger Unterschlupf für Abenteurer und Erkunder.
  * **Integrierter Schlafsack & Werkbank:** Enthält einen bequemen Schlafsack und eine direkt nutzbare Werkbank am Kopfende.
  * **16 Farbvarianten:** In allen 16 Wollfarben herstellbar und mit beliebigem Farbstoff umfärbbar.
  * **Maßgeschneiderte 3D-Geometrie:** Optimierte Zeltplane mit Stehhöhe im Mittelgang, bündiger Rückwand, zentriertem Zelteingang und passgenauen Kollisionen.
  * **Bequemes Einpacken:** Per Sneak-Rechtsklick auf beliebige Zeltteile wird das gesamte Zelt wieder in ein einziges Item zurückverwandelt.
* **Dokumentation & Rezepte:**
  * Aktualisierung der Web-Dokumentation mit originalgetreuen Minecraft-Inventar-Icons für alle Crafting-Rezepte.
  * Neues Rezept und Feature-Kachel für das Entdeckerzelt.

---

## [0.4.3] - 2026-09-05

### ✨ Neu hinzugefügt & Behoben
* **Birnenholzboote (`mc3ver:pear_boat` & `mc3ver:pear_chest_boat`):**
  * Boote können nun wie gewohnt aus 5x Birnenholzbrettern (`mc3ver:pear_planks`) gecraftet werden.
  * Birnenholztruhenboote können durch die Kombination von Truhe und Birnenholzboot hergestellt werden.
  * Vollständige Einbindung in Entity- und Item-Tags (`#minecraft:boats`, `#minecraft:chest_boats`, `#minecraft:boat`), Ofenbrennwert (60 Sekunden) und Werfer-Verhalten (*Dispenser*).
  * Eigene Pixelart-Texturen und Client-Renderer für Boote und Truhenboote.

---

## [0.4.2] - 2026-09-05

### ✨ Verbesserungen & Fehlerbehebungen
* **Beutel-Unterstützung für Transportkarten (Bundles):**
  * Transportkarten besitzen nun eine maximale Stapelgröße von 64 (analog zu Vanilla-Karten).
  * Dadurch belegt eine Transportkarte nur noch 1/64 eines Beutels, sodass bis zu 64 Transportkarten in einem Beutel platzsparend transportiert werden können.
* **Intelligente Wiederverwendung & Relinking von Transportkarten:**
  * Beim Abbauen und erneuten Platzieren einer Transportflamme wird keine überflüssige Zweitkarte mehr erzeugt.
  * Eine im Inventar (oder in einem Beutel) vorhandene, erloschene Transportkarte wird automatisch mit der neu platzierten Flamme verknüpft (Farbe und ID bleiben erhalten).
  * Eine neue Transportkarte wird nur dann ausgegeben, wenn der Spieler noch keine oder keine erloschene Karte mehr besitzt.

---

## [0.4.1] - 2026-09-05

### ✨ Neu hinzugefügt
* **Goldene Birne / Entdeckerbirne (`mc3ver:golden_pear`):**
  * **Verzehreffekte:**
    * **Sprungkraft II** für 60 Sekunden &rarr; schaltet den **Dreifachsprung** frei!
    * **Tempo II** für 60 Sekunden für schnelles Erkunden.
    * **Sanfter Fall I** für 45 Sekunden als Schutz vor Fallschaden.
  * **Nahrungswerte:** 4 Hungerpunkte (2 Keulen), 1.2 Sättigungs-Modifikator, auch bei vollem Hungerbalken essbar (`alwaysEdible`).
  * **Item-Seltenheit:** `RARE` (türkiser Gegenstandsname).
  * **Herstellung:**
    * Geformtes Rezept: 1x Birne im Zentrum, umgeben von 8x Goldbarren.
    * Formloses Rezept: 1x Birne + 8x Goldbarren beliebig in der Werkbank.
* **Erweiterte Sprungmechanik (Dreifachsprung):**
  * Spieler unter dem Einfluss von Sprungkraft (`MobEffects.JUMP_BOOST`) können nach dem Absprung vom Boden bis zu 2 Sprünge in der Luft ausführen (Doppelsprung & Dreifachsprung).
* **Website & Rezept-Dokumentation:**
  * Neues interaktives Crafting-Grid-Rezept für die Goldene Birne auf GitHub Pages.

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
