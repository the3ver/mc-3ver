package net.frank.mc3ver.friend;

import net.frank.mc3ver.teleport.Dimensions;
import net.frank.mc3ver.teleport.ChannelTeleport;
import net.frank.mc3ver.teleport.ChannelTeleportItem;
import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.component.DataComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.Prediction;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.ItemUtils;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.component.TooltipDisplay;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;

import java.util.function.Consumer;

// Führt den Halter zu dem Spieler, der den Stein geprägt hat. Sneak + Rechtsklick prägt (ungeprägt) oder setzt zurück (eigener Stein).
public class FriendStoneItem extends ChannelTeleportItem {

    public FriendStoneItem(Item.Properties properties) {
        super(properties, FriendStoneLogic.CHANNELING_TICKS);
    }

    @Override
    public InteractionResult use(Level level, Player player, InteractionHand hand) {
        ItemStack stack = player.getItemInHand(hand);
        FriendStoneLogic.FriendLink link = stack.get(ModFriendStone.FRIEND_LINK);

        if (link == null) {
            if (!player.isShiftKeyDown()) {
                hint(level, player, "message.mc3ver.friendship_stone_how_to_bind");
                return InteractionResult.FAIL;
            }
            if (!level.isClientSide()) {
                bind(level, player, hand, stack);
            }
            return InteractionResult.SUCCESS;
        }

        if (link.owner().equals(player.getUUID())) {
            if (!player.isShiftKeyDown()) {
                hint(level, player, "message.mc3ver.friendship_stone_own");
                return InteractionResult.FAIL;
            }
            if (!level.isClientSide()) {
                int generation = ModFriendStone.resetGeneration(player);
                stack.set(ModFriendStone.FRIEND_LINK, new FriendStoneLogic.FriendLink(
                    player.getUUID(), player.getPlainTextName(), generation));
                player.sendSystemMessage(Component.translatable("message.mc3ver.friendship_stone_reset").withStyle(ChatFormatting.YELLOW));
                level.playSound(null, player.getX(), player.getY(), player.getZ(),
                    SoundEvents.AMETHYST_BLOCK_BREAK, SoundSource.PLAYERS, 1.0f, 0.8f);
            }
            return InteractionResult.SUCCESS;
        }

        return ItemUtils.startUsingInstantly(level, player, hand);
    }

    private static void hint(Level level, Player player, String key) {
        if (!level.isClientSide()) {
            player.sendOverlayMessage(Component.translatable(key).withStyle(ChatFormatting.GRAY));
        }
    }

    // Prägt genau einen Stein aus dem Stapel; geprägte Steine stapeln sich nicht
    private static void bind(Level level, Player player, InteractionHand hand, ItemStack stack) {
        ItemStack bound = stack.copyWithCount(1);
        bound.set(ModFriendStone.FRIEND_LINK, new FriendStoneLogic.FriendLink(
            player.getUUID(), player.getPlainTextName(), ModFriendStone.generation(player)));
        bound.set(DataComponents.MAX_STACK_SIZE, 1);
        stack.shrink(1);
        if (stack.isEmpty()) {
            player.setItemInHand(hand, bound);
        } else if (!player.getInventory().add(bound)) {
            player.drop(bound, false, Prediction.SERVER_ONLY);
        }
        player.sendSystemMessage(Component.translatable("message.mc3ver.friendship_stone_bound").withStyle(ChatFormatting.LIGHT_PURPLE));
        level.playSound(null, player.getX(), player.getY(), player.getZ(),
            SoundEvents.AMETHYST_BLOCK_CHIME, SoundSource.PLAYERS, 1.0f, 1.2f);
    }

    @Override
    public ItemStack finishUsingItem(ItemStack stack, Level level, LivingEntity livingEntity) {
        if (level.isClientSide() || !(livingEntity instanceof ServerPlayer player)) {
            return stack;
        }
        MinecraftServer server = player.level().getServer();
        FriendStoneLogic.FriendLink link = stack.get(ModFriendStone.FRIEND_LINK);
        ServerPlayer friend = link != null && server != null ? server.getPlayerList().getPlayer(link.owner()) : null;
        int friendGeneration = friend != null ? ModFriendStone.generation(friend) : 0;

        switch (FriendStoneLogic.checkTeleport(link, player.getUUID(), friend != null, friendGeneration)) {
            case UNBOUND -> player.sendSystemMessage(Component.translatable("message.mc3ver.friendship_stone_how_to_bind").withStyle(ChatFormatting.GRAY));
            case OWN_STONE -> player.sendSystemMessage(Component.translatable("message.mc3ver.friendship_stone_own").withStyle(ChatFormatting.GRAY));
            case OFFLINE -> player.sendSystemMessage(Component.translatable("message.mc3ver.friend_offline", link.ownerName()).withStyle(ChatFormatting.RED));
            case REVOKED -> player.sendSystemMessage(Component.translatable("message.mc3ver.friendship_stone_revoked", link.ownerName()).withStyle(ChatFormatting.RED));
            case SUCCESS -> teleportToFriend(player, friend, stack);
        }
        return stack;
    }

    private static void teleportToFriend(ServerPlayer player, ServerPlayer friend, ItemStack stack) {
        ServerLevel friendLevel = friend.level();
        BlockPos friendPos = friend.blockPosition();
        int[] spot = FriendStoneLogic.findArrivalSpot(friendPos.getX(), friendPos.getY(), friendPos.getZ(),
            new FriendStoneLogic.SpotView() {
                @Override
                public boolean isFree(int x, int y, int z) {
                    return isPassable(friendLevel, new BlockPos(x, y, z)) && isPassable(friendLevel, new BlockPos(x, y + 1, z));
                }

                @Override
                public boolean isFloor(int x, int y, int z) {
                    BlockPos p = new BlockPos(x, y, z);
                    return friendLevel.getBlockState(p).isFaceSturdy(friendLevel, p, Direction.UP);
                }
            });

        ChannelTeleport.teleport(player, friendLevel, spot[0] + 0.5, spot[1], spot[2] + 0.5);
        player.sendSystemMessage(Component.translatable("message.mc3ver.friend_teleported", friend.getPlainTextName()).withStyle(ChatFormatting.AQUA));
        friend.sendSystemMessage(Component.translatable("message.mc3ver.friend_arriving", player.getPlainTextName()).withStyle(ChatFormatting.LIGHT_PURPLE));
        friendLevel.playSound(null, friend.getX(), friend.getY(), friend.getZ(),
            SoundEvents.AMETHYST_BLOCK_RESONATE, SoundSource.PLAYERS, 1.0f, 1.0f);
        player.getCooldowns().addCooldown(stack, FriendStoneLogic.COOLDOWN_TICKS);
    }

    private static boolean isPassable(ServerLevel level, BlockPos pos) {
        BlockState state = level.getBlockState(pos);
        return state.getCollisionShape(level, pos).isEmpty() && state.getFluidState().isEmpty();
    }

    // Zeigt in der Aktionsleiste, wo der Freund ist, solange man den Stein in der Hand hält
    @Override
    public void inventoryTick(ItemStack stack, ServerLevel level, Entity entity, EquipmentSlot slot) {
        if (slot != EquipmentSlot.MAINHAND && slot != EquipmentSlot.OFFHAND) {
            return;
        }
        FriendStoneLogic.FriendLink link = stack.get(ModFriendStone.FRIEND_LINK);
        if (link == null || !(entity instanceof ServerPlayer player) || player.isUsingItem()
                || link.owner().equals(player.getUUID()) || !FriendStoneLogic.shouldUpdateDistance(level.getGameTime())) {
            return;
        }
        ServerPlayer friend = level.getServer().getPlayerList().getPlayer(link.owner());
        if (friend == null) {
            player.sendOverlayMessage(Component.translatable("message.mc3ver.friend_offline", link.ownerName()).withStyle(ChatFormatting.GRAY));
            return;
        }
        String here = Dimensions.id(player.level());
        String there = Dimensions.id(friend.level());
        Component text = FriendStoneLogic.bearing(player.getX(), player.getZ(), here, friend.getX(), friend.getZ(), there)
            .map(b -> Component.translatable("message.mc3ver.friend_bearing", friend.getPlainTextName(), b.distance(),
                Component.translatable(b.direction().translationKey())))
            .orElseGet(() -> Component.translatable("message.mc3ver.friend_other_dimension", friend.getPlainTextName(),
                Dimensions.name(there)));
        player.sendOverlayMessage(text.copy().withStyle(ChatFormatting.LIGHT_PURPLE));
    }

    @Override
    public Component getName(ItemStack stack) {
        FriendStoneLogic.FriendLink link = stack.get(ModFriendStone.FRIEND_LINK);
        return link != null
            ? Component.translatable("item.mc3ver.friendship_stone.bound", link.ownerName())
            : super.getName(stack);
    }

    @Override
    public void appendHoverText(ItemStack stack, TooltipContext context, TooltipDisplay display, Consumer<Component> tooltipAdder, TooltipFlag flag) {
        FriendStoneLogic.FriendLink link = stack.get(ModFriendStone.FRIEND_LINK);
        if (link == null) {
            tooltipAdder.accept(Component.translatable("tooltip.mc3ver.friendship_stone.unbound").withStyle(ChatFormatting.GRAY));
        } else {
            tooltipAdder.accept(Component.translatable("tooltip.mc3ver.friendship_stone.leads_to", link.ownerName()).withStyle(ChatFormatting.LIGHT_PURPLE));
            tooltipAdder.accept(Component.translatable("tooltip.mc3ver.friendship_stone.bound").withStyle(ChatFormatting.GRAY));
        }
    }
}
