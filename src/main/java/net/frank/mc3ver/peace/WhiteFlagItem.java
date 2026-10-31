package net.frank.mc3ver.peace;

import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;

public class WhiteFlagItem extends Item {

    public WhiteFlagItem(Item.Properties properties) {
        super(properties);
    }

    @Override
    public InteractionResult use(Level level, Player player, InteractionHand hand) {
        ItemStack stack = player.getItemInHand(hand);
        PacifistLogic.ToggleResult result = PacifistLogic.toggle(
                player.hasEffect(ModPeace.PACIFIST), player.getCooldowns().isOnCooldown(stack));

        if (result == PacifistLogic.ToggleResult.LOCKED) {
            return InteractionResult.FAIL;
        }

        if (!level.isClientSide()) {
            if (result == PacifistLogic.ToggleResult.ACTIVATED) {
                player.addEffect(new MobEffectInstance(
                        ModPeace.PACIFIST, MobEffectInstance.INFINITE_DURATION, 0, false, false, true));
                if (player instanceof ServerPlayer sp) {
                    sp.sendOverlayMessage(Component.translatable("message.mc3ver.pacifist_on"));
                }
                level.playSound(
                        null,
                        player.getX(),
                        player.getY(),
                        player.getZ(),
                        SoundEvents.ARMOR_EQUIP_LEATHER,
                        SoundSource.PLAYERS,
                        1.0f,
                        1.4f);
            } else {
                player.removeEffect(ModPeace.PACIFIST);
                if (player instanceof ServerPlayer sp) {
                    sp.sendOverlayMessage(Component.translatable("message.mc3ver.pacifist_off"));
                }
                level.playSound(
                        null,
                        player.getX(),
                        player.getY(),
                        player.getZ(),
                        SoundEvents.ARMOR_EQUIP_LEATHER,
                        SoundSource.PLAYERS,
                        1.0f,
                        0.8f);
            }
        }

        player.getCooldowns().addCooldown(stack, PacifistLogic.TOGGLE_COOLDOWN_TICKS);
        return InteractionResult.SUCCESS;
    }
}
