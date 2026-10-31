package net.frank.mc3ver.grove;

import java.util.concurrent.ConcurrentHashMap;
import net.minecraft.core.QuartPos;
import net.minecraft.world.level.LevelHeightAccessor;
import net.minecraft.world.level.WorldGenLevel;
import net.minecraft.world.level.biome.BiomeResolver;
import net.minecraft.world.level.chunk.ChunkGenerator;
import net.minecraft.world.level.levelgen.Heightmap;
import net.minecraft.world.level.levelgen.RandomState;

/**
 * Tiefe des Birnenhains je Rasterpunkt (Chunk-Mitte), einmal gemessen und gemerkt.
 * Die Herz-Suche fragt viele Nachbarpunkte ab; ohne Cache wären das tausende Biomabfragen pro Chunk.
 *
 * <p>Biome sind dreidimensional: unter der Oberfläche liegen Höhlenbiome, in der Luft weichen sie ab.
 * Deshalb zählt das Biom an der berechneten Oberflächenhöhe, wie beim Biom-Filter der Platzierung.
 * Die Höhe kostet eine Rauschsäule und wird je 8×8-Feld gemerkt. Alles kommt aus Generator und
 * Biomquelle und hängt nicht davon ab, welche Chunks schon generiert sind.
 */
final class GroveDepthCache {

    private static final int CELL = 8;
    private static final int MAX_ENTRIES = 200_000;
    private static final ConcurrentHashMap<Long, Integer> DEPTHS = new ConcurrentHashMap<>();
    private static final ConcurrentHashMap<Long, Boolean> SURFACE_GROVE = new ConcurrentHashMap<>();
    private static volatile long cachedSeed;

    private GroveDepthCache() {}

    /** Für Features: alles kommt aus der Weltgenerierungs-Region. */
    static GroveShapeLogic.DepthGrid grid(WorldGenLevel level, ChunkGenerator generator) {
        return grid(
                level.getSeed(),
                generator,
                level,
                level.getLevel().getChunkSource().randomState(),
                level::getUncachedNoiseBiome);
    }

    /** Für Strukturen und Features gleich: Seed, Generator, Welthöhe, Rauschzustand und Biomquelle. */
    static GroveShapeLogic.DepthGrid grid(
            long seed,
            ChunkGenerator generator,
            LevelHeightAccessor heightAccessor,
            RandomState randomState,
            BiomeResolver biomes) {
        if (seed != cachedSeed || DEPTHS.size() > MAX_ENTRIES || SURFACE_GROVE.size() > MAX_ENTRIES) {
            DEPTHS.clear();
            SURFACE_GROVE.clear();
            cachedSeed = seed;
        }
        GroveShapeLogic.Probe probe = (x, z) -> {
            int cellX = Math.floorDiv(x, CELL);
            int cellZ = Math.floorDiv(z, CELL);
            return SURFACE_GROVE.computeIfAbsent(key(cellX, cellZ), k -> {
                int cx = cellX * CELL + CELL / 2;
                int cz = cellZ * CELL + CELL / 2;
                int y = generator.getBaseHeight(cx, cz, Heightmap.Types.WORLD_SURFACE_WG, heightAccessor, randomState);
                return biomes.getNoiseBiome(QuartPos.fromBlock(cx), QuartPos.fromBlock(y), QuartPos.fromBlock(cz))
                        .is(ModGrove.PEAR_GROVE);
            });
        };
        return (gx, gz) -> DEPTHS.computeIfAbsent(
                key(gx, gz),
                k -> GroveShapeLogic.depth(probe, GroveShapeLogic.gridCenter(gx), GroveShapeLogic.gridCenter(gz)));
    }

    private static long key(int a, int b) {
        return ((long) a << 32) | (b & 0xFFFFFFFFL);
    }
}
