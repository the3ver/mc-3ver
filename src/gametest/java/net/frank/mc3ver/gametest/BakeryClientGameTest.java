package net.frank.mc3ver.gametest;

import net.fabricmc.fabric.api.client.gametest.v1.FabricClientGameTest;
import net.fabricmc.fabric.api.client.gametest.v1.context.ClientGameTestContext;
import net.fabricmc.fabric.api.client.gametest.v1.context.TestSingleplayerContext;
import net.frank.mc3ver.village.BakeryLayoutLogic;
import net.frank.mc3ver.village.ModVillagers;
import net.minecraft.core.BlockPos;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.Identifier;
import net.minecraft.server.MinecraftServer;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.entity.ChestBlockEntity;
import net.minecraft.world.level.levelgen.structure.pools.StructurePoolElement;
import net.minecraft.world.level.levelgen.structure.pools.StructureTemplatePool;

// Die Bäckerei steht in allen fünf Häuser-Pools und lässt sich je Dorftyp aufstellen: Ofen, Schornstein, Truhe.
// Aufruf: ./gradlew runClientGameTest, Screenshot unter build/run/clientGameTest/screenshots
public class BakeryClientGameTest implements FabricClientGameTest {

    private static final int Y = 150;
    private static final int SPACING = 14;

    @Override
    public void runTest(ClientGameTestContext context) {
        try (TestSingleplayerContext game = context.worldBuilder().create()) {
            game.getServer().computeOnServer(BakeryClientGameTest::checkPools);
            game.getServer().runCommand("gamerule doDaylightCycle false");
            game.getServer().runCommand("time set 6000");
            game.getServer().runCommand("difficulty peaceful");
            int width = SPACING * BakeryLayoutLogic.STYLES.size();
            game.getServer().runCommand("fill -2 " + (Y - 1) + " -4 " + width + " " + (Y - 1) + " 14 grass_block");
            game.getServer().runCommand("fill -2 " + Y + " -4 " + width + " " + (Y + 12) + " 14 air");
            for (int i = 0; i < BakeryLayoutLogic.STYLES.size(); i++) {
                String style = BakeryLayoutLogic.STYLES.get(i);
                game.getServer()
                        .runCommand("place template " + BakeryLayoutLogic.templateId(style) + " " + i * SPACING + " "
                                + Y + " 0");
            }
            context.waitTicks(20);
            for (int i = 0; i < BakeryLayoutLogic.STYLES.size(); i++) {
                int x = i * SPACING;
                String style = BakeryLayoutLogic.STYLES.get(i);
                game.getServer().computeOnServer(server -> checkHouse(server, x, style));
            }
            game.getServer().runCommand("gamemode spectator @p");
            game.getServer().runCommand("tp @p -9 " + (Y + 6) + " -9 -45 20");
            game.getConnection().waitForChunksRender();
            context.takeScreenshot("bakery");
        }
    }

    private static boolean checkPools(MinecraftServer server) {
        var pools = server.registryAccess().lookupOrThrow(Registries.TEMPLATE_POOL);
        for (String style : BakeryLayoutLogic.STYLES) {
            StructureTemplatePool pool = pools.getValue(Identifier.parse(BakeryLayoutLogic.poolId(style)));
            check(pool != null, "Pool fehlt: " + style);
            String template = BakeryLayoutLogic.templateId(style);
            long count = pool.getShuffledTemplates(net.minecraft.util.RandomSource.create(1)).stream()
                    .map(StructurePoolElement::toString)
                    .filter(element -> element.contains(template))
                    .count();
            check(count == BakeryLayoutLogic.POOL_WEIGHT, style + ": Bäckerei " + count + "-mal im Pool");
        }
        return true;
    }

    private static boolean checkHouse(MinecraftServer server, int x, String style) {
        var level = server.overworld();
        BlockPos origin = new BlockPos(x, Y, 0);
        check(level.getBlockState(origin.offset(6, 1, 5)).is(ModVillagers.BAKING_OVEN), style + ": Ofen fehlt");
        check(
                level.getBlockState(origin.offset(BakeryLayoutLogic.CHIMNEY_X, BakeryLayoutLogic.CHIMNEY_TOP, 5))
                        .is(Blocks.BRICKS),
                style + ": Schornstein fehlt");
        check(level.getBlockEntity(origin.offset(6, 1, 7)) instanceof ChestBlockEntity, style + ": Truhe fehlt");
        return true;
    }

    private static void check(boolean ok, String message) {
        if (!ok) throw new AssertionError(message);
    }
}
