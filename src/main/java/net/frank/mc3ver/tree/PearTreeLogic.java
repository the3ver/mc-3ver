package net.frank.mc3ver.tree;

import net.minecraft.resources.ResourceKey;
import net.minecraft.world.level.storage.loot.providers.number.ints.ContextIntProvider;
import net.minecraft.world.level.storage.loot.providers.number.ints.ContextIntProviders;

public class PearTreeLogic {

    public static final int PEAR_NUTRITION = 4;
    public static final float PEAR_SATURATION_MODIFIER = 0.3f;

    public static final int GOLDEN_PEAR_NUTRITION = 4;
    public static final float GOLDEN_PEAR_SATURATION_MODIFIER = 1.2f;
    public static final int JUMP_BOOST_DURATION_TICKS = 1200;
    public static final int SPEED_DURATION_TICKS = 1200;
    public static final int SLOW_FALLING_DURATION_TICKS = 900;

    public static float getCompostChance(String itemName) {
        if ("pear".equals(itemName)) {
            return 0.65f;
        } else if ("pear_leaves".equals(itemName) || "pear_sapling".equals(itemName)) {
            return 0.30f;
        }
        return 0.0f;
    }

    // Seit 26.3 kompostiert der Composter über die Item-Komponente; Vanilla-Stufen statt eigener Zahlen
    public static ResourceKey<ContextIntProvider> getCompostableKey(String itemName) {
        float chance = getCompostChance(itemName);
        if (chance == 0.65f) {
            return ContextIntProviders.COMPOSTABLE_MEDIUM;
        } else if (chance == 0.30f) {
            return ContextIntProviders.COMPOSTABLE_LOW;
        }
        return null;
    }

    public static String getStrippedBlockId(String blockName) {
        if ("pear_log".equals(blockName)) {
            return "stripped_pear_log";
        } else if ("pear_wood".equals(blockName)) {
            return "stripped_pear_wood";
        }
        return null;
    }

    // Dropchancen des Birnenlaubs je Glücksstufe, wie Vanilla-Eichenlaub (Birne = Apfel) (Index = Fortune-Level,
    // letzter Wert gilt darüber hinaus).
    // Einzige Quelle für loot_table/blocks/pear_leaves.json, ein Test hält beide synchron.
    static final float[] SAPLING_DROP_CHANCES = {0.05f, 0.0625f, 0.083333336f, 0.1f};
    static final float[] STICK_DROP_CHANCES = {0.02f, 0.022222223f, 0.025f, 0.033333335f, 0.1f};
    static final float[] PEAR_DROP_CHANCES = {0.005f, 0.0055555557f, 0.00625f, 0.008333334f, 0.025f};

    public static int getFuelDurationTicks(String itemName) {
        if ("pear_boat".equals(itemName) || "pear_chest_boat".equals(itemName)) {
            return PearBoatLogic.BOAT_FUEL_DURATION_TICKS;
        } else if ("pear_log".equals(itemName)
                || "stripped_pear_log".equals(itemName)
                || "pear_wood".equals(itemName)
                || "stripped_pear_wood".equals(itemName)
                || "pear_planks".equals(itemName)
                || "pear_stairs".equals(itemName)
                || "pear_fence".equals(itemName)
                || "pear_fence_gate".equals(itemName)
                || "pear_trapdoor".equals(itemName)
                || "pear_pressure_plate".equals(itemName)) {
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

    // Seit 26.3 ist Brennstoff eine Item-Komponente; die Vanilla-Provider liefern dieselben Brenndauern
    public static ResourceKey<ContextIntProvider> getCookingFuelKey(String itemName) {
        return switch (getFuelDurationTicks(itemName)) {
            case PearBoatLogic.BOAT_FUEL_DURATION_TICKS -> ContextIntProviders.COOKING_TIME_BOATS;
            case 300 -> ContextIntProviders.COOKING_TIME_WOOD_BLOCKS;
            case 200 -> ContextIntProviders.COOKING_TIME_WOOD_ITEMS_LARGE;
            case 150 -> ContextIntProviders.COOKING_TIME_WOOD_SLABS;
            case 100 -> ContextIntProviders.COOKING_TIME_WOOD_ITEMS_EXTRA_SMALL;
            default -> null;
        };
    }

    public static java.util.List<String> getTargetBiomeIds() {
        return java.util.List.of(
                "minecraft:plains",
                "minecraft:sunflower_plains",
                "minecraft:forest",
                "minecraft:flower_forest",
                "minecraft:meadow",
                "minecraft:birch_forest");
    }
}
