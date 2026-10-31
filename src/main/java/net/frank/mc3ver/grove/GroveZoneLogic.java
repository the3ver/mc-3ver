package net.frank.mc3ver.grove;

/**
 * Zonen des Birnenhains: außen einzelne Bäume, dann Gruppen, dann dichter Wald, um den alten Baum frei.
 * Die Zone hängt an der Tiefe im Hain (Abstand zum Biomrand), so passt sie zu jeder Hain-Form.
 * Schwellen sind Startwerte und werden im Spiel abgestimmt.
 */
public class GroveZoneLogic {

    public static final int HEART_RADIUS = 20;
    // Breite des dichten Waldes um die Lichtung des alten Baums
    public static final int FOREST_RING = 24;
    public static final int GROUPS_DEPTH = 16;
    public static final int FOREST_DEPTH = 32;

    public enum Zone {
        EDGE,
        GROUPS,
        FOREST,
        HEART
    }

    /**
     * Bewuchs einer Zone: Baum-Cluster pro Chunk (Nachkommastelle = Chance auf einen weiteren),
     * Bäume pro Cluster und Streuradius um den Cluster-Punkt, Chance auf einen Bodenfleck pro Chunk,
     * Laubbüsche, Farn- und Blumenflecken pro Chunk, Chance auf einen Rest Gartenmauer pro Chunk.
     */
    public record Density(
            float clustersPerChunk,
            int minClusterSize,
            int maxClusterSize,
            int clusterRadius,
            float groundPatchChance,
            float bushesPerChunk,
            float fernPatchesPerChunk,
            float flowerPatchesPerChunk,
            float wallChance) {}

    // Büsche vor allem zwischen den Gruppen, Farne nur im dichten Wald, Blumen am meisten auf der offenen Wiese
    private static final Density EDGE_DENSITY = new Density(0.5f, 1, 1, 0, 0f, 0.3f, 0f, 1.5f, 0f);
    private static final Density GROUPS_DENSITY = new Density(0.5f, 3, 5, 4, 0f, 1f, 0f, 1f, 0f);
    private static final Density FOREST_DENSITY = new Density(9f, 1, 1, 0, 0.3f, 0.5f, 1f, 0.3f, 0f);
    private static final Density HEART_DENSITY = new Density(0f, 0, 0, 0, 0f, 0f, 0f, 1f, 0f);

    /**
     * Zone aus der Tiefe im Hain (Abstand zum Biomrand) und dem Abstand zum alten Baum. Das Herz hat Vorrang,
     * um die Lichtung liegt immer ein Ring dichten Waldes, auch wo der Hain dort schon flacher wird.
     */
    public static Zone zoneAt(int depth, double distanceToTree) {
        if (distanceToTree < HEART_RADIUS) {
            return Zone.HEART;
        }
        if (distanceToTree < HEART_RADIUS + FOREST_RING || depth >= FOREST_DEPTH) {
            return Zone.FOREST;
        }
        if (depth >= GROUPS_DEPTH) {
            return Zone.GROUPS;
        }
        return Zone.EDGE;
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
