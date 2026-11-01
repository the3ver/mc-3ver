package net.frank.mc3ver.grove;

import com.mojang.datafixers.util.Pair;
import java.util.function.Consumer;
import net.fabricmc.fabric.api.particle.v1.FabricParticleTypes;
import net.frank.mc3ver.Mc3verMod;
import net.minecraft.core.Registry;
import net.minecraft.core.particles.SimpleParticleType;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.biome.Biomes;
import net.minecraft.world.level.biome.Climate;
import net.minecraft.world.level.levelgen.structure.StructureType;
import net.minecraft.world.level.levelgen.structure.pieces.StructurePieceType;
import net.minecraft.world.level.storage.loot.LootTable;
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

    // Gewicht relativ zur Vanilla-Region (TerraBlender-Standard 10): in etwa einem Viertel der Welt werden
    // Forest und Flower Forest zum Birnenhain. Startwert, wird im Spiel abgestimmt.
    public static final int REGION_WEIGHT = 3;

    public static final ResourceKey<LootTable> HEART_LOOT =
            ResourceKey.create(Registries.LOOT_TABLE, Mc3verMod.id("chests/pear_grove_heart"));

    public static final StructurePieceType HEART_PIECE = Registry.register(
            BuiltInRegistries.STRUCTURE_PIECE, Mc3verMod.id("pear_grove_heart"), (StructurePieceType.ContextlessType)
                    PearGroveHeartPiece::new);

    public static final StructureType<PearGroveHeartStructure> HEART_STRUCTURE = Registry.register(
            BuiltInRegistries.STRUCTURE_TYPE, Mc3verMod.id("pear_grove_heart"), () -> PearGroveHeartStructure.CODEC);

    // Goldene Pollen, die im Hain schweben; Häufigkeit steht im Biom (visual/ambient_particles)
    public static final SimpleParticleType POLLEN = Registry.register(
            BuiltInRegistries.PARTICLE_TYPE, Mc3verMod.id("pear_pollen"), FabricParticleTypes.simple());

    /**
     * Feature-Typ; aus Mc3verMod.onInitialize, bevor Datapacks die JSONs laden. Strukturtyp, Stücktyp und
     * Partikeltyp registrieren sich beim Laden der Klasse über die Felder oben.
     */
    public static void register() {
        Registry.register(
                BuiltInRegistries.FEATURE_TYPE, Mc3verMod.id("pear_grove_vegetation"), GroveVegetationFeature.CODEC);
    }

    @Override
    public void onTerraBlenderInitialized() {
        Regions.register(new PearGroveRegion());
    }

    /**
     * Vanilla-Overworld, Forest und Flower Forest werden zum Birnenhain: gleiches Klima, Nachbarn Plains und
     * Meadow. Flower Forest allein gab nur Flecken um 64×64 Blöcke, zu klein für die Zonen.
     */
    static class PearGroveRegion extends Region {

        PearGroveRegion() {
            super(Mc3verMod.id("pear_grove"), RegionType.OVERWORLD, REGION_WEIGHT);
        }

        @Override
        public void addBiomes(
                Registry<Biome> registry, Consumer<Pair<Climate.ParameterPoint, ResourceKey<Biome>>> mapper) {
            addModifiedVanillaOverworldBiomes(mapper, builder -> {
                builder.replaceBiome(Biomes.FOREST, PEAR_GROVE);
                builder.replaceBiome(Biomes.FLOWER_FOREST, PEAR_GROVE);
            });
        }
    }
}
