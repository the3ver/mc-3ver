# WorldExplorerMod (mc-3ver) – Agent Guidelines

Modulare Minecraft Java Edition Modifikation für Minecraft 26.3 basierend auf Fabric Mod Loader und Java 25.

## Tech Stack
- **Spiel:** Minecraft 26.3 (Java Edition)
- **Mod Loader:** Fabric Loader (>= 0.20.0) + Fabric API
- **Sprache/Runtime:** Java 25
- **Build-System:** Gradle (Wrapper `gradlew` / `gradlew.bat`)

## Commands
- Prüfen (Format, Error Prone, Tests, Architektur- und Ressourcentests, Semgrep, knappe Ausgabe): `bash scripts/check.sh`. Läuft lokal auch als Claude-Stop-Hook.
- Projektregeln stehen in `.semgrep/rules.yml`. Funde beheben; nur begründete Ausnahmen mit `// nosemgrep: <regel-id> <Grund>`.
- Formatieren: `./gradlew spotlessApply` (palantir-java-format). Lokal formatiert ein Claude-Hook jede bearbeitete Java-Datei.
- Teststärke messen: `bash scripts/mutation-report.sh --run` (PIT auf `*Logic`, ca. 1 min). Überlebende Mutanten zeigen Verhalten, das kein Test absichert.
- Generierte Daten: `./gradlew runDatagen` schreibt nach `src/main/generated` (Provider in `src/datagen/java`). Diese JSONs nie von Hand ändern, sondern den Provider; Ausgabe mitcommitten, CI prüft den Gleichstand.
- Version setzen: `bash scripts/bump-version.sh X.Y.Z` (beim Release mit `--docs`). Einzige Quelle ist `gradle.properties`.
- Build: `./gradlew build`
- Run Client: `./gradlew runClient`
