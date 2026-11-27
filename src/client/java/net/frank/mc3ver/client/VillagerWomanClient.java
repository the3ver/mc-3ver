package net.frank.mc3ver.client;

import java.util.EnumMap;
import java.util.List;
import net.fabricmc.fabric.api.client.rendering.v1.LivingEntityRenderLayerRegistrationCallback;
import net.fabricmc.fabric.api.client.rendering.v1.ModelLayerRegistry;
import net.fabricmc.fabric.api.client.rendering.v1.RenderStateDataKey;
import net.frank.mc3ver.Mc3verMod;
import net.minecraft.client.renderer.entity.VillagerRenderer;
import net.minecraft.client.renderer.entity.state.VillagerRenderState;
import net.minecraft.resources.Identifier;

/** Bewohnerinnen: eigener Grundskin statt villager.png und ein Layer mit Zopf und Rock. */
public final class VillagerWomanClient {

    public static final RenderStateDataKey<Boolean> FEMALE = RenderStateDataKey.create(() -> "mc3ver:female");
    public static final Identifier BABY_TEXTURE = Mc3verMod.id("textures/entity/villager/villager_woman_baby.png");
    public static final RenderStateDataKey<Integer> LOOK = RenderStateDataKey.create(() -> "mc3ver:look");

    private VillagerWomanClient() {}

    public static boolean isFemale(VillagerRenderState state) {
        return Boolean.TRUE.equals(state.getData(FEMALE));
    }

    // Reihenfolge fest: der Index kommt aus der UUID, neue Aussehen nur hinten anhängen
    private static final List<VillagerWomanLook> LOOKS =
            List.of(VillagerWomanLook.BRAID, VillagerWomanLook.PIGTAILS, VillagerWomanLook.BUN);

    public static int lookCount() {
        return LOOKS.size();
    }

    static VillagerWomanLook look(VillagerRenderState state) {
        Integer look = state.getData(LOOK);
        return LOOKS.get(look == null ? 0 : look);
    }

    public static Identifier texture(VillagerRenderState state) {
        return state.isBaby ? BABY_TEXTURE : look(state).skin;
    }

    static void register() {
        for (VillagerWomanLook look : VillagerWomanLook.values()) {
            ModelLayerRegistry.registerModelLayer(look.layer, () -> VillagerWomanModel.createLayer(look));
        }
        LivingEntityRenderLayerRegistrationCallback.EVENT.register((type, renderer, helper, context) -> {
            if (renderer instanceof VillagerRenderer villagerRenderer) {
                EnumMap<VillagerWomanLook, VillagerWomanModel> models = new EnumMap<>(VillagerWomanLook.class);
                for (VillagerWomanLook look : VillagerWomanLook.values()) {
                    models.put(look, new VillagerWomanModel(context.bakeLayer(look.layer)));
                }
                helper.register(new VillagerWomanLayer(villagerRenderer, models));
            }
        });
    }
}
