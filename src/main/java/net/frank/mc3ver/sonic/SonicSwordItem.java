package net.frank.mc3ver.sonic;

import java.util.function.Consumer;
import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.ItemUseAnimation;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.component.TooltipDisplay;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;

// Rechtsklick lädt die Schallwelle auf, Loslassen löst sie aus
public class SonicSwordItem extends Item {

    private static final int USE_DURATION = 72000;

    public SonicSwordItem(Properties properties) {
        super(properties);
    }

    @Override
    public int getUseDuration(ItemStack stack, LivingEntity entity) {
        return USE_DURATION;
    }

    @Override
    public ItemUseAnimation getUseAnimation(ItemStack stack) {
        return ItemUseAnimation.TRIDENT;
    }

    @Override
    public InteractionResult use(Level level, Player player, InteractionHand hand) {
        ItemStack stack = player.getItemInHand(hand);
        if (!SonicSwordLogic.canCharge(player.getCooldowns().isOnCooldown(stack), player.isSpectator())) {
            return InteractionResult.FAIL;
        }
        player.startUsingItem(hand);
        return InteractionResult.CONSUME;
    }

    @Override
    public boolean releaseUsing(ItemStack stack, Level level, LivingEntity entity, int timeLeft) {
        int chargeTicks = getUseDuration(stack, entity) - timeLeft;
        if (!(entity instanceof Player player) || !SonicSwordLogic.fires(chargeTicks)) {
            return false;
        }
        if (level instanceof ServerLevel serverLevel) {
            SonicSwordAbilities.wave(
                    serverLevel,
                    player,
                    stack,
                    SonicSwordLogic.charge(chargeTicks, SonicSwordLogic.WAVE_FULL_CHARGE_TICKS));
        }
        return true;
    }

    // Links gedrückt halten lädt den Strahl auf, darum baut das Schwert keine Blöcke ab
    @Override
    public boolean canDestroyBlock(ItemStack stack, BlockState state, Level level, BlockPos pos, LivingEntity entity) {
        return false;
    }

    @Override
    public void appendHoverText(
            ItemStack stack,
            TooltipContext context,
            TooltipDisplay display,
            Consumer<Component> tooltipAdder,
            TooltipFlag flag) {
        tooltipAdder.accept(
                Component.translatable("tooltip.mc3ver.sonic_sword.wave").withStyle(ChatFormatting.LIGHT_PURPLE));
        tooltipAdder.accept(
                Component.translatable("tooltip.mc3ver.sonic_sword.beam").withStyle(ChatFormatting.LIGHT_PURPLE));
    }
}
