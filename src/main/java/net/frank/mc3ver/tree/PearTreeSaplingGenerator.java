package net.frank.mc3ver.tree;

import net.fabricmc.fabric.api.biome.v1.BiomeModifications;
import net.fabricmc.fabric.api.biome.v1.BiomeSelectors;
import net.frank.mc3ver.Mc3verMod;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.util.random.WeightedList;
import net.minecraft.world.level.biome.Biomes;
import net.minecraft.world.level.block.grower.TreeGrower;
import net.minecraft.world.level.levelgen.GenerationStep;
import net.minecraft.world.level.levelgen.feature.Feature;
import net.minecraft.world.level.levelgen.placement.PlacedFeature;

public class PearTreeSaplingGenerator {

    public static final ResourceKey<Feature> PEAR_TREE_KEY = ResourceKey.create(
        Registries.FEATURE,
        Mc3verMod.id("pear_tree")
    );

    public static final ResourceKey<PlacedFeature> PEAR_TREE_PLACED_KEY = ResourceKey.create(
        Registries.PLACED_FEATURE,
        Mc3verMod.id("pear_tree_placed")
    );

    public static final TreeGrower PEAR_TREE_GROWER = new TreeGrower(
        "mc3ver:pear_tree",
        WeightedList.of(PEAR_TREE_KEY),
        WeightedList.of(),
        WeightedList.of(),
        PEAR_TREE_KEY
    );

    public static void registerWorldGen() {
        BiomeModifications.addFeature(
            BiomeSelectors.includeByKey(
                Biomes.PLAINS,
                Biomes.SUNFLOWER_PLAINS,
                Biomes.FOREST,
                Biomes.FLOWER_FOREST,
                Biomes.MEADOW,
                Biomes.BIRCH_FOREST
            ),
            GenerationStep.Decoration.VEGETAL_DECORATION,
            PEAR_TREE_PLACED_KEY
        );
    }
}
