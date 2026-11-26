package net.frank.mc3ver.gametest;

import java.util.Set;
import java.util.UUID;
import net.fabricmc.fabric.api.client.gametest.v1.FabricClientGameTest;
import net.fabricmc.fabric.api.client.gametest.v1.context.ClientGameTestContext;
import net.fabricmc.fabric.api.client.gametest.v1.context.TestSingleplayerContext;
import net.frank.mc3ver.village.BakerTitleLogic;
import net.frank.mc3ver.village.ModVillagers;
import net.minecraft.client.gui.screens.inventory.MerchantScreen;
import net.minecraft.core.BlockPos;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.chat.Component;
import net.minecraft.server.MinecraftServer;
import net.minecraft.world.entity.ai.memory.MemoryModuleType;
import net.minecraft.world.entity.npc.villager.Villager;
import net.minecraft.world.inventory.MerchantMenu;
import net.minecraft.world.item.trading.MerchantOffer;

// Ein Bewohner neben dem Backofen bleibt arbeitslos, eine Bewohnerin wird Bäckerin und handelt Backwaren.
// Aufruf: ./gradlew runClientGameTest, Screenshot unter build/run/clientGameTest/screenshots
public class BakerClientGameTest implements FabricClientGameTest {

    private static final int Y = 150;
    private static final int POLL_TICKS = 20;
    private static final int MAX_POLLS = 60;
    // Unterstes UUID-Bit gerade: Bewohnerin, ungerade: Bewohner
    private static final UUID MAN = new UUID(5L << 32 | 6, 7L << 32 | 9);
    private static final UUID WOMAN = new UUID(5L << 32 | 6, 7L << 32 | 8);
    private static final Set<String> LEVEL_1_RESULTS = Set.of("minecraft:emerald", "minecraft:bread", "mc3ver:pear");

    @Override
    public void runTest(ClientGameTestContext context) {
        try (TestSingleplayerContext game = context.worldBuilder().create()) {
            game.getServer().runCommand("gamerule doDaylightCycle false");
            game.getServer().runCommand("gamerule doMobSpawning false");
            game.getServer().runCommand("time set 3000");
            game.getServer().runCommand("difficulty peaceful");
            game.getServer().runCommand("fill -12 " + (Y - 1) + " -12 12 " + (Y - 1) + " 12 stone");
            game.getServer().runCommand("fill -12 " + Y + " -12 12 " + (Y + 10) + " 12 air");
            game.getServer().runCommand("setblock 0 " + Y + " 0 mc3ver:baking_oven[facing=west]");
            game.getServer().runCommand("gamemode creative @p");
            game.getServer().runCommand("tp @p -5.5 " + Y + " 0.5 -90 15");
            game.getConnection().waitForChunksRender();

            // 1. Bewohner: findet den Ofen nicht als Arbeitsplatz
            summon(-2.5, -2.5, MAN, game);
            context.waitTicks(POLL_TICKS * MAX_POLLS / 2);
            check(!game.getServer().computeOnServer(server -> isBaker(server, MAN)), "Bewohner wurde Bäcker");

            // 2. Bewohnerin: nimmt den Ofen, obwohl der Bewohner daneben steht
            summon(-2.5, 2.5, WOMAN, game);
            boolean baker = false;
            for (int i = 0; i < MAX_POLLS && !baker; i++) {
                context.waitTicks(POLL_TICKS);
                baker = game.getServer().computeOnServer(server -> isBaker(server, WOMAN));
            }
            check(
                    baker,
                    "Bewohnerin wurde nicht Bäckerin: "
                            + game.getServer().computeOnServer(BakerClientGameTest::diagnose));
            check(!game.getServer().computeOnServer(server -> isBaker(server, MAN)), "Bewohner wurde Bäcker");

            // 3. Angebote der Stufe 1 kommen aus den Bäcker-Trades
            Set<String> results = game.getServer().computeOnServer(server -> {
                Villager woman = (Villager) server.overworld().getEntity(WOMAN);
                return woman.getOffers().stream()
                        .map(MerchantOffer::getResult)
                        .map(stack ->
                                BuiltInRegistries.ITEM.getKey(stack.getItem()).toString())
                        .collect(java.util.stream.Collectors.toSet());
            });
            System.out.println("[baker] Angebote: " + results);
            check(!results.isEmpty() && LEVEL_1_RESULTS.containsAll(results), "Falsche Angebote: " + results);
            context.takeScreenshot("baker");

            // 4. Handelsmenü einer Meisterin: Stufenname in weiblicher Form (MerchantScreenMixin)
            context.setScreen(() -> {
                var inventory =
                        net.minecraft.client.Minecraft.getInstance().player.getInventory();
                MerchantMenu menu = new MerchantMenu(0, inventory);
                menu.setMerchantLevel(5);
                menu.setShowProgressBar(true);
                return new MerchantScreen(menu, inventory, Component.translatable(BakerTitleLogic.BAKER_NAME_KEY));
            });
            context.waitTicks(5);
            context.takeScreenshot("baker_master");
            context.setScreen(() -> null);
        }
    }

    private static void summon(double x, double z, UUID uuid, TestSingleplayerContext game) {
        game.getServer()
                .runCommand("summon villager " + x + " " + Y + " " + z + " {UUID:[I;"
                        + (int) (uuid.getMostSignificantBits() >> 32) + "," + (int) uuid.getMostSignificantBits() + ","
                        + (int) (uuid.getLeastSignificantBits() >> 32) + "," + (int) uuid.getLeastSignificantBits()
                        + "]}");
    }

    private static boolean isBaker(MinecraftServer server, UUID uuid) {
        Villager villager = (Villager) server.overworld().getEntity(uuid);
        check(villager != null, "Dorfbewohner " + uuid + " fehlt");
        return villager.getVillagerData().profession().is(ModVillagers.BAKER_KEY);
    }

    private static String diagnose(MinecraftServer server) {
        StringBuilder out = new StringBuilder();
        out.append("POI am Ofen: ").append(server.overworld().getPoiManager().getType(new BlockPos(0, Y, 0)));
        for (UUID uuid : new UUID[] {MAN, WOMAN}) {
            Villager villager = (Villager) server.overworld().getEntity(uuid);
            out.append("; ")
                    .append(ModVillagers.isFemale(villager) ? "Bewohnerin" : "Bewohner")
                    .append(" bei ")
                    .append(villager.blockPosition().toShortString())
                    .append(" Ticks ")
                    .append(villager.tickCount)
                    .append(" KI aus ")
                    .append(villager.isNoAi())
                    .append(" Aktivitäten ")
                    .append(villager.getBrain().getActiveActivities())
                    .append(" Beruf ")
                    .append(villager.getVillagerData().profession().getRegisteredName())
                    .append(" potentiell ")
                    .append(villager.getBrain().getMemory(MemoryModuleType.POTENTIAL_JOB_SITE))
                    .append(" Arbeitsplatz ")
                    .append(villager.getBrain().getMemory(MemoryModuleType.JOB_SITE));
        }
        return out.toString();
    }

    private static void check(boolean ok, String message) {
        if (!ok) throw new AssertionError(message);
    }
}
