package net.frank.mc3ver.client.mixin;

import net.frank.mc3ver.client.VillagerWomanClient;
import net.frank.mc3ver.village.ModVillagers;
import net.minecraft.client.renderer.entity.VillagerRenderer;
import net.minecraft.client.renderer.entity.state.VillagerRenderState;
import net.minecraft.resources.Identifier;
import net.minecraft.world.entity.npc.villager.Villager;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

// Vanilla kennt nur einen festen Grundskin; Bewohnerinnen bekommen ihren eigenen
@Mixin(VillagerRenderer.class)
abstract class VillagerRendererMixin {

    @Inject(
            method =
                    "extractRenderState(Lnet/minecraft/world/entity/npc/villager/Villager;Lnet/minecraft/client/renderer/entity/state/VillagerRenderState;F)V",
            at = @At("TAIL"))
    private void mc3ver$extractFemale(
            Villager villager, VillagerRenderState state, float unusedPartialTick, CallbackInfo unusedCi) {
        state.setData(VillagerWomanClient.FEMALE, ModVillagers.isFemale(villager));
    }

    @Inject(
            method =
                    "getTextureLocation(Lnet/minecraft/client/renderer/entity/state/VillagerRenderState;)Lnet/minecraft/resources/Identifier;",
            at = @At("HEAD"),
            cancellable = true)
    private void mc3ver$womanTexture(VillagerRenderState state, CallbackInfoReturnable<Identifier> cir) {
        if (VillagerWomanClient.isFemale(state)) cir.setReturnValue(VillagerWomanClient.texture(state));
    }
}
