# Aufgabenliste: Neuer Baumtyp – Der Birnbaum (`pear_tree`)

- [x] Task 1: TDD - PearTreeLogic Nährwerte & Nahrungseigenschaften der Birne
  - Acceptance: `PearTreeLogic.PEAR_NUTRITION` ist 4, `PearTreeLogic.PEAR_SATURATION_MODIFIER` ist 0.3f.
  - Verify: JUnit 5 Test `testPearFoodProperties()` in `PearTreeLogicTest.java` ist grün.
  - Files: `src/test/java/net/frank/mc3ver/tree/PearTreeLogicTest.java`, `src/main/java/net/frank/mc3ver/tree/PearTreeLogic.java`

- [x] Task 2: TDD - Kompostier-Wahrscheinlichkeiten (Birne, Setzling, Laub)
  - Acceptance: `PearTreeLogic.getCompostChance(...)` liefert korrekte Float-Werte (Birne: 0.65f, Laub: 0.3f, Setzling: 0.3f).
  - Verify: JUnit 5 Test `testCompostChances()` in `PearTreeLogicTest.java` ist grün.
  - Files: `src/test/java/net/frank/mc3ver/tree/PearTreeLogicTest.java`, `src/main/java/net/frank/mc3ver/tree/PearTreeLogic.java`

- [x] Task 3: TDD - Axt-Entrindungs-Mapping (Logs & Wood)
  - Acceptance: `PearTreeLogic.getStrippedBlockId(...)` mappt `pear_log` -> `stripped_pear_log` und `pear_wood` -> `stripped_pear_wood`.
  - Verify: JUnit 5 Test `testStrippedMappings()` in `PearTreeLogicTest.java` ist grün.
  - Files: `src/test/java/net/frank/mc3ver/tree/PearTreeLogicTest.java`, `src/main/java/net/frank/mc3ver/tree/PearTreeLogic.java`

- [x] Task 4: TDD - Laub-Dropchancen (Setzlinge & Birnen)
  - Acceptance: `PearTreeLogic.calculateLeafDropRoll(...)` ermittelt Drop-Chancen unter Berücksichtigung von Fortune/Glück.
  - Verify: JUnit 5 Test `testLeafDropChances()` in `PearTreeLogicTest.java` ist grün.
  - Files: `src/test/java/net/frank/mc3ver/tree/PearTreeLogicTest.java`, `src/main/java/net/frank/mc3ver/tree/PearTreeLogic.java`

- [x] Task 5: TDD - Brennwerte (Fuel-Dauer)
  - Acceptance: `PearTreeLogic.getFuelDurationTicks(...)` liefert Standard-Brennwerte (z.B. Planks 300, Sapling 100).
  - Verify: JUnit 5 Test `testFuelDuration()` in `PearTreeLogicTest.java` ist grün.
  - Files: `src/test/java/net/frank/mc3ver/tree/PearTreeLogicTest.java`, `src/main/java/net/frank/mc3ver/tree/PearTreeLogic.java`

- [x] Task 6: Block- und Item-Registrierung (`PearTreeBlocks`, `PearTreeItems`, `Mc3verMod`)
  - Acceptance: `pear`, `pear_log`, `stripped_pear_log`, `pear_wood`, `stripped_pear_wood`, `pear_planks`, `pear_leaves`, `pear_sapling` sind im Spiel registriert.
  - Verify: Mod kompiliert fehlerfrei.
  - Files: `src/main/java/net/frank/mc3ver/tree/PearTreeBlocks.java`, `src/main/java/net/frank/mc3ver/tree/PearTreeItems.java`, `src/main/java/net/frank/mc3ver/Mc3verMod.java`

- [x] Task 7: Fabric-Hooks & Integrationen
  - Acceptance: Entrindung mit Axt (`StrippableBlockRegistry`), Brennbarkeit (`FlammableBlockRegistry`), Kompostierbarkeit (`ComposterBlock.COMPOSTABLES`) registriert.
  - Verify: Registrierungsmethoden werden bei Mod-Init aufgerufen.
  - Files: `src/main/java/net/frank/mc3ver/tree/PearTreeBlocks.java`, `src/main/java/net/frank/mc3ver/Mc3verMod.java`

- [x] Task 8: TreeGrower & Sapling-Wachstumslogik
  - Acceptance: Birnensetzling lässt sich mit Knochenmehl / Zeit zu einem Baum hochziehen.
  - Verify: TreeGrower und Feature-Definitionen sind angelegt.
  - Files: `src/main/java/net/frank/mc3ver/tree/PearTreeSaplingGenerator.java`, `src/main/resources/data/mc3ver/worldgen/configured_feature/pear_tree.json`

- [x] Task 9: Assets, Modelle, Rezepte, Loot-Tables, Tags & Übersetzungen
  - Acceptance: Blockstates, Models, Item-Modelle, Crafting-Rezepte (Bretter & Holz), Loot-Tables (Log drops Log, Laub drops Sapling + Birne), Tags (`#logs`, `#leaves`, `#saplings`, `#planks`), Sprachdateien (`de_de.json`, `en_us.json`).
  - Verify: Valide JSON-Ressourcen vorhanden.
  - Files: `src/main/resources/assets/mc3ver/...`, `src/main/resources/data/mc3ver/...`, `src/main/resources/data/minecraft/tags/...`

- [x] Task 10: Gesamt-Verifikation & Build
  - Acceptance: `.\gradlew.bat test` und `.\gradlew.bat build` erfolgreich (Exit Code 0).
  - Verify: CI / Build grün.
  - Files: N/A
