package net.frank.mc3ver.transport;

import net.minecraft.core.BlockPos;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
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
        if (target.isAt(currentX, currentY, currentZ, currentDimension)) {
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
            if (target != null && target.isAt(placedX, placedY, placedZ, placedDimension)) {
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

    // Karte nachholen: Rechtsklick mit leerer Hand gedrückt halten. Der Client wiederholt den Rechtsklick alle 4 Ticks.
    public static final int RECOVERY_HOLD_TICKS = 30;  // 1.5 seconds at 20 ticks/sec
    public static final int HOLD_MAX_GAP_TICKS = 8;    // longer pause counts as letting go

    public static boolean carriesMapFor(List<FlameTarget> playerMapTargets, int x, int y, int z, String dimension) {
        for (FlameTarget target : playerMapTargets) {
            if (target != null && target.isAt(x, y, z, dimension)) {
                return true;
            }
        }
        return false;
    }

    // Flammen aus älteren Versionen kennen ihr Ziel nicht; sie bekommen beim ersten Nachholen eine neue Farbe und ID
    public static FlameTarget resolveFlameTarget(FlameTarget stored, int x, int y, int z, String dimension, Random random) {
        if (stored != null && stored.isAt(x, y, z, dimension)) {
            return stored;
        }
        if (stored != null) {
            return new FlameTarget(x, y, z, dimension, stored.colorRgb(), stored.flameId());
        }
        return new FlameTarget(x, y, z, dimension, getRandomFlameColorRgb(random), UUID.randomUUID());
    }

    public static String progressBar(float progress, int segments) {
        int filled = Math.round(Math.max(0.0f, Math.min(1.0f, progress)) * segments);
        return "■".repeat(filled) + "□".repeat(segments - filled);
    }

    public static class FlameHoldTracker {
        private record Hold(String flameKey, long startTick, long lastTick) {}

        private final Map<UUID, Hold> holds = new HashMap<>();

        // Liefert den Fortschritt von 0 bis 1; bei 1 ist das Halten abgeschlossen und beginnt beim nächsten Mal von vorn
        public float update(UUID playerId, String flameKey, long gameTime) {
            Hold hold = holds.get(playerId);
            if (hold == null || !hold.flameKey().equals(flameKey) || gameTime - hold.lastTick() > HOLD_MAX_GAP_TICKS) {
                hold = new Hold(flameKey, gameTime, gameTime);
            }
            float progress = Math.min(1.0f, (float) (gameTime - hold.startTick()) / RECOVERY_HOLD_TICKS);
            if (progress >= 1.0f) {
                holds.remove(playerId);
                return 1.0f;
            }
            holds.put(playerId, new Hold(flameKey, hold.startTick(), gameTime));
            return progress;
        }

        public void reset(UUID playerId) {
            holds.remove(playerId);
        }
    }

    // Name beim Aufstellen: einmal vergeben, danach nur noch per Amboss an der Karte änderbar
    public static final int MAX_FLAME_NAME_LENGTH = 32;

    // Liefert null, wenn kein brauchbarer Name übrig bleibt
    public static String sanitizeFlameName(String raw) {
        if (raw == null) {
            return null;
        }
        // Farbcodes wie \u00A7c samt folgendem Zeichen und Steuerzeichen entfernen
        StringBuilder cleaned = new StringBuilder();
        int[] codePoints = raw.codePoints().toArray();
        for (int i = 0; i < codePoints.length; i++) {
            int cp = codePoints[i];
            if (cp == '\u00A7') {
                i++;
            } else if (!Character.isISOControl(cp)) {
                cleaned.appendCodePoint(cp);
            }
        }
        String name = cleaned.toString().strip();
        if (name.codePointCount(0, name.length()) > MAX_FLAME_NAME_LENGTH) {
            name = name.substring(0, name.offsetByCodePoints(0, MAX_FLAME_NAME_LENGTH)).strip();
        }
        return name.isEmpty() ? null : name;
    }

    // Schließen ohne Namen sperrt nichts; erst ein gesetzter Name ist endgültig
    public static boolean canNameFlame(String currentName, UUID placer, UUID player) {
        return currentName == null && (placer == null || placer.equals(player));
    }

    public record FlameTarget(
        int x,
        int y,
        int z,
        String dimension,
        int colorRgb,
        java.util.UUID flameId
    ) {
        public boolean isAt(int otherX, int otherY, int otherZ, String otherDimension) {
            return x == otherX && y == otherY && z == otherZ && otherDimension.equals(dimension);
        }

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
