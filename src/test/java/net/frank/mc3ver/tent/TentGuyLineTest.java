package net.frank.mc3ver.tent;

import static org.junit.jupiter.api.Assertions.*;

import java.util.Set;
import org.junit.jupiter.api.Test;

class TentGuyLineTest {

    private static final int AX = 100;
    private static final int AY = 64;
    private static final int AZ = 200;

    @Test
    void lowerSideCellsAndBothGablesCarryALine() {
        int lines = 0;
        for (int relY = 0; relY < ExplorerTentLogic.HEIGHT; relY++) {
            for (int relX = -1; relX <= 1; relX++) {
                for (int relZ = 0; relZ < ExplorerTentLogic.LENGTH; relZ++) {
                    if (ExplorerTentLogic.hasGuyLine(relX, relY, relZ)) lines++;
                }
            }
        }
        assertEquals(10, lines, "vier je Zeltseite, dazu je eine an beiden Giebeln");
        assertTrue(ExplorerTentLogic.hasGuyLine(-1, 0, 2), "Seitenwand");
        assertTrue(ExplorerTentLogic.hasGuyLine(0, 1, 0), "vorderer Giebel");
        assertTrue(ExplorerTentLogic.hasGuyLine(0, 1, ExplorerTentLogic.LENGTH - 1), "hinterer Giebel");
        assertFalse(ExplorerTentLogic.hasGuyLine(0, 0, 1), "Schlafsack in der Mitte");
        assertFalse(ExplorerTentLogic.hasGuyLine(1, 1, 1), "Dachschräge");
        assertFalse(ExplorerTentLogic.hasGuyLine(0, 1, 1), "First in der Mitte");
    }

    @Test
    void stakesStandOutsideTheTent() {
        assertEquals(new ExplorerTentLogic.BlockCoord(-2, 0, 2), ExplorerTentLogic.guyLineStake(-1, 0, 2));
        assertEquals(new ExplorerTentLogic.BlockCoord(2, 0, 1), ExplorerTentLogic.guyLineStake(1, 0, 1));
        // Ecken: schräg vor bzw. hinter das Zelt
        assertEquals(new ExplorerTentLogic.BlockCoord(-2, 0, -1), ExplorerTentLogic.guyLineStake(-1, 0, 0));
        assertEquals(
                new ExplorerTentLogic.BlockCoord(2, 0, ExplorerTentLogic.LENGTH),
                ExplorerTentLogic.guyLineStake(1, 0, ExplorerTentLogic.LENGTH - 1));
        // Giebel: auf Bodenhöhe vor dem Eingang bzw. hinter der Rückwand
        assertEquals(new ExplorerTentLogic.BlockCoord(0, 0, -1), ExplorerTentLogic.guyLineStake(0, 1, 0));
        assertEquals(
                new ExplorerTentLogic.BlockCoord(0, 0, ExplorerTentLogic.LENGTH),
                ExplorerTentLogic.guyLineStake(0, 1, ExplorerTentLogic.LENGTH - 1));
    }

    @Test
    void lineStaysOnFlatGround() {
        for (ExplorerTentLogic.Facing facing : ExplorerTentLogic.Facing.values()) {
            assertTrue(anchored(facing, -1, 0, 2, world(Set.of(), Set.of())), facing.name());
            assertTrue(anchored(facing, 0, 1, 0, world(Set.of(), Set.of())), facing.name());
        }
    }

    @Test
    void lineDisappearsWhenTheStakeWouldFloat() {
        // Neben der linken Zeltwand fällt das Gelände ab: kein Boden unter dem Hering
        ExplorerTentLogic.BlockCoord hole = worldPos(ExplorerTentLogic.Facing.NORTH, -2, -1, 2);
        ExplorerTentLogic.WorldView view = world(Set.of(hole), Set.of());
        assertFalse(anchored(ExplorerTentLogic.Facing.NORTH, -1, 0, 2, view), "Hering schwebte");
        assertTrue(anchored(ExplorerTentLogic.Facing.NORTH, 1, 0, 2, view), "rechte Seite steht auf Boden");
    }

    @Test
    void lineDisappearsWhenTheStakeWouldBeBuried() {
        // Ein Block steht genau dort, wo der Hering vor dem Eingang stecken soll
        ExplorerTentLogic.BlockCoord rock = worldPos(ExplorerTentLogic.Facing.EAST, 0, 0, -1);
        assertFalse(anchored(ExplorerTentLogic.Facing.EAST, 0, 1, 0, world(Set.of(), Set.of(rock))));
        assertTrue(anchored(
                ExplorerTentLogic.Facing.EAST, 0, 1, ExplorerTentLogic.LENGTH - 1, world(Set.of(), Set.of(rock))));
    }

    @Test
    void cellsWithoutLineAreNeverAnchored() {
        assertFalse(anchored(ExplorerTentLogic.Facing.NORTH, 0, 0, 1, world(Set.of(), Set.of())));
    }

    private static boolean anchored(
            ExplorerTentLogic.Facing facing, int relX, int relY, int relZ, ExplorerTentLogic.WorldView view) {
        return ExplorerTentLogic.isGuyLineAnchored(AX, AY, AZ, facing, relX, relY, relZ, view);
    }

    private static ExplorerTentLogic.BlockCoord worldPos(
            ExplorerTentLogic.Facing facing, int relX, int relY, int relZ) {
        return ExplorerTentLogic.toWorldPos(AX, AY, AZ, facing, relX, relY, relZ);
    }

    // Flaches Gelände bis y = 63; holes fehlen im Boden, blocks stehen darüber
    private static ExplorerTentLogic.WorldView world(
            Set<ExplorerTentLogic.BlockCoord> holes, Set<ExplorerTentLogic.BlockCoord> blocks) {
        return new ExplorerTentLogic.WorldView() {
            @Override
            public boolean isSolid(int x, int y, int z) {
                ExplorerTentLogic.BlockCoord c = new ExplorerTentLogic.BlockCoord(x, y, z);
                return (y < AY && !holes.contains(c)) || blocks.contains(c);
            }

            @Override
            public boolean isReplaceable(int x, int y, int z) {
                return !isSolid(x, y, z);
            }
        };
    }
}
