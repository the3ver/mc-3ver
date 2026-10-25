package net.frank.mc3ver.tent;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class TentRainproofTest {

    // Nachbau von BlockStateBase.calculateSolid() aus 26.2: Regen fällt bis zum ersten "soliden" Block
    private static boolean vanillaSolidFromShape(net.minecraft.world.phys.shapes.VoxelShape shape) {
        if (shape.isEmpty()) return false;
        net.minecraft.world.phys.AABB bounds = shape.bounds();
        return bounds.getSize() >= 0.7291666666666666 || bounds.getYsize() >= 1.0;
    }

    @Test
    void thinRoofPlaneAloneWouldLetRainThrough() {
        net.minecraft.SharedConstants.tryDetectVersion();
        net.minecraft.server.Bootstrap.bootStrap();

        // Der First über dem Schlafsack ist nur 2 Pixel dick: ohne forceSolidOn regnet es durch
        assertFalse(vanillaSolidFromShape(TentCanvasBlock.getWallShape(TentCanvasBlock.CanvasPart.ROOF_RIDGE, net.minecraft.core.Direction.NORTH)));
        assertFalse(vanillaSolidFromShape(TentCanvasBlock.getWallShape(TentCanvasBlock.CanvasPart.ROOF_FRONT, net.minecraft.core.Direction.NORTH)));
    }

    @Test
    void tentCanvasIsForcedSolidSoRainStopsAtTheRoof() throws Exception {
        // Blöcke lassen sich im Test nicht registrieren, daher Prüfung der Block-Eigenschaften im Quelltext
        // Windows-Checkouts haben CRLF, die Suche unten erwartet LF
        String source = java.nio.file.Files.readString(java.nio.file.Paths.get("").toAbsolutePath()
            .resolve("src/main/java/net/frank/mc3ver/tent/ModTentBlocks.java")).replace("\r\n", "\n");
        int start = source.indexOf("new TentCanvasBlock(");
        assertTrue(start > 0, "TENT_CANVAS-Registrierung nicht gefunden");
        String canvasProperties = source.substring(start, source.indexOf(")\n    );", start));
        assertTrue(canvasProperties.contains(".forceSolidOn()"),
            "Zeltplane muss forceSolidOn() setzen, sonst regnet es durch das dünne Dach");
    }
}
