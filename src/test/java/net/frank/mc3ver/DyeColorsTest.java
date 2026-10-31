package net.frank.mc3ver;

import static org.junit.jupiter.api.Assertions.*;

import java.util.List;
import java.util.Map;
import net.minecraft.world.item.DyeColor;
import org.junit.jupiter.api.Test;

public class DyeColorsTest {

    // Die Werte, die Zelt, Transportflamme und Signalfeuer bisher jeweils selbst als Tabelle führten
    private static final Map<String, Integer> PREVIOUS_TABLE = Map.ofEntries(
            Map.entry("white", 0xF9FFFE),
            Map.entry("orange", 0xF9801D),
            Map.entry("magenta", 0xC74EBD),
            Map.entry("light_blue", 0x3AB3DA),
            Map.entry("yellow", 0xFED83D),
            Map.entry("lime", 0x80C71F),
            Map.entry("pink", 0xF38BAA),
            Map.entry("gray", 0x474F52),
            Map.entry("light_gray", 0x9D9D97),
            Map.entry("cyan", 0x169C9C),
            Map.entry("purple", 0x8932B8),
            Map.entry("blue", 0x3C44AA),
            Map.entry("brown", 0x835432),
            Map.entry("green", 0x5E7C16),
            Map.entry("red", 0xB02E26),
            Map.entry("black", 0x1D1D21));

    @Test
    void vanillaDyeColorsMatchThePreviousTables() {
        assertEquals(PREVIOUS_TABLE.size(), DyeColor.values().length);
        for (DyeColor dye : DyeColor.values()) {
            assertEquals(PREVIOUS_TABLE.get(dye.getName()), DyeColors.rgb(dye), dye.getName());
        }
    }

    @Test
    void byNameCoversAllDyesWithoutAlpha() {
        assertEquals(PREVIOUS_TABLE, DyeColors.BY_NAME);
    }

    @Test
    void allRgbKeepsTheVanillaDyeOrder() {
        List<Integer> all = DyeColors.ALL_RGB;
        assertEquals(16, all.size());
        assertEquals(0xF9FFFE, all.get(0));
        assertEquals(0xF9801D, all.get(1));
        assertEquals(0x1D1D21, all.get(15));
    }
}
