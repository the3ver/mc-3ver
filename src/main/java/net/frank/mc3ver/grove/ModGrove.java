package net.frank.mc3ver.grove;

import com.mojang.datafixers.util.Pair;
import java.util.function.Consumer;
import net.frank.mc3ver.Mc3verMod;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.biome.Biomes;
import net.minecraft.world.level.biome.Climate;
import terrablender.api.Region;
import terrablender.api.RegionType;
import terrablender.api.Regions;
import terrablender.api.TerraBlenderApi;

/**
 * Bindet den Birnenhain über TerraBlender in die Overworld ein (Fabric API kann keine Overworld-Biome).
 * Einstieg "terrablender" in fabric.mod.json.
 */
public class ModGrove implements TerraBlenderApi {

    public static final ResourceKey<Biome> PEAR_GROVE =
            ResourceKey.create(Registries.BIOME, Mc3verMod.id("pear_grove"));

    // Gewicht relativ zur Vanilla-Region (TerraBlender-Standard 10): in etwa der Hälfte der Welt wird
    // aus Flower Forest ein Birnenhain. Startwert, wird im Spiel abgestimmt.
    public static final int REGION_WEIGHT = 10;

    /** Feature-Typen; aus Mc3verMod.onInitialize, bevor Datapacks die Feature-JSONs laden. */
    public static void register() {
        Registry.register(BuiltInRegistries.FEATURE_TYPE, Mc3verMod.id("pear_grove_trees"), GroveTreesFeature.CODEC);
    }

    @Override
    public void onTerraBlenderInitialized() {
        Regions.register(new PearGroveRegion());
    }

    /** Vanilla-Overworld, nur Flower Forest wird zum Birnenhain: gleiches Klima, Nachbarn Plains und Forest. */
    static class PearGroveRegion extends Region {

        PearGroveRegion() {
            super(Mc3verMod.id("pear_grove"), RegionType.OVERWORLD, REGION_WEIGHT);
        }

        @Override
        public void addBiomes(
                Registry<Biome> registry, Consumer<Pair<Climate.ParameterPoint, ResourceKey<Biome>>> mapper) {
            addModifiedVanillaOverworldBiomes(
                    mapper, builder -> builder.replaceBiome(Biomes.FLOWER_FOREST, PEAR_GROVE));
        }
    }
}
