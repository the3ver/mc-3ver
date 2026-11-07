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
        if (!roof && side) {
            // Jede untere Seitenzelle bekommt eine Abspannleine von der Traufe zu einem Holzhering
            JsonObject textures = model.getAsJsonObject("textures");
            textures.addProperty("stake", "minecraft:block/stripped_oak_log");
            textures.addProperty("rope", "minecraft:block/white_wool");
            guyLine(cellX == 0).forEach(elements::add);
        }
        if (roof) {
            // Gedrehte Flächen bekommen sonst fleckige Umgebungsverdeckung
            model.addProperty("ambientocclusion", false);
        }
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
     * Holzhering im Boden außerhalb der Seitenwand und eine gedrehte Leine von seinem Kopf zur Traufkante, in
     * Koordinaten der unteren Seitenzelle (Zellmitte in Zeltlängsrichtung).
     */
    static List<JsonObject> guyLine(boolean leftCell) {
        double sign = leftCell ? 1 : -1;
        double outerEdge = leftCell ? 0 : BLOCK;
        double stakeX = outerEdge - sign * STAKE_DISTANCE;
        double startY = STAKE_HEIGHT - 0.5;
        double eaveX = outerEdge - sign * TentRoofLogic.EAVE;
        double eaveY = TentRoofLogic.WALL_HEIGHT - TentRoofLogic.EAVE * TentRoofLogic.slope() - TentRoofLogic.THICKNESS;
        double length = Math.hypot(eaveX - stakeX, eaveY - startY);
        double angle = Math.toDegrees(Math.atan2(eaveY - startY, Math.abs(eaveX - stakeX)));

        JsonObject stake = box(stakeX - 0.5, 0, BLOCK / 2 - 0.5, stakeX + 0.5, STAKE_HEIGHT, BLOCK / 2 + 0.5, "#stake");
        double minX = leftCell ? stakeX : stakeX - length;
        JsonObject rope =
                box(minX, startY - 0.25, BLOCK / 2 - 0.25, minX + length, startY + 0.25, BLOCK / 2 + 0.25, "#rope");
        JsonObject rotation = new JsonObject();
        rotation.add("origin", vec(stakeX, startY, BLOCK / 2));
        rotation.addProperty("axis", "z");
        rotation.addProperty("angle", round(sign * angle));
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
