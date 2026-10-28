package net.frank.mc3ver.tree;

import net.fabricmc.fabric.api.object.builder.v1.block.type.BlockSetTypeBuilder;
import net.fabricmc.fabric.api.object.builder.v1.block.type.WoodTypeBuilder;
import net.fabricmc.fabric.api.registry.FlammableBlockRegistry;
import net.fabricmc.fabric.api.registry.StrippableBlockRegistry;
import net.frank.mc3ver.Mc3verMod;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.DoubleHighBlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.ButtonBlock;
import net.minecraft.world.level.block.ComposterBlock;
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

import java.util.function.BiFunction;
import java.util.function.Function;

public class PearTreeBlocks {

    // Wood and Set Types
    public static final BlockSetType PEAR_SET_TYPE = BlockSetTypeBuilder.copyOf(BlockSetType.OAK).register(Mc3verMod.id("pear"));
    public static final WoodType PEAR_WOOD_TYPE = WoodTypeBuilder.copyOf(WoodType.OAK).register(Mc3verMod.id("pear"), PEAR_SET_TYPE);

    // Registriert einen Block; der ResourceKey entsteht aus dem Namen und wird als ID in die Properties gesetzt
    private static Block block(String name, Function<BlockBehaviour.Properties, Block> factory, BlockBehaviour.Properties properties) {
        ResourceKey<Block> key = ResourceKey.create(Registries.BLOCK, Mc3verMod.id(name));
        return Registry.register(BuiltInRegistries.BLOCK, key, factory.apply(properties.setId(key)));
    }

    // Registriert das Item zu einem Block unter demselben Namen
    private static Item blockItem(Block block, BiFunction<Block, Item.Properties, Item> factory) {
        ResourceKey<Item> key = ResourceKey.create(Registries.ITEM, BuiltInRegistries.BLOCK.getKey(block));
        return Registry.register(BuiltInRegistries.ITEM, key, factory.apply(block, new Item.Properties().setId(key)));
    }

    private static Item blockItem(Block block) {
        return blockItem(block, BlockItem::new);
    }

    // Gemeinsame Eigenschaften der Holzblöcke wie bei Eiche
    private static BlockBehaviour.Properties wood(float hardness, float resistance) {
        return BlockBehaviour.Properties.of()
            .mapColor(MapColor.WOOD)
            .instrument(NoteBlockInstrument.BASS)
            .strength(hardness, resistance)
            .sound(SoundType.WOOD);
    }

    // Blocks
    public static final Block PEAR_LOG = block("pear_log", RotatedPillarBlock::new, wood(2.0F, 2.0F).ignitedByLava());
    public static final Block STRIPPED_PEAR_LOG = block("stripped_pear_log", RotatedPillarBlock::new, wood(2.0F, 2.0F).ignitedByLava());
    public static final Block PEAR_WOOD = block("pear_wood", RotatedPillarBlock::new, wood(2.0F, 2.0F).ignitedByLava());
    public static final Block STRIPPED_PEAR_WOOD = block("stripped_pear_wood", RotatedPillarBlock::new, wood(2.0F, 2.0F).ignitedByLava());
    public static final Block PEAR_PLANKS = block("pear_planks", Block::new, wood(2.0F, 3.0F).ignitedByLava());
    public static final Block PEAR_STAIRS = block("pear_stairs",
        p -> new StairBlock(PEAR_PLANKS.defaultBlockState(), p), wood(2.0F, 3.0F).ignitedByLava());
    public static final Block PEAR_SLAB = block("pear_slab", SlabBlock::new, wood(2.0F, 3.0F).ignitedByLava());
    public static final Block PEAR_FENCE = block("pear_fence", FenceBlock::new, wood(2.0F, 3.0F).ignitedByLava());
    public static final Block PEAR_FENCE_GATE = block("pear_fence_gate",
        p -> new FenceGateBlock(PEAR_WOOD_TYPE, p), wood(2.0F, 3.0F).ignitedByLava());
    public static final Block PEAR_DOOR = block("pear_door",
        p -> new DoorBlock(PEAR_SET_TYPE, p),
        wood(3.0F, 3.0F)
            .noOcclusion()
            .ignitedByLava()
            .pushReaction(PushReaction.DESTROY));
    public static final Block PEAR_TRAPDOOR = block("pear_trapdoor",
        p -> new TrapDoorBlock(PEAR_SET_TYPE, p),
        wood(3.0F, 3.0F)
            .noOcclusion()
            .isValidSpawn(Blocks::never)
            .ignitedByLava());
    public static final Block PEAR_PRESSURE_PLATE = block("pear_pressure_plate",
        p -> new PressurePlateBlock(PEAR_SET_TYPE, p),
        BlockBehaviour.Properties.of()
            .mapColor(MapColor.WOOD)
            .instrument(NoteBlockInstrument.BASS)
            .forceSolidOff()
            .noCollision()
            .strength(0.5F)
            .sound(SoundType.WOOD)
            .ignitedByLava()
            .pushReaction(PushReaction.DESTROY));
    public static final Block PEAR_BUTTON = block("pear_button",
        p -> new ButtonBlock(PEAR_SET_TYPE, 30, p),
        BlockBehaviour.Properties.of()
            .noCollision()
            .strength(0.5F)
            .sound(SoundType.WOOD)
            .pushReaction(PushReaction.DESTROY));
    public static final Block PEAR_LEAVES = block("pear_leaves", PearLeavesBlock::new,
        BlockBehaviour.Properties.of()
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
            .isRedstoneConductor(Blocks::never));
    public static final Block PEAR_SAPLING = block("pear_sapling",
        p -> new SaplingBlock(PearTreeSaplingGenerator.PEAR_TREE_GROWER, p),
        BlockBehaviour.Properties.of()
            .mapColor(MapColor.PLANT)
            .noCollision()
            .randomTicks()
            .instabreak()
            .sound(SoundType.GRASS)
            .pushReaction(PushReaction.DESTROY));

    // BlockItems
    public static final Item PEAR_LOG_ITEM = blockItem(PEAR_LOG);
    public static final Item STRIPPED_PEAR_LOG_ITEM = blockItem(STRIPPED_PEAR_LOG);
    public static final Item PEAR_WOOD_ITEM = blockItem(PEAR_WOOD);
    public static final Item STRIPPED_PEAR_WOOD_ITEM = blockItem(STRIPPED_PEAR_WOOD);
    public static final Item PEAR_PLANKS_ITEM = blockItem(PEAR_PLANKS);
    public static final Item PEAR_STAIRS_ITEM = blockItem(PEAR_STAIRS);
    public static final Item PEAR_SLAB_ITEM = blockItem(PEAR_SLAB);
    public static final Item PEAR_FENCE_ITEM = blockItem(PEAR_FENCE);
    public static final Item PEAR_FENCE_GATE_ITEM = blockItem(PEAR_FENCE_GATE);
    public static final Item PEAR_DOOR_ITEM = blockItem(PEAR_DOOR, DoubleHighBlockItem::new);
    public static final Item PEAR_TRAPDOOR_ITEM = blockItem(PEAR_TRAPDOOR);
    public static final Item PEAR_PRESSURE_PLATE_ITEM = blockItem(PEAR_PRESSURE_PLATE);
    public static final Item PEAR_BUTTON_ITEM = blockItem(PEAR_BUTTON);
    public static final Item PEAR_LEAVES_ITEM = blockItem(PEAR_LEAVES);
    public static final Item PEAR_SAPLING_ITEM = blockItem(PEAR_SAPLING);

    public static void register() {
        // Strippable Block Registry (Axe stripping)
        StrippableBlockRegistry.register(PEAR_LOG, STRIPPED_PEAR_LOG);
        StrippableBlockRegistry.register(PEAR_WOOD, STRIPPED_PEAR_WOOD);

        // Flammable Block Registry (Fire behavior)
        FlammableBlockRegistry flammable = FlammableBlockRegistry.getDefaultInstance();
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
        ComposterBlock.COMPOSTABLES.put(PearTreeItems.PEAR, PearTreeLogic.getCompostChance("pear"));
        ComposterBlock.COMPOSTABLES.put(PEAR_LEAVES_ITEM, PearTreeLogic.getCompostChance("pear_leaves"));
        ComposterBlock.COMPOSTABLES.put(PEAR_SAPLING_ITEM, PearTreeLogic.getCompostChance("pear_sapling"));
    }
}
