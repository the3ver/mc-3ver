package net.frank.mc3ver.grove;

import java.util.ArrayList;
import java.util.List;
import java.util.SplittableRandom;

/**
 * Rest einer verfallenen Gartenmauer: eine kurze Reihe Bruchstein mit Lücken, stellenweise zwei hoch,
 * teils bemoost. Deutet einen alten Obstgarten an. Liefert nur die Steine entlang der Mauer; Richtung und
 * Gelände bestimmt das Feature.
 */
public class GardenWallLogic {

    public static final int MIN_LENGTH = 3;
    public static final int MAX_LENGTH = 7;
    public static final int MAX_HEIGHT = 2;
    private static final double GAP_CHANCE = 0.25;
    private static final double SECOND_ROW_CHANCE = 0.4;
    private static final double MOSSY_CHANCE = 0.4;

    /** Stein an Position {@code along} (0 = Anfang), {@code dy} Blöcke über dem Boden. */
    public record Stone(int along, int dy, boolean mossy) {}

    public static List<Stone> wall(long seed) {
        SplittableRandom random = new SplittableRandom(seed);
        int length = MIN_LENGTH + random.nextInt(MAX_LENGTH - MIN_LENGTH + 1);
        // Anfang und Ende stehen immer; ab Länge 4 fehlt mindestens ein Stein dazwischen
        int forcedGap = length >= 4 ? 1 + random.nextInt(length - 2) : -1;
        List<Stone> stones = new ArrayList<>();
        for (int along = 0; along < length; along++) {
            boolean end = along == 0 || along == length - 1;
            if (along == forcedGap || (!end && random.nextDouble() < GAP_CHANCE)) {
                continue;
            }
            stones.add(new Stone(along, 0, random.nextDouble() < MOSSY_CHANCE));
            if (random.nextDouble() < SECOND_ROW_CHANCE) {
                stones.add(new Stone(along, 1, random.nextDouble() < MOSSY_CHANCE));
            }
        }
        return mixed(stones);
    }

    // Immer beides: ein Stein wird zu Moos bzw. zu Bruchstein, falls alle gleich sind
    private static List<Stone> mixed(List<Stone> stones) {
        boolean anyMossy = stones.stream().anyMatch(Stone::mossy);
        boolean anyPlain = stones.stream().anyMatch(s -> !s.mossy());
        if (!anyMossy) {
            Stone first = stones.get(0);
            stones.set(0, new Stone(first.along(), first.dy(), true));
        } else if (!anyPlain) {
            Stone last = stones.get(stones.size() - 1);
            stones.set(stones.size() - 1, new Stone(last.along(), last.dy(), false));
        }
        return stones;
    }
}
