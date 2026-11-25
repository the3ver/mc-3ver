package net.frank.mc3ver.datagen;

import com.google.common.hash.Hashing;
import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.UncheckedIOException;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.zip.Deflater;
import java.util.zip.GZIPOutputStream;
import net.fabricmc.fabric.api.datagen.v1.FabricPackOutput;
import net.frank.mc3ver.village.BakeryLayoutLogic;
import net.frank.mc3ver.village.BakingOvenBlock;
import net.frank.mc3ver.village.ModVillagers;
import net.minecraft.commands.arguments.blocks.BlockStateParser;
import net.minecraft.core.Direction;
import net.minecraft.core.FrontAndTop;
import net.minecraft.data.CachedOutput;
import net.minecraft.data.DataProvider;
import net.minecraft.data.PackOutput;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.IntTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.NbtIo;
import net.minecraft.nbt.NbtUtils;
import net.minecraft.resources.Identifier;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.DoorHingeSide;
import net.minecraft.world.level.block.state.properties.DoubleBlockHalf;
import net.minecraft.world.level.block.state.properties.SlabType;
import net.minecraft.world.level.block.state.properties.StairsShape;

/**
 * Schreibt die Bäckerei je Dorftyp als Strukturvorlage (NBT) nach data/mc3ver/structure/village/&lt;typ&gt;/houses.
 * Grundriss aus {@link BakeryLayoutLogic}, Material aus {@link Style}. Ohne Kompression gepackt, damit die Datei
 * auf jedem Rechner byte-gleich entsteht (CI prüft den Gleichstand).
 */
class BakeryStructureProvider implements DataProvider {

    static final String LOOT_TABLE = "mc3ver:chests/village/bakery";

    record Style(
            Block foundation,
            Block floor,
            Block wall,
            Block frame,
            Block door,
            Block roofStairs,
            Block roofPlanks,
            Block counter) {}

    static final Map<String, Style> STYLES = Map.of(
            "plains",
            new Style(
                    Blocks.COBBLESTONE,
                    Blocks.OAK_PLANKS,
                    Blocks.OAK_PLANKS,
                    Blocks.STRIPPED_OAK_LOG,
                    Blocks.OAK_DOOR,
                    Blocks.OAK_STAIRS,
                    Blocks.OAK_PLANKS,
                    Blocks.OAK_SLAB),
            "desert",
            new Style(
                    Blocks.CUT_SANDSTONE,
                    Blocks.SMOOTH_SANDSTONE,
                    Blocks.SMOOTH_SANDSTONE,
                    Blocks.CUT_SANDSTONE,
                    Blocks.JUNGLE_DOOR,
                    Blocks.SMOOTH_SANDSTONE_STAIRS,
                    Blocks.SMOOTH_SANDSTONE,
                    Blocks.SMOOTH_SANDSTONE_SLAB),
            "savanna",
            new Style(
                    Blocks.DYED_TERRACOTTA.orange(),
                    Blocks.ACACIA_PLANKS,
                    Blocks.ACACIA_PLANKS,
                    Blocks.ACACIA_LOG,
                    Blocks.ACACIA_DOOR,
                    Blocks.ACACIA_STAIRS,
                    Blocks.ACACIA_PLANKS,
                    Blocks.ACACIA_SLAB),
            "snowy",
            new Style(
                    Blocks.DIORITE,
                    Blocks.SPRUCE_PLANKS,
                    Blocks.SNOW_BLOCK,
                    Blocks.STRIPPED_SPRUCE_LOG,
                    Blocks.SPRUCE_DOOR,
                    Blocks.SPRUCE_STAIRS,
                    Blocks.SPRUCE_PLANKS,
                    Blocks.SPRUCE_SLAB),
            "taiga",
            new Style(
                    Blocks.COBBLESTONE,
                    Blocks.SPRUCE_PLANKS,
                    Blocks.SPRUCE_PLANKS,
                    Blocks.SPRUCE_LOG,
                    Blocks.SPRUCE_DOOR,
                    Blocks.SPRUCE_STAIRS,
                    Blocks.SPRUCE_PLANKS,
                    Blocks.SPRUCE_SLAB));

    private final PackOutput.PathProvider structures;

    BakeryStructureProvider(FabricPackOutput output) {
        this.structures = output.createPathProvider(PackOutput.Target.DATA_PACK, "structure");
    }

    @Override
    public CompletableFuture<?> run(CachedOutput output) {
        List<CompletableFuture<?>> writes = new ArrayList<>();
        for (String name : BakeryLayoutLogic.STYLES) {
            byte[] bytes = gzipUncompressed(structure(name, STYLES.get(name)));
            Identifier id = Identifier.parse(BakeryLayoutLogic.templateId(name));
            writes.add(CompletableFuture.runAsync(() -> {
                try {
                    output.writeIfNeeded(
                            structures.file(id, "nbt"), bytes, Hashing.sha256().hashBytes(bytes));
                } catch (IOException e) {
                    throw new UncheckedIOException(e);
                }
            }));
        }
        return CompletableFuture.allOf(writes.toArray(CompletableFuture[]::new));
    }

    @Override
    public String getName() {
        return "Bäckerei-Strukturen";
    }

    static CompoundTag structure(String name, Style style) {
        List<char[][]> layers = BakeryLayoutLogic.layers();
        Map<BlockState, Integer> palette = new LinkedHashMap<>();
        ListTag blocks = new ListTag();
        for (int y = 0; y < layers.size(); y++) {
            for (int z = 0; z < BakeryLayoutLogic.SIZE_Z; z++) {
                for (int x = 0; x < BakeryLayoutLogic.SIZE_X; x++) {
                    char cell = layers.get(y)[z][x];
                    if (cell == ' ') continue;
                    BlockState state = state(cell, x, style);
                    CompoundTag block = new CompoundTag();
                    block.put("pos", ints(x, y, z));
                    block.putInt("state", palette.computeIfAbsent(state, s -> palette.size()));
                    CompoundTag entity = blockEntity(cell, name, style);
                    if (entity != null) block.put("nbt", entity);
                    blocks.add(block);
                }
            }
        }
        ListTag paletteTag = new ListTag();
        palette.keySet().forEach(state -> paletteTag.add(NbtUtils.writeBlockState(state)));
        CompoundTag root = new CompoundTag();
        root.put("size", ints(BakeryLayoutLogic.SIZE_X, layers.size(), BakeryLayoutLogic.SIZE_Z));
        root.put("entities", new ListTag());
        root.put("blocks", blocks);
        root.put("palette", paletteTag);
        return NbtUtils.addCurrentDataVersion(root);
    }

    static BlockState state(char cell, int x, Style style) {
        return switch (cell) {
            case '.' -> Blocks.AIR.defaultBlockState();
            case 'S' -> style.foundation().defaultBlockState();
            case 'F' -> style.floor().defaultBlockState();
            case 'W' -> style.wall().defaultBlockState();
            case 'L' -> upright(style.frame());
            case 'P' -> style.roofPlanks().defaultBlockState();
            case 'K' -> Blocks.BRICKS.defaultBlockState();
            case 'D', 'd' ->
                style.door()
                        .defaultBlockState()
                        .setValue(BlockStateProperties.HORIZONTAL_FACING, Direction.EAST)
                        .setValue(BlockStateProperties.DOOR_HINGE, DoorHingeSide.RIGHT)
                        .setValue(
                                BlockStateProperties.DOUBLE_BLOCK_HALF,
                                cell == 'D' ? DoubleBlockHalf.LOWER : DoubleBlockHalf.UPPER);
            case 'G' -> pane(x == 1 || x == BakeryLayoutLogic.SIZE_X - 2);
            case 'O' -> ModVillagers.BAKING_OVEN.defaultBlockState().setValue(BakingOvenBlock.FACING, Direction.WEST);
            case 'C' ->
                Blocks.CHEST.defaultBlockState().setValue(BlockStateProperties.HORIZONTAL_FACING, Direction.WEST);
            case 'B' -> Blocks.BARREL.defaultBlockState().setValue(BlockStateProperties.FACING, Direction.UP);
            case 'H' -> Blocks.HAY_BLOCK.defaultBlockState();
            case 'T' -> style.counter().defaultBlockState().setValue(BlockStateProperties.SLAB_TYPE, SlabType.TOP);
            case 'e' -> torch(Direction.EAST);
            case 'w', 'x' -> torch(Direction.WEST);
            case 'J' -> jigsaw(FrontAndTop.WEST_UP);
            // Die Bewohner-Jigsaw liegt im Boden und zeigt nach oben, danach wird sie zu Boden
            case 'V' -> jigsaw(FrontAndTop.UP_NORTH);
            case '>' -> stairs(style, Direction.EAST, StairsShape.STRAIGHT);
            case '<' -> stairs(style, Direction.WEST, StairsShape.STRAIGHT);
            case 'v' -> stairs(style, Direction.SOUTH, StairsShape.STRAIGHT);
            case '^' -> stairs(style, Direction.NORTH, StairsShape.STRAIGHT);
            case '1' -> stairs(style, Direction.SOUTH, StairsShape.OUTER_LEFT);
            case '2' -> stairs(style, Direction.WEST, StairsShape.OUTER_LEFT);
            case '3' -> stairs(style, Direction.EAST, StairsShape.OUTER_LEFT);
            case '4' -> stairs(style, Direction.NORTH, StairsShape.OUTER_LEFT);
            default -> throw new IllegalArgumentException("Unbekanntes Zeichen im Grundriss: " + cell);
        };
    }

    private static CompoundTag blockEntity(char cell, String name, Style style) {
        return switch (cell) {
            case 'C' -> {
                CompoundTag chest = new CompoundTag();
                chest.putString("id", "minecraft:chest");
                chest.putString("LootTable", LOOT_TABLE);
                yield chest;
            }
            case 'J' ->
                jigsawEntity(
                        "minecraft:building_entrance",
                        "minecraft:village/" + name + "/streets",
                        "aligned",
                        stairs(style, Direction.EAST, StairsShape.STRAIGHT));
            case 'V' ->
                jigsawEntity(
                        "minecraft:bottom",
                        "minecraft:village/" + name + "/villagers",
                        "rollable",
                        style.floor().defaultBlockState());
            default -> null;
        };
    }

    private static CompoundTag jigsawEntity(String name, String pool, String joint, BlockState finalState) {
        CompoundTag tag = new CompoundTag();
        tag.putString("id", "minecraft:jigsaw");
        tag.putString("name", name);
        tag.putString("target", name);
        tag.putString("pool", pool);
        tag.putString("joint", joint);
        tag.putString("final_state", BlockStateParser.serialize(finalState));
        return tag;
    }

    private static BlockState jigsaw(FrontAndTop orientation) {
        return Blocks.JIGSAW.defaultBlockState().setValue(BlockStateProperties.ORIENTATION, orientation);
    }

    private static BlockState upright(Block block) {
        BlockState state = block.defaultBlockState();
        return state.hasProperty(BlockStateProperties.AXIS)
                ? state.setValue(BlockStateProperties.AXIS, Direction.Axis.Y)
                : state;
    }

    private static BlockState pane(boolean alongZ) {
        return Blocks.GLASS_PANE
                .defaultBlockState()
                .setValue(BlockStateProperties.NORTH, alongZ)
                .setValue(BlockStateProperties.SOUTH, alongZ)
                .setValue(BlockStateProperties.EAST, !alongZ)
                .setValue(BlockStateProperties.WEST, !alongZ);
    }

    private static BlockState torch(Direction facing) {
        return Blocks.WALL_TORCH.defaultBlockState().setValue(BlockStateProperties.HORIZONTAL_FACING, facing);
    }

    private static BlockState stairs(Style style, Direction facing, StairsShape shape) {
        return style.roofStairs()
                .defaultBlockState()
                .setValue(BlockStateProperties.HORIZONTAL_FACING, facing)
                .setValue(BlockStateProperties.STAIRS_SHAPE, shape);
    }

    private static ListTag ints(int... values) {
        ListTag list = new ListTag();
        for (int value : values) list.add(IntTag.valueOf(value));
        return list;
    }

    static byte[] gzipUncompressed(CompoundTag tag) {
        ByteArrayOutputStream bytes = new ByteArrayOutputStream();
        try (GZIPOutputStream gzip = new GZIPOutputStream(bytes) {
                    {
                        def.setLevel(Deflater.NO_COMPRESSION);
                    }
                };
                DataOutputStream data = new DataOutputStream(gzip)) {
            NbtIo.write(tag, data);
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }
        return bytes.toByteArray();
    }
}
