package net.frank.mc3ver.tent;

import java.util.EnumMap;
import java.util.Map;
import java.util.Optional;
import java.util.function.Function;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.network.chat.Component;
import net.minecraft.stats.Stats;
import net.minecraft.util.StringRepresentable;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.SimpleMenuProvider;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.ContainerLevelAccess;
import net.minecraft.world.inventory.CraftingMenu;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.HorizontalDirectionalBlock;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.EnumProperty;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;

public class TentCanvasBlock extends HorizontalDirectionalBlock {

    public enum CanvasPart implements StringRepresentable {
        WALL_SIDE("wall_side", ExplorerTentLogic.TentPart.WALL_SIDE),
        WALL_LEFT("wall_left", ExplorerTentLogic.TentPart.WALL_LEFT),
        WALL_RIGHT("wall_right", ExplorerTentLogic.TentPart.WALL_RIGHT),
        WALL_FRONT_LEFT("wall_front_left", ExplorerTentLogic.TentPart.WALL_FRONT_LEFT),
        WALL_FRONT_RIGHT("wall_front_right", ExplorerTentLogic.TentPart.WALL_FRONT_RIGHT),
        WALL_CORNER_LEFT("wall_corner_left", ExplorerTentLogic.TentPart.WALL_CORNER_LEFT),
        WALL_CORNER_RIGHT("wall_corner_right", ExplorerTentLogic.TentPart.WALL_CORNER_RIGHT),
        WALL_CRAFTING_TABLE_RIGHT("wall_crafting_table_right", ExplorerTentLogic.TentPart.WALL_CRAFTING_TABLE_RIGHT),
        WALL_BACK("wall_back", ExplorerTentLogic.TentPart.WALL_BACK),
        ROOF_LEFT("roof_left", ExplorerTentLogic.TentPart.ROOF_LEFT),
        ROOF_PIPE_LEFT("roof_pipe_left", ExplorerTentLogic.TentPart.ROOF_PIPE_LEFT),
        ROOF_RIGHT("roof_right", ExplorerTentLogic.TentPart.ROOF_RIGHT),
        ROOF_FRONT_LEFT("roof_front_left", ExplorerTentLogic.TentPart.ROOF_FRONT_LEFT),
        ROOF_FRONT_RIGHT("roof_front_right", ExplorerTentLogic.TentPart.ROOF_FRONT_RIGHT),
        ROOF_CORNER_LEFT("roof_corner_left", ExplorerTentLogic.TentPart.ROOF_CORNER_LEFT),
        ROOF_CORNER_RIGHT("roof_corner_right", ExplorerTentLogic.TentPart.ROOF_CORNER_RIGHT),
        ROOF_RIDGE("roof_ridge", ExplorerTentLogic.TentPart.ROOF_RIDGE),
        ROOF_FRONT("roof_front", ExplorerTentLogic.TentPart.ROOF_FRONT),
        ROOF_BACK("roof_back", ExplorerTentLogic.TentPart.ROOF_BACK),
        ROOF_LANTERN_RIGHT("roof_lantern_right", ExplorerTentLogic.TentPart.ROOF_LANTERN_RIGHT);

        private final String name;
        private final ExplorerTentLogic.TentPart tentPart;

        CanvasPart(String name, ExplorerTentLogic.TentPart tentPart) {
            this.name = name;
            this.tentPart = tentPart;
        }

        @Override
        public String getSerializedName() {
            return name;
        }

        public ExplorerTentLogic.TentPart toTentPart() {
            return tentPart;
        }

        public static Optional<CanvasPart> fromTentPart(ExplorerTentLogic.TentPart tentPart) {
            for (CanvasPart part : values()) {
                if (part.tentPart == tentPart) {
                    return Optional.of(part);
                }
            }
            return Optional.empty();
        }
    }

    public static final EnumProperty<Direction> FACING = HorizontalDirectionalBlock.FACING;
    public static final EnumProperty<CanvasPart> PART = EnumProperty.create("part", CanvasPart.class);
    public static final EnumProperty<DyeColor> COLOR = EnumProperty.create("color", DyeColor.class);

    protected static final VoxelShape SHAPE_ROOF_PLANE = Block.box(0.0, 14.0, 0.0, 16.0, 16.0, 16.0);

    protected static final VoxelShape SHAPE_WALL_WEST = Block.box(0.0, 0.0, 0.0, 2.0, 16.0, 16.0);
    protected static final VoxelShape SHAPE_WALL_EAST = Block.box(14.0, 0.0, 0.0, 16.0, 16.0, 16.0);
    protected static final VoxelShape SHAPE_WALL_NORTH = Block.box(0.0, 0.0, 0.0, 16.0, 16.0, 2.0);
    protected static final VoxelShape SHAPE_WALL_SOUTH = Block.box(0.0, 0.0, 14.0, 16.0, 16.0, 16.0);

    // Regenspritzer landen auf der Kollisions-Oberkante (ohne Kollision: auf der Blockunterkante) und ragen
    // ~3 Pixel um ihren Mittelpunkt, durch die nur 2 Pixel dicke Plane also nach innen. Für Abfragen ohne
    // Entity (Partikel, Regen) bekommt jedes Dachteil deshalb eine Kappe 4 Pixel über der Oberkante.
    private static final VoxelShape RAIN_CAP = Block.box(0.0, 16.0, 0.0, 16.0, 20.0, 16.0);
    private static final double RAIN_LIFT = 4.0;

    // Mittelzellen: der First liegt bis zu einen halben Block über der Zelle, die Kappe folgt der Dachlinie
    private static final Map<Direction, VoxelShape> RIDGE_RAIN_CAP = byOuterSide(outer -> {
        VoxelShape cap = Shapes.empty();
        for (int d = 0; d < 16; d++) {
            double roof = Math.max(TentRoofLogic.roofTopAt(16 + d), TentRoofLogic.roofTopAt(17 + d)) - 16;
            cap = Shapes.or(cap, slab(outer, d, d + 1, 16.0, Math.ceil(roof + RAIN_LIFT)));
        }
        return cap;
    });

    // Seitliche Dachzellen: alles über der Dachlinie ist fest, damit man außen auf dem Dach steht und nicht hineinläuft
    private static final Map<Direction, VoxelShape> ROOF_SLOPE = byOuterSide(outer -> {
        VoxelShape slope = Shapes.empty();
        for (int d = 0; d < 16; d++) {
            double roof = Math.min(TentRoofLogic.roofTopAt(d), TentRoofLogic.roofTopAt(d + 1)) - 16;
            double bottom = Math.clamp(Math.floor(roof - TentRoofLogic.THICKNESS), 0.0, 15.0);
            slope = Shapes.or(slope, slab(outer, d, d + 1, bottom, 16.0));
        }
        return slope;
    });

    public static VoxelShape getRainShape(CanvasPart part, Direction facing) {
        VoxelShape shape = getWallShape(part, facing);
        return switch (part) {
            case ROOF_RIDGE, ROOF_FRONT, ROOF_BACK ->
                Shapes.or(shape, RIDGE_RAIN_CAP.get(facing.getCounterClockWise()));
            default -> part.name().startsWith("ROOF_") ? Shapes.or(shape, RAIN_CAP) : shape;
        };
    }

    private static Map<Direction, VoxelShape> byOuterSide(Function<Direction, VoxelShape> build) {
        Map<Direction, VoxelShape> shapes = new EnumMap<>(Direction.class);
        for (Direction dir : Direction.Plane.HORIZONTAL) shapes.put(dir, build.apply(dir));
        return shapes;
    }

    /** Streifen über die ganze Zeltlänge, d0..d1 Pixel von der Seite {@code from} entfernt. */
    private static VoxelShape slab(Direction from, double d0, double d1, double y0, double y1) {
        return switch (from) {
            case WEST -> Block.box(d0, y0, 0.0, d1, y1, 16.0);
            case EAST -> Block.box(16.0 - d1, y0, 0.0, 16.0 - d0, y1, 16.0);
            case NORTH -> Block.box(0.0, y0, d0, 16.0, y1, d1);
            case SOUTH -> Block.box(0.0, y0, 16.0 - d1, 16.0, y1, 16.0 - d0);
            default -> Shapes.block();
        };
    }

    public static int lightEmission(CanvasPart part) {
        return part == CanvasPart.ROOF_LANTERN_RIGHT ? 14 : 0;
    }

    public static VoxelShape getWallShape(CanvasPart part, Direction facing) {
        Direction leftDir = facing.getCounterClockWise();
        Direction rightDir = facing.getClockWise();
        Direction backDir = facing.getOpposite();

        VoxelShape leftShape = getSideShape(leftDir);
        VoxelShape rightShape = getSideShape(rightDir);
        VoxelShape backShape = getSideShape(backDir);

        return switch (part) {
            case WALL_LEFT -> leftShape;
            case WALL_RIGHT -> rightShape;
            case WALL_CRAFTING_TABLE_RIGHT -> Shapes.or(rightShape, getTableShape(rightDir));
            case WALL_BACK, ROOF_BACK -> backShape;
            case WALL_FRONT_LEFT, WALL_CORNER_LEFT -> Shapes.or(leftShape, backShape);
            case WALL_FRONT_RIGHT, WALL_CORNER_RIGHT -> Shapes.or(rightShape, backShape);
            case ROOF_FRONT, ROOF_RIDGE -> SHAPE_ROOF_PLANE;
            case ROOF_LEFT, ROOF_PIPE_LEFT -> getRoofSlopeShape(leftDir);
            case ROOF_RIGHT, ROOF_LANTERN_RIGHT -> getRoofSlopeShape(rightDir);
            case ROOF_FRONT_LEFT, ROOF_CORNER_LEFT -> Shapes.or(getRoofSlopeShape(leftDir), backShape);
            case ROOF_FRONT_RIGHT, ROOF_CORNER_RIGHT -> Shapes.or(getRoofSlopeShape(rightDir), backShape);
            default -> Shapes.block();
        };
    }

    static VoxelShape getTableShape(Direction rightDir) {
        return switch (rightDir) {
            case EAST -> Block.box(0.0, 0.0, 0.0, 14.0, 14.0, 16.0);
            case WEST -> Block.box(2.0, 0.0, 0.0, 16.0, 14.0, 16.0);
            case SOUTH -> Block.box(0.0, 0.0, 0.0, 16.0, 14.0, 14.0);
            case NORTH -> Block.box(0.0, 0.0, 2.0, 16.0, 14.0, 16.0);
            default -> Shapes.block();
        };
    }

    public static VoxelShape getRoofSlopeShape(Direction outerDir) {
        return ROOF_SLOPE.getOrDefault(outerDir, Shapes.block());
    }

    private static VoxelShape getSideShape(Direction dir) {
        return switch (dir) {
            case WEST -> SHAPE_WALL_WEST;
            case EAST -> SHAPE_WALL_EAST;
            case NORTH -> SHAPE_WALL_NORTH;
            case SOUTH -> SHAPE_WALL_SOUTH;
            default -> Shapes.block();
        };
    }

    public TentCanvasBlock(BlockBehaviour.Properties properties) {
        super(properties);
        this.registerDefaultState(this.stateDefinition
                .any()
                .setValue(FACING, Direction.NORTH)
                .setValue(PART, CanvasPart.WALL_LEFT)
                .setValue(COLOR, DyeColor.WHITE));
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        builder.add(FACING, PART, COLOR);
    }

    @Override
    protected VoxelShape getShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext context) {
        return getWallShape(state.getValue(PART), state.getValue(FACING));
    }

    @Override
    protected VoxelShape getCollisionShape(
            BlockState state, BlockGetter level, BlockPos pos, CollisionContext context) {
        if (context == CollisionContext.empty()) {
            return getRainShape(state.getValue(PART), state.getValue(FACING));
        }
        return getWallShape(state.getValue(PART), state.getValue(FACING));
    }

    public static boolean hasMenuProvider(CanvasPart part) {
        return part == CanvasPart.WALL_CRAFTING_TABLE_RIGHT;
    }

    public static MenuProvider createCraftingMenuProvider(Level level, BlockPos pos) {
        ContainerLevelAccess access =
                level != null && pos != null ? ContainerLevelAccess.create(level, pos) : ContainerLevelAccess.NULL;
        return new SimpleMenuProvider(
                (syncId, inv, p) -> new CraftingMenu(syncId, inv, access) {
                    @Override
                    public boolean stillValid(Player player) {
                        return stillValid(access, player, ModTentBlocks.TENT_CANVAS);
                    }
                },
                Component.translatable("container.crafting"));
    }

    @Override
    protected MenuProvider getMenuProvider(BlockState state, Level level, BlockPos pos) {
        if (hasMenuProvider(state.getValue(PART))) {
            return createCraftingMenuProvider(level, pos);
        }
        return super.getMenuProvider(state, level, pos);
    }

    @Override
    protected InteractionResult useWithoutItem(
            BlockState state, Level level, BlockPos pos, Player player, BlockHitResult hit) {
        if (player.isShiftKeyDown()) {
            if (!level.isClientSide()) {
                ModTentBlocks.dismantleTent(level, pos, player);
            }
            return InteractionResult.SUCCESS;
        }
        if (hasMenuProvider(state.getValue(PART))) {
            if (!level.isClientSide()) {
                player.openMenu(state.getMenuProvider(level, pos));
                player.awardStat(Stats.INTERACT_WITH_CRAFTING_TABLE);
            }
            return InteractionResult.SUCCESS;
        }
        return InteractionResult.PASS;
    }

    @Override
    public BlockState playerWillDestroy(Level level, BlockPos pos, BlockState state, Player player) {
        if (!level.isClientSide()) {
            ModTentBlocks.dismantleTent(level, pos, player);
        }
        return super.playerWillDestroy(level, pos, state, player);
    }
}
