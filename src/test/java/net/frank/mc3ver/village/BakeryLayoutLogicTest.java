package net.frank.mc3ver.village;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.ArrayDeque;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import org.junit.jupiter.api.Test;

class BakeryLayoutLogicTest {

    private final List<char[][]> layers = BakeryLayoutLogic.layers();

    @Test
    void everyLayerHasTheFullFootprint() {
        assertEquals(BakeryLayoutLogic.CHIMNEY_TOP + 1, layers.size());
        for (char[][] layer : layers) {
            assertEquals(BakeryLayoutLogic.SIZE_Z, layer.length);
            for (char[] row : layer) assertEquals(BakeryLayoutLogic.SIZE_X, row.length);
        }
    }

    @Test
    void hasOneOvenChestDoorAndBothJigsaws() {
        assertEquals(1, count('O'));
        assertEquals(1, count('C'));
        assertEquals(1, count('D'));
        assertEquals(1, count('d'));
        assertEquals(1, count('J'));
        assertEquals(1, count('V'));
    }

    @Test
    void entranceJigsawSitsAtTheStreetInFrontOfTheDoor() {
        int[] jigsaw = find('J');
        int[] door = find('D');
        assertEquals(0, jigsaw[0]);
        assertEquals(0, jigsaw[1]);
        assertEquals(1, door[0]);
        assertEquals(jigsaw[1] + 1, door[1]);
        assertEquals(jigsaw[2], door[2]);
        assertEquals('d', layers.get(door[1] + 1)[door[2]][door[0]]);
    }

    @Test
    void villagerCanWalkFromTheDoorToTheOven() {
        int[] door = find('D');
        int[] oven = find('O');
        Set<Long> seen = new HashSet<>();
        ArrayDeque<int[]> open = new ArrayDeque<>();
        open.add(new int[] {door[0], door[2]});
        boolean reached = false;
        while (!open.isEmpty()) {
            int[] at = open.poll();
            if (Math.abs(at[0] - oven[0]) + Math.abs(at[1] - oven[2]) == 1) reached = true;
            for (int[] step : new int[][] {{1, 0}, {-1, 0}, {0, 1}, {0, -1}}) {
                int x = at[0] + step[0], z = at[1] + step[1];
                if (walkable(x, z, door[1]) && seen.add((long) x << 32 | z)) open.add(new int[] {x, z});
            }
        }
        assertTrue(reached, "Kein Weg von der Tür zum Ofen");
    }

    @Test
    void roofCoversEveryWallColumn() {
        for (int z = 0; z < BakeryLayoutLogic.SIZE_Z; z++) {
            for (int x = 0; x < BakeryLayoutLogic.SIZE_X; x++) {
                char wall = layers.get(BakeryLayoutLogic.WALL_TOP)[z][x];
                if (wall != 'W' && wall != 'L') continue;
                char roof = layers.get(BakeryLayoutLogic.WALL_TOP + 1)[z][x];
                assertTrue(roof == 'P' || roof == 'K', "Loch im Dach über " + x + "," + z + ": " + roof);
            }
        }
    }

    @Test
    void chimneyStandsBehindTheOvenAndRisesAboveTheRoof() {
        int[] oven = find('O');
        assertEquals(BakeryLayoutLogic.CHIMNEY_X, oven[0] + 1);
        assertEquals(BakeryLayoutLogic.CHIMNEY_Z, oven[2]);
        for (int y = 1; y <= BakeryLayoutLogic.CHIMNEY_TOP; y++) {
            assertEquals('K', layers.get(y)[BakeryLayoutLogic.CHIMNEY_Z][BakeryLayoutLogic.CHIMNEY_X], "y " + y);
        }
        assertEquals(' ', BakeryLayoutLogic.roof(3)[BakeryLayoutLogic.CHIMNEY_Z][BakeryLayoutLogic.CHIMNEY_X]);
    }

    @Test
    void roofRingsStepInwardWithCornersAndClosedTop() {
        char[][] eaves = BakeryLayoutLogic.roof(0);
        assertEquals('1', eaves[0][0]);
        assertEquals('2', eaves[0][8]);
        assertEquals('3', eaves[10][0]);
        assertEquals('4', eaves[10][8]);
        assertEquals('>', eaves[5][0]);
        assertEquals('<', eaves[5][8]);
        assertEquals('v', eaves[0][4]);
        assertEquals('^', eaves[10][4]);
        assertEquals('P', eaves[1][1]);
        assertEquals('.', eaves[5][4]);
        char[][] top = BakeryLayoutLogic.roof(3);
        assertEquals(' ', top[2][2]);
        assertEquals('1', top[3][3]);
        assertEquals('P', top[5][4]);
        assertEquals('P', top[4][4]);
        assertEquals('2', top[3][5]);
        assertEquals('4', top[7][5]);
        assertEquals('^', top[7][4]);
        assertEquals(' ', top[8][4]);
    }

    @Test
    void namesPoolsAndTemplatesPerVillageStyle() {
        assertEquals(List.of("plains", "desert", "savanna", "snowy", "taiga"), BakeryLayoutLogic.STYLES);
        assertEquals("minecraft:village/desert/houses", BakeryLayoutLogic.poolId("desert"));
        assertEquals("mc3ver:village/snowy/houses/snowy_bakery", BakeryLayoutLogic.templateId("snowy"));
        assertTrue(BakeryLayoutLogic.mossy("plains"));
        assertTrue(BakeryLayoutLogic.mossy("taiga"));
        assertFalse(BakeryLayoutLogic.mossy("desert"));
        assertFalse(BakeryLayoutLogic.mossy("savanna"));
        assertFalse(BakeryLayoutLogic.mossy("snowy"));
        assertEquals(2, BakeryLayoutLogic.POOL_WEIGHT);
    }

    private boolean walkable(int x, int z, int floorY) {
        if (x < 0 || z < 0 || x >= BakeryLayoutLogic.SIZE_X || z >= BakeryLayoutLogic.SIZE_Z) return false;
        char feet = layers.get(floorY)[z][x];
        char head = layers.get(floorY + 1)[z][x];
        return (feet == '.' || feet == 'D') && (head == '.' || head == 'd') && x > 0;
    }

    private int count(char c) {
        int n = 0;
        for (char[][] layer : layers) {
            for (char[] row : layer) {
                for (char cell : row) if (cell == c) n++;
            }
        }
        return n;
    }

    /** Position {x, y, z} des ersten Vorkommens. */
    private int[] find(char c) {
        for (int y = 0; y < layers.size(); y++) {
            for (int z = 0; z < BakeryLayoutLogic.SIZE_Z; z++) {
                for (int x = 0; x < BakeryLayoutLogic.SIZE_X; x++) {
                    if (layers.get(y)[z][x] == c) return new int[] {x, y, z};
                }
            }
        }
        throw new AssertionError("Zeichen fehlt: " + c);
    }
}
