package net.frank.mc3ver.grove;

import java.util.ArrayList;
import java.util.List;

/**
 * Form eines Birnenhains aus seinem Biom: Tiefe = Abstand zum Biomrand, Herz = tiefster Punkt der Umgebung.
 * So passen Zonen und alter Baum zu jedem Hain, egal wie groß oder krumm er ist.
 * Rasterpunkte liegen auf Chunk-Mitten, damit ein Chunk genau einen Tiefenwert hat.
 */
public class GroveShapeLogic {

    public static final int DEPTH_STEP = 8;
    public static final int MAX_DEPTH = 96;
    public static final int GRID = 16;
    // Nur tiefe Haine bekommen ein Herz: die Lichtung liegt weit im Wald (Spieltest 2026-11-01; mit 32 hatte
    // fast jeder Hain eines, der typische Hain mit Herz war nur gut 100 Blöcke breit)
    public static final int HEART_MIN_DEPTH = 64;
    // Kein tieferer Punkt in diesem Umkreis: so bekommt ein Hain meist genau ein Herz, auch wenn seine Mitte
    // breiter als MAX_DEPTH ist und überall gleich tief misst
    public static final int HEART_SEARCH_RADIUS = 128;
    // Alter Baum steht neben dem tiefsten Punkt, nicht genau darauf
    public static final int MIN_TREE_OFFSET = 6;
    public static final int MAX_TREE_OFFSET = 12;

    @FunctionalInterface
    public interface Probe {
        boolean isGrove(int x, int z);
    }

    @FunctionalInterface
    public interface DepthGrid {
        int depth(int gridX, int gridZ);
    }

    public record Heart(int gridX, int gridZ, int treeX, int treeZ) {}

    // Acht Richtungen, Länge 1000
    private static final int[][] DIRECTIONS = {
        {1000, 0}, {707, 707}, {0, 1000}, {-707, 707}, {-1000, 0}, {-707, -707}, {0, -1000}, {707, -707}
    };

    /** Abstand zum nächsten Nicht-Hain-Punkt in 8er-Schritten, höchstens MAX_DEPTH; außerhalb 0. */
    public static int depth(Probe probe, int x, int z) {
        if (!probe.isGrove(x, z)) {
            return 0;
        }
        int depth = MAX_DEPTH;
        for (int[] d : DIRECTIONS) {
            // Nur kürzere Abstände als der bisher kürzeste ändern das Ergebnis
            for (int dist = DEPTH_STEP; dist < depth; dist += DEPTH_STEP) {
                if (!probe.isGrove(x + d[0] * dist / 1000, z + d[1] * dist / 1000)) {
                    depth = dist;
                    break;
                }
            }
        }
        return depth;
    }

    public static int gridOf(int blockCoord) {
        return Math.floorDiv(blockCoord, GRID);
    }

    public static int gridCenter(int gridCoord) {
        return gridCoord * GRID + GRID / 2;
    }

    /** Herz: tief genug und kein Rasterpunkt im Suchradius ist tiefer; Gleichstand entscheidet ein fester Hash. */
    public static boolean isHeart(DepthGrid grid, long seed, int gridX, int gridZ) {
        int depth = grid.depth(gridX, gridZ);
        if (depth < HEART_MIN_DEPTH) {
            return false;
        }
        long rank = rank(seed, gridX, gridZ);
        int r = HEART_SEARCH_RADIUS / GRID;
        for (int ox = -r; ox <= r; ox++) {
            for (int oz = -r; oz <= r; oz++) {
                if ((ox == 0 && oz == 0) || ox * ox + oz * oz > r * r) {
                    continue;
                }
                int other = grid.depth(gridX + ox, gridZ + oz);
                if (other > depth || (other == depth && rank(seed, gridX + ox, gridZ + oz) > rank)) {
                    return false;
                }
            }
        }
        return true;
    }

    /** Herzen, deren alter Baum höchstens {@code radius} Blöcke von (x, z) entfernt stehen kann. */
    public static List<Heart> heartsNear(DepthGrid grid, long seed, int x, int z, int radius) {
        int reach = (radius + MAX_TREE_OFFSET) / GRID + 1;
        int gx = gridOf(x);
        int gz = gridOf(z);
        List<Heart> hearts = new ArrayList<>();
        for (int ox = -reach; ox <= reach; ox++) {
            for (int oz = -reach; oz <= reach; oz++) {
                if (isHeart(grid, seed, gx + ox, gz + oz)) {
                    Heart heart = heart(seed, gx + ox, gz + oz);
                    if (distance(heart.treeX() - x, heart.treeZ() - z) <= radius) {
                        hearts.add(heart);
                    }
                }
            }
        }
        return hearts;
    }

    /** Standort des alten Baums: fester Versatz vom Rasterpunkt, Richtung und Länge aus dem Hash. */
    public static Heart heart(long seed, int gridX, int gridZ) {
        long h = mix(seed ^ (gridX * 0x9E3779B97F4A7C15L) ^ (gridZ * 0xC2B2AE3D27D4EB4FL) ^ 0x7EA2L);
        double angle = (h & 0xFFFF) / 65536.0 * 2 * Math.PI;
        int offset = MIN_TREE_OFFSET + (int) ((h >>> 16) & 0xFFFF) % (MAX_TREE_OFFSET - MIN_TREE_OFFSET + 1);
        int cx = gridCenter(gridX);
        int cz = gridCenter(gridZ);
        return new Heart(
                gridX,
                gridZ,
                cx + (int) Math.round(Math.cos(angle) * offset),
                cz + (int) Math.round(Math.sin(angle) * offset));
    }

    public static double distance(int dx, int dz) {
        return Math.sqrt((double) dx * dx + (double) dz * dz);
    }

    private static long rank(long seed, int gridX, int gridZ) {
        return mix(seed ^ (gridX * 0xBF58476D1CE4E5B9L) ^ (gridZ * 0x94D049BB133111EBL));
    }

    private static long mix(long z) {
        z = (z ^ (z >>> 30)) * 0xBF58476D1CE4E5B9L;
        z = (z ^ (z >>> 27)) * 0x94D049BB133111EBL;
        return z ^ (z >>> 31);
    }
}
