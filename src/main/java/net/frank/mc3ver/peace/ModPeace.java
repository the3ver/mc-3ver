package net.frank.mc3ver.peace;

import net.frank.mc3ver.Mc3verMod;
import net.minecraft.core.Holder;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.monster.Enemy;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.fabricmc.fabric.api.entity.event.v1.ServerLivingEntityEvents;

public final class ModPeace {

    public static final Holder<MobEffect> PACIFIST = Registry.registerForHolder(
        BuiltInRegistries.MOB_EFFECT,
        Mc3verMod.id("pacifist"),
        new PacifistEffect()
    );

    public static final ResourceKey<Item> WHITE_FLAG_KEY =
        ResourceKey.create(Registries.ITEM, Mc3verMod.id("white_flag"));

    public static final Item WHITE_FLAG = Registry.register(
        BuiltInRegistries.ITEM,
        WHITE_FLAG_KEY,
        new WhiteFlagItem(new Item.Properties().setId(WHITE_FLAG_KEY).stacksTo(1))
    );

    private ModPeace() {}

    public static void register() {
        // Triggers static init

        ServerLivingEntityEvents.ALLOW_DAMAGE.register(
            (LivingEntity entity, DamageSource source, float amount) -> {
                if (entity instanceof Player p && p.hasEffect(PACIFIST)) {
                    boolean attackerIsEnemy = source.getEntity() instanceof Enemy;
                    if (PacifistLogic.shouldBlockDamage(true, attackerIsEnemy)) {
                        return false;
                    }
                }
                return true;
            }
        );

        // Gezähmte Wölfe sind nicht der Spieler und brechen den Frieden deshalb nicht
        ServerLivingEntityEvents.AFTER_DAMAGE.register(
            (LivingEntity entity, DamageSource source, float baseDamageTaken, float damageTaken, boolean blocked) -> {
                if (source.getEntity() instanceof ServerPlayer p
                        && p.hasEffect(PACIFIST)
                        && PacifistLogic.breaksPeace(true, entity instanceof Enemy)) {
                    p.removeEffect(PACIFIST);
                    p.getCooldowns().addCooldown(new ItemStack(WHITE_FLAG), PacifistLogic.BROKEN_PEACE_LOCK_TICKS);
                    p.sendOverlayMessage(Component.translatable("message.mc3ver.peace_broken"));
                }
            }
        );
    }
}
