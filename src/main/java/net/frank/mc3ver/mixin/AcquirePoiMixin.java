package net.frank.mc3ver.mixin;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.llamalad7.mixinextras.sugar.Local;
import com.mojang.datafixers.util.Pair;
import java.util.function.Predicate;
import java.util.stream.Stream;
import net.frank.mc3ver.village.ModVillagers;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
import net.minecraft.world.entity.PathfinderMob;
import net.minecraft.world.entity.ai.behavior.AcquirePoi;
import net.minecraft.world.entity.ai.village.poi.PoiManager;
import net.minecraft.world.entity.ai.village.poi.PoiType;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

// Die POI-Suche kennt nur den POI-Typ; hier kommt der suchende Bewohner dazu, damit nur Bewohnerinnen den Backofen
// finden
@Mixin(AcquirePoi.class)
abstract class AcquirePoiMixin {

    @WrapOperation(
            method = "lambda$create$3",
            at =
                    @At(
                            value = "INVOKE",
                            target =
                                    "Lnet/minecraft/world/entity/ai/village/poi/PoiManager;findAllClosestFirstWithType(Ljava/util/function/Predicate;Ljava/util/function/Predicate;Lnet/minecraft/core/BlockPos;ILnet/minecraft/world/entity/ai/village/poi/PoiManager$Occupancy;)Ljava/util/stream/Stream;"))
    private static Stream<Pair<Holder<PoiType>, BlockPos>> mc3ver$onlyWomenFindTheOven(
            PoiManager manager,
            Predicate<Holder<PoiType>> type,
            Predicate<BlockPos> position,
            BlockPos center,
            int radius,
            PoiManager.Occupancy occupancy,
            Operation<Stream<Pair<Holder<PoiType>, BlockPos>>> original,
            @Local(argsOnly = true) PathfinderMob body) {
        return original.call(
                manager, type.and(poi -> ModVillagers.mayWorkAt(body, poi)), position, center, radius, occupancy);
    }
}
