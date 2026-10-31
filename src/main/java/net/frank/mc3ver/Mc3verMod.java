package net.frank.mc3ver;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import java.io.IOException;
import java.io.InputStream;
import java.io.UncheckedIOException;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.networking.v1.ServerPlayConnectionEvents;
import net.minecraft.resources.Identifier;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Mc3verMod implements ModInitializer {
    public static final String MOD_ID = "mc3ver";
    public static final String MOD_NAME = "WorldExplorerMod";
    // Einzige Quelle ist gradle.properties; processResources schreibt sie in fabric.mod.json
    public static final String MOD_VERSION = readModVersion();

    public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);

    private static String readModVersion() {
        try {
            List<String> modJsons = new ArrayList<>();
            Enumeration<URL> urls = Mc3verMod.class.getClassLoader().getResources("fabric.mod.json");
            while (urls.hasMoreElements()) {
                try (InputStream in = urls.nextElement().openStream()) {
                    modJsons.add(new String(in.readAllBytes(), StandardCharsets.UTF_8));
                }
            }
            return findModVersion(modJsons);
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }
    }

    // Alle Mods teilen sich einen Classloader, die erste fabric.mod.json gehört oft einem anderen Mod
    static String findModVersion(List<String> modJsons) {
        for (String modJson : modJsons) {
            JsonObject json = JsonParser.parseString(modJson).getAsJsonObject();
            if (json.has("id") && MOD_ID.equals(json.get("id").getAsString())) {
                return json.get("version").getAsString();
            }
        }
        throw new IllegalStateException("fabric.mod.json von " + MOD_ID + " fehlt im Classpath");
    }

    @Override
    public void onInitialize() {
        LOGGER.info("{} v{} wurde initialisiert!", MOD_NAME, MOD_VERSION);

        net.frank.mc3ver.transport.ModDataComponents.register();
        net.frank.mc3ver.transport.ModBlocks.register();
        net.frank.mc3ver.transport.ModItems.register();
        net.frank.mc3ver.transport.TransportFlameNaming.register();
        net.frank.mc3ver.tree.PearTreeBlocks.register();
        net.frank.mc3ver.tree.PearTreeItems.register();
        net.frank.mc3ver.tree.PearTreeSaplingGenerator.registerWorldGen();
        net.frank.mc3ver.grove.ModGrove.register();
        net.frank.mc3ver.tent.ModTentBlocks.register();
        net.frank.mc3ver.tent.ModTentItems.register();
        net.frank.mc3ver.peace.ModPeace.register();
        net.frank.mc3ver.bonfire.ModBonfire.register();
        net.frank.mc3ver.friend.ModFriendStone.register();

        // Event: Spieler betritt die Welt
        ServerPlayConnectionEvents.JOIN.register((handler, sender, server) -> {
            ServerPlayer player = handler.getPlayer();
            player.sendSystemMessage(WelcomeMessageHandler.createWelcomeComponent(MOD_NAME, MOD_VERSION));
            player.level()
                    .playSound(
                            null,
                            player.getX(),
                            player.getY(),
                            player.getZ(),
                            SoundEvents.PLAYER_LEVELUP,
                            SoundSource.PLAYERS,
                            0.4f,
                            1.2f);
        });
    }

    public static Identifier id(String path) {
        return Identifier.fromNamespaceAndPath(MOD_ID, path);
    }
}
