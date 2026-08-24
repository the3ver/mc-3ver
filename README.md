# MC 3ver 🎮

Eine Minecraft Java Edition Modifikation, basierend auf dem **Fabric Mod Loader** für Minecraft **1.21.4**.

---

## 📋 Übersicht & Voraussetzungen

- **Minecraft Version:** `1.21.4`
- **Mod Loader:** `Fabric`
- **Java Version:** `Java 21` (z. B. Eclipse Adoptium / Temurin oder Microsoft OpenJDK 21)
- **Mapping:** Official Mojang Mappings
- **Lizenziert unter:** MIT License

---

## 🚀 Schnellstart & Entwicklung

### 1. Repository klonen
```bash
git clone https://github.com/frank/mc-3ver.git
cd mc-3ver
```

### 2. Entwicklungsumgebung (IDE) einrichten

#### IntelliJ IDEA (Empfohlen)
1. Öffne IntelliJ IDEA und wähle **Open**.
2. Wähle die Datei `build.gradle` oder das Hauptverzeichnis `mc-3ver` aus und öffne es als Gradle-Projekt.
3. Stelle sicher, dass in den Project Settings ( `Ctrl + Alt + Shift + S` ) das Project SDK auf **Java 21** eingestellt ist.
4. Führe den Gradle-Task `genSources` aus, um gemappte Minecraft-Quellen zu generieren:
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

## 📁 Projektstruktur

```
mc-3ver/
├── .github/
│   └── workflows/
│       └── build.yml               # GitHub Actions CI-Pipeline (Build & Artifacts)
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
