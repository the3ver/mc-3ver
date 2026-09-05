package net.frank.mc3ver.transport;

import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.ItemStackTemplate;
import net.minecraft.world.item.component.BundleContents;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.UUID;

public class TransportFlameBlock extends Block {

    protected static final VoxelShape SHAPE = Block.box(4.0, 0.0, 4.0, 12.0, 14.0, 12.0);

    public TransportFlameBlock(BlockBehaviour.Properties properties) {
        super(properties);
    }

    @Override
    protected VoxelShape getShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext context) {
        return SHAPE;
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

        interface MapItemRef {
            TransportFlameLogic.FlameTarget getTarget();
            void updateTarget(TransportFlameLogic.FlameTarget newTarget);
        }

        List<MapItemRef> mapRefs = new ArrayList<>();

        // 1. Scan regular inventory, hotbar, and offhand
        for (int slot = 0; slot < player.getInventory().getContainerSize(); slot++) {
            ItemStack slotStack = player.getInventory().getItem(slot);
            if (slotStack.isEmpty()) continue;

            if (slotStack.is(ModItems.TRANSPORT_MAP)) {
                mapRefs.add(new MapItemRef() {
                    @Override
                    public TransportFlameLogic.FlameTarget getTarget() {
                        return slotStack.get(ModDataComponents.FLAME_TARGET);
                    }

                    @Override
                    public void updateTarget(TransportFlameLogic.FlameTarget newTarget) {
                        slotStack.set(ModDataComponents.FLAME_TARGET, newTarget);
                        slotStack.set(net.minecraft.core.component.DataComponents.DYED_COLOR, new net.minecraft.world.item.component.DyedItemColor(newTarget.colorRgb()));
                    }
                });
            } else {
                // Check if it is a Bundle containing transport maps
                BundleContents bundle = slotStack.get(net.minecraft.core.component.DataComponents.BUNDLE_CONTENTS);
                if (bundle != null && !bundle.isEmpty()) {
                    List<ItemStackTemplate> templates = bundle.items();
                    for (int i = 0; i < templates.size(); i++) {
                        ItemStackTemplate template = templates.get(i);
                        TransportFlameLogic.FlameTarget target = template.get(ModDataComponents.FLAME_TARGET);
                        if (target != null || template.item().value() == ModItems.TRANSPORT_MAP) {
                            final int templateIndex = i;
                            mapRefs.add(new MapItemRef() {
                                @Override
                                public TransportFlameLogic.FlameTarget getTarget() {
                                    return target;
                                }

                                @Override
                                public void updateTarget(TransportFlameLogic.FlameTarget newTarget) {
                                    BundleContents currentBundle = slotStack.get(net.minecraft.core.component.DataComponents.BUNDLE_CONTENTS);
                                    if (currentBundle != null && templateIndex < currentBundle.items().size()) {
                                        List<ItemStackTemplate> updatedTemplates = new ArrayList<>(currentBundle.items());
                                        ItemStack stackInBundle = updatedTemplates.get(templateIndex).create();
                                        stackInBundle.set(ModDataComponents.FLAME_TARGET, newTarget);
                                        stackInBundle.set(net.minecraft.core.component.DataComponents.DYED_COLOR, new net.minecraft.world.item.component.DyedItemColor(newTarget.colorRgb()));
                                        updatedTemplates.set(templateIndex, ItemStackTemplate.fromNonEmptyStack(stackInBundle));
                                        slotStack.set(net.minecraft.core.component.DataComponents.BUNDLE_CONTENTS, new BundleContents(updatedTemplates));
                                    }
                                }
                            });
                        }
                    }
                }
            }
        }

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
        List<TransportFlameLogic.FlameTarget> candidateTargets = new ArrayList<>();
        for (MapItemRef ref : mapRefs) {
            candidateTargets.add(ref.getTarget());
        }

        // 4. Decide action
        TransportFlameLogic.PlacementDecision decision = TransportFlameLogic.decidePlacementAction(
            pos.getX(), pos.getY(), pos.getZ(), dimensionId,
            candidateTargets,
            flameChecker,
            new Random()
        );

        // 5. Execute decision
        switch (decision.actionType()) {
            case KEEP_EXISTING_MAP -> {
                player.sendSystemMessage(net.minecraft.network.chat.Component.translatable("message.mc3ver.map_already_linked").withStyle(net.minecraft.ChatFormatting.YELLOW));
            }
            case RELINK_OLD_MAP -> {
                for (MapItemRef ref : mapRefs) {
                    if (decision.targetToRelink() != null && decision.targetToRelink().equals(ref.getTarget())) {
                        ref.updateTarget(decision.newTarget());
                        break;
                    }
                }
                player.sendSystemMessage(net.minecraft.network.chat.Component.translatable("message.mc3ver.map_relinked").withStyle(net.minecraft.ChatFormatting.GREEN));
            }
            case CREATE_NEW_MAP -> {
                ItemStack mapStack = new ItemStack(ModItems.TRANSPORT_MAP);
                mapStack.set(ModDataComponents.FLAME_TARGET, decision.newTarget());
                mapStack.set(net.minecraft.core.component.DataComponents.DYED_COLOR, new net.minecraft.world.item.component.DyedItemColor(decision.newTarget().colorRgb()));

                boolean added = player.getInventory().add(mapStack);
                if (!added) {
                    player.drop(mapStack, false);
                }
            }
        }
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
