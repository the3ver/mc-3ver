package net.frank.mc3ver.client;

import net.fabricmc.fabric.api.client.rendering.v1.LivingEntityRenderLayerRegistrationCallback;
import net.fabricmc.fabric.api.client.rendering.v1.ModelLayerRegistry;
import net.fabricmc.fabric.api.client.rendering.v1.RenderStateDataKey;
import net.frank.mc3ver.Mc3verMod;
import net.minecraft.client.model.geom.ModelLayerLocation;
import net.minecraft.client.renderer.entity.VillagerRenderer;
import net.minecraft.client.renderer.entity.state.VillagerRenderState;
import net.minecraft.resources.Identifier;

/** Bewohnerinnen: eigener Grundskin statt villager.png und ein Layer mit Zopf und Rock. */
public final class VillagerWomanClient {

    public static final RenderStateDataKey<Boolean> FEMALE = RenderStateDataKey.create(() -> "mc3ver:female");
    public static final Identifier TEXTURE = Mc3verMod.id("textures/entity/villager/villager_woman.png");
    public static final Identifier BABY_TEXTURE = Mc3verMod.id("textures/entity/villager/villager_woman_baby.png");
    static final Identifier LAYER_TEXTURE = Mc3verMod.id("textures/entity/villager/villager_woman_layer.png");
    static final ModelLayerLocation LAYER = new ModelLayerLocation(Mc3verMod.id("villager_woman"), "main");

    private VillagerWomanClient() {}

    public static boolean isFemale(VillagerRenderState state) {
        return Boolean.TRUE.equals(state.getData(FEMALE));
    }

    public static Identifier texture(VillagerRenderState state) {
        return state.isBaby ? BABY_TEXTURE : TEXTURE;
    }

    static void register() {
        ModelLayerRegistry.registerModelLayer(LAYER, VillagerWomanModel::createLayer);
        LivingEntityRenderLayerRegistrationCallback.EVENT.register((type, renderer, helper, context) -> {
            if (renderer instanceof VillagerRenderer villagerRenderer) {
                helper.register(
                        new VillagerWomanLayer(villagerRenderer, new VillagerWomanModel(context.bakeLayer(LAYER))));
            }
        });
    }
}
