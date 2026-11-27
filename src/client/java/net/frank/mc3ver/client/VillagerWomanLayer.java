package net.frank.mc3ver.client;

import com.mojang.blaze3d.vertex.PoseStack;
import java.util.Map;
import net.minecraft.client.model.npc.VillagerModel;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.entity.RenderLayerParent;
import net.minecraft.client.renderer.entity.layers.RenderLayer;
import net.minecraft.client.renderer.entity.state.VillagerRenderState;

final class VillagerWomanLayer extends RenderLayer<VillagerRenderState, VillagerModel> {

    private static final int WHITE = -1;
    private static final int ORDER = 1;

    private final Map<VillagerWomanLook, VillagerWomanModel> models;

    VillagerWomanLayer(
            RenderLayerParent<VillagerRenderState, VillagerModel> parent,
            Map<VillagerWomanLook, VillagerWomanModel> models) {
        super(parent);
        this.models = models;
    }

    @Override
    public void submit(
            PoseStack poseStack,
            SubmitNodeCollector collector,
            int lightCoords,
            VillagerRenderState state,
            float yRot,
            float xRot) {
        // Babys haben ein anderes Modell, sie bekommen nur den eigenen Skin
        if (state.isBaby || state.isInvisible || !VillagerWomanClient.isFemale(state)) return;
        VillagerWomanLook look = VillagerWomanClient.look(state);
        renderColoredCutoutModel(
                models.get(look), look.layerTexture, poseStack, collector, lightCoords, state, WHITE, ORDER);
    }
}
