package net.frank.mc3ver.village;

import java.util.ArrayList;
import java.util.List;

/**
 * Grundriss der Dorfbäckerei, unabhängig vom Dorftyp. Jede Ebene ist ein Raster aus Zeichen, Zeile = z, Spalte = x;
 * x = 0 liegt an der Straße. Der Datagen-Provider setzt die Zeichen je Dorftyp in Blöcke um.
 *
 * <pre>
 * ' ' nichts (Gelände bleibt)  '.' Luft       J Eingangs-Jigsaw   V Bewohner-Jigsaw
 * S Fundament  F Boden  W Wand  L Eckbalken  D/d Tür unten/oben  G Fenster
 * O Backofen   C Truhe  B Fass  H Heuballen  T Theke (Stufe oben)  K Schornstein
 * e/w Wandfackel nach Osten/Westen   x Wandfackel außen nach Westen   P Dachfläche
 * &gt; &lt; v ^ Dachtreppe nach Osten/Westen/Süden/Norden   1 2 3 4 Dachecke NW/NO/SW/SO
 * </pre>
 */
public final class BakeryLayoutLogic {

    /** Dorftypen in der Reihenfolge von Vanilla; je Typ eine Struktur im Häuser-Pool. */
    public static final List<String> STYLES = List.of("plains", "desert", "savanna", "snowy", "taiga");

    /** Gewicht im Häuser-Pool, so häufig wie eine Vanilla-Metzgerei. */
    public static final int POOL_WEIGHT = 2;

    public static final int SIZE_X = 9;
    public static final int SIZE_Z = 11;
    public static final int WALL_TOP = 3;
    public static final int CHIMNEY_X = 7;
    public static final int CHIMNEY_Z = 5;
    public static final int CHIMNEY_TOP = 7;

    private static final String[][] WALLS = {
        {
            ".........",
            ".LSSSSSL.",
            ".SFFFFFS.",
            ".SFFFFFS.",
            ".SFFFFFS.",
            "JSFFVFFS.",
            ".SFFFFFS.",
            ".SFFFFFS.",
            ".SFFFFFS.",
            ".LSSSSSL.",
            ".........",
        },
        {
            ".........",
            ".LWWWWWL.",
            ".WB...BW.",
            ".W..T.BW.",
            ".W..T..W.",
            ".D....OW.",
            ".W..T..W.",
            ".W..T.CW.",
            ".WH...HW.",
            ".LWWWWWL.",
            ".........",
        },
        {
            ".........",
            ".LWGWGWL.",
            ".W.....W.",
            ".G.....G.",
            "xW.....W.",
            ".d.....W.",
            "xW.....W.",
            ".G.....G.",
            ".W.....W.",
            ".LWGWGWL.",
            ".........",
        },
        {
            ".........",
            ".LWWWWWL.",
            ".W.....W.",
            ".We...wW.",
            ".W.....W.",
            ".W.....W.",
            ".W.....W.",
            ".We...wW.",
            ".W.....W.",
            ".LWWWWWL.",
            ".........",
        },
    };

    private BakeryLayoutLogic() {}

    public static String poolId(String style) {
        return "minecraft:village/" + style + "/houses";
    }

    public static String templateId(String style) {
        return "mc3ver:village/" + style + "/houses/" + style + "_bakery";
    }

    /** Ebene- und Taiga-Häuser bemoost Vanilla zu 10 %, die Bäckerei dort auch. */
    public static boolean mossy(String style) {
        return style.equals("plains") || style.equals("taiga");
    }

    /** Alle Ebenen von unten nach oben: Fundament, drei Wandlagen, Walmdach, Schornstein. */
    public static List<char[][]> layers() {
        List<char[][]> layers = new ArrayList<>();
        for (String[] rows : WALLS) layers.add(grid(rows));
        for (int ring = 0; ring <= CHIMNEY_TOP - WALL_TOP - 1; ring++) layers.add(roof(ring));
        for (int y = 1; y <= CHIMNEY_TOP; y++) layers.get(y)[CHIMNEY_Z][CHIMNEY_X] = 'K';
        return layers;
    }

    /** Dachlage {@code ring} über den Wänden: Treppenring außen, Dachfläche im nächsten Ring, innen Dachboden. */
    static char[][] roof(int ring) {
        char[][] grid = new char[SIZE_Z][SIZE_X];
        for (int z = 0; z < SIZE_Z; z++) {
            for (int x = 0; x < SIZE_X; x++) grid[z][x] = roofCell(x, z, ring);
        }
        return grid;
    }

    private static char roofCell(int x, int z, int ring) {
        int x0 = ring, x1 = SIZE_X - 1 - ring, z0 = ring, z1 = SIZE_Z - 1 - ring;
        if (x < x0 || x > x1 || z < z0 || z > z1) return ' ';
        boolean west = x == x0, east = x == x1, north = z == z0, south = z == z1;
        if (west && north) return '1';
        if (east && north) return '2';
        if (west && south) return '3';
        if (east && south) return '4';
        if (west) return '>';
        if (east) return '<';
        if (north) return 'v';
        if (south) return '^';
        // Dachfläche unter dem nächsten Treppenring, darin Dachboden; die oberste Lage ist nur noch Fläche
        boolean innerRing = x == x0 + 1 || x == x1 - 1 || z == z0 + 1 || z == z1 - 1;
        return innerRing ? 'P' : '.';
    }

    private static char[][] grid(String[] rows) {
        char[][] grid = new char[rows.length][];
        for (int z = 0; z < rows.length; z++) grid[z] = rows[z].toCharArray();
        return grid;
    }
}
