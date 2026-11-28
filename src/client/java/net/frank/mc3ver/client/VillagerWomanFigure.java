package net.frank.mc3ver.client;

import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.PartPose;
import net.minecraft.client.model.geom.builders.CubeDeformation;
import net.minecraft.client.model.geom.builders.CubeListBuilder;
import net.minecraft.client.model.geom.builders.PartDefinition;
import net.minecraft.client.renderer.entity.state.VillagerRenderState;

/**
 * Figur der Bewohnerin im Vanilla-Villagermodell: schmale Taille, darunter setzt der Rock als Hüfte an. Körper und
 * Gewand (jacket) sind gerade Quader; für Bewohnerinnen blendet skipDraw sie aus und die Figur-Teile darunter
 * übernehmen. Die Teile liegen auf denselben Texturzeilen wie Körper und Gewand, damit Grundskin, Biom- und
 * Berufskleidung weiter passen.
 */
public final class VillagerWomanFigure {

    static final String FIGURE = "mc3ver_woman_figure";
    private static final String BODY = "body";
    private static final String JACKET = "jacket";

    private VillagerWomanFigure() {}

    /** Ergänzt das Mesh aus VillagerModel.createBodyModel um die Figur-Teile. */
    public static void addTo(PartDefinition root) {
        PartDefinition body = root.getChild(BODY);
        // Körper: texOffs(16,20), 8x12x6. Brust und Hüftsaum exakt, Taille 6x4x5 auf den Zeilen 33..37
        body.addOrReplaceChild(
                FIGURE,
                CubeListBuilder.create()
                        .texOffs(16, 20)
                        .addBox(-4.0f, 0.0f, -3.0f, 8.0f, 7.0f, 6.0f)
                        .texOffs(18, 28)
                        .addBox(-3.0f, 7.0f, -2.5f, 6.0f, 4.0f, 5.0f)
                        .texOffs(16, 31)
                        .addBox(-4.0f, 11.0f, -3.0f, 8.0f, 1.0f, 6.0f),
                PartPose.ZERO);
        // Gewand: texOffs(0,38), 8x20x6 mit 0.5 Aufschlag. Oben und unten exakt, dazwischen die Taille.
        // Die Hüfte bildet der Rock aus VillagerWomanModel
        body.getChild(JACKET)
                .addOrReplaceChild(
                        FIGURE,
                        CubeListBuilder.create()
                                .texOffs(0, 38)
                                .addBox(-4.0f, 0.0f, -3.0f, 8.0f, 7.0f, 6.0f, new CubeDeformation(0.5f))
                                .texOffs(2, 46)
                                .addBox(-3.0f, 7.0f, -2.5f, 6.0f, 4.0f, 5.0f, new CubeDeformation(0.5f))
                                .texOffs(0, 49)
                                .addBox(-4.0f, 11.0f, -3.0f, 8.0f, 9.0f, 6.0f, new CubeDeformation(0.5f)),
                        PartPose.ZERO);
    }

    /** Schaltet zwischen geradem Körper und Figur um; jedes setupAnim, weil ein Modell alle Dorfbewohner zeichnet. */
    public static void apply(ModelPart root, VillagerRenderState state) {
        ModelPart body = root.getChild(BODY);
        if (!body.hasChild(FIGURE)) return;
        ModelPart jacket = body.getChild(JACKET);
        boolean figure = !state.isBaby && VillagerWomanClient.isFemale(state);
        body.skipDraw = figure;
        jacket.skipDraw = figure;
        body.getChild(FIGURE).visible = figure;
        jacket.getChild(FIGURE).visible = figure;
    }
}
