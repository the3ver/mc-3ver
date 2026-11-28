package net.frank.mc3ver.client;

import java.util.List;
import java.util.function.Supplier;
import net.frank.mc3ver.Mc3verMod;
import net.minecraft.client.model.geom.ModelLayerLocation;
import net.minecraft.client.model.geom.builders.CubeListBuilder;
import net.minecraft.resources.Identifier;

/**
 * Aussehen der Bewohnerin: Grundskin, Textur der Zusatzteile und Haarform. Koordinaten passen zu VillagerModel (Kopf
 * x -4..4, y -10..0, z -4..4, Gesicht bei -z).
 */
record VillagerWomanLook(
        String name,
        Identifier skin,
        Identifier layerTexture,
        ModelLayerLocation layer,
        Supplier<CubeListBuilder> hairShape) {

    // Langes Haar hinten mit Zopf
    static final VillagerWomanLook BRAID = of("villager_woman", VillagerWomanLook::braid);
    // Blond, kurzes Haar hinten, zwei seitliche Zöpfe mit Schleife
    static final VillagerWomanLook PIGTAILS = of("villager_woman_pigtails", VillagerWomanLook::pigtails);
    // Dunkles schulterlanges Haar, Haarknoten oben hinten, Blume über dem Ohr
    static final VillagerWomanLook BUN = of("villager_woman_bun", VillagerWomanLook::bun);

    // Reihenfolge fest: der Index kommt aus der UUID, neue Aussehen nur hinten anhängen
    static final List<VillagerWomanLook> ALL = List.of(BRAID, PIGTAILS, BUN);

    private static VillagerWomanLook of(String name, Supplier<CubeListBuilder> hairShape) {
        return new VillagerWomanLook(
                name,
                Mc3verMod.id("textures/entity/villager/" + name + ".png"),
                Mc3verMod.id("textures/entity/villager/" + name + "_layer.png"),
                new ModelLayerLocation(Mc3verMod.id(name), "main"),
                hairShape);
    }

    /** Neuer Builder je Aufruf, CubeListBuilder ist veränderlich. */
    CubeListBuilder hair() {
        return hairShape.get();
    }

    private static CubeListBuilder braid() {
        return CubeListBuilder.create()
                .texOffs(0, 0)
                .addBox(-4.0f, -10.0f, 4.0f, 8.0f, 9.0f, 1.0f)
                .texOffs(20, 0)
                .addBox(-1.0f, -2.0f, 4.5f, 2.0f, 8.0f, 2.0f);
    }

    private static CubeListBuilder pigtails() {
        return CubeListBuilder.create()
                .texOffs(0, 0)
                .addBox(-4.0f, -10.0f, 4.0f, 8.0f, 6.0f, 1.0f)
                .texOffs(20, 0)
                .addBox(-6.0f, -6.0f, 0.0f, 2.0f, 10.0f, 2.0f)
                .texOffs(28, 0)
                .addBox(4.0f, -6.0f, 0.0f, 2.0f, 10.0f, 2.0f);
    }

    private static CubeListBuilder bun() {
        return CubeListBuilder.create()
                .texOffs(0, 0)
                .addBox(-4.0f, -10.0f, 4.0f, 8.0f, 7.0f, 1.0f)
                .texOffs(20, 0)
                .addBox(-2.0f, -11.0f, 4.0f, 4.0f, 4.0f, 3.0f)
                .texOffs(36, 0)
                .addBox(4.0f, -9.0f, -2.0f, 1.0f, 2.0f, 2.0f);
    }
}
