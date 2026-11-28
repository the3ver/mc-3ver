package net.frank.mc3ver.client;

import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.PartPose;
import net.minecraft.client.model.geom.builders.CubeDeformation;
import net.minecraft.client.model.geom.builders.CubeListBuilder;
import net.minecraft.client.model.geom.builders.PartDefinition;
import net.minecraft.client.renderer.entity.state.VillagerRenderState;

/**
 * Figur der Bewohnerin im Vanilla-Villagermodell. Körper und Gewand (jacket) sind gerade Quader; für erwachsene
 * Bewohnerinnen blendet skipDraw sie aus und Figur-Teile darunter übernehmen: schmale Taille, Brust, darunter setzt
 * der Rock aus VillagerWomanModel als Hüfte an. Dazu Hände in die Hüften statt verschränkter Arme und eine kürzere
 * Nase. Alle Teile liegen auf denselben Texturzeilen wie das Vanilla-Teil, damit Grundskin, Biom- und Berufskleidung
 * weiter passen.
 */
public final class VillagerWomanFigure {

    static final String FIGURE = "mc3ver_woman_figure";
    static final String BUST = "mc3ver_woman_bust";
    static final String HIP_ARMS = "mc3ver_woman_hip_arms";
    static final String NOSE = "mc3ver_woman_nose";
    private static final String BODY = "body";
    private static final String JACKET = "jacket";
    private static final String ARMS = "arms";
    private static final String HEAD = "head";
    private static final String VANILLA_NOSE = "nose";

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
        // Gewand: texOffs(0,38), 8x20x6 mit 0.5 Aufschlag. Oben und unten exakt, dazwischen die Taille
        PartDefinition jacket = body.getChild(JACKET);
        jacket.addOrReplaceChild(
                FIGURE,
                CubeListBuilder.create()
                        .texOffs(0, 38)
                        .addBox(-4.0f, 0.0f, -3.0f, 8.0f, 7.0f, 6.0f, new CubeDeformation(0.5f))
                        .texOffs(2, 46)
                        .addBox(-3.0f, 7.0f, -2.5f, 6.0f, 4.0f, 5.0f, new CubeDeformation(0.5f))
                        .texOffs(0, 49)
                        .addBox(-4.0f, 11.0f, -3.0f, 8.0f, 9.0f, 6.0f, new CubeDeformation(0.5f)),
                PartPose.ZERO);
        // Brust: zwei Stufen auf der Gewand-Vorderseite (Zeilen 46..49), damit Berufskleidung mitgeht. Tiefer wirkt
        // sie wie ein Schild um den Hals
        jacket.addOrReplaceChild(
                BUST,
                CubeListBuilder.create()
                        .texOffs(6, 45)
                        .addBox(-3.0f, 2.5f, -4.0f, 6.0f, 3.0f, 1.0f, new CubeDeformation(0.5f))
                        .texOffs(7, 46)
                        .addBox(-2.0f, 3.0f, -5.0f, 4.0f, 2.0f, 1.0f, new CubeDeformation(0.5f)),
                PartPose.ZERO);
        addHipArms(root);
        // Nase wie VillagerModel (texOffs(24,0), 2x4x2 ab y -3), aber nur 2 Pixel lang: obere Zeilen derselben Textur
        root.getChild(HEAD)
                .addOrReplaceChild(
                        NOSE,
                        CubeListBuilder.create().texOffs(24, 0).addBox(-1.0f, -1.0f, -6.0f, 2.0f, 2.0f, 2.0f),
                        PartPose.offset(0.0f, -2.0f, 0.0f));
    }

    // Hände in die Hüften: Oberarm schräg nach außen, Unterarm zurück zur Taille. Ärmel aus dem Arm-Bereich
    // texOffs(44,22), 3 statt 4 dick; Hände aus der Haut der Kopfseite (Zeilen 14..17)
    private static void addHipArms(PartDefinition root) {
        PartDefinition arms = root.addOrReplaceChild(HIP_ARMS, CubeListBuilder.create(), PartPose.ZERO);
        for (int side : new int[] {1, -1}) {
            PartDefinition upper = arms.addOrReplaceChild(
                    side > 0 ? "left" : "right",
                    CubeListBuilder.create()
                            .texOffs(44, 22)
                            .mirror(side < 0)
                            .addBox(-1.5f, 0.0f, -1.5f, 3.0f, 5.0f, 3.0f),
                    PartPose.offsetAndRotation(5.5f * side, 0.5f, 0.0f, 0.0f, 0.0f, -0.75f * side));
            upper.addOrReplaceChild(
                    "forearm",
                    CubeListBuilder.create()
                            .texOffs(44, 22)
                            .mirror(side < 0)
                            .addBox(-1.5f, 0.0f, -1.5f, 3.0f, 6.0f, 3.0f)
                            .texOffs(0, 12)
                            .addBox(-1.5f, 6.0f, -1.5f, 3.0f, 2.0f, 3.0f),
                    PartPose.offsetAndRotation(0.0f, 5.0f, 0.0f, 0.0f, 0.0f, 1.6f * side));
        }
    }

    /** Schaltet zwischen Vanilla und Figur um; jedes setupAnim, weil ein Modell alle Dorfbewohner zeichnet. */
    public static void apply(ModelPart root, VillagerRenderState state) {
        ModelPart body = root.getChild(BODY);
        if (!body.hasChild(FIGURE)) return;
        ModelPart jacket = body.getChild(JACKET);
        ModelPart head = root.getChild(HEAD);
        boolean figure = !state.isBaby && VillagerWomanClient.isFemale(state);
        // Ware beim Handeln liegt in den verschränkten Armen (CrossedArmsItemLayer), dann bleiben die stehen
        boolean hipArms = figure && state.heldItem.isEmpty();
        body.skipDraw = figure;
        jacket.skipDraw = figure;
        body.getChild(FIGURE).visible = figure;
        jacket.getChild(FIGURE).visible = figure;
        jacket.getChild(BUST).visible = figure;
        head.getChild(VANILLA_NOSE).visible = !figure;
        head.getChild(NOSE).visible = figure;
        root.getChild(ARMS).visible = !hipArms;
        root.getChild(HIP_ARMS).visible = hipArms;
    }
}
