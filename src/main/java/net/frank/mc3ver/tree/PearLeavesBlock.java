package net.frank.mc3ver.tree;

import com.mojang.serialization.MapCodec;
import net.minecraft.core.BlockPos;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.LeavesBlock;
import net.minecraft.world.level.block.state.BlockBehaviour;

public class PearLeavesBlock extends LeavesBlock {

    public static final MapCodec<PearLeavesBlock> CODEC = simpleCodec(PearLeavesBlock::new);

    public PearLeavesBlock(float leafParticleChance, BlockBehaviour.Properties properties) {
        super(leafParticleChance, properties);
    }

    public PearLeavesBlock(BlockBehaviour.Properties properties) {
        super(0.01f, properties);
    }

    @Override
    public MapCodec<PearLeavesBlock> codec() {
        return CODEC;
    }

    @Override
    protected void spawnFallingLeavesParticle(Level level, BlockPos pos, RandomSource random) {
        // Subtle ambient falling leaf particle hook
    }
}
