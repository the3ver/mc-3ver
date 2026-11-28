package net.frank.mc3ver.client;

import net.minecraft.client.model.EntityModel;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.PartPose;
import net.minecraft.client.model.geom.builders.CubeDeformation;
import net.minecraft.client.model.geom.builders.CubeListBuilder;
import net.minecraft.client.model.geom.builders.LayerDefinition;
import net.minecraft.client.model.geom.builders.MeshDefinition;
import net.minecraft.client.model.geom.builders.PartDefinition;
import net.minecraft.client.renderer.entity.state.VillagerRenderState;
import net.minecraft.util.Mth;

/**
 * Zusatzteile der Bewohnerin über dem Vanilla-Villagermodell: langes Haar und Zopf am Hinterkopf, ein Rock über dem
 * unteren Gewand. Die Koordinaten passen zu VillagerModel (Kopf -10..0, Gewand bis y 17.5).
 */
final class VillagerWomanModel extends EntityModel<VillagerRenderState> {

    private final ModelPart head;

    VillagerWomanModel(ModelPart root) {
        super(root);
        this.head = root.getChild("head");
    }

    static LayerDefinition createLayer(VillagerWomanLook look) {
        MeshDefinition mesh = new MeshDefinition();
        PartDefinition root = mesh.getRoot();
        root.addOrReplaceChild("head", look.hair(), PartPose.ZERO);
        // Etwas Aufschlag, sonst liegt der Rock genau auf der Hüfte der Figur (VillagerWomanFigure) und flimmert
        root.addOrReplaceChild(
                "skirt",
                CubeListBuilder.create()
                        .texOffs(0, 12)
                        .addBox(-5.0f, 12.0f, -4.0f, 10.0f, 8.0f, 8.0f, new CubeDeformation(0.3f)),
                PartPose.ZERO);
        return LayerDefinition.create(mesh, 64, 32);
    }

    // Gerendert wird verzögert: ModelFeatureRenderer ruft erst beim Zeichnen setupAnim(state), das die Pose
    // zurücksetzt. Den Kopf deshalb wie VillagerModel aus dem State drehen, eine vorher kopierte Pose ginge verloren.
    @Override
    public void setupAnim(VillagerRenderState state) {
        super.setupAnim(state);
        head.yRot = state.yRot * Mth.DEG_TO_RAD;
        head.xRot = state.xRot * Mth.DEG_TO_RAD;
        if (state.isUnhappy) {
            head.zRot = 0.3f * Mth.sin(0.45f * state.ageInTicks);
            head.xRot = 0.4f;
        }
    }
}
