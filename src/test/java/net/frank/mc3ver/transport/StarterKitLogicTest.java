package net.frank.mc3ver.transport;

import org.junit.jupiter.api.Test;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

public class StarterKitLogicTest {

    @Test
    void testStarterKitContainsMaterialsForTwoFlames() {
        Map<String, Integer> items = StarterKitLogic.getStarterItems();

        assertEquals(2, items.getOrDefault("torch", 0), "Should contain 2 torches for 2 transport flames");
        assertEquals(8, items.getOrDefault("cobblestone", 0), "Should contain 8 cobblestone for 2 transport flames");
        assertEquals(1, items.getOrDefault("crafting_table", 0), "Should contain 1 crafting table");
    }
}
