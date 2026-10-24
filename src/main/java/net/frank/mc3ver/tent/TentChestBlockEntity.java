package net.frank.mc3ver.tent;

import net.minecraft.core.BlockPos;
import net.minecraft.core.NonNullList;
import net.minecraft.network.chat.Component;
import net.minecraft.world.ContainerHelper;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.ContainerUser;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.ContainerOpenersCounter;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.ChestMenu;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.entity.RandomizableContainerBlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;

public class TentChestBlockEntity extends RandomizableContainerBlockEntity {

    private NonNullList<ItemStack> items = NonNullList.withSize(TentChestStorage.SIZE, ItemStack.EMPTY);

    private final ContainerOpenersCounter openersCounter = new ContainerOpenersCounter() {
        @Override
        protected void onOpen(Level level, BlockPos pos, BlockState state) {
            TentChestBlockEntity.this.playSound(SoundEvents.CHEST_OPEN);
            TentChestBlockEntity.this.updateOpen(state, isLidOpen(1));
        }

        @Override
        protected void onClose(Level level, BlockPos pos, BlockState state) {
            TentChestBlockEntity.this.playSound(SoundEvents.CHEST_CLOSE);
            TentChestBlockEntity.this.updateOpen(state, isLidOpen(0));
        }

        @Override
        protected void openerCountChanged(Level level, BlockPos pos, BlockState state, int oldCount, int newCount) {
        }

        @Override
        public boolean isOwnContainer(Player player) {
            return player.containerMenu instanceof ChestMenu menu && menu.getContainer() == TentChestBlockEntity.this;
        }
    };

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

    // Deckel offen, solange mindestens ein Spieler die Kiste geöffnet hat
    public static boolean isLidOpen(int openerCount) {
        return openerCount > 0;
    }

    @Override
    public void startOpen(ContainerUser user) {
        if (!this.remove && !user.getLivingEntity().isSpectator()) {
            this.openersCounter.incrementOpeners(user.getLivingEntity(), this.getLevel(), this.getBlockPos(), this.getBlockState(), user.getContainerInteractionRange());
        }
    }

    @Override
    public void stopOpen(ContainerUser user) {
        if (!this.remove && !user.getLivingEntity().isSpectator()) {
            this.openersCounter.decrementOpeners(user.getLivingEntity(), this.getLevel(), this.getBlockPos(), this.getBlockState());
        }
    }

    @Override
    public java.util.List<ContainerUser> getEntitiesWithContainerOpen() {
        return this.openersCounter.getEntitiesWithContainerOpen(this.getLevel(), this.getBlockPos());
    }

    public void recheckOpen() {
        if (!this.remove) {
            this.openersCounter.recheckOpeners(this.getLevel(), this.getBlockPos(), this.getBlockState());
        }
    }

    private void updateOpen(BlockState state, boolean open) {
        if (this.level != null) {
            this.level.setBlock(this.getBlockPos(), state.setValue(TentChestBlock.OPEN, open), 3);
        }
    }

    private void playSound(SoundEvent sound) {
        if (this.level == null) {
            return;
        }
        BlockPos pos = this.worldPosition;
        this.level.playSound(null, pos.getX() + 0.5, pos.getY() + 0.5, pos.getZ() + 0.5, sound, SoundSource.BLOCKS,
            0.5f, this.level.getRandom().nextFloat() * 0.1f + 0.9f);
    }
}
