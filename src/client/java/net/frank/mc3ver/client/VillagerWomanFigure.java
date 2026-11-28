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
    private static final String ARMS = "arms";
    private static final String SIDES = "mc3ver_woman_arms_sides";
    private static final String HIPS = "mc3ver_woman_arms_hips";

    /** Vorschlag zur Armhaltung, nur zum Vergleichen per Screenshot (Branch explore/villager-woman-arms). */
    public enum ArmStyle {
        CROSSED,
        HIGH,
        SIDES,
        HIPS
    }

    /** Vorschlag zur Brust: nur Schattenstrich (Textur), kleine oder runde Wölbung. */
    public enum BustStyle {
        NONE,
        TEXTURE,
        SMALL,
        ROUND,
        // Rund, 1 Pixel tiefer, mit Schattenstrich darunter und Ausschnitt oben (Strich im Layer)
        COMBINED,
        // Rund, 1 Pixel tiefer, ohne Striche
        LOW
    }

    /** Vorschlag zur Nase: Vanilla 4 Pixel lang, gekürzt auf 3 oder 2. */
    public enum NoseStyle {
        VANILLA,
        THREE,
        TWO
    }

    public static volatile NoseStyle noseStyle = NoseStyle.VANILLA;
    private static final String HEAD = "head";
    private static final String NOSE = "nose";
    private static final String NOSE_THREE = "mc3ver_woman_nose_three";
    private static final String NOSE_TWO = "mc3ver_woman_nose_two";

    // Vorschau: der Gametest schaltet zwischen den Screenshots um
    public static volatile ArmStyle armStyle = ArmStyle.CROSSED;
    public static volatile BustStyle bustStyle = BustStyle.NONE;
    private static final String BUST_SMALL = "mc3ver_woman_bust_small";
    private static final String BUST_ROUND = "mc3ver_woman_bust_round";
    private static final String BUST_LOW = "mc3ver_woman_bust_low";
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
        // Brust auf dem Gewand, Texturzeilen 46..49 der Gewand-Vorderseite, damit Berufskleidung mitgeht
        PartDefinition jacket = body.getChild(JACKET);
        jacket.addOrReplaceChild(
                BUST_SMALL,
                CubeListBuilder.create()
                        .texOffs(6, 46)
                        .addBox(-3.0f, 3.0f, -4.0f, 6.0f, 2.0f, 1.0f, new CubeDeformation(0.5f)),
                PartPose.ZERO);
        jacket.addOrReplaceChild(
                BUST_ROUND,
                CubeListBuilder.create()
                        .texOffs(6, 45)
                        .addBox(-3.0f, 2.5f, -4.0f, 6.0f, 3.0f, 1.0f, new CubeDeformation(0.5f))
                        .texOffs(7, 46)
                        .addBox(-2.0f, 3.0f, -5.0f, 4.0f, 2.0f, 1.0f, new CubeDeformation(0.5f)),
                PartPose.ZERO);
        jacket.addOrReplaceChild(
                BUST_LOW,
                CubeListBuilder.create()
                        .texOffs(6, 46)
                        .addBox(-3.0f, 3.5f, -4.0f, 6.0f, 3.0f, 1.0f, new CubeDeformation(0.5f))
                        .texOffs(7, 47)
                        .addBox(-2.0f, 4.0f, -5.0f, 4.0f, 2.0f, 1.0f, new CubeDeformation(0.5f)),
                PartPose.ZERO);
        addArms(root);
        // Nase wie VillagerModel: texOffs(24,0), 2x4x2 ab y -3; kürzer = obere Zeilen derselben Textur
        PartDefinition head = root.getChild(HEAD);
        head.addOrReplaceChild(
                NOSE_THREE,
                CubeListBuilder.create().texOffs(24, 0).addBox(-1.0f, -1.0f, -6.0f, 2.0f, 3.0f, 2.0f),
                PartPose.offset(0.0f, -2.0f, 0.0f));
        head.addOrReplaceChild(
                NOSE_TWO,
                CubeListBuilder.create().texOffs(24, 0).addBox(-1.0f, -1.0f, -6.0f, 2.0f, 2.0f, 2.0f),
                PartPose.offset(0.0f, -2.0f, 0.0f));
    }

    // Arm-Texturbereich wie VillagerModel: texOffs(44,22), 4x8x4; hier 3 dick, damit die Figur schmal bleibt
    private static void addArms(PartDefinition root) {
        PartDefinition sides = root.addOrReplaceChild(SIDES, CubeListBuilder.create(), PartPose.ZERO);
        PartDefinition hips = root.addOrReplaceChild(HIPS, CubeListBuilder.create(), PartPose.ZERO);
        for (int side : new int[] {1, -1}) {
            String name = side > 0 ? "left" : "right";
            // Hängend, leicht vom Körper weg
            sides.addOrReplaceChild(
                    name,
                    CubeListBuilder.create()
                            .texOffs(44, 22)
                            .mirror(side < 0)
                            .addBox(-1.5f, 0.0f, -1.5f, 3.0f, 8.0f, 3.0f)
                            .texOffs(0, 12)
                            .addBox(-1.5f, 8.0f, -1.5f, 3.0f, 2.0f, 3.0f),
                    PartPose.offsetAndRotation(6.0f * side, 0.5f, 0.0f, 0.0f, 0.0f, -0.12f * side));
            // In die Hüften gestemmt: Oberarm schräg nach außen, Unterarm zurück zur Taille
            PartDefinition upper = hips.addOrReplaceChild(
                    name,
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
        ArmStyle style = figure ? armStyle : ArmStyle.CROSSED;
        ModelPart arms = root.getChild(ARMS);
        arms.visible = style == ArmStyle.CROSSED || style == ArmStyle.HIGH;
        if (style == ArmStyle.HIGH) {
            // Verschränkt, aber höher und schmaler: unter der Brust, Taille darunter frei
            arms.y -= 1.0f;
            arms.xRot = -1.05f;
            arms.xScale = 0.8f;
        }
        root.getChild(SIDES).visible = style == ArmStyle.SIDES;
        root.getChild(HIPS).visible = style == ArmStyle.HIPS;
        BustStyle bust = figure ? bustStyle : BustStyle.NONE;
        jacket.getChild(BUST_SMALL).visible = bust == BustStyle.SMALL;
        jacket.getChild(BUST_ROUND).visible = bust == BustStyle.ROUND;
        jacket.getChild(BUST_LOW).visible = bust == BustStyle.COMBINED || bust == BustStyle.LOW;
        NoseStyle nose = figure ? noseStyle : NoseStyle.VANILLA;
        ModelPart head = root.getChild(HEAD);
        head.getChild(NOSE).visible = nose == NoseStyle.VANILLA;
        head.getChild(NOSE_THREE).visible = nose == NoseStyle.THREE;
        head.getChild(NOSE_TWO).visible = nose == NoseStyle.TWO;
    }
}
