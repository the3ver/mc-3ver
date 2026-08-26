package net.frank.mc3ver.transport;

import net.frank.mc3ver.Mc3verMod;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.state.BlockBehaviour;

public class ModBlocks {

    public static final ResourceKey<Block> TRANSPORT_FLAME_KEY = ResourceKey.create(
        Registries.BLOCK,
        Mc3verMod.id("transport_flame")
    );

    public static final ResourceKey<Item> TRANSPORT_FLAME_ITEM_KEY = ResourceKey.create(
        Registries.ITEM,
        Mc3verMod.id("transport_flame")
    );

    public static final Block TRANSPORT_FLAME = Registry.register(
        BuiltInRegistries.BLOCK,
        TRANSPORT_FLAME_KEY,
        new TransportFlameBlock(
            BlockBehaviour.Properties.of()
                .setId(TRANSPORT_FLAME_KEY)
                .noCollision()
                .lightLevel(state -> 14)
                .strength(0.3f)
                .sound(SoundType.STONE)
        )
    );

    public static final Item TRANSPORT_FLAME_ITEM = Registry.register(
        BuiltInRegistries.ITEM,
        TRANSPORT_FLAME_ITEM_KEY,
        new BlockItem(TRANSPORT_FLAME, new Item.Properties().setId(TRANSPORT_FLAME_ITEM_KEY))
    );

    public static void register() {
        // Triggers static initialization
    }
}
