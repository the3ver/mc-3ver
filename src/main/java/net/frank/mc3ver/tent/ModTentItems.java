package net.frank.mc3ver.tent;

import net.frank.mc3ver.Mc3verMod;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.item.Item;

public class ModTentItems {

    public static final ResourceKey<Item> EXPLORER_TENT_KEY = ResourceKey.create(
        Registries.ITEM,
        Mc3verMod.id("explorer_tent")
    );

    public static final Item EXPLORER_TENT = Registry.register(
        BuiltInRegistries.ITEM,
        EXPLORER_TENT_KEY,
        new ExplorerTentItem(
            new Item.Properties()
                .setId(EXPLORER_TENT_KEY)
                .stacksTo(1)
        )
    );

    public static void register() {
        // Triggers static init
    }
}
