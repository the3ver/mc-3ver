package net.frank.mc3ver.transport;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import io.netty.buffer.ByteBuf;
import net.frank.mc3ver.Mc3verMod;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.component.DataComponentType;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;

import java.util.UUID;

public class ModDataComponents {

    public static final Codec<TransportFlameLogic.FlameTarget> FLAME_TARGET_CODEC = RecordCodecBuilder.create(instance ->
        instance.group(
            Codec.INT.fieldOf("x").forGetter(TransportFlameLogic.FlameTarget::x),
            Codec.INT.fieldOf("y").forGetter(TransportFlameLogic.FlameTarget::y),
            Codec.INT.fieldOf("z").forGetter(TransportFlameLogic.FlameTarget::z),
            Codec.STRING.fieldOf("dimension").forGetter(TransportFlameLogic.FlameTarget::dimension),
            Codec.INT.fieldOf("color_rgb").forGetter(TransportFlameLogic.FlameTarget::colorRgb),
            Codec.STRING.xmap(UUID::fromString, UUID::toString).fieldOf("flame_id").forGetter(TransportFlameLogic.FlameTarget::flameId)
        ).apply(instance, TransportFlameLogic.FlameTarget::new)
    );

    public static final StreamCodec<ByteBuf, TransportFlameLogic.FlameTarget> FLAME_TARGET_STREAM_CODEC = StreamCodec.composite(
        ByteBufCodecs.VAR_INT, TransportFlameLogic.FlameTarget::x,
        ByteBufCodecs.VAR_INT, TransportFlameLogic.FlameTarget::y,
        ByteBufCodecs.VAR_INT, TransportFlameLogic.FlameTarget::z,
        ByteBufCodecs.STRING_UTF8, TransportFlameLogic.FlameTarget::dimension,
        ByteBufCodecs.INT, TransportFlameLogic.FlameTarget::colorRgb,
        ByteBufCodecs.STRING_UTF8.map(UUID::fromString, UUID::toString), TransportFlameLogic.FlameTarget::flameId,
        TransportFlameLogic.FlameTarget::new
    );

    public static final DataComponentType<TransportFlameLogic.FlameTarget> FLAME_TARGET = Registry.register(
        BuiltInRegistries.DATA_COMPONENT_TYPE,
        Mc3verMod.id("flame_target"),
        DataComponentType.<TransportFlameLogic.FlameTarget>builder()
            .persistent(FLAME_TARGET_CODEC)
            .networkSynchronized(FLAME_TARGET_STREAM_CODEC)
            .build()
    );

    public static void register() {
        // Classloading triggers static initializer
    }
}
