package net.frank.mc3ver.client;

import net.frank.mc3ver.Mc3verMod;
import net.minecraft.client.model.geom.ModelLayerLocation;
import net.minecraft.client.model.geom.builders.CubeListBuilder;
import net.minecraft.resources.Identifier;

/**
 * Aussehen der Bewohnerin: Grundskin, Textur der Zusatzteile und Haarform. Koordinaten passen zu VillagerModel (Kopf
 * x -4..4, y -10..0, z -4..4, Gesicht bei -z).
 */
enum VillagerWomanLook {
    // Langes Haar hinten mit Zopf
    BRAID(
            "villager_woman",
            CubeListBuilder.create()
                    .texOffs(0, 0)
                    .addBox(-4.0f, -10.0f, 4.0f, 8.0f, 9.0f, 1.0f)
                    .texOffs(20, 0)
                    .addBox(-1.0f, -2.0f, 4.5f, 2.0f, 8.0f, 2.0f)),
    // Blond, kurzes Haar hinten, zwei seitliche Zöpfe mit Schleife
    PIGTAILS(
            "villager_woman_pigtails",
            CubeListBuilder.create()
                    .texOffs(0, 0)
                    .addBox(-4.0f, -10.0f, 4.0f, 8.0f, 6.0f, 1.0f)
                    .texOffs(20, 0)
                    .addBox(-6.0f, -6.0f, 0.0f, 2.0f, 10.0f, 2.0f)
                    .texOffs(28, 0)
                    .addBox(4.0f, -6.0f, 0.0f, 2.0f, 10.0f, 2.0f)),
    // Dunkles schulterlanges Haar, Haarknoten oben hinten, Blume über dem Ohr
    BUN(
            "villager_woman_bun",
            CubeListBuilder.create()
                    .texOffs(0, 0)
                    .addBox(-4.0f, -10.0f, 4.0f, 8.0f, 7.0f, 1.0f)
                    .texOffs(20, 0)
                    .addBox(-2.0f, -11.0f, 4.0f, 4.0f, 4.0f, 3.0f)
                    .texOffs(36, 0)
                    .addBox(4.0f, -9.0f, -2.0f, 1.0f, 2.0f, 2.0f));

    final Identifier skin;
    final Identifier layerTexture;
    final ModelLayerLocation layer;
    final CubeListBuilder hair;

    VillagerWomanLook(String name, CubeListBuilder hair) {
        this.skin = Mc3verMod.id("textures/entity/villager/" + name + ".png");
        this.layerTexture = Mc3verMod.id("textures/entity/villager/" + name + "_layer.png");
        this.layer = new ModelLayerLocation(Mc3verMod.id(name), "main");
        this.hair = hair;
    }
}
