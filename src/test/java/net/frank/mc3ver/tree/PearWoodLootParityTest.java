package net.frank.mc3ver.tree;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

/** Birnenholz droppt wie Eichenholz, auch bei Explosionen. Vergleich mit den Vanilla-Tabellen im Minecraft-JAR. */
class PearWoodLootParityTest {

    @ParameterizedTest
    @ValueSource(
            strings = {
                "log",
                "stripped_log",
                "wood",
                "stripped_wood",
                "planks",
                "stairs",
                "slab",
                "fence",
                "fence_gate",
                "door",
                "trapdoor",
                "pressure_plate",
                "button",
                "sapling"
            })
    void pearLootEqualsOakLoot(String part) throws IOException {
        String oak = read("/data/minecraft/loot_table/blocks/" + oakName(part) + ".json");
        String pear = read("/data/mc3ver/loot_table/blocks/" + pearName(part) + ".json");
        String expected = oak.replace("minecraft:" + oakName(part), "mc3ver:" + pearName(part));
        assertEquals(drops(expected), drops(pear), pearName(part) + " weicht von " + oakName(part) + " ab");
    }

    @Test
    void vanillaLootIsOnTheTestClasspath() {
        assertNotNull(getClass().getResource("/data/minecraft/loot_table/blocks/oak_log.json"));
    }

    // random_sequence legt nur den Zufallsstrom fest, nicht die Drops; Fabric-Datagen lässt es weg
    private static JsonObject drops(String json) {
        JsonObject table = JsonParser.parseString(json).getAsJsonObject();
        table.remove("random_sequence");
        return table;
    }

    private static String oakName(String part) {
        return part.startsWith("stripped_") ? "stripped_oak_" + part.substring(9) : "oak_" + part;
    }

    private static String pearName(String part) {
        return part.startsWith("stripped_") ? "stripped_pear_" + part.substring(9) : "pear_" + part;
    }

    private static String read(String path) throws IOException {
        try (InputStream in = PearWoodLootParityTest.class.getResourceAsStream(path)) {
            assertNotNull(in, path + " fehlt im Classpath");
            return new String(in.readAllBytes(), StandardCharsets.UTF_8);
        }
    }
}
