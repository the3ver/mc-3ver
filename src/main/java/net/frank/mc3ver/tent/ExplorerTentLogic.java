package net.frank.mc3ver.tent;

public class ExplorerTentLogic {

    public static final int WIDTH = 3;
    public static final int LENGTH = 4;
    public static final int HEIGHT = 2;
    public static final int TOTAL_BLOCK_COUNT = WIDTH * LENGTH * HEIGHT; // 24

    public static final String DEFAULT_COLOR_NAME = "white";

    public static final java.util.Map<String, Integer> DYE_COLOR_MAP = java.util.Map.ofEntries(
        java.util.Map.entry("white", 0xF9FFFE),
        java.util.Map.entry("orange", 0xF9801D),
        java.util.Map.entry("magenta", 0xC74EBD),
        java.util.Map.entry("light_blue", 0x3AB3DA),
        java.util.Map.entry("yellow", 0xFED83D),
        java.util.Map.entry("lime", 0x80C71F),
        java.util.Map.entry("pink", 0xF38BAA),
        java.util.Map.entry("gray", 0x474F52),
        java.util.Map.entry("light_gray", 0x9D9D97),
        java.util.Map.entry("cyan", 0x169C9C),
        java.util.Map.entry("purple", 0x8932B8),
        java.util.Map.entry("blue", 0x3C44AA),
        java.util.Map.entry("brown", 0x835432),
        java.util.Map.entry("green", 0x5E7C16),
        java.util.Map.entry("red", 0xB02E26),
        java.util.Map.entry("black", 0x1D1D21)
    );

    public static final java.util.Set<String> ALL_DYE_COLORS = DYE_COLOR_MAP.keySet();

    public static String normalizeColorName(String name) {
        if (name == null) return DEFAULT_COLOR_NAME;
        String lower = name.toLowerCase(java.util.Locale.ROOT).trim();
        return DYE_COLOR_MAP.containsKey(lower) ? lower : DEFAULT_COLOR_NAME;
    }

    public static int getColorRgb(String colorName) {
        return DYE_COLOR_MAP.getOrDefault(normalizeColorName(colorName), DYE_COLOR_MAP.get(DEFAULT_COLOR_NAME));
    }

    public static int resolveItemColorRgb(Integer customRgb) {
        return customRgb != null ? customRgb : getColorRgb(DEFAULT_COLOR_NAME);
    }

    public static int resolveBlockColorRgb(String colorName) {
        return getColorRgb(colorName);
    }

    public static String getColorNameFromRgb(int rgb) {
        int r = (rgb >> 16) & 0xFF;
        int g = (rgb >> 8) & 0xFF;
        int b = rgb & 0xFF;

        String bestColor = DEFAULT_COLOR_NAME;
        double bestDist = Double.MAX_VALUE;

        for (java.util.Map.Entry<String, Integer> entry : DYE_COLOR_MAP.entrySet()) {
            int crgb = entry.getValue();
            if (crgb == rgb) {
                return entry.getKey();
            }
            int cr = (crgb >> 16) & 0xFF;
            int cg = (crgb >> 8) & 0xFF;
            int cb = crgb & 0xFF;
            double dist = Math.pow(r - cr, 2) + Math.pow(g - cg, 2) + Math.pow(b - cb, 2);
            if (dist < bestDist) {
                bestDist = dist;
                bestColor = entry.getKey();
            }
        }
        return bestColor;
    }

    public enum Facing {
        NORTH,
        SOUTH,
        EAST,
        WEST;

        public static Facing fromDirectionName(String name) {
            try {
                return valueOf(name.toUpperCase());
            } catch (Exception e) {
                return NORTH;
            }
        }
    }

    public enum TentPart {
        AIR,
        POLE,
        WALL_SIDE,
        WALL_LEFT,
        WALL_RIGHT,
        WALL_FRONT_LEFT,
        WALL_FRONT_RIGHT,
        WALL_CORNER_LEFT,
        WALL_CORNER_RIGHT,
        WALL_CRAFTING_TABLE_RIGHT,
        WALL_BACK,
        SLEEPING_BAG_FOOT,
        SLEEPING_BAG_HEAD,
        ROOF_LEFT,
        ROOF_RIGHT,
        ROOF_FRONT_LEFT,
        ROOF_FRONT_RIGHT,
        ROOF_CORNER_LEFT,
        ROOF_CORNER_RIGHT,
        ROOF_RIDGE,
        ROOF_FRONT,
        ROOF_BACK
    }

    public static TentPart getTentPart(int relX, int relY, int relZ) {
        if (relY == 0) {
            if (relX == 0) {
                if (relZ == 0) return TentPart.AIR; // Eingang frei begehbar
                if (relZ == 1) return TentPart.SLEEPING_BAG_FOOT;
                if (relZ == 2) return TentPart.SLEEPING_BAG_HEAD;
                if (relZ == 3) return TentPart.WALL_BACK;
            } else if (relX == -1) {
                if (relZ == 0) return TentPart.WALL_FRONT_LEFT;
                if (relZ == 3) return TentPart.WALL_CORNER_LEFT;
                return TentPart.WALL_LEFT;
            } else if (relX == 1) {
                if (relZ == 0) return TentPart.WALL_FRONT_RIGHT;
                if (relZ == 2) return TentPart.WALL_CRAFTING_TABLE_RIGHT;
                if (relZ == 3) return TentPart.WALL_CORNER_RIGHT;
                return TentPart.WALL_RIGHT;
            }
        } else if (relY == 1) {
            if (relX == -1) {
                if (relZ == 0) return TentPart.ROOF_FRONT_LEFT;
                if (relZ == 3) return TentPart.ROOF_CORNER_LEFT;
                return TentPart.ROOF_LEFT;
            } else if (relX == 1) {
                if (relZ == 0) return TentPart.ROOF_FRONT_RIGHT;
                if (relZ == 3) return TentPart.ROOF_CORNER_RIGHT;
                return TentPart.ROOF_RIGHT;
            } else {
                if (relZ == 0) return TentPart.ROOF_FRONT; // Vorderer Giebelbalken
                if (relZ == 3) return TentPart.ROOF_BACK; // Hinterer Giebel
                return TentPart.ROOF_RIDGE; // Mittlerer Firstbalken
            }
        }
        return TentPart.AIR;
    }

    public static boolean isCraftingTable(TentPart part) {
        return part == TentPart.WALL_CRAFTING_TABLE_RIGHT;
    }

    public static boolean hasSolidRoofPlane(TentPart part) {
        return part == TentPart.ROOF_FRONT || part == TentPart.ROOF_RIDGE;
    }

    public static double getRoofCollisionMinY() {
        return 14.0;
    }

    public static boolean hasSleepingBagObstacleCollision() {
        return false;
    }

    public static boolean hasFullBlockCollision(TentPart part) {
        return switch (part) {
            case WALL_SIDE, WALL_LEFT, WALL_RIGHT, WALL_FRONT_LEFT, WALL_FRONT_RIGHT,
                 WALL_CORNER_LEFT, WALL_CORNER_RIGHT, WALL_CRAFTING_TABLE_RIGHT, WALL_BACK,
                 ROOF_LEFT, ROOF_RIGHT, ROOF_FRONT_LEFT, ROOF_FRONT_RIGHT,
                 ROOF_CORNER_LEFT, ROOF_CORNER_RIGHT, ROOF_BACK, POLE -> true;
            default -> false;
        };
    }


    public record BlockCoord(int x, int y, int z) {}

    public static BlockCoord toWorldPos(int anchorX, int anchorY, int anchorZ, Facing facing, int relX, int relY, int relZ) {
        int worldY = anchorY + relY;
        int worldX;
        int worldZ;

        switch (facing) {
            case NORTH -> {
                worldX = anchorX + relX;
                worldZ = anchorZ - relZ;
            }
            case SOUTH -> {
                worldX = anchorX - relX;
                worldZ = anchorZ + relZ;
            }
            case EAST -> {
                worldX = anchorX + relZ;
                worldZ = anchorZ + relX;
            }
            case WEST -> {
                worldX = anchorX - relZ;
                worldZ = anchorZ - relX;
            }
            default -> {
                worldX = anchorX;
                worldZ = anchorZ;
            }
        }

        return new BlockCoord(worldX, worldY, worldZ);
    }

    public interface WorldView {
        boolean isSolid(int x, int y, int z);
        boolean isReplaceable(int x, int y, int z);
    }

    public enum PlacementStatus {
        SUCCESS,
        UNEVEN_GROUND,
        OBSTRUCTED
    }

    public record PlacementResult(PlacementStatus status, BlockCoord failedCoord) {
        public static PlacementResult success() {
            return new PlacementResult(PlacementStatus.SUCCESS, null);
        }

        public static PlacementResult unevenGround(BlockCoord coord) {
            return new PlacementResult(PlacementStatus.UNEVEN_GROUND, coord);
        }

        public static PlacementResult obstructed(BlockCoord coord) {
            return new PlacementResult(PlacementStatus.OBSTRUCTED, coord);
        }
    }

    public static PlacementResult validatePlacement(int anchorX, int anchorY, int anchorZ, Facing facing, WorldView world) {
        // 1. Boden-Prüfung: Alle 12 Blöcke unter dem Zelt (relY = -1) müssen fest sein
        for (int relX = -1; relX <= 1; relX++) {
            for (int relZ = 0; relZ < LENGTH; relZ++) {
                BlockCoord groundPos = toWorldPos(anchorX, anchorY, anchorZ, facing, relX, -1, relZ);
                if (!world.isSolid(groundPos.x(), groundPos.y(), groundPos.z())) {
                    return PlacementResult.unevenGround(groundPos);
                }
            }
        }

        // 2. Raum-Prüfung: Alle 24 Positionen des Zeltvolumens (relY = 0 und 1) müssen frei/ersetzbar sein
        for (int relY = 0; relY < HEIGHT; relY++) {
            for (int relX = -1; relX <= 1; relX++) {
                for (int relZ = 0; relZ < LENGTH; relZ++) {
                    BlockCoord tentPos = toWorldPos(anchorX, anchorY, anchorZ, facing, relX, relY, relZ);
                    if (!world.isReplaceable(tentPos.x(), tentPos.y(), tentPos.z())) {
                        return PlacementResult.obstructed(tentPos);
                    }
                }
            }
        }

        return PlacementResult.success();
    }

    public static BlockCoord getAnchorFromHead(int headX, int headY, int headZ, Facing facing) {
        // Kopf des Schlafsacks liegt immer bei (relX=0, relY=0, relZ=2)
        int anchorX = headX;
        int anchorY = headY;
        int anchorZ = headZ;

        switch (facing) {
            case NORTH -> anchorZ = headZ + 2;
            case SOUTH -> anchorZ = headZ - 2;
            case EAST -> anchorX = headX - 2;
            case WEST -> anchorX = headX + 2;
        }

        return new BlockCoord(anchorX, anchorY, anchorZ);
    }

    public interface TentStructureView {
        boolean isTentBlock(int x, int y, int z);
        boolean isSleepingBagHead(int x, int y, int z);
        Facing getFacing(int x, int y, int z);
        default String getColor(int x, int y, int z) {
            return null;
        }
    }

    public static String findTentColor(java.util.List<BlockCoord> blocks, TentStructureView world) {
        for (BlockCoord coord : blocks) {
            String color = world.getColor(coord.x(), coord.y(), coord.z());
            if (color != null && !color.isEmpty()) {
                return normalizeColorName(color);
            }
        }
        return DEFAULT_COLOR_NAME;
    }

    public static java.util.List<BlockCoord> collectTentBlocks(int clickedX, int clickedY, int clickedZ, TentStructureView world) {
        // Suche im Umkreis nach dem Kopfteil des Schlafsacks (maximaler Abstand eines Zeltblocks zum Kopf ist 2)
        BlockCoord foundHead = null;
        Facing foundFacing = null;

        for (int dy = -2; dy <= 2; dy++) {
            for (int dx = -3; dx <= 3; dx++) {
                for (int dz = -3; dz <= 3; dz++) {
                    int tx = clickedX + dx;
                    int ty = clickedY + dy;
                    int tz = clickedZ + dz;
                    if (world.isSleepingBagHead(tx, ty, tz)) {
                        foundHead = new BlockCoord(tx, ty, tz);
                        foundFacing = world.getFacing(tx, ty, tz);
                        break;
                    }
                }
                if (foundHead != null) break;
            }
            if (foundHead != null) break;
        }

        if (foundHead == null || foundFacing == null) {
            return java.util.Collections.emptyList();
        }

        BlockCoord anchor = getAnchorFromHead(foundHead.x(), foundHead.y(), foundHead.z(), foundFacing);
        java.util.List<BlockCoord> result = new java.util.ArrayList<>();

        for (int relY = 0; relY < HEIGHT; relY++) {
            for (int relX = -1; relX <= 1; relX++) {
                for (int relZ = 0; relZ < LENGTH; relZ++) {
                    if (getTentPart(relX, relY, relZ) != TentPart.AIR) {
                        BlockCoord p = toWorldPos(anchor.x(), anchor.y(), anchor.z(), foundFacing, relX, relY, relZ);
                        if (world.isTentBlock(p.x(), p.y(), p.z())) {
                            result.add(p);
                        }
                    }
                }
            }
        }

        return result;
    }

    public enum SleepStatus {
        SUCCESS,
        NOT_POSSIBLE_NOW,
        MONSTERS_NEARBY
    }

    public record SleepDecision(
        SleepStatus status,
        boolean preserveSpawnPoint,
        boolean resetRestTimer,
        boolean grantRestedBuff
    ) {}

    public static SleepDecision evaluateSleep(boolean isNight, boolean isThundering, boolean monstersNearby) {
        if (!isNight && !isThundering) {
            return new SleepDecision(SleepStatus.NOT_POSSIBLE_NOW, true, false, false);
        }
        if (monstersNearby) {
            return new SleepDecision(SleepStatus.MONSTERS_NEARBY, true, false, false);
        }
        return new SleepDecision(SleepStatus.SUCCESS, true, true, true);
    }
}



