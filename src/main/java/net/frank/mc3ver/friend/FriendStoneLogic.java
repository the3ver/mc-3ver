package net.frank.mc3ver.friend;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

// Spiellogik des Freundschaftssteins ohne Weltzugriff: ein geprägter Stein führt immer zu dem, der ihn geprägt hat
public final class FriendStoneLogic {

    public static final int CHANNELING_TICKS = 50;  // 2,5 Sekunden
    public static final int COOLDOWN_TICKS = 600;   // 30 Sekunden
    public static final int UNBOUND_STACK_SIZE = 16;
    public static final int DISTANCE_UPDATE_TICKS = 10;

    private FriendStoneLogic() {}

    // generation: Stand des Zurücksetzen-Zählers des Besitzers beim Prägen
    public record FriendLink(UUID owner, String ownerName, int generation) {}

    public enum TeleportCheck {
        SUCCESS,
        UNBOUND,
        OWN_STONE,
        OFFLINE,
        REVOKED
    }

    // ownerGeneration ist nur bekannt, wenn der Besitzer online ist
    public static TeleportCheck checkTeleport(FriendLink link, UUID holder, boolean ownerOnline, int ownerGeneration) {
        if (link == null) {
            return TeleportCheck.UNBOUND;
        }
        if (link.owner().equals(holder)) {
            return TeleportCheck.OWN_STONE;
        }
        if (!ownerOnline) {
            return TeleportCheck.OFFLINE;
        }
        if (link.generation() < ownerGeneration) {
            return TeleportCheck.REVOKED;
        }
        return TeleportCheck.SUCCESS;
    }

    public interface SpotView {
        // Füße und Kopf frei, kein Wasser oder Lava
        boolean isFree(int x, int y, int z);

        // Fester Boden, auf dem man stehen kann
        boolean isFloor(int x, int y, int z);
    }

    // Erst die vier direkten Nachbarn, dann die Ecken, jeweils auf gleicher Höhe, eine Stufe höher und tiefer
    public static List<int[]> arrivalCandidates(int friendX, int friendY, int friendZ) {
        int[][] offsets = {{1, 0}, {-1, 0}, {0, 1}, {0, -1}, {1, 1}, {1, -1}, {-1, 1}, {-1, -1}};
        List<int[]> candidates = new ArrayList<>();
        for (int dy : new int[]{0, 1, -1}) {
            for (int[] offset : offsets) {
                candidates.add(new int[]{friendX + offset[0], friendY + dy, friendZ + offset[1]});
            }
        }
        return candidates;
    }

    // Kein freies Feld daneben: dann direkt auf die Position des Freundes, dort steht er ja selbst
    public static int[] findArrivalSpot(int friendX, int friendY, int friendZ, SpotView view) {
        for (int[] c : arrivalCandidates(friendX, friendY, friendZ)) {
            if (view.isFree(c[0], c[1], c[2]) && view.isFloor(c[0], c[1] - 1, c[2])) {
                return c;
            }
        }
        return new int[]{friendX, friendY, friendZ};
    }

    public enum Compass {
        NORTH, NORTH_EAST, EAST, SOUTH_EAST, SOUTH, SOUTH_WEST, WEST, NORTH_WEST;

        public String translationKey() {
            return "direction.mc3ver." + name().toLowerCase(java.util.Locale.ROOT);
        }
    }

    // Minecraft: Norden ist -Z, Osten ist +X
    public static Compass compass(double dx, double dz) {
        double degrees = Math.toDegrees(Math.atan2(dx, -dz));
        int sector = (int) Math.floorMod(Math.round(degrees / 45.0), 8L);
        return Compass.values()[sector];
    }

    public record Bearing(int distance, Compass direction) {}

    // Leer, wenn der Freund in einer anderen Dimension ist; dann zeigt die Anzeige nur die Dimension
    public static Optional<Bearing> bearing(double fromX, double fromZ, String fromDimension,
                                            double toX, double toZ, String toDimension) {
        if (!fromDimension.equals(toDimension)) {
            return Optional.empty();
        }
        double dx = toX - fromX;
        double dz = toZ - fromZ;
        return Optional.of(new Bearing((int) Math.round(Math.sqrt(dx * dx + dz * dz)), compass(dx, dz)));
    }

    public static boolean shouldUpdateDistance(long gameTime) {
        return gameTime % DISTANCE_UPDATE_TICKS == 0;
    }
}
