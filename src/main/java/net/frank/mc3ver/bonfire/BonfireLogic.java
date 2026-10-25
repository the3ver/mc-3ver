package net.frank.mc3ver.bonfire;

import net.minecraft.util.StringRepresentable;

import java.util.Locale;
import java.util.Optional;

// Spiellogik des 3x3-Signalfeuers ohne Weltzugriff, damit sie sich ohne laufendes Spiel testen lässt
public final class BonfireLogic {

    public static final int SIZE = 3;
    public static final int LIGHT_LEVEL_LIT = 15;
    public static final float FIRE_DAMAGE = 2.0f;

    // Rauchsäule über dem Mittelblock
    public static final int SMOKE_COLUMN_HEIGHT = 64;
    public static final int SMOKE_COLOR = 0xFFD8D8D8;
    // Ab dieser Entfernung wächst die Säule mit, damit sie nicht zu einem Strich schrumpft (wie beim Beacon)
    public static final float SMOKE_SCALE_DISTANCE = 96.0f;

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
}
