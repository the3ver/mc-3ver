package net.frank.mc3ver.client;

import net.fabricmc.fabric.api.client.rendering.v1.BlockColorRegistry;
import net.frank.mc3ver.tree.PearTreeBlocks;
import net.frank.mc3ver.tree.PearTreeLogic;
import net.minecraft.client.renderer.BiomeColors;

final class PearLeavesClient {

    // Vanilla-Standard für Laub ohne Welt (wie das oak_leaves-Item), ARGB mit vollem Alpha
    private static final int DEFAULT_FOLIAGE = 0xFF48B518;

    private PearLeavesClient() {}

    static void register() {
        // Laubtextur ist grau (oak_leaves); Farbe = Laubfarbe des Bioms, je Position etwas heller oder dunkler
        BlockColorRegistry.register(
                (state, world, pos, tints) -> {
                    if (world == null || pos == null) {
                        tints.add(DEFAULT_FOLIAGE);
                    } else {
                        tints.add(PearTreeLogic.leafColor(
                                BiomeColors.getAverageFoliageColor(world, pos), pos.getX(), pos.getZ()));
                    }
                },
                PearTreeBlocks.PEAR_LEAVES);
    }
}
