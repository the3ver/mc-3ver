package net.frank.mc3ver.sonic;

import java.util.List;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.stats.Stats;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.OwnableEntity;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.decoration.ArmorStand;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;

// Wirkung von Schallwelle und Schallstrahl in der Welt; die Zahlen kommen aus SonicSwordLogic
final class SonicSwordAbilities {

    private SonicSwordAbilities() {}

    static void wave(ServerLevel level, Player player, ItemStack stack, float charge) {
        SonicSwordLogic.Wave wave = SonicSwordLogic.wave(charge);
        DamageSource source = level.damageSources().sonicBoom(player);
        Vec3 center = player.position().add(0, player.getBbHeight() / 2, 0);

        for (LivingEntity target : targets(level, player, new AABB(center, center).inflate(wave.radius()))) {
            if (target.distanceToSqr(center) > wave.radius() * wave.radius()) continue;
            target.hurtServer(level, source, wave.damage());
            target.addEffect(
                    new MobEffectInstance(
                            MobEffects.SLOWNESS, wave.paralysisTicks(), SonicSwordLogic.PARALYSIS_AMPLIFIER),
                    player);
            target.addEffect(new MobEffectInstance(MobEffects.WEAKNESS, wave.paralysisTicks(), 1), player);
        }

        // Ring aus Schallwellen auf Hüfthöhe
        int points = 12 + Math.round(12 * charge);
        for (int i = 0; i < points; i++) {
            double angle = 2 * Math.PI * i / points;
            level.sendParticles(
                    ParticleTypes.SONIC_BOOM,
                    center.x + Math.cos(angle) * wave.radius() * 0.7,
                    center.y,
                    center.z + Math.sin(angle) * wave.radius() * 0.7,
                    1,
                    0,
                    0,
                    0,
                    0);
        }
        level.playSound(
                null,
                player.getX(),
                player.getY(),
                player.getZ(),
                SoundEvents.WARDEN_SONIC_BOOM,
                SoundSource.PLAYERS,
                0.6f + 0.4f * charge,
                1.6f - 0.4f * charge);

        finish(player, stack, SonicSwordLogic.WAVE_COOLDOWN_TICKS);
    }

    static void beam(ServerLevel level, Player player, ItemStack stack, float charge) {
        SonicSwordLogic.Beam beam = SonicSwordLogic.beam(charge);
        DamageSource source = level.damageSources().sonicBoom(player);
        Vec3 eye = player.getEyePosition();
        Vec3 dir = player.getLookAngle().normalize();
        AABB box = new AABB(eye, eye.add(dir.scale(beam.range()))).inflate(SonicSwordLogic.BEAM_WIDTH);

        for (LivingEntity target : targets(level, player, box)) {
            Vec3 to = target.position().add(0, target.getBbHeight() / 2, 0).subtract(eye);
            double hit = SonicSwordLogic.beamHitDistance(
                    dir.x, dir.y, dir.z, to.x, to.y, to.z, beam.range(), SonicSwordLogic.BEAM_WIDTH);
            if (hit < 0) continue;
            target.hurtServer(level, source, beam.damage());
            double push = SonicSwordLogic.knockbackAfterResistance(
                    beam.knockback(), target.getAttributeValue(Attributes.KNOCKBACK_RESISTANCE));
            target.push(dir.x * push, SonicSwordLogic.BEAM_LIFT * push, dir.z * push);
            target.needsSync = true;
        }

        for (double d = 1.0; d <= beam.range(); d += 1.0) {
            Vec3 p = eye.add(dir.scale(d));
            level.sendParticles(ParticleTypes.SONIC_BOOM, p.x, p.y, p.z, 1, 0, 0, 0, 0);
        }
        level.playSound(
                null,
                player.getX(),
                player.getY(),
                player.getZ(),
                SoundEvents.WARDEN_SONIC_BOOM,
                SoundSource.PLAYERS,
                0.8f + 0.4f * charge,
                1.2f);

        finish(player, stack, SonicSwordLogic.BEAM_COOLDOWN_TICKS);
    }

    // Alle Lebewesen außer dem Träger, seinen zahmen Tieren und Rüstungsständern
    private static List<LivingEntity> targets(ServerLevel level, Player player, AABB box) {
        return level.getEntitiesOfClass(
                LivingEntity.class,
                box,
                e -> e != player
                        && e.isAlive()
                        && !(e instanceof ArmorStand)
                        && !(e instanceof OwnableEntity pet && pet.getOwner() == player));
    }

    private static void finish(Player player, ItemStack stack, int cooldownTicks) {
        player.getCooldowns().addCooldown(stack, cooldownTicks);
        player.awardStat(Stats.ITEM_USED.get(stack.getItem()));
        if (player instanceof ServerPlayer serverPlayer) {
            stack.hurtAndBreak(
                    SonicSwordLogic.DURABILITY_COST,
                    serverPlayer.level(),
                    serverPlayer,
                    item -> player.onEquippedItemBroken(item, EquipmentSlot.MAINHAND));
        }
    }
}
