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

    // Laub ist mal heller, mal dunkler: weiches Rauschen über die Position (x, z), damit eine Krone einheitlich wirkt
    public static final float LEAF_SHADE_VARIATION = 0.12f;
    static final int LEAF_SHADE_CELL = 8;

    // ARGB mit vollem Alpha: Laub rendert als cutout, Alpha 0 würde jedes Blatt wegschneiden
    public static int leafColor(int foliageRgb, int x, int z) {
        return 0xFF000000 | applyShade(foliageRgb, leafShade(x, z));
    }

    public static float leafShade(int x, int z) {
        int cellX = Math.floorDiv(x, LEAF_SHADE_CELL);
        int cellZ = Math.floorDiv(z, LEAF_SHADE_CELL);
        float tx = smooth((float) Math.floorMod(x, LEAF_SHADE_CELL) / LEAF_SHADE_CELL);
        float tz = smooth((float) Math.floorMod(z, LEAF_SHADE_CELL) / LEAF_SHADE_CELL);
        float top = lerp(cornerNoise(cellX, cellZ), cornerNoise(cellX + 1, cellZ), tx);
        float bottom = lerp(cornerNoise(cellX, cellZ + 1), cornerNoise(cellX + 1, cellZ + 1), tx);
        return 1 + LEAF_SHADE_VARIATION * lerp(top, bottom, tz);
    }

    public static int applyShade(int rgb, float shade) {
        int r = Math.min(255, Math.round(((rgb >> 16) & 0xFF) * shade));
        int g = Math.min(255, Math.round(((rgb >> 8) & 0xFF) * shade));
        int b = Math.min(255, Math.round((rgb & 0xFF) * shade));
        return (r << 16) | (g << 8) | b;
    }

    // Wert in [-1, 1], fest je Rasterpunkt
    private static float cornerNoise(int cellX, int cellZ) {
        int h = cellX * 0x27D4EB2D ^ cellZ * 0x165667B1;
        h ^= h >>> 15;
        h *= 0x2C1B3C6D;
        h ^= h >>> 12;
        return (h & 0xFFFF) / 32767.5f - 1;
    }

    private static float smooth(float t) {
        return t * t * (3 - 2 * t);
    }

    private static float lerp(float a, float b, float t) {
        return a + (b - a) * t;
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
