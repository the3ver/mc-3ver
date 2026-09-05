package net.frank.mc3ver.tree;

import org.junit.jupiter.api.Test;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;

import static org.junit.jupiter.api.Assertions.*;

public class GoldenPearLogicTest {

    @Test
    void testGoldenPearConstants() {
        assertEquals(4, PearTreeLogic.GOLDEN_PEAR_NUTRITION, "Golden pear should restore 4 hunger points (2 bars)");
        assertEquals(1.2f, PearTreeLogic.GOLDEN_PEAR_SATURATION_MODIFIER, 0.001f, "Golden pear should have 1.2 saturation modifier");
        assertEquals(1200, PearTreeLogic.JUMP_BOOST_DURATION_TICKS, "Jump boost duration should be 60 seconds (1200 ticks)");
        assertEquals(1200, PearTreeLogic.SPEED_DURATION_TICKS, "Speed duration should be 60 seconds (1200 ticks)");
        assertEquals(900, PearTreeLogic.SLOW_FALLING_DURATION_TICKS, "Slow falling duration should be 45 seconds (900 ticks)");
    }

    @Test
    void testGoldenPearItemModelAndTexture() throws Exception {
        // Model
        InputStream modelStream = getClass().getClassLoader().getResourceAsStream("assets/mc3ver/models/item/golden_pear.json");
        assertNotNull(modelStream, "assets/mc3ver/models/item/golden_pear.json must exist");
        String modelJson = new String(modelStream.readAllBytes(), StandardCharsets.UTF_8);
        assertTrue(modelJson.contains("\"layer0\": \"mc3ver:item/golden_pear\""), "Model must reference mc3ver:item/golden_pear");

        // Modern Item definition
        InputStream itemStream = getClass().getClassLoader().getResourceAsStream("assets/mc3ver/items/golden_pear.json");
        assertNotNull(itemStream, "assets/mc3ver/items/golden_pear.json must exist");
        String itemJson = new String(itemStream.readAllBytes(), StandardCharsets.UTF_8);
        assertTrue(itemJson.contains("\"model\":"), "Item definition must reference model");

        // Texture
        InputStream textureStream = getClass().getClassLoader().getResourceAsStream("assets/mc3ver/textures/item/golden_pear.png");
        assertNotNull(textureStream, "assets/mc3ver/textures/item/golden_pear.png must exist");
        BufferedImage img = ImageIO.read(textureStream);
        assertNotNull(img, "Texture must be valid readable image");
        assertEquals(16, img.getWidth(), "Texture width must be 16");
        assertEquals(16, img.getHeight(), "Texture height must be 16");
    }

    @Test
    void testGoldenPearCraftingRecipes() throws Exception {
        // Shaped Recipe
        InputStream shapedStream = getClass().getClassLoader().getResourceAsStream("data/mc3ver/recipe/golden_pear.json");
        assertNotNull(shapedStream, "data/mc3ver/recipe/golden_pear.json must exist");
        String shapedJson = new String(shapedStream.readAllBytes(), StandardCharsets.UTF_8);
        assertTrue(shapedJson.contains("minecraft:crafting_shaped"), "Must be shaped crafting");
        assertTrue(shapedJson.contains("mc3ver:pear"), "Key must require mc3ver:pear");
        assertTrue(shapedJson.contains("minecraft:gold_ingot"), "Key must require minecraft:gold_ingot");
        assertTrue(shapedJson.contains("mc3ver:golden_pear"), "Result must be mc3ver:golden_pear");

        // Shapeless Recipe
        InputStream shapelessStream = getClass().getClassLoader().getResourceAsStream("data/mc3ver/recipe/golden_pear_shapeless.json");
        assertNotNull(shapelessStream, "data/mc3ver/recipe/golden_pear_shapeless.json must exist");
        String shapelessJson = new String(shapelessStream.readAllBytes(), StandardCharsets.UTF_8);
        assertTrue(shapelessJson.contains("minecraft:crafting_shapeless"), "Must be shapeless crafting");
        assertTrue(shapelessJson.contains("mc3ver:pear"), "Ingredients must contain mc3ver:pear");
        assertTrue(shapelessJson.contains("minecraft:gold_ingot"), "Ingredients must contain minecraft:gold_ingot");
        assertTrue(shapelessJson.contains("mc3ver:golden_pear"), "Result must be mc3ver:golden_pear");
    }
}
