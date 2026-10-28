package net.frank.mc3ver.client;

import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.frank.mc3ver.transport.TransportFlameNaming;

final class TransportClient {

    private TransportClient() {}

    static void register() {
        // Eingabe-Screen für den Namen einer frisch aufgestellten Transportflamme
        ClientPlayNetworking.registerGlobalReceiver(TransportFlameNaming.OpenNameScreenPayload.TYPE, (payload, context) -> {
            if (!(context.client().gui.screen() instanceof TransportFlameNameScreen)) {
                context.client().gui.setScreen(new TransportFlameNameScreen(payload.pos()));
            }
        });
    }
}
