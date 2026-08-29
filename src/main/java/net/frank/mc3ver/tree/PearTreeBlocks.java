package net.frank.mc3ver.tree;

import net.frank.mc3ver.Mc3verMod;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.RotatedPillarBlock;
import net.minecraft.world.level.block.SaplingBlock;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.properties.NoteBlockInstrument;
import net.minecraft.world.level.material.MapColor;
import net.minecraft.world.level.material.PushReaction;

public class PearTreeBlocks {

    // Block Keys
    public static final ResourceKey<Block> PEAR_LOG_KEY = ResourceKey.create(Registries.BLOCK, Mc3verMod.id("pear_log"));
    public static final ResourceKey<Block> STRIPPED_PEAR_LOG_KEY = ResourceKey.create(Registries.BLOCK, Mc3verMod.id("stripped_pear_log"));
    public static final ResourceKey<Block> PEAR_WOOD_KEY = ResourceKey.create(Registries.BLOCK, Mc3verMod.id("pear_wood"));
    public static final ResourceKey<Block> STRIPPED_PEAR_WOOD_KEY = ResourceKey.create(Registries.BLOCK, Mc3verMod.id("stripped_pear_wood"));
    public static final ResourceKey<Block> PEAR_PLANKS_KEY = ResourceKey.create(Registries.BLOCK, Mc3verMod.id("pear_planks"));
    public static final ResourceKey<Block> PEAR_LEAVES_KEY = ResourceKey.create(Registries.BLOCK, Mc3verMod.id("pear_leaves"));
    public static final ResourceKey<Block> PEAR_SAPLING_KEY = ResourceKey.create(Registries.BLOCK, Mc3verMod.id("pear_sapling"));

    // Item Keys
    public static final ResourceKey<Item> PEAR_LOG_ITEM_KEY = ResourceKey.create(Registries.ITEM, Mc3verMod.id("pear_log"));
    public static final ResourceKey<Item> STRIPPED_PEAR_LOG_ITEM_KEY = ResourceKey.create(Registries.ITEM, Mc3verMod.id("stripped_pear_log"));
    public static final ResourceKey<Item> PEAR_WOOD_ITEM_KEY = ResourceKey.create(Registries.ITEM, Mc3verMod.id("pear_wood"));
    public static final ResourceKey<Item> STRIPPED_PEAR_WOOD_ITEM_KEY = ResourceKey.create(Registries.ITEM, Mc3verMod.id("stripped_pear_wood"));
    public static final ResourceKey<Item> PEAR_PLANKS_ITEM_KEY = ResourceKey.create(Registries.ITEM, Mc3verMod.id("pear_planks"));
    public static final ResourceKey<Item> PEAR_LEAVES_ITEM_KEY = ResourceKey.create(Registries.ITEM, Mc3verMod.id("pear_leaves"));
    public static final ResourceKey<Item> PEAR_SAPLING_ITEM_KEY = ResourceKey.create(Registries.ITEM, Mc3verMod.id("pear_sapling"));

    // Blocks
    public static final Block PEAR_LOG = Registry.register(
        BuiltInRegistries.BLOCK,
        PEAR_LOG_KEY,
        new RotatedPillarBlock(
            BlockBehaviour.Properties.of()
                .setId(PEAR_LOG_KEY)
                .mapColor(MapColor.WOOD)
                .instrument(NoteBlockInstrument.BASS)
                .strength(2.0F)
                .sound(SoundType.WOOD)
                .ignitedByLava()
        )
    );

    public static final Block STRIPPED_PEAR_LOG = Registry.register(
        BuiltInRegistries.BLOCK,
        STRIPPED_PEAR_LOG_KEY,
        new RotatedPillarBlock(
            BlockBehaviour.Properties.of()
                .setId(STRIPPED_PEAR_LOG_KEY)
                .mapColor(MapColor.WOOD)
                .instrument(NoteBlockInstrument.BASS)
                .strength(2.0F)
                .sound(SoundType.WOOD)
                .ignitedByLava()
        )
    );

    public static final Block PEAR_WOOD = Registry.register(
        BuiltInRegistries.BLOCK,
        PEAR_WOOD_KEY,
        new RotatedPillarBlock(
            BlockBehaviour.Properties.of()
                .setId(PEAR_WOOD_KEY)
                .mapColor(MapColor.WOOD)
                .instrument(NoteBlockInstrument.BASS)
                .strength(2.0F)
                .sound(SoundType.WOOD)
                .ignitedByLava()
        )
    );

    public static final Block STRIPPED_PEAR_WOOD = Registry.register(
        BuiltInRegistries.BLOCK,
        STRIPPED_PEAR_WOOD_KEY,
        new RotatedPillarBlock(
            BlockBehaviour.Properties.of()
                .setId(STRIPPED_PEAR_WOOD_KEY)
                .mapColor(MapColor.WOOD)
                .instrument(NoteBlockInstrument.BASS)
                .strength(2.0F)
                .sound(SoundType.WOOD)
                .ignitedByLava()
        )
    );

    public static final Block PEAR_PLANKS = Registry.register(
        BuiltInRegistries.BLOCK,
        PEAR_PLANKS_KEY,
        new Block(
            BlockBehaviour.Properties.of()
                .setId(PEAR_PLANKS_KEY)
                .mapColor(MapColor.WOOD)
                .instrument(NoteBlockInstrument.BASS)
                .strength(2.0F, 3.0F)
                .sound(SoundType.WOOD)
                .ignitedByLava()
        )
    );

    public static final Block PEAR_LEAVES = Registry.register(
        BuiltInRegistries.BLOCK,
        PEAR_LEAVES_KEY,
        new PearLeavesBlock(
            BlockBehaviour.Properties.of()
                .setId(PEAR_LEAVES_KEY)
                .mapColor(MapColor.PLANT)
                .strength(0.2F)
                .randomTicks()
                .sound(SoundType.GRASS)
                .noOcclusion()
                .isValidSpawn(Blocks::ocelotOrParrot)
                .isSuffocating(Blocks::never)
                .isViewBlocking(Blocks::never)
                .ignitedByLava()
                .pushReaction(PushReaction.DESTROY)
                .isRedstoneConductor(Blocks::never)
        )
    );

    public static final Block PEAR_SAPLING = Registry.register(
        BuiltInRegistries.BLOCK,
        PEAR_SAPLING_KEY,
        new SaplingBlock(
            PearTreeSaplingGenerator.PEAR_TREE_GROWER,
            BlockBehaviour.Properties.of()
                .setId(PEAR_SAPLING_KEY)
                .mapColor(MapColor.PLANT)
                .noCollision()
                .randomTicks()
                .instabreak()
                .sound(SoundType.GRASS)
                .pushReaction(PushReaction.DESTROY)
        )
    );

    // BlockItems
    public static final Item PEAR_LOG_ITEM = Registry.register(
        BuiltInRegistries.ITEM,
        PEAR_LOG_ITEM_KEY,
        new BlockItem(PEAR_LOG, new Item.Properties().setId(PEAR_LOG_ITEM_KEY))
    );

    public static final Item STRIPPED_PEAR_LOG_ITEM = Registry.register(
        BuiltInRegistries.ITEM,
        STRIPPED_PEAR_LOG_ITEM_KEY,
        new BlockItem(STRIPPED_PEAR_LOG, new Item.Properties().setId(STRIPPED_PEAR_LOG_ITEM_KEY))
    );

    public static final Item PEAR_WOOD_ITEM = Registry.register(
        BuiltInRegistries.ITEM,
        PEAR_WOOD_ITEM_KEY,
        new BlockItem(PEAR_WOOD, new Item.Properties().setId(PEAR_WOOD_ITEM_KEY))
    );

    public static final Item STRIPPED_PEAR_WOOD_ITEM = Registry.register(
        BuiltInRegistries.ITEM,
        STRIPPED_PEAR_WOOD_ITEM_KEY,
        new BlockItem(STRIPPED_PEAR_WOOD, new Item.Properties().setId(STRIPPED_PEAR_WOOD_ITEM_KEY))
    );

    public static final Item PEAR_PLANKS_ITEM = Registry.register(
        BuiltInRegistries.ITEM,
        PEAR_PLANKS_ITEM_KEY,
        new BlockItem(PEAR_PLANKS, new Item.Properties().setId(PEAR_PLANKS_ITEM_KEY))
    );

    public static final Item PEAR_LEAVES_ITEM = Registry.register(
        BuiltInRegistries.ITEM,
        PEAR_LEAVES_ITEM_KEY,
        new BlockItem(PEAR_LEAVES, new Item.Properties().setId(PEAR_LEAVES_ITEM_KEY))
    );

    public static final Item PEAR_SAPLING_ITEM = Registry.register(
        BuiltInRegistries.ITEM,
        PEAR_SAPLING_ITEM_KEY,
        new BlockItem(PEAR_SAPLING, new Item.Properties().setId(PEAR_SAPLING_ITEM_KEY))
    );

    public static void register() {
        // Strippable Block Registry (Axe stripping)
        net.fabricmc.fabric.api.registry.StrippableBlockRegistry.register(PEAR_LOG, STRIPPED_PEAR_LOG);
        net.fabricmc.fabric.api.registry.StrippableBlockRegistry.register(PEAR_WOOD, STRIPPED_PEAR_WOOD);

        // Flammable Block Registry (Fire behavior)
        net.fabricmc.fabric.api.registry.FlammableBlockRegistry flammable = net.fabricmc.fabric.api.registry.FlammableBlockRegistry.getDefaultInstance();
        flammable.add(PEAR_LOG, 5, 5);
        flammable.add(STRIPPED_PEAR_LOG, 5, 5);
        flammable.add(PEAR_WOOD, 5, 5);
        flammable.add(STRIPPED_PEAR_WOOD, 5, 5);
        flammable.add(PEAR_PLANKS, 5, 20);
        flammable.add(PEAR_LEAVES, 30, 60);

        // Composting (Vanilla ComposterBlock)
        net.minecraft.world.level.block.ComposterBlock.COMPOSTABLES.put(PearTreeItems.PEAR, PearTreeLogic.getCompostChance("pear"));
        net.minecraft.world.level.block.ComposterBlock.COMPOSTABLES.put(PEAR_LEAVES_ITEM, PearTreeLogic.getCompostChance("pear_leaves"));
        net.minecraft.world.level.block.ComposterBlock.COMPOSTABLES.put(PEAR_SAPLING_ITEM, PearTreeLogic.getCompostChance("pear_sapling"));
    }
}
