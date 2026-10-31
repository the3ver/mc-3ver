package net.frank.mc3ver.grove;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.util.stream.Stream;
import net.frank.mc3ver.grove.GroveZoneLogic.Density;
import net.frank.mc3ver.tree.PearTreeBlocks;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.WorldGenLevel;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.chunk.ChunkGenerator;
import net.minecraft.world.level.levelgen.Heightmap;
import net.minecraft.world.level.levelgen.feature.Feature;

/**
 * Bewuchs des Birnenhains nach Zonen: Bäume außen einzeln, dann in Gruppen, innen dichter Wald, ums Herz frei.
 * Läuft einmal pro Chunk; die Zone gilt für den ganzen Chunk.
 */
public record GroveVegetationFeature(
        Holder<Feature> tree, Holder<Feature> bush, Holder<Feature> fern, Holder<Feature> flower) implements Feature {

    public static final MapCodec<GroveVegetationFeature> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
                    Feature.CODEC.fieldOf("tree").forGetter(GroveVegetationFeature::tree),
                    Feature.CODEC.fieldOf("bush").forGetter(GroveVegetationFeature::bush),
                    Feature.CODEC.fieldOf("fern").forGetter(GroveVegetationFeature::fern),
                    Feature.CODEC.fieldOf("flower").forGetter(GroveVegetationFeature::flower))
            .apply(instance, GroveVegetationFeature::new));

    private static final int GROUND_PATCH_RADIUS = 3;
    private static final int PLANT_PATCH_RADIUS = 4;
    private static final int PLANT_PATCH_TRIES = 12;

    @Override
    public MapCodec<GroveVegetationFeature> codec() {
        return CODEC;
    }

    @Override
    public Stream<Holder<Feature>> getSubFeatures() {
        return Stream.of(tree, bush, fern, flower);
    }

    @Override
    public boolean place(WorldGenLevel level, ChunkGenerator generator, RandomSource random, BlockPos origin) {
        Density density = GroveZoneLogic.densityOf(zoneOfChunk(level, generator, origin));

        if (random.nextFloat() < density.groundPatchChance()) {
            placeGroundPatch(level, origin.offset(random.nextInt(16), 0, random.nextInt(16)), random);
        }

        boolean placed = false;
        int clusters = GroveZoneLogic.countFor(density.clustersPerChunk(), random.nextFloat());
        for (int c = 0; c < clusters; c++) {
            int anchorX = origin.getX() + random.nextInt(16);
            int anchorZ = origin.getZ() + random.nextInt(16);
            int size =
                    density.minClusterSize() + random.nextInt(density.maxClusterSize() - density.minClusterSize() + 1);
            for (int t = 0; t < size; t++) {
                int x = anchorX + spread(random, density.clusterRadius());
                int z = anchorZ + spread(random, density.clusterRadius());
                placed |= placeOnGround(tree, level, generator, random, x, z);
            }
        }

        int bushes = GroveZoneLogic.countFor(density.bushesPerChunk(), random.nextFloat());
        for (int b = 0; b < bushes; b++) {
            placed |= placeOnGround(
                    bush,
                    level,
                    generator,
                    random,
                    origin.getX() + random.nextInt(16),
                    origin.getZ() + random.nextInt(16));
        }
        placed |= placePatches(fern, density.fernPatchesPerChunk(), level, generator, random, origin);
        placed |= placePatches(flower, density.flowerPatchesPerChunk(), level, generator, random, origin);
        return placed;
    }

    // Zone gilt für den ganzen Chunk, gemessen an seiner Mitte (= Rasterpunkt)
    private static GroveZoneLogic.Zone zoneOfChunk(WorldGenLevel level, ChunkGenerator generator, BlockPos origin) {
        GroveShapeLogic.DepthGrid grid = GroveDepthCache.grid(level, generator);
        int x = origin.getX() + 8;
        int z = origin.getZ() + 8;
        double toTree = Double.POSITIVE_INFINITY;
        for (GroveShapeLogic.Heart heart :
                GroveShapeLogic.heartsNear(grid, level.getSeed(), x, z, GroveZoneLogic.HEART_RADIUS)) {
            toTree = Math.min(toTree, GroveShapeLogic.distance(heart.treeX() - x, heart.treeZ() - z));
        }
        int depth = grid.depth(GroveShapeLogic.gridOf(x), GroveShapeLogic.gridOf(z));
        return GroveZoneLogic.zoneAt(depth, toTree);
    }

    // Bäume und Büsche: auf festem Boden, nicht im Wasser, nur wo ein Birnensetzling wachsen könnte
    private static boolean placeOnGround(
            Holder<Feature> feature, WorldGenLevel level, ChunkGenerator generator, RandomSource random, int x, int z) {
        int floor = level.getHeight(Heightmap.Types.OCEAN_FLOOR_WG, x, z);
        // Wasser über dem Boden: kein Baum im See
        if (floor != level.getHeight(Heightmap.Types.WORLD_SURFACE_WG, x, z)) {
            return false;
        }
        BlockPos pos = new BlockPos(x, floor, z);
        if (!PearTreeBlocks.PEAR_SAPLING.defaultBlockState().canSurvive(level, pos)) {
            return false;
        }
        return feature.value().place(level, generator, random, pos);
    }

    // Farne und Blumen: Flecken aus mehreren Pflanzen auf Gras, wie die Vanilla-Patches
    private static boolean placePatches(
            Holder<Feature> plant,
            float perChunk,
            WorldGenLevel level,
            ChunkGenerator generator,
            RandomSource random,
            BlockPos origin) {
        boolean placed = false;
        int patches = GroveZoneLogic.countFor(perChunk, random.nextFloat());
        for (int p = 0; p < patches; p++) {
            int cx = origin.getX() + random.nextInt(16);
            int cz = origin.getZ() + random.nextInt(16);
            for (int t = 0; t < PLANT_PATCH_TRIES; t++) {
                int x = cx + spread(random, PLANT_PATCH_RADIUS);
                int z = cz + spread(random, PLANT_PATCH_RADIUS);
                BlockPos pos = new BlockPos(x, level.getHeight(Heightmap.Types.WORLD_SURFACE_WG, x, z), z);
                if (level.isEmptyBlock(pos) && level.getBlockState(pos.below()).is(Blocks.GRASS_BLOCK)) {
                    placed |= plant.value().place(level, generator, random, pos);
                }
            }
        }
        return placed;
    }

    private static void placeGroundPatch(WorldGenLevel level, BlockPos center, RandomSource random) {
        BlockState coarseDirt = Blocks.COARSE_DIRT.defaultBlockState();
        BlockState podzol = Blocks.PODZOL.defaultBlockState();
        int r = GROUND_PATCH_RADIUS;
        for (int dx = -r; dx <= r; dx++) {
            for (int dz = -r; dz <= r; dz++) {
                if (dx * dx + dz * dz > r * r || random.nextFloat() < 0.2f) {
                    continue;
                }
                int x = center.getX() + dx;
                int z = center.getZ() + dz;
                BlockPos ground = new BlockPos(x, level.getHeight(Heightmap.Types.WORLD_SURFACE_WG, x, z) - 1, z);
                if (level.getBlockState(ground).is(Blocks.GRASS_BLOCK)) {
                    level.setBlock(ground, random.nextBoolean() ? coarseDirt : podzol, 2);
                }
            }
        }
    }

    private static int spread(RandomSource random, int radius) {
        return radius == 0 ? 0 : random.nextInt(2 * radius + 1) - radius;
    }
}
