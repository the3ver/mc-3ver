package net.frank.mc3ver.grove;

import static org.junit.jupiter.api.Assertions.*;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import net.frank.mc3ver.grove.GroveCenterLogic.GroveCenter;
import org.junit.jupiter.api.Test;

class GroveCenterLogicTest {

    private static final long SEED = 12345L;

    @Test
    void sameSeedAndCellGiveSameCenter() {
        assertEquals(GroveCenterLogic.centerInCell(SEED, 3, -7), GroveCenterLogic.centerInCell(SEED, 3, -7));
    }

    @Test
    void differentSeedsMoveTheCenter() {
        Set<GroveCenter> centers = new HashSet<>();
        for (long seed = 0; seed < 20; seed++) {
            centers.add(GroveCenterLogic.centerInCell(seed, 0, 0));
        }
        assertTrue(centers.size() > 15, "Mittelpunkt muss vom Seed abhängen");
    }

    @Test
    void neighbouringCellsDoNotRepeatTheSameLocalPosition() {
        GroveCenter a = GroveCenterLogic.centerInCell(SEED, 0, 0);
        GroveCenter b = GroveCenterLogic.centerInCell(SEED, 1, 0);
        assertNotEquals(a.x(), b.x() - GroveCenterLogic.CELL_SIZE_BLOCKS);
    }

    @Test
    void neighbouringCellsAlongZDoNotRepeatTheSameLocalPosition() {
        GroveCenter a = GroveCenterLogic.centerInCell(SEED, 0, 0);
        GroveCenter b = GroveCenterLogic.centerInCell(SEED, 0, 1);
        assertNotEquals(a.z(), b.z() - GroveCenterLogic.CELL_SIZE_BLOCKS);
    }

    // Bestehende Welten: ändern sich die Würfe, passen neue Chunks nicht mehr zu schon generierten
    @Test
    void positionsStayStableAcrossVersions() {
        assertEquals(new GroveCenter(312, 200, 297, 202), GroveCenterLogic.centerInCell(SEED, 0, 0));
        assertEquals(new GroveCenter(-1000, 2968, -986, 2961), GroveCenterLogic.centerInCell(SEED, -3, 7));
    }

    @Test
    void centerStaysInsideItsCellWithMargin() {
        int margin = GroveCenterLogic.CELL_MARGIN_CHUNKS * 16;
        for (int cellX = -5; cellX <= 5; cellX++) {
            for (int cellZ = -5; cellZ <= 5; cellZ++) {
                GroveCenter c = GroveCenterLogic.centerInCell(SEED, cellX, cellZ);
                assertEquals(cellX, GroveCenterLogic.cellOf(c.x()));
                assertEquals(cellZ, GroveCenterLogic.cellOf(c.z()));
                int localX = Math.floorMod(c.x(), GroveCenterLogic.CELL_SIZE_BLOCKS);
                int localZ = Math.floorMod(c.z(), GroveCenterLogic.CELL_SIZE_BLOCKS);
                assertTrue(localX >= margin && localX < GroveCenterLogic.CELL_SIZE_BLOCKS - margin, "x " + localX);
                assertTrue(localZ >= margin && localZ < GroveCenterLogic.CELL_SIZE_BLOCKS - margin, "z " + localZ);
            }
        }
    }

    @Test
    void centerSitsInTheMiddleOfAChunk() {
        GroveCenter c = GroveCenterLogic.centerInCell(SEED, -2, 4);
        assertEquals(8, Math.floorMod(c.x(), 16));
        assertEquals(8, Math.floorMod(c.z(), 16));
    }

    @Test
    void oldTreeIsOffsetFromCenterWithinRing() {
        for (long seed = 0; seed < 200; seed++) {
            GroveCenter c = GroveCenterLogic.centerInCell(seed, 0, 0);
            double offset = GroveCenterLogic.distance(c, c.treeX(), c.treeZ());
            assertTrue(offset >= GroveCenterLogic.MIN_TREE_OFFSET, "Versatz " + offset);
            assertTrue(offset <= GroveCenterLogic.MAX_TREE_OFFSET, "Versatz " + offset);
        }
    }

    @Test
    void cellOfRoundsTowardsNegativeInfinity() {
        assertEquals(0, GroveCenterLogic.cellOf(0));
        assertEquals(0, GroveCenterLogic.cellOf(GroveCenterLogic.CELL_SIZE_BLOCKS - 1));
        assertEquals(1, GroveCenterLogic.cellOf(GroveCenterLogic.CELL_SIZE_BLOCKS));
        assertEquals(-1, GroveCenterLogic.cellOf(-1));
        assertEquals(-1, GroveCenterLogic.cellOf(-GroveCenterLogic.CELL_SIZE_BLOCKS));
        assertEquals(-2, GroveCenterLogic.cellOf(-GroveCenterLogic.CELL_SIZE_BLOCKS - 1));
    }

    @Test
    void candidatesNearCoverOwnAndEightNeighbourCells() {
        int x = 5 * GroveCenterLogic.CELL_SIZE_BLOCKS + 10;
        int z = -3 * GroveCenterLogic.CELL_SIZE_BLOCKS + 10;
        List<GroveCenter> candidates = GroveCenterLogic.candidatesNear(SEED, x, z);

        assertEquals(9, candidates.size());
        Set<String> cells = new HashSet<>();
        for (GroveCenter c : candidates) {
            cells.add(GroveCenterLogic.cellOf(c.x()) + "/" + GroveCenterLogic.cellOf(c.z()));
        }
        for (int cx = 4; cx <= 6; cx++) {
            for (int cz = -4; cz <= -2; cz++) {
                assertTrue(cells.contains(cx + "/" + cz), "Zelle " + cx + "/" + cz + " fehlt");
            }
        }
        assertTrue(candidates.contains(GroveCenterLogic.centerInCell(SEED, 5, -3)));
    }

    @Test
    void distanceIsEuclidean() {
        GroveCenter c = new GroveCenter(0, 0, 0, 0);
        assertEquals(5.0, GroveCenterLogic.distance(c, 3, 4), 1e-9);
        assertEquals(5.0, GroveCenterLogic.distance(c, -3, -4), 1e-9);
    }
}
