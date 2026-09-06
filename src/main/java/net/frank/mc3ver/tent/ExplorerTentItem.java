package net.frank.mc3ver.tent;

import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.network.chat.Component;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BedPart;

import net.minecraft.world.item.component.TooltipDisplay;

import java.util.List;
import java.util.function.Consumer;

public class ExplorerTentItem extends Item {

    public ExplorerTentItem(Item.Properties properties) {
        super(properties);
    }

    @Override
    public void appendHoverText(ItemStack stack, TooltipContext context, TooltipDisplay display, Consumer<Component> tooltipAdder, TooltipFlag flag) {
        tooltipAdder.accept(Component.translatable("tooltip.mc3ver.explorer_tent").withStyle(ChatFormatting.GRAY));
    }

    @Override
    public InteractionResult useOn(UseOnContext context) {
        Level level = context.getLevel();
        BlockPos clickedPos = context.getClickedPos();
        Direction clickedFace = context.getClickedFace();
        Player player = context.getPlayer();

        BlockPos anchor = clickedFace == Direction.UP ? clickedPos.above() : clickedPos.relative(clickedFace);
        Direction facing = context.getHorizontalDirection();
        ExplorerTentLogic.Facing tentFacing = ExplorerTentLogic.Facing.fromDirectionName(facing.name());

        ExplorerTentLogic.WorldView worldView = new ExplorerTentLogic.WorldView() {
            @Override
            public boolean isSolid(int x, int y, int z) {
                BlockPos p = new BlockPos(x, y, z);
                return level.getBlockState(p).isFaceSturdy(level, p, Direction.UP);
            }

            @Override
            public boolean isReplaceable(int x, int y, int z) {
                BlockPos p = new BlockPos(x, y, z);
                return level.getBlockState(p).canBeReplaced();
            }
        };

        ExplorerTentLogic.PlacementResult result = ExplorerTentLogic.validatePlacement(
            anchor.getX(), anchor.getY(), anchor.getZ(), tentFacing, worldView
        );

        if (result.status() == ExplorerTentLogic.PlacementStatus.UNEVEN_GROUND) {
            if (player != null) {
                player.sendOverlayMessage(Component.translatable("message.mc3ver.tent_uneven_ground").withStyle(ChatFormatting.RED));
            }
            return InteractionResult.FAIL;
        }

        if (result.status() == ExplorerTentLogic.PlacementStatus.OBSTRUCTED) {
            if (player != null) {
                player.sendOverlayMessage(Component.translatable("message.mc3ver.tent_no_space").withStyle(ChatFormatting.RED));
            }
            return InteractionResult.FAIL;
        }

        if (level.isClientSide()) {
            return InteractionResult.SUCCESS;
        }

        ItemStack heldStack = context.getItemInHand();
        net.minecraft.world.item.component.DyedItemColor dyedColor = heldStack.get(net.minecraft.core.component.DataComponents.DYED_COLOR);
        String colorName = dyedColor != null
            ? ExplorerTentLogic.getColorNameFromRgb(dyedColor.rgb())
            : ExplorerTentLogic.DEFAULT_COLOR_NAME;
        net.minecraft.world.item.DyeColor tentColor = net.minecraft.world.item.DyeColor.byName(colorName, net.minecraft.world.item.DyeColor.WHITE);

        // Platzierung aller 24 Blöcke
        for (int relY = 0; relY < ExplorerTentLogic.HEIGHT; relY++) {
            for (int relX = -1; relX <= 1; relX++) {
                for (int relZ = 0; relZ < ExplorerTentLogic.LENGTH; relZ++) {
                    ExplorerTentLogic.TentPart part = ExplorerTentLogic.getTentPart(relX, relY, relZ);
                    ExplorerTentLogic.BlockCoord coord = ExplorerTentLogic.toWorldPos(
                        anchor.getX(), anchor.getY(), anchor.getZ(), tentFacing, relX, relY, relZ
                    );
                    BlockPos p = new BlockPos(coord.x(), coord.y(), coord.z());

                    BlockState stateToPlace = null;
                    switch (part) {
                        case AIR -> {
                            if (level.getBlockState(p).canBeReplaced()) {
                                level.setBlock(p, Blocks.AIR.defaultBlockState(), Block.UPDATE_ALL);
                            }
                        }
                        case POLE -> stateToPlace = ModTentBlocks.TENT_POLE.defaultBlockState()
                            .setValue(TentPoleBlock.FACING, facing);
                        case SLEEPING_BAG_FOOT -> stateToPlace = ModTentBlocks.TENT_SLEEPING_BAG.defaultBlockState()
                            .setValue(TentSleepingBagBlock.FACING, facing)
                            .setValue(TentSleepingBagBlock.PART, BedPart.FOOT);
                        case SLEEPING_BAG_HEAD -> stateToPlace = ModTentBlocks.TENT_SLEEPING_BAG.defaultBlockState()
                            .setValue(TentSleepingBagBlock.FACING, facing)
                            .setValue(TentSleepingBagBlock.PART, BedPart.HEAD);
                        case WALL_SIDE, WALL_LEFT -> stateToPlace = ModTentBlocks.TENT_CANVAS.defaultBlockState()
                            .setValue(TentCanvasBlock.FACING, facing)
                            .setValue(TentCanvasBlock.PART, TentCanvasBlock.CanvasPart.WALL_LEFT);
                        case WALL_RIGHT -> stateToPlace = ModTentBlocks.TENT_CANVAS.defaultBlockState()
                            .setValue(TentCanvasBlock.FACING, facing)
                            .setValue(TentCanvasBlock.PART, TentCanvasBlock.CanvasPart.WALL_RIGHT);
                        case WALL_CRAFTING_TABLE_RIGHT -> stateToPlace = ModTentBlocks.TENT_CANVAS.defaultBlockState()
                            .setValue(TentCanvasBlock.FACING, facing)
                            .setValue(TentCanvasBlock.PART, TentCanvasBlock.CanvasPart.WALL_CRAFTING_TABLE_RIGHT);
                        case WALL_FRONT_LEFT -> stateToPlace = ModTentBlocks.TENT_CANVAS.defaultBlockState()
                            .setValue(TentCanvasBlock.FACING, facing)
                            .setValue(TentCanvasBlock.PART, TentCanvasBlock.CanvasPart.WALL_FRONT_LEFT);
                        case WALL_FRONT_RIGHT -> stateToPlace = ModTentBlocks.TENT_CANVAS.defaultBlockState()
                            .setValue(TentCanvasBlock.FACING, facing)
                            .setValue(TentCanvasBlock.PART, TentCanvasBlock.CanvasPart.WALL_FRONT_RIGHT);
                        case WALL_CORNER_LEFT -> stateToPlace = ModTentBlocks.TENT_CANVAS.defaultBlockState()
                            .setValue(TentCanvasBlock.FACING, facing)
                            .setValue(TentCanvasBlock.PART, TentCanvasBlock.CanvasPart.WALL_CORNER_LEFT);
                        case WALL_CORNER_RIGHT -> stateToPlace = ModTentBlocks.TENT_CANVAS.defaultBlockState()
                            .setValue(TentCanvasBlock.FACING, facing)
                            .setValue(TentCanvasBlock.PART, TentCanvasBlock.CanvasPart.WALL_CORNER_RIGHT);
                        case WALL_BACK -> stateToPlace = ModTentBlocks.TENT_CANVAS.defaultBlockState()
                            .setValue(TentCanvasBlock.FACING, facing)
                            .setValue(TentCanvasBlock.PART, TentCanvasBlock.CanvasPart.WALL_BACK);
                        case ROOF_LEFT -> stateToPlace = ModTentBlocks.TENT_CANVAS.defaultBlockState()
                            .setValue(TentCanvasBlock.FACING, facing)
                            .setValue(TentCanvasBlock.PART, TentCanvasBlock.CanvasPart.ROOF_LEFT);
                        case ROOF_RIGHT -> stateToPlace = ModTentBlocks.TENT_CANVAS.defaultBlockState()
                            .setValue(TentCanvasBlock.FACING, facing)
                            .setValue(TentCanvasBlock.PART, TentCanvasBlock.CanvasPart.ROOF_RIGHT);
                        case ROOF_FRONT_LEFT -> stateToPlace = ModTentBlocks.TENT_CANVAS.defaultBlockState()
                            .setValue(TentCanvasBlock.FACING, facing)
                            .setValue(TentCanvasBlock.PART, TentCanvasBlock.CanvasPart.ROOF_FRONT_LEFT);
                        case ROOF_FRONT_RIGHT -> stateToPlace = ModTentBlocks.TENT_CANVAS.defaultBlockState()
                            .setValue(TentCanvasBlock.FACING, facing)
                            .setValue(TentCanvasBlock.PART, TentCanvasBlock.CanvasPart.ROOF_FRONT_RIGHT);
                        case ROOF_CORNER_LEFT -> stateToPlace = ModTentBlocks.TENT_CANVAS.defaultBlockState()
                            .setValue(TentCanvasBlock.FACING, facing)
                            .setValue(TentCanvasBlock.PART, TentCanvasBlock.CanvasPart.ROOF_CORNER_LEFT);
                        case ROOF_CORNER_RIGHT -> stateToPlace = ModTentBlocks.TENT_CANVAS.defaultBlockState()
                            .setValue(TentCanvasBlock.FACING, facing)
                            .setValue(TentCanvasBlock.PART, TentCanvasBlock.CanvasPart.ROOF_CORNER_RIGHT);
                        case ROOF_RIDGE -> stateToPlace = ModTentBlocks.TENT_CANVAS.defaultBlockState()
                            .setValue(TentCanvasBlock.FACING, facing)
                            .setValue(TentCanvasBlock.PART, TentCanvasBlock.CanvasPart.ROOF_RIDGE);
                        case ROOF_FRONT -> stateToPlace = ModTentBlocks.TENT_CANVAS.defaultBlockState()
                            .setValue(TentCanvasBlock.FACING, facing)
                            .setValue(TentCanvasBlock.PART, TentCanvasBlock.CanvasPart.ROOF_FRONT);
                        case ROOF_BACK -> stateToPlace = ModTentBlocks.TENT_CANVAS.defaultBlockState()
                            .setValue(TentCanvasBlock.FACING, facing)
                            .setValue(TentCanvasBlock.PART, TentCanvasBlock.CanvasPart.ROOF_BACK);
                    }

                    if (stateToPlace != null) {
                        if (stateToPlace.hasProperty(TentCanvasBlock.COLOR)) {
                            stateToPlace = stateToPlace.setValue(TentCanvasBlock.COLOR, tentColor);
                        }
                        level.setBlock(p, stateToPlace, Block.UPDATE_ALL);
                    }
                }
            }
        }

        // Sounds
        level.playSound(null, anchor, SoundEvents.WOOL_PLACE, SoundSource.BLOCKS, 1.0f, 1.0f);
        level.playSound(null, anchor, SoundEvents.WOOD_PLACE, SoundSource.BLOCKS, 1.0f, 1.0f);

        if (player != null && !player.getAbilities().instabuild) {
            context.getItemInHand().shrink(1);
        }

        if (player != null) {
            player.sendSystemMessage(Component.translatable("message.mc3ver.tent_placed").withStyle(ChatFormatting.GREEN));
        }

        return InteractionResult.SUCCESS;
    }
}
