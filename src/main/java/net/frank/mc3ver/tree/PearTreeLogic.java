package net.frank.mc3ver.tree;

public class PearTreeLogic {

    public static final int PEAR_NUTRITION = 4;
    public static final float PEAR_SATURATION_MODIFIER = 0.3f;

    public static float getCompostChance(String itemName) {
        if ("pear".equals(itemName)) {
            return 0.65f;
        } else if ("pear_leaves".equals(itemName) || "pear_sapling".equals(itemName)) {
            return 0.30f;
        }
        return 0.0f;
    }

    public static String getStrippedBlockId(String blockName) {
        if ("pear_log".equals(blockName)) {
            return "stripped_pear_log";
        } else if ("pear_wood".equals(blockName)) {
            return "stripped_pear_wood";
        }
        return null;
    }

    public static boolean shouldDropSapling(double roll, int fortuneLevel) {
        double chance = 0.05 * (1.0 + Math.max(0, fortuneLevel) / 3.0);
        return roll <= chance;
    }

    public static boolean shouldDropPear(double roll, int fortuneLevel) {
        double chance = 0.025 * (1.0 + Math.max(0, fortuneLevel) * 0.5);
        return roll <= chance;
    }

    public static int getFuelDurationTicks(String itemName) {
        if ("pear_log".equals(itemName) || "stripped_pear_log".equals(itemName)
                || "pear_wood".equals(itemName) || "stripped_pear_wood".equals(itemName)
                || "pear_planks".equals(itemName) || "pear_stairs".equals(itemName)
                || "pear_fence".equals(itemName) || "pear_fence_gate".equals(itemName)
                || "pear_trapdoor".equals(itemName) || "pear_pressure_plate".equals(itemName)) {
            return 300;
        } else if ("pear_door".equals(itemName)) {
            return 200;
        } else if ("pear_slab".equals(itemName)) {
            return 150;
        } else if ("pear_sapling".equals(itemName) || "pear_button".equals(itemName)) {
            return 100;
        }
        return 0;
    }

    public static java.util.List<String> getTargetBiomeIds() {
        return java.util.List.of(
            "minecraft:plains",
            "minecraft:sunflower_plains",
            "minecraft:forest",
            "minecraft:flower_forest",
            "minecraft:meadow",
            "minecraft:birch_forest"
        );
    }
}
