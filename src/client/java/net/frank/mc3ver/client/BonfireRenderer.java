package net.frank.mc3ver.client;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.Axis;
import net.frank.mc3ver.Mc3verMod;
import net.frank.mc3ver.bonfire.BonfireBlock;
import net.frank.mc3ver.bonfire.BonfireBlockEntity;
import net.frank.mc3ver.bonfire.BonfireLogic;
import net.minecraft.client.Minecraft;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.state.BlockEntityRenderState;
import net.minecraft.client.renderer.feature.ModelFeatureRenderer;
import net.minecraft.client.renderer.rendertype.RenderType;
import net.minecraft.client.renderer.rendertype.RenderTypes;
import net.minecraft.client.renderer.state.level.CameraRenderState;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.resources.Identifier;
import net.minecraft.util.LightCoordsUtil;
import net.minecraft.world.phys.Vec3;

// Rauchfahne über dem Signalfeuer: zur Kamera gedrehte Wolken, die aufsteigen, wachsen, verblassen und im Wind treiben.
// Wird wie der Beacon-Strahl über die volle Render-Distanz gezeichnet.
public class BonfireRenderer implements BlockEntityRenderer<BonfireBlockEntity, BonfireRenderer.State> {

    private static final Identifier SMOKE_TEXTURE = Mc3verMod.id("textures/entity/bonfire_smoke.png");
    private static final RenderType SMOKE_RENDER_TYPE = RenderTypes.entityTranslucent(SMOKE_TEXTURE);
    private static final int SMOKE_GRAY = 0xD8D8D8;
    // Vanilla-Lagerfeuerflamme als eigenständige Textur; das Animationsbild wählt BonfireLogic.flameTongue
    private static final RenderType FLAME_RENDER_TYPE =
        RenderTypes.entityTranslucentEmissive(Identifier.withDefaultNamespace("textures/block/campfire_fire.png"));
    private static final float FLAME_BASE_Y = 0.2f;

    public static class State extends BlockEntityRenderState {
        boolean lit;
        double time;
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
        state.time = bonfire.getLevel() != null ? bonfire.getLevel().getGameTime() + (double) partialTick : 0.0;
        double distance = Vec3.atCenterOf(state.blockPos).subtract(cameraPos).horizontalDistance();
        LocalPlayer player = Minecraft.getInstance().player;
        state.radiusScale = BonfireLogic.smokeRadiusScale(distance, player != null && player.isScoping());
    }

    @Override
    public void submit(State state, PoseStack poseStack, SubmitNodeCollector collector, CameraRenderState camera) {
        if (!state.lit) {
            return;
        }
        submitFlames(state, poseStack, collector);

        BonfireLogic.Wind wind = BonfireLogic.windAt(state.time);
        int skyLight = LightCoordsUtil.sky(state.lightCoords);

        for (int i = 0; i < BonfireLogic.PLUME_PUFFS; i++) {
            BonfireLogic.Puff puff = BonfireLogic.plumePuff(i, state.time, wind);
            int alpha = Math.round(puff.alpha() * 255.0f);
            if (alpha < 4) {
                continue;
            }
            // Unten vom Feuer angestrahlt, weiter oben nur noch vom Himmel
            int blockLight = Math.max(0, 15 - (int) (puff.y() / 3.0));
            int light = LightCoordsUtil.pack(blockLight, skyLight);
            int color = (alpha << 24) | SMOKE_GRAY;
            float size = (float) puff.size() * state.radiusScale;
            float u0 = (puff.variant() % 2) * 0.5f;
            float v0 = (puff.variant() / 2) * 0.5f;

            poseStack.pushPose();
            poseStack.translate(0.5 + puff.x(), puff.y(), 0.5 + puff.z());
            poseStack.mulPose(camera.orientation);
            poseStack.mulPose(Axis.ZP.rotationDegrees(puff.roll()));
            poseStack.scale(size, size, size);
            collector.submitCustomGeometry(poseStack, SMOKE_RENDER_TYPE, (pose, consumer) -> {
                vertex(consumer, pose, -0.5f, -0.5f, 0.0f, u0 + 0.5f, v0 + 0.5f, color, light);
                vertex(consumer, pose, 0.5f, -0.5f, 0.0f, u0, v0 + 0.5f, color, light);
                vertex(consumer, pose, 0.5f, 0.5f, 0.0f, u0, v0, color, light);
                vertex(consumer, pose, -0.5f, 0.5f, 0.0f, u0 + 0.5f, v0, color, light);
            });
            poseStack.popPose();
        }
    }

    // Je Zunge zwei gekreuzte, beidseitige Flächen; leuchten voll und wachsen mit der Entfernung nach oben mit
    private static void submitFlames(State state, PoseStack poseStack, SubmitNodeCollector collector) {
        float frameHeight = 1.0f / BonfireLogic.FLAME_FRAMES;
        for (int i = 0; i < BonfireLogic.FLAME_TONGUES; i++) {
            BonfireLogic.FlameTongue tongue = BonfireLogic.flameTongue(i, state.time);
            float halfWidth = (float) tongue.width() * state.radiusScale / 2.0f;
            float height = (float) tongue.height() * state.radiusScale;
            float v0 = tongue.frame() * frameHeight;
            float v1 = v0 + frameHeight;

            poseStack.pushPose();
            poseStack.translate(0.5 + tongue.x(), FLAME_BASE_Y, 0.5 + tongue.z());
            poseStack.mulPose(Axis.YP.rotationDegrees(tongue.yaw()));
            collector.submitCustomGeometry(poseStack, FLAME_RENDER_TYPE, (pose, consumer) -> {
                flamePlane(consumer, pose, halfWidth, 0.0f, height, v0, v1);
                flamePlane(consumer, pose, 0.0f, halfWidth, height, v0, v1);
            });
            poseStack.popPose();
        }
    }

    private static void flamePlane(VertexConsumer consumer, PoseStack.Pose pose, float dx, float dz, float height,
                                   float v0, float v1) {
        int color = 0xFFFFFFFF;
        int light = LightCoordsUtil.FULL_BRIGHT;
        // Vorderseite und Rückseite, damit die Flamme von allen Seiten sichtbar ist
        vertex(consumer, pose, -dx, 0.0f, -dz, 0.0f, v1, color, light);
        vertex(consumer, pose, dx, 0.0f, dz, 1.0f, v1, color, light);
        vertex(consumer, pose, dx, height, dz, 1.0f, v0, color, light);
        vertex(consumer, pose, -dx, height, -dz, 0.0f, v0, color, light);
        vertex(consumer, pose, -dx, height, -dz, 0.0f, v0, color, light);
        vertex(consumer, pose, dx, height, dz, 1.0f, v0, color, light);
        vertex(consumer, pose, dx, 0.0f, dz, 1.0f, v1, color, light);
        vertex(consumer, pose, -dx, 0.0f, -dz, 0.0f, v1, color, light);
    }

    private static void vertex(VertexConsumer consumer, PoseStack.Pose pose, float x, float y, float z,
                                    float u, float v, int color, int light) {
        consumer.addVertex(pose, x, y, z)
            .setColor(color)
            .setUv(u, v)
            .setOverlay(OverlayTexture.NO_OVERLAY)
            .setLight(light)
            .setNormal(pose, 0.0f, 1.0f, 0.0f);
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
