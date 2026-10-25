package net.frank.mc3ver.bonfire;

import org.junit.jupiter.api.Test;

import java.util.HashSet;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

public class BonfireLogicTest {

    private static BonfireLogic.WorldView flatWorld(Set<String> obstacles, Set<String> holes) {
        return new BonfireLogic.WorldView() {
            @Override
            public boolean isSolid(int x, int y, int z) {
                return y == 63 && !holes.contains(x + "," + z);
            }

            @Override
            public boolean isReplaceable(int x, int y, int z) {
                return !obstacles.contains(x + "," + y + "," + z);
            }
        };
    }

    @Test
    void partsCoverTheFull3x3Grid() {
        Set<String> offsets = new HashSet<>();
        for (BonfireLogic.Part part : BonfireLogic.Part.values()) {
            assertTrue(Math.abs(part.dx()) <= 1 && Math.abs(part.dz()) <= 1);
            offsets.add(part.dx() + "," + part.dz());
        }
        assertEquals(BonfireLogic.SIZE * BonfireLogic.SIZE, offsets.size());
    }

    @Test
    void fromOffsetFindsPartsInsideAndRejectsOutside() {
        assertEquals(BonfireLogic.Part.CENTER, BonfireLogic.Part.fromOffset(0, 0).orElseThrow());
        assertEquals(BonfireLogic.Part.NORTH_EAST, BonfireLogic.Part.fromOffset(1, -1).orElseThrow());
        assertTrue(BonfireLogic.Part.fromOffset(2, 0).isEmpty());
    }

    @Test
    void neighborsOnlyExistTowardsTheCenter() {
        assertTrue(BonfireLogic.Part.NORTH_WEST.hasNeighbor(1, 0));
        assertTrue(BonfireLogic.Part.NORTH_WEST.hasNeighbor(0, 1));
        assertFalse(BonfireLogic.Part.NORTH_WEST.hasNeighbor(-1, 0));
        assertFalse(BonfireLogic.Part.NORTH_WEST.hasNeighbor(0, -1));
        // Mitte hat in alle vier Richtungen Nachbarn
        assertTrue(BonfireLogic.Part.CENTER.hasNeighbor(1, 0));
        assertTrue(BonfireLogic.Part.CENTER.hasNeighbor(-1, 0));
        assertTrue(BonfireLogic.Part.CENTER.hasNeighbor(0, 1));
        assertTrue(BonfireLogic.Part.CENTER.hasNeighbor(0, -1));
    }

    @Test
    void serializedNamesAreLowercase() {
        assertEquals("south_west", BonfireLogic.Part.SOUTH_WEST.getSerializedName());
    }

    @Test
    void placementOnFlatFreeGroundIsOk() {
        assertEquals(BonfireLogic.PlacementStatus.OK,
            BonfireLogic.validatePlacement(0, 64, 0, flatWorld(Set.of(), Set.of())));
    }

    @Test
    void blockInTheCornerObstructsPlacement() {
        assertEquals(BonfireLogic.PlacementStatus.OBSTRUCTED,
            BonfireLogic.validatePlacement(0, 64, 0, flatWorld(Set.of("1,64,1"), Set.of())));
    }

    @Test
    void holeInTheGroundIsUneven() {
        assertEquals(BonfireLogic.PlacementStatus.UNEVEN_GROUND,
            BonfireLogic.validatePlacement(0, 64, 0, flatWorld(Set.of(), Set.of("-1,0"))));
    }

    @Test
    void obstructionIsReportedBeforeUnevenGround() {
        assertEquals(BonfireLogic.PlacementStatus.OBSTRUCTED,
            BonfireLogic.validatePlacement(0, 64, 0, flatWorld(Set.of("0,64,0"), Set.of("1,1"))));
    }

    @Test
    void litBonfireGivesFullLight() {
        assertEquals(15, BonfireLogic.lightLevel(true));
        assertEquals(0, BonfireLogic.lightLevel(false));
    }

    @Test
    void smokeColumnGrowsWithDistance() {
        assertEquals(1.0f, BonfireLogic.smokeRadiusScale(10, false));
        assertEquals(1.0f, BonfireLogic.smokeRadiusScale(96, false));
        assertEquals(2.0f, BonfireLogic.smokeRadiusScale(192, false), 1e-6);
        // Mit Fernrohr bleibt die echte Größe
        assertEquals(1.0f, BonfireLogic.smokeRadiusScale(192, true));
    }

    @Test
    void smokeColumnReachesFarAboveTheFire() {
        assertTrue(BonfireLogic.SMOKE_COLUMN_HEIGHT >= 48);
    }
}
