package net.frank.mc3ver.bonfire;

import net.minecraft.util.StringRepresentable;

import java.util.Locale;
import java.util.Optional;

// Spiellogik des 3x3-Signalfeuers ohne Weltzugriff, damit sie sich ohne laufendes Spiel testen lässt
public final class BonfireLogic {

    public static final int SIZE = 3;
    public static final int LIGHT_LEVEL_LIT = 15;
    public static final float FIRE_DAMAGE = 2.0f;

    // Rauchfahne über dem Mittelblock: einzelne Wolken steigen auf, wachsen, verblassen und treiben im Wind
    public static final double PLUME_HEIGHT = 64.0;
    public static final double PLUME_BASE_Y = 1.6;
    public static final int PLUME_PUFFS = 40;
    public static final double PLUME_LIFETIME_TICKS = 600.0;
    public static final double PUFF_START_SIZE = 1.6;
    public static final double PUFF_END_SIZE = 8.0;
    public static final float PUFF_MAX_ALPHA = 0.7f;
    // Ab dieser Entfernung wächst die Fahne mit, damit sie nicht zu einem Strich schrumpft (wie beim Beacon)
    public static final float SMOKE_SCALE_DISTANCE = 96.0f;

    // Flammenzungen, die der Renderer über dem Holzstoß zeichnet (das Blockmodell darf nur 2 Blöcke hoch sein)
    public static final int FLAME_TONGUES = 5;
    public static final double FLAME_BASE_HEIGHT = 3.6;
    public static final double FLAME_WIDTH = 1.8;
    public static final int FLAME_FRAMES = 8;
    public static final int FLAME_FRAME_TICKS = 2;

    // Wind: seitlicher Versatz in Blöcken pro Block Steighöhe, Richtung dreht über Minuten langsam
    public static final double WIND_BASE = 0.3;
    public static final double WIND_VARIATION = 0.12;
    public static final double SIGNAL_SMOKE_RISE = 0.07;

    private BonfireLogic() {}

    public enum Part implements StringRepresentable {
        CENTER(0, 0),
        NORTH(0, -1),
        NORTH_EAST(1, -1),
        EAST(1, 0),
        SOUTH_EAST(1, 1),
        SOUTH(0, 1),
        SOUTH_WEST(-1, 1),
        WEST(-1, 0),
        NORTH_WEST(-1, -1);

        private final int dx;
        private final int dz;

        Part(int dx, int dz) {
            this.dx = dx;
            this.dz = dz;
        }

        public int dx() {
            return dx;
        }

        public int dz() {
            return dz;
        }

        public static Optional<Part> fromOffset(int dx, int dz) {
            for (Part part : values()) {
                if (part.dx == dx && part.dz == dz) {
                    return Optional.of(part);
                }
            }
            return Optional.empty();
        }

        // Liegt der Nachbar in Richtung (stepX, stepZ) noch innerhalb des 3x3-Feldes?
        public boolean hasNeighbor(int stepX, int stepZ) {
            return fromOffset(dx + stepX, dz + stepZ).isPresent();
        }

        @Override
        public String getSerializedName() {
            return name().toLowerCase(Locale.ROOT);
        }
    }

    public enum PlacementStatus {
        OK,
        OBSTRUCTED,
        UNEVEN_GROUND
    }

    public interface WorldView {
        boolean isSolid(int x, int y, int z);

        boolean isReplaceable(int x, int y, int z);
    }

    public static PlacementStatus validatePlacement(int centerX, int centerY, int centerZ, WorldView world) {
        for (Part part : Part.values()) {
            int x = centerX + part.dx();
            int z = centerZ + part.dz();
            if (!world.isReplaceable(x, centerY, z)) {
                return PlacementStatus.OBSTRUCTED;
            }
        }
        for (Part part : Part.values()) {
            if (!world.isSolid(centerX + part.dx(), centerY - 1, centerZ + part.dz())) {
                return PlacementStatus.UNEVEN_GROUND;
            }
        }
        return PlacementStatus.OK;
    }

    public static int lightLevel(boolean lit) {
        return lit ? LIGHT_LEVEL_LIT : 0;
    }

    public static float smokeRadiusScale(double horizontalDistance, boolean scoping) {
        if (scoping) {
            return 1.0f;
        }
        return Math.max(1.0f, (float) horizontalDistance / SMOKE_SCALE_DISTANCE);
    }

    public record Wind(double x, double z) {
        public double strength() {
            return Math.sqrt(x * x + z * z);
        }
    }

    // Hängt nur an der Spielzeit, damit Server-Spielzeit alle Clients gleich wehen lässt
    public static Wind windAt(double gameTime) {
        double angle = Math.sin(gameTime / 2400.0) * 1.3 + Math.sin(gameTime / 9100.0 + 1.0) * 2.1;
        double strength = WIND_BASE + WIND_VARIATION * Math.sin(gameTime / 3700.0 + 2.0);
        return new Wind(Math.cos(angle) * strength, Math.sin(angle) * strength);
    }

    // Geschwindigkeit eines Rauchpartikels, das mit SIGNAL_SMOKE_RISE aufsteigt und vom Wind mitgenommen wird
    public static double[] smokeParticleVelocity(Wind wind, double rise) {
        return new double[]{wind.x() * rise, rise, wind.z() * rise};
    }

    // x/z relativ zur Blockmitte, y relativ zur Blockunterkante; size in Blöcken, roll in Grad
    public record Puff(double x, double y, double z, double size, float alpha, float roll, int variant) {}

    public static Puff plumePuff(int index, double time, Wind wind) {
        double phase = frac(index / (double) PLUME_PUFFS + time / PLUME_LIFETIME_TICKS);
        double rise = PLUME_HEIGHT * phase;

        // Oben biegt der Wind stärker als direkt über dem Feuer, wo die Hitze den Rauch steil nach oben drückt
        double bend = rise * (0.15 + 0.65 * phase);
        double swirl = 0.3 + 1.5 * phase;
        double x = wind.x() * bend + Math.sin(time * 0.031 + index * 2.39) * swirl;
        double z = wind.z() * bend + Math.cos(time * 0.027 + index * 1.71) * swirl;

        double size = PUFF_START_SIZE + (PUFF_END_SIZE - PUFF_START_SIZE) * phase;
        double fadeIn = Math.min(1.0, phase / 0.06);
        float alpha = (float) (fadeIn * Math.pow(1.0 - phase, 1.3) * PUFF_MAX_ALPHA);
        float roll = (float) ((index * 47 + time * (index % 2 == 0 ? 0.25 : -0.25)) % 360.0);

        return new Puff(x, PLUME_BASE_Y + rise, z, size, alpha, roll, index % 4);
    }

    // x/z relativ zur Blockmitte, Fuß bei y = 0.2; yaw in Grad, frame = Bild im Animationsstreifen
    public record FlameTongue(double x, double z, double height, double width, float yaw, int frame) {}

    public static FlameTongue flameTongue(int index, double time) {
        // Mittlere Zunge am höchsten, die übrigen im Kreis darum
        boolean center = index == 0;
        double angle = index * (2.0 * Math.PI / (FLAME_TONGUES - 1));
        double radius = center ? 0.0 : 0.45;
        double flicker = 1.0 + 0.12 * Math.sin(time * 0.35 + index * 2.1) + 0.08 * Math.sin(time * 0.83 + index * 0.7);
        double height = FLAME_BASE_HEIGHT * (center ? 1.0 : 0.72) * flicker;
        double width = FLAME_WIDTH * (center ? 1.0 : 0.8);
        int frame = (int) Math.floorMod((long) Math.floor(time / FLAME_FRAME_TICKS) + index * 3L, FLAME_FRAMES);
        return new FlameTongue(Math.cos(angle) * radius, Math.sin(angle) * radius, height, width,
            (float) (index * 37.0), frame);
    }

    private static double frac(double value) {
        return value - Math.floor(value);
    }
}
