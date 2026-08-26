package net.frank.mc3ver.transport;

import net.frank.mc3ver.Mc3verMod;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.item.Item;

public class ModItems {

    public static final ResourceKey<Item> TRANSPORT_MAP_KEY = ResourceKey.create(
        Registries.ITEM,
        Mc3verMod.id("transport_map")
    );

    public static final Item TRANSPORT_MAP = Registry.register(
        BuiltInRegistries.ITEM,
        TRANSPORT_MAP_KEY,
        new TransportMapItem(new Item.Properties().setId(TRANSPORT_MAP_KEY).stacksTo(1))
    );

    public static void register() {
        // Triggers static initialization
    }
}
