package net.frank.mc3ver.gametest;

import java.util.List;
import net.fabricmc.fabric.api.client.gametest.v1.FabricClientGameTest;
import net.fabricmc.fabric.api.client.gametest.v1.context.ClientGameTestContext;
import net.fabricmc.fabric.api.client.gametest.v1.context.TestSingleplayerContext;
import net.frank.mc3ver.sonic.SonicSwordLogic;
import net.minecraft.core.BlockPos;
import net.minecraft.server.MinecraftServer;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.animal.pig.Pig;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.phys.AABB;

// Echte Eingaben: Linksklick halten feuert den Schallstrahl, Rechtsklick halten die Schallwelle,
// und mit dem Schwert in der Hand bleibt ein angeklickter Block stehen.
// Aufruf: ./gradlew runClientGameTest, Screenshot des Aufladebalkens unter build/run/clientGameTest/screenshots
public class SonicSwordClientGameTest implements FabricClientGameTest {

    private static final int Y = 150;
    private static final int COOLDOWN_WAIT = 45;

    @Override
    public void runTest(ClientGameTestContext context) {
        try (TestSingleplayerContext game = context.worldBuilder().create()) {
            game.getServer().runCommand("gamerule doDaylightCycle false");
            game.getServer().runCommand("gamerule doMobSpawning false");
            game.getServer().runCommand("time set 6000");
            game.getServer().runCommand("difficulty peaceful");
            game.getServer().runCommand("fill -12 " + (Y - 1) + " -12 30 " + (Y - 1) + " 12 stone");
            game.getServer().runCommand("fill -12 " + Y + " -12 30 " + (Y + 10) + " 12 air");
            game.getServer().runCommand("gamemode survival @p");
            game.getServer().runCommand("tp @p 0.5 " + Y + " 0.5 -90 0");
            game.getServer().runCommand("item replace entity @p weapon.mainhand with mc3ver:sonic_sword");
            game.getConnection().waitForChunksRender();
            context.waitTicks(20);

            // 1. Block auf Augenhöhe: gedrückt halten baut ihn nicht ab
            BlockPos dirt = new BlockPos(2, Y + 1, 0);
            game.getServer().runCommand("setblock 2 " + (Y + 1) + " 0 dirt");
            context.waitTicks(5);
            context.getInput().holdKeyFor(o -> o.keyAttack, 60);
            boolean dirtStands = game.getServer()
                    .computeOnServer(
                            server -> server.overworld().getBlockState(dirt).is(Blocks.DIRT));
            check(dirtStands, "Erde wurde mit dem Schallschwert abgebaut");
            game.getServer().runCommand("setblock 2 " + (Y + 1) + " 0 air");
            context.waitTicks(COOLDOWN_WAIT);

            // 2. Schallstrahl: Schwein in 5 Blöcken fliegt weit weg
            game.getServer().runCommand("summon pig 5.5 " + Y + " 0.5");
            game.getServer().runCommand("tp @p 0.5 " + Y + " 0.5 -90 12");
            context.waitTicks(5);
            double before = game.getServer()
                    .computeOnServer(server -> pigs(server).getFirst().getX());
            context.getInput().holdKeyFor(o -> o.keyAttack, SonicSwordLogic.BEAM_FULL_CHARGE_TICKS + 5);
            context.waitTicks(20);
            double after = game.getServer()
                    .computeOnServer(server -> pigs(server).getFirst().getX());
            System.out.println("[sonic] Strahl: Schwein von x=" + before + " nach x=" + after);
            check(after - before > 4.0, "Schallstrahl hat das Schwein nicht weggestoßen: " + before + " -> " + after);
            game.getServer().runCommand("kill @e[type=pig]");
            context.waitTicks(COOLDOWN_WAIT);

            // 3. Aufladebalken beim Halten fotografieren
            context.getInput().holdKey(o -> o.keyAttack);
            context.waitTicks(20);
            context.takeScreenshot("sonic_sword_charge");
            context.getInput().releaseKey(o -> o.keyAttack);
            context.waitTicks(COOLDOWN_WAIT);

            // 4. Schallwelle: Schwein daneben ist gelähmt und verletzt
            game.getServer().runCommand("summon pig 2.5 " + Y + " 2.5");
            context.waitTicks(5);
            context.getInput().holdKeyFor(o -> o.keyUse, SonicSwordLogic.WAVE_FULL_CHARGE_TICKS + 5);
            context.waitTicks(2);
            String wave = game.getServer().computeOnServer(server -> {
                Pig pig = pigs(server).getFirst();
                MobEffectInstance slow = pig.getEffect(MobEffects.SLOWNESS);
                return (slow == null ? "keine Langsamkeit" : "Langsamkeit " + slow.getAmplifier()) + ", Leben "
                        + pig.getHealth() + "/" + pig.getMaxHealth();
            });
            System.out.println("[sonic] Welle: " + wave);
            check(
                    wave.startsWith("Langsamkeit " + SonicSwordLogic.PARALYSIS_AMPLIFIER)
                            && !wave.endsWith("10.0/10.0"),
                    "Schallwelle hat nicht gelähmt oder nicht verletzt: " + wave);
        }
    }

    private static List<Pig> pigs(MinecraftServer server) {
        List<Pig> pigs = server.overworld().getEntitiesOfClass(Pig.class, new AABB(-40, Y - 5, -40, 60, Y + 30, 40));
        check(!pigs.isEmpty(), "Kein Schwein gefunden");
        return pigs;
    }

    private static void check(boolean ok, String message) {
        if (!ok) throw new AssertionError(message);
    }
}
