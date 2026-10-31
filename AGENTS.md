# WorldExplorerMod (mc-3ver) – Agent Guidelines

Modulare Minecraft Java Edition Modifikation für Minecraft 26.3 basierend auf Fabric Mod Loader und Java 25.

## Tech Stack
- **Spiel:** Minecraft 26.3 (Java Edition)
- **Mod Loader:** Fabric Loader (>= 0.20.0) + Fabric API
- **Sprache/Runtime:** Java 25
- **Build-System:** Gradle (Wrapper `gradlew` / `gradlew.bat`)

## Commands
- Prüfen (Compile, Tests, Architekturregeln, knappe Ausgabe): `bash scripts/check.sh`. Läuft lokal auch als Claude-Stop-Hook.
- Formatieren: `./gradlew spotlessApply` (palantir-java-format). Lokal formatiert ein Claude-Hook jede bearbeitete Java-Datei.
- Version setzen: `bash scripts/bump-version.sh X.Y.Z` (beim Release mit `--docs`). Einzige Quelle ist `gradle.properties`.
- Build: `./gradlew build`
- Run Client: `./gradlew runClient`
