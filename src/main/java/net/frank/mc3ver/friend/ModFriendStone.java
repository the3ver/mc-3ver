package net.frank.mc3ver.friend;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import io.netty.buffer.ByteBuf;
import net.fabricmc.fabric.api.attachment.v1.AttachmentRegistry;
import net.fabricmc.fabric.api.attachment.v1.AttachmentType;
import net.frank.mc3ver.Mc3verMod;
import net.minecraft.core.Registry;
import net.minecraft.core.UUIDUtil;
import net.minecraft.core.component.DataComponentType;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Rarity;

public final class ModFriendStone {

    public static final Codec<FriendStoneLogic.FriendLink> FRIEND_LINK_CODEC = RecordCodecBuilder.create(instance ->
        instance.group(
            UUIDUtil.CODEC.fieldOf("owner").forGetter(FriendStoneLogic.FriendLink::owner),
            Codec.STRING.fieldOf("owner_name").forGetter(FriendStoneLogic.FriendLink::ownerName),
            Codec.INT.fieldOf("generation").forGetter(FriendStoneLogic.FriendLink::generation)
        ).apply(instance, FriendStoneLogic.FriendLink::new)
    );

    public static final StreamCodec<ByteBuf, FriendStoneLogic.FriendLink> FRIEND_LINK_STREAM_CODEC = StreamCodec.composite(
        UUIDUtil.STREAM_CODEC, FriendStoneLogic.FriendLink::owner,
        ByteBufCodecs.STRING_UTF8, FriendStoneLogic.FriendLink::ownerName,
        ByteBufCodecs.VAR_INT, FriendStoneLogic.FriendLink::generation,
        FriendStoneLogic.FriendLink::new
    );

    public static final DataComponentType<FriendStoneLogic.FriendLink> FRIEND_LINK = Registry.register(
        BuiltInRegistries.DATA_COMPONENT_TYPE,
        Mc3verMod.id("friend_link"),
        DataComponentType.<FriendStoneLogic.FriendLink>builder()
            .persistent(FRIEND_LINK_CODEC)
            .networkSynchronized(FRIEND_LINK_STREAM_CODEC)
            .build()
    );

    // Zähler pro Spieler: Zurücksetzen erhöht ihn, geprägte Steine mit kleinerem Stand sind entwertet
    public static final AttachmentType<Integer> RESET_GENERATION = AttachmentRegistry.<Integer>builder()
        .persistent(Codec.INT)
        .initializer(() -> 0)
        .copyOnDeath()
        .buildAndRegister(Mc3verMod.id("friend_stone_generation"));

    public static final ResourceKey<Item> FRIENDSHIP_STONE_KEY =
        ResourceKey.create(Registries.ITEM, Mc3verMod.id("friendship_stone"));

    public static final Item FRIENDSHIP_STONE = Registry.register(
        BuiltInRegistries.ITEM,
        FRIENDSHIP_STONE_KEY,
        new FriendStoneItem(new Item.Properties()
            .setId(FRIENDSHIP_STONE_KEY)
            .stacksTo(FriendStoneLogic.UNBOUND_STACK_SIZE)
            .rarity(Rarity.UNCOMMON))
    );

    private ModFriendStone() {}

    public static int generation(Player player) {
        return player.getAttachedOrCreate(RESET_GENERATION);
    }

    public static int resetGeneration(Player player) {
        int next = generation(player) + 1;
        player.setAttached(RESET_GENERATION, next);
        return next;
    }

    public static void register() {
        // Triggers static init
    }
}
