package net.frank.mc3ver.gametest;

import com.mojang.datafixers.util.Pair;
import java.nio.file.Path;
import java.util.UUID;
import net.fabricmc.fabric.api.client.gametest.v1.FabricClientGameTest;
import net.fabricmc.fabric.api.client.gametest.v1.context.ClientGameTestContext;
import net.fabricmc.fabric.api.client.gametest.v1.context.TestSingleplayerContext;
import net.fabricmc.fabric.api.client.gametest.v1.screenshot.TestScreenshotOptions;
import net.frank.mc3ver.bonfire.ModBonfire;
import net.frank.mc3ver.tent.ExplorerTentLogic;
import net.frank.mc3ver.tent.ModTentItems;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.Holder;
import net.minecraft.core.component.DataComponents;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.component.DyedItemColor;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.biome.Biomes;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.levelgen.Heightmap;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.Vec3;

// Bilder für Modrinth und CurseForge: Lager mit Zelten und Signalfeuern, Bäckerei mit Bewohnerinnen, Gegenstände an
// einer Birnenholzwand. Das Titelbild am alten Birnbaum macht GalleryScreenshotClientGameTest. Läuft nur mit
// STORE_GALLERY_DIR=<ordner> ./gradlew runClientGameTest.
public class StoreGalleryClientGameTest implements FabricClientGameTest {

    private static final String DIR = System.getenv("STORE_GALLERY_DIR");

    @Override
    public void runTest(ClientGameTestContext context) {
        if (DIR == null) return;
        try (TestSingleplayerContext game = context.worldBuilder()
                .setUseConsistentSettings(false)
                .adjustSettings(creator -> creator.setSeed("mc3ver"))
                .create()) {
            game.getServer().runCommand("gamemode spectator @p");
            game.getServer().runCommand("gamerule doDaylightCycle false");
            game.getServer().runCommand("gamerule doWeatherCycle false");
            game.getServer().runCommand("gamerule doMobSpawning false");
            game.getServer().runCommand("weather clear");
            context.runOnClient(client -> {
                if (!client.gui.hud.isHidden()) client.gui.hud.toggle();
                client.options.fov().set(70);
            });

            // Jede Szene in einer eigenen Ebene, damit rundherum natürliches Gelände steht statt Meer
            camp(context, game, plains(game, -1500, 0));
            bakery(context, game, plains(game, 1500, 0));
            items(context, game, plains(game, 0, 1500));
        }
    }

    // Drei Zelte und zwei Signalfeuer mit gefärbtem Rauch, Abendlicht
    private static void camp(ClientGameTestContext context, TestSingleplayerContext game, BlockPos at) {
        game.getServer().runCommand("time set 12300");
        int cx = at.getX();
        int cz = at.getZ();
        int y = flatten(context, game, cx, cz, 15);
        game.getServer().runOnServer(server -> {
            ServerLevel level = server.overworld();
            ServerPlayer player = server.getPlayerList().getPlayers().getFirst();
            // Zelte vorn, Feuer dahinter, damit die Flammen die Zelte nicht verdecken
            tent(player, cx - 6, y, cz, "blue", Direction.SOUTH);
            tent(player, cx, y, cz + 2, "orange", Direction.SOUTH);
            tent(player, cx + 6, y, cz, "green", Direction.SOUTH);
            bonfire(level, player, cx - 4, y, cz + 9, DyeColor.RED);
            bonfire(level, player, cx + 4, y, cz + 10, DyeColor.LIGHT_BLUE);
        });
        look(game, cx + 0.5, y + 2.8, cz - 9.5, cx + 0.5, y + 2.6, cz + 4.5);
        settle(context, game, 120);
        context.takeScreenshot(shot("store_camp"));
    }

    // Bäckerei aus der Dorfvorlage, davor drei Bewohnerinnen, die Bäckerin und ein Bewohner
    private static void bakery(ClientGameTestContext context, TestSingleplayerContext game, BlockPos at) {
        game.getServer().runCommand("time set 3000");
        int bx = at.getX();
        int bz = at.getZ();
        int y = flatten(context, game, bx, bz, 12);
        game.getServer()
                .runCommand("place template mc3ver:village/plains/houses/plains_bakery " + (bx - 4) + " " + y + " "
                        + (bz + 2));
        // Unterstes UUID-Bit 0: weiblich, die Bits darüber wählen das Aussehen
        summon(game, bx - 3.5, y, bz - 3.0, new UUID(40L, 0L), "");
        summon(game, bx - 1.5, y, bz - 4.0, new UUID(40L, 2L), "");
        summon(game, bx + 0.5, y, bz - 3.0, new UUID(40L, 1L), "");
        summon(game, bx + 2.5, y, bz - 4.0, new UUID(40L, 4L), "");
        summon(
                game,
                bx + 4.5,
                y,
                bz - 3.0,
                new UUID(41L, 0L),
                "VillagerData:{profession:\"mc3ver:baker\",level:3,type:\"minecraft:plains\"},");
        look(game, bx + 0.5, y + 2.6, bz - 10.5, bx + 0.5, y + 2.0, bz);
        settle(context, game, 40);
        context.takeScreenshot(shot("store_bakery"));
    }

    // Birnenholzwand mit Rahmen: Waffen und Werkzeuge, Birnen, Stein, Fahne, Karte; davor Tür und Boot
    private static void items(ClientGameTestContext context, TestSingleplayerContext game, BlockPos at) {
        game.getServer().runCommand("time set 6000");
        int ix = at.getX();
        int iz = at.getZ();
        int y = flatten(context, game, ix, iz, 9);
        int wallZ = iz + 3;
        game.getServer()
                .runCommand("fill " + (ix - 5) + " " + y + " " + wallZ + " " + (ix + 5) + " " + (y + 4) + " " + wallZ
                        + " mc3ver:pear_planks");
        game.getServer()
                .runCommand("fill " + (ix - 5) + " " + (y + 5) + " " + wallZ + " " + (ix + 5) + " " + (y + 5) + " "
                        + wallZ + " mc3ver:pear_slab");
        game.getServer()
                .runCommand("setblock " + ix + " " + y + " " + wallZ + " mc3ver:pear_door[facing=north,half=lower]");
        game.getServer()
                .runCommand(
                        "setblock " + ix + " " + (y + 1) + " " + wallZ + " mc3ver:pear_door[facing=north,half=upper]");
        String[] top = {"sonic_sword", "lightning_wand", "wind_wand", "white_flag", "friendship_stone"};
        String[] bottom = {"golden_pear", "pear_cake", "transport_map", "transport_flame", "pear"};
        for (int i = 0; i < top.length; i++) {
            int x = ix - 5 + i + (i >= 2 ? 1 : 0) + (i >= 3 ? 3 : 0);
            frame(game, x, y + 3, wallZ - 1, top[i]);
            frame(game, x, y + 2, wallZ - 1, bottom[i]);
        }
        game.getServer().runCommand("setblock " + (ix - 2) + " " + y + " " + (wallZ - 1) + " mc3ver:pear_fence");
        game.getServer().runCommand("setblock " + (ix + 2) + " " + y + " " + (wallZ - 1) + " mc3ver:pear_fence");
        game.getServer().runCommand("setblock " + (ix - 2) + " " + (y + 1) + " " + (wallZ - 1) + " minecraft:lantern");
        game.getServer().runCommand("setblock " + (ix + 2) + " " + (y + 1) + " " + (wallZ - 1) + " minecraft:lantern");
        game.getServer()
                .runCommand(
                        "summon mc3ver:pear_boat " + (ix - 3.0) + " " + y + " " + (iz + 0.5) + " {Rotation:[60f,0f]}");
        game.getServer()
                .runCommand("summon mc3ver:pear_chest_boat " + (ix + 3.0) + " " + y + " " + (iz + 0.5)
                        + " {Rotation:[120f,0f]}");
        look(game, ix + 0.5, y + 2.4, iz - 5.0, ix + 0.5, y + 1.8, wallZ);
        settle(context, game, 40);
        context.takeScreenshot(shot("store_items"));
    }

    // Nächste Ebene von (x,z) aus, Biom-Suche wie /locate biome
    private static BlockPos plains(TestSingleplayerContext game, int x, int z) {
        return game.getServer().computeOnServer(server -> {
            Pair<BlockPos, Holder<Biome>> found = server.overworld()
                    .findClosestBiome3d(biome -> biome.is(Biomes.PLAINS), new BlockPos(x, 64, z), 3000, 32, 64);
            if (found == null) throw new AssertionError("Keine Ebene bei " + x + "," + z);
            return found.getFirst();
        });
    }

    // Ebnet ein Quadrat um die Mitte auf Bodenhöhe ein: darunter Erde, oben Gras, darüber Luft
    private static int flatten(ClientGameTestContext context, TestSingleplayerContext game, int x, int z, int r) {
        game.getServer().runCommand("tp @p " + x + " 200 " + z);
        game.getConnection().waitForChunksRender();
        context.waitTicks(20);
        int y = game.getServer()
                .computeOnServer(
                        server -> server.overworld().getHeight(Heightmap.Types.MOTION_BLOCKING_NO_LEAVES, x, z));
        String box = (x - r) + " %d " + (z - r) + " " + (x + r) + " %d " + (z + r);
        game.getServer().runCommand("fill " + String.format(box, y - 4, y - 2) + " minecraft:dirt");
        game.getServer().runCommand("fill " + String.format(box, y - 1, y - 1) + " minecraft:grass_block");
        game.getServer().runCommand("fill " + String.format(box, y, y + 20) + " minecraft:air");
        return y;
    }

    private static void tent(ServerPlayer player, int x, int y, int z, String color, Direction facing) {
        ItemStack tent = new ItemStack(ModTentItems.EXPLORER_TENT);
        tent.set(DataComponents.DYED_COLOR, new DyedItemColor(ExplorerTentLogic.getColorRgb(color)));
        use(player, tent, new BlockPos(x, y - 1, z), facing);
    }

    private static void bonfire(ServerLevel level, ServerPlayer player, int x, int y, int z, DyeColor color) {
        BlockPos ground = new BlockPos(x, y - 1, z);
        use(player, new ItemStack(ModBonfire.BONFIRE_ITEM), ground, Direction.SOUTH);
        for (BlockPos p : BlockPos.betweenClosed(ground.offset(-2, 1, -2), ground.offset(2, 3, 2))) {
            BlockState state = level.getBlockState(p);
            if (!state.is(ModBonfire.BONFIRE)) continue;
            ItemStack dye = new ItemStack(Items.DYE.pick(color));
            player.setItemInHand(InteractionHand.MAIN_HAND, dye);
            BlockHitResult hit = new BlockHitResult(Vec3.atCenterOf(p), Direction.UP, p.immutable(), false);
            state.useItemOn(dye, level, player, InteractionHand.MAIN_HAND, hit);
            player.setItemInHand(InteractionHand.MAIN_HAND, ItemStack.EMPTY);
            return;
        }
        throw new AssertionError("Signalfeuer nicht gefunden bei " + ground);
    }

    // Stellt ein Item so auf, wie es ein Spieler mit Rechtsklick auf den Boden täte
    private static void use(ServerPlayer player, ItemStack stack, BlockPos ground, Direction facing) {
        player.setYRot(facing.toYRot());
        player.setItemInHand(InteractionHand.MAIN_HAND, stack);
        BlockHitResult hit = new BlockHitResult(Vec3.atCenterOf(ground).add(0, 0.5, 0), Direction.UP, ground, false);
        stack.getItem().useOn(new UseOnContext(player, InteractionHand.MAIN_HAND, hit));
        player.setItemInHand(InteractionHand.MAIN_HAND, ItemStack.EMPTY);
    }

    private static void frame(TestSingleplayerContext game, int x, int y, int z, String item) {
        game.getServer()
                .runCommand("summon item_frame " + x + " " + y + " " + z + " {Facing:2b,Fixed:1b,Item:{id:\"mc3ver:"
                        + item + "\",count:1}}");
    }

    private static void summon(TestSingleplayerContext game, double x, int y, double z, UUID uuid, String nbt) {
        int[] ints = {
            (int) (uuid.getMostSignificantBits() >> 32),
            (int) uuid.getMostSignificantBits(),
            (int) (uuid.getLeastSignificantBits() >> 32),
            (int) uuid.getLeastSignificantBits()
        };
        game.getServer()
                .runCommand("summon villager " + x + " " + y + " " + z + " {NoAI:1b,Rotation:[180f,0f]," + nbt
                        + "UUID:[I;" + ints[0] + "," + ints[1] + "," + ints[2] + "," + ints[3] + "]}");
    }

    // Kamera an (x,y,z), Blick auf (tx,ty,tz); y ist die Augenhöhe
    private static void look(
            TestSingleplayerContext game, double x, double y, double z, double tx, double ty, double tz) {
        game.getServer().runCommand("tp @p " + x + " " + (y - 1.62) + " " + z + " facing " + tx + " " + ty + " " + tz);
    }

    private static void settle(ClientGameTestContext context, TestSingleplayerContext game, int ticks) {
        game.getConnection().waitForChunksRender();
        context.waitTicks(ticks);
    }

    private static TestScreenshotOptions shot(String name) {
        return TestScreenshotOptions.of(name).withSize(1920, 1080).withDestinationDir(Path.of(DIR));
    }
}
