package net.frank.mc3ver.teleport;

import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.ItemUseAnimation;
import net.minecraft.world.level.Level;

// Basis der Teleport-Items: Rechtsklick halten kanalisiert mit Portal-Effekten, ein Treffer bricht ab.
// Unterklassen entscheiden in use(), ob kanalisiert wird, und teleportieren in finishUsingItem().
public abstract class ChannelTeleportItem extends Item {

    private final int channelTicks;

    protected ChannelTeleportItem(Item.Properties properties, int channelTicks) {
        super(properties);
        this.channelTicks = channelTicks;
    }

    @Override
    public ItemUseAnimation getUseAnimation(ItemStack stack) {
        return ItemUseAnimation.BOW;
    }

    @Override
    public int getUseDuration(ItemStack stack, LivingEntity entity) {
        return channelTicks;
    }

    @Override
    public void onUseTick(Level level, LivingEntity livingEntity, ItemStack stack, int remainingUseDuration) {
        int ticksUsed = channelTicks - remainingUseDuration;
        if (ChannelTeleport.interruptIfHurt(level, livingEntity, ticksUsed)) {
            return;
        }
        ChannelTeleport.channelEffects(level, livingEntity, ticksUsed, channelTicks);
    }
}
