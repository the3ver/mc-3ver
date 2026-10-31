package net.frank.mc3ver.grove;

import java.util.concurrent.ConcurrentHashMap;
import net.minecraft.core.QuartPos;
import net.minecraft.world.level.WorldGenLevel;

/**
 * Tiefe des Birnenhains je Rasterpunkt (Chunk-Mitte), einmal gemessen und gemerkt.
 * Die Herz-Suche fragt viele Nachbarpunkte ab; ohne Cache wären das tausende Biomabfragen pro Chunk.
 * Das Biom kommt aus der Biomquelle und hängt nicht davon ab, welche Chunks schon generiert sind.
 */
final class GroveDepthCache {

    private static final int MAX_ENTRIES = 200_000;
    private static final ConcurrentHashMap<Long, Integer> DEPTHS = new ConcurrentHashMap<>();
    private static volatile long cachedSeed;

    private GroveDepthCache() {}

    static GroveShapeLogic.DepthGrid grid(WorldGenLevel level, int y) {
        long seed = level.getSeed();
        if (seed != cachedSeed || DEPTHS.size() > MAX_ENTRIES) {
            DEPTHS.clear();
            cachedSeed = seed;
        }
        GroveShapeLogic.Probe probe = (x, z) -> level.getUncachedNoiseBiome(
                        QuartPos.fromBlock(x), QuartPos.fromBlock(y), QuartPos.fromBlock(z))
                .is(ModGrove.PEAR_GROVE);
        return (gx, gz) -> DEPTHS.computeIfAbsent(
                ((long) gx << 32) | (gz & 0xFFFFFFFFL),
                key -> GroveShapeLogic.depth(probe, GroveShapeLogic.gridCenter(gx), GroveShapeLogic.gridCenter(gz)));
    }
}
