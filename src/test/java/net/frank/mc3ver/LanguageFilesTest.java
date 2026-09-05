package net.frank.mc3ver;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import org.junit.jupiter.api.Test;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.lang.reflect.Type;
import java.nio.charset.StandardCharsets;
import java.util.Map;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

public class LanguageFilesTest {

    private static final Gson GSON = new Gson();
    private static final Type MAP_TYPE = new TypeToken<Map<String, String>>() {}.getType();

    private Map<String, String> loadLangFile(String filename) {
        InputStream stream = getClass().getClassLoader().getResourceAsStream("assets/mc3ver/lang/" + filename);
        assertNotNull(stream, "Language file assets/mc3ver/lang/" + filename + " must exist");
        return GSON.fromJson(new InputStreamReader(stream, StandardCharsets.UTF_8), MAP_TYPE);
    }

    @Test
    void testLanguageFiles_ContainAllRequiredKeysAndMatch() {
        Map<String, String> enUs = loadLangFile("en_us.json");
        Map<String, String> deDe = loadLangFile("de_de.json");

        Set<String> requiredKeys = Set.of(
            "block.mc3ver.transport_flame",
            "item.mc3ver.transport_flame",
            "item.mc3ver.transport_map",
            "item.mc3ver.wind_wand",
            "item.mc3ver.lightning_wand",
            "item.mc3ver.pear",
            "item.mc3ver.golden_pear",
            "block.mc3ver.pear_log",
            "item.mc3ver.pear_log",
            "block.mc3ver.stripped_pear_log",
            "item.mc3ver.stripped_pear_log",
            "block.mc3ver.pear_wood",
            "item.mc3ver.pear_wood",
            "block.mc3ver.stripped_pear_wood",
            "item.mc3ver.stripped_pear_wood",
            "block.mc3ver.pear_planks",
            "item.mc3ver.pear_planks",
            "block.mc3ver.pear_leaves",
            "item.mc3ver.pear_leaves",
            "block.mc3ver.pear_sapling",
            "item.mc3ver.pear_sapling",
            "block.mc3ver.pear_stairs",
            "item.mc3ver.pear_stairs",
            "block.mc3ver.pear_slab",
            "item.mc3ver.pear_slab",
            "block.mc3ver.pear_fence",
            "item.mc3ver.pear_fence",
            "block.mc3ver.pear_fence_gate",
            "item.mc3ver.pear_fence_gate",
            "block.mc3ver.pear_door",
            "item.mc3ver.pear_door",
            "block.mc3ver.pear_trapdoor",
            "item.mc3ver.pear_trapdoor",
            "block.mc3ver.pear_pressure_plate",
            "item.mc3ver.pear_pressure_plate",
            "block.mc3ver.pear_button",
            "item.mc3ver.pear_button",
            "tooltip.mc3ver.destination",
            "tooltip.mc3ver.coordinates",
            "tooltip.mc3ver.unlinked",
            "message.mc3ver.no_target",
            "message.mc3ver.flame_extinguished",
            "message.mc3ver.already_at_target",
            "message.mc3ver.teleport_success",
            "message.mc3ver.welcome"
        );

        for (String key : requiredKeys) {
            assertTrue(enUs.containsKey(key), "en_us.json must contain key: " + key);
            assertTrue(deDe.containsKey(key), "de_de.json must contain key: " + key);
            assertFalse(enUs.get(key).isBlank(), "en_us.json value for " + key + " must not be blank");
            assertFalse(deDe.get(key).isBlank(), "de_de.json value for " + key + " must not be blank");
        }

        assertEquals(enUs.keySet(), deDe.keySet(), "Both language files should have the exact same set of translation keys");
    }
}
