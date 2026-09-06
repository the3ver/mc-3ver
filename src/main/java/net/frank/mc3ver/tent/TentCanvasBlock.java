package net.frank.mc3ver.tent;

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
        WALL_SIDE("wall_side"),
        WALL_LEFT("wall_left"),
        WALL_RIGHT("wall_right"),
        WALL_FRONT_LEFT("wall_front_left"),
        WALL_FRONT_RIGHT("wall_front_right"),
        WALL_CORNER_LEFT("wall_corner_left"),
        WALL_CORNER_RIGHT("wall_corner_right"),
        WALL_CRAFTING_TABLE_RIGHT("wall_crafting_table_right"),
        WALL_BACK("wall_back"),
        ROOF_LEFT("roof_left"),
        ROOF_RIGHT("roof_right"),
        ROOF_FRONT_LEFT("roof_front_left"),
        ROOF_FRONT_RIGHT("roof_front_right"),
        ROOF_CORNER_LEFT("roof_corner_left"),
        ROOF_CORNER_RIGHT("roof_corner_right"),
        ROOF_RIDGE("roof_ridge"),
        ROOF_FRONT("roof_front"),
        ROOF_BACK("roof_back");

        private final String name;

        CanvasPart(String name) {
            this.name = name;
        }

        @Override
        public String getSerializedName() {
            return name;
        }

        public ExplorerTentLogic.TentPart toTentPart() {
            return switch (this) {
                case WALL_SIDE -> ExplorerTentLogic.TentPart.WALL_SIDE;
                case WALL_LEFT -> ExplorerTentLogic.TentPart.WALL_LEFT;
                case WALL_RIGHT -> ExplorerTentLogic.TentPart.WALL_RIGHT;
                case WALL_FRONT_LEFT -> ExplorerTentLogic.TentPart.WALL_FRONT_LEFT;
                case WALL_FRONT_RIGHT -> ExplorerTentLogic.TentPart.WALL_FRONT_RIGHT;
                case WALL_CORNER_LEFT -> ExplorerTentLogic.TentPart.WALL_CORNER_LEFT;
                case WALL_CORNER_RIGHT -> ExplorerTentLogic.TentPart.WALL_CORNER_RIGHT;
                case WALL_CRAFTING_TABLE_RIGHT -> ExplorerTentLogic.TentPart.WALL_CRAFTING_TABLE_RIGHT;
                case WALL_BACK -> ExplorerTentLogic.TentPart.WALL_BACK;
                case ROOF_LEFT -> ExplorerTentLogic.TentPart.ROOF_LEFT;
                case ROOF_RIGHT -> ExplorerTentLogic.TentPart.ROOF_RIGHT;
                case ROOF_FRONT_LEFT -> ExplorerTentLogic.TentPart.ROOF_FRONT_LEFT;
                case ROOF_FRONT_RIGHT -> ExplorerTentLogic.TentPart.ROOF_FRONT_RIGHT;
                case ROOF_CORNER_LEFT -> ExplorerTentLogic.TentPart.ROOF_CORNER_LEFT;
                case ROOF_CORNER_RIGHT -> ExplorerTentLogic.TentPart.ROOF_CORNER_RIGHT;
                case ROOF_RIDGE -> ExplorerTentLogic.TentPart.ROOF_RIDGE;
                case ROOF_FRONT -> ExplorerTentLogic.TentPart.ROOF_FRONT;
                case ROOF_BACK -> ExplorerTentLogic.TentPart.ROOF_BACK;
            };
        }
    }

    public static final EnumProperty<Direction> FACING = HorizontalDirectionalBlock.FACING;
    public static final EnumProperty<CanvasPart> PART = EnumProperty.create("part", CanvasPart.class);
    public static final EnumProperty<net.minecraft.world.item.DyeColor> COLOR = EnumProperty.create("color", net.minecraft.world.item.DyeColor.class);
    public static final com.mojang.serialization.MapCodec<TentCanvasBlock> CODEC = simpleCodec(TentCanvasBlock::new);

    protected static final VoxelShape SHAPE_ROOF_PLANE = Block.box(0.0, 14.0, 0.0, 16.0, 16.0, 16.0);

    protected static final VoxelShape SHAPE_WALL_WEST = Block.box(0.0, 0.0, 0.0, 2.0, 16.0, 16.0);
    protected static final VoxelShape SHAPE_WALL_EAST = Block.box(14.0, 0.0, 0.0, 16.0, 16.0, 16.0);
    protected static final VoxelShape SHAPE_WALL_NORTH = Block.box(0.0, 0.0, 0.0, 16.0, 16.0, 2.0);
    protected static final VoxelShape SHAPE_WALL_SOUTH = Block.box(0.0, 0.0, 14.0, 16.0, 16.0, 16.0);

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
            case ROOF_LEFT -> getRoofSlopeShape(leftDir);
            case ROOF_RIGHT -> getRoofSlopeShape(rightDir);
            case ROOF_FRONT_LEFT, ROOF_CORNER_LEFT -> Shapes.or(getRoofSlopeShape(leftDir), backShape);
            case ROOF_FRONT_RIGHT, ROOF_CORNER_RIGHT -> Shapes.or(getRoofSlopeShape(rightDir), backShape);
            default -> Shapes.block();
        };
    }

    private static VoxelShape getTableShape(Direction rightDir) {
        return switch (rightDir) {
            case EAST -> Block.box(0.0, 0.0, 0.0, 14.0, 14.0, 16.0);
            case WEST -> Block.box(2.0, 0.0, 0.0, 16.0, 14.0, 16.0);
            case SOUTH -> Block.box(0.0, 0.0, 0.0, 16.0, 14.0, 14.0);
            case NORTH -> Block.box(0.0, 0.0, 2.0, 16.0, 14.0, 16.0);
            default -> Shapes.block();
        };
    }

    public static VoxelShape getRoofSlopeShape(Direction outerDir) {
        return switch (outerDir) {
            case WEST -> Shapes.or(
                Block.box(0.0, 0.0, 0.0, 3.0, 16.0, 16.0),
                Block.box(3.0, 6.0, 0.0, 7.0, 16.0, 16.0),
                Block.box(7.0, 10.0, 0.0, 11.0, 16.0, 16.0),
                Block.box(11.0, 14.0, 0.0, 16.0, 16.0, 16.0)
            );
            case EAST -> Shapes.or(
                Block.box(13.0, 0.0, 0.0, 16.0, 16.0, 16.0),
                Block.box(9.0, 6.0, 0.0, 13.0, 16.0, 16.0),
                Block.box(5.0, 10.0, 0.0, 9.0, 16.0, 16.0),
                Block.box(0.0, 14.0, 0.0, 5.0, 16.0, 16.0)
            );
            case NORTH -> Shapes.or(
                Block.box(0.0, 0.0, 0.0, 16.0, 16.0, 3.0),
                Block.box(0.0, 6.0, 3.0, 16.0, 16.0, 7.0),
                Block.box(0.0, 10.0, 7.0, 16.0, 16.0, 11.0),
                Block.box(0.0, 14.0, 11.0, 16.0, 16.0, 16.0)
            );
            case SOUTH -> Shapes.or(
                Block.box(0.0, 0.0, 13.0, 16.0, 16.0, 16.0),
                Block.box(0.0, 6.0, 9.0, 16.0, 16.0, 13.0),
                Block.box(0.0, 10.0, 5.0, 16.0, 16.0, 9.0),
                Block.box(0.0, 14.0, 0.0, 16.0, 16.0, 5.0)
            );
            default -> Shapes.block();
        };
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
        this.registerDefaultState(this.stateDefinition.any()
            .setValue(FACING, Direction.NORTH)
            .setValue(PART, CanvasPart.WALL_LEFT)
            .setValue(COLOR, net.minecraft.world.item.DyeColor.WHITE));
    }

    @Override
    protected com.mojang.serialization.MapCodec<? extends HorizontalDirectionalBlock> codec() {
        return CODEC;
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
    protected VoxelShape getCollisionShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext context) {
        return getWallShape(state.getValue(PART), state.getValue(FACING));
    }

    public static boolean hasMenuProvider(CanvasPart part) {
        return part == CanvasPart.WALL_CRAFTING_TABLE_RIGHT;
    }

    public static MenuProvider createCraftingMenuProvider(Level level, BlockPos pos) {
        ContainerLevelAccess access = level != null && pos != null ? ContainerLevelAccess.create(level, pos) : ContainerLevelAccess.NULL;
        return new SimpleMenuProvider(
            (syncId, inv, p) -> new CraftingMenu(syncId, inv, access) {
                @Override
                public boolean stillValid(Player player) {
                    return stillValid(access, player, ModTentBlocks.TENT_CANVAS);
                }
            },
            Component.translatable("container.crafting")
        );
    }

    @Override
    protected MenuProvider getMenuProvider(BlockState state, Level level, BlockPos pos) {
        if (hasMenuProvider(state.getValue(PART))) {
            return createCraftingMenuProvider(level, pos);
        }
        return super.getMenuProvider(state, level, pos);
    }

    @Override
    protected InteractionResult useWithoutItem(BlockState state, Level level, BlockPos pos, Player player, BlockHitResult hit) {
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
