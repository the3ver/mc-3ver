package net.frank.mc3ver.tent;

import com.mojang.datafixers.util.Either;
import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.stats.Stats;
import net.minecraft.util.Unit;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.HorizontalDirectionalBlock;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BedPart;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.block.state.properties.EnumProperty;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;

import java.util.List;

public class TentSleepingBagBlock extends HorizontalDirectionalBlock {

    public static final EnumProperty<Direction> FACING = HorizontalDirectionalBlock.FACING;
    public static final EnumProperty<BedPart> PART = BlockStateProperties.BED_PART;
    public static final BooleanProperty OCCUPIED = BlockStateProperties.OCCUPIED;
    public static final com.mojang.serialization.MapCodec<TentSleepingBagBlock> CODEC = simpleCodec(TentSleepingBagBlock::new);

    protected static final VoxelShape FOOT_SHAPE = Block.box(1.0, 0.0, 0.0, 15.0, 3.0, 16.0);
    protected static final VoxelShape HEAD_SHAPE = Shapes.or(
        Block.box(1.0, 0.0, 0.0, 15.0, 3.0, 16.0),
        Block.box(2.0, 3.0, 6.0, 14.0, 6.0, 15.0)
    );

    public TentSleepingBagBlock(BlockBehaviour.Properties properties) {
        super(properties);
        this.registerDefaultState(this.stateDefinition.any()
            .setValue(FACING, Direction.NORTH)
            .setValue(PART, BedPart.FOOT)
            .setValue(OCCUPIED, false));
    }

    @Override
    protected com.mojang.serialization.MapCodec<? extends HorizontalDirectionalBlock> codec() {
        return CODEC;
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        builder.add(FACING, PART, OCCUPIED);
    }

    @Override
    protected VoxelShape getShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext context) {
        return state.getValue(PART) == BedPart.HEAD ? HEAD_SHAPE : FOOT_SHAPE;
    }

    @Override
    protected VoxelShape getCollisionShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext context) {
        return Shapes.empty();
    }

    @Override
    protected InteractionResult useWithoutItem(BlockState state, Level level, BlockPos pos, Player player, BlockHitResult hit) {
        if (player.isShiftKeyDown()) {
            if (!level.isClientSide()) {
                ModTentBlocks.dismantleTent(level, pos, player);
            }
            return InteractionResult.SUCCESS;
        }

        if (level.isClientSide()) {
            return InteractionResult.CONSUME;
        }

        BlockPos targetPos = pos;
        if (state.getValue(PART) != BedPart.HEAD) {
            targetPos = pos.relative(state.getValue(FACING));
            BlockState headState = level.getBlockState(targetPos);
            if (!headState.is(this)) {
                return InteractionResult.CONSUME;
            }
        }

        if (player instanceof ServerPlayer serverPlayer) {
            ServerPlayer.RespawnConfig oldRespawn = serverPlayer.getRespawnConfig();
            Either<Player.BedSleepingProblem, Unit> result = serverPlayer.startSleepInBed(targetPos);

            result.ifLeft(problem -> {
                Component message = problem.message();
                if (message != null) {
                    serverPlayer.sendOverlayMessage(message);
                }
            });

            if (result.right().isPresent()) {
                // Heimat-Spawnpunkt unverändert wiederherstellen
                serverPlayer.setRespawnPosition(oldRespawn, false);

                // Ruhetimer gegen Phantome zurücksetzen
                serverPlayer.resetStat(Stats.CUSTOM.get(Stats.TIME_SINCE_REST));

                // Entdecker-Buff und Meldung
                serverPlayer.addEffect(new MobEffectInstance(MobEffects.SPEED, 20 * 60, 0));
                serverPlayer.sendSystemMessage(Component.translatable("message.mc3ver.tent_sleeping").withStyle(ChatFormatting.AQUA));
            }
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
