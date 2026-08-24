package net.frank.mc3ver;

import net.minecraft.network.chat.Component;

public class WelcomeMessageHandler {

	public static String getWelcomeText(String modName, String version) {
		return "§8[§a" + modName + "§8] §fWillkommen in der Welt! Mod §av" + version + "§f ist aktiv. §7(Doppelsprung aktiv: Drücke Leertaste in der Luft)";
	}

	public static Component createWelcomeComponent(String modName, String version) {
		return Component.literal(getWelcomeText(modName, version));
	}
}
