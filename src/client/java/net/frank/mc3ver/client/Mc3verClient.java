package net.frank.mc3ver.client;

import net.fabricmc.api.ClientModInitializer;
import net.frank.mc3ver.Mc3verMod;

public class Mc3verClient implements ClientModInitializer {
	@Override
	public void onInitializeClient() {
		Mc3verMod.LOGGER.info("MC 3ver client initialized!");
	}
}
