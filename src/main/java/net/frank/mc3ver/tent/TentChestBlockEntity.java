package net.frank.mc3ver.tent;

import net.minecraft.core.BlockPos;
import net.minecraft.core.NonNullList;
import net.minecraft.network.chat.Component;
import net.minecraft.world.ContainerHelper;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.ChestMenu;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.entity.RandomizableContainerBlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;

public class TentChestBlockEntity extends RandomizableContainerBlockEntity {

    private NonNullList<ItemStack> items = NonNullList.withSize(TentChestStorage.SIZE, ItemStack.EMPTY);

    public TentChestBlockEntity(BlockPos pos, BlockState state) {
        super(ModTentBlocks.TENT_CHEST_BLOCK_ENTITY, pos, state);
    }

    @Override
    public int getContainerSize() {
        return TentChestStorage.SIZE;
    }

    @Override
    protected NonNullList<ItemStack> getItems() {
        return this.items;
    }

    @Override
    protected void setItems(NonNullList<ItemStack> items) {
        this.items = items;
    }

    @Override
    protected Component getDefaultName() {
        return Component.translatable("block.mc3ver.tent_chest");
    }

    @Override
    protected AbstractContainerMenu createMenu(int containerId, Inventory inventory) {
        return ChestMenu.threeRows(containerId, inventory, this);
    }

    @Override
    protected void saveAdditional(ValueOutput output) {
        super.saveAdditional(output);
        if (!this.trySaveLootTable(output)) {
            ContainerHelper.saveAllItems(output, this.items);
        }
    }

    @Override
    protected void loadAdditional(ValueInput input) {
        super.loadAdditional(input);
        this.items = NonNullList.withSize(this.getContainerSize(), ItemStack.EMPTY);
        if (!this.tryLoadLootTable(input)) {
            ContainerHelper.loadAllItems(input, this.items);
        }
    }

    // Kopie des Inhalts liefern und die Kiste leeren (beim Abbau des Zelts)
    public NonNullList<ItemStack> takeAllItems() {
        NonNullList<ItemStack> copy = NonNullList.withSize(TentChestStorage.SIZE, ItemStack.EMPTY);
        for (int i = 0; i < this.items.size(); i++) {
            copy.set(i, this.items.get(i));
            this.items.set(i, ItemStack.EMPTY);
        }
        this.setChanged();
        return copy;
    }

    public void putAllItems(NonNullList<ItemStack> newItems) {
        for (int i = 0; i < this.items.size(); i++) {
            this.items.set(i, i < newItems.size() ? newItems.get(i) : ItemStack.EMPTY);
        }
        this.setChanged();
    }
}
