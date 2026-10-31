package net.frank.mc3ver.grove;

import java.util.function.Predicate;
import net.frank.mc3ver.grove.GroveCenterLogic.GroveCenter;

/**
 * Zonen des Birnenhains: außen einzelne Bäume, dann Gruppen, dann dichter Wald, um den alten Baum frei.
 * Radien sind Startwerte aus der Spec und werden im Spiel abgestimmt.
 */
public class GroveZoneLogic {

    public static final int HEART_RADIUS = 20;
    public static final int FOREST_RADIUS = 56;
    public static final int GROUPS_RADIUS = 96;

    public enum Zone {
        EDGE,
        GROUPS,
        FOREST,
        HEART
    }

    /**
     * Baumverteilung einer Zone: Cluster pro Chunk (Nachkommastelle = Chance auf einen weiteren),
     * Bäume pro Cluster und Streuradius um den Cluster-Punkt, Chance auf einen Bodenfleck pro Chunk.
     */
    public record Density(
            float clustersPerChunk,
            int minClusterSize,
            int maxClusterSize,
            int clusterRadius,
            float groundPatchChance) {}

    private static final Density EDGE_DENSITY = new Density(0.5f, 1, 1, 0, 0f);
    private static final Density GROUPS_DENSITY = new Density(0.5f, 3, 5, 4, 0f);
    private static final Density FOREST_DENSITY = new Density(9f, 1, 1, 0, 0.3f);
    private static final Density HEART_DENSITY = new Density(0f, 0, 0, 0, 0f);

    /** Zone aus dem Abstand zum Hain-Mittelpunkt und zum alten Baum; der Herz-Bereich hat Vorrang. */
    public static Zone zoneAt(double distanceToCenter, double distanceToTree) {
        if (distanceToTree < HEART_RADIUS) {
            return Zone.HEART;
        }
        if (distanceToCenter < FOREST_RADIUS) {
            return Zone.FOREST;
        }
        if (distanceToCenter < GROUPS_RADIUS) {
            return Zone.GROUPS;
        }
        return Zone.EDGE;
    }

    /**
     * Zone an einer Position; zählt nur Mittelpunkte, die {@code isInGrove} akzeptiert (Biom dort ist Birnenhain).
     * Ohne gültigen Mittelpunkt in Reichweite gilt der Rand.
     */
    public static Zone zoneFor(long seed, int blockX, int blockZ, Predicate<GroveCenter> isInGrove) {
        GroveCenter nearest = null;
        double nearestDistance = Double.POSITIVE_INFINITY;
        for (GroveCenter candidate : GroveCenterLogic.candidatesNear(seed, blockX, blockZ)) {
            double distance = GroveCenterLogic.distance(candidate, blockX, blockZ);
            if (distance < nearestDistance && isInGrove.test(candidate)) {
                nearest = candidate;
                nearestDistance = distance;
            }
        }
        if (nearest == null) {
            return Zone.EDGE;
        }
        double dx = blockX - nearest.treeX();
        double dz = blockZ - nearest.treeZ();
        return zoneAt(nearestDistance, Math.sqrt(dx * dx + dz * dz));
    }

    public static Density densityOf(Zone zone) {
        return switch (zone) {
            case EDGE -> EDGE_DENSITY;
            case GROUPS -> GROUPS_DENSITY;
            case FOREST -> FOREST_DENSITY;
            case HEART -> HEART_DENSITY;
        };
    }

    /** Ganzzahliger Teil immer, Nachkommastelle als Chance; {@code roll} ist ein Zufallswert aus [0, 1). */
    public static int countFor(float perChunk, float roll) {
        int whole = (int) perChunk;
        return roll < perChunk - whole ? whole + 1 : whole;
    }
}
