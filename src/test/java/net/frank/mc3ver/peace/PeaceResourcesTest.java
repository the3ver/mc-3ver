package net.frank.mc3ver.peace;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class PeaceResourcesTest {

    private static final java.nio.file.Path RESOURCES =
        java.nio.file.Paths.get("").toAbsolutePath().resolve("src/main/resources");

    private static java.awt.image.BufferedImage image(String path) throws Exception {
        java.nio.file.Path file = RESOURCES.resolve("assets/mc3ver/textures/" + path);
        assertTrue(java.nio.file.Files.exists(file), path + " muss existieren");
        return javax.imageio.ImageIO.read(file.toFile());
    }

    private static void assertCleanAlpha(java.awt.image.BufferedImage img, String name) {
        int opaque = 0;
        for (int y = 0; y < img.getHeight(); y++) {
            for (int x = 0; x < img.getWidth(); x++) {
                int alpha = (img.getRGB(x, y) >>> 24) & 0xFF;
                assertTrue(alpha == 0 || alpha == 255, name + ": nur ganz transparent oder deckend, Pixel " + x + "," + y);
                if (alpha == 255) opaque++;
            }
        }
        assertTrue(opaque >= 20, name + " muss eine erkennbare Form haben");
    }

    @Test
    void flagAndEffectTexturesHaveTheRightSize() throws Exception {
        java.awt.image.BufferedImage flag = image("item/white_flag.png");
        assertEquals(16, flag.getWidth());
        assertEquals(16, flag.getHeight());
        assertCleanAlpha(flag, "white_flag.png");

        java.awt.image.BufferedImage icon = image("mob_effect/pacifist.png");
        assertEquals(18, icon.getWidth());
        assertEquals(18, icon.getHeight());
        assertCleanAlpha(icon, "pacifist.png");
    }

    @Test
    void flagRecipeIsSticksBesideWool() throws Exception {
        com.google.gson.JsonObject recipe = com.google.gson.JsonParser.parseString(java.nio.file.Files.readString(
            RESOURCES.resolve("data/mc3ver/recipe/white_flag.json"))).getAsJsonObject();
        // 3 Stöcke links als Spalte, 4 weiße Wolle rechts daneben als 2x2
        com.google.gson.JsonArray pattern = recipe.getAsJsonArray("pattern");
        assertEquals("SWW", pattern.get(0).getAsString());
        assertEquals("SWW", pattern.get(1).getAsString());
        assertEquals("S  ", pattern.get(2).getAsString());
        assertEquals("minecraft:stick", recipe.getAsJsonObject("key").get("S").getAsString());
        assertEquals("minecraft:white_wool", recipe.getAsJsonObject("key").get("W").getAsString());
        assertEquals("mc3ver:white_flag", recipe.getAsJsonObject("result").get("id").getAsString());
    }

    @Test
    void flagItemModelPointsAtItsTexture() throws Exception {
        String model = java.nio.file.Files.readString(RESOURCES.resolve("assets/mc3ver/models/item/white_flag.json"));
        assertTrue(model.contains("mc3ver:item/white_flag"));
        String definition = java.nio.file.Files.readString(RESOURCES.resolve("assets/mc3ver/items/white_flag.json"));
        assertTrue(definition.contains("mc3ver:item/white_flag"));
    }
}
