package net.frank.mc3ver.grove;

import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.SplittableRandom;
import net.minecraft.core.Direction;

/**
 * Bauplan des Herzstücks: alter Birnbaum mit Wurzeln, Truhe und Bienenstöcken, kleiner See mit Quelle,
 * Moos und Pilze, Trittsteine. Liefert nur Positionen und Rollen; welcher Block eine Rolle füllt und wie
 * das Gelände angepasst wird, entscheidet die Struktur.
 *
 * <p>Koordinaten relativ zum Stamm: der 2×2-Stamm belegt x und z 0..1. y = 0 ist die erste Luftschicht über
 * dem Boden, y = -1 die Bodenoberfläche.
 */
public class OldPearTreeLogic {

    public static final int MIN_TRUNK_HEIGHT = 12;
    public static final int MAX_TRUNK_HEIGHT = 15;
    public static final int MIN_CROWN_RADIUS = 6;
    public static final int MAX_CROWN_RADIUS = 7;
    public static final int CROWN_DEPTH = 5;
    public static final int MIN_LAKE_RADIUS = 3;
    public static final int MAX_LAKE_RADIUS = 4;
    public static final int MIN_PATH_LENGTH = 8;
    public static final int MAX_PATH_LENGTH = 12;
    // Erster Trittstein knapp außerhalb der Wurzeln
    public static final int PATH_START = 6;
    public static final int GROUND_RADIUS = 5;
    // Abstand Stammmitte bis Seerand: außerhalb der Wurzeln (höchstens 5)
    public static final int LAKE_DISTANCE = 7;

    public enum Role {
        TRUNK,
        BRANCH,
        LEAVES,
        ROOT,
        BEEHIVE,
        CHEST,
        MOSS_BLOCK,
        MOSS_CARPET,
        MUSHROOM,
        WATER,
        CLAY,
        GRAVEL,
        LILY_PAD,
        SPRING_ROCK,
        SPRING_WATER,
        PATH
    }

    public record Pos(int x, int y, int z) {
        Pos offset(int dx, int dy, int dz) {
            return new Pos(x + dx, y + dy, z + dz);
        }
    }

    /** Rolle mit Ausrichtung: Achse bei Ästen und Wurzeln, Blickrichtung bei Truhe und Bienenstock. */
    public record Part(Role role, Direction facing) {}

    public record Layout(int trunkHeight, int lakeRadius, Map<Pos, Part> parts) {
        public List<Pos> positionsOf(Role role) {
            List<Pos> result = new ArrayList<>();
            parts.forEach((pos, part) -> {
                if (part.role() == role) {
                    result.add(pos);
                }
            });
            return result;
        }

        public Role roleAt(Pos pos) {
            Part part = parts.get(pos);
            return part == null ? null : part.role();
        }
    }

    // Stammmitte in Blockkoordinaten: zwischen Block 0 und 1; Block i hat seine Mitte bei i + 0,5
    private static final double TRUNK_CENTER = 1.0;

    private static final Direction[] SIDES = {Direction.NORTH, Direction.EAST, Direction.SOUTH, Direction.WEST};

    public static Layout layout(long seed) {
        SplittableRandom random = new SplittableRandom(seed);
        Map<Pos, Part> parts = new LinkedHashMap<>();
        int height = MIN_TRUNK_HEIGHT + random.nextInt(MAX_TRUNK_HEIGHT - MIN_TRUNK_HEIGHT + 1);
        double lakeAngle = random.nextDouble() * 2 * Math.PI;
        int lakeRadius = MIN_LAKE_RADIUS + random.nextInt(MAX_LAKE_RADIUS - MIN_LAKE_RADIUS + 1);

        placeTrunk(parts, height);
        placeBranches(parts, height, random);
        placeCrown(parts, height, random);
        placeRoots(parts, random);
        placeLake(parts, lakeAngle, lakeRadius, random);
        placeSpring(parts, lakeAngle, lakeRadius, random);
        placeChest(parts, random);
        placeBeehives(parts, height, random);
        placeGround(parts, random);
        placePath(parts, lakeAngle + Math.PI + (random.nextDouble() - 0.5) * Math.PI / 2, random);
        return new Layout(height, lakeRadius, Collections.unmodifiableMap(parts));
    }

    private static void placeTrunk(Map<Pos, Part> parts, int height) {
        for (int y = 0; y < height; y++) {
            for (int x = 0; x <= 1; x++) {
                for (int z = 0; z <= 1; z++) {
                    parts.put(new Pos(x, y, z), new Part(Role.TRUNK, Direction.UP));
                }
            }
        }
    }

    // Kurze waagerechte Äste unter der Kronenmitte, je Seite einer
    private static void placeBranches(Map<Pos, Part> parts, int height, SplittableRandom random) {
        for (Direction side : SIDES) {
            int y = height - 4 + random.nextInt(2);
            int length = 2 + random.nextInt(2);
            Pos start = edgeOfTrunk(side, random.nextInt(2), y);
            for (int i = 1; i <= length; i++) {
                parts.putIfAbsent(
                        start.offset(side.getStepX() * i, 0, side.getStepZ() * i), new Part(Role.BRANCH, side));
            }
        }
    }

    // Ellipsoid um die Stammspitze; am Rand fehlen zufällig Blätter, damit die Krone nicht wie eine Kugel wirkt
    private static void placeCrown(Map<Pos, Part> parts, int height, SplittableRandom random) {
        int radius = MIN_CROWN_RADIUS + random.nextInt(MAX_CROWN_RADIUS - MIN_CROWN_RADIUS + 1);
        int centerY = height - 1;
        for (int y = height - CROWN_DEPTH; y <= height + 2; y++) {
            for (int x = -radius; x <= radius + 1; x++) {
                for (int z = -radius; z <= radius + 1; z++) {
                    double h = sq(x - 0.5) + sq(z - 0.5);
                    double v = sq(y - centerY);
                    double e = h / sq(radius) + v / sq(CROWN_DEPTH - 1);
                    if (e <= 1 && !(e > 0.75 && random.nextDouble() < 0.35)) {
                        parts.putIfAbsent(new Pos(x, y, z), new Part(Role.LEAVES, Direction.UP));
                    }
                }
            }
        }
    }

    // Wurzeln laufen vom Stammfuß nach außen, jede von einem anderen Stammblock; das äußere Stück sinkt ein
    private static void placeRoots(Map<Pos, Part> parts, SplittableRandom random) {
        // 8 Startblöcke (4 Seiten × 2), gemischt; die ersten count werden Wurzeln
        int[] starts = {0, 1, 2, 3, 4, 5, 6, 7};
        for (int i = starts.length - 1; i > 0; i--) {
            int j = random.nextInt(i + 1);
            int tmp = starts[i];
            starts[i] = starts[j];
            starts[j] = tmp;
        }
        int count = 5 + random.nextInt(2);
        for (int r = 0; r < count; r++) {
            Direction side = SIDES[starts[r] / 2];
            Pos start = edgeOfTrunk(side, starts[r] % 2, 0);
            int length = 2 + random.nextInt(3);
            for (int i = 1; i <= length; i++) {
                int y = i == length && length > 2 ? -1 : 0;
                parts.putIfAbsent(
                        new Pos(start.x() + side.getStepX() * i, y, start.z() + side.getStepZ() * i),
                        new Part(Role.ROOT, side));
            }
        }
    }

    private static void placeLake(Map<Pos, Part> parts, double angle, int radius, SplittableRandom random) {
        double distance = LAKE_DISTANCE + radius;
        double cx = TRUNK_CENTER + Math.cos(angle) * distance;
        double cz = TRUNK_CENTER + Math.sin(angle) * distance;
        int reach = radius + 2;
        // Unregelmäßiger Rand: Radius je Richtung leicht verschoben
        double wobblePhase = random.nextDouble() * 2 * Math.PI;
        for (int x = (int) Math.floor(cx) - reach; x <= (int) Math.ceil(cx) + reach; x++) {
            for (int z = (int) Math.floor(cz) - reach; z <= (int) Math.ceil(cz) + reach; z++) {
                double d = Math.hypot(x + 0.5 - cx, z + 0.5 - cz);
                double edge = radius + 0.4 * Math.sin(3 * Math.atan2(z + 0.5 - cz, x + 0.5 - cx) + wobblePhase);
                if (d <= edge) {
                    parts.put(new Pos(x, -1, z), new Part(Role.WATER, Direction.UP));
                    if (d <= edge - 1.5) {
                        parts.put(new Pos(x, -2, z), new Part(Role.WATER, Direction.UP));
                        parts.put(new Pos(x, -3, z), new Part(Role.CLAY, Direction.UP));
                    } else {
                        parts.put(new Pos(x, -2, z), new Part(Role.CLAY, Direction.UP));
                    }
                } else if (d <= edge + 1) {
                    parts.putIfAbsent(new Pos(x, -1, z), new Part(Role.GRAVEL, Direction.UP));
                }
            }
        }
        List<Pos> water = new ArrayList<>();
        parts.forEach((pos, part) -> {
            if (part.role() == Role.WATER && pos.y() == -1) {
                water.add(pos);
            }
        });
        int lilies = 2 + random.nextInt(3);
        for (int i = 0; i < lilies && !water.isEmpty(); i++) {
            Pos w = water.remove(random.nextInt(water.size()));
            parts.putIfAbsent(w.offset(0, 1, 0), new Part(Role.LILY_PAD, Direction.UP));
        }
    }

    // Felsbrocken auf der dem Baum abgewandten Seeseite, oben eine Quelle, die in den See läuft
    private static void placeSpring(Map<Pos, Part> parts, double angle, int lakeRadius, SplittableRandom random) {
        double distance = LAKE_DISTANCE + 2 * lakeRadius + 2.5;
        int rx = (int) Math.floor(TRUNK_CENTER + Math.cos(angle) * distance);
        int rz = (int) Math.floor(TRUNK_CENTER + Math.sin(angle) * distance);
        for (int x = rx - 1; x <= rx + 1; x++) {
            for (int z = rz - 1; z <= rz + 1; z++) {
                Pos ground = new Pos(x, 0, z);
                if (parts.get(ground) == null && (x == rx || z == rz || random.nextBoolean())) {
                    parts.put(ground, new Part(Role.SPRING_ROCK, Direction.UP));
                }
            }
        }
        parts.put(new Pos(rx, 0, rz), new Part(Role.SPRING_ROCK, Direction.UP));
        parts.put(new Pos(rx, 1, rz), new Part(Role.SPRING_ROCK, Direction.UP));
        // Quelle auf dem Felsstück zum See hin
        int towardX = (int) Math.round(-Math.cos(angle));
        int towardZ = (int) Math.round(-Math.sin(angle));
        Pos spring = new Pos(rx + towardX, 1, rz + towardZ);
        parts.put(spring.offset(0, -1, 0), new Part(Role.SPRING_ROCK, Direction.UP));
        parts.put(spring, new Part(Role.SPRING_WATER, Direction.UP));
    }

    // Truhe am Stammfuß, neben mindestens einer Wurzel, Blick nach außen
    private static void placeChest(Map<Pos, Part> parts, SplittableRandom random) {
        List<Pos> candidates = new ArrayList<>();
        List<Direction> facings = new ArrayList<>();
        for (Direction side : SIDES) {
            for (int along = 0; along <= 1; along++) {
                Pos pos = edgeOfTrunk(side, along, 0).offset(side.getStepX(), 0, side.getStepZ());
                if (parts.get(pos) == null && touchesRoot(parts, pos)) {
                    candidates.add(pos);
                    facings.add(side);
                }
            }
        }
        if (candidates.isEmpty()) {
            // Ohne Lücke zwischen Wurzeln: eine Wurzel weicht der Truhe
            Direction side = SIDES[random.nextInt(SIDES.length)];
            Pos pos = edgeOfTrunk(side, 0, 0).offset(side.getStepX(), 0, side.getStepZ());
            parts.put(pos, new Part(Role.CHEST, side));
            parts.put(
                    pos.offset(
                            side.getClockWise().getStepX(),
                            0,
                            side.getClockWise().getStepZ()),
                    new Part(Role.ROOT, side));
            return;
        }
        int pick = random.nextInt(candidates.size());
        parts.put(candidates.get(pick), new Part(Role.CHEST, facings.get(pick)));
    }

    // Bienenstöcke direkt unter der Krone am Stamm, je an einer anderen Seite; dort ist immer Platz,
    // und davor bleibt Luft, damit Bienen ein- und ausfliegen
    private static void placeBeehives(Map<Pos, Part> parts, int height, SplittableRandom random) {
        int count = 1 + random.nextInt(2);
        int first = random.nextInt(SIDES.length);
        for (int i = 0; i < count; i++) {
            Direction side = SIDES[(first + 2 * i) % SIDES.length];
            Pos hive = edgeOfTrunk(side, random.nextInt(2), height - CROWN_DEPTH - 1)
                    .offset(side.getStepX(), 0, side.getStepZ());
            parts.put(hive, new Part(Role.BEEHIVE, side));
        }
    }

    // Moos und Pilze rund um den Stamm, nur wo nichts anderes liegt
    private static void placeGround(Map<Pos, Part> parts, SplittableRandom random) {
        int mushrooms = 0;
        for (int x = -GROUND_RADIUS; x <= GROUND_RADIUS + 1; x++) {
            for (int z = -GROUND_RADIUS; z <= GROUND_RADIUS + 1; z++) {
                if (sq(x - 0.5) + sq(z - 0.5) > sq(GROUND_RADIUS)) {
                    continue;
                }
                Pos ground = new Pos(x, -1, z);
                Pos above = new Pos(x, 0, z);
                if (parts.get(ground) != null || parts.get(above) != null || random.nextDouble() < 0.45) {
                    continue;
                }
                parts.put(ground, new Part(Role.MOSS_BLOCK, Direction.UP));
                double roll = random.nextDouble();
                if (roll < 0.35) {
                    parts.put(above, new Part(Role.MOSS_CARPET, Direction.UP));
                } else if (roll < 0.45 && mushrooms < 5) {
                    parts.put(above, new Part(Role.MUSHROOM, Direction.UP));
                    mushrooms++;
                }
            }
        }
    }

    // Trittsteine mit Lücken, von außen auf den Baum zu; nur auf freiem Boden
    private static void placePath(Map<Pos, Part> parts, double angle, SplittableRandom random) {
        int length = MIN_PATH_LENGTH + random.nextInt(MAX_PATH_LENGTH - MIN_PATH_LENGTH + 1);
        double dx = Math.cos(angle);
        double dz = Math.sin(angle);
        for (int step = 0; step < length; step++) {
            // Lücken nur dazwischen: erster und letzter Stein liegen immer, damit die Länge sichtbar bleibt
            if (step % 2 == 1 && step < length - 1 && random.nextBoolean()) {
                continue;
            }
            double d = PATH_START + step;
            Pos pos = new Pos((int) Math.floor(TRUNK_CENTER + dx * d), -1, (int) Math.floor(TRUNK_CENTER + dz * d));
            if (parts.get(pos) == null || parts.get(pos).role() == Role.MOSS_BLOCK) {
                parts.put(pos, new Part(Role.PATH, Direction.UP));
                parts.remove(pos.offset(0, 1, 0));
            }
        }
    }

    private static boolean touchesRoot(Map<Pos, Part> parts, Pos pos) {
        for (Direction side : SIDES) {
            if (roleAt(parts, pos.offset(side.getStepX(), 0, side.getStepZ())) == Role.ROOT) {
                return true;
            }
        }
        return false;
    }

    // Stammblock an der Seite {@code side}; along wählt einen der beiden Blöcke dieser Seite
    private static Pos edgeOfTrunk(Direction side, int along, int y) {
        return switch (side) {
            case NORTH -> new Pos(along, y, 0);
            case SOUTH -> new Pos(along, y, 1);
            case WEST -> new Pos(0, y, along);
            default -> new Pos(1, y, along);
        };
    }

    private static Role roleAt(Map<Pos, Part> parts, Pos pos) {
        Part part = parts.get(pos);
        return part == null ? null : part.role();
    }

    private static double sq(double v) {
        return v * v;
    }
}
