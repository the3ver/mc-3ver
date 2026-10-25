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
            boolean center = part == BonfireLogic.Part.CENTER;
            assertEquals(center ? "mc3ver:block/bonfire_center" : "mc3ver:block/bonfire_part",
                variants.getAsJsonObject("lit=true,part=" + name).get("model").getAsString());
            assertEquals(center ? "mc3ver:block/bonfire_center_off" : "mc3ver:block/bonfire_part",
                variants.getAsJsonObject("lit=false,part=" + name).get("model").getAsString());
        }
        assertEquals(BonfireLogic.Part.values().length * 2, variants.size());
    }

    // Minecraft lädt nur Elemente im Bereich -16..32, das deckt genau die 3x3-Fläche und zwei Blöcke Höhe ab
    @Test
    void woodpileStaysInsideTheAllowedModelBounds() throws Exception {
        for (String name : new String[]{"bonfire_center", "bonfire_center_off"}) {
            com.google.gson.JsonArray elements = json("assets/mc3ver/models/block/" + name + ".json").getAsJsonArray("elements");
            assertTrue(elements.size() >= 10, name + " braucht einen Holzstoß");
            for (com.google.gson.JsonElement element : elements) {
                JsonObject e = element.getAsJsonObject();
                for (String key : new String[]{"from", "to"}) {
                    for (com.google.gson.JsonElement c : e.getAsJsonArray(key)) {
                        double v = c.getAsDouble();
                        assertTrue(v >= -16 && v <= 32, name + ": Koordinate " + v + " außerhalb -16..32");
                    }
                }
                if (e.has("rotation")) {
                    double angle = e.getAsJsonObject("rotation").get("angle").getAsDouble();
                    assertTrue(Math.abs(angle) <= 45 && angle % 22.5 == 0, name + ": Winkel " + angle);
                }
            }
        }
    }

    @Test
    void onlyTheLitWoodpileBurns() throws Exception {
        String lit = Files.readString(RESOURCES.resolve("assets/mc3ver/models/block/bonfire_center.json"));
        String off = Files.readString(RESOURCES.resolve("assets/mc3ver/models/block/bonfire_center_off.json"));
        assertTrue(lit.contains("#fire") && lit.contains("#lit_log"));
        assertFalse(off.contains("#fire") || off.contains("#lit_log"));
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
        java.awt.image.BufferedImage smoke = javax.imageio.ImageIO.read(
            RESOURCES.resolve("assets/mc3ver/textures/entity/bonfire_smoke.png").toFile());
        // 2x2 Wolken, Ränder durchsichtig, damit beim Ausschneiden nichts aus der Nachbarwolke blutet
        assertEquals(64, smoke.getWidth());
        assertEquals(64, smoke.getHeight());
        for (int i = 0; i < 64; i++) {
            assertEquals(0, smoke.getRGB(i, 0) >>> 24);
            assertEquals(0, smoke.getRGB(31, i) >>> 24);
            assertEquals(0, smoke.getRGB(32, i) >>> 24);
        }
    }
}
