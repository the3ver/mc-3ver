package net.frank.mc3ver.tree;

import net.frank.mc3ver.Mc3verMod;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.food.FoodProperties;
import net.minecraft.world.item.Item;

public class PearTreeItems {

    public static final ResourceKey<Item> PEAR_KEY = ResourceKey.create(
        Registries.ITEM,
        Mc3verMod.id("pear")
    );

    public static final FoodProperties PEAR_FOOD = new FoodProperties.Builder()
        .nutrition(PearTreeLogic.PEAR_NUTRITION)
        .saturationModifier(PearTreeLogic.PEAR_SATURATION_MODIFIER)
        .build();

    public static final Item PEAR = Registry.register(
        BuiltInRegistries.ITEM,
        PEAR_KEY,
        new Item(new Item.Properties().setId(PEAR_KEY).food(PEAR_FOOD))
    );

    public static void register() {
        // Triggers static initialization
    }
}
