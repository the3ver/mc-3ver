package net.frank.mc3ver.friend;

import static org.junit.jupiter.api.Assertions.*;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import org.junit.jupiter.api.Test;

public class FriendStoneResourcesTest {

    private static final Path RESOURCES = Paths.get("").toAbsolutePath().resolve("src/main/resources");

    private static JsonObject json(String path) throws Exception {
        return JsonParser.parseString(Files.readString(RESOURCES.resolve(path))).getAsJsonObject();
    }

    @Test
    void recipeMakesTwoStonesFromAmethystAndAnEnderPearl() throws Exception {
        JsonObject recipe = json("data/mc3ver/recipe/friendship_stone.json");
        assertEquals(" A ", recipe.getAsJsonArray("pattern").get(0).getAsString());
        assertEquals("AEA", recipe.getAsJsonArray("pattern").get(1).getAsString());
        assertEquals(" A ", recipe.getAsJsonArray("pattern").get(2).getAsString());
        assertEquals(
                "minecraft:amethyst_shard",
                recipe.getAsJsonObject("key").get("A").getAsString());
        assertEquals(
                "minecraft:ender_pearl", recipe.getAsJsonObject("key").get("E").getAsString());
        assertEquals(
                "mc3ver:friendship_stone",
                recipe.getAsJsonObject("result").get("id").getAsString());
        // Zwei Steine, damit man einen prägen und tauschen kann
        assertEquals(2, recipe.getAsJsonObject("result").get("count").getAsInt());
    }

    @Test
    void boundStonesShowTheHeartModel() throws Exception {
        JsonObject model = json("assets/mc3ver/items/friendship_stone.json").getAsJsonObject("model");
        assertEquals("minecraft:has_component", model.get("property").getAsString());
        assertEquals("mc3ver:friend_link", model.get("component").getAsString());
        assertEquals(
                "mc3ver:item/friendship_stone_bound",
                model.getAsJsonObject("on_true").get("model").getAsString());
        assertEquals(
                "mc3ver:item/friendship_stone",
                model.getAsJsonObject("on_false").get("model").getAsString());
    }

    @Test
    void bothTexturesAre16x16WithHardAlpha() throws Exception {
        for (String name : new String[] {"friendship_stone", "friendship_stone_bound"}) {
            java.awt.image.BufferedImage img = javax.imageio.ImageIO.read(RESOURCES
                    .resolve("assets/mc3ver/textures/item/" + name + ".png")
                    .toFile());
            assertEquals(16, img.getWidth(), name);
            assertEquals(16, img.getHeight(), name);
            for (int y = 0; y < 16; y++) {
                for (int x = 0; x < 16; x++) {
                    int alpha = img.getRGB(x, y) >>> 24;
                    assertTrue(alpha == 0 || alpha == 255, name + ": Pixel " + x + "," + y);
                }
            }
        }
    }

    @Test
    void everyCompassDirectionIsTranslated() throws Exception {
        for (String lang : new String[] {"de_de", "en_us"}) {
            JsonObject texts = json("assets/mc3ver/lang/" + lang + ".json");
            for (FriendStoneLogic.Compass c : FriendStoneLogic.Compass.values()) {
                assertTrue(texts.has(c.translationKey()), lang + ": " + c.translationKey());
            }
            for (String dim : new String[] {"overworld", "the_nether", "the_end"}) {
                assertTrue(texts.has("dimension.mc3ver." + dim), lang + ": " + dim);
            }
        }
    }
}
