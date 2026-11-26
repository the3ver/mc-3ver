package net.frank.mc3ver.village;

import static net.frank.mc3ver.TestResources.RESOURCES;
import static net.frank.mc3ver.TestResources.image;
import static net.frank.mc3ver.TestResources.json;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import java.awt.image.BufferedImage;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import org.junit.jupiter.api.Test;

class BakerResourcesTest {

    private static final String DATA = "data/mc3ver/";

    // Jede Stufe zieht 2 Angebote aus ihrem Tag; der Tag nennt nur Trades, die es als Datei gibt
    @Test
    void everyLevelHasATradeSetWithAtLeastTwoExistingTrades() throws Exception {
        List<String> problems = new ArrayList<>();
        for (int level = 1; level <= 5; level++) {
            JsonObject set = json(DATA + "trade_set/baker/level_" + level + ".json");
            assertEquals("#mc3ver:baker/level_" + level, set.get("trades").getAsString());
            int amount = set.get("amount").getAsInt();
            JsonArray values = json(DATA + "tags/villager_trade/baker/level_" + level + ".json")
                    .getAsJsonArray("values");
            if (values.size() < amount) problems.add("Stufe " + level + ": weniger Trades als amount");
            for (JsonElement value : values) {
                String id = value.getAsString();
                if (!id.startsWith("mc3ver:baker/" + level + "/")) problems.add(id + ": falsche Stufe");
                String file = DATA + "villager_trade/" + id.substring("mc3ver:".length()) + ".json";
                if (!Files.exists(RESOURCES.resolve(file))) problems.add(id + ": " + file + " fehlt");
            }
        }
        assertTrue(problems.isEmpty(), String.join("\n", problems));
    }

    @Test
    void everyTradeNamesWhatItWantsAndGives() throws Exception {
        List<String> problems = new ArrayList<>();
        try (var files = Files.walk(RESOURCES.resolve(DATA + "villager_trade/baker"))) {
            for (var file : files.filter(f -> f.toString().endsWith(".json")).toList()) {
                JsonObject trade = json(RESOURCES.relativize(file).toString());
                if (!trade.getAsJsonObject("wants").has("id")) problems.add(file + ": wants.id");
                if (!trade.getAsJsonObject("gives").has("id")) problems.add(file + ": gives.id");
                if (trade.get("max_uses").getAsInt() <= 0) problems.add(file + ": max_uses");
            }
        }
        assertTrue(problems.isEmpty(), String.join("\n", problems));
    }

    @Test
    void masterBakerSellsPearCakeAndGoldenPears() throws Exception {
        Set<String> gives = Set.of(gives("5/emerald_pear_cake"), gives("5/emerald_golden_pear"));
        assertEquals(Set.of("mc3ver:pear_cake", "mc3ver:golden_pear"), gives);
    }

    private static String gives(String trade) throws Exception {
        return json(DATA + "villager_trade/baker/" + trade + ".json")
                .getAsJsonObject("gives")
                .get("id")
                .getAsString();
    }

    @Test
    void unemployedVillagersCanTakeTheBakingOven() throws Exception {
        assertTrue(json("data/minecraft/tags/point_of_interest_type/acquirable_job_site.json")
                .getAsJsonArray("values")
                .toString()
                .contains("\"mc3ver:baker\""));
        assertTrue(json("data/minecraft/tags/block/mineable/pickaxe.json")
                .getAsJsonArray("values")
                .toString()
                .contains("\"mc3ver:baking_oven\""));
    }

    @Test
    void ovenRecipeUsesFiveBricksAFurnaceAndThreeCobblestone() throws Exception {
        assertEquals(
                Map.of("minecraft:bricks", 5, "minecraft:furnace", 1, "minecraft:cobblestone", 3),
                ingredients("baking_oven"));
    }

    @Test
    void pearCakeRecipeUsesThreePearsSugarEggAndWheat() throws Exception {
        assertEquals(
                Map.of("mc3ver:pear", 3, "minecraft:sugar", 2, "minecraft:egg", 1, "minecraft:wheat", 3),
                ingredients("pear_cake"));
    }

    private static Map<String, Integer> ingredients(String recipe) throws Exception {
        JsonObject json = json(DATA + "recipe/" + recipe + ".json");
        JsonObject key = json.getAsJsonObject("key");
        Map<String, Integer> counts = new HashMap<>();
        for (JsonElement row : json.getAsJsonArray("pattern")) {
            for (char c : row.getAsString().toCharArray()) {
                if (c != ' ') counts.merge(key.get(String.valueOf(c)).getAsString(), 1, Integer::sum);
            }
        }
        assertEquals(
                "mc3ver:" + recipe, json.getAsJsonObject("result").get("id").getAsString());
        return counts;
    }

    @Test
    void professionOverlayExistsForVillagerAndZombieWithAFullHat() throws Exception {
        for (String entity : List.of("villager", "zombie_villager")) {
            String path = "assets/mc3ver/textures/entity/" + entity + "/profession/baker.png";
            BufferedImage img = image(path);
            assertEquals(64, img.getWidth(), path);
            assertEquals(64, img.getHeight(), path);
            assertEquals(
                    "full",
                    json(path + ".mcmeta")
                            .getAsJsonObject("villager")
                            .get("hat")
                            .getAsString(),
                    path);
        }
    }

    // Mützenring u 32..63, Kopfseiten ab v 8; Brauen (v 11) und Augen (v 12) der Bewohnerin bleiben frei
    @Test
    void hatLeavesBrowsAndEyesVisible() throws Exception {
        for (String entity : List.of("villager", "zombie_villager")) {
            BufferedImage img = image("assets/mc3ver/textures/entity/" + entity + "/profession/baker.png");
            for (int y = 11; y < 18; y++) {
                for (int x = 32; x < 64; x++) {
                    assertEquals(0, img.getRGB(x, y) >>> 24, entity + " Mütze verdeckt " + x + "," + y);
                }
            }
            for (int x = 32; x < 64; x++) {
                assertEquals(255, img.getRGB(x, 10) >>> 24, entity + " Krempe fehlt bei " + x);
            }
        }
    }

    @Test
    void ovenAndCakeTexturesAre16x16() throws Exception {
        for (String path : List.of(
                "block/baking_oven_front", "block/baking_oven_side", "block/baking_oven_top", "item/pear_cake")) {
            BufferedImage img = image("assets/mc3ver/textures/" + path + ".png");
            assertEquals(16, img.getWidth(), path);
            assertEquals(16, img.getHeight(), path);
        }
    }
}
