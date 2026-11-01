package net.frank.mc3ver.grove;

import static net.frank.mc3ver.grove.OldPearTreeLogic.*;
import static org.junit.jupiter.api.Assertions.*;

import java.util.List;
import java.util.function.Consumer;
import net.minecraft.core.Direction;
import org.junit.jupiter.api.Test;

class OldPearTreeLogicTest {

    private static final int SEEDS = 200;

    private static void forManySeeds(Consumer<Layout> check) {
        for (long seed = 0; seed < SEEDS; seed++) {
            check.accept(layout(seed));
        }
    }

    // Abstand zur Stammmitte (0,5 | 0,5)
    private static double fromTrunk(Pos p) {
        return Math.hypot(p.x() - 0.5, p.z() - 0.5);
    }

    private static boolean isSideNeighbour(Pos a, Pos b) {
        return a.y() == b.y() && Math.abs(a.x() - b.x()) + Math.abs(a.z() - b.z()) == 1;
    }

    @Test
    void trunkIsTwoByTwoAndTwelveToFifteenHigh() {
        forManySeeds(l -> {
            assertTrue(l.trunkHeight() >= 12 && l.trunkHeight() <= 15, "Höhe " + l.trunkHeight());
            // Die Inschrift ist Teil des Stamms
            List<Pos> trunk = new java.util.ArrayList<>(l.positionsOf(Role.TRUNK));
            trunk.addAll(l.positionsOf(Role.CARVING_LEFT));
            trunk.addAll(l.positionsOf(Role.CARVING_RIGHT));
            assertEquals(4 * l.trunkHeight(), trunk.size(), "kein Stammblock überschrieben");
            for (Pos p : trunk) {
                assertTrue(p.x() >= 0 && p.x() <= 1 && p.z() >= 0 && p.z() <= 1, "2×2: " + p);
            }
        });
    }

    @Test
    void crownSitsAtTheTopAndIsWide() {
        forManySeeds(l -> {
            List<Pos> leaves = l.positionsOf(Role.LEAVES);
            assertTrue(leaves.stream().allMatch(p -> p.y() >= l.trunkHeight() - CROWN_DEPTH), "Laub zu tief");
            double width = leaves.stream()
                    .mapToDouble(OldPearTreeLogicTest::fromTrunk)
                    .max()
                    .orElse(0);
            assertTrue(width >= 7, "Krone lädt nicht aus: " + width);
        });
    }

    // Licht: von oben gesehen bleibt innerhalb der Kronenfläche mindestens ein Fünftel der Spalten ohne Laub
    @Test
    void crownLetsLightThrough() {
        forManySeeds(l -> {
            java.util.Set<String> covered = new java.util.HashSet<>();
            double radius = 0;
            for (Pos p : l.positionsOf(Role.LEAVES)) {
                covered.add(p.x() + "/" + p.z());
                radius = Math.max(radius, fromTrunk(p));
            }
            int columns = 0;
            int open = 0;
            for (int x = -12; x <= 13; x++) {
                for (int z = -12; z <= 13; z++) {
                    if (fromTrunk(new Pos(x, 0, z)) <= radius && fromTrunk(new Pos(x, 0, z)) > 1) {
                        columns++;
                        if (!covered.contains(x + "/" + z)) {
                            open++;
                        }
                    }
                }
            }
            assertTrue(open >= columns / 5, "Krone zu dicht: " + open + " von " + columns + " Spalten offen");
        });
    }

    @Test
    void branchesReachOutFromTheTrunk() {
        forManySeeds(l -> {
            List<Pos> branches = l.positionsOf(Role.BRANCH);
            long farReaching = branches.stream()
                    .filter(p -> fromTrunk(p) >= MIN_BRANCH_LENGTH)
                    .count();
            assertTrue(farReaching >= MIN_BRANCHES - 1, "Äste zu kurz: " + farReaching);
        });
    }

    @Test
    void rootsSpreadFromTheTrunkFootAndSinkIntoTheGround() {
        forManySeeds(l -> {
            List<Pos> roots = l.positionsOf(Role.ROOT);
            assertTrue(roots.size() >= 10, "dicke Wurzeln: " + roots.size());
            assertTrue(roots.stream().allMatch(p -> p.y() == 0 || p.y() == -1));
            assertTrue(roots.stream().anyMatch(p -> p.y() == -1), "Wurzeln sinken ein");
            assertTrue(roots.stream().allMatch(p -> fromTrunk(p) <= GROUND_RADIUS + 0.5), "Wurzel zu lang");
        });
    }

    @Test
    void lakeIsFiveToTenAcrossBesideTheTreeWithClayAndGravel() {
        forManySeeds(l -> {
            List<Pos> water =
                    l.positionsOf(Role.WATER).stream().filter(p -> p.y() == -1).toList();
            int width = water.stream().mapToInt(Pos::x).max().getAsInt()
                    - water.stream().mapToInt(Pos::x).min().getAsInt()
                    + 1;
            int depth = water.stream().mapToInt(Pos::z).max().getAsInt()
                    - water.stream().mapToInt(Pos::z).min().getAsInt()
                    + 1;
            assertTrue(width >= 5 && width <= 10 && depth >= 5 && depth <= 10, width + "×" + depth);
            assertTrue(water.stream().allMatch(p -> fromTrunk(p) >= GROUND_RADIUS + 0.5), "See ragt in die Wurzeln");
            for (Pos w : water) {
                Role below = l.roleAt(w.offset(0, -1, 0));
                assertTrue(below == Role.WATER || below == Role.CLAY, "Grund unter " + w + ": " + below);
            }
            assertTrue(l.positionsOf(Role.WATER).stream().anyMatch(p -> p.y() == -2), "Mitte tiefer");
            assertFalse(l.positionsOf(Role.GRAVEL).isEmpty(), "Kiesrand");
        });
    }

    @Test
    void lilyPadsFloatOnTheLake() {
        forManySeeds(l -> {
            List<Pos> lilies = l.positionsOf(Role.LILY_PAD);
            assertTrue(lilies.size() >= 2 && lilies.size() <= 4, "Seerosen " + lilies.size());
            for (Pos p : lilies) {
                assertEquals(Role.WATER, l.roleAt(p.offset(0, -1, 0)));
            }
        });
    }

    @Test
    void springRunsFromARockIntoTheLake() {
        forManySeeds(l -> {
            List<Pos> springs = l.positionsOf(Role.SPRING_WATER);
            assertEquals(1, springs.size());
            Pos spring = springs.get(0);
            assertEquals(Role.SPRING_ROCK, l.roleAt(spring.offset(0, -1, 0)), "Quelle liegt auf Fels");
            double toLake = l.positionsOf(Role.WATER).stream()
                    .filter(p -> p.y() == -1)
                    .mapToDouble(w -> Math.hypot(w.x() - spring.x(), w.z() - spring.z()))
                    .min()
                    .getAsDouble();
            // Fließendes Wasser läuft auf ebenem Boden bis 7 Blöcke weit; 4 lässt Luft für Gelände
            assertTrue(toLake <= 4, "Quelle zu weit vom See: " + toLake);
            assertTrue(l.positionsOf(Role.SPRING_ROCK).size() >= 5, "Felsbrocken");
        });
    }

    @Test
    void oneChestBetweenTheRootsAtTheTrunkFoot() {
        forManySeeds(l -> {
            List<Pos> chests = l.positionsOf(Role.CHEST);
            assertEquals(1, chests.size());
            Pos chest = chests.get(0);
            assertEquals(0, chest.y());
            assertTrue(l.positionsOf(Role.TRUNK).stream().anyMatch(t -> isSideNeighbour(t, chest)), "am Stamm");
            assertTrue(l.positionsOf(Role.ROOT).stream().anyMatch(r -> isSideNeighbour(r, chest)), "neben Wurzel");
        });
    }

    @Test
    void beehivesHangUnderTheCrownWithRoomToFlyOut() {
        forManySeeds(l -> {
            List<Pos> hives = l.positionsOf(Role.BEEHIVE);
            assertTrue(hives.size() >= 1 && hives.size() <= 2, "Bienenstöcke " + hives.size());
            for (Pos hive : hives) {
                assertTrue(l.positionsOf(Role.TRUNK).stream().anyMatch(t -> isSideNeighbour(t, hive)), "am Stamm");
                Direction facing = l.parts().get(hive).facing();
                assertNull(l.roleAt(hive.offset(facing.getStepX(), 0, facing.getStepZ())), "Ausflug frei");
            }
        });
    }

    @Test
    void mossAndMushroomsOnlyAroundTheTree() {
        forManySeeds(l -> {
            List<Pos> moss = l.positionsOf(Role.MOSS_BLOCK);
            assertFalse(moss.isEmpty());
            assertTrue(moss.stream().allMatch(p -> p.y() == -1 && fromTrunk(p) <= GROUND_RADIUS));
            for (Role onTop : new Role[] {Role.MOSS_CARPET, Role.MUSHROOM}) {
                for (Pos p : l.positionsOf(onTop)) {
                    assertEquals(Role.MOSS_BLOCK, l.roleAt(p.offset(0, -1, 0)), onTop + " auf Moos");
                }
            }
            assertTrue(l.positionsOf(Role.MUSHROOM).size() <= 5);
        });
    }

    // Spec: Birnblüten nur am alten Baum; sie stehen auf freiem Boden, nicht auf Moos, Pfad, See oder Fels
    @Test
    void blossomsGrowInARingAroundTheTreeOnFreeGround() {
        forManySeeds(l -> {
            List<Pos> blossoms = l.positionsOf(Role.BLOSSOM);
            assertTrue(blossoms.size() >= 3, "Blüten " + blossoms.size());
            for (Pos p : blossoms) {
                assertEquals(0, p.y());
                assertTrue(fromTrunk(p) > GROUND_RADIUS && fromTrunk(p) <= BLOSSOM_RADIUS, "Abstand " + fromTrunk(p));
                for (Pos other : l.parts().keySet()) {
                    if (!other.equals(p) && other.x() == p.x() && other.z() == p.z()) {
                        assertTrue(other.y() > 0, "unter der Blüte liegt " + l.roleAt(other));
                    }
                }
            }
        });
    }

    // Zwei Stammblöcke auf Augenhöhe tragen die Inschrift, nach außen gewandt; links steht links vom Betrachter
    @Test
    void carvingSitsOnTwoTrunkBlocksFacingOutwards() {
        forManySeeds(l -> {
            List<Pos> left = l.positionsOf(Role.CARVING_LEFT);
            List<Pos> right = l.positionsOf(Role.CARVING_RIGHT);
            assertEquals(1, left.size());
            assertEquals(1, right.size());
            Pos a = left.get(0);
            Pos b = right.get(0);
            assertEquals(CARVING_Y, a.y());
            assertTrue(isSideNeighbour(a, b));
            Direction facing = l.parts().get(a).facing();
            assertEquals(facing, l.parts().get(b).facing());
            for (Pos p : List.of(a, b)) {
                assertTrue(p.x() >= 0 && p.x() <= 1 && p.z() >= 0 && p.z() <= 1, "im Stamm");
                assertNotEquals(Role.TRUNK, l.roleAt(p.offset(facing.getStepX(), 0, facing.getStepZ())), "Seite außen");
            }
            // Wer die Seite ansieht, schaut entgegen facing; seine linke Hand zeigt nach facing.getClockWise()
            Direction toLeft = facing.getClockWise();
            assertEquals(b.offset(toLeft.getStepX(), 0, toLeft.getStepZ()), a);
        });
    }

    @Test
    void everyCarvingTurnsUp() {
        java.util.Set<Carving> seen = java.util.EnumSet.noneOf(Carving.class);
        for (long seed = 0; seed < SEEDS; seed++) {
            seen.add(carving(seed));
        }
        assertEquals(java.util.EnumSet.allOf(Carving.class), seen);
        assertEquals(carving(42), carving(42));
    }

    @Test
    void steppingStonesLeadEightToTwelveBlocksTowardsTheTree() {
        forManySeeds(l -> {
            List<Pos> path = l.positionsOf(Role.PATH);
            assertTrue(path.size() >= MIN_PATH_LENGTH / 2, "Trittsteine " + path.size());
            assertTrue(path.stream().allMatch(p -> p.y() == -1));
            double nearest = path.stream()
                    .mapToDouble(OldPearTreeLogicTest::fromTrunk)
                    .min()
                    .getAsDouble();
            double farthest = path.stream()
                    .mapToDouble(OldPearTreeLogicTest::fromTrunk)
                    .max()
                    .getAsDouble();
            assertTrue(nearest <= PATH_START + 1.5, "Pfad endet zu weit vom Baum: " + nearest);
            // Erster und letzter Stein liegen MIN-1 Schritte auseinander; Runden auf Blöcke kostet bis zu 2 × √0,5
            assertTrue(
                    farthest - nearest >= MIN_PATH_LENGTH - 1 - 2 * Math.sqrt(0.5), "zu kurz: " + (farthest - nearest));
            assertTrue(farthest <= PATH_START + MAX_PATH_LENGTH - 1 + Math.sqrt(0.5), "zu lang: " + farthest);
            for (Pos p : path) {
                assertNull(l.roleAt(p.offset(0, 1, 0)), "nichts auf dem Trittstein");
            }
        });
    }

    // Formdetails (Kronenrand, Seeufer, Lücken im Pfad) prüfen die Eigenschaftstests nicht genau.
    // Ändert sich die Form ungewollt, fällt es hier auf; bei gewollter Änderung Werte neu übernehmen.
    @Test
    void roleCountsForOneSeedStayStable() {
        java.util.Map<Role, Long> counts = new java.util.TreeMap<>();
        layout(42).parts().values().forEach(part -> counts.merge(part.role(), 1L, Long::sum));
        assertEquals(
                "{TRUNK=54, BRANCH=23, LEAVES=442, ROOT=20, BEEHIVE=1, CHEST=1, MOSS_BLOCK=24, MOSS_CARPET=8,"
                        + " MUSHROOM=2, WATER=37, CLAY=30, GRAVEL=21, LILY_PAD=2, SPRING_ROCK=8, SPRING_WATER=1, PATH=9, BLOSSOM=15, CARVING_LEFT=1, CARVING_RIGHT=1}",
                counts.toString());
    }

    // Bug: See und Quellfels lagen fest auf Stammfuß-Höhe und schwebten über abfallendem Gelände
    @Test
    void lakeSitsAtTheLowestGroundOfItsColumns() {
        Layout l = layout(42);
        java.util.Set<Column> lake = lakeColumns(l);
        assertFalse(lake.isEmpty());
        for (Pos p : l.positionsOf(Role.WATER)) {
            assertTrue(lake.contains(new Column(p.x(), p.z())), "Wasserspalte fehlt: " + p);
        }
        for (Pos p : l.positionsOf(Role.GRAVEL)) {
            assertTrue(lake.contains(new Column(p.x(), p.z())), "Kiesrand gehört zum See: " + p);
        }
        // Gelände fällt nach +x ab: der Wasserspiegel folgt der tiefsten Spalte, nichts schwebt
        int level = lakeLevel(lake, (x, z) -> 80 - x);
        int lowest = lake.stream().mapToInt(c -> 80 - c.x()).min().getAsInt();
        assertEquals(lowest, level);
    }

    @Test
    void springRockStandsOnItsOwnGroundButNeverBelowTheLake() {
        Layout l = layout(42);
        java.util.Set<Column> rock = springColumns(l);
        for (Pos p : l.positionsOf(Role.SPRING_ROCK)) {
            assertTrue(rock.contains(new Column(p.x(), p.z())), "Felsspalte fehlt: " + p);
        }
        assertEquals(75, springLevel(rock, (x, z) -> 75, 70), "Fels auf höherem Gelände");
        assertEquals(70, springLevel(rock, (x, z) -> 60, 70), "Fels mindestens auf Seehöhe");
    }

    @Test
    void sameSeedSameHeartDifferentSeedsDiffer() {
        assertEquals(layout(42).parts(), layout(42).parts());
        assertNotEquals(layout(42).parts(), layout(43).parts());
    }
}
