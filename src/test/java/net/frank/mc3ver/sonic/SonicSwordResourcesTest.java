package net.frank.mc3ver.sonic;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import java.awt.image.BufferedImage;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Map;
import javax.imageio.ImageIO;
import org.junit.jupiter.api.Test;

class SonicSwordResourcesTest {

    private static final Path RESOURCES = Paths.get("").toAbsolutePath().resolve("src/main/resources");

    private static JsonObject json(String path) throws Exception {
        return JsonParser.parseString(Files.readString(RESOURCES.resolve(path))).getAsJsonObject();
    }

    @Test
    void recipeUsesADiamondSwordTwoAmethystShardsAndSixObsidian() throws Exception {
        JsonObject recipe = json("data/mc3ver/recipe/sonic_sword.json");
        JsonObject key = recipe.getAsJsonObject("key");
        Map<String, Integer> counts = new HashMap<>();
        JsonArray pattern = recipe.getAsJsonArray("pattern");
        for (int row = 0; row < pattern.size(); row++) {
            for (char c : pattern.get(row).getAsString().toCharArray()) {
                if (c == ' ') continue;
                counts.merge(key.get(String.valueOf(c)).getAsString(), 1, Integer::sum);
            }
        }
        assertEquals(
                Map.of("minecraft:diamond_sword", 1, "minecraft:amethyst_shard", 2, "minecraft:obsidian", 6), counts);
        assertEquals(
                "mc3ver:sonic_sword", recipe.getAsJsonObject("result").get("id").getAsString());
    }

    @Test
    void itemPointsAtAHandheldModelWithItsOwnTexture() throws Exception {
        assertEquals(
                "mc3ver:item/sonic_sword",
                json("assets/mc3ver/items/sonic_sword.json")
                        .getAsJsonObject("model")
                        .get("model")
                        .getAsString());
        JsonObject model = json("assets/mc3ver/models/item/sonic_sword.json");
        assertEquals("minecraft:item/handheld", model.get("parent").getAsString());
        assertEquals(
                "mc3ver:item/sonic_sword",
                model.getAsJsonObject("textures").get("layer0").getAsString());
    }

    @Test
    void textureIs16x16WithHardAlpha() throws Exception {
        BufferedImage img = ImageIO.read(
                RESOURCES.resolve("assets/mc3ver/textures/item/sonic_sword.png").toFile());
        assertEquals(16, img.getWidth());
        assertEquals(16, img.getHeight());
        for (int y = 0; y < 16; y++) {
            for (int x = 0; x < 16; x++) {
                int alpha = img.getRGB(x, y) >>> 24;
                assertTrue(alpha == 0 || alpha == 255, "Pixel " + x + "," + y);
            }
        }
    }
}
