package net.frank.mc3ver.gametest;

import com.mojang.datafixers.util.Pair;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import net.fabricmc.fabric.api.client.gametest.v1.FabricClientGameTest;
import net.fabricmc.fabric.api.client.gametest.v1.context.ClientGameTestContext;
import net.fabricmc.fabric.api.client.gametest.v1.context.TestSingleplayerContext;
import net.fabricmc.fabric.api.client.gametest.v1.screenshot.TestScreenshotOptions;
import net.frank.mc3ver.Mc3verMod;
import net.frank.mc3ver.bonfire.ModBonfire;
import net.frank.mc3ver.grove.GroveShapeLogic;
import net.frank.mc3ver.tent.ExplorerTentLogic;
import net.frank.mc3ver.tent.ModTentItems;
import net.frank.mc3ver.transport.ModBlocks;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.Holder;
import net.minecraft.core.HolderSet;
import net.minecraft.core.component.DataComponents;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.tags.BlockTags;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.component.DyedItemColor;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.levelgen.Heightmap;
import net.minecraft.world.level.levelgen.structure.Structure;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.Vec3;

// Galerie-Szene: alter Birnbaum auf seiner Lichtung, davor Zelt, Signalfeuer und Transportflamme im Abendlicht.
// Aufruf: GALLERY_SCREENSHOT_DIR=<ordner> ./gradlew runClientGameTest
public class GalleryScreenshotClientGameTest implements FabricClientGameTest {

    private static final String SEED = "mc3ver";
    private static final int SEARCH_SPAN = 2000;
    // Die Lichtung um den Baum hat einen Radius von 20 Blöcken; die Kamera steht knapp innerhalb
    private static final int CAMERA_DISTANCE = 19;

    private record Scene(BlockPos tree, double dirX, double dirZ, int score) {}

    private static TestScreenshotOptions shot(String name) {
        TestScreenshotOptions options = TestScreenshotOptions.of(name).withSize(1920, 1080);
        String dir = System.getenv("GALLERY_SCREENSHOT_DIR");
        return dir != null ? options.withDestinationDir(Path.of(dir)) : options;
    }

    @Override
    public void runTest(ClientGameTestContext context) {
        try (TestSingleplayerContext game = context.worldBuilder()
                .setUseConsistentSettings(false)
                .adjustSettings(creator -> creator.setSeed(SEED))
                .create()) {
            game.getServer().runCommand("gamemode spectator @p");
            game.getServer().runCommand("gamerule doDaylightCycle false");
            game.getServer().runCommand("gamerule doWeatherCycle false");
            game.getServer().runCommand("gamerule doMobSpawning false");
            game.getServer().runCommand("time set 12000");
            game.getServer().runCommand("weather clear");

            Scene scene = game.getServer().computeOnServer(GalleryScreenshotClientGameTest::chooseScene);
            System.out.println("[gallery] scene " + scene);
            BlockPos tree = scene.tree();

            // Erst hinfliegen, damit die Chunks erzeugt sind, bevor dort etwas aufgestellt wird
            game.getServer().runCommand("tp @p " + tree.getX() + " " + (tree.getY() + 30) + " " + tree.getZ());
            game.getConnection().waitForChunksRender();
            context.waitTicks(40);

            game.getServer().runOnServer(server -> buildScene(server, scene));

            double camX = tree.getX() + 0.5 + scene.dirX() * CAMERA_DISTANCE;
            double camZ = tree.getZ() + 0.5 + scene.dirZ() * CAMERA_DISTANCE;
            int cameraGround = game.getServer()
                    .computeOnServer(
                            server -> groundAt(server.overworld(), (int) Math.floor(camX), (int) Math.floor(camZ)));
            double eyeY = cameraGround + 0.6;
            double dx = tree.getX() + 0.5 - camX;
            double dy = tree.getY() + 9 - (eyeY + 1.62);
            double dz = tree.getZ() + 0.5 - camZ;
            float yaw = (float) Math.toDegrees(Math.atan2(-dx, dz));
            float pitch = (float) -Math.toDegrees(Math.atan2(dy, Math.sqrt(dx * dx + dz * dz)));
            game.getServer().runCommand("tp @p " + camX + " " + eyeY + " " + camZ + " " + yaw + " " + pitch);

            context.runOnClient(client -> {
                if (!client.gui.hud.isHidden()) client.gui.hud.toggle();
                client.options.fov().set(85);
            });
            game.getConnection().waitForChunksRender();
            // Partikel von Flamme und Feuer brauchen ein paar Sekunden
            context.waitTicks(100);
            context.takeScreenshot(shot("gallery"));
        }
    }

    // Sucht mehrere Herzen und nimmt das mit dem flachsten freien Blick auf den Baum
    private static Scene chooseScene(MinecraftServer server) {
        ServerLevel level = server.overworld();
        long seed = level.getSeed();
        Holder<Structure> heart = server.registryAccess()
                .lookupOrThrow(Registries.STRUCTURE)
                .getOrThrow(ResourceKey.create(Registries.STRUCTURE, Mc3verMod.id("pear_grove_heart")));
        List<BlockPos> trees = new ArrayList<>();
        for (int sx = -SEARCH_SPAN; sx <= SEARCH_SPAN; sx += SEARCH_SPAN) {
            for (int sz = -SEARCH_SPAN; sz <= SEARCH_SPAN; sz += SEARCH_SPAN) {
                Pair<BlockPos, Holder<Structure>> found = level.getChunkSource()
                        .getGenerator()
                        .findNearestMapStructure(level, HolderSet.direct(heart), new BlockPos(sx, 0, sz), 64, false);
                if (found == null) continue;
                BlockPos chunk = found.getFirst();
                GroveShapeLogic.Heart h = GroveShapeLogic.heart(
                        seed, GroveShapeLogic.gridOf(chunk.getX() + 8), GroveShapeLogic.gridOf(chunk.getZ() + 8));
                BlockPos tree = new BlockPos(h.treeX(), generatorGround(level, h.treeX(), h.treeZ()), h.treeZ());
                if (!trees.contains(tree)) trees.add(tree);
            }
        }
        if (trees.isEmpty()) throw new AssertionError("Kein alter Birnbaum gefunden");

        Scene best = null;
        for (BlockPos tree : trees) {
            for (int k = 0; k < 16; k++) {
                double angle = Math.PI * 2 * k / 16;
                double dirX = Math.sin(angle);
                double dirZ = Math.cos(angle);
                int worst = 0;
                for (int r = 4; r <= CAMERA_DISTANCE + 2; r += 2) {
                    for (int side = -6; side <= 6; side += 3) {
                        int x = (int) Math.floor(tree.getX() + dirX * r - dirZ * side);
                        int z = (int) Math.floor(tree.getZ() + dirZ * r + dirX * side);
                        worst = Math.max(worst, Math.abs(generatorGround(level, x, z) - tree.getY()));
                    }
                }
                if (best == null || worst < best.score()) best = new Scene(tree, dirX, dirZ, worst);
            }
            System.out.println("[gallery] candidate " + tree);
        }
        return best;
    }

    private static int generatorGround(ServerLevel level, int x, int z) {
        return level.getChunkSource()
                .getGenerator()
                .getBaseHeight(
                        x,
                        z,
                        Heightmap.Types.WORLD_SURFACE_WG,
                        level,
                        level.getChunkSource().randomState());
    }

    private static int groundAt(ServerLevel level, int x, int z) {
        return level.getHeight(Heightmap.Types.MOTION_BLOCKING_NO_LEAVES, x, z);
    }

    private static void buildScene(MinecraftServer server, Scene scene) {
        ServerLevel level = server.overworld();
        ServerPlayer player = server.getPlayerList().getPlayers().getFirst();
        Direction towardTree = Direction.getApproximateNearest(-scene.dirX(), 0, -scene.dirZ());

        ItemStack tent = new ItemStack(ModTentItems.EXPLORER_TENT);
        tent.set(DataComponents.DYED_COLOR, new DyedItemColor(ExplorerTentLogic.getColorRgb("orange")));
        place(level, player, tent, scene, 9, -5, towardTree, "Zelt");
        place(level, player, new ItemStack(ModBonfire.BONFIRE_ITEM), scene, 9, 4, towardTree, "Feuer");

        BlockPos flame = spot(level, scene, 13.5, -2.5).above();
        level.setBlockAndUpdate(flame, ModBlocks.TRANSPORT_FLAME.defaultBlockState());
        // Blumen um die kleine Flamme und auf der Sichtlinie zur Kamera verdecken sie sonst
        double camX = scene.tree().getX() + 0.5 + scene.dirX() * CAMERA_DISTANCE;
        double camZ = scene.tree().getZ() + 0.5 + scene.dirZ() * CAMERA_DISTANCE;
        for (int step = 0; step <= 8; step++) {
            double t = step / 8.0;
            BlockPos c = BlockPos.containing(
                    flame.getX() + 0.5 + (camX - flame.getX() - 0.5) * t,
                    flame.getY(),
                    flame.getZ() + 0.5 + (camZ - flame.getZ() - 0.5) * t);
            for (BlockPos p : BlockPos.betweenClosed(c.offset(-1, -2, -1), c.offset(1, 1, 1))) {
                if (!p.equals(flame)
                        && (level.getBlockState(p).canBeReplaced()
                                || level.getBlockState(p).is(BlockTags.SMALL_FLOWERS))) {
                    level.setBlockAndUpdate(p, Blocks.AIR.defaultBlockState());
                }
            }
        }
        System.out.println("[gallery] flame " + flame);
    }

    // Bodenblock vor dem Baum, in Blöcken entlang der Blickachse und quer dazu
    private static BlockPos spot(ServerLevel level, Scene scene, double along, double side) {
        int x = (int) Math.floor(scene.tree().getX() + 0.5 + scene.dirX() * along - scene.dirZ() * side);
        int z = (int) Math.floor(scene.tree().getZ() + 0.5 + scene.dirZ() * along + scene.dirX() * side);
        return new BlockPos(x, groundAt(level, x, z) - 1, z);
    }

    // Probiert die Wunschstelle und dann Nachbarstellen, bis das Item Platz findet
    private static void place(
            ServerLevel level,
            ServerPlayer player,
            ItemStack stack,
            Scene scene,
            double along,
            double side,
            Direction facing,
            String what) {
        for (int r = 0; r <= 3; r++) {
            for (int da = -r; da <= r; da++) {
                for (int ds = -r; ds <= r; ds++) {
                    if (Math.max(Math.abs(da), Math.abs(ds)) != r) continue;
                    if (use(player, stack.copy(), spot(level, scene, along + da, side + ds), facing, what)) return;
                }
            }
        }
        throw new AssertionError(what + " fand keinen Platz");
    }

    // Stellt ein Item so auf, wie es ein Spieler mit Rechtsklick auf den Boden täte
    private static boolean use(ServerPlayer player, ItemStack stack, BlockPos ground, Direction facing, String what) {
        player.setYRot(facing.toYRot());
        player.setItemInHand(InteractionHand.MAIN_HAND, stack);
        BlockHitResult hit = new BlockHitResult(Vec3.atCenterOf(ground).add(0, 0.5, 0), Direction.UP, ground, false);
        InteractionResult result = stack.getItem().useOn(new UseOnContext(player, InteractionHand.MAIN_HAND, hit));
        System.out.println("[gallery] " + what + " at " + ground + ": " + result);
        player.setItemInHand(InteractionHand.MAIN_HAND, ItemStack.EMPTY);
        return result.consumesAction();
    }
}
