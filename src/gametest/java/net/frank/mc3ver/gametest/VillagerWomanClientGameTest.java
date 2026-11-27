package net.frank.mc3ver.gametest;

import java.util.List;
import java.util.UUID;
import net.fabricmc.fabric.api.client.gametest.v1.FabricClientGameTest;
import net.fabricmc.fabric.api.client.gametest.v1.context.ClientGameTestContext;
import net.fabricmc.fabric.api.client.gametest.v1.context.TestSingleplayerContext;
import net.frank.mc3ver.village.ModVillagers;
import net.minecraft.server.MinecraftServer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntitySpawnReason;
import net.minecraft.world.entity.EntityTypes;
import net.minecraft.world.entity.monster.zombie.Zombie;
import net.minecraft.world.entity.monster.zombie.ZombieVillager;
import net.minecraft.world.entity.npc.villager.Villager;
import net.minecraft.world.phys.AABB;

// Bewohnerin und Bewohner nebeneinander: Flag kommt aus der UUID, erreicht den Client und übersteht Zombifizierung
// und Heilung. Aufruf: ./gradlew runClientGameTest, Screenshot unter build/run/clientGameTest/screenshots
public class VillagerWomanClientGameTest implements FabricClientGameTest {

    private static final int Y = 150;
    // Unterstes UUID-Bit gerade: Bewohnerin, ungerade: Bewohner
    private static final UUID WOMAN = new UUID(1L << 32 | 2, 3L << 32 | 4);
    private static final UUID MAN = new UUID(1L << 32 | 2, 3L << 32 | 5);
    private static final float HEAD_TURN = 60.0f;
    private static final UUID CURED = new UUID(1L << 32 | 2, 3L << 32 | 6);

    @Override
    public void runTest(ClientGameTestContext context) {
        try (TestSingleplayerContext game = context.worldBuilder().create()) {
            game.getServer().runCommand("gamerule doDaylightCycle false");
            game.getServer().runCommand("gamerule doMobSpawning false");
            game.getServer().runCommand("time set 6000");
            game.getServer().runCommand("difficulty hard");
            game.getServer().runCommand("fill -12 " + (Y - 1) + " -12 12 " + (Y - 1) + " 12 stone");
            game.getServer().runCommand("fill -12 " + Y + " -12 12 " + (Y + 10) + " 12 air");
            game.getServer().runCommand("gamemode creative @p");
            game.getServer().runCommand("tp @p 0.5 " + Y + " 0.5 -90 10");
            summon("villager", 3.5, -0.7, WOMAN, game);
            summon("villager", 3.5, 1.7, MAN, game);
            game.getConnection().waitForChunksRender();
            context.waitTicks(20);

            // 1. Server würfelt aus der UUID
            check(game.getServer().computeOnServer(server -> female(server, WOMAN)), "Bewohnerin ist nicht weiblich");
            check(!game.getServer().computeOnServer(server -> female(server, MAN)), "Bewohner ist weiblich");

            // 2. Flag erreicht den Client (Skin)
            boolean clientSees = context.computeOnClient(client -> {
                Entity entity = client.level.getEntity(uuidToId(client.level.entitiesForRendering(), WOMAN));
                return entity instanceof Villager v && ModVillagers.isFemale(v);
            });
            check(clientSees, "Client kennt das Flag der Bewohnerin nicht");

            // Kopf der Bewohnerin seitlich drehen: Haar und Zopf müssen mitgehen (Screenshot)
            game.getServer().runOnServer(server -> {
                Villager woman = (Villager) server.overworld().getEntity(WOMAN);
                woman.setYHeadRot(90.0f + HEAD_TURN);
            });
            context.waitTicks(5);
            context.takeScreenshot("villager_woman");

            // 3. Zombifizieren und heilen: Flag bleibt
            summon("villager", -3.5, 0.5, CURED, game);
            context.waitTicks(5);
            game.getServer().runOnServer(server -> {
                Villager villager = (Villager) server.overworld().getEntity(CURED);
                Zombie zombie = EntityTypes.ZOMBIE.create(server.overworld(), EntitySpawnReason.COMMAND);
                check(
                        zombie.convertVillagerToZombieVillager(server.overworld(), villager),
                        "Zombifizierung fehlgeschlagen");
            });
            context.waitTicks(5);
            boolean zombieFemale = game.getServer().computeOnServer(server -> {
                List<ZombieVillager> zombies = server.overworld().getEntitiesOfClass(ZombieVillager.class, area());
                check(zombies.size() == 1, "Erwartet ein Zombie-Dorfbewohner, gefunden: " + zombies.size());
                return zombies.getFirst().getAttachedOrElse(ModVillagers.FEMALE, false);
            });
            check(zombieFemale, "Zombie-Dorfbewohnerin hat das Flag verloren");
            game.getServer().runCommand("data merge entity @e[type=zombie_villager,limit=1] {ConversionTime:1}");
            context.waitTicks(10);
            long curedWomen = game.getServer()
                    .computeOnServer(server ->
                            server.overworld().getEntitiesOfClass(Villager.class, area(), v -> v.getX() < 0).stream()
                                    .filter(ModVillagers::isFemale)
                                    .count());
            check(curedWomen == 1, "Geheilte Bewohnerin ist nicht mehr weiblich");
        }
    }

    private static void summon(String type, double x, double z, UUID uuid, TestSingleplayerContext game) {
        int[] ints = {
            (int) (uuid.getMostSignificantBits() >> 32),
            (int) uuid.getMostSignificantBits(),
            (int) (uuid.getLeastSignificantBits() >> 32),
            (int) uuid.getLeastSignificantBits()
        };
        game.getServer()
                .runCommand("summon " + type + " " + x + " " + Y + " " + z + " {NoAI:1b,Rotation:[90f,0f],UUID:[I;"
                        + ints[0] + "," + ints[1] + "," + ints[2] + "," + ints[3] + "]}");
    }

    private static boolean female(MinecraftServer server, UUID uuid) {
        Entity entity = server.overworld().getEntity(uuid);
        check(entity instanceof Villager, "Kein Dorfbewohner mit " + uuid);
        return ModVillagers.isFemale((Villager) entity);
    }

    private static int uuidToId(Iterable<Entity> entities, UUID uuid) {
        for (Entity entity : entities) {
            if (entity.getUUID().equals(uuid)) return entity.getId();
        }
        throw new AssertionError("Client kennt " + uuid + " nicht");
    }

    private static AABB area() {
        return new AABB(-12, Y - 2, -12, 12, Y + 5, 12);
    }

    private static void check(boolean ok, String message) {
        if (!ok) throw new AssertionError(message);
    }
}
