package net.frank.mc3ver.tent;

import com.mojang.serialization.MapCodec;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.BaseEntityBlock;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.HorizontalDirectionalBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.block.state.properties.EnumProperty;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;

// Kiste an der rechten Zeltwand: FACING ist die Zelt-Blickrichtung (wie bei TentCanvasBlock)
public class TentChestBlock extends BaseEntityBlock {

    public static final EnumProperty<Direction> FACING = HorizontalDirectionalBlock.FACING;
    public static final BooleanProperty OPEN = BlockStateProperties.OPEN;
    public static final MapCodec<TentChestBlock> CODEC = simpleCodec(TentChestBlock::new);

    public TentChestBlock(BlockBehaviour.Properties properties) {
        super(properties);
        this.registerDefaultState(this.stateDefinition.any()
            .setValue(FACING, Direction.NORTH)
            .setValue(TentCanvasBlock.COLOR, DyeColor.WHITE)
            .setValue(OPEN, false));
    }

    @Override
    protected MapCodec<TentChestBlock> codec() {
        return CODEC;
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        builder.add(FACING, TentCanvasBlock.COLOR, OPEN);
    }

    public static VoxelShape getChestShape(Direction tentFacing) {
        Direction rightDir = tentFacing.getClockWise();
        VoxelShape body = switch (rightDir) {
            case EAST -> Block.box(1, 0, 2, 14, 11, 14);
            case WEST -> Block.box(2, 0, 2, 15, 11, 14);
            case SOUTH -> Block.box(2, 0, 1, 14, 11, 14);
            default -> Block.box(2, 0, 2, 14, 11, 15);
        };
        return Shapes.or(
            TentCanvasBlock.getWallShape(TentCanvasBlock.CanvasPart.WALL_RIGHT, tentFacing),
            body
        );
    }

    @Override
    protected VoxelShape getShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext context) {
        return getChestShape(state.getValue(FACING));
    }

    @Override
    protected void tick(BlockState state, ServerLevel level, BlockPos pos, RandomSource random) {
        if (level.getBlockEntity(pos) instanceof TentChestBlockEntity chest) {
            chest.recheckOpen();
        }
    }

    @Override
    public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
        return new TentChestBlockEntity(pos, state);
    }

    @Override
    protected InteractionResult useWithoutItem(BlockState state, Level level, BlockPos pos, Player player, BlockHitResult hit) {
        if (player.isShiftKeyDown()) {
            if (!level.isClientSide()) {
                ModTentBlocks.dismantleTent(level, pos, player);
            }
            return InteractionResult.SUCCESS;
        }
        if (!level.isClientSide() && level.getBlockEntity(pos) instanceof TentChestBlockEntity chest) {
            player.openMenu(chest);
        }
        return InteractionResult.SUCCESS;
    }

    @Override
    public BlockState playerWillDestroy(Level level, BlockPos pos, BlockState state, Player player) {
        if (!level.isClientSide()) {
            ModTentBlocks.dismantleTent(level, pos, player);
        }
        return super.playerWillDestroy(level, pos, state, player);
    }
}
