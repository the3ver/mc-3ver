package net.frank.mc3ver.tree;

import net.frank.mc3ver.Mc3verMod;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.fabricmc.fabric.api.object.builder.v1.block.type.BlockSetTypeBuilder;
import net.fabricmc.fabric.api.object.builder.v1.block.type.WoodTypeBuilder;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.DoubleHighBlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.ButtonBlock;
import net.minecraft.world.level.block.DoorBlock;
import net.minecraft.world.level.block.FenceBlock;
import net.minecraft.world.level.block.FenceGateBlock;
import net.minecraft.world.level.block.PressurePlateBlock;
import net.minecraft.world.level.block.RotatedPillarBlock;
import net.minecraft.world.level.block.SaplingBlock;
import net.minecraft.world.level.block.SlabBlock;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.StairBlock;
import net.minecraft.world.level.block.TrapDoorBlock;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.properties.BlockSetType;
import net.minecraft.world.level.block.state.properties.NoteBlockInstrument;
import net.minecraft.world.level.block.state.properties.WoodType;
import net.minecraft.world.level.material.MapColor;
import net.minecraft.world.level.material.PushReaction;

public class PearTreeBlocks {

    // Wood and Set Types
    public static final BlockSetType PEAR_SET_TYPE = BlockSetTypeBuilder.copyOf(BlockSetType.OAK).register(Mc3verMod.id("pear"));
    public static final WoodType PEAR_WOOD_TYPE = WoodTypeBuilder.copyOf(WoodType.OAK).register(Mc3verMod.id("pear"), PEAR_SET_TYPE);

    // Block Keys
    public static final ResourceKey<Block> PEAR_LOG_KEY = ResourceKey.create(Registries.BLOCK, Mc3verMod.id("pear_log"));
    public static final ResourceKey<Block> STRIPPED_PEAR_LOG_KEY = ResourceKey.create(Registries.BLOCK, Mc3verMod.id("stripped_pear_log"));
    public static final ResourceKey<Block> PEAR_WOOD_KEY = ResourceKey.create(Registries.BLOCK, Mc3verMod.id("pear_wood"));
    public static final ResourceKey<Block> STRIPPED_PEAR_WOOD_KEY = ResourceKey.create(Registries.BLOCK, Mc3verMod.id("stripped_pear_wood"));
    public static final ResourceKey<Block> PEAR_PLANKS_KEY = ResourceKey.create(Registries.BLOCK, Mc3verMod.id("pear_planks"));
    public static final ResourceKey<Block> PEAR_STAIRS_KEY = ResourceKey.create(Registries.BLOCK, Mc3verMod.id("pear_stairs"));
    public static final ResourceKey<Block> PEAR_SLAB_KEY = ResourceKey.create(Registries.BLOCK, Mc3verMod.id("pear_slab"));
    public static final ResourceKey<Block> PEAR_FENCE_KEY = ResourceKey.create(Registries.BLOCK, Mc3verMod.id("pear_fence"));
    public static final ResourceKey<Block> PEAR_FENCE_GATE_KEY = ResourceKey.create(Registries.BLOCK, Mc3verMod.id("pear_fence_gate"));
    public static final ResourceKey<Block> PEAR_DOOR_KEY = ResourceKey.create(Registries.BLOCK, Mc3verMod.id("pear_door"));
    public static final ResourceKey<Block> PEAR_TRAPDOOR_KEY = ResourceKey.create(Registries.BLOCK, Mc3verMod.id("pear_trapdoor"));
    public static final ResourceKey<Block> PEAR_PRESSURE_PLATE_KEY = ResourceKey.create(Registries.BLOCK, Mc3verMod.id("pear_pressure_plate"));
    public static final ResourceKey<Block> PEAR_BUTTON_KEY = ResourceKey.create(Registries.BLOCK, Mc3verMod.id("pear_button"));
    public static final ResourceKey<Block> PEAR_LEAVES_KEY = ResourceKey.create(Registries.BLOCK, Mc3verMod.id("pear_leaves"));
    public static final ResourceKey<Block> PEAR_SAPLING_KEY = ResourceKey.create(Registries.BLOCK, Mc3verMod.id("pear_sapling"));

    // Item Keys
    public static final ResourceKey<Item> PEAR_LOG_ITEM_KEY = ResourceKey.create(Registries.ITEM, Mc3verMod.id("pear_log"));
    public static final ResourceKey<Item> STRIPPED_PEAR_LOG_ITEM_KEY = ResourceKey.create(Registries.ITEM, Mc3verMod.id("stripped_pear_log"));
    public static final ResourceKey<Item> PEAR_WOOD_ITEM_KEY = ResourceKey.create(Registries.ITEM, Mc3verMod.id("pear_wood"));
    public static final ResourceKey<Item> STRIPPED_PEAR_WOOD_ITEM_KEY = ResourceKey.create(Registries.ITEM, Mc3verMod.id("stripped_pear_wood"));
    public static final ResourceKey<Item> PEAR_PLANKS_ITEM_KEY = ResourceKey.create(Registries.ITEM, Mc3verMod.id("pear_planks"));
    public static final ResourceKey<Item> PEAR_STAIRS_ITEM_KEY = ResourceKey.create(Registries.ITEM, Mc3verMod.id("pear_stairs"));
    public static final ResourceKey<Item> PEAR_SLAB_ITEM_KEY = ResourceKey.create(Registries.ITEM, Mc3verMod.id("pear_slab"));
    public static final ResourceKey<Item> PEAR_FENCE_ITEM_KEY = ResourceKey.create(Registries.ITEM, Mc3verMod.id("pear_fence"));
    public static final ResourceKey<Item> PEAR_FENCE_GATE_ITEM_KEY = ResourceKey.create(Registries.ITEM, Mc3verMod.id("pear_fence_gate"));
    public static final ResourceKey<Item> PEAR_DOOR_ITEM_KEY = ResourceKey.create(Registries.ITEM, Mc3verMod.id("pear_door"));
    public static final ResourceKey<Item> PEAR_TRAPDOOR_ITEM_KEY = ResourceKey.create(Registries.ITEM, Mc3verMod.id("pear_trapdoor"));
    public static final ResourceKey<Item> PEAR_PRESSURE_PLATE_ITEM_KEY = ResourceKey.create(Registries.ITEM, Mc3verMod.id("pear_pressure_plate"));
    public static final ResourceKey<Item> PEAR_BUTTON_ITEM_KEY = ResourceKey.create(Registries.ITEM, Mc3verMod.id("pear_button"));
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

    public static final Block PEAR_STAIRS = Registry.register(
        BuiltInRegistries.BLOCK,
        PEAR_STAIRS_KEY,
        new StairBlock(
            PEAR_PLANKS.defaultBlockState(),
            BlockBehaviour.Properties.of()
                .setId(PEAR_STAIRS_KEY)
                .mapColor(MapColor.WOOD)
                .instrument(NoteBlockInstrument.BASS)
                .strength(2.0F, 3.0F)
                .sound(SoundType.WOOD)
                .ignitedByLava()
        )
    );

    public static final Block PEAR_SLAB = Registry.register(
        BuiltInRegistries.BLOCK,
        PEAR_SLAB_KEY,
        new SlabBlock(
            BlockBehaviour.Properties.of()
                .setId(PEAR_SLAB_KEY)
                .mapColor(MapColor.WOOD)
                .instrument(NoteBlockInstrument.BASS)
                .strength(2.0F, 3.0F)
                .sound(SoundType.WOOD)
                .ignitedByLava()
        )
    );

    public static final Block PEAR_FENCE = Registry.register(
        BuiltInRegistries.BLOCK,
        PEAR_FENCE_KEY,
        new FenceBlock(
            BlockBehaviour.Properties.of()
                .setId(PEAR_FENCE_KEY)
                .mapColor(MapColor.WOOD)
                .instrument(NoteBlockInstrument.BASS)
                .strength(2.0F, 3.0F)
                .sound(SoundType.WOOD)
                .ignitedByLava()
        )
    );

    public static final Block PEAR_FENCE_GATE = Registry.register(
        BuiltInRegistries.BLOCK,
        PEAR_FENCE_GATE_KEY,
        new FenceGateBlock(
            PEAR_WOOD_TYPE,
            BlockBehaviour.Properties.of()
                .setId(PEAR_FENCE_GATE_KEY)
                .mapColor(MapColor.WOOD)
                .instrument(NoteBlockInstrument.BASS)
                .strength(2.0F, 3.0F)
                .sound(SoundType.WOOD)
                .ignitedByLava()
        )
    );

    public static final Block PEAR_DOOR = Registry.register(
        BuiltInRegistries.BLOCK,
        PEAR_DOOR_KEY,
        new DoorBlock(
            PEAR_SET_TYPE,
            BlockBehaviour.Properties.of()
                .setId(PEAR_DOOR_KEY)
                .mapColor(MapColor.WOOD)
                .instrument(NoteBlockInstrument.BASS)
                .strength(3.0F)
                .sound(SoundType.WOOD)
                .noOcclusion()
                .ignitedByLava()
                .pushReaction(PushReaction.DESTROY)
        )
    );

    public static final Block PEAR_TRAPDOOR = Registry.register(
        BuiltInRegistries.BLOCK,
        PEAR_TRAPDOOR_KEY,
        new TrapDoorBlock(
            PEAR_SET_TYPE,
            BlockBehaviour.Properties.of()
                .setId(PEAR_TRAPDOOR_KEY)
                .mapColor(MapColor.WOOD)
                .instrument(NoteBlockInstrument.BASS)
                .strength(3.0F)
                .sound(SoundType.WOOD)
                .noOcclusion()
                .isValidSpawn(Blocks::never)
                .ignitedByLava()
        )
    );

    public static final Block PEAR_PRESSURE_PLATE = Registry.register(
        BuiltInRegistries.BLOCK,
        PEAR_PRESSURE_PLATE_KEY,
        new PressurePlateBlock(
            PEAR_SET_TYPE,
            BlockBehaviour.Properties.of()
                .setId(PEAR_PRESSURE_PLATE_KEY)
                .mapColor(MapColor.WOOD)
                .instrument(NoteBlockInstrument.BASS)
                .forceSolidOff()
                .noCollision()
                .strength(0.5F)
                .sound(SoundType.WOOD)
                .ignitedByLava()
                .pushReaction(PushReaction.DESTROY)
        )
    );

    public static final Block PEAR_BUTTON = Registry.register(
        BuiltInRegistries.BLOCK,
        PEAR_BUTTON_KEY,
        new ButtonBlock(
            PEAR_SET_TYPE,
            30,
            BlockBehaviour.Properties.of()
                .setId(PEAR_BUTTON_KEY)
                .noCollision()
                .strength(0.5F)
                .sound(SoundType.WOOD)
                .pushReaction(PushReaction.DESTROY)
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

    public static final Item PEAR_STAIRS_ITEM = Registry.register(
        BuiltInRegistries.ITEM,
        PEAR_STAIRS_ITEM_KEY,
        new BlockItem(PEAR_STAIRS, new Item.Properties().setId(PEAR_STAIRS_ITEM_KEY))
    );

    public static final Item PEAR_SLAB_ITEM = Registry.register(
        BuiltInRegistries.ITEM,
        PEAR_SLAB_ITEM_KEY,
        new BlockItem(PEAR_SLAB, new Item.Properties().setId(PEAR_SLAB_ITEM_KEY))
    );

    public static final Item PEAR_FENCE_ITEM = Registry.register(
        BuiltInRegistries.ITEM,
        PEAR_FENCE_ITEM_KEY,
        new BlockItem(PEAR_FENCE, new Item.Properties().setId(PEAR_FENCE_ITEM_KEY))
    );

    public static final Item PEAR_FENCE_GATE_ITEM = Registry.register(
        BuiltInRegistries.ITEM,
        PEAR_FENCE_GATE_ITEM_KEY,
        new BlockItem(PEAR_FENCE_GATE, new Item.Properties().setId(PEAR_FENCE_GATE_ITEM_KEY))
    );

    public static final Item PEAR_DOOR_ITEM = Registry.register(
        BuiltInRegistries.ITEM,
        PEAR_DOOR_ITEM_KEY,
        new DoubleHighBlockItem(PEAR_DOOR, new Item.Properties().setId(PEAR_DOOR_ITEM_KEY))
    );

    public static final Item PEAR_TRAPDOOR_ITEM = Registry.register(
        BuiltInRegistries.ITEM,
        PEAR_TRAPDOOR_ITEM_KEY,
        new BlockItem(PEAR_TRAPDOOR, new Item.Properties().setId(PEAR_TRAPDOOR_ITEM_KEY))
    );

    public static final Item PEAR_PRESSURE_PLATE_ITEM = Registry.register(
        BuiltInRegistries.ITEM,
        PEAR_PRESSURE_PLATE_ITEM_KEY,
        new BlockItem(PEAR_PRESSURE_PLATE, new Item.Properties().setId(PEAR_PRESSURE_PLATE_ITEM_KEY))
    );

    public static final Item PEAR_BUTTON_ITEM = Registry.register(
        BuiltInRegistries.ITEM,
        PEAR_BUTTON_ITEM_KEY,
        new BlockItem(PEAR_BUTTON, new Item.Properties().setId(PEAR_BUTTON_ITEM_KEY))
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
        flammable.add(PEAR_STAIRS, 5, 20);
        flammable.add(PEAR_SLAB, 5, 20);
        flammable.add(PEAR_FENCE, 5, 20);
        flammable.add(PEAR_FENCE_GATE, 5, 20);
        flammable.add(PEAR_DOOR, 5, 20);
        flammable.add(PEAR_TRAPDOOR, 5, 20);
        flammable.add(PEAR_LEAVES, 30, 60);

        // Composting (Vanilla ComposterBlock)
        net.minecraft.world.level.block.ComposterBlock.COMPOSTABLES.put(PearTreeItems.PEAR, PearTreeLogic.getCompostChance("pear"));
        net.minecraft.world.level.block.ComposterBlock.COMPOSTABLES.put(PEAR_LEAVES_ITEM, PearTreeLogic.getCompostChance("pear_leaves"));
        net.minecraft.world.level.block.ComposterBlock.COMPOSTABLES.put(PEAR_SAPLING_ITEM, PearTreeLogic.getCompostChance("pear_sapling"));
    }
}
