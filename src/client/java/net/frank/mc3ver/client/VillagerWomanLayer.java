package net.frank.mc3ver.client;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.model.npc.VillagerModel;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.entity.RenderLayerParent;
import net.minecraft.client.renderer.entity.layers.RenderLayer;
import net.minecraft.client.renderer.entity.state.VillagerRenderState;

final class VillagerWomanLayer extends RenderLayer<VillagerRenderState, VillagerModel> {

    private static final int WHITE = -1;
    private static final int ORDER = 1;

    private final VillagerWomanModel model;

    VillagerWomanLayer(RenderLayerParent<VillagerRenderState, VillagerModel> parent, VillagerWomanModel model) {
        super(parent);
        this.model = model;
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
        renderColoredCutoutModel(
                model, VillagerWomanClient.LAYER_TEXTURE, poseStack, collector, lightCoords, state, WHITE, ORDER);
    }
}
