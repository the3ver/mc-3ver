# WorldExplorerMod (MC 3ver) 🎮

[![Build Mod](https://github.com/the3ver/mc-3ver/actions/workflows/build.yml/badge.svg)](https://github.com/the3ver/mc-3ver/actions/workflows/build.yml)
[![GitHub Release](https://img.shields.io/github/v/release/the3ver/mc-3ver?color=emerald)](https://github.com/the3ver/mc-3ver/releases)
[![Modrinth](https://img.shields.io/badge/Modrinth-worldexplorermod-00AF5C?logo=modrinth)](https://modrinth.com/mod/worldexplorermod)
[![CurseForge](https://img.shields.io/badge/CurseForge-1673253-f16436?logo=curseforge)](https://www.curseforge.com/minecraft/mc-mods/worldexplorermod)
[![Minecraft](https://img.shields.io/badge/Minecraft-26.2-blue)](https://fabricmc.net/)
[![Fabric](https://img.shields.io/badge/Loader-Fabric-lightgrey)](https://fabricmc.net/)
[![Java](https://img.shields.io/badge/Java-25-orange)](https://adoptium.net/)
[![License](https://img.shields.io/badge/License-MIT-green.svg)](LICENSE)

**WorldExplorerMod** ist eine moderne, modulare Minecraft Java Edition Modifikation für Minecraft **26.2**, basierend auf dem **Fabric Mod Loader** und **Java 25**.

🌐 **Website & Dokumentation:** [the3ver.github.io/mc-3ver](https://the3ver.github.io/mc-3ver/)  
🟢 **Modrinth:** [modrinth.com/mod/worldexplorermod](https://modrinth.com/mod/worldexplorermod)  
🔥 **CurseForge:** [curseforge.com/minecraft/mc-mods/worldexplorermod](https://www.curseforge.com/minecraft/mc-mods/worldexplorermod)

---

## 📋 Übersicht & Voraussetzungen

- **Minecraft Version:** `26.2` (Java Edition)
- **Mod Loader:** `Fabric Loader` (>= 0.19.0)
- **Erforderliche Abhängigkeit:** `Fabric API` (für 26.2)
- **Java Runtime:** `Java 25 (LTS)`
- **Mapping:** Unobfuscated (Native Identifiers)
- **Lizenziert unter:** MIT License

---

## 📦 Schritt-für-Schritt Installationsanleitung

### Variante A: Standard Minecraft Launcher (Offiziell)

#### Schritt 1: Fabric Loader installieren
1. Öffne die Website [fabricmc.net/use/installer](https://fabricmc.net/use/installer/) und lade den **Universal (.jar)** oder **Windows (.exe)** Installer herunter.
2. Starte den Installer:
   - Wähle **Minecraft Version:** `26.2`
   - Wähle **Loader Version:** `0.19.3` oder höher
   - Klicke auf **Installieren**.
3. Es wird automatisch ein neues Profil namens `fabric-loader-26.2` im Minecraft Launcher angelegt.

#### Schritt 2: Fabric API herunterladen
1. Besuche [Modrinth Fabric API](https://modrinth.com/mod/fabric-api/versions?g=26.2) oder [CurseForge](https://www.curseforge.com/minecraft/mc-mods/fabric-api/files).
2. Lade die passende `.jar`-Datei für **Minecraft 26.2** herunter (z. B. `fabric-api-0.158.0+26.2.jar`).

#### Schritt 3: WorldExplorerMod herunterladen
1. Gehe zu den [GitHub Releases von WorldExplorerMod](https://github.com/the3ver/mc-3ver/releases/latest).
2. Lade die Datei `mc-3ver-0.2.1.jar` herunter.

#### Schritt 4: Dateien in den Mods-Ordner legen
1. Öffne das Minecraft-Verzeichnis auf deinem Computer:
   - **Windows:** Drücke `Win + R`, tippe `%appdata%\.minecraft` ein und drücke Enter.
   - **macOS:** Öffne den Finder, drücke `Cmd + Shift + G`, tippe `~/Library/Application Support/minecraft` ein.
   - **Linux:** Navigiere zu `~/.minecraft`.
2. Öffne den Ordner `mods` (falls er noch nicht existiert, erstelle einfach einen neuen Ordner mit dem Namen `mods`).
3. Kopiere sowohl die **`fabric-api-...jar`** als auch die **`mc-3ver-0.2.1.jar`** in diesen `mods`-Ordner.

#### Schritt 5: Spiel starten
1. Starte den offiziellen **Minecraft Launcher**.
2. Wähle unten links das Profil **fabric-loader-26.2** aus.
3. Klicke auf **Spielen** – fertig!

---

### Variante B: Custom Launcher (Prism Launcher, Modrinth App, CurseForge)

1. **Neue Instanz erstellen:**
   - Wähle Minecraft Version: `26.2`
   - Wähle Modloader: `Fabric` (aktuelle Version)
2. **Mods hinzufügen:**
   - Installiere **Fabric API** über den integrierten Mod-Download-Manager des Launchers.
   - Ziehe die heruntergeladene `mc-3ver-0.2.1.jar` per Drag & Drop in den Mods-Bereich der Instanz.
3. **Instanz starten.**

---

### Variante C: Dedizierter Server (Multiplayer)

1. Richte einen Minecraft 26.2 Fabric-Server ein.
2. Platziere `fabric-api-...jar` und `mc-3ver-0.2.1.jar` im `mods/`-Ordner auf dem Server.
3. Starte den Server mit `java -Xmx4G -jar fabric-server-launch.jar nogui`.

---

## ✨ Features

* **💨 Doppelsprung (Double Jump):**
  * Drücke die Leertaste (Jump) erneut mitten im Sprung / in der Luft, um einen zweiten Sprung mit Schwung nach vorne und Wind-Burst-Partikeln auszuführen.
* **🔥 Transportflammen & Teleport-Karten:**
  * **Rezept:** 1 Fackel + 4 Bruchstein in der Werkbank (oder formlos).
  * **Platzieren:** Beim Aufstellen einer Transportflamme erhält der Spieler eine verknüpfte, zufällig gefärbte **Transportkarte** ins Inventar.
  * **Teleportation:** Halte die Karte mit Rechtsklick gedrückt (2.5s Channeling mit Portal-Sounds und Partikeln) $\rightarrow$ sicherer Teleport zurück zur Flamme mit 30s Cooldown.
  * **Dimensionsübergreifend:** Funktioniert nahtlos aus der Overworld, dem Nether und dem End.
  * **Sicher:** Prüft, ob die Flamme noch existiert und droppt die Flamme beim Abbauen sauber als Item.

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

---

## 🏷️ Neues Release veröffentlichen

Dank des GitHub Actions Release-Workflows wird bei jedem Git-Tag automatisch ein Release mit kompilierter JAR auf GitHub veröffentlicht:

```bash
# 1. Neuen Tag erstellen
git tag v0.2.0

# 2. Tag zu GitHub pushen
git push origin v0.2.0
```

GitHub Actions baut die Mod automatisch und hängt die `mc-3ver-0.2.0.jar` als Download an das Release an.

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
