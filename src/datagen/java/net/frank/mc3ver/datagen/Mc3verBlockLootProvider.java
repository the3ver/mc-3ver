package net.frank.mc3ver.datagen;

import java.util.concurrent.CompletableFuture;
import net.fabricmc.fabric.api.datagen.v1.FabricPackOutput;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricBlockLootSubProvider;
import net.frank.mc3ver.tree.PearTreeBlocks;
import net.minecraft.core.HolderLookup;

/**
 * Loot-Tabellen mit Vanilla-Verhalten (wie Eiche). Sonderfälle bleiben handgeschrieben in src/main/resources:
 * Birnenlaub (Chancen aus PearTreeLogic), Lagerfeuer, Transportflamme.
 */
class Mc3verBlockLootProvider extends FabricBlockLootSubProvider {

    Mc3verBlockLootProvider(FabricPackOutput output, CompletableFuture<HolderLookup.Provider> registries) {
        super(output, registries);
    }

    @Override
    public void generate() {
        dropSelf(PearTreeBlocks.PEAR_LOG);
        dropSelf(PearTreeBlocks.STRIPPED_PEAR_LOG);
        dropSelf(PearTreeBlocks.PEAR_WOOD);
        dropSelf(PearTreeBlocks.STRIPPED_PEAR_WOOD);
        dropSelf(PearTreeBlocks.PEAR_PLANKS);
        dropSelf(PearTreeBlocks.PEAR_STAIRS);
        dropSelf(PearTreeBlocks.PEAR_FENCE);
        dropSelf(PearTreeBlocks.PEAR_FENCE_GATE);
        dropSelf(PearTreeBlocks.PEAR_TRAPDOOR);
        dropSelf(PearTreeBlocks.PEAR_PRESSURE_PLATE);
        dropSelf(PearTreeBlocks.PEAR_BUTTON);
        dropSelf(PearTreeBlocks.PEAR_SAPLING);
        add(PearTreeBlocks.PEAR_SLAB, this::createSlabItemTable);
        add(PearTreeBlocks.PEAR_DOOR, this::createDoorTable);
    }
}
