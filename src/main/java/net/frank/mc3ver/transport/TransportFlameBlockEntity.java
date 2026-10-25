package net.frank.mc3ver.transport;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;
import org.jetbrains.annotations.Nullable;

// Merkt sich Farbe und ID der Flamme, damit verlorene Karten nachgeholt werden können
public class TransportFlameBlockEntity extends BlockEntity {

    @Nullable
    private TransportFlameLogic.FlameTarget target;

    public TransportFlameBlockEntity(BlockPos pos, BlockState state) {
        super(ModBlocks.TRANSPORT_FLAME_BLOCK_ENTITY, pos, state);
    }

    @Nullable
    public TransportFlameLogic.FlameTarget getTarget() {
        return target;
    }

    public void setTarget(TransportFlameLogic.FlameTarget target) {
        this.target = target;
        setChanged();
    }

    @Override
    protected void loadAdditional(ValueInput input) {
        super.loadAdditional(input);
        target = input.read("flame_target", ModDataComponents.FLAME_TARGET_CODEC).orElse(null);
    }

    @Override
    protected void saveAdditional(ValueOutput output) {
        super.saveAdditional(output);
        output.storeNullable("flame_target", ModDataComponents.FLAME_TARGET_CODEC, target);
    }
}
