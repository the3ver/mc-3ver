package net.frank.mc3ver.bonfire;

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
import net.minecraft.world.item.component.TooltipDisplay;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;

import java.util.function.Consumer;

public class BonfireItem extends Item {

    public BonfireItem(Item.Properties properties) {
        super(properties);
    }

    @Override
    public void appendHoverText(ItemStack stack, TooltipContext context, TooltipDisplay display, Consumer<Component> tooltipAdder, TooltipFlag flag) {
        tooltipAdder.accept(Component.translatable("tooltip.mc3ver.bonfire").withStyle(ChatFormatting.GRAY));
    }

    @Override
    public InteractionResult useOn(UseOnContext context) {
        Level level = context.getLevel();
        BlockPos clickedPos = context.getClickedPos();
        Direction clickedFace = context.getClickedFace();
        Player player = context.getPlayer();

        BlockPos center = clickedFace == Direction.UP ? clickedPos.above() : clickedPos.relative(clickedFace);

        BonfireLogic.WorldView worldView = new BonfireLogic.WorldView() {
            @Override
            public boolean isSolid(int x, int y, int z) {
                BlockPos p = new BlockPos(x, y, z);
                return level.getBlockState(p).isFaceSturdy(level, p, Direction.UP);
            }

            @Override
            public boolean isReplaceable(int x, int y, int z) {
                return level.getBlockState(new BlockPos(x, y, z)).canBeReplaced();
            }
        };

        BonfireLogic.PlacementStatus status = BonfireLogic.validatePlacement(
            center.getX(), center.getY(), center.getZ(), worldView);

        if (status != BonfireLogic.PlacementStatus.OK) {
            if (player != null) {
                String key = status == BonfireLogic.PlacementStatus.OBSTRUCTED
                    ? "message.mc3ver.bonfire_no_space"
                    : "message.mc3ver.bonfire_uneven_ground";
                player.sendOverlayMessage(Component.translatable(key).withStyle(ChatFormatting.RED));
            }
            return InteractionResult.FAIL;
        }

        if (level.isClientSide()) {
            return InteractionResult.SUCCESS;
        }

        // Mitte zuerst, damit Randfelder beim Setzen schon einen Nachbarn vorfinden
        BlockState base = ModBonfire.BONFIRE.defaultBlockState();
        for (BonfireLogic.Part part : BonfireLogic.Part.values()) {
            level.setBlock(center.offset(part.dx(), 0, part.dz()),
                base.setValue(BonfireBlock.PART, part), Block.UPDATE_ALL);
        }

        level.playSound(null, center, SoundEvents.WOOD_PLACE, SoundSource.BLOCKS, 1.0f, 0.8f);
        level.playSound(null, center, SoundEvents.FIRECHARGE_USE, SoundSource.BLOCKS, 0.8f, 1.0f);

        if (player != null && !player.getAbilities().instabuild) {
            context.getItemInHand().shrink(1);
        }
        return InteractionResult.SUCCESS;
    }
}
