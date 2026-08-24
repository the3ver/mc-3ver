package net.frank.mc3ver.client;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.frank.mc3ver.DoubleJumpLogic;
import net.frank.mc3ver.Mc3verMod;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.phys.Vec3;

public class Mc3verClient implements ClientModInitializer {
	private static boolean canDoubleJump = false;
	private static boolean jumpWasDown = false;

	@Override
	public void onInitializeClient() {
		Mc3verMod.LOGGER.info("MC 3ver Client-Features aktiviert (Doppelsprung aktiv)!");

		// Client-Tick Event für den Doppelsprung
		ClientTickEvents.END_CLIENT_TICK.register(client -> {
			LocalPlayer player = client.player;
			if (player == null || client.isPaused()) {
				return;
			}

			boolean isJumpKeyDown = client.options.keyJump.isDown();

			// Prüfen, ob der Doppelsprung aufgeladen werden soll
			if (DoubleJumpLogic.shouldResetDoubleJump(
				player.onGround(),
				player.isInWater(),
				player.isInLava(),
				player.isPassenger(),
				player.onClimbable()
			)) {
				canDoubleJump = true;
			} else if (DoubleJumpLogic.shouldAllowDoubleJump(
				canDoubleJump,
				isJumpKeyDown,
				jumpWasDown,
				player.getAbilities().flying,
				player.isFallFlying(),
				player.isSpectator()
			)) {
				performDoubleJump(player);
				canDoubleJump = false;
			}

			jumpWasDown = isJumpKeyDown;
		});
	}

	private static void performDoubleJump(LocalPlayer player) {
		Vec3 currentVelocity = player.getDeltaMovement();
		Vec3 look = player.getLookAngle();

		// Berechnung über DoubleJumpLogic
		double[] newVel = DoubleJumpLogic.calculateNewVelocity(
			currentVelocity.x,
			currentVelocity.z,
			look.x,
			look.z,
			0.52,
			0.15
		);

		player.setDeltaMovement(newVel[0], newVel[1], newVel[2]);

		// Sound-Effekt (Wind-Burst)
		player.playSound(SoundEvents.WIND_CHARGE_BURST.value(), 0.7f, 1.3f);

		// Partikel-Effekte unter den Füßen des Spielers
		for (int i = 0; i < 12; i++) {
			double px = player.getX() + (player.getRandom().nextDouble() - 0.5) * 0.6;
			double py = player.getY() + 0.05;
			double pz = player.getZ() + (player.getRandom().nextDouble() - 0.5) * 0.6;

			double vx = (player.getRandom().nextDouble() - 0.5) * 0.2;
			double vy = -0.05;
			double vz = (player.getRandom().nextDouble() - 0.5) * 0.2;

			player.level().addParticle(ParticleTypes.CLOUD, px, py, pz, vx, vy, vz);
			player.level().addParticle(ParticleTypes.FIREWORK, px, py, pz, vx * 0.5, 0.05, vz * 0.5);
		}
	}
}
