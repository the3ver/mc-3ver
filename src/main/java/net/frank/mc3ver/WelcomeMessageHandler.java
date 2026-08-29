package net.frank.mc3ver;

import net.minecraft.network.chat.Component;

public class WelcomeMessageHandler {

	public static final String TRANSLATION_KEY = "message.mc3ver.welcome";

	public static Component createWelcomeComponent(String modName, String version) {
		return Component.translatable(TRANSLATION_KEY, modName, version);
	}
}
