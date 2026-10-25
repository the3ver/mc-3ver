package net.frank.mc3ver.transport;

import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.network.chat.Component;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.RandomSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.EntityBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.Random;

public class TransportFlameBlock extends Block implements EntityBlock {

    protected static final VoxelShape SHAPE = Block.box(4.0, 0.0, 4.0, 12.0, 14.0, 12.0);

    private static final TransportFlameLogic.FlameHoldTracker MAP_RECOVERY_HOLDS = new TransportFlameLogic.FlameHoldTracker();

    public TransportFlameBlock(BlockBehaviour.Properties properties) {
        super(properties);
    }

    @Override
    protected VoxelShape getShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext context) {
        return SHAPE;
    }

    @Override
    public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
        return new TransportFlameBlockEntity(pos, state);
    }

    @Override
    public void setPlacedBy(Level level, BlockPos pos, BlockState state, @Nullable LivingEntity placer, ItemStack stack) {
        super.setPlacedBy(level, pos, state, placer, stack);

        if (!level.isClientSide() && placer instanceof ServerPlayer player) {
            handleFlamePlaced(player, level, pos);
        }
    }

    private void handleFlamePlaced(ServerPlayer player, Level level, BlockPos pos) {
        MinecraftServer server = player.level().getServer();
        String dimensionId = level.dimension().identifier().toString();

        // 1. Scan regular inventory, hotbar, offhand and bundles
        List<TransportMapInventory.MapItemRef> mapRefs = TransportMapInventory.scan(player);

        // 2. Prepare FlameChecker
        TransportFlameLogic.FlameChecker flameChecker = (dimStr, targetPos) -> {
            if (server == null) return false;
            net.minecraft.resources.ResourceKey<Level> dimKey = net.minecraft.resources.ResourceKey.create(
                net.minecraft.core.registries.Registries.DIMENSION,
                net.minecraft.resources.Identifier.parse(dimStr)
            );
            ServerLevel targetLevel = server.getLevel(dimKey);
            if (targetLevel == null) return false;
            return targetLevel.getBlockState(targetPos).is(ModBlocks.TRANSPORT_FLAME);
        };

        // 3. Extract targets
        List<TransportFlameLogic.FlameTarget> candidateTargets = TransportMapInventory.targets(mapRefs);

        // 4. Decide action
        TransportFlameLogic.PlacementDecision decision = TransportFlameLogic.decidePlacementAction(
            pos.getX(), pos.getY(), pos.getZ(), dimensionId,
            candidateTargets,
            flameChecker,
            new Random()
        );

        if (level.getBlockEntity(pos) instanceof TransportFlameBlockEntity flame) {
            flame.setTarget(decision.newTarget());
        }

        // 5. Execute decision
        switch (decision.actionType()) {
            case KEEP_EXISTING_MAP -> {
                player.sendSystemMessage(net.minecraft.network.chat.Component.translatable("message.mc3ver.map_already_linked").withStyle(net.minecraft.ChatFormatting.YELLOW));
            }
            case RELINK_OLD_MAP -> {
                for (TransportMapInventory.MapItemRef ref : mapRefs) {
                    if (decision.targetToRelink() != null && decision.targetToRelink().equals(ref.getTarget())) {
                        ref.updateTarget(decision.newTarget());
                        break;
                    }
                }
                player.sendSystemMessage(net.minecraft.network.chat.Component.translatable("message.mc3ver.map_relinked").withStyle(net.minecraft.ChatFormatting.GREEN));
            }
            case CREATE_NEW_MAP -> {
                ItemStack mapStack = TransportMapInventory.createMap(decision.newTarget());

                boolean added = player.getInventory().add(mapStack);
                if (!added) {
                    player.drop(mapStack, false);
                }
            }
        }
    }

    @Override
    protected InteractionResult useItemOn(ItemStack stack, BlockState state, Level level, BlockPos pos, Player player, InteractionHand hand, BlockHitResult hitResult) {
        if (stack.isEmpty()) {
            return InteractionResult.TRY_WITH_EMPTY_HAND;
        }
        if (!stack.is(Items.PAPER)) {
            return InteractionResult.PASS;
        }
        if (level instanceof ServerLevel serverLevel && player instanceof ServerPlayer serverPlayer) {
            continueMapRecovery(serverPlayer, serverLevel, pos, stack);
        }
        return InteractionResult.SUCCESS;
    }

    @Override
    protected InteractionResult useWithoutItem(BlockState state, Level level, BlockPos pos, Player player, BlockHitResult hitResult) {
        if (player.getOffhandItem().is(Items.PAPER)) {
            return InteractionResult.PASS;
        }
        if (player instanceof ServerPlayer serverPlayer) {
            showMapRecoveryHint(serverPlayer, level, pos);
        }
        return InteractionResult.SUCCESS;
    }

    private static boolean carriesMapFor(ServerPlayer player, Level level, BlockPos pos) {
        return TransportFlameLogic.carriesMapFor(
            TransportMapInventory.targets(TransportMapInventory.scan(player)),
            pos.getX(), pos.getY(), pos.getZ(), level.dimension().identifier().toString()
        );
    }

    private static void showMapRecoveryHint(ServerPlayer player, Level level, BlockPos pos) {
        TransportFlameLogic.MapRecoveryOutcome outcome = TransportFlameLogic.decideMapRecovery(carriesMapFor(player, level, pos), false);
        String key = outcome == TransportFlameLogic.MapRecoveryOutcome.ALREADY_HAS_MAP
            ? "message.mc3ver.map_already_linked"
            : "message.mc3ver.map_recovery_hint";
        player.sendOverlayMessage(Component.translatable(key).withStyle(ChatFormatting.YELLOW));
    }

    // Wird bei gedrückter rechter Maustaste alle 4 Ticks aufgerufen; nach RECOVERY_HOLD_TICKS gibt es die Karte
    private static void continueMapRecovery(ServerPlayer player, ServerLevel level, BlockPos pos, ItemStack paper) {
        TransportFlameLogic.MapRecoveryOutcome outcome = TransportFlameLogic.decideMapRecovery(carriesMapFor(player, level, pos), true);
        if (outcome == TransportFlameLogic.MapRecoveryOutcome.ALREADY_HAS_MAP) {
            MAP_RECOVERY_HOLDS.reset(player.getUUID());
            player.sendOverlayMessage(Component.translatable("message.mc3ver.map_already_linked").withStyle(ChatFormatting.YELLOW));
            return;
        }

        String dimensionId = level.dimension().identifier().toString();
        float progress = MAP_RECOVERY_HOLDS.update(player.getUUID(), dimensionId + "|" + pos.asLong(), level.getGameTime());
        if (progress < 1.0f) {
            player.sendOverlayMessage(Component.translatable("message.mc3ver.map_recovery_progress",
                TransportFlameLogic.progressBar(progress, 10)).withStyle(ChatFormatting.AQUA));
            level.playSound(null, pos, SoundEvents.BOOK_PAGE_TURN, SoundSource.BLOCKS, 0.6f, 0.8f + progress * 0.6f);
            return;
        }

        TransportFlameBlockEntity flame = level.getBlockEntity(pos) instanceof TransportFlameBlockEntity be ? be : null;
        TransportFlameLogic.FlameTarget target = TransportFlameLogic.resolveFlameTarget(
            flame != null ? flame.getTarget() : null,
            pos.getX(), pos.getY(), pos.getZ(), dimensionId, new Random()
        );
        if (flame != null) {
            flame.setTarget(target);
        }

        if (!player.hasInfiniteMaterials()) {
            paper.shrink(1);
        }
        ItemStack mapStack = TransportMapInventory.createMap(target);
        if (!player.getInventory().add(mapStack)) {
            player.drop(mapStack, false);
        }
        level.playSound(null, pos, SoundEvents.UI_CARTOGRAPHY_TABLE_TAKE_RESULT, SoundSource.BLOCKS, 1.0f, 1.0f);
        player.sendOverlayMessage(Component.translatable("message.mc3ver.map_recovered").withStyle(ChatFormatting.GREEN));
    }

    @Override
    public void animateTick(BlockState state, Level level, BlockPos pos, RandomSource random) {
        double x = pos.getX() + 0.5 + (random.nextDouble() - 0.5) * 0.2;
        double y = pos.getY() + 0.7 + (random.nextDouble() - 0.5) * 0.2;
        double z = pos.getZ() + 0.5 + (random.nextDouble() - 0.5) * 0.2;

        level.addParticle(ParticleTypes.SOUL_FIRE_FLAME, x, y, z, 0.0, 0.02, 0.0);
        level.addParticle(ParticleTypes.PORTAL, x, y, z, (random.nextDouble() - 0.5) * 0.5, (random.nextDouble() - 0.5) * 0.5, (random.nextDouble() - 0.5) * 0.5);
    }
}
