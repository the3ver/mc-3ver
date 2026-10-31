package net.frank.mc3ver.grove;

import com.mojang.serialization.MapCodec;
import java.util.Optional;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.levelgen.Heightmap;
import net.minecraft.world.level.levelgen.structure.Structure;
import net.minecraft.world.level.levelgen.structure.StructureType;

/**
 * Herzstück des Birnenhains: alter Birnbaum mit See und Quelle. Jeder Chunk ist Kandidat (wie beim
 * vergrabenen Schatz); entstehen darf es nur am Herz aus {@link GroveShapeLogic}, also genau dort, wo das
 * Vegetations-Feature die Lichtung frei lässt.
 */
public class PearGroveHeartStructure extends Structure {

    public static final MapCodec<PearGroveHeartStructure> CODEC = simpleCodec(PearGroveHeartStructure::new);

    public PearGroveHeartStructure(StructureSettings settings) {
        super(settings);
    }

    @Override
    protected Optional<GenerationStub> findGenerationPoint(GenerationContext context) {
        GroveShapeLogic.DepthGrid grid = GroveDepthCache.grid(
                context.seed(),
                context.chunkGenerator(),
                context.heightAccessor(),
                context.randomState(),
                context.biomeResolver());
        int gx = GroveShapeLogic.gridOf(context.chunkPos().getMiddleBlockX());
        int gz = GroveShapeLogic.gridOf(context.chunkPos().getMiddleBlockZ());
        if (!GroveShapeLogic.isHeart(grid, context.seed(), gx, gz)) {
            return Optional.empty();
        }
        GroveShapeLogic.Heart heart = GroveShapeLogic.heart(context.seed(), gx, gz);
        int y = context.chunkGenerator()
                .getBaseHeight(
                        heart.treeX(),
                        heart.treeZ(),
                        Heightmap.Types.WORLD_SURFACE_WG,
                        context.heightAccessor(),
                        context.randomState());
        BlockPos origin = new BlockPos(heart.treeX(), y, heart.treeZ());
        long layoutSeed = context.seed() ^ (gx * 341_873_128_712L) ^ (gz * 132_897_987_541L);
        return Optional.of(
                new GenerationStub(origin, builder -> builder.addPiece(new PearGroveHeartPiece(origin, layoutSeed))));
    }

    @Override
    public StructureType<?> type() {
        return ModGrove.HEART_STRUCTURE;
    }
}
