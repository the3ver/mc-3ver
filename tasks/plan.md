# Implementierungsplan: Birnbaum (`pear_tree`)

## 1. Übersicht & Ziel
Hinzufügen einer neuen Baumsorte: Der Birnbaum (`mc3ver:pear_tree`), inklusive essbarer Birnen (`mc3ver:pear`), vollwertigem Holzset (Stamm, entrindeter Stamm, Holz, entrindetes Holz, Bretter), Laub mit Drop-Chancen, Setzling mit Wachstumslogik, Kompostierung und Axt-Entrindungs-Integration.

## 2. Komponenten & Architektur
1. **Reine Spiellogik (`PearTreeLogic.java`):**
   - Nährwerte der Birne (`FOOD_NUTRITION`, `FOOD_SATURATION_MODIFIER`)
   - Kompostierbarkeits-Chancen für Birne, Setzling und Laub
   - Axt-Entrindungs-Mapping (`pear_log` -> `stripped_pear_log`, `pear_wood` -> `stripped_pear_wood`)
   - Laub-Dropchancen (Setzling-Rate, Birnen-Rate, Glücks-Multiplikator)
   - Brennwerte / Brenndauern für Holz, Bretter, Setzlinge
   - Testbar via JUnit 5 ohne Minecraft-Engine.
2. **Fabric/Minecraft Blöcke & Items (`PearTreeBlocks.java`, `PearTreeItems.java`):**
   - `PEAR` (Food Item)
   - `PEAR_LOG`, `STRIPPED_PEAR_LOG`, `PEAR_WOOD`, `STRIPPED_PEAR_WOOD` (RotatedPillarBlock / PillarBlock)
   - `PEAR_PLANKS` (Block)
   - `PEAR_LEAVES` (LeavesBlock)
   - `PEAR_SAPLING` (SaplingBlock)
3. **Mod-Registrierung & Fabric-Hooks (`Mc3verMod.java`):**
   - Registrierung von Blöcken & Items
   - `StrippableBlockRegistry` (Axt-Entrindung)
   - `FlammableBlockRegistry` (Entzündbarkeit von Stamm, Holz, Brettern und Laub)
   - `CompostingChanceRegistry` (Komposter-Chancen)
   - `FuelRegistry` (Brennstoff-Werte)
4. **Baumwachstum (`PearTreeGrower.java` / ConfiguredFeature):**
   - Sapling wächst mit Knochenmehl / Zeit zu einem Birnbaum heran.
5. **Assets & Rezepte:**
   - Blockstates, Models, Item-Modelle
   - Übersetzungen (`de_de.json`, `en_us.json`)
   - Rezepte (Bretter aus Stamm/Holz, Holz aus Stämmen)
   - Loot-Tables (Blöcke drop self; Laub drops Setzling & Birne).

## 3. Inkrementeller TDD-Ablauf
1. **Test 1:** Nährwerte & Sättigung der Birne.
2. **Test 2:** Kompostier-Chancen für Frucht, Laub und Setzling.
3. **Test 3:** Entrindungs-Regeln für Axt-Interaktion.
4. **Test 4:** Laub-Dropchancen für Birnen und Setzlinge.
5. **Test 5:** Brennstoff-Werte (Fuel-Ticks).
6. **Block- & Item-Registrierung:** `PearTreeBlocks`, `PearTreeItems`, `Mc3verMod`.
7. **Fabric-Hooks & WorldGen/Grower:** Strippable, Flammable, Composting, Fuel & TreeGrower.
8. **Assets, Rezepte, Loot-Tables & Tags:** JSONs & Übersetzungen.
9. **Gesamt-Validierung:** `.\gradlew.bat test` und `.\gradlew.bat build`.
