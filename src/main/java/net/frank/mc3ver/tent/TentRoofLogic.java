package net.frank.mc3ver.tent;

/**
 * Querschnitt des Zeltdachs quer zum Eingang, in Pixeln: x läuft von der linken Außenkante (0) bis zur rechten (48),
 * y vom Zeltboden aufwärts. Niedrige Seitenwände, steiles Satteldach, spitzer First über der Mittelreihe. Das
 * Blockvolumen des Zelts ist 2 Blöcke (32 px) hoch, der First ragt einen halben Block darüber hinaus.
 */
public final class TentRoofLogic {

    public static final double WIDTH = 48.0;
    public static final double WALL_HEIGHT = 12.0;
    public static final double RIDGE_HEIGHT = 40.0;
    /** Überstand der Traufe über die Seitenwand hinaus. */
    public static final double EAVE = 2.0;
    /** Dicke der Dachplanken, senkrecht zur Dachfläche. */
    public static final double THICKNESS = 1.5;

    private TentRoofLogic() {}

    /** Anstieg der Dachfläche (Höhe pro Breite). */
    public static double slope() {
        return (RIDGE_HEIGHT - WALL_HEIGHT) / (WIDTH / 2);
    }

    public static double angleDegrees() {
        return Math.toDegrees(Math.atan(slope()));
    }

    /** Oberkante des Dachs über dem Zeltboden an der Querposition x (0..48). */
    public static double roofTopAt(double x) {
        double fromOuterEdge = Math.min(x, WIDTH - x);
        return WALL_HEIGHT + fromOuterEdge * slope();
    }

    /** Länge einer Dachfläche von der Traufe bis zum First. */
    public static double planeLength() {
        double run = WIDTH / 2 + EAVE;
        return Math.hypot(run, run * slope());
    }
}
