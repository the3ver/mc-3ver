package net.frank.mc3ver.tent;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class TentPennantTest {

    private static final java.nio.file.Path ASSETS =
        java.nio.file.Paths.get("").toAbsolutePath().resolve("src/main/resources/assets/mc3ver");

    private static java.awt.image.BufferedImage texture() throws Exception {
        return javax.imageio.ImageIO.read(ASSETS.resolve("textures/block/tent_pennant.png").toFile());
    }

    @Test
    void pennantTextureIsFullyOpaque() throws Exception {
        // Transparenz im Wimpel wirkt ingame durchsichtig, die Form kommt aus dem Modell
        java.awt.image.BufferedImage img = texture();
        for (int y = 0; y < img.getHeight(); y++) {
            for (int x = 0; x < img.getWidth(); x++) {
                assertEquals(255, (img.getRGB(x, y) >>> 24) & 0xFF, "Pixel " + x + "," + y + " muss deckend sein");
            }
        }
    }

    @Test
    void pennantAnimationVisiblyChangesEveryFrame() throws Exception {
        java.awt.image.BufferedImage img = texture();
        int frames = img.getHeight() / 16;
        assertTrue(frames >= 6, "Flattern braucht mindestens 6 Frames, waren " + frames);
        for (int f = 0; f < frames; f++) {
            int next = (f + 1) % frames;
            int changed = 0;
            for (int y = 0; y < 16; y++) {
                for (int x = 0; x < 16; x++) {
                    if (img.getRGB(x, f * 16 + y) != img.getRGB(x, next * 16 + y)) changed++;
                }
            }
            assertTrue(changed >= 64, "Frame " + f + " -> " + next + " ändert nur " + changed + " von 256 Pixeln");
        }
    }

    @Test
    void pennantTapersFromPoleToTip() throws Exception {
        com.google.gson.JsonObject model = com.google.gson.JsonParser.parseString(
            java.nio.file.Files.readString(ASSETS.resolve("models/block/tent_canvas_roof_front.json"))).getAsJsonObject();
        java.util.List<double[]> strips = new java.util.ArrayList<>();
        for (com.google.gson.JsonElement el : model.getAsJsonArray("elements")) {
            com.google.gson.JsonObject e = el.getAsJsonObject();
            if (!e.toString().contains("#pennant")) continue;
            com.google.gson.JsonArray from = e.getAsJsonArray("from");
            com.google.gson.JsonArray to = e.getAsJsonArray("to");
            strips.add(new double[]{from.get(0).getAsDouble(), to.get(0).getAsDouble(), to.get(1).getAsDouble() - from.get(1).getAsDouble()});
            for (String face : e.getAsJsonObject("faces").keySet()) {
                assertTrue(e.getAsJsonObject("faces").getAsJsonObject(face).has("tintindex"), "Wimpel in Zeltfarbe: " + face);
            }
        }
        assertTrue(strips.size() >= 4, "Wimpel besteht aus mindestens 4 gestuften Streifen");
        strips.sort(java.util.Comparator.comparingDouble(s -> s[0]));
        assertEquals(8.5, strips.get(0)[0], 1e-9, "Wimpel beginnt an der Stange");
        assertEquals(16.0, strips.get(strips.size() - 1)[1], 1e-9, "Spitze endet am Blockrand");
        for (int i = 1; i < strips.size(); i++) {
            assertEquals(strips.get(i - 1)[1], strips.get(i)[0], 1e-9, "Streifen schließen lückenlos an");
            assertTrue(strips.get(i)[2] < strips.get(i - 1)[2], "Wimpel wird zur Spitze hin schmaler");
        }
    }
}
