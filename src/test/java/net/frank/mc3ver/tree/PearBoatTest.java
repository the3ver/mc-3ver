package net.frank.mc3ver.tree;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class PearBoatTest {

    @Test
    void testPearBoatLogicConstants() {
        assertEquals("pear_boat", PearBoatLogic.BOAT_ID);
        assertEquals("pear_chest_boat", PearBoatLogic.CHEST_BOAT_ID);
        assertEquals(1, PearBoatLogic.BOAT_MAX_STACK_SIZE);
        assertEquals(1200, PearBoatLogic.BOAT_FUEL_DURATION_TICKS);
        assertEquals("boat/pear", PearBoatLogic.BOAT_MODEL_PATH);
        assertEquals("chest_boat/pear", PearBoatLogic.CHEST_BOAT_MODEL_PATH);
        assertEquals("textures/entity/boat/pear.png", PearBoatLogic.BOAT_TEXTURE_PATH);
        assertEquals("textures/entity/chest_boat/pear.png", PearBoatLogic.CHEST_BOAT_TEXTURE_PATH);
        assertEquals(1200, PearTreeLogic.getFuelDurationTicks("pear_boat"));
        assertEquals(1200, PearTreeLogic.getFuelDurationTicks("pear_chest_boat"));
    }

    @Test
    void testBoatItemDefinitionsAndModels() throws Exception {
        // Pear Boat Item Definition
        java.io.InputStream boatItemStream = getClass().getClassLoader().getResourceAsStream("assets/mc3ver/items/pear_boat.json");
        assertNotNull(boatItemStream, "assets/mc3ver/items/pear_boat.json must exist");
        String boatItemJson = new String(boatItemStream.readAllBytes(), java.nio.charset.StandardCharsets.UTF_8);
        assertTrue(boatItemJson.contains("mc3ver:item/pear_boat"), "Item definition must reference mc3ver:item/pear_boat");

        // Pear Chest Boat Item Definition
        java.io.InputStream chestBoatItemStream = getClass().getClassLoader().getResourceAsStream("assets/mc3ver/items/pear_chest_boat.json");
        assertNotNull(chestBoatItemStream, "assets/mc3ver/items/pear_chest_boat.json must exist");
        String chestBoatItemJson = new String(chestBoatItemStream.readAllBytes(), java.nio.charset.StandardCharsets.UTF_8);
        assertTrue(chestBoatItemJson.contains("mc3ver:item/pear_chest_boat"), "Item definition must reference mc3ver:item/pear_chest_boat");

        // Pear Boat Model
        java.io.InputStream boatModelStream = getClass().getClassLoader().getResourceAsStream("assets/mc3ver/models/item/pear_boat.json");
        assertNotNull(boatModelStream, "assets/mc3ver/models/item/pear_boat.json must exist");
        String boatModelJson = new String(boatModelStream.readAllBytes(), java.nio.charset.StandardCharsets.UTF_8);
        assertTrue(boatModelJson.contains("\"layer0\": \"mc3ver:item/pear_boat\""), "Model must reference layer0 mc3ver:item/pear_boat");

        // Pear Chest Boat Model
        java.io.InputStream chestBoatModelStream = getClass().getClassLoader().getResourceAsStream("assets/mc3ver/models/item/pear_chest_boat.json");
        assertNotNull(chestBoatModelStream, "assets/mc3ver/models/item/pear_chest_boat.json must exist");
        String chestBoatModelJson = new String(chestBoatModelStream.readAllBytes(), java.nio.charset.StandardCharsets.UTF_8);
        assertTrue(chestBoatModelJson.contains("\"layer0\": \"mc3ver:item/pear_chest_boat\""), "Model must reference layer0 mc3ver:item/pear_chest_boat");
    }

    @Test
    void testBoatCraftingRecipes() throws Exception {
        // Shaped Pear Boat Recipe
        java.io.InputStream boatRecipeStream = getClass().getClassLoader().getResourceAsStream("data/mc3ver/recipe/pear_boat.json");
        assertNotNull(boatRecipeStream, "data/mc3ver/recipe/pear_boat.json must exist");
        String boatRecipeJson = new String(boatRecipeStream.readAllBytes(), java.nio.charset.StandardCharsets.UTF_8);
        assertTrue(boatRecipeJson.contains("minecraft:crafting_shaped"), "Must be shaped crafting");
        assertTrue(boatRecipeJson.contains("\"#\": \"mc3ver:pear_planks\"") || boatRecipeJson.contains("\"#\": \"mc3ver:pear_planks\"".replace(" ", "")), "Key must require mc3ver:pear_planks");
        assertTrue(boatRecipeJson.contains("mc3ver:pear_boat"), "Result must be mc3ver:pear_boat");

        // Shapeless Pear Chest Boat Recipe
        java.io.InputStream chestBoatRecipeStream = getClass().getClassLoader().getResourceAsStream("data/mc3ver/recipe/pear_chest_boat.json");
        assertNotNull(chestBoatRecipeStream, "data/mc3ver/recipe/pear_chest_boat.json must exist");
        String chestBoatRecipeJson = new String(chestBoatRecipeStream.readAllBytes(), java.nio.charset.StandardCharsets.UTF_8);
        assertTrue(chestBoatRecipeJson.contains("minecraft:crafting_shapeless"), "Must be shapeless crafting");
        assertTrue(chestBoatRecipeJson.contains("minecraft:chest"), "Ingredients must require minecraft:chest");
        assertTrue(chestBoatRecipeJson.contains("mc3ver:pear_boat"), "Ingredients must require mc3ver:pear_boat");
        assertTrue(chestBoatRecipeJson.contains("mc3ver:pear_chest_boat"), "Result must be mc3ver:pear_chest_boat");
    }

    @Test
    void testBoatTagsIncludePearBoats() throws Exception {
        // Item Tag: boats
        java.io.InputStream boatsTagStream = getClass().getClassLoader().getResourceAsStream("data/minecraft/tags/item/boats.json");
        assertNotNull(boatsTagStream, "data/minecraft/tags/item/boats.json must exist");
        String boatsTagJson = new String(boatsTagStream.readAllBytes(), java.nio.charset.StandardCharsets.UTF_8);
        assertTrue(boatsTagJson.contains("mc3ver:pear_boat"), "Item tag boats must contain mc3ver:pear_boat");

        // Item Tag: chest_boats
        java.io.InputStream chestBoatsTagStream = getClass().getClassLoader().getResourceAsStream("data/minecraft/tags/item/chest_boats.json");
        assertNotNull(chestBoatsTagStream, "data/minecraft/tags/item/chest_boats.json must exist");
        String chestBoatsTagJson = new String(chestBoatsTagStream.readAllBytes(), java.nio.charset.StandardCharsets.UTF_8);
        assertTrue(chestBoatsTagJson.contains("mc3ver:pear_chest_boat"), "Item tag chest_boats must contain mc3ver:pear_chest_boat");

        // Entity Type Tag: boat
        java.io.InputStream boatEntityTagStream = getClass().getClassLoader().getResourceAsStream("data/minecraft/tags/entity_type/boat.json");
        assertNotNull(boatEntityTagStream, "data/minecraft/tags/entity_type/boat.json must exist");
        String boatEntityTagJson = new String(boatEntityTagStream.readAllBytes(), java.nio.charset.StandardCharsets.UTF_8);
        assertTrue(boatEntityTagJson.contains("mc3ver:pear_boat"), "Entity tag boat must contain mc3ver:pear_boat");
    }

    @Test
    void testBoatTexturesExistAndHaveValidDimensions() throws Exception {
        // Item Texture: pear_boat.png
        java.io.InputStream boatItemTextureStream = getClass().getClassLoader().getResourceAsStream("assets/mc3ver/textures/item/pear_boat.png");
        assertNotNull(boatItemTextureStream, "assets/mc3ver/textures/item/pear_boat.png must exist");
        java.awt.image.BufferedImage boatItemImg = javax.imageio.ImageIO.read(boatItemTextureStream);
        assertNotNull(boatItemImg, "pear_boat.png must be valid image");
        assertEquals(16, boatItemImg.getWidth(), "pear_boat item texture width must be 16");
        assertEquals(16, boatItemImg.getHeight(), "pear_boat item texture height must be 16");

        // Item Texture: pear_chest_boat.png
        java.io.InputStream chestBoatItemTextureStream = getClass().getClassLoader().getResourceAsStream("assets/mc3ver/textures/item/pear_chest_boat.png");
        assertNotNull(chestBoatItemTextureStream, "assets/mc3ver/textures/item/pear_chest_boat.png must exist");
        java.awt.image.BufferedImage chestBoatItemImg = javax.imageio.ImageIO.read(chestBoatItemTextureStream);
        assertNotNull(chestBoatItemImg, "pear_chest_boat.png must be valid image");
        assertEquals(16, chestBoatItemImg.getWidth(), "pear_chest_boat item texture width must be 16");
        assertEquals(16, chestBoatItemImg.getHeight(), "pear_chest_boat item texture height must be 16");

        // Entity Texture: boat/pear.png
        java.io.InputStream boatEntityTextureStream = getClass().getClassLoader().getResourceAsStream("assets/mc3ver/textures/entity/boat/pear.png");
        assertNotNull(boatEntityTextureStream, "assets/mc3ver/textures/entity/boat/pear.png must exist");
        java.awt.image.BufferedImage boatEntityImg = javax.imageio.ImageIO.read(boatEntityTextureStream);
        assertNotNull(boatEntityImg, "boat/pear.png must be valid image");
        assertEquals(128, boatEntityImg.getWidth(), "boat/pear.png entity texture width must be 128");
        assertEquals(64, boatEntityImg.getHeight(), "boat/pear.png entity texture height must be 64");

        // Entity Texture: chest_boat/pear.png
        java.io.InputStream chestBoatEntityTextureStream = getClass().getClassLoader().getResourceAsStream("assets/mc3ver/textures/entity/chest_boat/pear.png");
        assertNotNull(chestBoatEntityTextureStream, "assets/mc3ver/textures/entity/chest_boat/pear.png must exist");
        java.awt.image.BufferedImage chestBoatEntityImg = javax.imageio.ImageIO.read(chestBoatEntityTextureStream);
        assertNotNull(chestBoatEntityImg, "chest_boat/pear.png must be valid image");
        assertEquals(128, chestBoatEntityImg.getWidth(), "chest_boat/pear.png entity texture width must be 128");
        assertEquals(128, chestBoatEntityImg.getHeight(), "chest_boat/pear.png entity texture height must be 128");
    }

    @Test
    void testBoatTranslationsPresent() throws Exception {
        java.io.InputStream deStream = getClass().getClassLoader().getResourceAsStream("assets/mc3ver/lang/de_de.json");
        assertNotNull(deStream, "assets/mc3ver/lang/de_de.json must exist");
        String deJson = new String(deStream.readAllBytes(), java.nio.charset.StandardCharsets.UTF_8);
        assertTrue(deJson.contains("\"item.mc3ver.pear_boat\": \"Birnenholzboot\""), "DE translation for pear_boat must exist");
        assertTrue(deJson.contains("\"item.mc3ver.pear_chest_boat\": \"Birnenholztruhenboot\""), "DE translation for pear_chest_boat must exist");
        assertTrue(deJson.contains("\"entity.mc3ver.pear_boat\": \"Birnenholzboot\""), "DE translation for entity pear_boat must exist");
        assertTrue(deJson.contains("\"entity.mc3ver.pear_chest_boat\": \"Birnenholztruhenboot\""), "DE translation for entity pear_chest_boat must exist");

        java.io.InputStream enStream = getClass().getClassLoader().getResourceAsStream("assets/mc3ver/lang/en_us.json");
        assertNotNull(enStream, "assets/mc3ver/lang/en_us.json must exist");
        String enJson = new String(enStream.readAllBytes(), java.nio.charset.StandardCharsets.UTF_8);
        assertTrue(enJson.contains("\"item.mc3ver.pear_boat\": \"Pear Boat\""), "EN translation for pear_boat must exist");
        assertTrue(enJson.contains("\"item.mc3ver.pear_chest_boat\": \"Pear Boat with Chest\""), "EN translation for pear_chest_boat must exist");
        assertTrue(enJson.contains("\"entity.mc3ver.pear_boat\": \"Pear Boat\""), "EN translation for entity pear_boat must exist");
        assertTrue(enJson.contains("\"entity.mc3ver.pear_chest_boat\": \"Pear Boat with Chest\""), "EN translation for entity pear_chest_boat must exist");
    }
}
