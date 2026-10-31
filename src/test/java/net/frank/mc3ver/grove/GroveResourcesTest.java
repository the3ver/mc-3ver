package net.frank.mc3ver.grove;

import static org.junit.jupiter.api.Assertions.*;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import org.junit.jupiter.api.Test;

class GroveResourcesTest {

    private static final Path RESOURCES = Paths.get("").toAbsolutePath().resolve("src/main/resources");
    private static final int VEGETAL_DECORATION = 9;

    private static JsonObject json(String path) throws Exception {
        return JsonParser.parseString(Files.readString(RESOURCES.resolve(path))).getAsJsonObject();
    }

    private static JsonObject biome() throws Exception {
        return json("data/mc3ver/worldgen/biome/pear_grove.json");
    }

    private static JsonObject vanillaPlains() throws Exception {
        try (InputStream in = GroveResourcesTest.class
                .getClassLoader()
                .getResourceAsStream("data/minecraft/worldgen/biome/plains.json")) {
            assertNotNull(in, "Vanilla-plains.json muss im Minecraft-JAR liegen");
            return JsonParser.parseReader(new InputStreamReader(in, StandardCharsets.UTF_8))
                    .getAsJsonObject();
        }
    }

    private static List<String> strings(JsonArray array) {
        List<String> result = new ArrayList<>();
        for (JsonElement e : array) {
            result.add(e.getAsString());
        }
        return result;
    }

    @Test
    void climateMatchesTheSpec() throws Exception {
        JsonObject biome = biome();
        assertEquals(0.8f, biome.get("temperature").getAsFloat());
        assertEquals(0.5f, biome.get("downfall").getAsFloat());
        assertTrue(biome.get("has_precipitation").getAsBoolean());

        JsonObject effects = biome.getAsJsonObject("effects");
        assertEquals("#a4b94e", effects.get("grass_color").getAsString());
        assertEquals("#93ae47", effects.get("foliage_color").getAsString());
        assertEquals("#3fb8c4", effects.get("water_color").getAsString());

        JsonObject attributes = biome.getAsJsonObject("attributes");
        assertEquals(
                "minecraft:music.overworld.meadow",
                attributes
                        .getAsJsonObject("minecraft:audio/background_music")
                        .getAsJsonObject("default")
                        .get("sound")
                        .getAsString());
    }

    @Test
    void undergroundIsVanillaPlains() throws Exception {
        JsonObject biome = biome();
        JsonObject plains = vanillaPlains();
        assertEquals(plains.get("carvers"), biome.get("carvers"));
        JsonArray steps = biome.getAsJsonArray("features");
        JsonArray plainsSteps = plains.getAsJsonArray("features");
        assertEquals(plainsSteps.size(), steps.size());
        for (int i = 0; i < steps.size(); i++) {
            if (i != VEGETAL_DECORATION) {
                assertEquals(plainsSteps.get(i), steps.get(i), "Schritt " + i + " wie Plains");
            }
        }
    }

    // Gemeinsame Features müssen in jedem Biom gleich sortiert sein, sonst bricht die Weltgenerierung
    // mit "Feature order cycle" ab. Fabric hängt den Birnbaum in Plains ans Ende.
    @Test
    void vegetationKeepsThePlainsFeatureOrder() throws Exception {
        List<String> plainsOrder = strings(vanillaPlains()
                .getAsJsonArray("features")
                .get(VEGETAL_DECORATION)
                .getAsJsonArray());
        plainsOrder.add("mc3ver:pear_tree_placed");
        List<String> grove = strings(
                biome().getAsJsonArray("features").get(VEGETAL_DECORATION).getAsJsonArray());

        assertTrue(grove.contains("mc3ver:pear_tree_placed"), "Birnbäume im Hain");
        int last = -1;
        for (String feature : grove) {
            int index = plainsOrder.indexOf(feature);
            assertTrue(index >= 0, feature + " fehlt in Plains, Reihenfolge nicht geprüft");
            assertTrue(index > last, feature + " steht in Plains früher");
            last = index;
        }
    }

    @Test
    void groveIsTaggedAsOverworldForest() throws Exception {
        for (String tag : List.of("is_overworld", "is_forest")) {
            JsonObject json = json("data/minecraft/tags/worldgen/biome/" + tag + ".json");
            assertFalse(json.get("replace").getAsBoolean(), tag + " darf Vanilla nicht ersetzen");
            assertTrue(strings(json.getAsJsonArray("values")).contains("mc3ver:pear_grove"), tag);
        }
    }

    @Test
    void terraBlenderLoadsTheRegion() throws Exception {
        JsonObject mod = json("fabric.mod.json");
        assertEquals(
                List.of(ModGrove.class.getName()),
                strings(mod.getAsJsonObject("entrypoints").getAsJsonArray("terrablender")));
        assertTrue(mod.getAsJsonObject("depends").has("terrablender"));
    }
}
