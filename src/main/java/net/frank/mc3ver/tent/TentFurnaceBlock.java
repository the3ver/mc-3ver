package net.frank.mc3ver.tent;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.stats.Stats;
import net.minecraft.util.RandomSource;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.AbstractFurnaceBlock;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;

// Ofen an der linken Zeltwand: FACING ist die Zelt-Blickrichtung (wie bei TentCanvasBlock), nicht die Ofenfront
public class TentFurnaceBlock extends AbstractFurnaceBlock {

    public static final com.mojang.serialization.MapCodec<TentFurnaceBlock> CODEC = simpleCodec(TentFurnaceBlock::new);

    public TentFurnaceBlock(BlockBehaviour.Properties properties) {
        super(properties);
        this.registerDefaultState(this.defaultBlockState()
            .setValue(TentCanvasBlock.COLOR, net.minecraft.world.item.DyeColor.WHITE));
    }

    @Override
    protected com.mojang.serialization.MapCodec<TentFurnaceBlock> codec() {
        return CODEC;
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        super.createBlockStateDefinition(builder);
        builder.add(TentCanvasBlock.COLOR);
    }

    public static Direction getFrontDirection(Direction tentFacing) {
        return tentFacing.getClockWise();
    }

    // Rohrmitte im Nord-Modell (Pixel) und Oberkante relativ zum Ofenblock, gedreht wie die Blockstate-Rotation
    private static final double PIPE_CENTER_X = 6.0;
    private static final double PIPE_CENTER_Z = 8.0;
    private static final double PIPE_TOP_Y = 1.0 + 24.0 / 16.0;

    public static double[] getPipeTopOffset(Direction tentFacing) {
        double x = PIPE_CENTER_X;
        double z = PIPE_CENTER_Z;
        double[] rotated = switch (tentFacing) {
            case EAST -> new double[]{16.0 - z, x};
            case SOUTH -> new double[]{16.0 - x, 16.0 - z};
            case WEST -> new double[]{z, 16.0 - x};
            default -> new double[]{x, z};
        };
        return new double[]{rotated[0] / 16.0, PIPE_TOP_Y, rotated[1] / 16.0};
    }

    public static VoxelShape getFurnaceShape(Direction tentFacing) {
        Direction leftDir = tentFacing.getCounterClockWise();
        return Shapes.or(
            TentCanvasBlock.getWallShape(TentCanvasBlock.CanvasPart.WALL_LEFT, tentFacing),
            TentCanvasBlock.getTableShape(leftDir)
        );
    }

    @Override
    protected VoxelShape getShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext context) {
        return getFurnaceShape(state.getValue(FACING));
    }

    @Override
    public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
        return new TentFurnaceBlockEntity(pos, state);
    }

    @Override
    public <T extends BlockEntity> BlockEntityTicker<T> getTicker(Level level, BlockState state, BlockEntityType<T> type) {
        return createFurnaceTicker(level, type, ModTentBlocks.TENT_FURNACE_BLOCK_ENTITY);
    }

    @Override
    protected void openContainer(Level level, BlockPos pos, Player player) {
        if (level.getBlockEntity(pos) instanceof TentFurnaceBlockEntity furnace) {
            player.openMenu((MenuProvider) furnace);
            player.awardStat(Stats.INTERACT_WITH_FURNACE);
        }
    }

    @Override
    protected InteractionResult useWithoutItem(BlockState state, Level level, BlockPos pos, Player player, BlockHitResult hit) {
        if (player.isShiftKeyDown()) {
            if (!level.isClientSide()) {
                ModTentBlocks.dismantleTent(level, pos, player);
            }
            return InteractionResult.SUCCESS;
        }
        return super.useWithoutItem(state, level, pos, player, hit);
    }

    @Override
    public BlockState playerWillDestroy(Level level, BlockPos pos, BlockState state, Player player) {
        if (!level.isClientSide()) {
            ModTentBlocks.dismantleTent(level, pos, player);
        }
        return super.playerWillDestroy(level, pos, state, player);
    }

    @Override
    public void animateTick(BlockState state, Level level, BlockPos pos, RandomSource random) {
        if (!state.getValue(LIT)) {
            return;
        }
        double x = pos.getX() + 0.5;
        double y = pos.getY();
        double z = pos.getZ() + 0.5;
        if (random.nextDouble() < 0.1) {
            level.playLocalSound(x, y, z, SoundEvents.FURNACE_FIRE_CRACKLE, SoundSource.BLOCKS, 1.0f, 1.0f, false);
        }

        Direction front = getFrontDirection(state.getValue(FACING));
        double spread = random.nextDouble() * 0.6 - 0.3;
        double dx = front.getAxis() == Direction.Axis.X ? front.getStepX() * 0.52 : spread;
        double dy = random.nextDouble() * 6.0 / 16.0;
        double dz = front.getAxis() == Direction.Axis.Z ? front.getStepZ() * 0.52 : spread;
        level.addParticle(ParticleTypes.SMOKE, x + dx, y + dy, z + dz, 0.0, 0.0, 0.0);
        level.addParticle(ParticleTypes.FLAME, x + dx, y + dy, z + dz, 0.0, 0.0, 0.0);

        // Rauch aus dem Ofenrohr über dem Dach
        if (random.nextFloat() < 0.6f) {
            double[] pipeTop = getPipeTopOffset(state.getValue(FACING));
            level.addParticle(ParticleTypes.LARGE_SMOKE,
                pos.getX() + pipeTop[0], pos.getY() + pipeTop[1], pos.getZ() + pipeTop[2],
                0.0, 0.04, 0.0);
        }
    }
}
