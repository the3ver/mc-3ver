package net.frank.mc3ver.client;

import net.fabricmc.fabric.api.client.rendering.v1.BlockColorRegistry;
import net.frank.mc3ver.tent.ExplorerTentLogic;
import net.frank.mc3ver.tent.ModTentBlocks;
import net.frank.mc3ver.tent.TentCanvasBlock;
import net.minecraft.world.item.DyeColor;

final class TentClient {

    private TentClient() {}

    static void register() {
        // Färbung der Zeltplane in der Welt
        BlockColorRegistry.register(
                (state, world, pos, tints) -> {
                    if (state != null && state.hasProperty(TentCanvasBlock.COLOR)) {
                        DyeColor dyeColor = state.getValue(TentCanvasBlock.COLOR);
                        tints.add(ExplorerTentLogic.resolveBlockColorRgb(dyeColor.getName()));
                    } else {
                        tints.add(ExplorerTentLogic.getColorRgb("white"));
                    }
                },
                ModTentBlocks.TENT_CANVAS,
                ModTentBlocks.TENT_FURNACE,
                ModTentBlocks.TENT_FLAP,
                ModTentBlocks.TENT_CHEST);
    }
}
