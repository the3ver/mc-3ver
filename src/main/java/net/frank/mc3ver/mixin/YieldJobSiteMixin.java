package net.frank.mc3ver.mixin;

import net.frank.mc3ver.village.ModVillagers;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
import net.minecraft.world.entity.ai.behavior.YieldJobSite;
import net.minecraft.world.entity.ai.village.poi.PoiType;
import net.minecraft.world.entity.npc.villager.Villager;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

// Wer einen Arbeitsplatz nicht nutzen kann, gibt ihn an Nachbarn weiter; den Backofen nur an Bewohnerinnen
@Mixin(YieldJobSite.class)
abstract class YieldJobSiteMixin {

    @Inject(method = "nearbyWantsJobsite", at = @At("HEAD"), cancellable = true)
    private static void mc3ver$onlyWomenTakeTheOven(
            Holder<PoiType> type, Villager nearby, BlockPos unusedPos, CallbackInfoReturnable<Boolean> cir) {
        if (!ModVillagers.mayWorkAt(nearby, type)) cir.setReturnValue(false);
    }
}
