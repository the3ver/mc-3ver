package net.frank.mc3ver.client;

import net.fabricmc.fabric.api.client.rendering.v1.BlockColorRegistry;
import net.fabricmc.fabric.api.client.rendering.v1.level.LevelRenderEvents;
import net.frank.mc3ver.tent.ExplorerTentLogic;
import net.frank.mc3ver.tent.ModTentBlocks;
import net.frank.mc3ver.tent.TentCanvasBlock;
import net.frank.mc3ver.tent.TentFlapBlock;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.level.block.state.BlockState;

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

        // Kein Gitter aus Kollisionskästen auf der Zeltplane; Anvisieren und Abbauen gehen weiter
        LevelRenderEvents.BEFORE_BLOCK_OUTLINE.register((context, outline) -> {
            ClientLevel level = Minecraft.getInstance().level;
            if (level == null) return true;
            BlockState state = level.getBlockState(outline.pos());
            if (state.is(ModTentBlocks.TENT_CANVAS)) {
                return TentCanvasBlock.showsOutline(state.getValue(TentCanvasBlock.PART));
            }
            if (state.is(ModTentBlocks.TENT_FLAP)) {
                return TentFlapBlock.showsOutline(state.getValue(TentFlapBlock.OPEN));
            }
            return true;
        });
    }
}
