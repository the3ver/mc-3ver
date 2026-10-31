package net.frank.mc3ver.tree;

import net.minecraft.core.BlockPos;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.FallingParticlesLeavesBlock;
import net.minecraft.world.level.block.sounds.AmbientLeavesBlockSoundPlayer;
import net.minecraft.world.level.block.state.BlockBehaviour;

public class PearLeavesBlock extends FallingParticlesLeavesBlock {

    public PearLeavesBlock(float leafParticleChance, BlockBehaviour.Properties properties) {
        super(leafParticleChance, AmbientLeavesBlockSoundPlayer.noAmbientSound(), properties);
    }

    public PearLeavesBlock(BlockBehaviour.Properties properties) {
        this(0.01f, properties);
    }

    @Override
    protected void spawnFallingLeavesParticle(Level level, BlockPos pos, RandomSource random) {
        // Subtle ambient falling leaf particle hook
    }
}
