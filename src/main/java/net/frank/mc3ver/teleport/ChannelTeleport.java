package net.frank.mc3ver.teleport;

import java.util.Set;
import net.minecraft.ChatFormatting;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.Level;

// Gemeinsamer Ablauf der Teleport-Items: kanalisieren mit Portal-Effekten, dann über Dimensionen springen
public final class ChannelTeleport {

    private ChannelTeleport() {}

    public static void channelEffects(Level level, LivingEntity entity, int ticksUsed, int channelTicks) {
        if (level.isClientSide()) {
            double x = entity.getX() + (level.getRandom().nextDouble() - 0.5) * 1.5;
            double y = entity.getY() + level.getRandom().nextDouble() * 2.0;
            double z = entity.getZ() + (level.getRandom().nextDouble() - 0.5) * 1.5;

            level.addParticle(
                    ParticleTypes.PORTAL,
                    x,
                    y,
                    z,
                    (level.getRandom().nextDouble() - 0.5) * 2.0,
                    (level.getRandom().nextDouble() - 0.5) * 2.0,
                    (level.getRandom().nextDouble() - 0.5) * 2.0);
        }

        if (ticksUsed % 10 == 0) {
            float pitch = 0.8f + ((float) ticksUsed / channelTicks) * 0.6f;
            level.playSound(
                    null,
                    entity.getX(),
                    entity.getY(),
                    entity.getZ(),
                    SoundEvents.PORTAL_TRIGGER,
                    SoundSource.PLAYERS,
                    0.5f,
                    pitch);
        }
    }

    // Ein Treffer während des Kanalisierens bricht ab, damit der Teleport kein Fluchtknopf im Kampf ist
    public static boolean interruptIfHurt(Level level, LivingEntity entity, int ticksUsed) {
        if (ticksUsed <= 0 || entity.hurtTime <= 0) {
            return false;
        }
        entity.stopUsingItem();
        if (!level.isClientSide() && entity instanceof ServerPlayer player) {
            player.sendOverlayMessage(Component.translatable("message.mc3ver.teleport_interrupted")
                    .withStyle(ChatFormatting.RED));
            level.playSound(
                    null,
                    entity.getX(),
                    entity.getY(),
                    entity.getZ(),
                    SoundEvents.FIRE_EXTINGUISH,
                    SoundSource.PLAYERS,
                    0.5f,
                    1.4f);
        }
        return true;
    }

    public static void teleport(ServerPlayer player, ServerLevel targetLevel, double x, double y, double z) {
        player.teleportTo(targetLevel, x, y, z, Set.of(), player.getYRot(), player.getXRot(), false);
        targetLevel.playSound(null, x, y, z, SoundEvents.PORTAL_TRAVEL, SoundSource.PLAYERS, 0.8f, 1.0f);
    }
}
