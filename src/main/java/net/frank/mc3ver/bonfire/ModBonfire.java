package net.frank.mc3ver.bonfire;

import net.frank.mc3ver.Mc3verMod;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.material.MapColor;
import net.minecraft.world.level.material.PushReaction;

import java.util.Set;

public final class ModBonfire {

    public static final ResourceKey<Block> BONFIRE_BLOCK_KEY =
        ResourceKey.create(Registries.BLOCK, Mc3verMod.id("bonfire"));

    public static final Block BONFIRE = Registry.register(
        BuiltInRegistries.BLOCK,
        BONFIRE_BLOCK_KEY,
        new BonfireBlock(
            BlockBehaviour.Properties.of()
                .setId(BONFIRE_BLOCK_KEY)
                .mapColor(MapColor.PODZOL)
                .strength(2.0f)
                .sound(SoundType.WOOD)
                .lightLevel(state -> BonfireLogic.lightLevel(state.getValue(BonfireBlock.LIT)))
                .pushReaction(PushReaction.IMMOVEABLE)
                .noOcclusion()
        )
    );

    public static final BlockEntityType<BonfireBlockEntity> BONFIRE_BLOCK_ENTITY = Registry.register(
        BuiltInRegistries.BLOCK_ENTITY_TYPE,
        Mc3verMod.id("bonfire"),
        new BlockEntityType<>(BonfireBlockEntity::new, Set.of(BONFIRE))
    );

    public static final ResourceKey<Item> BONFIRE_ITEM_KEY =
        ResourceKey.create(Registries.ITEM, Mc3verMod.id("bonfire"));

    public static final Item BONFIRE_ITEM = Registry.register(
        BuiltInRegistries.ITEM,
        BONFIRE_ITEM_KEY,
        new BonfireItem(new Item.Properties().setId(BONFIRE_ITEM_KEY))
    );

    private ModBonfire() {}

    public static void register() {
        // Triggers static init
    }
}
