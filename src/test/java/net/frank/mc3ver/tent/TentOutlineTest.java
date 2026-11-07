package net.frank.mc3ver.tent;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;

class TentOutlineTest {

    // Die Blockklassen brauchen beim Laden die Minecraft-Registries; ohne Bootstrap bleibt die Klasse für alle
    // folgenden Tests derselben JVM unbrauchbar
    @org.junit.jupiter.api.BeforeAll
    static void bootstrap() {
        net.minecraft.SharedConstants.tryDetectVersion();
        net.minecraft.server.Bootstrap.bootStrap();
    }

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

    @Test
    void flapShowsItsOutlineOnlyWhenClosed() {
        assertTrue(TentFlapBlock.showsOutline(false), "geschlossene Plane füllt ihre Klickform aus");
        assertFalse(TentFlapBlock.showsOutline(true), "offene Plane ist oben aufgerollt");
    }

    @Test
    void sideWallsDoNotPokeThroughTheRoof() {
        // Ofen, Truhe und Werkbank tragen die Seitenwand in ihrer Form. Unter der Dachschräge (Zelt Richtung Norden:
        // linke Zelle mit Außenkante bei x = 0, rechte bei x = 16) darf keine Pixelspalte über die Dachlinie reichen,
        // sonst stechen Kollision und Auswahlrahmen durchs Dach.
        net.minecraft.core.Direction facing = net.minecraft.core.Direction.NORTH;
        java.util.Map<String, net.minecraft.world.phys.shapes.VoxelShape> left = java.util.Map.of(
                "WALL_LEFT", TentCanvasBlock.getWallShape(TentCanvasBlock.CanvasPart.WALL_LEFT, facing),
                "Ofen", TentFurnaceBlock.getFurnaceShape(facing));
        java.util.Map<String, net.minecraft.world.phys.shapes.VoxelShape> right = java.util.Map.of(
                "WALL_RIGHT", TentCanvasBlock.getWallShape(TentCanvasBlock.CanvasPart.WALL_RIGHT, facing),
                "Werkbank", TentCanvasBlock.getWallShape(TentCanvasBlock.CanvasPart.WALL_CRAFTING_TABLE_RIGHT, facing),
                "Truhe", TentChestBlock.getChestShape(facing));
        for (int d = 0; d < 16; d++) {
            double roof = TentRoofLogic.roofTopAt(d) / 16.0;
            for (var entry : left.entrySet()) assertUnderRoof(entry.getKey(), entry.getValue(), d + 0.5, roof);
            for (var entry : right.entrySet()) assertUnderRoof(entry.getKey(), entry.getValue(), 15.5 - d, roof);
        }
    }

    private static void assertUnderRoof(
            String name, net.minecraft.world.phys.shapes.VoxelShape shape, double x, double roof) {
        for (double z = 0.5; z < 16; z += 1) {
            // VoxelShape.max(Y, …) nimmt erst z, dann x
            double top = shape.max(net.minecraft.core.Direction.Axis.Y, z / 16.0, x / 16.0);
            assertTrue(top <= roof + 1e-9, name + " bei x=" + x + ", z=" + z + " reicht bis " + top * 16 + " px");
        }
    }
}
