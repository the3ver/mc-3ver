package net.frank.mc3ver.grove;

import static net.frank.mc3ver.grove.GroveShapeLogic.*;
import static org.junit.jupiter.api.Assertions.*;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import org.junit.jupiter.api.Test;

class GroveShapeLogicTest {

    private static final long SEED = 12345L;
    // Beide Koordinaten werden gerundet: höchstens ein halber Block je Achse, also √0,5
    private static final double ROUNDING = Math.sqrt(0.5);

    // Kreisförmiger Hain um (0, 0) mit Radius r
    private static Probe disc(int r) {
        return (x, z) -> x * x + z * z <= r * r;
    }

    // Tiefe auf dem Raster, direkt aus der Probe gemessen
    private static DepthGrid gridOf(Probe probe) {
        return (gx, gz) -> depth(probe, gridCenter(gx), gridCenter(gz));
    }

    @Test
    void depthIsZeroOutsideTheGrove() {
        assertEquals(0, depth(disc(50), 100, 0));
    }

    @Test
    void depthIsTheDistanceToTheEdgeInSteps() {
        // Rand bei x = 50: von x = 30 aus ist x = 54 (Schritt 24) der erste Punkt draußen
        assertEquals(24, depth(disc(50), 30, 0));
        assertEquals(8, depth(disc(50), 45, 0));
        assertEquals(48, depth(disc(50), 5, 0));
    }

    @Test
    void depthLooksInEveryDirection() {
        // Halbebene: nur z < 20 ist Hain; die Kante liegt in z-Richtung
        Probe band = (x, z) -> z < 20;
        assertEquals(16, depth(band, 0, 4));
        // Schräg: Kante x + z < 40 trifft der Diagonalstrahl zuerst
        Probe diagonal = (x, z) -> x + z < 40;
        assertTrue(depth(diagonal, 0, 0) <= 32, "Diagonale zählt");
    }

    @Test
    void depthIsCappedForHugeGroves() {
        assertEquals(MAX_DEPTH, depth((x, z) -> true, 0, 0));
    }

    @Test
    void gridPointsAreChunkCenters() {
        assertEquals(8, gridCenter(0));
        assertEquals(-8, gridCenter(-1));
        assertEquals(0, GroveShapeLogic.gridOf(15));
        assertEquals(-1, GroveShapeLogic.gridOf(-1));
        assertEquals(1, GroveShapeLogic.gridOf(16));
    }

    @Test
    void aRoundGroveHasExactlyOneHeartNearItsMiddle() {
        DepthGrid grid = gridOf(disc(80));
        List<int[]> hearts = heartsIn(grid, -8, 8);
        assertEquals(1, hearts.size(), "genau ein Herz");
        int[] h = hearts.get(0);
        assertTrue(distance(gridCenter(h[0]), gridCenter(h[1])) <= 24, "Herz nahe der Mitte");
    }

    @Test
    void aSmallGroveHasNoHeart() {
        assertTrue(heartsIn(gridOf(disc(24)), -4, 4).isEmpty(), "zu flach für den alten Baum");
    }

    @Test
    void twoFarApartGrovesGetOneHeartEach() {
        Probe two = (x, z) -> (x * x + z * z <= 80 * 80) || ((x - 400) * (x - 400) + z * z <= 80 * 80);
        assertEquals(2, heartsIn(gridOf(two), -8, 34).size());
    }

    @Test
    void heartNeedsEnoughDepth() {
        assertFalse(isHeart((gx, gz) -> HEART_MIN_DEPTH - 8, SEED, 0, 0));
        assertTrue(isHeart((gx, gz) -> gx == 0 && gz == 0 ? HEART_MIN_DEPTH : 0, SEED, 0, 0));
    }

    @Test
    void aDeeperNeighbourInsideTheSearchRadiusWins() {
        int r = HEART_SEARCH_RADIUS / GRID;
        // Tiefer Nachbar genau auf dem Suchradius, in jeder Achsrichtung; sonst ist nur der Kandidat tief
        for (int[] at : new int[][] {{r, 0}, {-r, 0}, {0, r}, {0, -r}}) {
            assertFalse(isHeart(onlyCandidateAnd(at[0], at[1]), SEED, 0, 0), "Nachbar " + at[0] + "," + at[1]);
        }
        // Knapp außerhalb des Suchradius zählt der tiefere Punkt nicht
        for (int[] at : new int[][] {{r + 1, 0}, {-r - 1, 0}, {0, r + 1}, {0, -r - 1}, {r - 1, r - 1}}) {
            assertTrue(isHeart(onlyCandidateAnd(at[0], at[1]), SEED, 0, 0), "Nachbar " + at[0] + "," + at[1]);
        }
    }

    @Test
    void aFlatPlateauGetsExactlyOneHeart() {
        DepthGrid plateau = (gx, gz) -> Math.abs(gx) <= 2 && Math.abs(gz) <= 2 ? 40 : 0;
        assertEquals(1, heartsIn(plateau, -3, 3).size());
    }

    @Test
    void heartsNearReachesFarTreesForALargeRadius() {
        DepthGrid grid = gridOf(disc(80));
        Heart heart = heartsNear(grid, SEED, 0, 0, 40).get(0);
        assertEquals(
                1,
                heartsNear(grid, SEED, heart.treeX() + 90, heart.treeZ() - 30, 100)
                        .size());
        assertEquals(
                1,
                heartsNear(grid, SEED, heart.treeX() - 90, heart.treeZ() + 30, 100)
                        .size());
    }

    // Abfragepunkt auf der vom Rasterpunkt abgewandten Seite des Baums: Rasterpunkt liegt weiter weg als der Radius
    @Test
    void heartsNearFindsATreeAtTheRadiusBehindIt() {
        DepthGrid grid = gridOf(disc(80));
        Heart heart = heartsNear(grid, SEED, 0, 0, 40).get(0);
        int dx = heart.treeX() - gridCenter(heart.gridX());
        int dz = heart.treeZ() - gridCenter(heart.gridZ());
        double len = distance(dx, dz);
        int qx = heart.treeX() + (int) (dx / len * 19.5);
        int qz = heart.treeZ() + (int) (dz / len * 19.5);
        assertEquals(1, heartsNear(grid, SEED, qx, qz, 20).size());
    }

    // Kandidat (0, 0) mit Tiefe 40, ein tieferer Punkt (64) an (ax, az), alles andere flach
    private static DepthGrid onlyCandidateAnd(int ax, int az) {
        return (gx, gz) -> gx == ax && gz == az ? 64 : (gx == 0 && gz == 0 ? 40 : 0);
    }

    @Test
    void oldTreeStandsBesideTheDeepestPoint() {
        Set<String> spots = new HashSet<>();
        for (int g = -20; g < 20; g++) {
            Heart heart = heart(SEED, g, -g);
            double offset = distance(heart.treeX() - gridCenter(g), heart.treeZ() - gridCenter(-g));
            assertTrue(
                    offset >= MIN_TREE_OFFSET - ROUNDING && offset <= MAX_TREE_OFFSET + ROUNDING, "Versatz " + offset);
            spots.add((heart.treeX() - gridCenter(g)) + "/" + (heart.treeZ() - gridCenter(-g)));
        }
        assertTrue(spots.size() > 20, "Versatz variiert");
        assertEquals(heart(SEED, 3, 4), heart(SEED, 3, 4));
    }

    @Test
    void heartsNearFindsTheTreeWithinTheRadiusOnly() {
        DepthGrid grid = gridOf(disc(80));
        Heart heart = heartsNear(grid, SEED, 0, 0, 40).get(0);
        assertEquals(1, heartsNear(grid, SEED, heart.treeX(), heart.treeZ(), 0).size());
        assertEquals(
                1, heartsNear(grid, SEED, heart.treeX() + 19, heart.treeZ(), 20).size());
        assertTrue(heartsNear(grid, SEED, heart.treeX() + 25, heart.treeZ(), 20).isEmpty());
    }

    private static List<int[]> heartsIn(DepthGrid grid, int from, int to) {
        List<int[]> hearts = new java.util.ArrayList<>();
        for (int gx = from; gx <= to; gx++) {
            for (int gz = from; gz <= to; gz++) {
                if (isHeart(grid, SEED, gx, gz)) {
                    hearts.add(new int[] {gx, gz});
                }
            }
        }
        return hearts;
    }
}
