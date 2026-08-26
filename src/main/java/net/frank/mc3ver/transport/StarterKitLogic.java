package net.frank.mc3ver.transport;

import java.util.Map;

public class StarterKitLogic {

    public static Map<String, Integer> getStarterItems() {
        return Map.of(
            "torch", 2,
            "cobblestone", 8,
            "crafting_table", 1
        );
    }
}
