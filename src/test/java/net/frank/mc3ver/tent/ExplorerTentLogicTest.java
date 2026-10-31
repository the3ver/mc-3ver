package net.frank.mc3ver.tent;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;

public class ExplorerTentLogicTest {

    @Test
    void testDimensionsAndCoordinateTransformation() {
        assertEquals(3, ExplorerTentLogic.WIDTH, "Zeltbreite sollte 3 Blöcke sein");
        assertEquals(4, ExplorerTentLogic.LENGTH, "Zeltlänge sollte 4 Blöcke sein");
        assertEquals(2, ExplorerTentLogic.HEIGHT, "Zelthöhe sollte 2 Blöcke sein");
        assertEquals(24, ExplorerTentLogic.TOTAL_BLOCK_COUNT, "Gesamtanzahl an Blockpositionen sollte 24 sein");

        // Anchor an (100, 64, 200) mit Blickrichtung SOUTH (vorwärts = +Z)
        ExplorerTentLogic.BlockCoord originSouth =
                ExplorerTentLogic.toWorldPos(100, 64, 200, ExplorerTentLogic.Facing.SOUTH, 0, 0, 0);
        assertEquals(100, originSouth.x());
        assertEquals(64, originSouth.y());
        assertEquals(200, originSouth.z());

        // relX=0, relY=0, relZ=2 bei SOUTH
        ExplorerTentLogic.BlockCoord forwardPos =
                ExplorerTentLogic.toWorldPos(100, 64, 200, ExplorerTentLogic.Facing.SOUTH, 0, 0, 2);
        assertEquals(100, forwardPos.x());
        assertEquals(64, forwardPos.y());
        assertEquals(202, forwardPos.z());

        // Blickrichtung NORTH (vorwärts = -Z)
        ExplorerTentLogic.BlockCoord forwardNorth =
                ExplorerTentLogic.toWorldPos(100, 64, 200, ExplorerTentLogic.Facing.NORTH, 0, 0, 2);
        assertEquals(100, forwardNorth.x());
        assertEquals(64, forwardNorth.y());
        assertEquals(198, forwardNorth.z());

        // Blickrichtung EAST (vorwärts = +X)
        ExplorerTentLogic.BlockCoord forwardEast =
                ExplorerTentLogic.toWorldPos(100, 64, 200, ExplorerTentLogic.Facing.EAST, 0, 0, 2);
        assertEquals(102, forwardEast.x());
        assertEquals(64, forwardEast.y());
        assertEquals(200, forwardEast.z());

        // Blickrichtung WEST (vorwärts = -X)
        ExplorerTentLogic.BlockCoord forwardWest =
                ExplorerTentLogic.toWorldPos(100, 64, 200, ExplorerTentLogic.Facing.WEST, 0, 0, 2);
        assertEquals(98, forwardWest.x());
        assertEquals(64, forwardWest.y());
        assertEquals(200, forwardWest.z());
    }

    @Test
    void testPlacementValidation() {
        // Mock World predicate: ground (y=63) is solid, volume (y=64, 65) is replaceable
        ExplorerTentLogic.WorldView clearWorld = new ExplorerTentLogic.WorldView() {
            @Override
            public boolean isSolid(int x, int y, int z) {
                return y < 64; // Y=63 und darunter ist fest
            }

            @Override
            public boolean isReplaceable(int x, int y, int z) {
                return y >= 64; // Y=64 und darüber ist Luft/Gras
            }
        };

        // 1. Erfolgreicher Aufbau auf freier, ebener Fläche
        ExplorerTentLogic.PlacementResult successResult =
                ExplorerTentLogic.validatePlacement(100, 64, 200, ExplorerTentLogic.Facing.NORTH, clearWorld);
        assertEquals(
                ExplorerTentLogic.PlacementStatus.SUCCESS,
                successResult.status(),
                "Auf ebener freier Fläche sollte Platzierung erfolgreich sein");

        // 2. Fehler: Ungleichmäßiger Boden (z.B. Loch bei relX=1, relZ=2, y=63)
        ExplorerTentLogic.WorldView unevenGroundWorld = new ExplorerTentLogic.WorldView() {
            @Override
            public boolean isSolid(int x, int y, int z) {
                ExplorerTentLogic.BlockCoord hole =
                        ExplorerTentLogic.toWorldPos(100, 64, 200, ExplorerTentLogic.Facing.NORTH, 1, -1, 2);
                if (x == hole.x() && y == hole.y() && z == hole.z()) {
                    return false; // Loch im Boden
                }
                return y < 64;
            }

            @Override
            public boolean isReplaceable(int x, int y, int z) {
                return y >= 64;
            }
        };
        ExplorerTentLogic.PlacementResult unevenResult =
                ExplorerTentLogic.validatePlacement(100, 64, 200, ExplorerTentLogic.Facing.NORTH, unevenGroundWorld);
        assertEquals(
                ExplorerTentLogic.PlacementStatus.UNEVEN_GROUND,
                unevenResult.status(),
                "Loch im Boden sollte UNEVEN_GROUND liefern");

        // 3. Fehler: Blockierung im Zeltvolumen (z.B. Baumstamm bei relX=0, relZ=1, y=64)
        ExplorerTentLogic.WorldView obstructedWorld = new ExplorerTentLogic.WorldView() {
            @Override
            public boolean isSolid(int x, int y, int z) {
                return y < 64;
            }

            @Override
            public boolean isReplaceable(int x, int y, int z) {
                ExplorerTentLogic.BlockCoord obst =
                        ExplorerTentLogic.toWorldPos(100, 64, 200, ExplorerTentLogic.Facing.NORTH, 0, 0, 1);
                if (x == obst.x() && y == obst.y() && z == obst.z()) {
                    return false; // Fester Block im Weg
                }
                return y >= 64;
            }
        };
        ExplorerTentLogic.PlacementResult obstResult =
                ExplorerTentLogic.validatePlacement(100, 64, 200, ExplorerTentLogic.Facing.NORTH, obstructedWorld);
        assertEquals(
                ExplorerTentLogic.PlacementStatus.OBSTRUCTED,
                obstResult.status(),
                "Hindernis im Zeltvolumen sollte OBSTRUCTED liefern");
    }

    @Test
    void testStructureMappingAndPartCount() {
        // Prüft, dass alle 24 Positionen einem gültigen Zelt-Part zugeordnet werden
        int airCount = 0;
        int flapCount = 0;
        int wallLeftCount = 0;
        int wallFurnaceLeftCount = 0;
        int wallRightCount = 0;
        int wallChestRightCount = 0;
        int wallFrontLeftCount = 0;
        int wallFrontRightCount = 0;
        int wallCornerLeftCount = 0;
        int wallCornerRightCount = 0;
        int wallBackCount = 0;
        int poleCount = 0;
        int bedFootCount = 0;
        int bedHeadCount = 0;
        int wallCraftingTableRightCount = 0;
        int roofLeftCount = 0;
        int roofRightCount = 0;
        int roofPipeLeftCount = 0;
        int roofFrontLeftCount = 0;
        int roofFrontRightCount = 0;
        int roofCornerLeftCount = 0;
        int roofCornerRightCount = 0;
        int roofRidgeCount = 0;
        int roofFrontCount = 0;
        int roofBackCount = 0;

        for (int relY = 0; relY < ExplorerTentLogic.HEIGHT; relY++) {
            for (int relX = -1; relX <= 1; relX++) {
                for (int relZ = 0; relZ < ExplorerTentLogic.LENGTH; relZ++) {
                    ExplorerTentLogic.TentPart part = ExplorerTentLogic.getTentPart(relX, relY, relZ);
                    switch (part) {
                        case AIR -> airCount++;
                        case ENTRANCE_FLAP -> flapCount++;
                        case POLE -> poleCount++;
                        case WALL_LEFT -> wallLeftCount++;
                        case WALL_FURNACE_LEFT -> wallFurnaceLeftCount++;
                        case WALL_RIGHT -> wallRightCount++;
                        case WALL_CHEST_RIGHT -> wallChestRightCount++;
                        case WALL_CRAFTING_TABLE_RIGHT -> wallCraftingTableRightCount++;
                        case WALL_FRONT_LEFT -> wallFrontLeftCount++;
                        case WALL_FRONT_RIGHT -> wallFrontRightCount++;
                        case WALL_CORNER_LEFT -> wallCornerLeftCount++;
                        case WALL_CORNER_RIGHT -> wallCornerRightCount++;
                        case WALL_BACK -> wallBackCount++;
                        case SLEEPING_BAG_FOOT -> bedFootCount++;
                        case SLEEPING_BAG_HEAD -> bedHeadCount++;
                        case ROOF_LEFT -> roofLeftCount++;
                        case ROOF_RIGHT, ROOF_LANTERN_RIGHT -> roofRightCount++;
                        case ROOF_PIPE_LEFT -> roofPipeLeftCount++;
                        case ROOF_FRONT_LEFT -> roofFrontLeftCount++;
                        case ROOF_FRONT_RIGHT -> roofFrontRightCount++;
                        case ROOF_CORNER_LEFT -> roofCornerLeftCount++;
                        case ROOF_CORNER_RIGHT -> roofCornerRightCount++;
                        case ROOF_RIDGE -> roofRidgeCount++;
                        case ROOF_FRONT -> roofFrontCount++;
                        case ROOF_BACK -> roofBackCount++;
                    }
                }
            }
        }

        // Layer 0 Validierung (Leichte Zeltkonstruktion: Keine Pfosten!)
        assertEquals(0, airCount, "Im Zeltvolumen gibt es keine Luftblöcke mehr");
        assertEquals(1, flapCount, "Eingang muss genau 1 Eingangsplane haben");
        assertEquals(0, poleCount, "Es darf KEINE Holzpfosten im Zelt mehr geben!");
        assertEquals(1, wallFrontLeftCount, "Es muss 1 vorderen linken Stoff-Eingangsblock geben");
        assertEquals(1, wallFrontRightCount, "Es muss 1 vorderen rechten Stoff-Eingangsblock geben");
        assertEquals(1, wallLeftCount, "Es muss 1 linken Seitenwand-Block geben");
        assertEquals(1, wallFurnaceLeftCount, "Es muss 1 Ofen-Block auf der linken Seite geben");
        assertEquals(1, wallCornerLeftCount, "Es muss 1 linke Eck-Zeltwand geben");
        assertEquals(0, wallRightCount, "Rechts gibt es keine reine Seitenwand mehr");
        assertEquals(1, wallChestRightCount, "Es muss 1 Truhe an der rechten Wand geben");
        assertEquals(1, wallCraftingTableRightCount, "Es muss 1 Craftingtable-Block auf der rechten Seite geben");
        assertEquals(1, wallCornerRightCount, "Es muss 1 rechte Eck-Zeltwand geben");
        assertEquals(1, wallBackCount, "Es muss 1 hinteren Zeltwand-Block geben");
        assertEquals(1, bedFootCount, "Es muss 1 Fußteil des Schlafsacks geben");
        assertEquals(1, bedHeadCount, "Es muss 1 Kopfteil des Schlafsacks geben");

        // Layer 1 Validierung
        assertEquals(1, roofFrontLeftCount, "Es muss 1 vordere linke Dachfläche geben");
        assertEquals(1, roofFrontRightCount, "Es muss 1 vordere rechte Dachfläche geben");
        assertEquals(1, roofLeftCount, "Es muss 1 linke Dachfläche ohne Ofenrohr geben");
        assertEquals(1, roofPipeLeftCount, "Es muss 1 linke Dachfläche mit Ofenrohr geben");
        assertEquals(2, roofRightCount, "Es muss 2 rechte Dachflächen geben");
        assertEquals(1, roofCornerLeftCount, "Es muss 1 linke hintere Dachecke geben");
        assertEquals(1, roofCornerRightCount, "Es muss 1 rechte hintere Dachecke geben");
        assertEquals(2, roofRidgeCount, "Es muss 2 Firstbalken-Dachblöcke geben");
        assertEquals(1, roofFrontCount, "Es muss 1 vorderen Giebel-/Eingangsbalken geben");
        assertEquals(1, roofBackCount, "Es muss 1 hinteren Giebel geben");

        // Spezifische Positions-Checks
        assertEquals(
                ExplorerTentLogic.TentPart.ENTRANCE_FLAP,
                ExplorerTentLogic.getTentPart(0, 0, 0),
                "Eingang bei (0,0,0) muss die Eingangsplane sein");
        assertEquals(
                ExplorerTentLogic.TentPart.WALL_FRONT_LEFT,
                ExplorerTentLogic.getTentPart(-1, 0, 0),
                "Vorne links muss Stoff-Eingangsplane sein");
        assertEquals(
                ExplorerTentLogic.TentPart.WALL_FRONT_RIGHT,
                ExplorerTentLogic.getTentPart(1, 0, 0),
                "Vorne rechts muss Stoff-Eingangsplane sein");
        assertEquals(
                ExplorerTentLogic.TentPart.WALL_CORNER_LEFT,
                ExplorerTentLogic.getTentPart(-1, 0, 3),
                "Hinten links muss Stoff-Ecke sein");
        assertEquals(
                ExplorerTentLogic.TentPart.WALL_CORNER_RIGHT,
                ExplorerTentLogic.getTentPart(1, 0, 3),
                "Hinten rechts muss Stoff-Ecke sein");
        assertEquals(
                ExplorerTentLogic.TentPart.ROOF_FRONT_LEFT,
                ExplorerTentLogic.getTentPart(-1, 1, 0),
                "Vorne links oben muss Dacheingangsecke sein");
        assertEquals(
                ExplorerTentLogic.TentPart.ROOF_FRONT_RIGHT,
                ExplorerTentLogic.getTentPart(1, 1, 0),
                "Vorne rechts oben muss Dacheingangsecke sein");
        assertEquals(
                ExplorerTentLogic.TentPart.ROOF_CORNER_LEFT,
                ExplorerTentLogic.getTentPart(-1, 1, 3),
                "Hinten links oben muss Dachecke sein");
        assertEquals(
                ExplorerTentLogic.TentPart.ROOF_CORNER_RIGHT,
                ExplorerTentLogic.getTentPart(1, 1, 3),
                "Hinten rechts oben muss Dachecke sein");
        assertEquals(
                ExplorerTentLogic.TentPart.SLEEPING_BAG_FOOT,
                ExplorerTentLogic.getTentPart(0, 0, 1),
                "Schlafsack-Fuß bei (0,0,1)");
        assertEquals(
                ExplorerTentLogic.TentPart.SLEEPING_BAG_HEAD,
                ExplorerTentLogic.getTentPart(0, 0, 2),
                "Schlafsack-Kopf bei (0,0,2)");
        assertEquals(
                ExplorerTentLogic.TentPart.WALL_BACK, ExplorerTentLogic.getTentPart(0, 0, 3), "Rückwand bei (0,0,3)");
        assertEquals(
                ExplorerTentLogic.TentPart.ROOF_RIDGE, ExplorerTentLogic.getTentPart(0, 1, 1), "First bei (0,1,1)");
        assertEquals(
                ExplorerTentLogic.TentPart.ROOF_RIDGE, ExplorerTentLogic.getTentPart(0, 1, 2), "First bei (0,1,2)");
    }

    @Test
    void testDismantlingAndAnchorRecovery() {
        int anchorX = 100;
        int anchorY = 64;
        int anchorZ = 200;
        ExplorerTentLogic.Facing facing = ExplorerTentLogic.Facing.NORTH;

        // 1. Anchor-Rückrechnung aus Schlafsack-Kopfteil
        ExplorerTentLogic.BlockCoord headPos = ExplorerTentLogic.toWorldPos(anchorX, anchorY, anchorZ, facing, 0, 0, 2);
        ExplorerTentLogic.BlockCoord recoveredAnchor =
                ExplorerTentLogic.getAnchorFromHead(headPos.x(), headPos.y(), headPos.z(), facing);
        assertEquals(anchorX, recoveredAnchor.x());
        assertEquals(anchorY, recoveredAnchor.y());
        assertEquals(anchorZ, recoveredAnchor.z());

        // 2. Mock World: Alle Zeltblöcke platziert
        java.util.Map<ExplorerTentLogic.BlockCoord, ExplorerTentLogic.TentPart> worldBlocks = new java.util.HashMap<>();
        for (int relY = 0; relY < ExplorerTentLogic.HEIGHT; relY++) {
            for (int relX = -1; relX <= 1; relX++) {
                for (int relZ = 0; relZ < ExplorerTentLogic.LENGTH; relZ++) {
                    ExplorerTentLogic.TentPart part = ExplorerTentLogic.getTentPart(relX, relY, relZ);
                    if (part != ExplorerTentLogic.TentPart.AIR) {
                        ExplorerTentLogic.BlockCoord pos =
                                ExplorerTentLogic.toWorldPos(anchorX, anchorY, anchorZ, facing, relX, relY, relZ);
                        worldBlocks.put(pos, part);
                    }
                }
            }
        }

        ExplorerTentLogic.TentStructureView structureView = new ExplorerTentLogic.TentStructureView() {
            @Override
            public boolean isTentBlock(int x, int y, int z) {
                return worldBlocks.containsKey(new ExplorerTentLogic.BlockCoord(x, y, z));
            }

            @Override
            public boolean isSleepingBagHead(int x, int y, int z) {
                return worldBlocks.get(new ExplorerTentLogic.BlockCoord(x, y, z))
                        == ExplorerTentLogic.TentPart.SLEEPING_BAG_HEAD;
            }

            @Override
            public ExplorerTentLogic.Facing getFacing(int x, int y, int z) {
                return facing;
            }
        };

        // 3. Teste Abbau von verschiedenen Positionen:
        // A. Klick auf Dach links oben
        ExplorerTentLogic.BlockCoord roofPos =
                ExplorerTentLogic.toWorldPos(anchorX, anchorY, anchorZ, facing, -1, 1, 3);
        java.util.List<ExplorerTentLogic.BlockCoord> blocksFromRoof =
                ExplorerTentLogic.collectTentBlocks(roofPos.x(), roofPos.y(), roofPos.z(), structureView);
        assertEquals(24, blocksFromRoof.size(), "Sollte alle 24 platzierten Zeltblöcke erfassen");

        // B. Klick auf vorderen Eckpfosten
        ExplorerTentLogic.BlockCoord polePos = ExplorerTentLogic.toWorldPos(anchorX, anchorY, anchorZ, facing, 1, 0, 0);
        java.util.List<ExplorerTentLogic.BlockCoord> blocksFromPole =
                ExplorerTentLogic.collectTentBlocks(polePos.x(), polePos.y(), polePos.z(), structureView);
        assertEquals(24, blocksFromPole.size(), "Sollte auch vom Pfosten aus alle 24 Zeltblöcke erfassen");

        // C. Klick auf Schlafsack selbst
        java.util.List<ExplorerTentLogic.BlockCoord> blocksFromBed =
                ExplorerTentLogic.collectTentBlocks(headPos.x(), headPos.y(), headPos.z(), structureView);
        assertEquals(24, blocksFromBed.size(), "Sollte vom Schlafsack aus alle 24 Zeltblöcke erfassen");
    }

    @Test
    void testSleepingLogicAndRespawnPreservation() {
        // 1. Tag und kein Gewitter -> Schlafen nicht möglich
        ExplorerTentLogic.SleepDecision dayDecision = ExplorerTentLogic.evaluateSleep(false, false, false);
        assertEquals(
                ExplorerTentLogic.SleepStatus.NOT_POSSIBLE_NOW,
                dayDecision.status(),
                "Tagsüber ohne Gewitter kein Schlaf");

        // 2. Monster in der Nähe bei Nacht -> Monster-Warnung
        ExplorerTentLogic.SleepDecision monsterDecision = ExplorerTentLogic.evaluateSleep(true, false, true);
        assertEquals(
                ExplorerTentLogic.SleepStatus.MONSTERS_NEARBY, monsterDecision.status(), "Monster verhindern Schlaf");

        // 3. Nacht ohne Monster -> Erfolgreich
        ExplorerTentLogic.SleepDecision nightSuccess = ExplorerTentLogic.evaluateSleep(true, false, false);
        assertEquals(ExplorerTentLogic.SleepStatus.SUCCESS, nightSuccess.status(), "Schlaf bei Nacht möglich");
        assertTrue(nightSuccess.preserveSpawnPoint(), "Heimat-Spawnpunkt MUSS unangetastet bleiben!");
        assertTrue(nightSuccess.resetRestTimer(), "Ruhetimer gegen Phantome muss zurückgesetzt werden");
        assertTrue(nightSuccess.grantRestedBuff(), "Ausgeruht-Buff nach Schlaf im Zelt");

        // 4. Gewitter am Tag ohne Monster -> Erfolgreich
        ExplorerTentLogic.SleepDecision thunderSuccess = ExplorerTentLogic.evaluateSleep(false, true, false);
        assertEquals(ExplorerTentLogic.SleepStatus.SUCCESS, thunderSuccess.status(), "Schlaf bei Gewitter möglich");
        assertTrue(
                thunderSuccess.preserveSpawnPoint(), "Heimat-Spawnpunkt MUSS auch bei Gewitter unangetastet bleiben!");
    }

    @Test
    void testSleepingBagIsRecognizedAsBedByVanillaSleepTick() {
        // LivingEntity.tick() weckt Schlafende sofort, wenn der Block an sleepingPos kein BedBlock ist
        assertTrue(
                net.minecraft.world.level.block.BedBlock.class.isAssignableFrom(TentSleepingBagBlock.class),
                "Schlafsack muss von BedBlock erben, sonst bricht Vanilla den Schlaf im ersten Tick ab");
    }

    @Test
    void testOnlyTentSleepingBagBlocksSettingSpawn() {
        net.minecraft.SharedConstants.tryDetectVersion();
        net.minecraft.server.Bootstrap.bootStrap();

        assertTrue(
                TentSleepingBagBlock.allowsSettingSpawn(net.minecraft.world.level.block.Blocks.BED.red()),
                "Vanilla-Betten müssen den Spawnpunkt weiterhin setzen");
        assertTrue(
                TentSleepingBagBlock.allowsSettingSpawn(net.minecraft.world.level.block.Blocks.STONE),
                "Nicht-Zelt-Blöcke dürfen nicht blockiert werden");
    }

    @Test
    void testTentResourcesRecipesAndTranslations() throws Exception {
        java.nio.file.Path baseDir = java.nio.file.Paths.get("").toAbsolutePath();
        java.nio.file.Path resourcesDir = baseDir.resolve("src/main/resources");

        // 1. Texturen
        java.nio.file.Path itemTexture = resourcesDir.resolve("assets/mc3ver/textures/item/explorer_tent.png");
        assertTrue(java.nio.file.Files.exists(itemTexture), "explorer_tent.png muss existieren");

        java.nio.file.Path canvasTexture = resourcesDir.resolve("assets/mc3ver/textures/block/tent_canvas.png");
        assertTrue(java.nio.file.Files.exists(canvasTexture), "tent_canvas.png muss existieren");

        java.nio.file.Path poleTexture = resourcesDir.resolve("assets/mc3ver/textures/block/tent_pole.png");
        assertTrue(java.nio.file.Files.exists(poleTexture), "tent_pole.png muss existieren");

        java.nio.file.Path bedTexture = resourcesDir.resolve("assets/mc3ver/textures/block/tent_sleeping_bag.png");
        assertTrue(java.nio.file.Files.exists(bedTexture), "tent_sleeping_bag.png muss existieren");

        // 2. Item JSONs
        java.nio.file.Path itemJson = resourcesDir.resolve("assets/mc3ver/items/explorer_tent.json");
        assertTrue(java.nio.file.Files.exists(itemJson), "items/explorer_tent.json muss existieren");

        java.nio.file.Path itemModelJson = resourcesDir.resolve("assets/mc3ver/models/item/explorer_tent.json");
        assertTrue(java.nio.file.Files.exists(itemModelJson), "models/item/explorer_tent.json muss existieren");

        // 3. Blockstates
        assertTrue(
                java.nio.file.Files.exists(resourcesDir.resolve("assets/mc3ver/blockstates/tent_canvas.json")),
                "blockstates/tent_canvas.json fehlt");
        assertTrue(
                java.nio.file.Files.exists(resourcesDir.resolve("assets/mc3ver/blockstates/tent_pole.json")),
                "blockstates/tent_pole.json fehlt");
        assertTrue(
                java.nio.file.Files.exists(resourcesDir.resolve("assets/mc3ver/blockstates/tent_sleeping_bag.json")),
                "blockstates/tent_sleeping_bag.json fehlt");

        // 4. Rezept
        java.nio.file.Path recipeJson = resourcesDir.resolve("data/mc3ver/recipe/explorer_tent_white.json");
        assertTrue(java.nio.file.Files.exists(recipeJson), "recipe/explorer_tent_white.json muss existieren");

        // 5. Übersetzungen
        java.nio.file.Path deJson = resourcesDir.resolve("assets/mc3ver/lang/de_de.json");
        java.nio.file.Path enJson = resourcesDir.resolve("assets/mc3ver/lang/en_us.json");
        String deContent = java.nio.file.Files.readString(deJson);
        String enContent = java.nio.file.Files.readString(enJson);

        assertTrue(deContent.contains("\"item.mc3ver.explorer_tent\""), "DE Übersetzung für explorer_tent fehlt");
        assertTrue(enContent.contains("\"item.mc3ver.explorer_tent\""), "EN Übersetzung für explorer_tent fehlt");
        assertTrue(deContent.contains("\"block.mc3ver.tent_canvas\""), "DE Übersetzung für tent_canvas fehlt");
        assertTrue(enContent.contains("\"block.mc3ver.tent_canvas\""), "EN Übersetzung für tent_canvas fehlt");
        assertTrue(deContent.contains("\"block.mc3ver.tent_pole\""), "DE Übersetzung für tent_pole fehlt");
        assertTrue(enContent.contains("\"block.mc3ver.tent_pole\""), "EN Übersetzung für tent_pole fehlt");
        assertTrue(
                deContent.contains("\"block.mc3ver.tent_sleeping_bag\""), "DE Übersetzung für tent_sleeping_bag fehlt");
        assertTrue(
                enContent.contains("\"block.mc3ver.tent_sleeping_bag\""), "EN Übersetzung für tent_sleeping_bag fehlt");
    }

    @Test
    void testExplorerTentRecipeSyntax() throws Exception {
        java.nio.file.Path baseDir = java.nio.file.Paths.get("").toAbsolutePath();
        java.nio.file.Path recipeJson =
                baseDir.resolve("src/main/resources/data/mc3ver/recipe/explorer_tent_white.json");
        String content = java.nio.file.Files.readString(recipeJson);

        com.google.gson.JsonObject json =
                com.google.gson.JsonParser.parseString(content).getAsJsonObject();
        assertEquals("minecraft:crafting_shaped", json.get("type").getAsString());

        com.google.gson.JsonObject key = json.getAsJsonObject("key");
        assertTrue(
                key.get("W").isJsonPrimitive() && key.get("W").getAsString().equals("minecraft:white_wool"),
                "Wolle muss als 'minecraft:white_wool' String definiert sein");
        assertTrue(
                key.get("S").isJsonPrimitive() && key.get("S").getAsString().equals("minecraft:stick"),
                "Stick muss als 'minecraft:stick' String definiert sein");
        assertTrue(
                key.get("B").isJsonPrimitive() && key.get("B").getAsString().equals("#minecraft:beds"),
                "Betten müssen als '#minecraft:beds' String definiert sein");

        com.google.gson.JsonObject result = json.getAsJsonObject("result");
        assertEquals("mc3ver:explorer_tent", result.get("id").getAsString());
    }

    @Test
    void testTentRoofAndCeilingClearanceAndSolidRoof() throws Exception {
        java.nio.file.Path baseDir = java.nio.file.Paths.get("").toAbsolutePath();

        // 1. Visuelle Prüfung: Eingangsdach (tent_canvas_roof_front.json)
        // Muss bei Y >= 14.0 liegen, damit der Spieler (Höhe 1.80m = 12.8px) niemals mit dem Kopf in der Decke steckt
        java.nio.file.Path frontModel =
                baseDir.resolve("src/main/resources/assets/mc3ver/models/block/tent_canvas_roof_front.json");
        String frontContent = java.nio.file.Files.readString(frontModel);
        com.google.gson.JsonObject frontJson =
                com.google.gson.JsonParser.parseString(frontContent).getAsJsonObject();
        com.google.gson.JsonArray frontElements = frontJson.getAsJsonArray("elements");

        for (com.google.gson.JsonElement el : frontElements) {
            com.google.gson.JsonObject obj = el.getAsJsonObject();
            com.google.gson.JsonArray from = obj.getAsJsonArray("from");
            double minY = from.get(1).getAsDouble();
            assertTrue(minY >= 14.0, "Eingangsdecke muss mindestens bei Y=14.0 liegen (war: " + minY + ")");
            com.google.gson.JsonObject faces = obj.getAsJsonObject("faces");
            assertTrue(faces.has("down"), "Dach muss von unten sichtbar sein (down-Face)");
            assertTrue(faces.has("up"), "Dach muss von oben sichtbar sein (up-Face)");
        }

        // 2. Visuelle Prüfung: Firstdach (tent_canvas_roof_ridge.json)
        java.nio.file.Path ridgeModel =
                baseDir.resolve("src/main/resources/assets/mc3ver/models/block/tent_canvas_roof_ridge.json");
        String ridgeContent = java.nio.file.Files.readString(ridgeModel);
        com.google.gson.JsonObject ridgeJson =
                com.google.gson.JsonParser.parseString(ridgeContent).getAsJsonObject();
        com.google.gson.JsonArray ridgeElements = ridgeJson.getAsJsonArray("elements");

        for (com.google.gson.JsonElement el : ridgeElements) {
            com.google.gson.JsonObject obj = el.getAsJsonObject();
            com.google.gson.JsonArray from = obj.getAsJsonArray("from");
            double minY = from.get(1).getAsDouble();
            assertTrue(minY >= 14.0, "Firstdecke muss mindestens bei Y=14.0 liegen (war: " + minY + ")");
            com.google.gson.JsonObject faces = obj.getAsJsonObject("faces");
            assertTrue(faces.has("down"), "First muss von unten sichtbar sein (down-Face)");
            assertTrue(faces.has("up"), "First muss von oben sichtbar sein (up-Face)");
        }

        // 3. Physische Kollisionsprüfung Dachplane:
        // Eingang (ROOF_FRONT) und First (ROOF_RIDGE) MÜSSEN eine solide Dachplane besitzen (Y=14..16),
        // damit man nicht von oben durchfallen oder von unten hinausspringen kann!
        assertTrue(
                ExplorerTentLogic.hasSolidRoofPlane(ExplorerTentLogic.TentPart.ROOF_FRONT),
                "ROOF_FRONT muss eine feste Dachplane besitzen");
        assertTrue(
                ExplorerTentLogic.hasSolidRoofPlane(ExplorerTentLogic.TentPart.ROOF_RIDGE),
                "ROOF_RIDGE muss eine feste Dachplane besitzen");
        assertEquals(
                14.0,
                ExplorerTentLogic.getRoofCollisionMinY(),
                "Dach-Kollision muss bei Y=14.0 beginnen (1.875m über Boden, Spielerhöhe ist 1.80m)");

        // 4. Schlafsack darf den Spieler nicht anheben (keine Fuß-/Kopf-Kollisionshindernisse)
        assertFalse(
                ExplorerTentLogic.hasSleepingBagObstacleCollision(),
                "Schlafsack darf keine Hindernis-Kollision haben, um den Spieler nicht in die Decke zu drücken");

        // 5. Wände und geschlossene Dachseiten müssen geschlossene Begrenzungen sein
        assertTrue(ExplorerTentLogic.hasFullBlockCollision(ExplorerTentLogic.TentPart.WALL_LEFT));
        assertTrue(ExplorerTentLogic.hasFullBlockCollision(ExplorerTentLogic.TentPart.WALL_RIGHT));
        assertTrue(ExplorerTentLogic.hasFullBlockCollision(ExplorerTentLogic.TentPart.WALL_CORNER_LEFT));
        assertTrue(ExplorerTentLogic.hasFullBlockCollision(ExplorerTentLogic.TentPart.WALL_CORNER_RIGHT));
        assertTrue(ExplorerTentLogic.hasFullBlockCollision(ExplorerTentLogic.TentPart.WALL_BACK));
        assertTrue(ExplorerTentLogic.hasFullBlockCollision(ExplorerTentLogic.TentPart.ROOF_LEFT));
        assertTrue(ExplorerTentLogic.hasFullBlockCollision(ExplorerTentLogic.TentPart.ROOF_RIGHT));
        assertTrue(ExplorerTentLogic.hasFullBlockCollision(ExplorerTentLogic.TentPart.ROOF_BACK));
        assertFalse(ExplorerTentLogic.hasFullBlockCollision(ExplorerTentLogic.TentPart.AIR));
        assertFalse(ExplorerTentLogic.hasFullBlockCollision(ExplorerTentLogic.TentPart.ROOF_FRONT));
        assertFalse(ExplorerTentLogic.hasFullBlockCollision(ExplorerTentLogic.TentPart.ROOF_RIDGE));
    }

    @Test
    void testThinWallModelsAndNoPolesPlaced() throws Exception {
        java.nio.file.Path baseDir = java.nio.file.Paths.get("").toAbsolutePath();
        java.nio.file.Path modelsDir = baseDir.resolve("src/main/resources/assets/mc3ver/models/block");

        // 1. Alle Wandmodelle müssen dünn sein (Dicke <= 2.0 Pixel, keine 16px Vollblöcke)
        String[] wallModels = {
            "tent_canvas_wall_left.json",
            "tent_canvas_wall_right.json",
            "tent_canvas_wall_back.json",
            "tent_canvas_wall_corner_left.json",
            "tent_canvas_wall_corner_right.json",
            "tent_canvas_roof_back.json"
        };

        for (String modelName : wallModels) {
            java.nio.file.Path modelPath = modelsDir.resolve(modelName);
            assertTrue(java.nio.file.Files.exists(modelPath), "Modell " + modelName + " muss existieren");

            String content = java.nio.file.Files.readString(modelPath);
            com.google.gson.JsonObject json =
                    com.google.gson.JsonParser.parseString(content).getAsJsonObject();
            com.google.gson.JsonArray elements = json.getAsJsonArray("elements");
            assertNotNull(elements, "Modell " + modelName + " muss eigene elements definieren (kein cube_all)");

            for (com.google.gson.JsonElement el : elements) {
                com.google.gson.JsonObject obj = el.getAsJsonObject();
                com.google.gson.JsonArray from = obj.getAsJsonArray("from");
                com.google.gson.JsonArray to = obj.getAsJsonArray("to");

                double thickX = Math.abs(to.get(0).getAsDouble() - from.get(0).getAsDouble());
                double thickZ = Math.abs(to.get(2).getAsDouble() - from.get(2).getAsDouble());
                double minThickness = Math.min(thickX, thickZ);

                assertTrue(
                        minThickness <= 2.0,
                        "Wand in " + modelName + " muss dünn sein (Dicke <= 2.0, war: " + minThickness + ")");
            }
        }
    }

    @Test
    void testDyeColorSupportAndDefaults() {
        // 1. Es muss genau 16 Minecraft-Farben geben
        assertEquals(16, ExplorerTentLogic.ALL_DYE_COLORS.size(), "Es muss genau 16 Zeltfarben geben");

        // 2. Standard-Farbe ist weiß (WHITE)
        assertEquals("white", ExplorerTentLogic.DEFAULT_COLOR_NAME);
        assertEquals(0xF9FFFE, ExplorerTentLogic.getColorRgb("white"));

        // 3. Alle 16 Farben müssen gültige RGB-Werte haben
        String[] expectedColors = {
            "white", "orange", "magenta", "light_blue", "yellow", "lime", "pink", "gray",
            "light_gray", "cyan", "purple", "blue", "brown", "green", "red", "black"
        };
        for (String colorName : expectedColors) {
            assertTrue(
                    ExplorerTentLogic.ALL_DYE_COLORS.contains(colorName),
                    "Farbe " + colorName + " muss unterstützt werden");
            int rgb = ExplorerTentLogic.getColorRgb(colorName);
            assertTrue(rgb > 0, "RGB-Wert für " + colorName + " muss positiv sein");
        }

        // 4. Fallback bei ungültigem Namen liefert Standardfarbe
        assertEquals(ExplorerTentLogic.DEFAULT_COLOR_NAME, ExplorerTentLogic.normalizeColorName("invalid_color"));
        assertEquals(ExplorerTentLogic.getColorRgb("white"), ExplorerTentLogic.getColorRgb("unknown"));
    }

    @Test
    void testColorDetectionAndRgbMatching() {
        // 1. Exakte RGB-Abfragen
        assertEquals("white", ExplorerTentLogic.getColorNameFromRgb(0xF9FFFE));
        assertEquals("red", ExplorerTentLogic.getColorNameFromRgb(0xB02E26));
        assertEquals("light_blue", ExplorerTentLogic.getColorNameFromRgb(0x3AB3DA));
        assertEquals("lime", ExplorerTentLogic.getColorNameFromRgb(0x80C71F));

        // 2. Nächste Farberkennung (Nearest Color Match bei Mischfarben)
        assertEquals("red", ExplorerTentLogic.getColorNameFromRgb(0xB02020));
        assertEquals("blue", ExplorerTentLogic.getColorNameFromRgb(0x353FAA));

        // 3. Farberkennung bei Zelt-Abbau (findTentColor)
        ExplorerTentLogic.TentStructureView coloredView = new ExplorerTentLogic.TentStructureView() {
            @Override
            public boolean isTentBlock(int x, int y, int z) {
                return true;
            }

            @Override
            public boolean isSleepingBagHead(int x, int y, int z) {
                return false;
            }

            @Override
            public ExplorerTentLogic.Facing getFacing(int x, int y, int z) {
                return ExplorerTentLogic.Facing.NORTH;
            }

            @Override
            public String getColor(int x, int y, int z) {
                if (x == 10 && y == 64 && z == 10) return "yellow";
                return null;
            }
        };

        java.util.List<ExplorerTentLogic.BlockCoord> coords = java.util.List.of(
                new ExplorerTentLogic.BlockCoord(0, 64, 0),
                new ExplorerTentLogic.BlockCoord(10, 64, 10),
                new ExplorerTentLogic.BlockCoord(20, 64, 20));

        assertEquals("yellow", ExplorerTentLogic.findTentColor(coords, coloredView));

        // Fallback wenn keine Farbe gefunden wird
        ExplorerTentLogic.TentStructureView defaultView = new ExplorerTentLogic.TentStructureView() {
            @Override
            public boolean isTentBlock(int x, int y, int z) {
                return true;
            }

            @Override
            public boolean isSleepingBagHead(int x, int y, int z) {
                return false;
            }

            @Override
            public ExplorerTentLogic.Facing getFacing(int x, int y, int z) {
                return ExplorerTentLogic.Facing.NORTH;
            }
        };
        assertEquals("white", ExplorerTentLogic.findTentColor(coords, defaultView));
    }

    @Test
    void testColoredTentRecipesExistAndValid() throws Exception {
        java.nio.file.Path baseDir = java.nio.file.Paths.get("").toAbsolutePath();
        java.nio.file.Path recipeDir = baseDir.resolve("src/main/resources/data/mc3ver/recipe");

        for (String colorName : ExplorerTentLogic.ALL_DYE_COLORS) {
            // 1. Crafting-Rezept mit Wolle
            java.nio.file.Path craftPath = recipeDir.resolve("explorer_tent_" + colorName + ".json");
            assertTrue(
                    java.nio.file.Files.exists(craftPath),
                    "Crafting-Rezept für " + colorName + " muss existieren: " + craftPath);

            String craftContent = java.nio.file.Files.readString(craftPath);
            com.google.gson.JsonObject craftJson =
                    com.google.gson.JsonParser.parseString(craftContent).getAsJsonObject();
            assertEquals("minecraft:crafting_shaped", craftJson.get("type").getAsString());

            com.google.gson.JsonObject key = craftJson.getAsJsonObject("key");
            assertEquals("minecraft:" + colorName + "_wool", key.get("W").getAsString());

            com.google.gson.JsonObject result = craftJson.getAsJsonObject("result");
            assertEquals("mc3ver:explorer_tent", result.get("id").getAsString());

            com.google.gson.JsonObject components = result.getAsJsonObject("components");
            assertNotNull(components, "Resultat muss components für Färbung haben");
            int expectedRgb = ExplorerTentLogic.getColorRgb(colorName);
            int actualRgb = components.has("minecraft:dyed_color")
                            && components.get("minecraft:dyed_color").isJsonObject()
                    ? components
                            .getAsJsonObject("minecraft:dyed_color")
                            .get("rgb")
                            .getAsInt()
                    : components.get("minecraft:dyed_color").getAsInt();
            assertEquals(expectedRgb, actualRgb, "RGB für " + colorName + " muss übereinstimmen");
        }
    }

    @Test
    void testTentCanvasBlockColorProperty() {
        net.minecraft.SharedConstants.tryDetectVersion();
        net.minecraft.server.Bootstrap.bootStrap();

        assertNotNull(TentCanvasBlock.COLOR, "TentCanvasBlock muss ein COLOR Property besitzen");
        assertEquals(
                16, TentCanvasBlock.COLOR.getPossibleValues().size(), "COLOR Property muss 16 DyeColors unterstützen");
        assertTrue(TentCanvasBlock.COLOR.getPossibleValues().contains(net.minecraft.world.item.DyeColor.WHITE));
        assertTrue(TentCanvasBlock.COLOR.getPossibleValues().contains(net.minecraft.world.item.DyeColor.RED));
        assertEquals("color", TentCanvasBlock.COLOR.getName());
        assertEquals("white", TentCanvasBlock.COLOR.getName(net.minecraft.world.item.DyeColor.WHITE));
        assertEquals("red", TentCanvasBlock.COLOR.getName(net.minecraft.world.item.DyeColor.RED));
    }

    @Test
    void testTentCanvasModelsHaveTintIndex() throws Exception {
        java.nio.file.Path baseDir = java.nio.file.Paths.get("").toAbsolutePath();
        java.nio.file.Path modelsDir = baseDir.resolve("src/main/resources/assets/mc3ver/models/block");

        String[] canvasModels = {
            "tent_canvas_wall.json",
            "tent_canvas_wall_left.json",
            "tent_canvas_wall_right.json",
            "tent_canvas_wall_back.json",
            "tent_canvas_wall_corner_left.json",
            "tent_canvas_wall_corner_right.json",
            "tent_canvas_roof_left.json",
            "tent_canvas_roof_right.json",
            "tent_canvas_roof_front.json",
            "tent_canvas_roof_ridge.json",
            "tent_canvas_roof_back.json",
            "tent_canvas_roof_corner_left.json",
            "tent_canvas_roof_corner_right.json",
            "tent_canvas_wall_front_left.json",
            "tent_canvas_wall_front_right.json",
            "tent_canvas_roof_front_left.json",
            "tent_canvas_roof_front_right.json"
        };

        for (String modelName : canvasModels) {
            java.nio.file.Path modelPath = modelsDir.resolve(modelName);
            assertTrue(java.nio.file.Files.exists(modelPath), "Modell " + modelName + " muss existieren");

            String content = java.nio.file.Files.readString(modelPath);
            com.google.gson.JsonObject json =
                    com.google.gson.JsonParser.parseString(content).getAsJsonObject();
            com.google.gson.JsonArray elements = json.getAsJsonArray("elements");
            assertNotNull(elements, "Modell " + modelName + " muss elements haben");

            for (com.google.gson.JsonElement el : elements) {
                if (!el.toString().contains("#canvas")) continue; // Deko wie Hering, Leine, Wimpel ist keine Zeltplane
                com.google.gson.JsonObject elObj = el.getAsJsonObject();
                com.google.gson.JsonObject faces = elObj.getAsJsonObject("faces");
                for (String faceName : faces.keySet()) {
                    com.google.gson.JsonObject faceObj = faces.getAsJsonObject(faceName);
                    assertTrue(
                            faceObj.has("tintindex"),
                            "Face " + faceName + " in " + modelName + " muss tintindex: 0 haben für Färbung");
                    assertEquals(0, faceObj.get("tintindex").getAsInt(), "tintindex in " + modelName + " muss 0 sein");
                }
            }
        }
    }

    @Test
    void testTentCanvasBlockstateFile() throws Exception {
        java.nio.file.Path baseDir = java.nio.file.Paths.get("").toAbsolutePath();
        java.nio.file.Path bsPath = baseDir.resolve("src/main/resources/assets/mc3ver/blockstates/tent_canvas.json");
        assertTrue(java.nio.file.Files.exists(bsPath), "blockstates/tent_canvas.json muss existieren");

        String content = java.nio.file.Files.readString(bsPath);
        com.google.gson.JsonObject json =
                com.google.gson.JsonParser.parseString(content).getAsJsonObject();
        assertTrue(
                json.has("multipart"),
                "blockstates/tent_canvas.json muss multipart nutzen, um mit dem COLOR-Property kompatibel zu sein");

        com.google.gson.JsonArray multipart = json.getAsJsonArray("multipart");
        assertNotNull(multipart);

        // Prüfe, dass alle 11 Teile für alle 4 Blickrichtungen abgedeckt sind
        for (TentCanvasBlock.CanvasPart part : TentCanvasBlock.CanvasPart.values()) {
            for (String facing : new String[] {"north", "east", "south", "west"}) {
                boolean found = false;
                for (com.google.gson.JsonElement elem : multipart) {
                    com.google.gson.JsonObject clause = elem.getAsJsonObject();
                    if (clause.has("when")) {
                        com.google.gson.JsonObject when = clause.getAsJsonObject("when");
                        if (facing.equals(when.get("facing").getAsString())
                                && part.getSerializedName()
                                        .equals(when.get("part").getAsString())) {
                            found = true;
                            assertTrue(clause.has("apply"));
                            break;
                        }
                    }
                }
                assertTrue(
                        found,
                        "Multipart muss Bedingung für part=" + part.getSerializedName() + ", facing=" + facing
                                + " haben");
            }
        }
    }

    @Test
    void testColorCycleIntegrityForAll16Colors() {
        for (String colorName : ExplorerTentLogic.ALL_DYE_COLORS) {
            int rgb = ExplorerTentLogic.getColorRgb(colorName);
            String recoveredName = ExplorerTentLogic.getColorNameFromRgb(rgb);
            assertEquals(colorName, recoveredName, "RGB Roundtrip für " + colorName + " fehlgeschlagen");

            ExplorerTentLogic.TentStructureView view = new ExplorerTentLogic.TentStructureView() {
                @Override
                public boolean isTentBlock(int x, int y, int z) {
                    return true;
                }

                @Override
                public boolean isSleepingBagHead(int x, int y, int z) {
                    return false;
                }

                @Override
                public ExplorerTentLogic.Facing getFacing(int x, int y, int z) {
                    return ExplorerTentLogic.Facing.NORTH;
                }

                @Override
                public String getColor(int x, int y, int z) {
                    return colorName;
                }
            };

            java.util.List<ExplorerTentLogic.BlockCoord> blocks =
                    java.util.List.of(new ExplorerTentLogic.BlockCoord(0, 0, 0));

            String foundColor = ExplorerTentLogic.findTentColor(blocks, view);
            assertEquals(colorName, foundColor);
            assertEquals(rgb, ExplorerTentLogic.getColorRgb(foundColor));
        }
    }

    @Test
    void testColorResolversForBlockAndItem() {
        // Item-Farb-Resolver
        assertEquals(ExplorerTentLogic.getColorRgb("white"), ExplorerTentLogic.resolveItemColorRgb(null));
        assertEquals(0xB02E26, ExplorerTentLogic.resolveItemColorRgb(0xB02E26));

        // Block-Farb-Resolver
        assertEquals(ExplorerTentLogic.getColorRgb("blue"), ExplorerTentLogic.resolveBlockColorRgb("blue"));
        assertEquals(ExplorerTentLogic.getColorRgb("white"), ExplorerTentLogic.resolveBlockColorRgb("invalid"));
        assertEquals(ExplorerTentLogic.getColorRgb("white"), ExplorerTentLogic.resolveBlockColorRgb(null));
    }

    @Test
    void testExplorerTentItemModelTints() throws Exception {
        java.nio.file.Path baseDir = java.nio.file.Paths.get("").toAbsolutePath();
        java.nio.file.Path itemJsonPath = baseDir.resolve("src/main/resources/assets/mc3ver/items/explorer_tent.json");
        assertTrue(java.nio.file.Files.exists(itemJsonPath), "assets/mc3ver/items/explorer_tent.json muss existieren");

        String content = java.nio.file.Files.readString(itemJsonPath);
        com.google.gson.JsonObject json =
                com.google.gson.JsonParser.parseString(content).getAsJsonObject();
        com.google.gson.JsonObject model = json.getAsJsonObject("model");
        assertNotNull(model, "item definition muss model haben");
        com.google.gson.JsonArray tints = model.getAsJsonArray("tints");
        assertNotNull(tints, "model muss tints array haben");
        assertTrue(tints.size() > 0, "tints array darf nicht leer sein");

        com.google.gson.JsonObject tint = tints.get(0).getAsJsonObject();
        assertEquals("minecraft:dye", tint.get("type").getAsString(), "Erster Tint muss type minecraft:dye sein");
    }

    @Test
    void testRoofSlopeModelsAreThinAndHollow() throws Exception {
        java.nio.file.Path baseDir = java.nio.file.Paths.get("").toAbsolutePath();
        java.nio.file.Path modelsDir = baseDir.resolve("src/main/resources/assets/mc3ver/models/block");

        String[] roofSlopeModels = {"tent_canvas_roof_left.json", "tent_canvas_roof_right.json"};

        for (String modelName : roofSlopeModels) {
            java.nio.file.Path modelPath = modelsDir.resolve(modelName);
            assertTrue(java.nio.file.Files.exists(modelPath), "Modell " + modelName + " muss existieren");

            String content = java.nio.file.Files.readString(modelPath);
            com.google.gson.JsonObject json =
                    com.google.gson.JsonParser.parseString(content).getAsJsonObject();
            com.google.gson.JsonArray elements = json.getAsJsonArray("elements");
            assertNotNull(elements, "Modell " + modelName + " muss elements haben");

            boolean hasTopStep = false;
            boolean isLeft = modelName.contains("left");

            for (com.google.gson.JsonElement el : elements) {
                com.google.gson.JsonObject obj = el.getAsJsonObject();
                com.google.gson.JsonArray from = obj.getAsJsonArray("from");
                com.google.gson.JsonArray to = obj.getAsJsonArray("to");

                double fromX = from.get(0).getAsDouble();
                double toX = to.get(0).getAsDouble();
                double fromY = from.get(1).getAsDouble();
                double toY = to.get(1).getAsDouble();

                double thicknessY = toY - fromY;
                assertTrue(
                        thicknessY <= 2.0,
                        "Jede Dachstufe in " + modelName + " muss dünn sein (<= 2.0px, war: " + thicknessY + ")");

                if (toY >= 15.9) {
                    hasTopStep = true;
                }

                // Im Innenbereich (zur Zeltmitte hin) darf die Plane nicht bei y=0 liegen (muss hohl sein)
                boolean isInnerRegion = isLeft ? (fromX >= 8.0) : (toX <= 8.0);
                if (isInnerRegion) {
                    assertTrue(
                            fromY >= 8.0,
                            "Innenbereich von " + modelName + " muss nach unten hohl sein (fromY >= 8.0, war: " + fromY
                                    + ")");
                }

                // Alle Faces müssen weiterhin tintindex: 0 haben
                com.google.gson.JsonObject faces = obj.getAsJsonObject("faces");
                for (String faceName : faces.keySet()) {
                    com.google.gson.JsonObject faceObj = faces.getAsJsonObject(faceName);
                    assertTrue(
                            faceObj.has("tintindex") && faceObj.get("tintindex").getAsInt() == 0,
                            "Face " + faceName + " in " + modelName + " muss tintindex: 0 besitzen");
                }
            }

            assertTrue(hasTopStep, modelName + " muss oben bei y=16 an den First anschließen");
        }
    }

    @Test
    void testRoofSlopeCollisionShapeAllowsHeadroom() {
        net.minecraft.SharedConstants.tryDetectVersion();
        net.minecraft.server.Bootstrap.bootStrap();

        net.minecraft.world.phys.shapes.VoxelShape leftRoofShape =
                TentCanvasBlock.getWallShape(TentCanvasBlock.CanvasPart.ROOF_LEFT, net.minecraft.core.Direction.NORTH);

        assertNotEquals(
                net.minecraft.world.phys.shapes.Shapes.block(),
                leftRoofShape,
                "ROOF_LEFT darf kein massiver Vollblock sein");

        // Ein stehender Spieler am inneren Rand (x = 12..15, y = 0..12, z = 4..12) darf keine Kollision mit dem Dach
        // haben
        net.minecraft.world.phys.AABB innerHeadBox =
                new net.minecraft.world.phys.AABB(12.0 / 16.0, 0.0, 4.0 / 16.0, 15.0 / 16.0, 12.0 / 16.0, 12.0 / 16.0);

        boolean collision = net.minecraft.world.phys.shapes.Shapes.joinIsNotEmpty(
                leftRoofShape,
                net.minecraft.world.phys.shapes.Shapes.create(innerHeadBox),
                net.minecraft.world.phys.shapes.BooleanOp.AND);

        assertFalse(collision, "Kopfraum an der Innenseite der Dachschräge (ROOF_LEFT) muss frei sein");

        // Auch für ROOF_RIGHT (facing NORTH: Innenrand liegt bei x = 1..4)
        net.minecraft.world.phys.shapes.VoxelShape rightRoofShape =
                TentCanvasBlock.getWallShape(TentCanvasBlock.CanvasPart.ROOF_RIGHT, net.minecraft.core.Direction.NORTH);

        assertNotEquals(
                net.minecraft.world.phys.shapes.Shapes.block(),
                rightRoofShape,
                "ROOF_RIGHT darf kein massiver Vollblock sein");

        net.minecraft.world.phys.AABB rightHeadBox =
                new net.minecraft.world.phys.AABB(1.0 / 16.0, 0.0, 4.0 / 16.0, 4.0 / 16.0, 12.0 / 16.0, 12.0 / 16.0);

        boolean collisionRight = net.minecraft.world.phys.shapes.Shapes.joinIsNotEmpty(
                rightRoofShape,
                net.minecraft.world.phys.shapes.Shapes.create(rightHeadBox),
                net.minecraft.world.phys.shapes.BooleanOp.AND);

        assertFalse(collisionRight, "Kopfraum an der Innenseite der Dachschräge (ROOF_RIGHT) muss frei sein");
    }

    @Test
    void testRoofCornersInTentLogic() {
        assertEquals(
                ExplorerTentLogic.TentPart.ROOF_CORNER_LEFT,
                ExplorerTentLogic.getTentPart(-1, 1, 3),
                "Hinten links oben (-1, 1, 3) muss ROOF_CORNER_LEFT sein");
        assertEquals(
                ExplorerTentLogic.TentPart.ROOF_CORNER_RIGHT,
                ExplorerTentLogic.getTentPart(1, 1, 3),
                "Hinten rechts oben (1, 1, 3) muss ROOF_CORNER_RIGHT sein");

        assertEquals(ExplorerTentLogic.TentPart.ROOF_FRONT_LEFT, ExplorerTentLogic.getTentPart(-1, 1, 0));
        assertEquals(ExplorerTentLogic.TentPart.ROOF_PIPE_LEFT, ExplorerTentLogic.getTentPart(-1, 1, 1));
        assertEquals(ExplorerTentLogic.TentPart.ROOF_LEFT, ExplorerTentLogic.getTentPart(-1, 1, 2));

        assertEquals(ExplorerTentLogic.TentPart.ROOF_FRONT_RIGHT, ExplorerTentLogic.getTentPart(1, 1, 0));
        assertEquals(ExplorerTentLogic.TentPart.ROOF_RIGHT, ExplorerTentLogic.getTentPart(1, 1, 1));
        assertEquals(ExplorerTentLogic.TentPart.ROOF_LANTERN_RIGHT, ExplorerTentLogic.getTentPart(1, 1, 2));

        assertTrue(ExplorerTentLogic.hasFullBlockCollision(ExplorerTentLogic.TentPart.ROOF_CORNER_LEFT));
        assertTrue(ExplorerTentLogic.hasFullBlockCollision(ExplorerTentLogic.TentPart.ROOF_CORNER_RIGHT));
    }

    @Test
    void testRoofCornerModelsAreSealedAndTinted() throws Exception {
        java.nio.file.Path baseDir = java.nio.file.Paths.get("").toAbsolutePath();
        java.nio.file.Path modelsDir = baseDir.resolve("src/main/resources/assets/mc3ver/models/block");

        String[] cornerModels = {"tent_canvas_roof_corner_left.json", "tent_canvas_roof_corner_right.json"};

        for (String modelName : cornerModels) {
            java.nio.file.Path modelPath = modelsDir.resolve(modelName);
            assertTrue(java.nio.file.Files.exists(modelPath), "Modell " + modelName + " muss existieren");

            String content = java.nio.file.Files.readString(modelPath);
            com.google.gson.JsonObject json =
                    com.google.gson.JsonParser.parseString(content).getAsJsonObject();
            com.google.gson.JsonArray elements = json.getAsJsonArray("elements");
            assertNotNull(elements, "Modell " + modelName + " muss elements haben");

            boolean hasBackWallAtZ14 = false;
            boolean hasRoofSteps = false;

            for (com.google.gson.JsonElement el : elements) {
                if (!el.toString().contains("#canvas")) continue; // Deko wie Hering, Leine, Wimpel ist keine Zeltplane
                com.google.gson.JsonObject elObj = el.getAsJsonObject();
                com.google.gson.JsonArray from = elObj.getAsJsonArray("from");
                com.google.gson.JsonArray to = elObj.getAsJsonArray("to");

                double fromZ = from.get(2).getAsDouble();
                double toZ = to.get(2).getAsDouble();
                double fromY = from.get(1).getAsDouble();
                double toY = to.get(1).getAsDouble();

                if (fromZ >= 13.9 && toZ <= 16.1 && (toY - fromY) > 2.1) {
                    hasBackWallAtZ14 = true;
                }
                if (fromZ >= 13.9 && toZ <= 16.1 && (toY - fromY) <= 2.1) {
                    hasRoofSteps = true;
                }

                com.google.gson.JsonObject faces = elObj.getAsJsonObject("faces");
                for (String faceName : faces.keySet()) {
                    com.google.gson.JsonObject faceObj = faces.getAsJsonObject(faceName);
                    assertTrue(
                            faceObj.has("tintindex"),
                            "Face " + faceName + " in " + modelName + " muss tintindex haben");
                    assertEquals(0, faceObj.get("tintindex").getAsInt(), "tintindex in " + modelName + " muss 0 sein");
                }
            }

            assertTrue(hasBackWallAtZ14, modelName + " muss vertikale Rückwandelemente an z=14..16 besitzen");
            assertTrue(hasRoofSteps, modelName + " muss Dachschrägen-Elemente besitzen");
        }
    }

    @Test
    void testRoofCornerCollisionAndBlockstate() {
        net.minecraft.SharedConstants.tryDetectVersion();
        net.minecraft.server.Bootstrap.bootStrap();

        assertEquals(
                ExplorerTentLogic.TentPart.ROOF_CORNER_LEFT, TentCanvasBlock.CanvasPart.ROOF_CORNER_LEFT.toTentPart());
        assertEquals(
                ExplorerTentLogic.TentPart.ROOF_CORNER_RIGHT,
                TentCanvasBlock.CanvasPart.ROOF_CORNER_RIGHT.toTentPart());

        net.minecraft.world.phys.shapes.VoxelShape leftShape = TentCanvasBlock.getWallShape(
                TentCanvasBlock.CanvasPart.ROOF_CORNER_LEFT, net.minecraft.core.Direction.NORTH);

        net.minecraft.world.phys.AABB backWallBox =
                new net.minecraft.world.phys.AABB(2.0 / 16.0, 0.0, 14.0 / 16.0, 14.0 / 16.0, 12.0 / 16.0, 16.0 / 16.0);
        boolean backCollision = net.minecraft.world.phys.shapes.Shapes.joinIsNotEmpty(
                leftShape,
                net.minecraft.world.phys.shapes.Shapes.create(backWallBox),
                net.minecraft.world.phys.shapes.BooleanOp.AND);
        assertTrue(backCollision, "ROOF_CORNER_LEFT muss eine geschlossene Rückwand-Kollision an z=14..16 besitzen");

        net.minecraft.world.phys.AABB innerHeadBox =
                new net.minecraft.world.phys.AABB(12.0 / 16.0, 0.0, 4.0 / 16.0, 15.0 / 16.0, 12.0 / 16.0, 12.0 / 16.0);
        boolean headCollision = net.minecraft.world.phys.shapes.Shapes.joinIsNotEmpty(
                leftShape,
                net.minecraft.world.phys.shapes.Shapes.create(innerHeadBox),
                net.minecraft.world.phys.shapes.BooleanOp.AND);
        assertFalse(headCollision, "Kopfraum im Zeltinneren bei ROOF_CORNER_LEFT muss frei sein");

        net.minecraft.world.phys.shapes.VoxelShape rightShape = TentCanvasBlock.getWallShape(
                TentCanvasBlock.CanvasPart.ROOF_CORNER_RIGHT, net.minecraft.core.Direction.NORTH);

        boolean rightBackCollision = net.minecraft.world.phys.shapes.Shapes.joinIsNotEmpty(
                rightShape,
                net.minecraft.world.phys.shapes.Shapes.create(backWallBox),
                net.minecraft.world.phys.shapes.BooleanOp.AND);
        assertTrue(
                rightBackCollision, "ROOF_CORNER_RIGHT muss eine geschlossene Rückwand-Kollision an z=14..16 besitzen");

        net.minecraft.world.phys.AABB rightHeadBox =
                new net.minecraft.world.phys.AABB(1.0 / 16.0, 0.0, 4.0 / 16.0, 4.0 / 16.0, 12.0 / 16.0, 12.0 / 16.0);
        boolean rightHeadCollision = net.minecraft.world.phys.shapes.Shapes.joinIsNotEmpty(
                rightShape,
                net.minecraft.world.phys.shapes.Shapes.create(rightHeadBox),
                net.minecraft.world.phys.shapes.BooleanOp.AND);
        assertFalse(rightHeadCollision, "Kopfraum im Zeltinneren bei ROOF_CORNER_RIGHT muss frei sein");
    }

    @Test
    void testRearWallAndSideWallsEndFlush() throws Exception {
        java.nio.file.Path baseDir = java.nio.file.Paths.get("").toAbsolutePath();
        java.nio.file.Path modelsDir = baseDir.resolve("src/main/resources/assets/mc3ver/models/block");

        String[] rearCornerModels = {
            "tent_canvas_wall_corner_left.json",
            "tent_canvas_wall_corner_right.json",
            "tent_canvas_roof_corner_left.json",
            "tent_canvas_roof_corner_right.json"
        };

        for (String modelName : rearCornerModels) {
            java.nio.file.Path modelPath = modelsDir.resolve(modelName);
            assertTrue(java.nio.file.Files.exists(modelPath), "Modell " + modelName + " muss existieren");

            String content = java.nio.file.Files.readString(modelPath);
            com.google.gson.JsonObject json =
                    com.google.gson.JsonParser.parseString(content).getAsJsonObject();
            com.google.gson.JsonArray elements = json.getAsJsonArray("elements");
            assertNotNull(elements, "Modell " + modelName + " muss elements haben");

            for (com.google.gson.JsonElement el : elements) {
                if (!el.toString().contains("#canvas")) continue; // Deko wie Hering, Leine, Wimpel ist keine Zeltplane
                com.google.gson.JsonObject elObj = el.getAsJsonObject();
                com.google.gson.JsonArray from = elObj.getAsJsonArray("from");
                double fromZ = from.get(2).getAsDouble();
                assertTrue(
                        fromZ >= 13.9,
                        "Element in " + modelName
                                + " darf nicht nach hinten über die Rückwand hinausragen (fromZ >= 14, war: " + fromZ
                                + ")");
            }
        }
    }

    @Test
    void testFrontEntranceCanvasPartsInLogic() {
        // Eingang seitlich begrenzt: links und rechts an relZ=0 muss Stoff sein, Mitte (relX=0) frei
        assertEquals(
                ExplorerTentLogic.TentPart.WALL_FRONT_LEFT,
                ExplorerTentLogic.getTentPart(-1, 0, 0),
                "Eingang links unten (-1, 0, 0) muss WALL_FRONT_LEFT sein");
        assertEquals(
                ExplorerTentLogic.TentPart.WALL_FRONT_RIGHT,
                ExplorerTentLogic.getTentPart(1, 0, 0),
                "Eingang rechts unten (1, 0, 0) muss WALL_FRONT_RIGHT sein");
        assertEquals(
                ExplorerTentLogic.TentPart.ENTRANCE_FLAP,
                ExplorerTentLogic.getTentPart(0, 0, 0),
                "Mittelgang unten (0, 0, 0) ist die Eingangsplane");

        assertEquals(
                ExplorerTentLogic.TentPart.ROOF_FRONT_LEFT,
                ExplorerTentLogic.getTentPart(-1, 1, 0),
                "Eingang links oben (-1, 1, 0) muss ROOF_FRONT_LEFT sein");
        assertEquals(
                ExplorerTentLogic.TentPart.ROOF_FRONT_RIGHT,
                ExplorerTentLogic.getTentPart(1, 1, 0),
                "Eingang rechts oben (1, 1, 0) muss ROOF_FRONT_RIGHT sein");
        assertEquals(
                ExplorerTentLogic.TentPart.ROOF_FRONT,
                ExplorerTentLogic.getTentPart(0, 1, 0),
                "Mittelgang oben (0, 1, 0) muss ROOF_FRONT bleiben");

        // Innenbereich neben Schlafmatte: links Ofen am Fußende (nicht am Kissen), rechts Werkbank am Kopfende
        assertEquals(ExplorerTentLogic.TentPart.WALL_FURNACE_LEFT, ExplorerTentLogic.getTentPart(-1, 0, 1));
        assertEquals(ExplorerTentLogic.TentPart.WALL_LEFT, ExplorerTentLogic.getTentPart(-1, 0, 2));
        assertEquals(ExplorerTentLogic.TentPart.WALL_CHEST_RIGHT, ExplorerTentLogic.getTentPart(1, 0, 1));
        assertEquals(ExplorerTentLogic.TentPart.WALL_CRAFTING_TABLE_RIGHT, ExplorerTentLogic.getTentPart(1, 0, 2));

        assertTrue(ExplorerTentLogic.hasFullBlockCollision(ExplorerTentLogic.TentPart.WALL_FRONT_LEFT));
        assertTrue(ExplorerTentLogic.hasFullBlockCollision(ExplorerTentLogic.TentPart.WALL_FRONT_RIGHT));
        assertTrue(ExplorerTentLogic.hasFullBlockCollision(ExplorerTentLogic.TentPart.ROOF_FRONT_LEFT));
        assertTrue(ExplorerTentLogic.hasFullBlockCollision(ExplorerTentLogic.TentPart.ROOF_FRONT_RIGHT));
    }

    @Test
    void testFrontEntranceModelsExistAndValid() throws Exception {
        java.nio.file.Path baseDir = java.nio.file.Paths.get("").toAbsolutePath();
        java.nio.file.Path modelsDir = baseDir.resolve("src/main/resources/assets/mc3ver/models/block");

        String[] frontModels = {
            "tent_canvas_wall_front_left.json",
            "tent_canvas_wall_front_right.json",
            "tent_canvas_roof_front_left.json",
            "tent_canvas_roof_front_right.json"
        };

        for (String modelName : frontModels) {
            java.nio.file.Path modelPath = modelsDir.resolve(modelName);
            assertTrue(java.nio.file.Files.exists(modelPath), "Modell " + modelName + " muss existieren");

            String content = java.nio.file.Files.readString(modelPath);
            com.google.gson.JsonObject json =
                    com.google.gson.JsonParser.parseString(content).getAsJsonObject();
            com.google.gson.JsonArray elements = json.getAsJsonArray("elements");
            assertNotNull(elements, "Modell " + modelName + " muss elements haben");

            boolean hasFrontFlapAtZ14 = false;
            boolean hasFullDepthSideOrSlope = false;

            for (com.google.gson.JsonElement el : elements) {
                if (!el.toString().contains("#canvas")) continue; // Deko wie Hering, Leine, Wimpel ist keine Zeltplane
                com.google.gson.JsonObject elObj = el.getAsJsonObject();
                com.google.gson.JsonArray from = elObj.getAsJsonArray("from");
                com.google.gson.JsonArray to = elObj.getAsJsonArray("to");

                double fromZ = from.get(2).getAsDouble();
                double toZ = to.get(2).getAsDouble();

                if (fromZ >= 13.9 && toZ <= 16.1) {
                    hasFrontFlapAtZ14 = true;
                }
                if (fromZ <= 0.1 && toZ >= 15.9) {
                    hasFullDepthSideOrSlope = true;
                }

                com.google.gson.JsonObject faces = elObj.getAsJsonObject("faces");
                for (String faceName : faces.keySet()) {
                    com.google.gson.JsonObject faceObj = faces.getAsJsonObject(faceName);
                    assertTrue(faceObj.has("tintindex"), "Face " + faceName + " muss tintindex haben");
                    assertEquals(0, faceObj.get("tintindex").getAsInt());
                }
            }

            assertTrue(hasFrontFlapAtZ14, modelName + " muss eine vordere Zeltplane an z=14..16 besitzen");
            assertTrue(
                    hasFullDepthSideOrSlope,
                    modelName + " muss die Seitenwand/Dachschräge über die gesamte Blocktiefe führen");
        }
    }

    @Test
    void testFrontEntranceCollisionAndBlockstate() {
        net.minecraft.SharedConstants.tryDetectVersion();
        net.minecraft.server.Bootstrap.bootStrap();

        assertEquals(
                ExplorerTentLogic.TentPart.WALL_FRONT_LEFT, TentCanvasBlock.CanvasPart.WALL_FRONT_LEFT.toTentPart());
        assertEquals(
                ExplorerTentLogic.TentPart.WALL_FRONT_RIGHT, TentCanvasBlock.CanvasPart.WALL_FRONT_RIGHT.toTentPart());
        assertEquals(
                ExplorerTentLogic.TentPart.ROOF_FRONT_LEFT, TentCanvasBlock.CanvasPart.ROOF_FRONT_LEFT.toTentPart());
        assertEquals(
                ExplorerTentLogic.TentPart.ROOF_FRONT_RIGHT, TentCanvasBlock.CanvasPart.ROOF_FRONT_RIGHT.toTentPart());

        // WALL_FRONT_LEFT Kollision
        net.minecraft.world.phys.shapes.VoxelShape wallFrontLeftShape = TentCanvasBlock.getWallShape(
                TentCanvasBlock.CanvasPart.WALL_FRONT_LEFT, net.minecraft.core.Direction.NORTH);

        net.minecraft.world.phys.AABB frontFlapBoxLeft =
                new net.minecraft.world.phys.AABB(2.0 / 16.0, 0.0, 14.0 / 16.0, 16.0 / 16.0, 16.0 / 16.0, 16.0 / 16.0);
        assertTrue(
                net.minecraft.world.phys.shapes.Shapes.joinIsNotEmpty(
                        wallFrontLeftShape,
                        net.minecraft.world.phys.shapes.Shapes.create(frontFlapBoxLeft),
                        net.minecraft.world.phys.shapes.BooleanOp.AND),
                "WALL_FRONT_LEFT muss an z=14..16 kollidieren");

        net.minecraft.world.phys.AABB interiorBoxLeft =
                new net.minecraft.world.phys.AABB(3.0 / 16.0, 0.0, 2.0 / 16.0, 15.0 / 16.0, 16.0 / 16.0, 12.0 / 16.0);
        assertFalse(
                net.minecraft.world.phys.shapes.Shapes.joinIsNotEmpty(
                        wallFrontLeftShape,
                        net.minecraft.world.phys.shapes.Shapes.create(interiorBoxLeft),
                        net.minecraft.world.phys.shapes.BooleanOp.AND),
                "WALL_FRONT_LEFT muss im Innenraum frei sein");

        // ROOF_FRONT_LEFT Kollision
        net.minecraft.world.phys.shapes.VoxelShape roofFrontLeftShape = TentCanvasBlock.getWallShape(
                TentCanvasBlock.CanvasPart.ROOF_FRONT_LEFT, net.minecraft.core.Direction.NORTH);

        assertTrue(
                net.minecraft.world.phys.shapes.Shapes.joinIsNotEmpty(
                        roofFrontLeftShape,
                        net.minecraft.world.phys.shapes.Shapes.create(frontFlapBoxLeft),
                        net.minecraft.world.phys.shapes.BooleanOp.AND),
                "ROOF_FRONT_LEFT muss an z=14..16 kollidieren");

        net.minecraft.world.phys.AABB innerHeadBoxLeft =
                new net.minecraft.world.phys.AABB(12.0 / 16.0, 0.0, 4.0 / 16.0, 15.0 / 16.0, 12.0 / 16.0, 12.0 / 16.0);
        assertFalse(
                net.minecraft.world.phys.shapes.Shapes.joinIsNotEmpty(
                        roofFrontLeftShape,
                        net.minecraft.world.phys.shapes.Shapes.create(innerHeadBoxLeft),
                        net.minecraft.world.phys.shapes.BooleanOp.AND),
                "ROOF_FRONT_LEFT muss Kopffreiheit im Innenraum bieten");
    }

    @Test
    void testCraftingTablePartInTentLogic() {
        assertEquals(
                ExplorerTentLogic.TentPart.WALL_CHEST_RIGHT,
                ExplorerTentLogic.getTentPart(1, 0, 1),
                "relX=1, relY=0, relZ=1 (neben Fußende) muss die Truhe sein");
        assertEquals(
                ExplorerTentLogic.TentPart.WALL_CRAFTING_TABLE_RIGHT,
                ExplorerTentLogic.getTentPart(1, 0, 2),
                "relX=1, relY=0, relZ=2 (neben Kopfende) muss WALL_CRAFTING_TABLE_RIGHT sein");
        assertTrue(
                ExplorerTentLogic.isCraftingTable(ExplorerTentLogic.TentPart.WALL_CRAFTING_TABLE_RIGHT),
                "WALL_CRAFTING_TABLE_RIGHT muss als Craftingtable erkannt werden");
        assertFalse(
                ExplorerTentLogic.isCraftingTable(ExplorerTentLogic.TentPart.WALL_RIGHT),
                "WALL_RIGHT darf kein Craftingtable sein");
        assertTrue(
                ExplorerTentLogic.hasFullBlockCollision(ExplorerTentLogic.TentPart.WALL_CRAFTING_TABLE_RIGHT),
                "WALL_CRAFTING_TABLE_RIGHT muss solide Kollision haben");
    }

    @Test
    void testCraftingTableModelExistsAndValid() throws Exception {
        java.nio.file.Path baseDir = java.nio.file.Paths.get("").toAbsolutePath();
        java.nio.file.Path modelPath = baseDir.resolve(
                "src/main/resources/assets/mc3ver/models/block/tent_canvas_wall_crafting_table_right.json");

        assertTrue(java.nio.file.Files.exists(modelPath), "tent_canvas_wall_crafting_table_right.json muss existieren");

        String content = java.nio.file.Files.readString(modelPath);
        com.google.gson.JsonObject json =
                com.google.gson.JsonParser.parseString(content).getAsJsonObject();
        com.google.gson.JsonObject textures = json.getAsJsonObject("textures");
        assertNotNull(textures, "textures Objekt muss vorhanden sein");
        assertEquals("mc3ver:block/tent_canvas", textures.get("canvas").getAsString());
        assertEquals(
                "minecraft:block/crafting_table_top", textures.get("table_top").getAsString());
        assertEquals(
                "minecraft:block/crafting_table_front",
                textures.get("table_front").getAsString());
        assertEquals(
                "minecraft:block/crafting_table_side",
                textures.get("table_side").getAsString());
        assertEquals("minecraft:block/oak_planks", textures.get("table_bottom").getAsString());

        com.google.gson.JsonArray elements = json.getAsJsonArray("elements");
        assertNotNull(elements, "elements Array muss vorhanden sein");
        assertTrue(elements.size() >= 2, "Muss mindestens 2 Elemente haben: Wand und Craftingtable");

        boolean foundCanvasWall = false;
        boolean foundTable = false;

        for (com.google.gson.JsonElement el : elements) {
            com.google.gson.JsonObject elObj = el.getAsJsonObject();
            com.google.gson.JsonArray from = elObj.getAsJsonArray("from");
            com.google.gson.JsonArray to = elObj.getAsJsonArray("to");

            double fromX = from.get(0).getAsDouble();
            double toX = to.get(0).getAsDouble();
            double toY = to.get(1).getAsDouble();

            // Wand an x=14..16, y bis 16
            if (fromX >= 13.9 && toX <= 16.1 && toY >= 15.9) {
                foundCanvasWall = true;
                com.google.gson.JsonObject faces = elObj.getAsJsonObject("faces");
                for (String faceName : faces.keySet()) {
                    com.google.gson.JsonObject faceObj = faces.getAsJsonObject(faceName);
                    assertTrue(faceObj.has("tintindex"), "Wandface " + faceName + " muss tintindex haben");
                    assertEquals(0, faceObj.get("tintindex").getAsInt());
                }
            }

            // Tisch an x=0..14
            if (fromX <= 0.1 && toX >= 13.9) {
                foundTable = true;
                com.google.gson.JsonObject faces = elObj.getAsJsonObject("faces");
                assertTrue(faces.has("up"), "Tisch muss 'up' Face haben");
                assertEquals(
                        "#table_top", faces.getAsJsonObject("up").get("texture").getAsString());
                assertTrue(faces.has("west"), "Tisch muss 'west' Face haben");
                assertEquals(
                        "#table_front",
                        faces.getAsJsonObject("west").get("texture").getAsString());
            }
        }

        assertTrue(foundCanvasWall, "Muss Außenwand bei x=14..16 mit Farbtönung haben");
        assertTrue(foundTable, "Muss Tisch bei x=0..14 mit Craftingtable-Texturen haben");
    }

    @Test
    void testCraftingTableBlockstateAndCollision() throws Exception {
        net.minecraft.SharedConstants.tryDetectVersion();
        net.minecraft.server.Bootstrap.bootStrap();

        assertEquals(
                ExplorerTentLogic.TentPart.WALL_CRAFTING_TABLE_RIGHT,
                TentCanvasBlock.CanvasPart.WALL_CRAFTING_TABLE_RIGHT.toTentPart());

        // Kollision für WALL_CRAFTING_TABLE_RIGHT
        net.minecraft.world.phys.shapes.VoxelShape shapeNorth = TentCanvasBlock.getWallShape(
                TentCanvasBlock.CanvasPart.WALL_CRAFTING_TABLE_RIGHT, net.minecraft.core.Direction.NORTH);

        // Wand bei x=14..16, z=0..16, y=0..16
        net.minecraft.world.phys.AABB wallBox =
                new net.minecraft.world.phys.AABB(14.0 / 16.0, 0.0, 0.0, 16.0 / 16.0, 16.0 / 16.0, 16.0 / 16.0);
        assertTrue(
                net.minecraft.world.phys.shapes.Shapes.joinIsNotEmpty(
                        shapeNorth,
                        net.minecraft.world.phys.shapes.Shapes.create(wallBox),
                        net.minecraft.world.phys.shapes.BooleanOp.AND),
                "WALL_CRAFTING_TABLE_RIGHT muss an der Außenwand kollidieren");

        // Tisch bei x=0..14, y=0..14, z=1..15
        net.minecraft.world.phys.AABB tableBox = new net.minecraft.world.phys.AABB(
                1.0 / 16.0, 1.0 / 16.0, 2.0 / 16.0, 13.0 / 16.0, 13.0 / 16.0, 14.0 / 16.0);
        assertTrue(
                net.minecraft.world.phys.shapes.Shapes.joinIsNotEmpty(
                        shapeNorth,
                        net.minecraft.world.phys.shapes.Shapes.create(tableBox),
                        net.minecraft.world.phys.shapes.BooleanOp.AND),
                "WALL_CRAFTING_TABLE_RIGHT muss am Tisch kollidieren");

        // Blockstate tent_canvas.json prüfen
        java.nio.file.Path baseDir = java.nio.file.Paths.get("").toAbsolutePath();
        java.nio.file.Path blockstatePath =
                baseDir.resolve("src/main/resources/assets/mc3ver/blockstates/tent_canvas.json");
        String content = java.nio.file.Files.readString(blockstatePath);
        com.google.gson.JsonObject json =
                com.google.gson.JsonParser.parseString(content).getAsJsonObject();
        com.google.gson.JsonArray multipart = json.getAsJsonArray("multipart");

        java.util.Set<String> foundFacings = new java.util.HashSet<>();
        for (com.google.gson.JsonElement ruleEl : multipart) {
            com.google.gson.JsonObject rule = ruleEl.getAsJsonObject();
            if (rule.has("when")) {
                com.google.gson.JsonObject when = rule.getAsJsonObject("when");
                if (when.has("part")
                        && "wall_crafting_table_right".equals(when.get("part").getAsString())) {
                    if (when.has("facing")) {
                        foundFacings.add(when.get("facing").getAsString());
                        com.google.gson.JsonObject apply = rule.getAsJsonObject("apply");
                        assertEquals(
                                "mc3ver:block/tent_canvas_wall_crafting_table_right",
                                apply.get("model").getAsString());
                    }
                }
            }
        }

        assertTrue(foundFacings.contains("north"), "Blockstate muss wall_crafting_table_right für north definieren");
        assertTrue(foundFacings.contains("east"), "Blockstate muss wall_crafting_table_right für east definieren");
        assertTrue(foundFacings.contains("south"), "Blockstate muss wall_crafting_table_right für south definieren");
        assertTrue(foundFacings.contains("west"), "Blockstate muss wall_crafting_table_right für west definieren");
    }

    @Test
    void testCraftingTableMenuProviderAndInteraction() {
        net.minecraft.SharedConstants.tryDetectVersion();
        net.minecraft.server.Bootstrap.bootStrap();

        assertTrue(
                TentCanvasBlock.hasMenuProvider(TentCanvasBlock.CanvasPart.WALL_CRAFTING_TABLE_RIGHT),
                "WALL_CRAFTING_TABLE_RIGHT muss einen MenuProvider besitzen");
        assertFalse(
                TentCanvasBlock.hasMenuProvider(TentCanvasBlock.CanvasPart.WALL_RIGHT),
                "Normaler Wandteil darf keinen MenuProvider besitzen");

        net.minecraft.core.BlockPos pos = new net.minecraft.core.BlockPos(10, 64, 20);
        net.minecraft.world.MenuProvider provider = TentCanvasBlock.createCraftingMenuProvider(null, pos);
        assertNotNull(provider, "createCraftingMenuProvider darf nicht null liefern");
        assertEquals(
                net.minecraft.network.chat.Component.translatable("container.crafting"), provider.getDisplayName());
    }

    @Test
    void testFurnaceSitsOnLeftWallNextToFoot() {
        assertEquals(
                ExplorerTentLogic.TentPart.WALL_FURNACE_LEFT,
                ExplorerTentLogic.getTentPart(-1, 0, 1),
                "relX=-1, relY=0, relZ=1 (links neben Fußende) muss WALL_FURNACE_LEFT sein");
        assertEquals(
                ExplorerTentLogic.TentPart.WALL_LEFT,
                ExplorerTentLogic.getTentPart(-1, 0, 2),
                "Neben dem Kissen (-1, 0, 2) darf kein Ofen stehen");
        assertTrue(ExplorerTentLogic.isFurnace(ExplorerTentLogic.TentPart.WALL_FURNACE_LEFT));
        assertFalse(ExplorerTentLogic.isFurnace(ExplorerTentLogic.TentPart.WALL_LEFT));
        assertFalse(ExplorerTentLogic.isFurnace(ExplorerTentLogic.TentPart.WALL_CRAFTING_TABLE_RIGHT));
        assertTrue(
                ExplorerTentLogic.hasFullBlockCollision(ExplorerTentLogic.TentPart.WALL_FURNACE_LEFT),
                "Ofen-Wandteil muss solide Kollision haben");
    }

    @Test
    void testTentFurnaceFacesIntoTent() {
        net.minecraft.SharedConstants.tryDetectVersion();
        net.minecraft.server.Bootstrap.bootStrap();

        // Ofen hängt an der linken Wand, seine Front zeigt zum Mittelgang (rechts von der Zelt-Blickrichtung)
        assertEquals(
                net.minecraft.core.Direction.EAST,
                TentFurnaceBlock.getFrontDirection(net.minecraft.core.Direction.NORTH));
        assertEquals(
                net.minecraft.core.Direction.SOUTH,
                TentFurnaceBlock.getFrontDirection(net.minecraft.core.Direction.EAST));
        assertEquals(
                net.minecraft.core.Direction.WEST,
                TentFurnaceBlock.getFrontDirection(net.minecraft.core.Direction.SOUTH));
        assertEquals(
                net.minecraft.core.Direction.NORTH,
                TentFurnaceBlock.getFrontDirection(net.minecraft.core.Direction.WEST));
    }

    @Test
    void testTentFurnaceCollisionCoversWallAndFurnace() {
        net.minecraft.SharedConstants.tryDetectVersion();
        net.minecraft.server.Bootstrap.bootStrap();

        net.minecraft.world.phys.shapes.VoxelShape shapeNorth =
                TentFurnaceBlock.getFurnaceShape(net.minecraft.core.Direction.NORTH);

        // Linke Außenwand bei x=0..2
        net.minecraft.world.phys.AABB wallBox = new net.minecraft.world.phys.AABB(0.0, 0.0, 0.0, 2.0 / 16.0, 1.0, 1.0);
        assertTrue(
                net.minecraft.world.phys.shapes.Shapes.joinIsNotEmpty(
                        shapeNorth,
                        net.minecraft.world.phys.shapes.Shapes.create(wallBox),
                        net.minecraft.world.phys.shapes.BooleanOp.AND),
                "Ofen-Wandteil muss an der linken Außenwand kollidieren");

        // Ofenkörper im Inneren bei x=2..16
        net.minecraft.world.phys.AABB furnaceBox = new net.minecraft.world.phys.AABB(
                4.0 / 16.0, 2.0 / 16.0, 4.0 / 16.0, 12.0 / 16.0, 12.0 / 16.0, 12.0 / 16.0);
        assertTrue(
                net.minecraft.world.phys.shapes.Shapes.joinIsNotEmpty(
                        shapeNorth,
                        net.minecraft.world.phys.shapes.Shapes.create(furnaceBox),
                        net.minecraft.world.phys.shapes.BooleanOp.AND),
                "Ofenkörper muss Kollision haben");
    }

    @Test
    void testTentFurnaceResourcesExist() throws Exception {
        java.nio.file.Path res =
                java.nio.file.Paths.get("").toAbsolutePath().resolve("src/main/resources/assets/mc3ver");

        java.nio.file.Path blockstate = res.resolve("blockstates/tent_furnace.json");
        assertTrue(java.nio.file.Files.exists(blockstate), "blockstates/tent_furnace.json muss existieren");
        com.google.gson.JsonObject variants = com.google.gson.JsonParser.parseString(
                        java.nio.file.Files.readString(blockstate))
                .getAsJsonObject()
                .getAsJsonObject("variants");
        for (String facing : new String[] {"north", "east", "south", "west"}) {
            for (String lit : new String[] {"false", "true"}) {
                assertTrue(
                        variants.has("facing=" + facing + ",lit=" + lit),
                        "Blockstate muss Variante facing=" + facing + ",lit=" + lit + " definieren");
            }
        }

        String baseModel = java.nio.file.Files.readString(res.resolve("models/block/tent_furnace_left.json"));
        assertTrue(baseModel.contains("\"tintindex\": 0"), "tent_furnace_left muss die Zeltplane einfärbbar machen");
        assertTrue(
                baseModel.contains("\"minecraft:block/furnace_front\""),
                "tent_furnace_left muss die Ofenfront verwenden");

        // Brennender Ofen erbt das Grundmodell und tauscht nur die Front
        String litModel = java.nio.file.Files.readString(res.resolve("models/block/tent_furnace_left_on.json"));
        assertTrue(
                litModel.contains("\"mc3ver:block/tent_furnace_left\""),
                "tent_furnace_left_on muss vom Grundmodell erben");
        assertTrue(
                litModel.contains("\"minecraft:block/furnace_front_on\""),
                "tent_furnace_left_on muss die brennende Ofenfront verwenden");

        for (String lang : new String[] {"de_de", "en_us"}) {
            String content = java.nio.file.Files.readString(res.resolve("lang/" + lang + ".json"));
            assertTrue(
                    content.contains("\"block.mc3ver.tent_furnace\""),
                    lang + " muss block.mc3ver.tent_furnace übersetzen");
        }
    }

    @Test
    void testStovepipeRoofSitsAboveFurnace() {
        assertEquals(
                ExplorerTentLogic.TentPart.ROOF_PIPE_LEFT,
                ExplorerTentLogic.getTentPart(-1, 1, 1),
                "Direkt über dem Ofen (-1, 1, 1) muss die Dachschräge mit Ofenrohr sitzen");
        assertEquals(ExplorerTentLogic.TentPart.WALL_FURNACE_LEFT, ExplorerTentLogic.getTentPart(-1, 0, 1));
        assertTrue(ExplorerTentLogic.hasFullBlockCollision(ExplorerTentLogic.TentPart.ROOF_PIPE_LEFT));
        assertEquals(ExplorerTentLogic.TentPart.ROOF_PIPE_LEFT, TentCanvasBlock.CanvasPart.ROOF_PIPE_LEFT.toTentPart());
    }

    @Test
    void testStovepipeRoofKeepsSlopeCollision() {
        net.minecraft.SharedConstants.tryDetectVersion();
        net.minecraft.server.Bootstrap.bootStrap();

        for (net.minecraft.core.Direction facing : net.minecraft.core.Direction.Plane.HORIZONTAL) {
            net.minecraft.world.phys.shapes.VoxelShape slope =
                    TentCanvasBlock.getWallShape(TentCanvasBlock.CanvasPart.ROOF_LEFT, facing);
            net.minecraft.world.phys.shapes.VoxelShape pipe =
                    TentCanvasBlock.getWallShape(TentCanvasBlock.CanvasPart.ROOF_PIPE_LEFT, facing);
            assertFalse(
                    net.minecraft.world.phys.shapes.Shapes.joinIsNotEmpty(
                            slope, pipe, net.minecraft.world.phys.shapes.BooleanOp.NOT_SAME),
                    "Dachschräge mit Ofenrohr muss dieselbe Kollision wie ROOF_LEFT haben (" + facing + ")");
        }
    }

    @Test
    void testStovepipeSmokeRisesFromPipeTop() {
        net.minecraft.SharedConstants.tryDetectVersion();
        net.minecraft.server.Bootstrap.bootStrap();

        // Rohrmitte liegt im Nord-Modell bei x=6, z=8 (Pixel), Oberkante 24 Pixel über der Dachblock-Unterkante
        assertPipeTop(net.minecraft.core.Direction.NORTH, 6.0 / 16.0, 8.0 / 16.0);
        assertPipeTop(net.minecraft.core.Direction.EAST, 8.0 / 16.0, 6.0 / 16.0);
        assertPipeTop(net.minecraft.core.Direction.SOUTH, 10.0 / 16.0, 8.0 / 16.0);
        assertPipeTop(net.minecraft.core.Direction.WEST, 8.0 / 16.0, 10.0 / 16.0);
    }

    private static void assertPipeTop(net.minecraft.core.Direction facing, double expectedX, double expectedZ) {
        double[] top = TentFurnaceBlock.getPipeTopOffset(facing);
        assertEquals(expectedX, top[0], 1e-9, "x-Offset der Rohröffnung bei " + facing);
        assertEquals(1.0 + 24.0 / 16.0, top[1], 1e-9, "y-Offset der Rohröffnung bei " + facing);
        assertEquals(expectedZ, top[2], 1e-9, "z-Offset der Rohröffnung bei " + facing);
    }

    @Test
    void testFurnaceAndPipeKeepOriginalColors() throws Exception {
        java.nio.file.Path models =
                java.nio.file.Paths.get("").toAbsolutePath().resolve("src/main/resources/assets/mc3ver/models/block");

        // Ofenkörper und Rohr dürfen nicht in Zeltfarbe getönt werden, nur die Zeltplane
        for (String model : new String[] {"tent_furnace_left", "tent_canvas_roof_pipe_left"}) {
            com.google.gson.JsonObject json = com.google.gson.JsonParser.parseString(
                            java.nio.file.Files.readString(models.resolve(model + ".json")))
                    .getAsJsonObject();
            boolean hasPipe = false;
            for (com.google.gson.JsonElement el : json.getAsJsonArray("elements")) {
                com.google.gson.JsonObject faces = el.getAsJsonObject().getAsJsonObject("faces");
                for (String face : faces.keySet()) {
                    com.google.gson.JsonObject f = faces.getAsJsonObject(face);
                    String texture = f.get("texture").getAsString();
                    if (texture.equals("#pipe")) hasPipe = true;
                    assertEquals(
                            texture.equals("#canvas"),
                            f.has("tintindex"),
                            model + ": nur #canvas darf getönt sein, Face " + face + " nutzt " + texture);
                }
            }
            assertTrue(hasPipe, model + " muss ein Rohrstück (#pipe) enthalten");
            assertEquals(
                    "minecraft:block/anvil",
                    json.getAsJsonObject("textures").get("pipe").getAsString());
        }

        String canvasStates =
                java.nio.file.Files.readString(models.getParent().getParent().resolve("blockstates/tent_canvas.json"));
        assertTrue(
                canvasStates.contains("mc3ver:block/tent_canvas_roof_pipe_left"),
                "tent_canvas Blockstate muss das Ofenrohr-Dachmodell verwenden");
    }

    @Test
    void characterizeCompleteTentLayout() {
        // Grundriss je Ebene, Zeilen = relZ 0..3 (Eingang -> Rückwand), Spalten = relX -1, 0, 1
        String[][] expected = {
            {
                "WALL_FRONT_LEFT ENTRANCE_FLAP WALL_FRONT_RIGHT",
                "WALL_FURNACE_LEFT SLEEPING_BAG_FOOT WALL_CHEST_RIGHT",
                "WALL_LEFT SLEEPING_BAG_HEAD WALL_CRAFTING_TABLE_RIGHT",
                "WALL_CORNER_LEFT WALL_BACK WALL_CORNER_RIGHT"
            },
            {
                "ROOF_FRONT_LEFT ROOF_FRONT ROOF_FRONT_RIGHT",
                "ROOF_PIPE_LEFT ROOF_RIDGE ROOF_RIGHT",
                "ROOF_LEFT ROOF_RIDGE ROOF_LANTERN_RIGHT",
                "ROOF_CORNER_LEFT ROOF_BACK ROOF_CORNER_RIGHT"
            }
        };
        for (int relY = 0; relY < ExplorerTentLogic.HEIGHT; relY++) {
            for (int relZ = 0; relZ < ExplorerTentLogic.LENGTH; relZ++) {
                StringBuilder row = new StringBuilder();
                for (int relX = -1; relX <= 1; relX++) {
                    if (relX > -1) row.append(' ');
                    row.append(ExplorerTentLogic.getTentPart(relX, relY, relZ).name());
                }
                assertEquals(expected[relY][relZ], row.toString(), "Ebene " + relY + ", relZ " + relZ);
            }
        }
        // Außerhalb des Zeltvolumens ist alles Luft
        assertEquals(ExplorerTentLogic.TentPart.AIR, ExplorerTentLogic.getTentPart(0, 2, 1));
        assertEquals(ExplorerTentLogic.TentPart.AIR, ExplorerTentLogic.getTentPart(0, -1, 1));
        assertEquals(ExplorerTentLogic.TentPart.AIR, ExplorerTentLogic.getTentPart(2, 0, 1));
        assertEquals(ExplorerTentLogic.TentPart.AIR, ExplorerTentLogic.getTentPart(0, 0, 4));
    }

    @Test
    void characterizeCanvasPartMapping() {
        net.minecraft.SharedConstants.tryDetectVersion();
        net.minecraft.server.Bootstrap.bootStrap();

        // Jede Zeltplanen-Variante steht für genau ein Layout-Teil
        java.util.Set<ExplorerTentLogic.TentPart> seen = new java.util.HashSet<>();
        for (TentCanvasBlock.CanvasPart canvasPart : TentCanvasBlock.CanvasPart.values()) {
            assertTrue(seen.add(canvasPart.toTentPart()), "Doppelte Zuordnung für " + canvasPart);
            assertEquals(canvasPart.name(), canvasPart.toTentPart().name(), "CanvasPart und TentPart heißen gleich");
            assertEquals(
                    java.util.Optional.of(canvasPart),
                    TentCanvasBlock.CanvasPart.fromTentPart(canvasPart.toTentPart()),
                    "fromTentPart muss die Zuordnung umkehren: " + canvasPart);
        }
        assertTrue(
                TentCanvasBlock.CanvasPart.fromTentPart(ExplorerTentLogic.TentPart.WALL_FURNACE_LEFT)
                        .isEmpty(),
                "Eigene Blöcke (Ofen) sind keine Zeltplane");
    }

    private static double averageBrightness(java.awt.image.BufferedImage img, int fromX, int toX) {
        double sum = 0;
        int count = 0;
        for (int y = 0; y < img.getHeight(); y++) {
            for (int x = fromX; x <= toX; x++) {
                int rgb = img.getRGB(x, y);
                sum += ((rgb >> 16) & 0xFF) + ((rgb >> 8) & 0xFF) + (rgb & 0xFF);
                count += 3;
            }
        }
        return sum / count;
    }

    @Test
    void testCanvasTexturesHaveSeamsAndDarkerRoof() throws Exception {
        java.nio.file.Path textures =
                java.nio.file.Paths.get("").toAbsolutePath().resolve("src/main/resources/assets/mc3ver/textures/block");
        java.awt.image.BufferedImage wall =
                javax.imageio.ImageIO.read(textures.resolve("tent_canvas.png").toFile());
        java.awt.image.BufferedImage roof = javax.imageio.ImageIO.read(
                textures.resolve("tent_canvas_roof.png").toFile());

        assertEquals(16, roof.getWidth());
        assertEquals(16, roof.getHeight());
        assertTrue(
                averageBrightness(roof, 0, 15) < averageBrightness(wall, 0, 15) * 0.9,
                "Dachplane muss sichtbar dunkler sein als die Wandplane");

        for (java.awt.image.BufferedImage img : new java.awt.image.BufferedImage[] {wall, roof}) {
            double fabric = averageBrightness(img, 4, 11);
            assertTrue(
                    averageBrightness(img, 15, 15) < fabric - 16,
                    "Falz an der Blockkante muss dunkler als der Stoff sein");
            assertTrue(
                    averageBrightness(img, 1, 1) < fabric - 4, "Steppnaht neben der Kante muss sich vom Stoff abheben");
        }
    }

    @Test
    void testRoofModelsUseRoofCanvasAndWallsUseWallCanvas() throws Exception {
        java.nio.file.Path models =
                java.nio.file.Paths.get("").toAbsolutePath().resolve("src/main/resources/assets/mc3ver/models/block");
        try (java.util.stream.Stream<java.nio.file.Path> files = java.nio.file.Files.list(models)) {
            for (java.nio.file.Path model : files.filter(
                            f -> f.getFileName().toString().startsWith("tent_canvas_"))
                    .toList()) {
                String name = model.getFileName().toString();
                String canvas = com.google.gson.JsonParser.parseString(java.nio.file.Files.readString(model))
                        .getAsJsonObject()
                        .getAsJsonObject("textures")
                        .get("canvas")
                        .getAsString();
                assertEquals(
                        name.startsWith("tent_canvas_roof_")
                                ? "mc3ver:block/tent_canvas_roof"
                                : "mc3ver:block/tent_canvas",
                        canvas,
                        name + " nutzt die falsche Planen-Textur");
            }
        }
    }

    @Test
    void testLanternHangsAboveCraftingTableAndGlows() {
        net.minecraft.SharedConstants.tryDetectVersion();
        net.minecraft.server.Bootstrap.bootStrap();

        // Über der Werkbank steht niemand, die Kamera ragt also nie in die Laterne
        assertEquals(ExplorerTentLogic.TentPart.ROOF_LANTERN_RIGHT, ExplorerTentLogic.getTentPart(1, 1, 2));
        assertEquals(ExplorerTentLogic.TentPart.WALL_CRAFTING_TABLE_RIGHT, ExplorerTentLogic.getTentPart(1, 0, 2));
        assertTrue(ExplorerTentLogic.hasFullBlockCollision(ExplorerTentLogic.TentPart.ROOF_LANTERN_RIGHT));
        assertEquals(14, TentCanvasBlock.lightEmission(TentCanvasBlock.CanvasPart.ROOF_LANTERN_RIGHT));
        for (TentCanvasBlock.CanvasPart part : TentCanvasBlock.CanvasPart.values()) {
            if (part != TentCanvasBlock.CanvasPart.ROOF_LANTERN_RIGHT) {
                assertEquals(0, TentCanvasBlock.lightEmission(part), part + " darf nicht leuchten");
            }
        }
        // Gleiche Kollision wie die normale rechte Dachschräge
        for (net.minecraft.core.Direction facing : net.minecraft.core.Direction.Plane.HORIZONTAL) {
            assertFalse(
                    net.minecraft.world.phys.shapes.Shapes.joinIsNotEmpty(
                            TentCanvasBlock.getWallShape(TentCanvasBlock.CanvasPart.ROOF_RIGHT, facing),
                            TentCanvasBlock.getWallShape(TentCanvasBlock.CanvasPart.ROOF_LANTERN_RIGHT, facing),
                            net.minecraft.world.phys.shapes.BooleanOp.NOT_SAME),
                    "Kollision Laternen-Dachschräge " + facing);
        }
    }

    @Test
    void testLanternResources() throws Exception {
        java.nio.file.Path res =
                java.nio.file.Paths.get("").toAbsolutePath().resolve("src/main/resources/assets/mc3ver");
        String model = java.nio.file.Files.readString(res.resolve("models/block/tent_canvas_roof_lantern_right.json"));
        assertTrue(
                model.contains("\"minecraft:block/lantern\""), "Laternenmodell muss die Vanilla-Laternentextur nutzen");
        assertTrue(model.contains("\"mc3ver:block/tent_canvas_roof\""), "Dachschräge muss die Dachplane nutzen");
        // Laterne hängt unter der Schräge über der Werkbank, nicht außerhalb des Blocks
        com.google.gson.JsonArray elements =
                com.google.gson.JsonParser.parseString(model).getAsJsonObject().getAsJsonArray("elements");
        for (com.google.gson.JsonElement el : elements) {
            com.google.gson.JsonObject e = el.getAsJsonObject();
            if (e.toString().contains("#lantern")) {
                assertTrue(
                        e.getAsJsonArray("from").get(2).getAsDouble() >= 0
                                && e.getAsJsonArray("to").get(2).getAsDouble() <= 16,
                        "Laterne muss innerhalb des Dachblocks hängen");
                assertTrue(
                        e.getAsJsonArray("to").get(1).getAsDouble() <= 10.5,
                        "Laterne muss unter der Dachschräge hängen");
            }
        }
        String states = java.nio.file.Files.readString(res.resolve("blockstates/tent_canvas.json"));
        assertEquals(
                4,
                states.split("\"part\": \"roof_lantern_right\"", -1).length - 1,
                "4 Blockstate-Einträge (je Richtung)");
    }

    @Test
    void testEntranceFlapOpensAndCloses() {
        net.minecraft.SharedConstants.tryDetectVersion();
        net.minecraft.server.Bootstrap.bootStrap();

        for (net.minecraft.core.Direction facing : net.minecraft.core.Direction.Plane.HORIZONTAL) {
            assertTrue(
                    TentFlapBlock.getCollision(facing, true).isEmpty(),
                    "Offene Plane darf nicht blockieren (" + facing + ")");
            net.minecraft.world.phys.shapes.VoxelShape closed = TentFlapBlock.getCollision(facing, false);
            assertFalse(closed.isEmpty(), "Geschlossene Plane muss blockieren (" + facing + ")");
            // Geschlossene Plane liegt an der Außenkante des Eingangs, wie die Rückwand an ihrer Kante
            assertFalse(
                    net.minecraft.world.phys.shapes.Shapes.joinIsNotEmpty(
                            closed,
                            TentCanvasBlock.getWallShape(TentCanvasBlock.CanvasPart.WALL_BACK, facing),
                            net.minecraft.world.phys.shapes.BooleanOp.NOT_SAME),
                    "Plane an der Außenkante (" + facing + ")");
        }
        assertTrue(TentFlapBlock.isPassable(true), "Mobs dürfen durch die offene Plane");
        assertFalse(TentFlapBlock.isPassable(false), "Mobs dürfen nicht durch die geschlossene Plane");
    }

    @Test
    void testEntranceFlapResources() throws Exception {
        java.nio.file.Path res =
                java.nio.file.Paths.get("").toAbsolutePath().resolve("src/main/resources/assets/mc3ver");
        com.google.gson.JsonObject variants = com.google.gson.JsonParser.parseString(
                        java.nio.file.Files.readString(res.resolve("blockstates/tent_flap.json")))
                .getAsJsonObject()
                .getAsJsonObject("variants");
        for (String facing : new String[] {"north", "east", "south", "west"}) {
            assertEquals(
                    "mc3ver:block/tent_flap_closed",
                    variants.getAsJsonObject("facing=" + facing + ",open=false")
                            .get("model")
                            .getAsString());
            assertEquals(
                    "mc3ver:block/tent_flap_open",
                    variants.getAsJsonObject("facing=" + facing + ",open=true")
                            .get("model")
                            .getAsString());
        }
        for (String model : new String[] {"tent_flap_closed", "tent_flap_open"}) {
            String content = java.nio.file.Files.readString(res.resolve("models/block/" + model + ".json"));
            assertTrue(content.contains("\"tintindex\": 0"), model + " muss in Zeltfarbe getönt sein");
        }
        for (String lang : new String[] {"de_de", "en_us"}) {
            assertTrue(java.nio.file.Files.readString(res.resolve("lang/" + lang + ".json"))
                    .contains("\"block.mc3ver.tent_flap\""));
        }

        // Plane reicht vom Boden bis unter das Dach (Dachunterkante im Block darüber bei y=30)
        assertEquals(0.0, modelBound(res, "tent_flap_closed", 1, true), 1e-9, "Geschlossene Plane beginnt am Boden");
        assertEquals(
                30.0,
                modelBound(res, "tent_flap_closed", 1, false),
                1e-9,
                "Geschlossene Plane reicht bis unter das Dach");
        assertEquals(30.0, modelBound(res, "tent_flap_open", 1, false), 1e-9, "Aufgerollte Plane hängt unter dem Dach");
        assertTrue(
                modelBound(res, "tent_flap_open", 1, true) >= 24.0,
                "Aufgerollte Plane darf nicht in den Durchgang hängen");
    }

    private static double modelBound(java.nio.file.Path res, String model, int axis, boolean min) throws Exception {
        com.google.gson.JsonArray elements = com.google.gson.JsonParser.parseString(
                        java.nio.file.Files.readString(res.resolve("models/block/" + model + ".json")))
                .getAsJsonObject()
                .getAsJsonArray("elements");
        double bound = min ? Double.MAX_VALUE : -Double.MAX_VALUE;
        for (com.google.gson.JsonElement el : elements) {
            double from = el.getAsJsonObject().getAsJsonArray("from").get(axis).getAsDouble();
            double to = el.getAsJsonObject().getAsJsonArray("to").get(axis).getAsDouble();
            bound = min ? Math.min(bound, from) : Math.max(bound, to);
        }
        return bound;
    }

    @Test
    void testChestStorageRoundTripKeepsItemsInTentItem() {
        bootstrapWithItemComponents();

        // Beliebiges Vanilla-Item als Träger, das Zelt-Item lässt sich im Test nicht registrieren
        net.minecraft.world.item.ItemStack carrier =
                new net.minecraft.world.item.ItemStack(net.minecraft.world.item.Items.PAPER);
        java.util.List<net.minecraft.world.item.ItemStack> items = new java.util.ArrayList<>();
        for (int i = 0; i < TentChestStorage.SIZE; i++) items.add(net.minecraft.world.item.ItemStack.EMPTY);
        items.set(0, new net.minecraft.world.item.ItemStack(net.minecraft.world.item.Items.DIAMOND, 5));
        items.set(26, new net.minecraft.world.item.ItemStack(net.minecraft.world.item.Items.BREAD, 12));

        TentChestStorage.store(carrier, items);
        net.minecraft.core.NonNullList<net.minecraft.world.item.ItemStack> restored = TentChestStorage.restore(carrier);
        assertEquals(TentChestStorage.SIZE, restored.size());
        assertTrue(net.minecraft.world.item.ItemStack.matches(items.get(0), restored.get(0)), "Diamanten in Slot 0");
        assertTrue(net.minecraft.world.item.ItemStack.matches(items.get(26), restored.get(26)), "Brot in Slot 26");
        assertTrue(restored.get(13).isEmpty());

        // Leere Truhe hinterlässt keine Komponente am Item
        java.util.List<net.minecraft.world.item.ItemStack> empty = new java.util.ArrayList<>();
        for (int i = 0; i < TentChestStorage.SIZE; i++) empty.add(net.minecraft.world.item.ItemStack.EMPTY);
        TentChestStorage.store(carrier, empty);
        assertFalse(
                carrier.has(net.minecraft.core.component.DataComponents.CONTAINER),
                "Leere Truhe darf keinen Inhalt speichern");
    }

    @Test
    void testDyeingKeepsChestContentsAndSetsExactColor() {
        bootstrapWithItemComponents();

        net.minecraft.world.item.ItemStack carrier =
                new net.minecraft.world.item.ItemStack(net.minecraft.world.item.Items.PAPER);
        java.util.List<net.minecraft.world.item.ItemStack> items = new java.util.ArrayList<>();
        for (int i = 0; i < TentChestStorage.SIZE; i++) items.add(net.minecraft.world.item.ItemStack.EMPTY);
        items.set(3, new net.minecraft.world.item.ItemStack(net.minecraft.world.item.Items.TORCH, 32));
        TentChestStorage.store(carrier, items);
        carrier.set(
                net.minecraft.core.component.DataComponents.DYED_COLOR,
                new net.minecraft.world.item.component.DyedItemColor(ExplorerTentLogic.getColorRgb("white")));

        net.minecraft.world.item.ItemStack dyed = TentDyeRecipe.dye(carrier, net.minecraft.world.item.DyeColor.RED);
        assertEquals(
                ExplorerTentLogic.getColorRgb("red"),
                dyed.get(net.minecraft.core.component.DataComponents.DYED_COLOR).rgb(),
                "Färben setzt die exakte Zeltfarbe statt zu mischen");
        assertTrue(
                net.minecraft.world.item.ItemStack.matches(
                        items.get(3), TentChestStorage.restore(dyed).get(3)),
                "Truheninhalt muss beim Färben erhalten bleiben");
        assertEquals(1, dyed.getCount());
    }

    @Test
    void testChestCollisionCoversWallAndBox() {
        net.minecraft.SharedConstants.tryDetectVersion();
        net.minecraft.server.Bootstrap.bootStrap();

        for (net.minecraft.core.Direction facing : net.minecraft.core.Direction.Plane.HORIZONTAL) {
            net.minecraft.world.phys.shapes.VoxelShape shape = TentChestBlock.getChestShape(facing);
            assertTrue(
                    net.minecraft.world.phys.shapes.Shapes.joinIsNotEmpty(
                            shape,
                            TentCanvasBlock.getWallShape(TentCanvasBlock.CanvasPart.WALL_RIGHT, facing),
                            net.minecraft.world.phys.shapes.BooleanOp.AND),
                    "Truhe hat die rechte Zeltwand (" + facing + ")");
            net.minecraft.world.phys.AABB center =
                    new net.minecraft.world.phys.AABB(6 / 16.0, 2 / 16.0, 6 / 16.0, 10 / 16.0, 8 / 16.0, 10 / 16.0);
            assertTrue(
                    net.minecraft.world.phys.shapes.Shapes.joinIsNotEmpty(
                            shape,
                            net.minecraft.world.phys.shapes.Shapes.create(center),
                            net.minecraft.world.phys.shapes.BooleanOp.AND),
                    "Kistenkörper in der Blockmitte (" + facing + ")");
            assertTrue(shape.max(net.minecraft.core.Direction.Axis.Y) <= 1.0);
        }
    }

    @Test
    void testChestDyeRecipeAndResources() throws Exception {
        java.nio.file.Path base = java.nio.file.Paths.get("").toAbsolutePath().resolve("src/main/resources");
        java.nio.file.Path recipes = base.resolve("data/mc3ver/recipe");
        assertEquals(
                "mc3ver:tent_dye",
                com.google.gson.JsonParser.parseString(
                                java.nio.file.Files.readString(recipes.resolve("explorer_tent_dye.json")))
                        .getAsJsonObject()
                        .get("type")
                        .getAsString());
        try (java.util.stream.Stream<java.nio.file.Path> files = java.nio.file.Files.list(recipes)) {
            assertEquals(
                    0,
                    files.filter(f -> f.getFileName().toString().startsWith("explorer_tent_dye_"))
                            .count(),
                    "Die alten Einzel-Färberezepte verlieren den Truheninhalt und müssen weg sein");
        }

        java.nio.file.Path assets = base.resolve("assets/mc3ver");
        com.google.gson.JsonObject variants = com.google.gson.JsonParser.parseString(
                        java.nio.file.Files.readString(assets.resolve("blockstates/tent_chest.json")))
                .getAsJsonObject()
                .getAsJsonObject("variants");
        for (String facing : new String[] {"north", "east", "south", "west"}) {
            assertEquals(
                    "mc3ver:block/tent_chest_right",
                    variants.getAsJsonObject("facing=" + facing + ",open=false")
                            .get("model")
                            .getAsString());
        }
        String model = java.nio.file.Files.readString(assets.resolve("models/block/tent_chest_right.json"));
        assertTrue(model.contains("\"tintindex\": 0"), "Zeltwand hinter der Truhe in Zeltfarbe");
        for (String lang : new String[] {"de_de", "en_us"}) {
            assertTrue(java.nio.file.Files.readString(assets.resolve("lang/" + lang + ".json"))
                    .contains("\"block.mc3ver.tent_chest\""));
        }
    }

    @Test
    void testStakesAndGuyLinesOutsideTheTent() throws Exception {
        java.nio.file.Path models =
                java.nio.file.Paths.get("").toAbsolutePath().resolve("src/main/resources/assets/mc3ver/models/block");
        for (String side : new String[] {"front_left", "corner_left", "front_right", "corner_right"}) {
            com.google.gson.JsonObject json = com.google.gson.JsonParser.parseString(
                            java.nio.file.Files.readString(models.resolve("tent_canvas_wall_" + side + ".json")))
                    .getAsJsonObject();
            boolean left = side.endsWith("left");
            boolean stakeOutside = false;
            boolean hasRope = false;
            for (com.google.gson.JsonElement el : json.getAsJsonArray("elements")) {
                com.google.gson.JsonObject e = el.getAsJsonObject();
                String text = e.toString();
                double fromX = e.getAsJsonArray("from").get(0).getAsDouble();
                double toX = e.getAsJsonArray("to").get(0).getAsDouble();
                if (text.contains("#stake")) {
                    stakeOutside = left ? toX <= 0 : fromX >= 16;
                    assertEquals(
                            0.0,
                            e.getAsJsonArray("from").get(1).getAsDouble(),
                            1e-9,
                            "Hering steckt im Boden (" + side + ")");
                }
                if (text.contains("#rope")) {
                    hasRope = true;
                    assertTrue(e.has("rotation"), "Abspannleine verläuft schräg (" + side + ")");
                }
                if (text.contains("#stake") || text.contains("#rope")) {
                    assertFalse(text.contains("tintindex"), "Hering und Leine bleiben ungefärbt (" + side + ")");
                }
            }
            assertTrue(stakeOutside, "Hering muss außerhalb der Zeltseite stecken (" + side + ")");
            assertTrue(hasRope, "Abspannleine fehlt (" + side + ")");
        }
    }

    @Test
    void testPennantOnFrontGable() throws Exception {
        java.nio.file.Path assets =
                java.nio.file.Paths.get("").toAbsolutePath().resolve("src/main/resources/assets/mc3ver");
        String roofFront = java.nio.file.Files.readString(assets.resolve("models/block/tent_canvas_roof_front.json"));
        assertTrue(roofFront.contains("\"mc3ver:block/tent_pennant\""), "Giebel muss den Wimpel tragen");
        assertTrue(roofFront.contains("#pole"), "Wimpel braucht eine Stange");

        java.awt.image.BufferedImage pennant = javax.imageio.ImageIO.read(
                assets.resolve("textures/block/tent_pennant.png").toFile());
        assertEquals(16, pennant.getWidth());
        assertEquals(0, pennant.getHeight() % 16, "Animierte Textur besteht aus 16x16-Frames");
        assertTrue(pennant.getHeight() / 16 >= 2, "Wimpel flattert: mindestens 2 Frames");
        assertTrue(java.nio.file.Files.readString(assets.resolve("textures/block/tent_pennant.png.mcmeta"))
                .contains("\"animation\""));
    }

    private static boolean itemComponentsBound = false;

    // Bindet die Standard-Komponenten der Items, damit Tests ItemStacks bauen können
    private static synchronized void bootstrapWithItemComponents() {
        net.minecraft.SharedConstants.tryDetectVersion();
        net.minecraft.server.Bootstrap.bootStrap();
        if (!itemComponentsBound) {
            net.minecraft.core.HolderLookup.Provider lookup =
                    net.minecraft.data.registries.VanillaRegistries.createWorldLookup();
            net.minecraft.core.registries.BuiltInRegistries.DATA_COMPONENT_INITIALIZERS
                    .build(lookup)
                    .forEach(net.minecraft.core.component.DataComponentInitializers.PendingComponents::apply);
            itemComponentsBound = true;
        }
    }

    @Test
    void testChestLidOpenRule() {
        assertFalse(TentChestBlockEntity.isLidOpen(0));
        assertTrue(TentChestBlockEntity.isLidOpen(1));
        assertTrue(TentChestBlockEntity.isLidOpen(3));
    }

    @Test
    void testChestOpenVariantsAndLidModel() throws Exception {
        net.minecraft.SharedConstants.tryDetectVersion();
        net.minecraft.server.Bootstrap.bootStrap();
        java.nio.file.Path assets =
                java.nio.file.Paths.get("").toAbsolutePath().resolve("src/main/resources/assets/mc3ver");
        com.google.gson.JsonObject variants = com.google.gson.JsonParser.parseString(
                        java.nio.file.Files.readString(assets.resolve("blockstates/tent_chest.json")))
                .getAsJsonObject()
                .getAsJsonObject("variants");
        for (String facing : new String[] {"north", "east", "south", "west"}) {
            assertEquals(
                    "mc3ver:block/tent_chest_right",
                    variants.getAsJsonObject("facing=" + facing + ",open=false")
                            .get("model")
                            .getAsString());
            assertEquals(
                    "mc3ver:block/tent_chest_right_open",
                    variants.getAsJsonObject("facing=" + facing + ",open=true")
                            .get("model")
                            .getAsString());
        }
        com.google.gson.JsonObject rot = lidElement(assets.resolve("models/block/tent_chest_right_open.json"))
                .getAsJsonObject("rotation");
        assertNotNull(rot, "Offener Deckel ist gedreht");
        assertEquals("z", rot.get("axis").getAsString());
        assertEquals(-45, rot.get("angle").getAsInt());
        assertEquals(14, rot.getAsJsonArray("origin").get(0).getAsDouble());
        assertNull(
                lidElement(assets.resolve("models/block/tent_chest_right.json")).get("rotation"),
                "Geschlossener Deckel ist nicht gedreht");
        assertEquals("open", TentChestBlock.OPEN.getName());
    }

    private static com.google.gson.JsonObject lidElement(java.nio.file.Path model) throws Exception {
        for (com.google.gson.JsonElement e : com.google.gson.JsonParser.parseString(
                        java.nio.file.Files.readString(model))
                .getAsJsonObject()
                .getAsJsonArray("elements")) {
            com.google.gson.JsonObject o = e.getAsJsonObject();
            if (o.getAsJsonArray("from").get(1).getAsDouble() == 9
                    && o.getAsJsonArray("to").get(1).getAsDouble() == 11) {
                return o;
            }
        }
        throw new AssertionError("Kein Deckel-Element");
    }
}
