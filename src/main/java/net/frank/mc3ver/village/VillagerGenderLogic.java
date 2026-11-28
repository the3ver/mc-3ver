package net.frank.mc3ver.village;

import java.util.UUID;

/** Entscheidet, ob ein Dorfbewohner eine Bewohnerin ist. Die UUID legt das fest: gleicher Bewohner, gleiches Ergebnis. */
public final class VillagerGenderLogic {

    private VillagerGenderLogic() {}

    // Das unterste Bit einer zufälligen UUID (Version 4) ist gleichverteilt: genau die Hälfte ist weiblich
    public static boolean isFemale(UUID uuid) {
        return (uuid.getLeastSignificantBits() & 1L) == 0L;
    }

    // Aussehen der Bewohnerin: die Bits über dem Geschlechtsbit, damit jede Variante etwa gleich oft vorkommt
    public static int look(UUID uuid, int looks) {
        return (int) ((uuid.getLeastSignificantBits() >>> 1) % looks);
    }
}
