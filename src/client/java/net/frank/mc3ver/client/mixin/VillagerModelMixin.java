package net.frank.mc3ver.client.mixin;

import net.frank.mc3ver.client.VillagerWomanFigure;
import net.minecraft.client.model.geom.builders.MeshDefinition;
import net.minecraft.client.model.npc.VillagerModel;
import net.minecraft.client.renderer.entity.state.VillagerRenderState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

// Bewohnerinnen bekommen Taille und Hüfte: Figur-Teile im Mesh, Umschalten pro Dorfbewohner in setupAnim
@Mixin(VillagerModel.class)
abstract class VillagerModelMixin {

    @Inject(method = "createBodyModel", at = @At("RETURN"))
    private static void mc3ver$addWomanFigure(CallbackInfoReturnable<MeshDefinition> cir) {
        VillagerWomanFigure.addTo(cir.getReturnValue().getRoot());
    }

    @Inject(method = "setupAnim(Lnet/minecraft/client/renderer/entity/state/VillagerRenderState;)V", at = @At("TAIL"))
    private void mc3ver$toggleWomanFigure(VillagerRenderState state, CallbackInfo unusedCi) {
        VillagerWomanFigure.apply(((VillagerModel) (Object) this).root(), state);
    }
}
