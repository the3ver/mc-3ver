package net.frank.mc3ver.transport;

import net.frank.mc3ver.teleport.Dimensions;
import net.fabricmc.fabric.api.networking.v1.PayloadTypeRegistry;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.frank.mc3ver.Mc3verMod;
import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.core.component.DataComponents;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.server.level.ServerPlayer;

// Name beim Aufstellen: der Server öffnet beim Aufsteller einen Eingabe-Screen, der Client schickt den Namen zurück
public final class TransportFlameNaming {

    public record OpenNameScreenPayload(BlockPos pos) implements CustomPacketPayload {
        public static final Type<OpenNameScreenPayload> TYPE = new Type<>(Mc3verMod.id("open_flame_name_screen"));
        public static final StreamCodec<RegistryFriendlyByteBuf, OpenNameScreenPayload> CODEC = StreamCodec.composite(
            BlockPos.STREAM_CODEC, OpenNameScreenPayload::pos,
            OpenNameScreenPayload::new
        );

        @Override
        public Type<OpenNameScreenPayload> type() {
            return TYPE;
        }
    }

    public record SetNamePayload(BlockPos pos, String name) implements CustomPacketPayload {
        public static final Type<SetNamePayload> TYPE = new Type<>(Mc3verMod.id("set_flame_name"));
        public static final StreamCodec<RegistryFriendlyByteBuf, SetNamePayload> CODEC = StreamCodec.composite(
            BlockPos.STREAM_CODEC, SetNamePayload::pos,
            ByteBufCodecs.stringUtf8(TransportFlameLogic.MAX_FLAME_NAME_LENGTH * 4), SetNamePayload::name,
            SetNamePayload::new
        );

        @Override
        public Type<SetNamePayload> type() {
            return TYPE;
        }
    }

    private static final double MAX_NAMING_DISTANCE = 8.0;

    private TransportFlameNaming() {}

    public static void register() {
        PayloadTypeRegistry.clientboundPlay().register(OpenNameScreenPayload.TYPE, OpenNameScreenPayload.CODEC);
        PayloadTypeRegistry.serverboundPlay().register(SetNamePayload.TYPE, SetNamePayload.CODEC);
        ServerPlayNetworking.registerGlobalReceiver(SetNamePayload.TYPE,
            (payload, context) -> handleSetName(context.player(), payload.pos(), payload.name()));
    }

    public static boolean mayName(ServerPlayer player, TransportFlameBlockEntity flame) {
        return TransportFlameLogic.canNameFlame(flame.getName(), flame.getPlacer(), player.getUUID());
    }

    public static void openNameScreen(ServerPlayer player, BlockPos pos) {
        if (ServerPlayNetworking.canSend(player, OpenNameScreenPayload.TYPE)) {
            ServerPlayNetworking.send(player, new OpenNameScreenPayload(pos));
        }
    }

    private static void handleSetName(ServerPlayer player, BlockPos pos, String rawName) {
        if (!player.isWithinBlockInteractionRange(pos, MAX_NAMING_DISTANCE)) return;
        if (!(player.level().getBlockEntity(pos) instanceof TransportFlameBlockEntity flame)) return;
        if (!mayName(player, flame)) return;
        String name = TransportFlameLogic.sanitizeFlameName(rawName);
        if (name == null) return;

        applyName(player, flame, name);
        player.sendSystemMessage(Component.translatable("message.mc3ver.flame_named", name).withStyle(ChatFormatting.GREEN));
    }

    // Setzt den Namen an der Flamme und an allen Karten des Spielers, die auf sie zeigen
    public static void applyName(ServerPlayer player, TransportFlameBlockEntity flame, String name) {
        flame.setName(name);
        BlockPos pos = flame.getBlockPos();
        String dimensionId = Dimensions.id(player.level());
        for (TransportMapInventory.MapItemRef ref : TransportMapInventory.scan(player)) {
            TransportFlameLogic.FlameTarget target = ref.getTarget();
            if (target != null && target.isAt(pos.getX(), pos.getY(), pos.getZ(), dimensionId)) {
                ref.modify(stack -> stack.set(DataComponents.CUSTOM_NAME, Component.literal(name)));
            }
        }
    }
}
