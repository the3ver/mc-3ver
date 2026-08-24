package net.frank.mc3ver;

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
	public static final String MOD_NAME = "MC 3ver";
	public static final String MOD_VERSION = "0.1.1";

	public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);

	@Override
	public void onInitialize() {
		LOGGER.info("{} v{} wurde initialisiert!", MOD_NAME, MOD_VERSION);

		// Event: Spieler betritt die Welt
		ServerPlayConnectionEvents.JOIN.register((handler, sender, server) -> {
			ServerPlayer player = handler.getPlayer();
			player.sendSystemMessage(WelcomeMessageHandler.createWelcomeComponent(MOD_NAME, MOD_VERSION));
			player.level().playSound(null, player.getX(), player.getY(), player.getZ(), SoundEvents.PLAYER_LEVELUP, SoundSource.PLAYERS, 0.4f, 1.2f);
		});
	}

	public static Identifier id(String path) {
		return Identifier.fromNamespaceAndPath(MOD_ID, path);
	}
}
