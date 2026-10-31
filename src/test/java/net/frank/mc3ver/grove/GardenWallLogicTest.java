package net.frank.mc3ver.grove;

import static net.frank.mc3ver.grove.GardenWallLogic.*;
import static org.junit.jupiter.api.Assertions.*;

import java.util.List;
import java.util.function.Consumer;
import net.frank.mc3ver.grove.GardenWallLogic.Stone;
import org.junit.jupiter.api.Test;

class GardenWallLogicTest {

    private static void forManySeeds(Consumer<List<Stone>> check) {
        for (long seed = 0; seed < 300; seed++) {
            check.accept(wall(seed));
        }
    }

    private static int length(List<Stone> wall) {
        return wall.stream().mapToInt(Stone::along).max().getAsInt() + 1;
    }

    @Test
    void wallIsThreeToSevenLongAndAtMostTwoHigh() {
        forManySeeds(w -> {
            assertTrue(length(w) >= MIN_LENGTH && length(w) <= MAX_LENGTH, "Länge " + length(w));
            assertTrue(w.stream().allMatch(s -> s.dy() >= 0 && s.dy() < MAX_HEIGHT), "Höhe");
        });
    }

    @Test
    void bothEndsStandOnTheGround() {
        forManySeeds(w -> {
            int last = length(w) - 1;
            assertTrue(w.contains(new Stone(0, 0, true)) || w.contains(new Stone(0, 0, false)), "Anfang");
            assertTrue(w.contains(new Stone(last, 0, true)) || w.contains(new Stone(last, 0, false)), "Ende");
        });
    }

    @Test
    void longerWallsHaveGaps() {
        forManySeeds(w -> {
            long base = w.stream().filter(s -> s.dy() == 0).count();
            if (length(w) >= 4) {
                assertTrue(base < length(w), "keine Lücke in " + w);
            }
        });
    }

    @Test
    void upperStonesRestOnLowerOnes() {
        forManySeeds(w -> {
            for (Stone s : w) {
                if (s.dy() == 1) {
                    assertTrue(w.stream().anyMatch(b -> b.along() == s.along() && b.dy() == 0), "schwebt: " + s);
                }
            }
        });
    }

    @Test
    void wallsMixMossyAndPlainStone() {
        forManySeeds(w -> {
            assertTrue(w.stream().anyMatch(Stone::mossy), "bemoost fehlt");
            assertTrue(w.stream().anyMatch(s -> !s.mossy()), "Bruchstein fehlt");
        });
    }

    // Über viele Mauern: verfallen, aber noch als Mauer erkennbar
    @Test
    void wallsAreRuinedButRecognisable() {
        int slots = 0;
        int ground = 0;
        int upper = 0;
        int mossy = 0;
        int stones = 0;
        int shortWithoutGap = 0;
        int shortWalls = 0;
        for (long seed = 0; seed < 2000; seed++) {
            List<Stone> w = wall(seed);
            int length = length(w);
            slots += length;
            long base = w.stream().filter(s -> s.dy() == 0).count();
            ground += (int) base;
            upper += (int) w.stream().filter(s -> s.dy() == 1).count();
            mossy += (int) w.stream().filter(Stone::mossy).count();
            stones += w.size();
            if (length == MIN_LENGTH) {
                shortWalls++;
                if (base == length) {
                    shortWithoutGap++;
                }
            }
        }
        double standing = (double) ground / slots;
        double secondRow = (double) upper / ground;
        double moss = (double) mossy / stones;
        assertTrue(standing > 0.6 && standing < 0.85, "stehende Stellen: " + standing);
        assertTrue(secondRow > 0.25 && secondRow < 0.55, "zweite Reihe: " + secondRow);
        assertTrue(moss > 0.3 && moss < 0.5, "bemoost: " + moss);
        assertTrue(shortWithoutGap > shortWalls / 2, "kurze Mauern meist ohne Lücke: " + shortWithoutGap);
    }

    @Test
    void sameSeedSameWall() {
        assertEquals(wall(7), wall(7));
        assertNotEquals(wall(7), wall(8));
    }
}
