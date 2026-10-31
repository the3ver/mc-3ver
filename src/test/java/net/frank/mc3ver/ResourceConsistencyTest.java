package net.frank.mc3ver;

import static org.junit.jupiter.api.Assertions.assertTrue;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import org.junit.jupiter.api.Test;

/**
 * Hält die Ressourcen jedes Blocks und Items vollständig: Blockstate, Item-Definition, Modell, Loot-Tabelle und
 * Übersetzung (Gleichstand de/en prüft LanguageFilesTest). Fehlt eins davon, zeigt das Spiel lila-schwarze Kacheln, Rohschlüssel statt Namen
 * oder der Block droppt nichts.
 */
class ResourceConsistencyTest {

    private static final Path RESOURCES = Paths.get("").toAbsolutePath().resolve("src/main/resources");
    private static final Path ASSETS = RESOURCES.resolve("assets/mc3ver");
    private static final Path DATA = RESOURCES.resolve("data/mc3ver");

    // Zeltteile entstehen nur beim Aufstellen des Zelts und wandern beim Einpacken ins Zelt-Item zurück
    private static final Set<String> BLOCKS_WITHOUT_ITEM_AND_LOOT =
            Set.of("tent_canvas", "tent_chest", "tent_flap", "tent_furnace", "tent_pole", "tent_sleeping_bag");

    // "type": "mc3ver:..." nennt einen Rezepttyp, kein Item
    private static final Pattern MOD_ID = Pattern.compile("(\"type\"\\s*:\\s*)?\"mc3ver:([a-z0-9_/]+)\"");
    private static final Pattern MODEL_REF = Pattern.compile("\"model\"\\s*:\\s*\"mc3ver:([a-z0-9_/]+)\"");

    @Test
    void everyBlockHasItemLootAndTranslation() throws IOException {
        JsonObject en = lang("en_us");
        Set<String> items = names(ASSETS.resolve("items"));
        Set<String> loot = names(DATA.resolve("loot_table/blocks"));
        List<String> problems = new ArrayList<>();
        for (String block : names(ASSETS.resolve("blockstates"))) {
            if (!en.has("block.mc3ver." + block)) problems.add(block + ": Übersetzung block.mc3ver." + block);
            if (BLOCKS_WITHOUT_ITEM_AND_LOOT.contains(block)) continue;
            if (!items.contains(block))
                problems.add(block + ": Item-Definition assets/mc3ver/items/" + block + ".json");
            if (!loot.contains(block))
                problems.add(block + ": Loot-Tabelle data/mc3ver/loot_table/blocks/" + block + ".json");
        }
        assertTrue(problems.isEmpty(), "Unvollständige Blöcke:\n" + String.join("\n", problems));
    }

    @Test
    void everyItemHasTranslation() throws IOException {
        JsonObject en = lang("en_us");
        List<String> missing = names(ASSETS.resolve("items")).stream()
                .filter(item -> !en.has("item.mc3ver." + item) && !en.has("block.mc3ver." + item))
                .toList();
        assertTrue(missing.isEmpty(), "Items ohne Übersetzung (item.mc3ver.* oder block.mc3ver.*): " + missing);
    }

    @Test
    void lootTablesBelongToExistingBlocks() throws IOException {
        Set<String> orphans = names(DATA.resolve("loot_table/blocks"));
        orphans.removeAll(names(ASSETS.resolve("blockstates")));
        assertTrue(orphans.isEmpty(), "Loot-Tabellen ohne Block: " + orphans);
    }

    @Test
    void blockstatesAndItemDefinitionsPointToExistingModels() throws IOException {
        List<String> missing = new ArrayList<>();
        for (Path dir : List.of(ASSETS.resolve("blockstates"), ASSETS.resolve("items"))) {
            for (Path file : jsonFiles(dir)) {
                Matcher m = MODEL_REF.matcher(Files.readString(file));
                while (m.find()) {
                    if (!Files.exists(ASSETS.resolve("models/" + m.group(1) + ".json"))) {
                        missing.add(RESOURCES.relativize(file) + " -> mc3ver:" + m.group(1));
                    }
                }
            }
        }
        assertTrue(missing.isEmpty(), "Fehlende Modelle:\n" + String.join("\n", missing));
    }

    // Rezepte und Loot-Tabellen dürfen nur mc3ver-Items nennen, die es gibt (Tippfehler fallen sonst erst im Spiel auf)
    @Test
    void recipesAndLootReferenceExistingItems() throws IOException {
        Set<String> items = names(ASSETS.resolve("items"));
        List<String> unknown = new ArrayList<>();
        for (Path dir : List.of(DATA.resolve("recipe"), DATA.resolve("loot_table"))) {
            for (Path file : jsonFiles(dir)) {
                Matcher m = MOD_ID.matcher(Files.readString(file));
                while (m.find()) {
                    String id = m.group(2);
                    if (m.group(1) == null && !items.contains(id) && !id.startsWith("blocks/")) {
                        unknown.add(RESOURCES.relativize(file) + " -> mc3ver:" + id);
                    }
                }
            }
        }
        assertTrue(unknown.isEmpty(), "Unbekannte mc3ver-IDs:\n" + String.join("\n", unknown));
    }

    private static JsonObject lang(String code) throws IOException {
        return JsonParser.parseString(Files.readString(ASSETS.resolve("lang/" + code + ".json")))
                .getAsJsonObject();
    }

    private static Set<String> names(Path dir) throws IOException {
        return jsonFiles(dir).stream()
                .map(f -> dir.relativize(f).toString().replace('\\', '/').replaceAll("\\.json$", ""))
                .collect(Collectors.toCollection(TreeSet::new));
    }

    private static List<Path> jsonFiles(Path dir) throws IOException {
        try (Stream<Path> files = Files.walk(dir)) {
            return files.filter(f -> f.toString().endsWith(".json")).toList();
        }
    }
}
