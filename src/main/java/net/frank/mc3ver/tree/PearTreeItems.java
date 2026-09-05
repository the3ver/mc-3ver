package net.frank.mc3ver.tree;

import net.frank.mc3ver.Mc3verMod;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.food.FoodProperties;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Rarity;
import net.minecraft.world.item.component.Consumable;
import net.minecraft.world.item.component.Consumables;
import net.minecraft.world.item.consume_effects.ApplyStatusEffectsConsumeEffect;

import java.util.List;

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

    public static final ResourceKey<Item> GOLDEN_PEAR_KEY = ResourceKey.create(
        Registries.ITEM,
        Mc3verMod.id("golden_pear")
    );

    public static final FoodProperties GOLDEN_PEAR_FOOD = new FoodProperties.Builder()
        .nutrition(PearTreeLogic.GOLDEN_PEAR_NUTRITION)
        .saturationModifier(PearTreeLogic.GOLDEN_PEAR_SATURATION_MODIFIER)
        .alwaysEdible()
        .build();

    public static final Consumable GOLDEN_PEAR_CONSUMABLE = Consumables.defaultFood()
        .onConsume(new ApplyStatusEffectsConsumeEffect(List.of(
            new MobEffectInstance(MobEffects.JUMP_BOOST, PearTreeLogic.JUMP_BOOST_DURATION_TICKS, 1),
            new MobEffectInstance(MobEffects.SPEED, PearTreeLogic.SPEED_DURATION_TICKS, 1),
            new MobEffectInstance(MobEffects.SLOW_FALLING, PearTreeLogic.SLOW_FALLING_DURATION_TICKS, 0)
        )))
        .build();

    public static final Item GOLDEN_PEAR = Registry.register(
        BuiltInRegistries.ITEM,
        GOLDEN_PEAR_KEY,
        new Item(new Item.Properties()
            .setId(GOLDEN_PEAR_KEY)
            .food(GOLDEN_PEAR_FOOD, GOLDEN_PEAR_CONSUMABLE)
            .rarity(Rarity.RARE)
        )
    );

    public static void register() {
        // Triggers static initialization
    }
}
