package net.frank.mc3ver.client;

import net.fabricmc.fabric.api.client.particle.v1.ParticleProviderRegistry;
import net.frank.mc3ver.grove.ModGrove;
import net.minecraft.client.particle.SingleQuadParticle;
import net.minecraft.client.particle.SuspendedParticle;

final class PearPollenClient {

    // Warmes Gold, passend zum Gras des Hains
    private static final float RED = 1.0f;
    private static final float GREEN = 0.82f;
    private static final float BLUE = 0.32f;

    private PearPollenClient() {}

    static void register() {
        // Schwebt wie die Sporen der Sporenblüte: langsam sinkend, lange sichtbar; nur die Farbe ist golden
        ParticleProviderRegistry.getInstance().register(ModGrove.POLLEN, sprites -> {
            SuspendedParticle.SporeBlossomAirProvider spores = new SuspendedParticle.SporeBlossomAirProvider(sprites);
            return (type, level, x, y, z, dx, dy, dz, random) -> {
                SingleQuadParticle particle =
                        (SingleQuadParticle) spores.createParticle(type, level, x, y, z, dx, dy, dz, random);
                particle.setColor(RED, GREEN, BLUE);
                return particle;
            };
        });
    }
}
