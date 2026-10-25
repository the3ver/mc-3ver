package net.frank.mc3ver.client;

import com.mojang.blaze3d.vertex.PoseStack;
import net.frank.mc3ver.Mc3verMod;
import net.frank.mc3ver.bonfire.BonfireBlock;
import net.frank.mc3ver.bonfire.BonfireBlockEntity;
import net.frank.mc3ver.bonfire.BonfireLogic;
import net.minecraft.client.Minecraft;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.blockentity.BeaconRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.state.BlockEntityRenderState;
import net.minecraft.client.renderer.feature.ModelFeatureRenderer;
import net.minecraft.client.renderer.state.level.CameraRenderState;
import net.minecraft.resources.Identifier;
import net.minecraft.world.phys.Vec3;

// Rauchsäule über dem Signalfeuer. Nutzt den Beacon-Strahl, weil der über die volle Render-Distanz gezeichnet wird.
public class BonfireRenderer implements BlockEntityRenderer<BonfireBlockEntity, BonfireRenderer.State> {

    private static final Identifier SMOKE_TEXTURE = Mc3verMod.id("textures/entity/bonfire_smoke.png");
    private static final float SOLID_RADIUS = 0.45f;
    private static final float GLOW_RADIUS = 0.7f;

    public static class State extends BlockEntityRenderState {
        boolean lit;
        float animationTime;
        float radiusScale;
    }

    @Override
    public State createRenderState() {
        return new State();
    }

    @Override
    public void extractRenderState(BonfireBlockEntity bonfire, State state, float partialTick, Vec3 cameraPos,
                                   ModelFeatureRenderer.CrumblingOverlay breakProgress) {
        BlockEntityRenderer.super.extractRenderState(bonfire, state, partialTick, cameraPos, breakProgress);
        state.lit = bonfire.getBlockState().getValue(BonfireBlock.LIT);
        state.animationTime = bonfire.getLevel() != null
            ? Math.floorMod(bonfire.getLevel().getGameTime(), 40) + partialTick
            : 0.0f;
        double distance = Vec3.atCenterOf(state.blockPos).subtract(cameraPos).horizontalDistance();
        LocalPlayer player = Minecraft.getInstance().player;
        state.radiusScale = BonfireLogic.smokeRadiusScale(distance, player != null && player.isScoping());
    }

    @Override
    public void submit(State state, PoseStack poseStack, SubmitNodeCollector collector, CameraRenderState camera) {
        if (!state.lit) {
            return;
        }
        BeaconRenderer.submitBeaconBeam(poseStack, collector, SMOKE_TEXTURE, 1.0f, state.animationTime,
            1, BonfireLogic.SMOKE_COLUMN_HEIGHT, BonfireLogic.SMOKE_COLOR,
            SOLID_RADIUS * state.radiusScale, GLOW_RADIUS * state.radiusScale);
    }

    @Override
    public boolean shouldRenderOffScreen() {
        return true;
    }

    @Override
    public int getViewDistance() {
        return Minecraft.getInstance().options.getEffectiveRenderDistance() * 16;
    }

    @Override
    public boolean shouldRender(BonfireBlockEntity bonfire, Vec3 cameraPos) {
        return Vec3.atCenterOf(bonfire.getBlockPos()).multiply(1.0, 0.0, 1.0)
            .closerThan(cameraPos.multiply(1.0, 0.0, 1.0), getViewDistance());
    }
}
