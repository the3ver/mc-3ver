package net.frank.mc3ver.grove;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * Hain-Mittelpunkte: ein Kandidat pro Rasterzelle, deterministisch aus Seed und Zelle.
 * Baum-Feature und Herzstück-Struktur fragen beide hier, damit Dichte und alter Baum zusammenpassen.
 * Ob ein Kandidat zählt (Biom am Punkt ist Birnenhain), prüft der Aufrufer.
 */
public class GroveCenterLogic {

    public static final int CELL_SIZE_CHUNKS = 24;
    public static final int CELL_SIZE_BLOCKS = CELL_SIZE_CHUNKS * 16;
    // Kandidaten halten Abstand zum Zellrand, damit zwei Mittelpunkte nie zu dicht liegen (wie separation bei
    // Strukturen)
    public static final int CELL_MARGIN_CHUNKS = 4;
    public static final int MIN_TREE_OFFSET = 8;
    public static final int MAX_TREE_OFFSET = 20;

    private static final long SALT = 0x5EA2_6807_EL;

    /** Mittelpunkt des Hains (x, z) und Standort des alten Baums daneben (treeX, treeZ). */
    public record GroveCenter(int x, int z, int treeX, int treeZ) {}

    public static int cellOf(int blockCoord) {
        return Math.floorDiv(blockCoord, CELL_SIZE_BLOCKS);
    }

    public static GroveCenter centerInCell(long seed, int cellX, int cellZ) {
        Random random = new Random(seed ^ (cellX * 341_873_128_712L) ^ (cellZ * 132_897_987_541L) ^ SALT);
        int span = CELL_SIZE_CHUNKS - 2 * CELL_MARGIN_CHUNKS;
        int chunkX = cellX * CELL_SIZE_CHUNKS + CELL_MARGIN_CHUNKS + random.nextInt(span);
        int chunkZ = cellZ * CELL_SIZE_CHUNKS + CELL_MARGIN_CHUNKS + random.nextInt(span);
        int x = chunkX * 16 + 8;
        int z = chunkZ * 16 + 8;

        // Versatz so lange würfeln, bis er im Ring MIN..MAX liegt; gleicher Seed, gleiche Würfe
        int minSq = MIN_TREE_OFFSET * MIN_TREE_OFFSET;
        int maxSq = MAX_TREE_OFFSET * MAX_TREE_OFFSET;
        int dx;
        int dz;
        int distanceSq;
        do {
            dx = random.nextInt(2 * MAX_TREE_OFFSET + 1) - MAX_TREE_OFFSET;
            dz = random.nextInt(2 * MAX_TREE_OFFSET + 1) - MAX_TREE_OFFSET;
            distanceSq = dx * dx + dz * dz;
        } while (distanceSq < minSq || distanceSq > maxSq);

        return new GroveCenter(x, z, x + dx, z + dz);
    }

    /** Kandidaten der eigenen und der acht umliegenden Zellen; deckt jeden Zonen-Radius unter einer Zellgröße ab. */
    public static List<GroveCenter> candidatesNear(long seed, int blockX, int blockZ) {
        int cellX = cellOf(blockX);
        int cellZ = cellOf(blockZ);
        List<GroveCenter> candidates = new ArrayList<>(9);
        for (int ox = -1; ox <= 1; ox++) {
            for (int oz = -1; oz <= 1; oz++) {
                candidates.add(centerInCell(seed, cellX + ox, cellZ + oz));
            }
        }
        return candidates;
    }

    public static double distance(GroveCenter center, int blockX, int blockZ) {
        double dx = blockX - center.x();
        double dz = blockZ - center.z();
        return Math.sqrt(dx * dx + dz * dz);
    }
}
