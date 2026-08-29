package net.frank.mc3ver.wand;

import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.stats.Stats;
import net.minecraft.util.RandomSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.hurtingprojectile.windcharge.WindCharge;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;

public class WindWandItem extends Item {

    public WindWandItem(Properties properties) {
        super(properties);
    }

    @Override
    public InteractionResult use(Level level, Player player, InteractionHand hand) {
        ItemStack stack = player.getItemInHand(hand);

        if (!WindWandLogic.canUse(player.getCooldowns().isOnCooldown(stack), player.isSpectator())) {
            return InteractionResult.FAIL;
        }

        // Sound auf allen Seiten abspielen
        level.playSound(
            null,
            player.getX(),
            player.getY(),
            player.getZ(),
            SoundEvents.WIND_CHARGE_THROW,
            SoundSource.PLAYERS,
            0.5f,
            0.4f / (level.getRandom().nextFloat() * 0.4f + 0.8f)
        );

        // Cooldown setzen
        player.getCooldowns().addCooldown(stack, WindWandLogic.COOLDOWN_TICKS);

        if (level instanceof ServerLevel serverLevel) {
            Vec3 lookVec = player.getLookAngle();
            RandomSource random = level.getRandom();

            // Streuung (Inaccuracy)
            double spreadX = (random.nextFloat() - 0.5f) * WindWandLogic.DEFAULT_INACCURACY * 2.0;
            double spreadY = (random.nextFloat() - 0.5f) * WindWandLogic.DEFAULT_INACCURACY * 2.0;
            double spreadZ = (random.nextFloat() - 0.5f) * WindWandLogic.DEFAULT_INACCURACY * 2.0;

            double[] velocity = WindWandLogic.calculateVelocity(
                lookVec.x,
                lookVec.y,
                lookVec.z,
                spreadX,
                spreadY,
                spreadZ,
                WindWandLogic.PROJECTILE_SPEED
            );

            WindCharge windCharge = new WindCharge(
                player,
                serverLevel,
                player.getX(),
                player.getEyePosition().y(),
                player.getZ()
            );

            windCharge.shoot(velocity[0], velocity[1], velocity[2], WindWandLogic.PROJECTILE_SPEED, 0.0f);
            serverLevel.addFreshEntity(windCharge);

            // Haltbarkeit um 1 reduzieren
            if (player instanceof ServerPlayer serverPlayer) {
                stack.hurtAndBreak(
                    1,
                    serverLevel,
                    serverPlayer,
                    item -> player.onEquippedItemBroken(item, hand == InteractionHand.MAIN_HAND ? EquipmentSlot.MAINHAND : EquipmentSlot.OFFHAND)
                );
            }
        }

        player.awardStat(Stats.ITEM_USED.get(this));
        return InteractionResult.SUCCESS;
    }
}
