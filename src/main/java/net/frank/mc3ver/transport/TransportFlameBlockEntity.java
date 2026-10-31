package net.frank.mc3ver.transport;

import com.mojang.serialization.Codec;
import java.util.UUID;
import net.minecraft.core.BlockPos;
import net.minecraft.core.UUIDUtil;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;
import org.jetbrains.annotations.Nullable;

// Merkt sich Farbe, ID und Namen der Flamme, damit verlorene Karten nachgeholt werden können
public class TransportFlameBlockEntity extends BlockEntity {

    @Nullable
    private TransportFlameLogic.FlameTarget target;

    @Nullable
    private String name;

    @Nullable
    private UUID placer;

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

    @Nullable
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
        setChanged();
    }

    @Nullable
    public UUID getPlacer() {
        return placer;
    }

    public void setPlacer(UUID placer) {
        this.placer = placer;
        setChanged();
    }

    @Override
    protected void loadAdditional(ValueInput input) {
        super.loadAdditional(input);
        target =
                input.read("flame_target", ModDataComponents.FLAME_TARGET_CODEC).orElse(null);
        name = input.read("name", Codec.STRING).orElse(null);
        placer = input.read("placer", UUIDUtil.CODEC).orElse(null);
    }

    @Override
    protected void saveAdditional(ValueOutput output) {
        super.saveAdditional(output);
        output.storeNullable("flame_target", ModDataComponents.FLAME_TARGET_CODEC, target);
        output.storeNullable("name", Codec.STRING, name);
        output.storeNullable("placer", UUIDUtil.CODEC, placer);
    }
}
