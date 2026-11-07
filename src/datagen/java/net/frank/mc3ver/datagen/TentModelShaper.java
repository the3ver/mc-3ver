package net.frank.mc3ver.datagen;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import java.util.ArrayList;
import java.util.List;
import net.frank.mc3ver.tent.TentRoofLogic;

/**
 * Bringt eine Zeltmodell-Vorlage in die Dachform aus {@link TentRoofLogic}. Die Vorlagen enthalten Wände und Giebel
 * als grobe Kästen; alles mit Planentextur wird hier in 1-Pixel-Spalten unter die Dachlinie gestuft. Dazu kommen die
 * schrägen Dachplanken (obere Seitenzellen) und die Firststange (obere Mittelzellen).
 *
 * <p>Welche Zelle ein Modell zeigt, steht im Namen: "left"/"right" für die Seitenspalten, sonst die Mitte; "roof" für
 * die obere Blockebene.
 */
final class TentModelShaper {

    private static final String[] FACES = {"up", "down", "north", "south", "east", "west"};
    private static final double BLOCK = 16.0;
    /** So viel kürzer endet die rechte Dachfläche an den Giebeln, unsichtbar, aber nicht mehr deckungsgleich. */
    static final double END_INSET = 0.02;
    /** So weit steht die Firststange an den Zeltenden über das Dach hinaus. */
    static final double RIDGE_POLE_OVERHANG = 0.5;
    /** Abstand des Herings von der Seitenwand und seine Höhe über dem Boden. */
    static final double STAKE_DISTANCE = 10.5;

    static final double STAKE_HEIGHT = 3.0;
    /** So weit stehen die Eck-Heringe vor bzw. hinter dem Giebel, die Leine läuft dadurch schräg vom Zelt weg. */
    static final double CORNER_SPREAD = 7.0;
    /** Abstand der Giebel-Heringe vom Giebel; innerhalb der Modellgrenze von 32. */
    static final double GABLE_STAKE_DISTANCE = 14.0;

    private TentModelShaper() {}

    static JsonObject shape(String name, JsonObject template) {
        JsonObject model = template.deepCopy();
        if (!model.has("elements")) return model;
        double cellX = name.contains("left") ? 0 : name.contains("right") ? 2 * BLOCK : BLOCK;
        double cellY = name.contains("roof") ? BLOCK : 0;
        boolean roof = cellY > 0;
        boolean side = cellX != BLOCK;

        JsonArray elements = new JsonArray();
        for (JsonElement el : model.getAsJsonArray("elements")) {
            JsonObject element = el.getAsJsonObject();
            if (isCanvas(element)) {
                clipUnderRoof(element, cellX, cellY).forEach(elements::add);
            } else {
                elements.add(element);
            }
        }
        // Der Eingang liegt in Modellkoordinaten bei z = 16, die Rückwand der hinteren Reihe bei z = 0. Über beide
        // Giebel steht das Dach ein Stück hinaus.
        double zFrom = name.contains("corner") || name.contains("back") ? -TentRoofLogic.GABLE_OVERHANG : 0;
        double zTo = name.contains("front") ? BLOCK + TentRoofLogic.GABLE_OVERHANG : BLOCK;
        // Unter dem First überlappen sich die beiden Dachflächen, die Firststange steckt dazwischen. Liegen ihre
        // Stirnflächen an den Giebeln in einer Ebene, flackern sie (Z-Fighting). Die rechte Fläche endet deshalb ein
        // Hauch früher, die Firststange steht an den Zeltenden sichtbar über.
        if (roof && side) {
            double inset = cellX == 0 ? 0 : END_INSET;
            double from = zFrom < 0 ? zFrom + inset : zFrom;
            double to = zTo > BLOCK ? zTo - inset : zTo;
            planks(cellX == 0, from, to).forEach(elements::add);
        }
        if (roof && !side) {
            model.getAsJsonObject("textures").addProperty("ridge", "mc3ver:block/tent_pole");
            double from = zFrom < 0 ? zFrom - RIDGE_POLE_OVERHANG : zFrom;
            double to = zTo > BLOCK ? zTo + RIDGE_POLE_OVERHANG : zTo;
            elements.add(ridgePole(from, to));
        }
        if (roof) {
            // Gedrehte Flächen bekommen sonst fleckige Umgebungsverdeckung
            model.addProperty("ambientocclusion", false);
        }
        model.add("elements", elements);
        return model;
    }

    /** Modelle mit je einer Abspannleine samt Hering, in Koordinaten der Zelle, an der sie hängen. */
    static final List<String> GUY_LINE_MODELS = List.of(
            "tent_guy_line_left",
            "tent_guy_line_right",
            "tent_guy_line_front_left",
            "tent_guy_line_front_right",
            "tent_guy_line_corner_left",
            "tent_guy_line_corner_right",
            "tent_guy_line_gable_front",
            "tent_guy_line_gable_back");

    /**
     * Welche Abspannleine zur Zelle eines Zeltmodells gehört: je untere Seitenzelle eine, dazu je eine vom Firstende
     * an beiden Giebeln. {@code null} für Zellen ohne Leine.
     */
    static String guyLineFor(String cellModel) {
        if (cellModel.endsWith("roof_front")) return "tent_guy_line_gable_front";
        if (cellModel.endsWith("roof_back")) return "tent_guy_line_gable_back";
        if (cellModel.contains("roof")) return null;
        String side = cellModel.endsWith("left") ? "left" : cellModel.endsWith("right") ? "right" : null;
        if (side == null) return null;
        if (cellModel.contains("wall_front")) return "tent_guy_line_front_" + side;
        if (cellModel.contains("wall_corner")) return "tent_guy_line_corner_" + side;
        return "tent_guy_line_" + side;
    }

    static JsonObject guyLineModel(String name) {
        List<JsonObject> line = name.contains("gable")
                ? gableGuyLine(name.endsWith("front"))
                : sideGuyLine(name, name.endsWith("left"));
        JsonObject textures = new JsonObject();
        textures.addProperty("stake", "minecraft:block/stripped_oak_log");
        textures.addProperty("rope", "minecraft:block/white_wool");
        textures.addProperty("particle", "minecraft:block/stripped_oak_log");
        JsonArray elements = new JsonArray();
        line.forEach(elements::add);
        JsonObject model = new JsonObject();
        model.addProperty("parent", "minecraft:block/block");
        model.add("textures", textures);
        model.add("elements", elements);
        return model;
    }

    static boolean isCanvas(JsonObject element) {
        for (var face : element.getAsJsonObject("faces").entrySet()) {
            if (!"#canvas"
                    .equals(face.getValue().getAsJsonObject().get("texture").getAsString())) return false;
        }
        return true;
    }

    /** Teilt das Element in Spalten, deren Oberkante nirgends über der Dachlinie liegt. */
    static List<JsonObject> clipUnderRoof(JsonObject element, double cellX, double cellY) {
        JsonArray from = element.getAsJsonArray("from");
        JsonArray to = element.getAsJsonArray("to");
        double x0 = from.get(0).getAsDouble();
        double x1 = to.get(0).getAsDouble();
        double y0 = from.get(1).getAsDouble();
        double y1 = to.get(1).getAsDouble();

        List<double[]> columns = new ArrayList<>();
        for (double x = x0; x < x1 - 1e-9; ) {
            double next = Math.min(x1, Math.floor(x) + 1);
            double cap = Math.min(TentRoofLogic.roofTopAt(cellX + x), TentRoofLogic.roofTopAt(cellX + next)) - cellY;
            double top = Math.min(y1, Math.floor(cap * 2) / 2);
            if (top > y0) {
                double[] last = columns.isEmpty() ? null : columns.get(columns.size() - 1);
                if (last != null && last[2] == top && last[1] == x) {
                    last[1] = next;
                } else {
                    columns.add(new double[] {x, next, top});
                }
            }
            x = next;
        }
        if (columns.size() == 1 && columns.get(0)[0] == x0 && columns.get(0)[1] == x1 && columns.get(0)[2] == y1) {
            return List.of(element);
        }
        List<JsonObject> result = new ArrayList<>();
        for (double[] column : columns) {
            JsonObject part = element.deepCopy();
            part.add("from", vec(column[0], y0, from.get(2).getAsDouble()));
            part.add("to", vec(column[1], column[2], to.get(2).getAsDouble()));
            // Ohne feste UV nimmt Minecraft die Position, die Textur läuft so über die Stufen weiter
            for (var face : part.getAsJsonObject("faces").entrySet()) {
                face.getValue().getAsJsonObject().remove("uv");
            }
            result.add(part);
        }
        return result;
    }

    /**
     * Eine Dachfläche von der Traufe bis zum First, in Stücke von höchstens 16 px zerlegt. Jedes Stück dreht um seinen
     * eigenen Anfangspunkt auf der Dachlinie, so bleiben die ungedrehten Koordinaten im erlaubten Bereich -16..32.
     */
    static List<JsonObject> planks(boolean leftCell, double zFrom, double zTo) {
        double angle = TentRoofLogic.angleDegrees();
        double cos = Math.cos(Math.toRadians(angle));
        double sin = Math.sin(Math.toRadians(angle));
        double sign = leftCell ? 1 : -1;
        // Traufe in Koordinaten der oberen Seitenzelle
        double eaveX = leftCell ? -TentRoofLogic.EAVE : BLOCK + TentRoofLogic.EAVE;
        double eaveY = TentRoofLogic.WALL_HEIGHT - TentRoofLogic.EAVE * TentRoofLogic.slope() - BLOCK;
        double length = TentRoofLogic.planeLength();

        List<JsonObject> result = new ArrayList<>();
        for (double s = 0; s < length - 1e-9; s += BLOCK) {
            double piece = Math.min(BLOCK, length - s);
            double startX = eaveX + sign * s * cos;
            double startY = eaveY + s * sin;
            double minX = leftCell ? startX : startX - piece;
            JsonObject element = new JsonObject();
            element.add("from", vec(minX, startY - TentRoofLogic.THICKNESS, zFrom));
            element.add("to", vec(minX + piece, startY, zTo));
            JsonObject rotation = new JsonObject();
            rotation.add("origin", vec(startX, startY, BLOCK / 2));
            rotation.addProperty("axis", "z");
            rotation.addProperty("angle", round(sign * angle));
            element.add("rotation", rotation);
            JsonObject faces = new JsonObject();
            for (String face : FACES) {
                double u = face.equals("east") || face.equals("west") ? BLOCK : piece;
                double v = face.equals("up") || face.equals("down") ? BLOCK : TentRoofLogic.THICKNESS;
                JsonObject f = new JsonObject();
                f.add("uv", vec(0, 0, u, v));
                f.addProperty("texture", "#canvas");
                f.addProperty("tintindex", 0);
                faces.add(face, f);
            }
            element.add("faces", faces);
            result.add(element);
        }
        return result;
    }

    /**
     * Seitliche Abspannleine einer unteren Seitenzelle. Die mittleren Reihen spannen gerade zur Seite; an den Zeltecken
     * setzt die Leine am Giebelende der Traufe an und läuft schräg nach außen, der Hering steht vor bzw. hinter dem
     * Zelt. Die Zellen der Eingangsreihe enthalten "front", die der hinteren Reihe "corner" im Namen.
     */
    static List<JsonObject> sideGuyLine(String name, boolean leftCell) {
        double sign = leftCell ? 1 : -1;
        double outerEdge = leftCell ? 0 : BLOCK;
        double eaveX = outerEdge - sign * TentRoofLogic.EAVE;
        double eaveY = TentRoofLogic.WALL_HEIGHT - TentRoofLogic.EAVE * TentRoofLogic.slope() - TentRoofLogic.THICKNESS;
        double attachZ = BLOCK / 2;
        double stakeZ = BLOCK / 2;
        double stakeOut = STAKE_DISTANCE;
        if (name.contains("front")) {
            attachZ = BLOCK + TentRoofLogic.GABLE_OVERHANG - 0.5;
            stakeZ = attachZ + CORNER_SPREAD;
            stakeOut = STAKE_DISTANCE - 2;
        } else if (name.contains("corner")) {
            attachZ = 0.5 - TentRoofLogic.GABLE_OVERHANG;
            stakeZ = attachZ - CORNER_SPREAD;
            stakeOut = STAKE_DISTANCE - 2;
        }
        return guyLine(outerEdge - sign * stakeOut, 0, stakeZ, eaveX, eaveY, attachZ);
    }

    /**
     * Abspannleine am Giebel, vom Ende der Firststange schräg hinunter zu einem Hering vor bzw. hinter dem Zelt, in
     * Koordinaten der oberen Mittelzelle (der Boden liegt dort bei y = -16).
     */
    static List<JsonObject> gableGuyLine(boolean front) {
        double ridgeEnd = TentRoofLogic.GABLE_OVERHANG + RIDGE_POLE_OVERHANG;
        double attachZ = front ? BLOCK + ridgeEnd : -ridgeEnd;
        double stakeZ = front ? BLOCK + GABLE_STAKE_DISTANCE : -GABLE_STAKE_DISTANCE;
        double attachY = TentRoofLogic.RIDGE_HEIGHT - BLOCK - 1.5;
        return guyLine(BLOCK / 2, -BLOCK, stakeZ, BLOCK / 2, attachY, attachZ);
    }

    /**
     * Holzhering und Leine von seinem Kopf zum Ansatzpunkt. Die Leine ist ein senkrechter Strich auf dem Hering, den
     * Minecraft erst um x neigt und dann um y in Richtung Ansatz dreht (Euler-Drehung: x, dann y, dann z).
     */
    static List<JsonObject> guyLine(
            double stakeX, double groundY, double stakeZ, double attachX, double attachY, double attachZ) {
        double startY = groundY + STAKE_HEIGHT - 0.5;
        double dx = attachX - stakeX;
        double dy = attachY - startY;
        double dz = attachZ - stakeZ;
        double length = Math.sqrt(dx * dx + dy * dy + dz * dz);
        double tilt = Math.toDegrees(Math.acos(dy / length));
        double heading = Math.toDegrees(Math.atan2(dx, dz));

        JsonObject stake =
                box(stakeX - 0.5, groundY, stakeZ - 0.5, stakeX + 0.5, groundY + STAKE_HEIGHT, stakeZ + 0.5, "#stake");
        JsonObject rope =
                box(stakeX - 0.25, startY, stakeZ - 0.25, stakeX + 0.25, startY + length, stakeZ + 0.25, "#rope");
        JsonObject rotation = new JsonObject();
        rotation.add("origin", vec(stakeX, startY, stakeZ));
        rotation.addProperty("x", round(tilt));
        rotation.addProperty("y", round(heading));
        rotation.addProperty("z", 0.0);
        rope.add("rotation", rotation);
        return List.of(stake, rope);
    }

    private static JsonObject box(double x0, double y0, double z0, double x1, double y1, double z1, String texture) {
        JsonObject element = new JsonObject();
        element.add("from", vec(x0, y0, z0));
        element.add("to", vec(x1, y1, z1));
        JsonObject faces = new JsonObject();
        for (String face : FACES) {
            JsonObject f = new JsonObject();
            f.addProperty("texture", texture);
            faces.add(face, f);
        }
        element.add("faces", faces);
        return element;
    }

    /** Längsstange unter dem First, in Koordinaten der oberen Mittelzelle. */
    static JsonObject ridgePole(double zFrom, double zTo) {
        double top = TentRoofLogic.RIDGE_HEIGHT - BLOCK;
        JsonObject element = new JsonObject();
        element.add("from", vec(7.25, top - 1.5, zFrom));
        element.add("to", vec(8.75, top + 0.25, zTo));
        JsonObject faces = new JsonObject();
        for (String face : FACES) {
            JsonObject f = new JsonObject();
            f.addProperty("texture", "#ridge");
            faces.add(face, f);
        }
        element.add("faces", faces);
        return element;
    }

    private static JsonArray vec(double... values) {
        JsonArray array = new JsonArray();
        for (double v : values) array.add(round(v));
        return array;
    }

    private static double round(double v) {
        return Math.round(v * 1000) / 1000.0;
    }
}
