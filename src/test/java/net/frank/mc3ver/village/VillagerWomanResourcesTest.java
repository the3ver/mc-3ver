package net.frank.mc3ver.village;

import static net.frank.mc3ver.TestResources.image;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.awt.image.BufferedImage;
import org.junit.jupiter.api.Test;

class VillagerWomanResourcesTest {

    private static final String DIR = "assets/mc3ver/textures/entity/villager/";

    private static void assertSize(String file, int width, int height) throws Exception {
        BufferedImage img = image(DIR + file);
        assertNotNull(img, file + " fehlt");
        assertEquals(width, img.getWidth(), file);
        assertEquals(height, img.getHeight(), file);
    }

    @Test
    void skinsMatchTheVanillaVillagerLayout() throws Exception {
        assertSize("villager_woman.png", 64, 64);
        assertSize("villager_woman_baby.png", 64, 64);
    }

    // Augen der Bewohnerin (und damit der Bäckerin): je ein Pixel in Zeile 4 der Gesichtsvorderseite
    @Test
    void eyesAreBrightGreen() throws Exception {
        assertGreen("villager_woman.png", 10, 12);
        assertGreen("villager_woman.png", 13, 12);
        assertGreen("villager_woman_baby.png", 9, 11);
        assertGreen("villager_woman_baby.png", 12, 11);
    }

    private static void assertGreen(String file, int x, int y) throws Exception {
        int rgb = image(DIR + file).getRGB(x, y);
        int r = (rgb >> 16) & 255;
        int g = (rgb >> 8) & 255;
        int b = rgb & 255;
        assertTrue(
                g >= 160 && g - r >= 80 && g - b >= 60,
                file + " Auge bei " + x + "," + y + ": " + r + "," + g + "," + b);
    }

    @Test
    void braidAndSkirtLayerMatchesItsModel() throws Exception {
        assertSize("villager_woman_layer.png", 64, 32);
    }
}
