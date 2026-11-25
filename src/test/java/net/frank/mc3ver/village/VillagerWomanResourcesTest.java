package net.frank.mc3ver.village;

import static net.frank.mc3ver.TestResources.image;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

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

    @Test
    void braidAndSkirtLayerMatchesItsModel() throws Exception {
        assertSize("villager_woman_layer.png", 64, 32);
    }
}
