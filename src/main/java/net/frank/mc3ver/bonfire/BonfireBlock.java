package net.frank.mc3ver.bonfire;

import java.util.function.UnaryOperator;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.component.DataComponents;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.core.particles.SimpleParticleType;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.tags.ItemTags;
import net.minecraft.util.RandomSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.InsideBlockEffectApplier;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.ItemUtils;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.ScheduledTickAccess;
import net.minecraft.world.level.block.BaseEntityBlock;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.block.state.properties.EnumProperty;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;

// Ein Block pro Feld des 3x3-Signalfeuers; nur die Mitte trägt die BlockEntity für die Rauchsäule
public class BonfireBlock extends BaseEntityBlock {

    public static final EnumProperty<BonfireLogic.Part> PART = EnumProperty.create("part", BonfireLogic.Part.class);
    public static final BooleanProperty LIT = BlockStateProperties.LIT;
    public static final EnumProperty<BonfireLogic.SmokeColor> COLOR =
            EnumProperty.create("color", BonfireLogic.SmokeColor.class);

    private static final VoxelShape SHAPE = Block.box(0.0, 0.0, 0.0, 16.0, 7.0, 16.0);

    public BonfireBlock(BlockBehaviour.Properties properties) {
        super(properties);
        this.registerDefaultState(this.stateDefinition
                .any()
                .setValue(PART, BonfireLogic.Part.CENTER)
                .setValue(LIT, true)
                .setValue(COLOR, BonfireLogic.SmokeColor.NONE));
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        builder.add(PART, LIT, COLOR);
    }

    @Override
    protected VoxelShape getShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext context) {
        return SHAPE;
    }

    @Override
    public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
        return state.getValue(PART) == BonfireLogic.Part.CENTER ? new BonfireBlockEntity(pos, state) : null;
    }

    public static BlockPos centerOf(BlockPos pos, BlockState state) {
        BonfireLogic.Part part = state.getValue(PART);
        return pos.offset(-part.dx(), 0, -part.dz());
    }

    // Wendet dieselbe Zustandsänderung auf alle 9 Felder an, damit sie immer gleich aussehen
    public static void updateAllParts(Level level, BlockPos center, UnaryOperator<BlockState> change) {
        for (BonfireLogic.Part part : BonfireLogic.Part.values()) {
            BlockPos p = center.offset(part.dx(), 0, part.dz());
            BlockState s = level.getBlockState(p);
            if (s.getBlock() instanceof BonfireBlock) {
                BlockState changed = change.apply(s);
                if (changed != s) {
                    level.setBlock(p, changed, Block.UPDATE_ALL);
                }
            }
        }
    }

    @Override
    protected InteractionResult useItemOn(
            ItemStack stack,
            BlockState state,
            Level level,
            BlockPos pos,
            Player player,
            InteractionHand hand,
            BlockHitResult hit) {
        boolean lit = state.getValue(LIT);
        BlockPos center = centerOf(pos, state);

        if (!lit && (stack.is(Items.FLINT_AND_STEEL) || stack.is(Items.FIRE_CHARGE))) {
            if (!level.isClientSide()) {
                updateAllParts(level, center, s -> s.setValue(LIT, true));
                if (stack.is(Items.FLINT_AND_STEEL)) {
                    level.playSound(null, pos, SoundEvents.FLINTANDSTEEL_USE, SoundSource.BLOCKS, 1.0f, 1.0f);
                    stack.hurtAndBreak(1, player, hand);
                } else {
                    level.playSound(null, pos, SoundEvents.FIRECHARGE_USE, SoundSource.BLOCKS, 1.0f, 1.0f);
                    stack.consume(1, player);
                }
            }
            return InteractionResult.SUCCESS;
        }

        if (lit && (stack.is(ItemTags.SHOVELS) || stack.is(Items.WATER_BUCKET))) {
            if (!level.isClientSide()) {
                updateAllParts(level, center, s -> s.setValue(LIT, false));
                level.playSound(null, pos, SoundEvents.GENERIC_EXTINGUISH_FIRE, SoundSource.BLOCKS, 1.0f, 1.0f);
                if (stack.is(Items.WATER_BUCKET)) {
                    player.setItemInHand(
                            hand, ItemUtils.createFilledResult(stack, player, new ItemStack(Items.BUCKET)));
                } else {
                    stack.hurtAndBreak(1, player, hand);
                }
            }
            return InteractionResult.SUCCESS;
        }

        DyeColor dye = stack.get(DataComponents.DYE);
        if (dye != null) {
            BonfireLogic.SmokeColor color =
                    BonfireLogic.SmokeColor.fromDyeName(dye.getName()).orElseThrow();
            if (color == state.getValue(COLOR)) {
                return InteractionResult.PASS;
            }
            if (!level.isClientSide()) {
                updateAllParts(level, center, s -> s.setValue(COLOR, color));
                level.playSound(null, pos, SoundEvents.DYE_USE, SoundSource.BLOCKS, 1.0f, 1.0f);
                stack.consume(1, player);
            }
            return InteractionResult.SUCCESS;
        }

        return super.useItemOn(stack, state, level, pos, player, hand, hit);
    }

    @Override
    protected void entityInside(
            BlockState state,
            Level level,
            BlockPos pos,
            Entity entity,
            InsideBlockEffectApplier effectApplier,
            boolean isPrecise) {
        if (state.getValue(LIT) && entity instanceof LivingEntity) {
            entity.hurt(level.damageSources().campfire(), BonfireLogic.FIRE_DAMAGE);
        }
        super.entityInside(state, level, pos, entity, effectApplier, isPrecise);
    }

    // Fehlt ein Nachbarfeld (abgebaut, gesprengt), zerfällt der Rest Feld für Feld mit
    @Override
    protected BlockState updateShape(
            BlockState state,
            LevelReader level,
            ScheduledTickAccess ticks,
            BlockPos pos,
            Direction direction,
            BlockPos neighborPos,
            BlockState neighborState,
            RandomSource random) {
        if (direction.getAxis().isHorizontal()
                && state.getValue(PART).hasNeighbor(direction.getStepX(), direction.getStepZ())
                && !neighborState.is(this)) {
            return Blocks.AIR.defaultBlockState();
        }
        return super.updateShape(state, level, ticks, pos, direction, neighborPos, neighborState, random);
    }

    // Nur die Mitte droppt das Item (Loot-Table); beim Abbau eines Randfelds wird sie mit abgebaut
    @Override
    public BlockState playerWillDestroy(Level level, BlockPos pos, BlockState state, Player player) {
        if (!level.isClientSide() && state.getValue(PART) != BonfireLogic.Part.CENTER) {
            BlockPos center = centerOf(pos, state);
            if (level.getBlockState(center).is(this)) {
                level.destroyBlock(center, !player.getAbilities().instabuild, player);
            }
        }
        return super.playerWillDestroy(level, pos, state, player);
    }

    // Wie CampfireBlock.makeParticles, aber mit demselben Wind wie die Rauchfahne des Renderers
    // Partikel lassen sich nur clientseitig einfärben; der Client ersetzt diesen Spawner in Mc3verClient
    public interface SmokeSpawner {
        void spawn(
                Level level,
                SimpleParticleType type,
                double x,
                double y,
                double z,
                double xd,
                double yd,
                double zd,
                BonfireLogic.SmokeColor color);
    }

    public static SmokeSpawner smokeSpawner = (level, type, x, y, z, xd, yd, zd, color) ->
            level.addAlwaysVisibleParticle(type, true, x, y, z, xd, yd, zd);

    private static void addWindblownSmoke(
            Level level, BlockPos pos, BlockState state, RandomSource random, SimpleParticleType type) {
        double[] velocity = BonfireLogic.smokeParticleVelocity(
                BonfireLogic.windAt((double) level.getGameTime()), BonfireLogic.SIGNAL_SMOKE_RISE);
        smokeSpawner.spawn(
                level,
                type,
                pos.getX() + 0.5 + random.nextDouble() / 3.0 * (random.nextBoolean() ? 1 : -1),
                pos.getY() + random.nextDouble() + random.nextDouble(),
                pos.getZ() + 0.5 + random.nextDouble() / 3.0 * (random.nextBoolean() ? 1 : -1),
                velocity[0],
                velocity[1],
                velocity[2],
                state.getValue(COLOR));
    }

    @Override
    public void animateTick(BlockState state, Level level, BlockPos pos, RandomSource random) {
        if (!state.getValue(LIT)) {
            return;
        }
        if (state.getValue(PART) == BonfireLogic.Part.CENTER) {
            if (random.nextInt(5) == 0) {
                level.playLocalSound(
                        pos.getX() + 0.5,
                        pos.getY() + 0.5,
                        pos.getZ() + 0.5,
                        SoundEvents.CAMPFIRE_CRACKLE,
                        SoundSource.BLOCKS,
                        1.5f + random.nextFloat(),
                        random.nextFloat() * 0.7f + 0.6f,
                        false);
            }
            addWindblownSmoke(level, pos, state, random, ParticleTypes.CAMPFIRE_SIGNAL_SMOKE);
        } else if (random.nextInt(4) == 0) {
            addWindblownSmoke(level, pos, state, random, ParticleTypes.CAMPFIRE_COSY_SMOKE);
        }
        if (random.nextInt(6) == 0) {
            level.addParticle(
                    ParticleTypes.LAVA,
                    pos.getX() + 0.5,
                    pos.getY() + 0.5,
                    pos.getZ() + 0.5,
                    random.nextFloat() / 2.0f,
                    5.0E-5,
                    random.nextFloat() / 2.0f);
        }
    }
}
