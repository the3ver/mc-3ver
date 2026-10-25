package net.frank.mc3ver.bonfire;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;

// Ohne eigene Daten; existiert nur, damit der Client die Rauchsäule über einen BlockEntityRenderer zeichnen kann
public class BonfireBlockEntity extends BlockEntity {

    public BonfireBlockEntity(BlockPos pos, BlockState state) {
        super(ModBonfire.BONFIRE_BLOCK_ENTITY, pos, state);
    }
}
