package net.frank.mc3ver.grove;

import static org.junit.jupiter.api.Assertions.*;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import org.junit.jupiter.api.Test;

class GroveHeartResourcesTest {

    private static final Path DATA = Paths.get("").toAbsolutePath().resolve("src/main/resources/data");

    private static JsonObject json(String path) throws Exception {
        return JsonParser.parseString(Files.readString(DATA.resolve(path))).getAsJsonObject();
    }

    private static List<String> names(JsonArray entries) {
        List<String> names = new ArrayList<>();
        for (JsonElement e : entries) {
            JsonObject entry = e.getAsJsonObject();
            names.add(
                    entry.has("name")
                            ? entry.get("name").getAsString()
                            : entry.get("type").getAsString());
        }
        return names;
    }

    private static int weightOf(JsonArray entries, String name) {
        for (JsonElement e : entries) {
            JsonObject entry = e.getAsJsonObject();
            String id = entry.has("name")
                    ? entry.get("name").getAsString()
                    : entry.get("type").getAsString();
            if (id.equals(name)) {
                return entry.get("weight").getAsInt();
            }
        }
        throw new AssertionError(name + " fehlt");
    }

    @Test
    void chestAlwaysHoldsTheBasicStock() throws Exception {
        JsonArray pools = json("mc3ver/loot_table/chests/pear_grove_heart.json").getAsJsonArray("pools");
        assertEquals(
                List.of("mc3ver:pear", "mc3ver:pear_sapling", "minecraft:honeycomb", "minecraft:bone_meal"),
                names(pools.get(0).getAsJsonObject().getAsJsonArray("entries")));
    }

    // Selten genau eins: goldene Birne, ungeprägter Freundschaftsstein, Windstab oder Blitzstab
    @Test
    void chestSometimesHoldsExactlyOneRareItem() throws Exception {
        JsonObject rare = json("mc3ver/loot_table/chests/pear_grove_heart.json")
                .getAsJsonArray("pools")
                .get(1)
                .getAsJsonObject();
        assertEquals(1, rare.get("rolls").getAsInt(), "genau ein Zug");
        JsonArray entries = rare.getAsJsonArray("entries");
        assertEquals(
                List.of(
                        "minecraft:empty",
                        "mc3ver:golden_pear",
                        "mc3ver:friendship_stone",
                        "mc3ver:wind_wand",
                        "mc3ver:lightning_wand"),
                names(entries));
        int total = 0;
        for (JsonElement e : entries) {
            total += e.getAsJsonObject().get("weight").getAsInt();
        }
        double rareChance = 1 - (double) weightOf(entries, "minecraft:empty") / total;
        assertTrue(rareChance >= 0.2 && rareChance <= 0.4, "selten: " + rareChance);
        // Ungeprägt: kein Eintrag setzt Komponenten auf den Freundschaftsstein
        for (JsonElement e : entries) {
            JsonObject entry = e.getAsJsonObject();
            if (entry.has("name") && entry.get("name").getAsString().equals("mc3ver:friendship_stone")) {
                assertFalse(entry.has("modifier") || entry.has("functions"), "Stein ungeprägt");
            }
        }
    }

    @Test
    void structureOnlyInThePearGroveAndEveryChunkIsACandidate() throws Exception {
        JsonObject structure = json("mc3ver/worldgen/structure/pear_grove_heart.json");
        assertEquals("mc3ver:pear_grove_heart", structure.get("type").getAsString());
        assertEquals(
                "#mc3ver:has_structure/pear_grove_heart",
                structure.get("biomes").getAsString());
        JsonArray biomes = json("mc3ver/tags/worldgen/biome/has_structure/pear_grove_heart.json")
                .getAsJsonArray("values");
        assertEquals(List.of("mc3ver:pear_grove"), names(biomesAsEntries(biomes)));

        // Ob ein Herz entsteht, entscheidet GroveShapeLogic im Chunk selbst; die Platzierung muss jeden Chunk anbieten
        JsonObject placement =
                json("mc3ver/worldgen/structure_set/pear_grove_heart.json").getAsJsonObject("placement");
        assertEquals("minecraft:random_spread", placement.get("type").getAsString());
        assertEquals(1, placement.get("spacing").getAsInt());
        assertEquals(0, placement.get("separation").getAsInt());
    }

    private static JsonArray biomesAsEntries(JsonArray values) {
        JsonArray entries = new JsonArray();
        for (JsonElement v : values) {
            JsonObject o = new JsonObject();
            o.addProperty("name", v.getAsString());
            entries.add(o);
        }
        return entries;
    }

    // Birnblüte verhält sich wie eine Vanilla-Blume: Bienen fliegen sie an, sie passt in den Blumentopf,
    // Wasser spült sie weg, sie gibt rosa Farbstoff
    @Test
    void pearBlossomIsTaggedLikeASmallFlower() throws Exception {
        for (String tag : new String[] {
            "block/small_flowers",
            "block/bee_attractive",
            "block/washed_away_by_fluids",
            "item/small_flowers",
            "item/bee_food"
        }) {
            assertTrue(
                    json("minecraft/tags/" + tag + ".json")
                            .getAsJsonArray("values")
                            .contains(new com.google.gson.JsonPrimitive("mc3ver:pear_blossom")),
                    tag);
        }
        assertTrue(json("minecraft/tags/block/flower_pots.json")
                .getAsJsonArray("values")
                .contains(new com.google.gson.JsonPrimitive("mc3ver:potted_pear_blossom")));
        assertEquals(
                "minecraft:pink_dye",
                json("mc3ver/recipe/pink_dye_from_pear_blossom.json")
                        .getAsJsonObject("result")
                        .get("id")
                        .getAsString());
    }
}
