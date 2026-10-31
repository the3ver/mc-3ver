package net.frank.mc3ver.tent;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;

public class TentRainSplashTest {

    // ClientLevel setzt Regenspritzer auf blockY + Kollisionsform.max(Y, dx, dz) (Kontext ohne Entity).
    // Ein Regenpartikel ragt bis ~3,2 Pixel um seinen Mittelpunkt: auf der nur 2 Pixel dicken Plane
    // scheint er nach innen durch. Deshalb muss jede Regen-Oberkante mindestens 4 Pixel über der Plane liegen.
    private static final double SPLASH_LIFT = 4.0 / 16.0;

    @Test
    void rainSplashesLandOnTopOfEveryRoofPart() {
        net.minecraft.SharedConstants.tryDetectVersion();
        net.minecraft.server.Bootstrap.bootStrap();

        java.util.List<String> leaks = new java.util.ArrayList<>();
        for (TentCanvasBlock.CanvasPart part : TentCanvasBlock.CanvasPart.values()) {
            if (!part.name().startsWith("ROOF_")) continue;
            for (net.minecraft.core.Direction facing : net.minecraft.core.Direction.Plane.HORIZONTAL) {
                net.minecraft.world.phys.shapes.VoxelShape shape = TentCanvasBlock.getRainShape(part, facing);
                for (int i = 0; i < 16; i++) {
                    for (int j = 0; j < 16; j++) {
                        double dx = (i + 0.5) / 16.0;
                        double dz = (j + 0.5) / 16.0;
                        // Spalten ohne Plane (hinter dem Giebel) würden -Infinity liefern: Vanilla setzt den Spritzer
                        // dann auf die Blockunterkante direkt an die Rückwand, von wo er nach innen durchscheint
                        double top = shape.max(net.minecraft.core.Direction.Axis.Y, dx, dz);
                        if (top < 1.0 + SPLASH_LIFT - 1e-9) {
                            leaks.add(part + "/" + facing + " bei " + i + "," + j + " -> " + top);
                        }
                    }
                }
            }
        }
        assertTrue(
                leaks.isEmpty(),
                leaks.size() + " Spalten ohne Dachoberkante, z. B. " + leaks.subList(0, Math.min(5, leaks.size())));
    }

    @Test
    void entitiesKeepTheNormalRoofCollision() {
        net.minecraft.SharedConstants.tryDetectVersion();
        net.minecraft.server.Bootstrap.bootStrap();

        // Die Regenkappe gilt nur für Abfragen ohne Entity, Spieler und Mobs stoßen weiter an die echte Plane
        for (TentCanvasBlock.CanvasPart part : TentCanvasBlock.CanvasPart.values()) {
            for (net.minecraft.core.Direction facing : net.minecraft.core.Direction.Plane.HORIZONTAL) {
                assertTrue(
                        TentCanvasBlock.getWallShape(part, facing).max(net.minecraft.core.Direction.Axis.Y) <= 1.0,
                        part + " darf für Entities nicht über den Block hinausragen");
            }
        }
    }
}
