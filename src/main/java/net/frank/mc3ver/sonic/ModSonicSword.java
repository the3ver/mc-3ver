package net.frank.mc3ver.sonic;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import net.fabricmc.fabric.api.event.player.AttackBlockCallback;
import net.fabricmc.fabric.api.networking.v1.PayloadTypeRegistry;
import net.fabricmc.fabric.api.networking.v1.ServerPlayConnectionEvents;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.frank.mc3ver.Mc3verMod;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Rarity;
import net.minecraft.world.item.ToolMaterial;

public final class ModSonicSword {

    public static final ResourceKey<Item> SONIC_SWORD_KEY =
            ResourceKey.create(Registries.ITEM, Mc3verMod.id("sonic_sword"));

    public static final Item SONIC_SWORD = Registry.register(
            BuiltInRegistries.ITEM,
            SONIC_SWORD_KEY,
            new SonicSwordItem(new Item.Properties()
                    .setId(SONIC_SWORD_KEY)
                    .sword(
                            ToolMaterial.DIAMOND,
                            SonicSwordLogic.attackDamageBonus(ToolMaterial.DIAMOND.attackDamageBonus()),
                            SonicSwordLogic.ATTACK_SPEED)
                    .rarity(Rarity.EPIC)));

    // Client meldet Drücken (charging=true) und Loslassen der Angriffstaste; die Ladezeit misst der Server
    public record BeamChargePayload(boolean charging) implements CustomPacketPayload {
        public static final Type<BeamChargePayload> TYPE = new Type<>(Mc3verMod.id("sonic_beam_charge"));
        public static final StreamCodec<RegistryFriendlyByteBuf, BeamChargePayload> CODEC =
                StreamCodec.composite(ByteBufCodecs.BOOL, BeamChargePayload::charging, BeamChargePayload::new);

        @Override
        public Type<BeamChargePayload> type() {
            return TYPE;
        }
    }

    private static final Map<UUID, Long> BEAM_CHARGE_START = new HashMap<>();

    private ModSonicSword() {}

    public static boolean holdsSonicSword(Player player) {
        return player.getMainHandItem().is(SONIC_SWORD);
    }

    public static void register() {
        PayloadTypeRegistry.serverboundPlay().register(BeamChargePayload.TYPE, BeamChargePayload.CODEC);
        ServerPlayNetworking.registerGlobalReceiver(
                BeamChargePayload.TYPE, (payload, context) -> onBeamCharge(context.player(), payload.charging()));
        ServerPlayConnectionEvents.DISCONNECT.register((handler, server) ->
                BEAM_CHARGE_START.remove(handler.getPlayer().getUUID()));
        AttackBlockCallback.EVENT.register((player, level, hand, pos, direction) ->
                holdsSonicSword(player) ? InteractionResult.FAIL : InteractionResult.PASS);
    }

    private static void onBeamCharge(ServerPlayer player, boolean charging) {
        long now = player.level().getGameTime();
        if (charging) {
            BEAM_CHARGE_START.put(player.getUUID(), now);
            return;
        }
        Long start = BEAM_CHARGE_START.remove(player.getUUID());
        ItemStack stack = player.getMainHandItem();
        if (start == null || !stack.is(SONIC_SWORD)) return;
        if (!SonicSwordLogic.canCharge(player.getCooldowns().isOnCooldown(stack), player.isSpectator())) return;
        long chargeTicks = SonicSwordLogic.chargeTicks(start, now);
        if (!SonicSwordLogic.fires(chargeTicks)) return;
        SonicSwordAbilities.beam(
                player.level(),
                player,
                stack,
                SonicSwordLogic.charge(chargeTicks, SonicSwordLogic.BEAM_FULL_CHARGE_TICKS));
    }
}
