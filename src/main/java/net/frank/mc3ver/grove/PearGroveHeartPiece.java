package net.frank.mc3ver.grove;

import java.util.EnumSet;
import java.util.Map;
import java.util.Set;
import net.frank.mc3ver.grove.OldPearTreeLogic.Layout;
import net.frank.mc3ver.grove.OldPearTreeLogic.Part;
import net.frank.mc3ver.grove.OldPearTreeLogic.Pos;
import net.frank.mc3ver.grove.OldPearTreeLogic.Role;
import net.frank.mc3ver.tree.PearTreeBlocks;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.StructureManager;
import net.minecraft.world.level.WorldGenLevel;
import net.minecraft.world.level.block.BeehiveBlock;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.ChestBlock;
import net.minecraft.world.level.block.LeavesBlock;
import net.minecraft.world.level.block.RotatedPillarBlock;
import net.minecraft.world.level.block.entity.BeehiveBlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTypes;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.chunk.ChunkGenerator;
import net.minecraft.world.level.levelgen.Heightmap;
import net.minecraft.world.level.levelgen.structure.BoundingBox;
import net.minecraft.world.level.levelgen.structure.StructurePiece;
import net.minecraft.world.level.levelgen.structure.pieces.StructurePieceSerializationContext;
import net.minecraft.world.level.material.Fluids;

/**
 * Setzt den Bauplan aus {@link OldPearTreeLogic} in die Welt, Chunk für Chunk.
 * Baum, See und Quelle stehen auf der Höhe des Stammfußes (Wasser liegt eben); Wurzeln, Truhe, Moos und
 * Trittsteine folgen der Oberfläche ihrer Spalte.
 */
public class PearGroveHeartPiece extends StructurePiece {

    // Diese Rollen liegen auf Stammfuß-Höhe, alle anderen auf der Oberfläche ihrer Spalte
    private static final Set<Role> AT_TRUNK_LEVEL = EnumSet.of(
            Role.TRUNK,
            Role.BRANCH,
            Role.LEAVES,
            Role.BEEHIVE,
            Role.WATER,
            Role.CLAY,
            Role.GRAVEL,
            Role.LILY_PAD,
            Role.SPRING_ROCK,
            Role.SPRING_WATER);
    private static final Set<Role> LAKE = EnumSet.of(Role.WATER, Role.CLAY, Role.GRAVEL, Role.LILY_PAD);
    // Über dem See wird bis zu dieser Höhe freigeräumt, falls Gelände hineinragt
    private static final int LAKE_CLEARANCE = 4;
    private static final int MAX_TRUNK_FOOTING = 5;

    private final BlockPos origin;
    private final long layoutSeed;

    public PearGroveHeartPiece(BlockPos origin, long layoutSeed) {
        super(ModGrove.HEART_PIECE, 0, boundingBox(origin, OldPearTreeLogic.layout(layoutSeed)));
        this.origin = origin;
        this.layoutSeed = layoutSeed;
    }

    public PearGroveHeartPiece(CompoundTag tag) {
        super(ModGrove.HEART_PIECE, tag);
        this.origin = new BlockPos(tag.getIntOr("OX", 0), tag.getIntOr("OY", 0), tag.getIntOr("OZ", 0));
        this.layoutSeed = tag.getLongOr("LayoutSeed", 0L);
    }

    @Override
    protected void addAdditionalSaveData(StructurePieceSerializationContext context, CompoundTag tag) {
        tag.putInt("OX", origin.getX());
        tag.putInt("OY", origin.getY());
        tag.putInt("OZ", origin.getZ());
        tag.putLong("LayoutSeed", layoutSeed);
    }

    private static BoundingBox boundingBox(BlockPos origin, Layout layout) {
        int minX = 0;
        int maxX = 0;
        int minZ = 0;
        int maxZ = 0;
        for (Pos pos : layout.parts().keySet()) {
            minX = Math.min(minX, pos.x());
            maxX = Math.max(maxX, pos.x());
            minZ = Math.min(minZ, pos.z());
            maxZ = Math.max(maxZ, pos.z());
        }
        // Spalten-Oberfläche kann vom Stammfuß abweichen: großzügig nach unten und oben
        return new BoundingBox(
                origin.getX() + minX,
                origin.getY() - 16,
                origin.getZ() + minZ,
                origin.getX() + maxX,
                origin.getY() + layout.trunkHeight() + 8,
                origin.getZ() + maxZ);
    }

    @Override
    public void postProcess(
            WorldGenLevel level,
            StructureManager structureManager,
            ChunkGenerator generator,
            RandomSource random,
            BoundingBox chunkBox,
            ChunkPos chunkPos,
            BlockPos pivot) {
        Layout layout = OldPearTreeLogic.layout(layoutSeed);
        clearAboveLake(level, chunkBox, layout);
        for (Map.Entry<Pos, Part> entry : layout.parts().entrySet()) {
            Pos pos = entry.getKey();
            Part part = entry.getValue();
            // Erst die Spalte prüfen: die Höhenabfrage darf nur Chunks lesen, die gerade erzeugt werden
            if (!inColumns(chunkBox, origin.getX() + pos.x(), origin.getZ() + pos.z())) {
                continue;
            }
            BlockPos world = worldPos(level, pos, part.role());
            if (chunkBox.isInside(world)) {
                place(level, chunkBox, random, world, part);
            }
        }
        supportTrunk(level, chunkBox);
    }

    private static boolean inColumns(BoundingBox box, int x, int z) {
        return x >= box.minX() && x <= box.maxX() && z >= box.minZ() && z <= box.maxZ();
    }

    private BlockPos worldPos(WorldGenLevel level, Pos pos, Role role) {
        int x = origin.getX() + pos.x();
        int z = origin.getZ() + pos.z();
        if (AT_TRUNK_LEVEL.contains(role)) {
            return new BlockPos(x, origin.getY() + pos.y(), z);
        }
        return new BlockPos(x, level.getHeight(Heightmap.Types.WORLD_SURFACE_WG, x, z) + pos.y(), z);
    }

    // Ragt Gelände in den See, wird darüber Luft; liegt das Gelände tiefer, bekommt der See Erde als Sockel
    private void clearAboveLake(WorldGenLevel level, BoundingBox chunkBox, Layout layout) {
        for (Map.Entry<Pos, Part> entry : layout.parts().entrySet()) {
            Pos pos = entry.getKey();
            if (pos.y() != -1 || !LAKE.contains(entry.getValue().role())) {
                continue;
            }
            int x = origin.getX() + pos.x();
            int z = origin.getZ() + pos.z();
            for (int y = origin.getY(); y < origin.getY() + LAKE_CLEARANCE; y++) {
                BlockPos clear = new BlockPos(x, y, z);
                if (chunkBox.isInside(clear)) {
                    level.setBlock(clear, Blocks.AIR.defaultBlockState(), 2);
                }
            }
            for (int y = origin.getY() - 4; y > origin.getY() - 8; y--) {
                BlockPos below = new BlockPos(x, y, z);
                if (chunkBox.isInside(below) && level.getBlockState(below).canBeReplaced()) {
                    level.setBlock(below, Blocks.DIRT.defaultBlockState(), 2);
                }
            }
        }
    }

    // Steht der Stamm am Hang, wird er nach unten bis zum Boden verlängert
    private void supportTrunk(WorldGenLevel level, BoundingBox chunkBox) {
        BlockState log = PearTreeBlocks.PEAR_LOG.defaultBlockState();
        for (int dx = 0; dx <= 1; dx++) {
            for (int dz = 0; dz <= 1; dz++) {
                for (int dy = 1; dy <= MAX_TRUNK_FOOTING; dy++) {
                    BlockPos below = origin.offset(dx, -dy, dz);
                    if (!chunkBox.isInside(below) || !level.getBlockState(below).canBeReplaced()) {
                        break;
                    }
                    level.setBlock(below, log, 2);
                }
            }
        }
    }

    private void place(WorldGenLevel level, BoundingBox chunkBox, RandomSource random, BlockPos pos, Part part) {
        switch (part.role()) {
            case TRUNK -> set(level, pos, PearTreeBlocks.PEAR_LOG.defaultBlockState());
            case BRANCH, ROOT ->
                set(
                        level,
                        pos,
                        PearTreeBlocks.PEAR_LOG
                                .defaultBlockState()
                                .setValue(RotatedPillarBlock.AXIS, part.facing().getAxis()));
            // Dauerhaft: der Kronenrand ist weiter als 6 Blöcke vom Holz entfernt und würde sonst verrotten
            case LEAVES -> {
                if (level.getBlockState(pos).canBeReplaced()) {
                    set(
                            level,
                            pos,
                            PearTreeBlocks.PEAR_LEAVES.defaultBlockState().setValue(LeavesBlock.PERSISTENT, true));
                }
            }
            case BEEHIVE -> {
                set(level, pos, Blocks.BEE_NEST.defaultBlockState().setValue(BeehiveBlock.FACING, part.facing()));
                level.getBlockEntity(pos, BlockEntityTypes.BEEHIVE).ifPresent(hive -> {
                    int bees = 2 + random.nextInt(2);
                    for (int i = 0; i < bees; i++) {
                        hive.storeBee(BeehiveBlockEntity.Occupant.create(random.nextInt(599)));
                    }
                });
            }
            case CHEST ->
                createChest(
                        level,
                        chunkBox,
                        random,
                        pos,
                        ModGrove.HEART_LOOT,
                        Blocks.CHEST.defaultBlockState().setValue(ChestBlock.FACING, part.facing()));
            case MOSS_BLOCK -> set(level, pos, Blocks.MOSS_BLOCK.defaultBlockState());
            case MOSS_CARPET -> set(level, pos, Blocks.MOSS_CARPET.defaultBlockState());
            case MUSHROOM -> set(level, pos, Blocks.BROWN_MUSHROOM.defaultBlockState());
            case WATER -> set(level, pos, Blocks.WATER.defaultBlockState());
            case CLAY -> set(level, pos, Blocks.CLAY.defaultBlockState());
            case GRAVEL -> set(level, pos, Blocks.GRAVEL.defaultBlockState());
            case LILY_PAD -> set(level, pos, Blocks.LILY_PAD.defaultBlockState());
            case SPRING_ROCK -> set(level, pos, rock(pos));
            case SPRING_WATER -> {
                set(level, pos, Blocks.WATER.defaultBlockState());
                level.scheduleTick(pos, Fluids.WATER, 0);
            }
            case PATH ->
                set(
                        level,
                        pos,
                        variant(pos)
                                ? Blocks.MOSSY_STONE_BRICKS.defaultBlockState()
                                : Blocks.STONE_BRICKS.defaultBlockState());
        }
    }

    private static void set(WorldGenLevel level, BlockPos pos, BlockState state) {
        level.setBlock(pos, state, 2);
    }

    // Fels aus bemoostem Bruchstein, Bruchstein und Stein, fest je Position
    private static BlockState rock(BlockPos pos) {
        return switch (Math.floorMod(pos.getX() * 31 + pos.getY() * 17 + pos.getZ() * 7, 3)) {
            case 0 -> Blocks.MOSSY_COBBLESTONE.defaultBlockState();
            case 1 -> Blocks.COBBLESTONE.defaultBlockState();
            default -> Blocks.STONE.defaultBlockState();
        };
    }

    private static boolean variant(BlockPos pos) {
        return Math.floorMod(pos.getX() * 13 + pos.getZ() * 29, 2) == 0;
    }
}
