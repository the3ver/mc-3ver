package net.frank.mc3ver.teleport;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import org.junit.jupiter.api.Test;

import java.nio.file.Files;
import java.nio.file.Paths;

import static org.junit.jupiter.api.Assertions.*;

public class DimensionsTest {

    @Test
    void translationKeyUsesThePathOfTheDimensionId() {
        assertEquals("dimension.mc3ver.overworld", Dimensions.translationKey("minecraft:overworld"));
        assertEquals("dimension.mc3ver.the_nether", Dimensions.translationKey("minecraft:the_nether"));
        assertEquals("dimension.mc3ver.the_end", Dimensions.translationKey("minecraft:the_end"));
    }

    // Der deutsche Tooltip der Transportkarte zeigte "Overworld", weil der Name fest auf Englisch im Code stand
    @Test
    void vanillaDimensionsHavePlainNamesInBothLanguages() throws Exception {
        String[][] expected = {
            {"de_de", "Oberwelt", "Nether", "End"},
            {"en_us", "Overworld", "Nether", "End"},
        };
        for (String[] row : expected) {
            JsonObject texts = JsonParser.parseString(Files.readString(Paths.get("").toAbsolutePath()
                .resolve("src/main/resources/assets/mc3ver/lang/" + row[0] + ".json"))).getAsJsonObject();
            assertEquals(row[1], texts.get("dimension.mc3ver.overworld").getAsString(), row[0]);
            assertEquals(row[2], texts.get("dimension.mc3ver.the_nether").getAsString(), row[0]);
            assertEquals(row[3], texts.get("dimension.mc3ver.the_end").getAsString(), row[0]);
        }
    }
}
