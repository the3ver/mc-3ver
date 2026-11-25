package net.frank.mc3ver.village;

/** Den Backofen als Arbeitsplatz nehmen nur Bewohnerinnen; alle anderen Arbeitsplätze bleiben wie in Vanilla. */
public final class BakerJobLogic {

    private BakerJobLogic() {}

    public static boolean canTakeJob(boolean female, boolean bakerJobSite) {
        return female || !bakerJobSite;
    }
}
