package net.frank.mc3ver.client;

import net.fabricmc.api.ClientModInitializer;
import net.frank.mc3ver.Mc3verMod;

public class Mc3verClient implements ClientModInitializer {

    @Override
    public void onInitializeClient() {
        Mc3verMod.LOGGER.info(
                "WorldExplorerMod Client-Features aktiviert (Doppelsprung, Transportflammen & Birnenboote aktiv)!");

        PearBoatClient.register();
        BonfireClient.register();
        TransportClient.register();
        TentClient.register();
        DoubleJumpClient.register();
    }
}
