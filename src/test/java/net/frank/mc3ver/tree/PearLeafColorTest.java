package net.frank.mc3ver.tree;

import static org.junit.jupiter.api.Assertions.*;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashSet;
import java.util.Set;
import org.junit.jupiter.api.Test;

class PearLeafColorTest {

    private static final Path RESOURCES = Paths.get("").toAbsolutePath().resolve("src/main/resources");
    private static final int FOLIAGE = 0x48B518;

    // Bug: Laub nutzt das graue oak_leaves-Muster, ohne Färbung bleibt es grau
    @Test
    void leavesAreTintedWithTheFoliageColor() {
        int color = PearTreeLogic.leafColor(FOLIAGE, 0, 0);
        assertNotEquals(0xFFFFFF, color, "ungefärbt = grau");
        int green = (color >> 8) & 0xFF;
        assertTrue(green > ((color >> 16) & 0xFF) && green > (color & 0xFF), "Laub bleibt grün");
    }

    @Test
    void leafItemIsTintedInTheInventory() throws Exception {
        JsonObject model = JsonParser.parseString(
                        Files.readString(RESOURCES.resolve("assets/mc3ver/items/pear_leaves.json")))
                .getAsJsonObject()
                .getAsJsonObject("model");
        JsonArray tints = model.getAsJsonArray("tints");
        assertNotNull(tints, "Item braucht einen Farbton, sonst grau im Inventar");
        assertEquals(
                "minecraft:constant", tints.get(0).getAsJsonObject().get("type").getAsString());
    }

    @Test
    void shadeStaysWithinTheVariationRange() {
        for (int x = -200; x <= 200; x += 7) {
            for (int z = -200; z <= 200; z += 5) {
                float shade = PearTreeLogic.leafShade(x, z);
                assertTrue(shade >= 1 - PearTreeLogic.LEAF_SHADE_VARIATION, "zu dunkel: " + shade);
                assertTrue(shade <= 1 + PearTreeLogic.LEAF_SHADE_VARIATION, "zu hell: " + shade);
            }
        }
    }

    @Test
    void shadeIsTheSameForTheSamePosition() {
        assertEquals(PearTreeLogic.leafShade(123, -45), PearTreeLogic.leafShade(123, -45));
    }

    @Test
    void treesInDifferentPlacesGetDifferentShades() {
        Set<Integer> shades = new HashSet<>();
        for (int i = 0; i < 20; i++) {
            shades.add(Math.round(PearTreeLogic.leafShade(i * 37, i * -53) * 100));
        }
        assertTrue(shades.size() >= 8, "zu wenig Variation: " + shades);
    }

    // Weicher Verlauf: eine Krone wirkt einheitlich, statt pixelig gesprenkelt
    @Test
    void neighbouringBlocksDifferOnlySlightly() {
        for (int x = -50; x < 50; x++) {
            for (int z = -50; z < 50; z += 3) {
                float here = PearTreeLogic.leafShade(x, z);
                assertTrue(Math.abs(here - PearTreeLogic.leafShade(x + 1, z)) < 0.05f, "Sprung bei " + x + "," + z);
                assertTrue(Math.abs(here - PearTreeLogic.leafShade(x, z + 1)) < 0.05f, "Sprung bei " + x + "," + z);
            }
        }
    }

    @Test
    void shadeScalesEachChannelAndClamps() {
        assertEquals(0x48B518, PearTreeLogic.applyShade(0x48B518, 1f));
        assertEquals(0x245B0C, PearTreeLogic.applyShade(0x48B518, 0.5f));
        assertEquals(0xFFFFFF, PearTreeLogic.applyShade(0xF0F0F0, 1.2f));
        assertEquals(0x000000, PearTreeLogic.applyShade(0x48B518, 0f));
    }

    @Test
    void leafColorIsTheShadedFoliage() {
        assertEquals(
                PearTreeLogic.applyShade(FOLIAGE, PearTreeLogic.leafShade(10, 20)),
                PearTreeLogic.leafColor(FOLIAGE, 10, 20));
    }
}
