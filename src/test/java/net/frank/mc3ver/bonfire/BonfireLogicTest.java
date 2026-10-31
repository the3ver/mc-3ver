package net.frank.mc3ver.bonfire;

import static org.junit.jupiter.api.Assertions.*;

import java.util.HashSet;
import java.util.Set;
import org.junit.jupiter.api.Test;

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
        assertEquals(
                BonfireLogic.Part.CENTER, BonfireLogic.Part.fromOffset(0, 0).orElseThrow());
        assertEquals(
                BonfireLogic.Part.NORTH_EAST,
                BonfireLogic.Part.fromOffset(1, -1).orElseThrow());
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
        assertEquals(
                BonfireLogic.PlacementStatus.OK,
                BonfireLogic.validatePlacement(0, 64, 0, flatWorld(Set.of(), Set.of())));
    }

    @Test
    void blockInTheCornerObstructsPlacement() {
        assertEquals(
                BonfireLogic.PlacementStatus.OBSTRUCTED,
                BonfireLogic.validatePlacement(0, 64, 0, flatWorld(Set.of("1,64,1"), Set.of())));
    }

    @Test
    void holeInTheGroundIsUneven() {
        assertEquals(
                BonfireLogic.PlacementStatus.UNEVEN_GROUND,
                BonfireLogic.validatePlacement(0, 64, 0, flatWorld(Set.of(), Set.of("-1,0"))));
    }

    @Test
    void obstructionIsReportedBeforeUnevenGround() {
        assertEquals(
                BonfireLogic.PlacementStatus.OBSTRUCTED,
                BonfireLogic.validatePlacement(0, 64, 0, flatWorld(Set.of("0,64,0"), Set.of("1,1"))));
    }

    @Test
    void everyVanillaDyeHasASmokeColor() {
        for (net.minecraft.world.item.DyeColor dye : net.minecraft.world.item.DyeColor.values()) {
            BonfireLogic.SmokeColor color = BonfireLogic.SmokeColor.fromDyeName(dye.getName())
                    .orElseThrow(() -> new AssertionError("Keine Rauchfarbe für " + dye.getName()));
            assertEquals(dye.getName(), color.getSerializedName());
        }
        assertEquals(net.minecraft.world.item.DyeColor.values().length + 1, BonfireLogic.SmokeColor.values().length);
    }

    @Test
    void noneIsNotADye() {
        assertTrue(BonfireLogic.SmokeColor.fromDyeName("none").isEmpty());
        assertTrue(BonfireLogic.SmokeColor.fromDyeName("rainbow").isEmpty());
    }

    @Test
    void undyedSmokeStaysGray() {
        assertEquals(0xD8D8D8, BonfireLogic.SmokeColor.NONE.smokeRgb());
    }

    @Test
    void dyedSmokeIsLightenedButKeepsItsHue() {
        int red = BonfireLogic.SmokeColor.RED.smokeRgb();
        int r = (red >> 16) & 0xFF;
        int g = (red >> 8) & 0xFF;
        int b = red & 0xFF;
        assertTrue(r > 0xB0, "heller als der Farbstoff");
        assertTrue(r > g + 60 && r > b + 60, "bleibt rot");
        // Schwarz wird dunkelgrau statt fast schwarz
        int black = BonfireLogic.SmokeColor.BLACK.smokeRgb() & 0xFF;
        assertTrue(black >= 0x5A, "Schwarz: " + Integer.toHexString(black));
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

    private static final BonfireLogic.Wind CALM = new BonfireLogic.Wind(0.0, 0.0);

    @Test
    void windStaysWithinItsLimitsAndTurnsOverTime() {
        double min = Double.MAX_VALUE;
        double max = 0.0;
        Set<Long> directions = new HashSet<>();
        for (long t = 0; t < 200_000; t += 500) {
            BonfireLogic.Wind wind = BonfireLogic.windAt(t);
            min = Math.min(min, wind.strength());
            max = Math.max(max, wind.strength());
            directions.add(Math.round(Math.toDegrees(Math.atan2(wind.z(), wind.x())) / 45.0));
        }
        assertTrue(min >= BonfireLogic.WIND_BASE - BonfireLogic.WIND_VARIATION - 1e-9);
        assertTrue(max <= BonfireLogic.WIND_BASE + BonfireLogic.WIND_VARIATION + 1e-9);
        // Der Wind kommt im Lauf der Zeit aus verschiedenen Richtungen
        assertTrue(directions.size() >= 4, "Richtungen: " + directions);
    }

    @Test
    void windIsTheSameForTheSameGameTime() {
        assertEquals(BonfireLogic.windAt(12345), BonfireLogic.windAt(12345));
    }

    @Test
    void windChangesOnlySlowlyBetweenTicks() {
        BonfireLogic.Wind a = BonfireLogic.windAt(5000);
        BonfireLogic.Wind b = BonfireLogic.windAt(5001);
        assertEquals(a.x(), b.x(), 0.01);
        assertEquals(a.z(), b.z(), 0.01);
    }

    @Test
    void smokeParticlesDriftWithTheWind() {
        double[] v = BonfireLogic.smokeParticleVelocity(new BonfireLogic.Wind(0.5, -0.25), 0.08);
        assertEquals(0.04, v[0], 1e-9);
        assertEquals(0.08, v[1], 1e-9);
        assertEquals(-0.02, v[2], 1e-9);
    }

    @Test
    void puffsStayInsideThePlume() {
        for (int i = 0; i < BonfireLogic.PLUME_PUFFS; i++) {
            BonfireLogic.Puff puff = BonfireLogic.plumePuff(i, 1234.5, CALM);
            assertTrue(puff.y() >= BonfireLogic.PLUME_BASE_Y);
            assertTrue(puff.y() < BonfireLogic.PLUME_BASE_Y + BonfireLogic.PLUME_HEIGHT);
            assertTrue(puff.alpha() >= 0.0f && puff.alpha() <= BonfireLogic.PUFF_MAX_ALPHA);
            assertTrue(puff.variant() >= 0 && puff.variant() < 4);
        }
    }

    @Test
    void puffRisesGrowsAndFadesOverItsLifetime() {
        double lifetime = BonfireLogic.PLUME_LIFETIME_TICKS;
        BonfireLogic.Puff young = BonfireLogic.plumePuff(0, lifetime * 0.1, CALM);
        BonfireLogic.Puff old = BonfireLogic.plumePuff(0, lifetime * 0.9, CALM);
        assertTrue(old.y() > young.y());
        assertTrue(old.size() > young.size());
        assertTrue(old.alpha() < young.alpha());
    }

    @Test
    void puffFadesInRightAboveTheFire() {
        BonfireLogic.Puff born = BonfireLogic.plumePuff(0, 0.0, CALM);
        assertEquals(0.0f, born.alpha(), 1e-6);
    }

    @Test
    void flamesRiseWellAboveTheWoodpile() {
        // Der Holzstoß ist 2 Blöcke hoch, die mittlere Flamme soll deutlich darüber hinaus schlagen
        for (double t = 0; t < 400; t += 7) {
            BonfireLogic.FlameTongue middle = BonfireLogic.flameTongue(0, t);
            assertTrue(middle.height() > 2.8, "Höhe " + middle.height() + " bei t=" + t);
            assertEquals(0.0, middle.x(), 1e-9);
            assertEquals(0.0, middle.z(), 1e-9);
        }
    }

    @Test
    void outerFlamesSurroundTheMiddleAndAreSmaller() {
        for (int i = 1; i < BonfireLogic.FLAME_TONGUES; i++) {
            BonfireLogic.FlameTongue tongue = BonfireLogic.flameTongue(i, 100.0);
            assertTrue(Math.hypot(tongue.x(), tongue.z()) > 0.3);
            assertTrue(tongue.height() < BonfireLogic.flameTongue(0, 100.0).height() * 1.3);
            assertTrue(tongue.width() < BonfireLogic.FLAME_WIDTH);
        }
    }

    @Test
    void flamesFlickerOverTime() {
        double min = Double.MAX_VALUE;
        double max = 0.0;
        for (double t = 0; t < 200; t += 1) {
            double h = BonfireLogic.flameTongue(0, t).height();
            min = Math.min(min, h);
            max = Math.max(max, h);
        }
        assertTrue(max - min > 0.3);
    }

    @Test
    void flameFramesFollowTheVanillaAnimation() {
        assertEquals(0, BonfireLogic.flameTongue(0, 0.0).frame());
        assertEquals(1, BonfireLogic.flameTongue(0, 2.0).frame());
        assertEquals(0, BonfireLogic.flameTongue(0, 16.0).frame());
        for (int i = 0; i < BonfireLogic.FLAME_TONGUES; i++) {
            int frame = BonfireLogic.flameTongue(i, 12345.6).frame();
            assertTrue(frame >= 0 && frame < BonfireLogic.FLAME_FRAMES);
        }
    }

    @Test
    void windBendsThePlumeMoreHigherUp() {
        BonfireLogic.Wind east = new BonfireLogic.Wind(0.4, 0.0);
        double lifetime = BonfireLogic.PLUME_LIFETIME_TICKS;
        // Wirbel mitteln sich über viele Wolken heraus, der Windversatz bleibt
        double low = 0.0;
        double high = 0.0;
        for (int i = 0; i < 64; i++) {
            low += BonfireLogic.plumePuff(0, lifetime * 0.2 + i * lifetime, east)
                    .x();
            high += BonfireLogic.plumePuff(0, lifetime * 0.8 + i * lifetime, east)
                    .x();
        }
        assertTrue(low / 64 > 0.0);
        assertTrue(high / 64 > 4.0 * (low / 64));
    }
}
