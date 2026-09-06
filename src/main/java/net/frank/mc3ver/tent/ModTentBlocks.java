package net.frank.mc3ver.tent;

import net.frank.mc3ver.Mc3verMod;
import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.Registry;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.HorizontalDirectionalBlock;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BedPart;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class ModTentBlocks {

    public static final ResourceKey<Block> TENT_CANVAS_KEY = ResourceKey.create(
        Registries.BLOCK,
        Mc3verMod.id("tent_canvas")
    );

    public static final ResourceKey<Block> TENT_POLE_KEY = ResourceKey.create(
        Registries.BLOCK,
        Mc3verMod.id("tent_pole")
    );

    public static final ResourceKey<Block> TENT_SLEEPING_BAG_KEY = ResourceKey.create(
        Registries.BLOCK,
        Mc3verMod.id("tent_sleeping_bag")
    );

    public static final Block TENT_CANVAS = Registry.register(
        BuiltInRegistries.BLOCK,
        TENT_CANVAS_KEY,
        new TentCanvasBlock(
            BlockBehaviour.Properties.of()
                .setId(TENT_CANVAS_KEY)
                .strength(0.6f)
                .sound(SoundType.WOOL)
                .noOcclusion()
        )
    );

    public static final Block TENT_POLE = Registry.register(
        BuiltInRegistries.BLOCK,
        TENT_POLE_KEY,
        new TentPoleBlock(
            BlockBehaviour.Properties.of()
                .setId(TENT_POLE_KEY)
                .strength(0.8f)
                .sound(SoundType.WOOD)
                .noOcclusion()
        )
    );

    public static final Block TENT_SLEEPING_BAG = Registry.register(
        BuiltInRegistries.BLOCK,
        TENT_SLEEPING_BAG_KEY,
        new TentSleepingBagBlock(
            BlockBehaviour.Properties.of()
                .setId(TENT_SLEEPING_BAG_KEY)
                .strength(0.4f)
                .sound(SoundType.WOOL)
                .noOcclusion()
        )
    );

    private static final Set<BlockPos> DISMANTLING_IN_PROGRESS = new HashSet<>();

    public static boolean isTentBlock(BlockState state) {
        return state.is(TENT_CANVAS) || state.is(TENT_POLE) || state.is(TENT_SLEEPING_BAG);
    }

    public static void dismantleTent(Level level, BlockPos pos, Player player) {
        if (level.isClientSide()) return;
        if (DISMANTLING_IN_PROGRESS.contains(pos)) return;

        ExplorerTentLogic.TentStructureView structureView = new ExplorerTentLogic.TentStructureView() {
            @Override
            public boolean isTentBlock(int x, int y, int z) {
                return ModTentBlocks.isTentBlock(level.getBlockState(new BlockPos(x, y, z)));
            }

            @Override
            public boolean isSleepingBagHead(int x, int y, int z) {
                BlockState s = level.getBlockState(new BlockPos(x, y, z));
                return s.is(TENT_SLEEPING_BAG) && s.getValue(TentSleepingBagBlock.PART) == BedPart.HEAD;
            }

            @Override
            public ExplorerTentLogic.Facing getFacing(int x, int y, int z) {
                BlockState s = level.getBlockState(new BlockPos(x, y, z));
                Direction dir = s.hasProperty(HorizontalDirectionalBlock.FACING)
                    ? s.getValue(HorizontalDirectionalBlock.FACING)
                    : Direction.NORTH;
                return ExplorerTentLogic.Facing.fromDirectionName(dir.name());
            }

            @Override
            public String getColor(int x, int y, int z) {
                BlockState s = level.getBlockState(new BlockPos(x, y, z));
                if (s.is(TENT_CANVAS) && s.hasProperty(TentCanvasBlock.COLOR)) {
                    return s.getValue(TentCanvasBlock.COLOR).getName();
                }
                return null;
            }
        };

        List<ExplorerTentLogic.BlockCoord> blocksToClear = ExplorerTentLogic.collectTentBlocks(
            pos.getX(), pos.getY(), pos.getZ(), structureView
        );

        if (blocksToClear.isEmpty()) {
            return;
        }

        String tentColorName = ExplorerTentLogic.findTentColor(blocksToClear, structureView);
        int tentColorRgb = ExplorerTentLogic.getColorRgb(tentColorName);

        try {
            for (ExplorerTentLogic.BlockCoord c : blocksToClear) {
                DISMANTLING_IN_PROGRESS.add(new BlockPos(c.x(), c.y(), c.z()));
            }

            for (ExplorerTentLogic.BlockCoord c : blocksToClear) {
                BlockPos targetPos = new BlockPos(c.x(), c.y(), c.z());
                if (isTentBlock(level.getBlockState(targetPos))) {
                    level.setBlock(targetPos, Blocks.AIR.defaultBlockState(), Block.UPDATE_ALL);
                }
            }
        } finally {
            for (ExplorerTentLogic.BlockCoord c : blocksToClear) {
                DISMANTLING_IN_PROGRESS.remove(new BlockPos(c.x(), c.y(), c.z()));
            }
        }

        // Effekte & Sound
        level.playSound(null, pos.getX() + 0.5, pos.getY() + 0.5, pos.getZ() + 0.5,
            SoundEvents.WOOL_BREAK, SoundSource.BLOCKS, 1.0f, 1.0f);
        level.playSound(null, pos.getX() + 0.5, pos.getY() + 0.5, pos.getZ() + 0.5,
            SoundEvents.ARMOR_EQUIP_LEATHER, SoundSource.BLOCKS, 0.8f, 1.2f);

        if (level instanceof ServerLevel serverLevel) {
            serverLevel.sendParticles(ParticleTypes.POOF,
                pos.getX() + 0.5, pos.getY() + 0.5, pos.getZ() + 0.5,
                15, 0.5, 0.5, 0.5, 0.05);
        }

        // Zelt als Item zurückgeben oder droppen
        ItemStack tentStack = new ItemStack(ModTentItems.EXPLORER_TENT);
        tentStack.set(net.minecraft.core.component.DataComponents.DYED_COLOR,
            new net.minecraft.world.item.component.DyedItemColor(tentColorRgb));
        if (player != null && !player.getAbilities().instabuild) {
            boolean added = player.getInventory().add(tentStack);
            if (!added) {
                ItemEntity drop = new ItemEntity(level, pos.getX() + 0.5, pos.getY() + 0.5, pos.getZ() + 0.5, tentStack);
                level.addFreshEntity(drop);
            }
        } else if (player == null) {
            ItemEntity drop = new ItemEntity(level, pos.getX() + 0.5, pos.getY() + 0.5, pos.getZ() + 0.5, tentStack);
            level.addFreshEntity(drop);
        }

        if (player != null) {
            player.sendSystemMessage(Component.translatable("message.mc3ver.tent_packed").withStyle(ChatFormatting.YELLOW));
        }
    }

    public static void register() {
        // Triggers static init
    }
}
