package net.frank.mc3ver.tree;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class PearTreeLogicTest {

    @Test
    void testPearFoodProperties() {
        assertEquals(4, PearTreeLogic.PEAR_NUTRITION, "Pear should restore 4 hunger points (2 bars)");
        assertEquals(0.3f, PearTreeLogic.PEAR_SATURATION_MODIFIER, 0.001f, "Pear should have 0.3 saturation modifier");
    }

    @Test
    void testCompostChances() {
        assertEquals(0.65f, PearTreeLogic.getCompostChance("pear"), 0.001f, "Pear should have 65% composting chance");
        assertEquals(0.30f, PearTreeLogic.getCompostChance("pear_leaves"), 0.001f, "Leaves should have 30% composting chance");
        assertEquals(0.30f, PearTreeLogic.getCompostChance("pear_sapling"), 0.001f, "Sapling should have 30% composting chance");
        assertEquals(0.0f, PearTreeLogic.getCompostChance("pear_log"), 0.001f, "Logs are not compostable");
    }

    @Test
    void testStrippedMappings() {
        assertEquals("stripped_pear_log", PearTreeLogic.getStrippedBlockId("pear_log"), "pear_log should strip to stripped_pear_log");
        assertEquals("stripped_pear_wood", PearTreeLogic.getStrippedBlockId("pear_wood"), "pear_wood should strip to stripped_pear_wood");
        org.junit.jupiter.api.Assertions.assertNull(PearTreeLogic.getStrippedBlockId("pear_planks"), "pear_planks cannot be stripped");
    }

    @Test
    void testLeafDropChances() {
        // Base chances without Fortune (fortune = 0)
        // Sapling base rate: 5% (0.05)
        org.junit.jupiter.api.Assertions.assertTrue(PearTreeLogic.shouldDropSapling(0.04, 0));
        org.junit.jupiter.api.Assertions.assertFalse(PearTreeLogic.shouldDropSapling(0.06, 0));

        // Pear base rate: 2.5% (0.025)
        org.junit.jupiter.api.Assertions.assertTrue(PearTreeLogic.shouldDropPear(0.02, 0));
        org.junit.jupiter.api.Assertions.assertFalse(PearTreeLogic.shouldDropPear(0.03, 0));

        // Fortune 3 increases sapling drop rate to 10% (0.10)
        org.junit.jupiter.api.Assertions.assertTrue(PearTreeLogic.shouldDropSapling(0.08, 3));
        org.junit.jupiter.api.Assertions.assertFalse(PearTreeLogic.shouldDropSapling(0.12, 3));

        // Fortune 3 increases pear drop rate to 6.25% (0.0625)
        org.junit.jupiter.api.Assertions.assertTrue(PearTreeLogic.shouldDropPear(0.05, 3));
        org.junit.jupiter.api.Assertions.assertFalse(PearTreeLogic.shouldDropPear(0.07, 3));
    }

    @Test
    void testFuelDuration() {
        assertEquals(300, PearTreeLogic.getFuelDurationTicks("pear_log"), "Logs should burn for 300 ticks (1.5 items)");
        assertEquals(300, PearTreeLogic.getFuelDurationTicks("stripped_pear_log"), "Stripped logs should burn for 300 ticks");
        assertEquals(300, PearTreeLogic.getFuelDurationTicks("pear_wood"), "Wood should burn for 300 ticks");
        assertEquals(300, PearTreeLogic.getFuelDurationTicks("stripped_pear_wood"), "Stripped wood should burn for 300 ticks");
        assertEquals(300, PearTreeLogic.getFuelDurationTicks("pear_planks"), "Planks should burn for 300 ticks");
        assertEquals(300, PearTreeLogic.getFuelDurationTicks("pear_stairs"), "Stairs should burn for 300 ticks");
        assertEquals(150, PearTreeLogic.getFuelDurationTicks("pear_slab"), "Slabs should burn for 150 ticks");
        assertEquals(300, PearTreeLogic.getFuelDurationTicks("pear_fence"), "Fences should burn for 300 ticks");
        assertEquals(300, PearTreeLogic.getFuelDurationTicks("pear_fence_gate"), "Fence gates should burn for 300 ticks");
        assertEquals(200, PearTreeLogic.getFuelDurationTicks("pear_door"), "Doors should burn for 200 ticks");
        assertEquals(300, PearTreeLogic.getFuelDurationTicks("pear_trapdoor"), "Trapdoors should burn for 300 ticks");
        assertEquals(300, PearTreeLogic.getFuelDurationTicks("pear_pressure_plate"), "Pressure plates should burn for 300 ticks");
        assertEquals(100, PearTreeLogic.getFuelDurationTicks("pear_button"), "Buttons should burn for 100 ticks");
        assertEquals(100, PearTreeLogic.getFuelDurationTicks("pear_sapling"), "Saplings should burn for 100 ticks");
        assertEquals(0, PearTreeLogic.getFuelDurationTicks("pear"), "Pears are not fuel");
        assertEquals(0, PearTreeLogic.getFuelDurationTicks("pear_leaves"), "Leaves are not fuel");
    }

    @Test
    void testTargetBiomes() {
        java.util.List<String> biomes = PearTreeLogic.getTargetBiomeIds();
        org.junit.jupiter.api.Assertions.assertTrue(biomes.contains("minecraft:plains"), "Should spawn in plains");
        org.junit.jupiter.api.Assertions.assertTrue(biomes.contains("minecraft:sunflower_plains"), "Should spawn in sunflower plains");
        org.junit.jupiter.api.Assertions.assertTrue(biomes.contains("minecraft:forest"), "Should spawn in forest");
        org.junit.jupiter.api.Assertions.assertTrue(biomes.contains("minecraft:flower_forest"), "Should spawn in flower forest");
        org.junit.jupiter.api.Assertions.assertTrue(biomes.contains("minecraft:meadow"), "Should spawn in meadow");
        org.junit.jupiter.api.Assertions.assertTrue(biomes.contains("minecraft:birch_forest"), "Should spawn in birch forest");
    }

    @Test
    void testPearItemModelUsesCustomTexture() throws Exception {
        java.io.InputStream stream = getClass().getClassLoader().getResourceAsStream("assets/mc3ver/models/item/pear.json");
        org.junit.jupiter.api.Assertions.assertNotNull(stream, "Pear item model must exist");
        String json = new String(stream.readAllBytes(), java.nio.charset.StandardCharsets.UTF_8);
        org.junit.jupiter.api.Assertions.assertTrue(json.contains("\"layer0\": \"mc3ver:item/pear\""), "Pear model must reference custom mc3ver:item/pear texture");
        org.junit.jupiter.api.Assertions.assertFalse(json.contains("minecraft:item/apple"), "Pear model must not reuse apple texture");

        java.io.InputStream textureStream = getClass().getClassLoader().getResourceAsStream("assets/mc3ver/textures/item/pear.png");
        org.junit.jupiter.api.Assertions.assertNotNull(textureStream, "Pear texture assets/mc3ver/textures/item/pear.png must exist");
        java.awt.image.BufferedImage img = javax.imageio.ImageIO.read(textureStream);
        org.junit.jupiter.api.Assertions.assertEquals(16, img.getWidth());
        org.junit.jupiter.api.Assertions.assertEquals(16, img.getHeight());
    }

    @Test
    void testPearSaplingUsesCustomTextureAndDistinctLeafColor() throws Exception {
        // 1. Block Model must use custom pear sapling texture
        java.io.InputStream blockModelStream = getClass().getClassLoader().getResourceAsStream("assets/mc3ver/models/block/pear_sapling.json");
        org.junit.jupiter.api.Assertions.assertNotNull(blockModelStream, "Pear sapling block model must exist");
        String blockModelJson = new String(blockModelStream.readAllBytes(), java.nio.charset.StandardCharsets.UTF_8);
        org.junit.jupiter.api.Assertions.assertTrue(blockModelJson.contains("\"cross\": \"mc3ver:block/pear_sapling\""),
                "Pear sapling block model must reference custom mc3ver:block/pear_sapling texture");
        org.junit.jupiter.api.Assertions.assertFalse(blockModelJson.contains("minecraft:block/oak_sapling"),
                "Pear sapling block model must not reuse oak_sapling texture");

        // 2. Item Model must use custom pear sapling texture
        java.io.InputStream itemModelStream = getClass().getClassLoader().getResourceAsStream("assets/mc3ver/models/item/pear_sapling.json");
        org.junit.jupiter.api.Assertions.assertNotNull(itemModelStream, "Pear sapling item model must exist");
        String itemModelJson = new String(itemModelStream.readAllBytes(), java.nio.charset.StandardCharsets.UTF_8);
        org.junit.jupiter.api.Assertions.assertTrue(itemModelJson.contains("\"layer0\": \"mc3ver:block/pear_sapling\""),
                "Pear sapling item model must reference custom mc3ver:block/pear_sapling texture");
        org.junit.jupiter.api.Assertions.assertFalse(itemModelJson.contains("minecraft:block/oak_sapling"),
                "Pear sapling item model must not reuse oak_sapling texture");

        // 3. Texture file must exist and have 16x16 dimensions
        java.io.InputStream textureStream = getClass().getClassLoader().getResourceAsStream("assets/mc3ver/textures/block/pear_sapling.png");
        org.junit.jupiter.api.Assertions.assertNotNull(textureStream, "Pear sapling texture assets/mc3ver/textures/block/pear_sapling.png must exist");
        java.awt.image.BufferedImage img = javax.imageio.ImageIO.read(textureStream);
        org.junit.jupiter.api.Assertions.assertEquals(16, img.getWidth());
        org.junit.jupiter.api.Assertions.assertEquals(16, img.getHeight());

        // 4. Must not have oak sapling bright green leaves, but silvery/pear leaf tone
        int silveryLeafPixelCount = 0;
        int oakGreenPixelCount = 0;
        for (int y = 0; y < 16; y++) {
            for (int x = 0; x < 16; x++) {
                int argb = img.getRGB(x, y);
                int a = (argb >> 24) & 0xFF;
                if (a == 0) continue;
                int r = (argb >> 16) & 0xFF;
                int g = (argb >> 8) & 0xFF;
                int b = argb & 0xFF;

                // Oak sapling leaves are vibrant green (e.g. 87, 173, 63 or 64, 143, 47)
                if (g > 120 && g > r + 35 && g > b + 35) {
                    oakGreenPixelCount++;
                }

                // Silvery / pear tree leaf color (grayscale/balanced RGB matching pear leaves)
                if (Math.abs(r - g) <= 20 && Math.abs(g - b) <= 20 && r > 70 && r < 180) {
                    silveryLeafPixelCount++;
                }
            }
        }
        org.junit.jupiter.api.Assertions.assertEquals(0, oakGreenPixelCount, "Pear sapling must not contain vibrant oak green leaves");
        org.junit.jupiter.api.Assertions.assertTrue(silveryLeafPixelCount >= 10, "Pear sapling must contain silvery/pear leaves matching the pear tree leaf color");
    }

    @Test
    void testPearWoodCustomTexturesAndModels() throws Exception {
        // 1. Textures must exist and have 16x16 dimensions
        String[] texturePaths = {
            "assets/mc3ver/textures/block/pear_planks.png",
            "assets/mc3ver/textures/block/pear_log.png",
            "assets/mc3ver/textures/block/pear_log_top.png",
            "assets/mc3ver/textures/block/stripped_pear_log.png",
            "assets/mc3ver/textures/block/stripped_pear_log_top.png",
            "assets/mc3ver/textures/block/pear_door_top.png",
            "assets/mc3ver/textures/block/pear_door_bottom.png",
            "assets/mc3ver/textures/block/pear_trapdoor.png",
            "assets/mc3ver/textures/item/pear_door.png"
        };

        for (String path : texturePaths) {
            java.io.InputStream stream = getClass().getClassLoader().getResourceAsStream(path);
            org.junit.jupiter.api.Assertions.assertNotNull(stream, "Texture " + path + " must exist");
            java.awt.image.BufferedImage img = javax.imageio.ImageIO.read(stream);
            org.junit.jupiter.api.Assertions.assertEquals(16, img.getWidth(), path + " width must be 16");
            org.junit.jupiter.api.Assertions.assertEquals(16, img.getHeight(), path + " height must be 16");
        }

        // 2. Planks model must reference mc3ver:block/pear_planks
        java.io.InputStream planksStream = getClass().getClassLoader().getResourceAsStream("assets/mc3ver/models/block/pear_planks.json");
        org.junit.jupiter.api.Assertions.assertNotNull(planksStream, "pear_planks.json model must exist");
        String planksJson = new String(planksStream.readAllBytes(), java.nio.charset.StandardCharsets.UTF_8);
        org.junit.jupiter.api.Assertions.assertTrue(planksJson.contains("\"all\": \"mc3ver:block/pear_planks\""),
                "pear_planks model must reference custom mc3ver:block/pear_planks");
        org.junit.jupiter.api.Assertions.assertFalse(planksJson.contains("minecraft:block/oak_planks"),
                "pear_planks model must not reuse oak_planks");

        // 3. Log model must reference mc3ver:block/pear_log and mc3ver:block/pear_log_top
        java.io.InputStream logStream = getClass().getClassLoader().getResourceAsStream("assets/mc3ver/models/block/pear_log.json");
        org.junit.jupiter.api.Assertions.assertNotNull(logStream, "pear_log.json model must exist");
        String logJson = new String(logStream.readAllBytes(), java.nio.charset.StandardCharsets.UTF_8);
        org.junit.jupiter.api.Assertions.assertTrue(logJson.contains("\"side\": \"mc3ver:block/pear_log\""),
                "pear_log model must reference custom mc3ver:block/pear_log");
        org.junit.jupiter.api.Assertions.assertTrue(logJson.contains("\"end\": \"mc3ver:block/pear_log_top\""),
                "pear_log model must reference custom mc3ver:block/pear_log_top");
        org.junit.jupiter.api.Assertions.assertFalse(logJson.contains("minecraft:block/oak_log"),
                "pear_log model must not reuse oak_log");

        // 4. Stripped Log model must reference mc3ver:block/stripped_pear_log and top
        java.io.InputStream strippedLogStream = getClass().getClassLoader().getResourceAsStream("assets/mc3ver/models/block/stripped_pear_log.json");
        org.junit.jupiter.api.Assertions.assertNotNull(strippedLogStream, "stripped_pear_log.json model must exist");
        String strippedLogJson = new String(strippedLogStream.readAllBytes(), java.nio.charset.StandardCharsets.UTF_8);
        org.junit.jupiter.api.Assertions.assertTrue(strippedLogJson.contains("\"side\": \"mc3ver:block/stripped_pear_log\""),
                "stripped_pear_log model must reference custom mc3ver:block/stripped_pear_log");
        org.junit.jupiter.api.Assertions.assertTrue(strippedLogJson.contains("\"end\": \"mc3ver:block/stripped_pear_log_top\""),
                "stripped_pear_log model must reference custom mc3ver:block/stripped_pear_log_top");
    }

    @Test
    void testPearWoodSetModelsRecipesAndLootTables() throws Exception {
        String[] woodBlocks = {
            "pear_stairs", "pear_slab", "pear_fence", "pear_fence_gate",
            "pear_door", "pear_trapdoor", "pear_pressure_plate", "pear_button"
        };

        for (String name : woodBlocks) {
            // Blockstate
            java.io.InputStream bsStream = getClass().getClassLoader().getResourceAsStream("assets/mc3ver/blockstates/" + name + ".json");
            org.junit.jupiter.api.Assertions.assertNotNull(bsStream, "Blockstate assets/mc3ver/blockstates/" + name + ".json must exist");

            // Item definition
            java.io.InputStream itemDefStream = getClass().getClassLoader().getResourceAsStream("assets/mc3ver/items/" + name + ".json");
            org.junit.jupiter.api.Assertions.assertNotNull(itemDefStream, "Item def assets/mc3ver/items/" + name + ".json must exist");

            // Item model
            java.io.InputStream itemModelStream = getClass().getClassLoader().getResourceAsStream("assets/mc3ver/models/item/" + name + ".json");
            org.junit.jupiter.api.Assertions.assertNotNull(itemModelStream, "Item model assets/mc3ver/models/item/" + name + ".json must exist");

            // Recipe
            java.io.InputStream recipeStream = getClass().getClassLoader().getResourceAsStream("data/mc3ver/recipe/" + name + ".json");
            org.junit.jupiter.api.Assertions.assertNotNull(recipeStream, "Recipe data/mc3ver/recipe/" + name + ".json must exist");

            // Loot Table
            java.io.InputStream lootStream = getClass().getClassLoader().getResourceAsStream("data/mc3ver/loot_table/blocks/" + name + ".json");
            org.junit.jupiter.api.Assertions.assertNotNull(lootStream, "Loot table data/mc3ver/loot_table/blocks/" + name + ".json must exist");
        }

        // Tags
        String[] requiredBlockTags = {
            "wooden_stairs", "wooden_slabs", "wooden_fences", "fence_gates",
            "wooden_doors", "wooden_trapdoors", "wooden_pressure_plates", "wooden_buttons"
        };
        for (String tag : requiredBlockTags) {
            java.io.InputStream tagStream = getClass().getClassLoader().getResourceAsStream("data/minecraft/tags/block/" + tag + ".json");
            org.junit.jupiter.api.Assertions.assertNotNull(tagStream, "Block tag data/minecraft/tags/block/" + tag + ".json must exist");
        }
    }
}
