package net.frank.mc3ver.tent;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;

class TentOutlineTest {

    @Test
    void canvasHidesItsOutlineExceptAtTheCraftingTable() {
        // Die Kollisionskästen der Plane würden als Gitter über den schrägen Dachflächen erscheinen
        for (TentCanvasBlock.CanvasPart part : TentCanvasBlock.CanvasPart.values()) {
            assertEquals(
                    part == TentCanvasBlock.CanvasPart.WALL_CRAFTING_TABLE_RIGHT,
                    TentCanvasBlock.showsOutline(part),
                    part.name());
        }
    }
}
