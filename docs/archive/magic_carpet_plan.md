# Archivierter Plan: Fliegender Teppich (`magic_carpet`)

## 1. Übersicht & Ziel
Hinzufügen des Items `mc3ver:magic_carpet` (Fliegender Teppich), mit dem Spieler durch die Luft gleiten und schweben können (Aktivierung per Rechtsklick / Halten).

## 2. Komponenten & Architektur
1. **Reine Spiellogik (`MagicCarpetLogic.java`):**
   - Vektor- & Fluggeschwindigkeitsberechnung (Hover, Ascend, Descend, Forward Boost)
   - Fallschaden-Vermeidungsprüfung
   - Testbar via JUnit 5 ohne Minecraft-Engine.
2. **Fabric/Minecraft Item (`MagicCarpetItem.java`):**
   - `Item.Properties` (1er Stack, ggf. Haltbarkeit oder unendlich)
   - Rechtsklick toggelt Flugmodus / Hover-Zustand.
3. **Client-Tick Event (`Mc3verClient.java`):**
   - Wendet Flugbewegung auf `LocalPlayer` an, wenn der Teppich aktiv ist
   - Erzeugt Teppich-Partikeleffekte unter den Füßen des Spielers.
4. **Registry & Starter-Kit:**
   - Registrierung von `mc3ver:magic_carpet` in `ModItems.java`
   - Beigabe im Starter-Kit (`StarterKitLogic.java`, `Mc3verMod.java`).
5. **Assets & Rezepte:**
   - Client Item-Definition & Model
   - Übersetzungen (`de_de.json`, `en_us.json`)
   - Shaped & Shapeless Crafting-Rezepte.

## 3. Aufgabenliste / Tasks
- [x] Task 1: TDD - MagicCarpetLogic Konstanten (Hover, Ascend, Descend, Forward Boost)
- [x] Task 2: TDD - Schwebegeschwindigkeit im Leerlauf
- [x] Task 3: TDD - Vertikale Steuerung (Aufsteigen & Absinken)
- [x] Task 4: TDD - Horizontale Vorwärtsbewegung in Blickrichtung
- [x] Task 5: TDD - Fallschaden- und Deaktivierungsregeln
- [x] Task 6: TDD - Starter-Kit Ergänzung
- [x] Task 7: Item-Implementierung, Registry & Client-Tick-Steuerung
- [x] Task 8: Assets, Übersetzungen und Crafting-Rezepte
- [x] Task 9: Gesamt-Verifikation & Build
