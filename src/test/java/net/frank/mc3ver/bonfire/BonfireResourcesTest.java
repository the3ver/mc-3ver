package net.frank.mc3ver.bonfire;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import org.junit.jupiter.api.Test;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import static org.junit.jupiter.api.Assertions.*;

public class BonfireResourcesTest {

    private static final Path RESOURCES = Paths.get("").toAbsolutePath().resolve("src/main/resources");

    private static JsonObject json(String path) throws Exception {
        return JsonParser.parseString(Files.readString(RESOURCES.resolve(path))).getAsJsonObject();
    }

    @Test
    void blockstateCoversEveryPartLitAndUnlit() throws Exception {
        JsonObject variants = json("assets/mc3ver/blockstates/bonfire.json").getAsJsonObject("variants");
        for (BonfireLogic.Part part : BonfireLogic.Part.values()) {
            String name = part.getSerializedName();
            assertEquals("minecraft:block/campfire",
                variants.getAsJsonObject("lit=true,part=" + name).get("model").getAsString());
            assertEquals("minecraft:block/campfire_off",
                variants.getAsJsonObject("lit=false,part=" + name).get("model").getAsString());
        }
        assertEquals(BonfireLogic.Part.values().length * 2, variants.size());
    }

    @Test
    void onlyTheCenterDropsTheBonfire() throws Exception {
        String lootTable = Files.readString(RESOURCES.resolve("data/mc3ver/loot_table/blocks/bonfire.json"));
        assertTrue(lootTable.contains("\"part\": \"center\""));
        assertTrue(lootTable.contains("mc3ver:bonfire"));
    }

    @Test
    void recipeIsCampfireSurroundedByLogsAndCoal() throws Exception {
        JsonObject recipe = json("data/mc3ver/recipe/bonfire.json");
        assertEquals("LKL", recipe.getAsJsonArray("pattern").get(0).getAsString());
        assertEquals("KCK", recipe.getAsJsonArray("pattern").get(1).getAsString());
        assertEquals("LKL", recipe.getAsJsonArray("pattern").get(2).getAsString());
        assertEquals("minecraft:campfire", recipe.getAsJsonObject("key").get("C").getAsString());
        assertEquals("mc3ver:bonfire", recipe.getAsJsonObject("result").get("id").getAsString());
    }

    @Test
    void texturesExist() throws Exception {
        java.awt.image.BufferedImage item = javax.imageio.ImageIO.read(
            RESOURCES.resolve("assets/mc3ver/textures/item/bonfire.png").toFile());
        assertEquals(16, item.getWidth());
        assertEquals(16, item.getHeight());
        assertTrue(Files.exists(RESOURCES.resolve("assets/mc3ver/textures/entity/bonfire_smoke.png")));
    }
}
