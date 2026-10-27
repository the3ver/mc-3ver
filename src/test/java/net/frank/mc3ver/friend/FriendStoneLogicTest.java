package net.frank.mc3ver.friend;

import org.junit.jupiter.api.Test;

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

public class FriendStoneLogicTest {

    private static final UUID FRANK = UUID.fromString("00000000-0000-0000-0000-00000000000f");
    private static final UUID ANNA = UUID.fromString("00000000-0000-0000-0000-00000000000a");
    private static final FriendStoneLogic.FriendLink FRANKS_STONE = new FriendStoneLogic.FriendLink(FRANK, "Frank", 2);

    @Test
    void friendWithValidStoneMayTeleport() {
        assertEquals(FriendStoneLogic.TeleportCheck.SUCCESS,
            FriendStoneLogic.checkTeleport(FRANKS_STONE, ANNA, true, 2));
    }

    @Test
    void unboundStoneLeadsNowhere() {
        assertEquals(FriendStoneLogic.TeleportCheck.UNBOUND,
            FriendStoneLogic.checkTeleport(null, ANNA, true, 0));
    }

    @Test
    void ownStoneLeadsNowhere() {
        assertEquals(FriendStoneLogic.TeleportCheck.OWN_STONE,
            FriendStoneLogic.checkTeleport(FRANKS_STONE, FRANK, true, 2));
    }

    @Test
    void offlineFriendCannotBeReached() {
        assertEquals(FriendStoneLogic.TeleportCheck.OFFLINE,
            FriendStoneLogic.checkTeleport(FRANKS_STONE, ANNA, false, 0));
    }

    @Test
    void resetInvalidatesStonesFromEarlierGenerations() {
        assertEquals(FriendStoneLogic.TeleportCheck.REVOKED,
            FriendStoneLogic.checkTeleport(FRANKS_STONE, ANNA, true, 3));
    }

    @Test
    void arrivalPrefersTheDirectNeighbors() {
        // Alles frei mit Boden: das erste Feld ist direkt östlich des Freundes
        int[] spot = FriendStoneLogic.findArrivalSpot(10, 64, 20, view(Set.of(), 63));
        assertArrayEquals(new int[]{11, 64, 20}, spot);
    }

    @Test
    void arrivalSkipsBlockedFields() {
        // Osten und Westen zugebaut: dann Süden (+Z)
        int[] spot = FriendStoneLogic.findArrivalSpot(10, 64, 20, view(Set.of("11,64,20", "9,64,20"), 63));
        assertArrayEquals(new int[]{10, 64, 21}, spot);
    }

    @Test
    void arrivalStepsUpWhenTheFriendStandsInAHole() {
        // Auf Höhe 64 ist rundherum Wand, eine Stufe höher ist frei mit Boden auf 64
        Set<String> walls = new HashSet<>();
        for (int[] c : FriendStoneLogic.arrivalCandidates(10, 64, 20)) {
            if (c[1] == 64) walls.add(c[0] + "," + c[1] + "," + c[2]);
        }
        FriendStoneLogic.SpotView v = new FriendStoneLogic.SpotView() {
            @Override
            public boolean isFree(int x, int y, int z) {
                return !walls.contains(x + "," + y + "," + z);
            }

            @Override
            public boolean isFloor(int x, int y, int z) {
                return y == 64 && walls.contains(x + "," + y + "," + z);
            }
        };
        int[] spot = FriendStoneLogic.findArrivalSpot(10, 64, 20, v);
        assertEquals(65, spot[1]);
    }

    @Test
    void noFreeFieldMeansLandingOnTheFriend() {
        FriendStoneLogic.SpotView blocked = new FriendStoneLogic.SpotView() {
            @Override
            public boolean isFree(int x, int y, int z) {
                return false;
            }

            @Override
            public boolean isFloor(int x, int y, int z) {
                return true;
            }
        };
        assertArrayEquals(new int[]{10, 64, 20}, FriendStoneLogic.findArrivalSpot(10, 64, 20, blocked));
    }

    @Test
    void compassFollowsMinecraftAxes() {
        assertEquals(FriendStoneLogic.Compass.NORTH, FriendStoneLogic.compass(0, -10));
        assertEquals(FriendStoneLogic.Compass.EAST, FriendStoneLogic.compass(10, 0));
        assertEquals(FriendStoneLogic.Compass.SOUTH, FriendStoneLogic.compass(0, 10));
        assertEquals(FriendStoneLogic.Compass.WEST, FriendStoneLogic.compass(-10, 0));
        assertEquals(FriendStoneLogic.Compass.NORTH_EAST, FriendStoneLogic.compass(10, -10));
        assertEquals(FriendStoneLogic.Compass.SOUTH_WEST, FriendStoneLogic.compass(-7, 7));
    }

    @Test
    void bearingGivesRoundedDistanceInTheSameDimension() {
        FriendStoneLogic.Bearing b = FriendStoneLogic.bearing(0, 0, "minecraft:overworld", 300, -160, "minecraft:overworld")
            .orElseThrow();
        assertEquals(340, b.distance());
        // 300 Ost, 160 Nord: 62 Grad liegt näher an Nordost (45) als an Ost (90)
        assertEquals(FriendStoneLogic.Compass.NORTH_EAST, b.direction());
    }

    @Test
    void noBearingAcrossDimensions() {
        assertTrue(FriendStoneLogic.bearing(0, 0, "minecraft:overworld", 5, 5, "minecraft:the_nether").isEmpty());
    }

    @Test
    void distanceDisplayUpdatesTwiceASecond() {
        assertTrue(FriendStoneLogic.shouldUpdateDistance(20));
        assertFalse(FriendStoneLogic.shouldUpdateDistance(21));
    }

    private static FriendStoneLogic.SpotView view(Set<String> blocked, int floorY) {
        return new FriendStoneLogic.SpotView() {
            @Override
            public boolean isFree(int x, int y, int z) {
                return !blocked.contains(x + "," + y + "," + z);
            }

            @Override
            public boolean isFloor(int x, int y, int z) {
                return y == floorY;
            }
        };
    }
}
