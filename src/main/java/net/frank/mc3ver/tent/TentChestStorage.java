package net.frank.mc3ver.tent;

import java.util.List;

import net.minecraft.core.NonNullList;
import net.minecraft.core.component.DataComponents;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.component.ItemContainerContents;

public final class TentChestStorage {

    public static final int SIZE = 27;

    private TentChestStorage() {
    }

    public static void store(ItemStack carrier, List<ItemStack> items) {
        boolean allEmpty = true;
        for (ItemStack stack : items) {
            if (!stack.isEmpty()) {
                allEmpty = false;
                break;
            }
        }
        if (allEmpty) {
            carrier.remove(DataComponents.CONTAINER);
        } else {
            carrier.set(DataComponents.CONTAINER, ItemContainerContents.fromItems(items));
        }
    }

    public static NonNullList<ItemStack> restore(ItemStack carrier) {
        NonNullList<ItemStack> list = NonNullList.withSize(SIZE, ItemStack.EMPTY);
        carrier.getOrDefault(DataComponents.CONTAINER, ItemContainerContents.EMPTY).copyInto(list);
        return list;
    }
}
