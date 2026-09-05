package net.frank.mc3ver.transport;

import net.minecraft.core.BlockPos;

import java.util.Collections;
import java.util.List;
import java.util.Random;
import java.util.UUID;

public class TransportFlameLogic {

    public static final List<Integer> ALL_FLAME_COLORS = List.of(
        0xF9FFFE, // White
        0xF9801D, // Orange
        0xC74EBD, // Magenta
        0x3AB3DA, // Light Blue
        0xFED83D, // Yellow
        0x80C71F, // Lime
        0xF38BAA, // Pink
        0x474F52, // Gray
        0x9D9D97, // Light Gray
        0x169C9C, // Cyan
        0x8932B8, // Purple
        0x3C44AA, // Blue
        0x835432, // Brown
        0x5E7C16, // Green
        0xB02E26, // Red
        0x1D1D21  // Black
    );

    public static final int CHANNELING_TICKS = 50; // 2.5 seconds at 20 ticks/sec
    public static final int COOLDOWN_TICKS = 600;  // 30 seconds at 20 ticks/sec
    public static final int TRANSPORT_MAP_MAX_STACK_SIZE = 64; // Allows multiple (up to 64) maps to fit in a bundle

    public enum TeleportOutcome {
        SUCCESS,
        INVALID_TARGET,
        FLAME_EXTINGUISHED,
        ALREADY_AT_TARGET
    }

    public static TeleportOutcome evaluateTeleport(
        FlameTarget target,
        boolean flameExistsInWorld,
        int currentX,
        int currentY,
        int currentZ,
        String currentDimension
    ) {
        if (target == null) {
            return TeleportOutcome.INVALID_TARGET;
        }
        if (!flameExistsInWorld) {
            return TeleportOutcome.FLAME_EXTINGUISHED;
        }
        if (target.dimension().equals(currentDimension)
            && target.x() == currentX
            && target.y() == currentY
            && target.z() == currentZ) {
            return TeleportOutcome.ALREADY_AT_TARGET;
        }
        return TeleportOutcome.SUCCESS;
    }

    public static double[] calculateArrivalCoordinates(FlameTarget target) {
        return new double[]{
            target.x() + 0.5,
            (double) target.y(),
            target.z() + 0.5
        };
    }

    public static class ChannelingSession {
        private int ticksUsed = 0;
        private boolean complete = false;

        public boolean tick(boolean isUsingItem) {
            if (!isUsingItem) {
                ticksUsed = 0;
                complete = false;
                return false;
            }

            ticksUsed++;
            if (ticksUsed >= CHANNELING_TICKS) {
                complete = true;
                return true;
            }
            return false;
        }

        public int getTicksUsed() {
            return ticksUsed;
        }

        public float getProgress() {
            return Math.min(1.0f, (float) ticksUsed / CHANNELING_TICKS);
        }

        public boolean isComplete() {
            return complete;
        }

        public void reset() {
            ticksUsed = 0;
            complete = false;
        }
    }

    public static int getRandomFlameColorRgb(Random random) {
        return ALL_FLAME_COLORS.get(random.nextInt(ALL_FLAME_COLORS.size()));
    }

    public static boolean isValidFlameColorRgb(int colorRgb) {
        return ALL_FLAME_COLORS.contains(colorRgb);
    }

    public enum PlacementActionType {
        KEEP_EXISTING_MAP,
        RELINK_OLD_MAP,
        CREATE_NEW_MAP
    }

    public record PlacementDecision(
        PlacementActionType actionType,
        FlameTarget targetToRelink,
        FlameTarget newTarget
    ) {}

    @FunctionalInterface
    public interface FlameChecker {
        boolean flameExists(String dimension, BlockPos pos);
    }

    public static PlacementDecision decidePlacementAction(
        int placedX, int placedY, int placedZ, String placedDimension,
        List<FlameTarget> playerMapTargets,
        FlameChecker flameChecker,
        Random random
    ) {
        // 1. Check if player already has a map pointing to this exact position
        for (FlameTarget target : playerMapTargets) {
            if (target != null &&
                target.x() == placedX &&
                target.y() == placedY &&
                target.z() == placedZ &&
                placedDimension.equals(target.dimension())) {
                return new PlacementDecision(PlacementActionType.KEEP_EXISTING_MAP, target, target);
            }
        }

        // 2. Check if player has an extinguished old map (its flame is gone in the world)
        for (FlameTarget target : playerMapTargets) {
            if (target != null) {
                boolean exists = flameChecker.flameExists(target.dimension(), new BlockPos(target.x(), target.y(), target.z()));
                if (!exists) {
                    // Relink this old map! Preserve original color & flameId
                    FlameTarget relinkedTarget = new FlameTarget(
                        placedX, placedY, placedZ, placedDimension, target.colorRgb(), target.flameId()
                    );
                    return new PlacementDecision(PlacementActionType.RELINK_OLD_MAP, target, relinkedTarget);
                }
            }
        }

        // 3. No existing or extinguished map found -> create brand new map
        int newColor = getRandomFlameColorRgb(random);
        UUID newFlameId = UUID.randomUUID();
        FlameTarget freshTarget = new FlameTarget(
            placedX, placedY, placedZ, placedDimension, newColor, newFlameId
        );
        return new PlacementDecision(PlacementActionType.CREATE_NEW_MAP, null, freshTarget);
    }

    public record FlameTarget(
        int x,
        int y,
        int z,
        String dimension,
        int colorRgb,
        java.util.UUID flameId
    ) {
        public String formatCoordinates() {
            return String.format("X: %d, Y: %d, Z: %d", x, y, z);
        }

        public String formatDimensionName() {
            if (dimension == null) return "Unknown";
            if (dimension.endsWith("overworld")) return "Overworld";
            if (dimension.endsWith("the_nether") || dimension.endsWith("nether")) return "Nether";
            if (dimension.endsWith("the_end") || dimension.endsWith("end")) return "The End";
            return dimension;
        }
    }
}
