package net.frank.mc3ver.transport;

import static net.frank.mc3ver.TestResources.json;
import static org.junit.jupiter.api.Assertions.*;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import java.util.ArrayList;
import java.util.List;
import org.junit.jupiter.api.Test;

public class TransportFlameResourcesTest {

    private static final String MODEL = "assets/mc3ver/models/block/transport_flame.json";

    private record Box(double x1, double y1, double z1, double x2, double y2, double z2, boolean stone) {
        boolean covers(double x, double y, double z) {
            return x > x1 && x < x2 && y > y1 && y < y2 && z > z1 && z < z2;
        }
    }

    private static List<Box> boxes() throws Exception {
        List<Box> boxes = new ArrayList<>();
        for (JsonElement element : json(MODEL).getAsJsonArray("elements")) {
            JsonObject e = element.getAsJsonObject();
            JsonArray f = e.getAsJsonArray("from");
            JsonArray t = e.getAsJsonArray("to");
            boolean stone = e.getAsJsonObject("faces").entrySet().stream()
                    .allMatch(face -> face.getValue()
                            .getAsJsonObject()
                            .get("texture")
                            .getAsString()
                            .equals("#stone"));
            boxes.add(new Box(
                    f.get(0).getAsDouble(),
                    f.get(1).getAsDouble(),
                    f.get(2).getAsDouble(),
                    t.get(0).getAsDouble(),
                    t.get(1).getAsDouble(),
                    t.get(2).getAsDouble(),
                    stone));
        }
        return boxes;
    }

    private static boolean stoneAt(List<Box> boxes, double x, double y, double z) {
        return boxes.stream().anyMatch(b -> b.stone() && b.covers(x, y, z));
    }

    @Test
    void plateUsesCobblestoneLikeTheRecipe() throws Exception {
        assertEquals(
                "minecraft:block/cobblestone",
                json(MODEL).getAsJsonObject("textures").get("stone").getAsString());
    }

    // Rund heißt: entlang der Achsen reicht die Platte weiter hinaus als in den Ecken
    @Test
    void plateIsRound() throws Exception {
        List<Box> boxes = boxes();
        double y = 1;
        for (double[] edge : new double[][] {{2.5, 8}, {13.5, 8}, {8, 2.5}, {8, 13.5}}) {
            assertTrue(stoneAt(boxes, edge[0], y, edge[1]), "Platte fehlt am Rand " + edge[0] + "," + edge[1]);
        }
        for (double[] corner : new double[][] {{2.5, 2.5}, {13.5, 2.5}, {2.5, 13.5}, {13.5, 13.5}}) {
            assertFalse(stoneAt(boxes, corner[0], y, corner[1]), "Ecke " + corner[0] + "," + corner[1] + " eckig");
        }
    }

    @Test
    void torchStandsOnThePlate() throws Exception {
        List<Box> boxes = boxes();
        Box torch = boxes.stream().filter(b -> !b.stone()).findFirst().orElseThrow();
        double plateTop = boxes.stream()
                .filter(b -> b.stone() && b.covers(8, b.y1() + 0.5, 8))
                .mapToDouble(Box::y2)
                .max()
                .orElseThrow();
        assertTrue(plateTop > 0);
        assertEquals(plateTop, torch.y1());
    }

    @Test
    void itemShowsThePlacedModel() throws Exception {
        assertEquals(
                "mc3ver:block/transport_flame",
                json("assets/mc3ver/models/item/transport_flame.json")
                        .get("parent")
                        .getAsString());
    }
}
