package net.frank.mc3ver.transport;

import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.Identifier;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.ItemUseAnimation;
import net.minecraft.world.item.ItemUtils;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;

import net.minecraft.world.item.component.TooltipDisplay;

import java.util.List;
import java.util.Set;
import java.util.function.Consumer;

public class TransportMapItem extends Item {

    public TransportMapItem(Properties properties) {
        super(properties);
    }

    @Override
    public ItemUseAnimation getUseAnimation(ItemStack stack) {
        return ItemUseAnimation.BOW;
    }

    @Override
    public int getUseDuration(ItemStack stack, LivingEntity entity) {
        return TransportFlameLogic.CHANNELING_TICKS;
    }

    @Override
    public InteractionResult use(Level level, Player player, InteractionHand hand) {
        ItemStack stack = player.getItemInHand(hand);
        TransportFlameLogic.FlameTarget target = stack.get(ModDataComponents.FLAME_TARGET);

        if (target == null) {
            if (!level.isClientSide()) {
                player.sendSystemMessage(Component.translatable("message.mc3ver.no_target").withStyle(ChatFormatting.RED));
            }
            return InteractionResult.FAIL;
        }

        return ItemUtils.startUsingInstantly(level, player, hand);
    }

    @Override
    public void onUseTick(Level level, LivingEntity livingEntity, ItemStack stack, int remainingUseDuration) {
        int ticksUsed = getUseDuration(stack, livingEntity) - remainingUseDuration;

        if (level.isClientSide()) {
            double x = livingEntity.getX() + (level.getRandom().nextDouble() - 0.5) * 1.5;
            double y = livingEntity.getY() + level.getRandom().nextDouble() * 2.0;
            double z = livingEntity.getZ() + (level.getRandom().nextDouble() - 0.5) * 1.5;

            level.addParticle(ParticleTypes.PORTAL, x, y, z,
                (level.getRandom().nextDouble() - 0.5) * 2.0,
                (level.getRandom().nextDouble() - 0.5) * 2.0,
                (level.getRandom().nextDouble() - 0.5) * 2.0);
        }

        if (ticksUsed % 10 == 0) {
            float pitch = 0.8f + ((float) ticksUsed / TransportFlameLogic.CHANNELING_TICKS) * 0.6f;
            level.playSound(
                null,
                livingEntity.getX(), livingEntity.getY(), livingEntity.getZ(),
                SoundEvents.PORTAL_TRIGGER,
                SoundSource.PLAYERS,
                0.5f,
                pitch
            );
        }
    }

    @Override
    public ItemStack finishUsingItem(ItemStack stack, Level level, LivingEntity livingEntity) {
        if (!level.isClientSide() && livingEntity instanceof ServerPlayer player) {
            MinecraftServer server = player.level().getServer();
            if (server == null) {
                return stack;
            }

            TransportFlameLogic.FlameTarget target = stack.get(ModDataComponents.FLAME_TARGET);

            if (target == null) {
                player.sendSystemMessage(Component.translatable("message.mc3ver.no_target").withStyle(ChatFormatting.RED));
                return stack;
            }

            ResourceKey<Level> targetDimKey = ResourceKey.create(
                Registries.DIMENSION,
                Identifier.parse(target.dimension())
            );
            ServerLevel targetLevel = server.getLevel(targetDimKey);

            if (targetLevel == null) {
                player.sendSystemMessage(Component.translatable("message.mc3ver.flame_extinguished").withStyle(ChatFormatting.RED));
                return stack;
            }

            BlockPos targetPos = new BlockPos(target.x(), target.y(), target.z());
            boolean flameExists = targetLevel.getBlockState(targetPos).is(ModBlocks.TRANSPORT_FLAME);

            TransportFlameLogic.TeleportOutcome outcome = TransportFlameLogic.evaluateTeleport(
                target,
                flameExists,
                player.getBlockX(),
                player.getBlockY(),
                player.getBlockZ(),
                player.level().dimension().identifier().toString()
            );

            switch (outcome) {
                case FLAME_EXTINGUISHED, INVALID_TARGET -> {
                    player.sendSystemMessage(Component.translatable("message.mc3ver.flame_extinguished").withStyle(ChatFormatting.RED));
                    player.level().playSound(null, player.getX(), player.getY(), player.getZ(), SoundEvents.FIRE_EXTINGUISH, SoundSource.PLAYERS, 0.7f, 1.0f);
                }
                case ALREADY_AT_TARGET -> {
                    player.sendSystemMessage(Component.translatable("message.mc3ver.already_at_target").withStyle(ChatFormatting.YELLOW));
                }
                case SUCCESS -> {
                    double[] coords = TransportFlameLogic.calculateArrivalCoordinates(target);
                    player.teleportTo(targetLevel, coords[0], coords[1], coords[2], Set.of(), player.getYRot(), player.getXRot(), false);
                    
                    targetLevel.playSound(null, coords[0], coords[1], coords[2], SoundEvents.PORTAL_TRAVEL, SoundSource.PLAYERS, 0.8f, 1.0f);
                    player.sendSystemMessage(Component.translatable("message.mc3ver.teleport_success").withStyle(ChatFormatting.AQUA));
                    
                    player.getCooldowns().addCooldown(stack, TransportFlameLogic.COOLDOWN_TICKS);
                }
            }
        }
        return stack;
    }

    @Override
    public void appendHoverText(ItemStack stack, TooltipContext context, TooltipDisplay display, Consumer<Component> tooltipAdder, TooltipFlag flag) {
        TransportFlameLogic.FlameTarget target = stack.get(ModDataComponents.FLAME_TARGET);
        if (target != null) {
            tooltipAdder.accept(Component.translatable("tooltip.mc3ver.destination", target.formatDimensionName()).withStyle(ChatFormatting.GRAY));
            tooltipAdder.accept(Component.translatable("tooltip.mc3ver.coordinates", target.formatCoordinates()).withStyle(ChatFormatting.DARK_AQUA));
        } else {
            tooltipAdder.accept(Component.translatable("tooltip.mc3ver.unlinked").withStyle(ChatFormatting.RED));
        }
    }
}
