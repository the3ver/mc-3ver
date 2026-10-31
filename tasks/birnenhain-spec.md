# Spec: Biom Birnenhain (`mc3ver:pear_grove`)

Eigenes Overworld-Biom rund um den Birnbaum. Außen stehen die Bäume einzeln, nach innen in Gruppen, dann als dichter Wald. Nahe der Mitte steht ein alter Birnbaum an einem kleinen See mit Quelle.

## Phasen

| Phase | Inhalt |
|---|---|
| 1 | Biom-Definition, Einbindung in die Overworld, Zonen-Verteilung der Bäume, Laubbüsche, Vanilla-Vegetation, Herzstück (alter Baum, See, Quelle, Truhe, Bienen), Gartenmauern, Trittsteine |
| 2 | Neue Blöcke und Effekte: Fallobst-Birne (Deko-Block), Birnblüten-Blume, goldene Pollenpartikel |
| 3 | Eigene Kreaturen |

Dieses Dokument legt alle Phasen fest. Der Umsetzungsplan in `birnenhain-plan.md` deckt Phase 1 ab.

## Klima und Atmosphäre

| Eigenschaft | Wert | Vorbild |
|---|---|---|
| Temperatur | 0.8 | Plains |
| Niederschlag (`downfall`) | 0.5 | zwischen Plains (0.4) und Forest (0.8) |
| Niederschlag | Regen, auf Berghöhe Schnee (Vanilla-Verhalten bei 0.8) | Plains |
| Grasfarbe | warmes Gold-Grün, fest gesetzt (Startwert `#A4B94E`) | eigener Ton |
| Laubfarbe | passend zum Gras (Startwert `#93AE47`) | eigener Ton |
| Wasserfarbe | warm-türkis (Startwert `#3FB8C4`) | heller als Vanilla `#3F76E4` |
| Wasser-Nebelfarbe | dunkleres Türkis (Startwert `#0E4A52`) | |
| Himmel, Nebel | Vanilla-Werte für Temperatur 0.8 | Plains |
| Licht | hell und offen, nur im dichten Kern Schatten durch Baumdichte | Birch Forest |
| Ambient-Sound | Vanilla, Bienen am alten Baum summen von selbst | |
| Musik | ruhige Vanilla-Tracks aus Meadow und Forest | Meadow |
| Partikel | Phase 2: wenige goldene Pollen | Cherry Grove (Blütenblätter) |

Die Farbwerte sind Startwerte und werden im Spiel abgestimmt. Die Feldnamen (seit 1.21.11 teils unter `attributes` statt `effects`) beim Umsetzen gegen das Vanilla-`meadow.json` von 26.3 abgleichen.

## Gelände

- Sanft wellig wie Meadow, ab und zu flache Senken. Die Höhe kommt aus den Klima-Parametern der Biom-Einbindung (Erosion, Weirdness), nicht aus dem Biom selbst.
- Oberfläche: Grasblock. Im dichten Kern Flecken aus grober Erde und Podsol. Moos nur am alten Baum.
- Untergrund, Höhlen, Erze: Vanilla-Overworld-Standard wie bei Meadow.

## Verbreitung

- Selten, etwa so häufig wie Flower Forest. Ein Spieler muss suchen, aber nicht jeder Seed hat einen Hain in Laufweite.
- Umsetzung (2026-10-31): In einer eigenen TerraBlender-Region (Gewicht 3, Vanilla 10) werden Forest und Flower Forest zum Birnenhain. Flower Forest allein ergab Flecken um 64×64 Blöcke, zu klein für die Zonen; Forests sind deutlich größer, das niedrige Gewicht hält den Hain selten.
- Streifen (Entscheidung 2026-10-31): Wo eine TerraBlender-Regionsgrenze durch einen Forest läuft, entsteht ein schmaler Hain-Streifen mit gerader Kante zum normalen Forest. Diese Streifen bleiben, wie sie sind. Verworfene Alternativen: nur einen Teil des Forest-Klimas ersetzen (Region mit hohem Gewicht), Regionsgröße erhöhen (Spielereinstellung), Streifen dichter bepflanzen.
- Klima-Nachbarschaft: Plains, Meadow, Forest. Dort wächst der Birnbaum schon heute.
- Der Birnbaum wächst weiter in seinen bisherigen Biomen. Der Hain ersetzt das nicht.

## Zonen

Die Baumdichte hängt von der Tiefe im Hain ab: dem Abstand zum Biomrand, gemessen über 8 Richtungen in 8er-Schritten (höchstens 64). So passen die Zonen zu jeder Hain-Form. Haine sind in der Praxis 4.000 bis 25.000 Blöcke² groß und höchstens 32 bis 64 Blöcke tief (Messung 2026-10-31).

| Zone | Tiefe (Startwert) | Bäume | Boden |
|---|---|---|---|
| Rand | unter 16 | einzeln, etwa 1 Baum pro 2 Chunks | Gras, viel offene Wiese |
| Gruppen | 16 bis unter 32 | Gruppen aus 3 bis 5 Bäumen, Lücken dazwischen | Gras, Laubbüsche |
| Dichter Wald | ab 32 | etwa 8 bis 10 Bäume pro Chunk | grobe Erde und Podsol in Flecken, Farne |
| Herz | unter 20 um den alten Baum | keine normalen Bäume | siehe Herzstück |

Das Herz liegt am tiefsten Punkt des Hains: ein Rasterpunkt (Chunk-Mitte) mit Tiefe ab 32, ohne tieferen Punkt im Umkreis von 96 Blöcken. Der alte Baum steht 6 bis 12 Blöcke daneben, damit er nicht genau in der Mitte liegt. Haine mit weniger als 32 Tiefe haben kein Herz. Schwellen sind Startwerte und werden im Spiel abgestimmt.

## Vegetation

| Element | Phase | Verteilung |
|---|---|---|
| Birnbaum (bestehendes Feature) | 1 | nach Zonen, siehe oben |
| Laubfarben-Variation: Birnenlaub mal heller, mal dunkler, je nach Position | 1 | alle Birnbäume, auch außerhalb des Hains |
| Laubbüsche (1 Birnenstamm + Birnenlaub, wie Dschungel-Büsche) | 1 | Gruppen-Zone und Waldrand |
| Kurzgras, hohes Gras | 1 | überall, wie Plains |
| Farne | 1 | dichter Wald |
| Blumen: Löwenzahn, Margerite, Kornblume, Mohn | 1 | überall, mehr am Rand als im Wald |
| Fallobst-Birne (Deko-Block) | 2 | unter Birnbäumen, vereinzelt |
| Birnblüten-Blume (weiß-rosa) | 2 | nur am alten Baum |

## Herzstück

Ein Herzstück pro Hain, als eigene Struktur.

**Alter Birnbaum**
- 2×2-Stamm aus Birnenholz, 12 bis 15 Blöcke hoch.
- Breite Krone aus Birnenlaub, deutlich breiter als der normale Birnbaum.
- Dicke Wurzeln über dem Boden, die vom Stamm wegführen.
- Bienenstöcke in der Krone, mit Bienen.
- Eine Truhe zwischen den Wurzeln, halb verdeckt.
- Moosteppich, Moosblöcke und braune Pilze zwischen den Wurzeln.

**See**
- 5 bis 10 Blöcke Durchmesser, unregelmäßiger Rand.
- Lehm am Grund, Kiesrand, Seerosen.
- Liegt direkt neben dem alten Baum.

**Quelle**
- Kleiner moosbewachsener Felsblock (Bruchstein, bemooster Bruchstein, Stein) am Seeufer.
- Aus dem Fels rinnt fließendes Wasser in den See.

**Trittsteine**
- Lückenhafter Pfad aus Steinplatten oder Pfadblöcken.
- Nur auf den letzten 8 bis 12 Blöcken vor dem alten Baum. Von außen verrät der Pfad nichts.

**Umsetzung Bauplan (Task 7, Startwerte in `OldPearTreeLogic`)**
- Krone (nach Spieltest ausladender und lichter): 5 bis 7 Äste, 4 bis 6 lang, schräg nach oben; an jedem Ende eine flache Laubwolke mit Radius 2 bis 4, am Rand fehlen 45 % der Blätter; Laub reicht höchstens 6 unter die Stammspitze.
- Wurzeln: 5 bis 6, je von einem anderen Stammblock, 3 bis 4 lang, das äußere Ende sinkt in den Boden.
- See: Radius 3 bis 4 (5 bis 9 Blöcke breit), unregelmäßiger Rand, beginnt 7 Blöcke von der Stammmitte; Mitte 2 tief.
- Quelle: Fels auf der dem Baum abgewandten Seeseite, Wasser oben auf dem Fels zum See hin.
- Bienenstöcke: 1 bis 2 direkt unter der Krone am Stamm, an gegenüberliegenden Seiten.
- Moos und Pilze im Umkreis von 5 Blöcken; Trittsteine ab 6 Blöcken, auf der dem See abgewandten Seite.

**Truhen-Loot (entschieden 2026-10-31)**
- Grundstock: Birnen, Birnensetzlinge, Honigwaben, Knochenmehl.
- Selten genau einer davon: goldene Birne, Freundschaftsstein (ungeprägt), Windstab oder Blitzstab.

## Gartenmauern

- Kurze Reste einer Bruchsteinmauer (3 bis 7 Blöcke lang, 1 bis 2 hoch), teils bemoost, mit Lücken.
- Selten, nur in der Rand- und Gruppen-Zone. Deuten einen alten Obstgarten an.

## Mobs

- Phase 1: Vanilla-Spawns wie Plains (Schaf, Schwein, Huhn, Kuh, Pferd, Kaninchen), Bienen kommen aus den Bienenstöcken.
- Phase 3: eigene Kreaturen, eigene Spec.

## Technische Entscheidungen

**Biom-Einbindung.** Fabric API 26.3 (`fabric-biome-api-v1` 20.1.1) bietet nur `NetherBiomes` und `TheEndBiomes`, keine API für Overworld-Biome. Optionen:

| Option | Vorteil | Nachteil |
|---|---|---|
| TerraBlender (Fabric-Build für 26.3, Stand Beta) | übliche Lösung, verträgt sich mit anderen Biom-Mods, Seltenheit über Region-Gewicht | neue Abhängigkeit für Client und Server, Beta-Status |
| Eigener Mixin in `OverworldBiomeBuilder` | keine Abhängigkeit | bricht bei MC-Updates, Konflikte mit anderen Biom-Mods |
| Datapack-Override von `minecraft:overworld` | kein Code | ersetzt die ganze Biom-Verteilung, unverträglich mit anderen Mods |

Entscheidung (2026-10-31): TerraBlender. Artefakt `com.github.glitchfiend:TerraBlender-fabric:26.3-26.3.0.0.9` vom Forge-Maven (`maven.minecraftforge.net`), Lizenz LGPL-3.0, braucht Fabric API. Spieler müssen TerraBlender zusätzlich installieren.

**Zonen.** Vanilla-Features kennen keinen Abstand zum Biomrand. `GroveShapeLogic` misst ihn über Biomabfragen an der Biomquelle (unabhängig davon, welche Chunks schon generiert sind) und findet das Herz als tiefsten Punkt. Die Tiefe je Chunk-Mitte wird gecacht, weil die Herz-Suche viele Nachbarn braucht. Baum-Feature und Herzstück-Struktur nutzen dieselbe Logik. Erster Ansatz mit festem Raster für Mittelpunkte und Radien 20/56/96 verworfen: Haine sind zu klein, die meisten bekamen keinen alten Baum.

**Herzstück.** Struktur mit einem im Code erzeugten Strukturteil. Der Bauplan (welcher Block wo) entsteht in einer reinen Logik-Klasse und ist ohne laufendes Spiel testbar. Keine NBT-Vorlage, weil See und Wurzeln zufällig geformt sein sollen.
