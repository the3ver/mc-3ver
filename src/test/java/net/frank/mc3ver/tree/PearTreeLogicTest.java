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
}
