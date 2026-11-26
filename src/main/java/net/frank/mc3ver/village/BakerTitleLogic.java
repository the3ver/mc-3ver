package net.frank.mc3ver.village;

/** Stufennamen im Handelsmenü der Bäckerin in weiblicher Form (Gesellin, Expertin, Meisterin). */
public final class BakerTitleLogic {

    public static final String BAKER_NAME_KEY = "entity.mc3ver.villager.baker";
    static final String VANILLA_LEVEL_PREFIX = "merchant.level.";
    static final String FEMALE_LEVEL_PREFIX = "mc3ver.merchant.level.female.";

    private BakerTitleLogic() {}

    /** Übersetzungsschlüssel des Stufennamens; {@code titleKey} ist der Schlüssel des Händlernamens oder leer. */
    public static String levelKey(String titleKey, String vanillaLevelKey) {
        if (!BAKER_NAME_KEY.equals(titleKey) || !vanillaLevelKey.startsWith(VANILLA_LEVEL_PREFIX)) {
            return vanillaLevelKey;
        }
        return FEMALE_LEVEL_PREFIX + vanillaLevelKey.substring(VANILLA_LEVEL_PREFIX.length());
    }
}
