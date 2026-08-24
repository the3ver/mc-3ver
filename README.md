# MC 3ver 🎮

[![Build Mod](https://github.com/the3ver/mc-3ver/actions/workflows/build.yml/badge.svg)](https://github.com/the3ver/mc-3ver/actions/workflows/build.yml)
[![GitHub Release](https://img.shields.io/github/v/release/the3ver/mc-3ver?color=emerald)](https://github.com/the3ver/mc-3ver/releases)
[![Minecraft](https://img.shields.io/badge/Minecraft-1.21.4-blue)](https://fabricmc.net/)
[![Fabric](https://img.shields.io/badge/Loader-Fabric-lightgrey)](https://fabricmc.net/)
[![Java](https://img.shields.io/badge/Java-21-orange)](https://adoptium.net/)
[![License](https://img.shields.io/badge/License-MIT-green.svg)](LICENSE)

Eine moderne, modulare Minecraft Java Edition Modifikation für Minecraft **1.21.4**, basierend auf dem **Fabric Mod Loader** und **Java 21**.

🌐 **Website & Dokumentation:** [the3ver.github.io/mc-3ver](https://the3ver.github.io/mc-3ver/)

---

## 📋 Übersicht & Voraussetzungen

- **Minecraft Version:** `1.21.4`
- **Mod Loader:** `Fabric` (>= 0.16.0)
- **Java Version:** `Java 21` (z. B. Temurin / Microsoft OpenJDK 21)
- **Mapping:** Official Mojang Mappings
- **Lizenziert unter:** MIT License

---

## 📦 Installation (für Spieler)

1. Installiere den [Fabric Loader für 1.21.4](https://fabricmc.net/use/installer/).
2. Lade die [Fabric API](https://modrinth.com/mod/fabric-api) für Minecraft 1.21.4 herunter.
3. Lade die neueste `mc-3ver-1.0.0.jar` aus den [GitHub Releases](https://github.com/the3ver/mc-3ver/releases/latest) herunter.
4. Lege beide `.jar`-Dateien in deinen Minecraft-Ordner:
   - **Windows:** `%appdata%\.minecraft\mods\`
   - **macOS:** `~/Library/Application Support/minecraft/mods/`
   - **Linux:** `~/.minecraft/mods/`
5. Starte Minecraft mit dem Fabric-Profil.

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
3. Stelle sicher, dass in den Project Settings (`Ctrl + Alt + Shift + S`) das Project SDK auf **Java 21** eingestellt ist.
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
| `.\gradlew.bat genSources` | `./gradlew genSources` | Dekompiliert und generiert lesbare Minecraft-Quellcodes |

---

## 🏷️ Neues Release veröffentlichen

Dank des GitHub Actions Release-Workflows wird bei jedem Git-Tag automatisch ein Release mit kompilierter JAR auf GitHub veröffentlicht:

```bash
# 1. Neuen Tag erstellen
git tag v1.0.0

# 2. Tag zu GitHub pushen
git push origin v1.0.0
```

GitHub Actions baut die Mod automatisch und hängt die `mc-3ver-1.0.0.jar` als Download an das Release an.

---

## 📁 Projektstruktur

```
mc-3ver/
├── .github/
│   └── workflows/
│       ├── build.yml               # CI-Pipeline (Build & Tests bei Push)
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
│   │   │       └── mixin/          # Mixins für Server & Game Logic
│   │   └── resources/
│   │       ├── fabric.mod.json     # Fabric Mod Manifest
│   │       ├── mc3ver.mixins.json  # Mixin Konfiguration
│   │       └── assets/mc3ver/      # Texturen, Models, Icons, Sounds
│   └── client/
│       ├── java/
│       │   └── net/frank/mc3ver/client/
│       │       ├── Mc3verClient.java # Client-seitige Initialisierung
│       │       └── mixin/          # Client Mixins (Rendering, UI)
│       └── resources/
│           └── mc3ver.client.mixins.json
├── build.gradle                    # Fabric Loom Build-Skript
├── gradle.properties               # Versions- und Mod-Eigenschaften
├── settings.gradle                 # Repository & Projektnamen
└── LICENSE                         # MIT Lizenz
```

---

## 📜 Lizenz

Dieses Projekt ist unter der [MIT Lizenz](LICENSE) veröffentlicht.
