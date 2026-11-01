# WorldExplorerMod (MC 3ver) 🎮

[![Build Mod](https://github.com/the3ver/mc-3ver/actions/workflows/build.yml/badge.svg)](https://github.com/the3ver/mc-3ver/actions/workflows/build.yml)
[![GitHub Release](https://img.shields.io/github/v/release/the3ver/mc-3ver?color=emerald)](https://github.com/the3ver/mc-3ver/releases)
[![Modrinth](https://img.shields.io/badge/Modrinth-worldexplorermod-00AF5C?logo=modrinth)](https://modrinth.com/mod/worldexplorermod)
[![CurseForge](https://img.shields.io/badge/CurseForge-1673253-f16436?logo=curseforge)](https://www.curseforge.com/minecraft/mc-mods/worldexplorermod)
[![Minecraft](https://img.shields.io/badge/Minecraft-26.3-blue)](https://fabricmc.net/)
[![Fabric](https://img.shields.io/badge/Loader-Fabric-lightgrey)](https://fabricmc.net/)
[![Java](https://img.shields.io/badge/Java-25-orange)](https://adoptium.net/)
[![License](https://img.shields.io/badge/License-MIT-green.svg)](LICENSE)

**WorldExplorerMod** ist eine moderne, modulare Minecraft Java Edition Modifikation für Minecraft **26.3**, basierend auf dem **Fabric Mod Loader** und **Java 25**.

🌐 **Website & Dokumentation:** [the3ver.github.io/mc-3ver](https://the3ver.github.io/mc-3ver/)  
🟢 **Modrinth:** [modrinth.com/mod/worldexplorermod](https://modrinth.com/mod/worldexplorermod)  
🔥 **CurseForge:** [curseforge.com/minecraft/mc-mods/worldexplorermod](https://www.curseforge.com/minecraft/mc-mods/worldexplorermod)

---

## 📋 Übersicht & Voraussetzungen

- **Minecraft Version:** `26.3` (Java Edition)
- **Mod Loader:** `Fabric Loader` (>= 0.19.0)
- **Erforderliche Abhängigkeiten:** `Fabric API` (für 26.3) und `TerraBlender` (26.3.0.0.9 oder neuer, für das Biom Birnenhain)
- **Java Runtime:** `Java 25 (LTS)`
- **Mapping:** Unobfuscated (Native Identifiers)
- **Lizenziert unter:** MIT License

---

## 📦 Schritt-für-Schritt Installationsanleitung

### Variante A: Standard Minecraft Launcher (Offiziell)

#### Schritt 1: Fabric Loader installieren
1. Öffne die Website [fabricmc.net/use/installer](https://fabricmc.net/use/installer/) und lade den **Universal (.jar)** oder **Windows (.exe)** Installer herunter.
2. Starte den Installer:
   - Wähle **Minecraft Version:** `26.3`
   - Wähle **Loader Version:** `0.19.3` oder höher
   - Klicke auf **Installieren**.
3. Es wird automatisch ein neues Profil namens `fabric-loader-26.3` im Minecraft Launcher angelegt.

#### Schritt 2: Fabric API und TerraBlender herunterladen
1. Besuche [Modrinth Fabric API](https://modrinth.com/mod/fabric-api/versions?g=26.3) oder [CurseForge](https://www.curseforge.com/minecraft/mc-mods/fabric-api/files).
2. Lade die passende `.jar`-Datei für **Minecraft 26.3** herunter (z. B. `fabric-api-0.165.0+26.3.jar`).
3. Lade **TerraBlender** für Fabric und Minecraft 26.3 von [Modrinth](https://modrinth.com/mod/terrablender/versions?g=26.3&l=fabric) herunter (z. B. `TerraBlender-fabric-26.3-26.3.0.0.9.jar`).

#### Schritt 3: WorldExplorerMod herunterladen
1. Gehe zu den [GitHub Releases von WorldExplorerMod](https://github.com/the3ver/mc-3ver/releases/latest).
2. Lade die Datei `mc-3ver-0.10.0.jar` herunter.

#### Schritt 4: Dateien in den Mods-Ordner legen
1. Öffne das Minecraft-Verzeichnis auf deinem Computer:
   - **Windows:** Drücke `Win + R`, tippe `%appdata%\.minecraft` ein und drücke Enter.
   - **macOS:** Öffne den Finder, drücke `Cmd + Shift + G`, tippe `~/Library/Application Support/minecraft` ein.
   - **Linux:** Navigiere zu `~/.minecraft`.
2. Öffne den Ordner `mods` (falls er noch nicht existiert, erstelle einfach einen neuen Ordner mit dem Namen `mods`).
3. Kopiere die **`fabric-api-...jar`**, die **`TerraBlender-fabric-...jar`** und die **`mc-3ver-0.10.0.jar`** in diesen `mods`-Ordner.

#### Schritt 5: Spiel starten
1. Starte den offiziellen **Minecraft Launcher**.
2. Wähle unten links das Profil **fabric-loader-26.3** aus.
3. Klicke auf **Spielen** – fertig!

---

### Variante B: Custom Launcher (Prism Launcher, Modrinth App, CurseForge)

1. **Neue Instanz erstellen:**
   - Wähle Minecraft Version: `26.3`
   - Wähle Modloader: `Fabric` (aktuelle Version)
2. **Mods hinzufügen:**
   - Installiere **Fabric API** und **TerraBlender** über den integrierten Mod-Download-Manager des Launchers.
   - Ziehe die heruntergeladene `mc-3ver-0.10.0.jar` per Drag & Drop in den Mods-Bereich der Instanz.
3. **Instanz starten.**

---

### Variante C: Dedizierter Server (Multiplayer)

1. Richte einen Minecraft 26.3 Fabric-Server ein.
2. Platziere `fabric-api-...jar`, `TerraBlender-fabric-...jar` und `mc-3ver-0.10.0.jar` im `mods/`-Ordner auf dem Server. Spieler brauchen TerraBlender auch im Client.
3. Starte den Server mit `java -Xmx4G -jar fabric-server-launch.jar nogui`.

---

## ✨ Features

* **💨 Doppelsprung & Dreifachsprung (Air Jumps):**
  * Drücke die Leertaste (Jump) erneut mitten im Sprung / in der Luft, um einen zweiten Sprung mit Schwung nach vorne und Wind-Burst-Partikeln auszuführen.
  * Unter Einfluss des **Sprungkraft-Effekts** (z. B. durch die Entdeckerbirne) ist sogar ein kraftvoller **Dreifachsprung** möglich!
* **🍐 Goldene Birne (Entdeckerbirne):**
  * **Verzehreffekte:** Sprungkraft II (ermöglicht Dreifachsprung!), Tempo II (60s) und Sanfter Fall I (45s).
  * **Eigenschaften:** Sättigung 1.2, 4 Hungerpunkte, immer essbar (`alwaysEdible`).
  * **Crafting:** 8x Goldbarren + 1x Birne (geformt oder formlos).
* **🔥 Transportflammen & Teleport-Karten:**
  * **Rezept:** 1 Fackel + 4 Bruchstein in der Werkbank (oder formlos).
  * **Platzieren:** Beim Aufstellen einer Transportflamme erhält der Spieler eine verknüpfte, zufällig gefärbte **Transportkarte** ins Inventar.
  * **Teleportation:** Halte die Karte mit Rechtsklick gedrückt (2.5s Channeling mit Portal-Sounds und Partikeln) $\rightarrow$ sicherer Teleport zurück zur Flamme mit 30s Cooldown.
  * **Dimensionsübergreifend:** Funktioniert nahtlos aus der Overworld, dem Nether und dem End.
  * **Name beim Aufstellen:** Nach dem Platzieren fragt ein Eingabefeld nach dem Namen der Flamme; er erscheint auf der Karte. Ohne Namen geschlossen, öffnet ein Rechtsklick mit leerer Hand das Feld erneut. Der erste Name ist endgültig, danach nur noch Umbenennen der Karte am Amboss.
  * **Karte nachholen:** Karte verloren? Mit leerer Hand 1,5s Rechtsklick auf die Flamme gedrückt halten $\rightarrow$ neue verknüpfte Karte, gratis. Wer schon eine Karte für die Flamme trägt (auch im Beutel), bekommt keine zweite.
  * **Intelligentes Relinking:** Wird eine Flamme versetzt, wird die alte erloschene Karte im Inventar (oder Beutel) automatisch auf den neuen Standort aktualisiert, statt nutzlose Duplikate zu erzeugen.
  * **Beutel-kompatibel (Bundles):** Stapelgröße 64 &ndash; bis zu 64 Karten passen übersichtlich in einen einzigen Beutel.
* **⚡ Kettenblitzstab (Lightning Wand):**
  * **Kettenblitz:** Visiert Mobs an und springt kettenartig auf bis zu 4 weitere Ziele über.
  * **Hold-to-Charge:** Halte Rechtsklick bis zu 5 Sekunden gedrückt für bis zu **24,0 Schaden (12 Herzen, 3x Schaden)** mit ansteigendem Knistern und Funken. Haltbarkeitskosten: 1 bis 5 Punkte.
  * **Mob-Interaktionen:** Blitze verwandeln Creeper in **geladene Creeper**, Schweine in Zombified Piglins, Villager in Hexen etc.
  * **Block-Zündung:** Zielen auf brennbare Blöcke setzt diese in Brand; entzündet Lagerfeuer/Kerzen und zündet TNT (`prime`), ohne weiterzuspringen.
  * **Crafting:** 1 Blitzableiter, 2 Amethyst-Scherben, 1 Kupferbarren, 1 Stock (geformt oder formlos).
* **🌪️ Windstab (Wind Wand):**
  * Verschiesst Windstöße, die feindliche Mobs und Projektile mit starkem Rückstoß zurückschleudern.
  * Geformt oder formlos in der Werkbank craftbar.
* **🌳 Birnbaum & Birnenholz-Set (Pear Tree & Wood Set):**
  * Neuer Baumtyp mit eigenen Holzblöcken, Blättern und optisch unterscheidbaren Setzlingen.
  * Vollwertiges Holz-Bauset im Schweizer Birnbaum-Design: Treppen, Stufen, Zäune, Tore, Türen mit Glaseinsatz, Falltüren, Druckplatten, Knöpfe sowie Birnenholzboote und Birnenholztruhenboote.
* **🌳 Biom Birnenhain (Pear Grove):**
  * Seltenes Overworld-Biom in warmem Gold-Grün mit türkisfarbenem Wasser. Außen stehen einzelne Birnbäume, dann Gruppen, innen dichter Wald mit Farnen. Die Birnbäume sind unterschiedlich groß, manche tragen Äste. Dazu Laubbüsche, Wiesenblumen, Fallobst unter den Bäumen, goldene Pollen in der Luft und selten Reste alter Gartenmauern.
  * **Herzstück:** In großen Hainen steht am tiefsten Punkt ein alter Birnbaum mit ausladender Krone, Wurzeln und Bienennestern. Seine Lichtung umgibt ein Ring großer, verzweigter Birnen. Um den Stamm wachsen weiß-rosa Birnblüten. Daneben ein kleiner See mit Seerosen und eine Quelle am Fels. Zwischen den Wurzeln steht eine Truhe mit Birnen, Setzlingen, Honigwaben und Knochenmehl, selten auch mit goldener Birne, Freundschaftsstein, Windstab oder Blitzstab.
  * **Fallobst:** Rechtsklick oder Abbauen gibt eine Birne, mit Behutsamkeit den Block zum Selbst-Hinlegen.
  * **Birnblüte:** passt in den Blumentopf und gibt rosa Farbstoff.
  * **Finden:** `/locate biome mc3ver:pear_grove` und `/locate structure mc3ver:pear_grove_heart`.
  * Benötigt **TerraBlender**.
* **🏳️ Weiße Fahne (Pazifist-Modus):**
  * Rechtsklick hisst die Fahne: Der Dauer-Effekt *Friedfertig* sorgt dafür, dass Monster dich nicht angreifen und dir keinen Schaden zufügen.
  * Greifst du selbst ein Monster an, ist der Frieden gebrochen: Effekt weg, Fahne 30 Sekunden gesperrt.
  * **Crafting:** 3 Stöcke links als Spalte, 4 weiße Wolle rechts daneben.
* **💎 Freundschaftsstein (Friendship Stone):**
  * Sneak + Rechtsklick prägt einen ungeprägten Stein auf dich. Er heißt dann 'Freundschaftsstein von <Name>' und zeigt ein Herz. Den gibst du einem Freund.
  * Wer den Stein hält, hält Rechtsklick 2,5 Sekunden gedrückt und landet neben dem Spieler, der ihn geprägt hat. Klappt in Nether und End. Abklingzeit 30 Sekunden.
  * Ein Treffer während des Kanalisierens bricht ab, auch bei der Transportkarte.
  * Der Freund bekommt eine Meldung, dass du kommst. Ist er offline, passiert nichts.
  * Solange der Stein in der Hand ist, zeigt die Aktionsleiste Entfernung und Richtung zum Freund oder seine aktuelle Dimension.
  * Sneak + Rechtsklick auf einen eigenen geprägten Stein entwertet alle Steine, die du je ausgegeben hast. Für beide Richtungen prägt jeder einen Stein und ihr tauscht.
  * **Crafting:** 4 Amethystscherben um eine Enderperle, ergibt 2 Steine. Ungeprägt stapelbar bis 16.
* **🔥 Signalfeuer (Bonfire):**
  * Hoher Holzstoß aus dicken Stämmen auf 3x3 Feldern. Eine Rauchfahne steigt 64 Blöcke auf, treibt im Wind und ist über die volle Render-Distanz sichtbar.
  * Schaufel oder Wassereimer löscht es, Feuerzeug oder Feuerkugel zündet es an. Brennend Lichtstufe 15 und Feuerschaden.
  * Rechtsklick mit einem Farbstoff auf das Feuer färbt die Rauchfahne in einer der 16 Farben. Mehrere Lager lassen sich so aus der Ferne unterscheiden.
  * **Crafting:** Lagerfeuer in der Mitte, Holzstämme in den Ecken, Kohle oder Holzkohle an den Seiten.
* **🏕️ Mobiles Entdeckerzelt (Mobile Explorer Tent):**
  * Tragbares 3x4-Zelt mit Schlafsack, Werkbank am Kopfende sowie Ofen und Truhe am Fußende.
  * Ofenrohr mit Rauch, hängende Laterne, Eingangsplane zum Öffnen und Schließen, Heringe mit Abspannleinen und ein flatternder Wimpel.
  * Der Truheninhalt wandert beim Einpacken ins Zelt-Item und kommt beim Aufbau zurück, auch nach dem Umfärben.
  * In allen 16 Wollfarben herstellbar und mit beliebigem Farbstoff im Crafting-Grid umfärbbar.
  * Stehhöhe im Mittelgang, bündiger Abschluss der Wände und passgenaue Voxel-Kollisionsformen.
  * Praktisches Einpacken per Sneak-Rechtsklick auf beliebige Zeltbestandteile.

---

## 🚀 Schnellstart für Entwickler

### 1. Repository klonen
```bash
git clone https://github.com/the3ver/mc-3ver.git
cd mc-3ver
```

### 2. Entwicklungsumgebung (IDE) einrichten

#### IntelliJ IDEA (Empfohlen)
1. Öffne IntelliJ IDEA und wähle **Open**.
2. Wähle die Datei `build.gradle` oder das Hauptverzeichnis `mc-3ver` aus und öffne es als Gradle-Projekt.
3. Stelle sicher, dass in den Project Settings (`Ctrl + Alt + Shift + S`) das Project SDK auf **Java 25** eingestellt ist.
4. Führe den Gradle-Task `genSources` aus:
   ```bash
   ./gradlew genSources
   ```

#### Visual Studio Code
1. Installiere die **Extension Pack for Java** Erweiterung.
2. Öffne den Ordner `mc-3ver`.
3. Warte, bis der Java Language Server das Gradle-Projekt synchronisiert hat.

---

## 🛠️ Wichtige Gradle-Befehle

| Befehl (Windows / PowerShell) | Befehl (Linux / macOS) | Beschreibung |
|---|---|---|
| `.\gradlew.bat runClient` | `./gradlew runClient` | Startet den Minecraft-Client mit geladener Mod |
| `.\gradlew.bat runServer` | `./gradlew runServer` | Startet einen lokalen dedizierten Minecraft-Testserver |
| `.\gradlew.bat build` | `./gradlew build` | Kompiliert das Projekt und erstellt die `.jar` in `build/libs/` |
| `.\gradlew.bat test` | `./gradlew test` | Führt alle Unit-Tests aus |

**Spielstände im Dev-Client:** `runClient` startet immer als `Frank` (siehe `loom.runs` in `build.gradle`). Seit 26.3 speichert `level.dat` den Spieler nicht mehr; `singleplayer_uuid` zeigt auf `players/data/<uuid>.dat`. Ohne festen Namen vergibt der Dev-Client bei jedem Start einen neuen `PlayerNNN`-Namen und damit eine neue Offline-UUID, und die Welt startet mit leerem Inventar am Spawn. Alte Dev-Welten umziehen: die Dateien der alten UUID unter `players/data`, `players/advancements` und `players/stats` auf die UUID von `Frank` (`6ae9f2b8-00b0-3749-a576-b5a51f6417b9`) kopieren und `singleplayer_uuid` in `level.dat` anpassen. Den Client über das Menü (Speichern und zum Titelbildschirm) beenden, nicht mit Strg-C, sonst speichert er den Spieler nicht.

---

## 🏷️ Neues Release veröffentlichen

Dank des GitHub Actions Release-Workflows wird bei jedem Git-Tag automatisch ein Release mit kompilierter JAR auf GitHub veröffentlicht:

```bash
# 1. Neuen Tag erstellen
git tag v0.4.0

# 2. Tag zu GitHub pushen
git push origin v0.4.0
```

GitHub Actions baut die Mod automatisch und hängt die `mc-3ver-0.4.0.jar` als Download an das Release an.

---

## 📁 Projektstruktur

```
mc-3ver/
├── .github/
│   └── workflows/
│       ├── build.yml               # CI-Pipeline (Build & Tests bei Push mit Java 25)
│       ├── release.yml             # Automatischer GitHub Release bei Tags (v*)
│       └── pages.yml               # Automatisches Deployment der Showcase-Website
├── docs/                           # GitHub Pages Website (HTML/CSS/JS)
├── gradle/
│   └── wrapper/                    # Gradle Wrapper Binärdateien & Konfiguration
├── src/
│   ├── main/
│   │   ├── java/
│   │   │   └── net/frank/mc3ver/
│   │   │       ├── Mc3verMod.java  # Hauptinitialisierung (Common/Server)
│   │   │       ├── WelcomeMessageHandler.java # Willkommens-Text Generator
│   │   │       └── DoubleJumpLogic.java       # Doppelsprung-Logik
│   │   └── resources/
│   │       ├── fabric.mod.json     # Fabric Mod Manifest
│   │       ├── mc3ver.mixins.json  # Mixin Konfiguration
│   │       └── assets/mc3ver/      # Texturen, Models, Icons, Sounds
│   ├── client/
│   │   ├── java/
│   │   │   └── net/frank/mc3ver/client/
│   │   │       └── Mc3verClient.java # Client-seitige Initialisierung & Doppelsprung-Handler
│   │   └── resources/
│   │       └── mc3ver.client.mixins.json
│   └── test/
│       └── java/
│           └── net/frank/mc3ver/   # Unit-Tests (TDD)
├── build.gradle                    # Fabric Loom Build-Skript
├── gradle.properties               # Versions- und Mod-Eigenschaften
├── settings.gradle                 # Repository & Projektnamen
└── LICENSE                         # MIT Lizenz
```

---

## 💬 Feedback & Issue Tracker

Hast du einen Fehler gefunden oder einen Vorschlag für ein neues Feature?

* 🐛 **Fehler melden (Bug Report):** Bitte erstelle ein Issue auf unserem [GitHub Issue Tracker](https://github.com/the3ver/mc-3ver/issues/new?template=bug_report.yml).
* ✨ **Ideen & Feature-Requests:** Reiche deine Vorschläge gerne über das [Feature Request Formular](https://github.com/the3ver/mc-3ver/issues/new?template=feature_request.yml) ein.
* 💬 **Community & Diskussionen:** Tausche dich in den [GitHub Discussions](https://github.com/the3ver/mc-3ver/discussions) mit anderen Spielern aus.

---

## 📜 Lizenz

Dieses Projekt ist unter der [MIT Lizenz](LICENSE) veröffentlicht.
