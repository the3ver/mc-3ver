package net.frank.mc3ver.tent;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.HorizontalDirectionalBlock;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.pathfinder.PathComputationType;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;

// Eingangsplane: offen aufgerollt und begehbar, geschlossen eine dünne Plane an der Außenkante
public class TentFlapBlock extends HorizontalDirectionalBlock {

    public static final BooleanProperty OPEN = BlockStateProperties.OPEN;

    public TentFlapBlock(BlockBehaviour.Properties properties) {
        super(properties);
        this.registerDefaultState(this.stateDefinition
                .any()
                .setValue(FACING, Direction.NORTH)
                .setValue(OPEN, true)
                .setValue(TentCanvasBlock.COLOR, DyeColor.WHITE));
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        builder.add(FACING, OPEN, TentCanvasBlock.COLOR);
    }

    public static VoxelShape getCollision(Direction facing, boolean open) {
        return open ? Shapes.empty() : TentCanvasBlock.getWallShape(TentCanvasBlock.CanvasPart.WALL_BACK, facing);
    }

    public static boolean isPassable(boolean open) {
        return open;
    }

    @Override
    protected VoxelShape getShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext context) {
        // Auch offen anklickbar, damit man die Plane wieder schließen kann
        return getCollision(state.getValue(FACING), false);
    }

    @Override
    protected VoxelShape getCollisionShape(
            BlockState state, BlockGetter level, BlockPos pos, CollisionContext context) {
        return getCollision(state.getValue(FACING), state.getValue(OPEN));
    }

    @Override
    protected boolean isPathfindable(BlockState state, PathComputationType type) {
        return isPassable(state.getValue(OPEN));
    }

    @Override
    protected InteractionResult useWithoutItem(
            BlockState state, Level level, BlockPos pos, Player player, BlockHitResult hit) {
        if (player.isShiftKeyDown()) {
            if (!level.isClientSide()) {
                ModTentBlocks.dismantleTent(level, pos, player);
            }
            return InteractionResult.SUCCESS;
        }
        if (!level.isClientSide()) {
            boolean open = !state.getValue(OPEN);
            level.setBlock(pos, state.setValue(OPEN, open), Block.UPDATE_ALL);
            level.playSound(
                    null, pos, open ? SoundEvents.WOOL_BREAK : SoundEvents.WOOL_PLACE, SoundSource.BLOCKS, 0.8f, 1.1f);
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
