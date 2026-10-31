# Plan: Birnenhain Phase 1

Grundlage: `tasks/birnenhain-spec.md`. Neues Paket `net.frank.mc3ver.grove`. Logik in `*Logic`-Klassen ohne Spiel-Laufzeit (siehe `ArchitectureTest`), Registrierung und Worldgen-Anbindung in `ModGrove` und Feature-Klassen.

Jede Task endet mit grünem `bash scripts/check.sh`. Commits mit Logik erhöhen die Patch-Version (`bash scripts/bump-version.sh`).

## Task 0: Entscheidung Biom-Einbindung

- Frank wählt TerraBlender, Mixin oder Datapack (Tabelle in der Spec).
- Bei TerraBlender: exakte 26.3-Version auf Modrinth prüfen, in `build.gradle` und `fabric.mod.json` als Abhängigkeit eintragen.
- Verify: `./gradlew build` grün, `runClient` startet mit TerraBlender.

## Task 1: Vorbereitung (eigener Commit)

- Worldgen-Keys des Birnbaums (`PEAR_TREE_KEY`, Feature-JSON) so ablegen, dass das Hain-Feature sie mitnutzen kann, ohne `PearTreeSaplingGenerator` zu duplizieren.
- Keine Verhaltensänderung, Tests bleiben grün.

## Task 2: `GroveCenterLogic` – Hain-Mittelpunkte

- Rasterzelle (Startwert 24 Chunks) → ein Kandidat-Mittelpunkt pro Zelle, deterministisch aus Seed und Zelle.
- Versatz des alten Baums zum Mittelpunkt: 8 bis 20 Blöcke, deterministisch.
- `nearestCenter(seed, x, z)` liefert die Kandidaten der umliegenden Zellen.
- Tests: gleiche Eingabe → gleiches Ergebnis; Versatz immer im Bereich; Kandidat liegt in seiner Zelle.

## Task 3: `GroveZoneLogic` – Zonen und Dichte

- Abstand → Zone (Rand, Gruppen, Wald, Herz) mit den Startradien der Spec.
- Zone → Baumversuche pro Chunk, Gruppengröße, Anteil grobe Erde/Podsol.
- Kein Mittelpunkt in Reichweite → Rand.
- Tests für jede Zonengrenze, danach `scripts/mutation-report.sh --run` auf die neuen Klassen.

## Task 4: Biom-Definition und Einbindung

- `data/mc3ver/worldgen/biome/pear_grove.json`: Klima, Farben, Musik, Spawns, Carver und Grund-Features nach Vanilla-`meadow.json` von 26.3.
- Einbindung nach Task 0, Klima-Parameter neben Plains, Meadow, Forest, Gewicht für Flower-Forest-Seltenheit.
- Biom-Tags: `is_overworld`, `is_forest`, `has_structure/...` für das Herzstück.
- Sprachschlüssel `biome.mc3ver.pear_grove` (de, en), `LanguageFilesTest` grün.
- Verify: `/locate biome mc3ver:pear_grove` findet das Biom; im Spiel Farben und Musik prüfen.

## Task 5: Zonen-Baum-Feature

- Eigenes Feature `pear_grove_trees`: fragt Biom am Mittelpunkt-Kandidaten über die Biomquelle des Generators, wählt Zone über `GroveZoneLogic`, setzt das bestehende Birnbaum-Feature.
- Bodenflecken (grobe Erde, Podsol) im dichten Wald.
- Verify: im Spiel Übergang einzeln → Gruppen → dicht sichtbar; Screenshots für den PR.

## Task 6: Laubbüsche und Bodenbewuchs

- `pear_bush`: Baum-Feature mit 1 Stamm und Busch-Laub (wie Vanilla `jungle_bush`).
- Platzierte Features für Gras, Farne, Blumen-Mix nach Spec, dem Biom zuordnen.
- Verify: Datagen-Gleichstand (`runDatagen`), im Spiel sichtbar.

## Task 7: `OldPearTreeLogic` – Bauplan Herzstück

- Liefert eine Liste (relative Position, Blockrolle) für Stamm 2×2, Krone, Wurzeln, See, Kiesrand, Lehmgrund, Seerosen, Quellfels, Wasserquelle, Trittsteine, Truhe, Bienenstöcke, Moos, Pilze.
- Rollen als eigenes Enum, die Zuordnung Rolle → Block passiert außerhalb der Logik.
- Tests: Stammhöhe 12 bis 15; See-Durchmesser 5 bis 10; Truhe neben Wurzel; Pfad 8 bis 12 Blöcke; Quelle grenzt an Seewasser; kein Block doppelt belegt.

## Task 8: Herzstück-Struktur

- Strukturtyp, Strukturteil und Struktur-Set; Platzierung am Baum-Punkt aus `GroveCenterLogic`, nur im Birnenhain.
- Geländeanpassung: See in den Boden, Baum auf Bodenhöhe.
- Loot-Table `chests/pear_grove_heart` per Datagen-Provider.
- Verify: `/locate structure mc3ver:pear_grove_heart`; Truhe, Bienen, fließendes Wasser im Spiel prüfen.

## Task 9: Gartenmauern

- Kleines Feature: 3 bis 7 Blöcke Bruchstein, teils bemoost, mit Lücken, nur Rand- und Gruppen-Zone.
- Verify: selten sichtbar, nie im Wald oder am Herzstück.

## Task 10: Abschluss

- Version erhöhen (`--docs`), CHANGELOG (de, en), README und `docs/` ergänzen.
- `bash scripts/check.sh` grün, PR mit Screenshots aller Zonen und des Herzstücks.

## Offene Punkte

- Task 0: Einbindungsweg.
- Truhen-Loot: Vorschlag aus der Spec bestätigen.
- Radien, Farben, Seltenheit: Startwerte, Abstimmung im Spiel nach Task 5 und Task 8.
