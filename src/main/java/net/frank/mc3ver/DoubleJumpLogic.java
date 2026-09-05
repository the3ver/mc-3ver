package net.frank.mc3ver;

public class DoubleJumpLogic {

	public static class DoubleJumpState {
		private boolean primed = false;
		private int extraJumpsRemaining = 1;

		public boolean update(
			boolean onGround,
			boolean inWater,
			boolean inLava,
			boolean isPassenger,
			boolean onClimbable,
			boolean isFlying,
			boolean isFallFlying,
			boolean isSpectator,
			boolean isJumpKeyDown,
			boolean jumpWasDown
		) {
			return update(onGround, inWater, inLava, isPassenger, onClimbable, isFlying, isFallFlying, isSpectator, isJumpKeyDown, jumpWasDown, 1);
		}

		public boolean update(
			boolean onGround,
			boolean inWater,
			boolean inLava,
			boolean isPassenger,
			boolean onClimbable,
			boolean isFlying,
			boolean isFallFlying,
			boolean isSpectator,
			boolean isJumpKeyDown,
			boolean jumpWasDown,
			int maxExtraJumps
		) {
			int maxJumps = Math.max(1, maxExtraJumps);

			// Wenn der Spieler den Boden berührt, schwimmt, klettert oder reitet, wird der Doppelsprung aufgeladen/zurückgesetzt
			if (onGround || inWater || inLava || isPassenger || onClimbable) {
				primed = false;
				extraJumpsRemaining = maxJumps;
				return false;
			}

			// In besonderen Bewegungsmodi kein Doppelsprung
			if (isFlying || isFallFlying || isSpectator) {
				primed = false;
				extraJumpsRemaining = 0;
				return false;
			}

			// Wenn noch Luftsprünge verfügbar sind und der Spieler in der Luft die Sprungtaste loslässt: scharfschalten
			if (extraJumpsRemaining > 0 && !isJumpKeyDown) {
				primed = true;
			}

			// Wenn scharfgeschaltet und die Sprungtaste in der Luft neu gedrückt wird: Auslösen und 1 Luftsprung verbrauchen!
			if (extraJumpsRemaining > 0 && primed && isJumpKeyDown && !jumpWasDown) {
				primed = false;
				extraJumpsRemaining--;
				return true;
			}

			return false;
		}

		public boolean isPrimed() {
			return primed;
		}

		public boolean canDoubleJump() {
			return extraJumpsRemaining > 0;
		}

		public int getExtraJumpsRemaining() {
			return extraJumpsRemaining;
		}
	}

	public static boolean shouldAllowDoubleJump(
		boolean canDoubleJump,
		boolean isJumpKeyDown,
		boolean jumpWasDown,
		boolean isFlying,
		boolean isFallFlying,
		boolean isSpectator
	) {
		if (!canDoubleJump || isFlying || isFallFlying || isSpectator) {
			return false;
		}
		return isJumpKeyDown && !jumpWasDown;
	}

	public static boolean shouldResetDoubleJump(
		boolean onGround,
		boolean inWater,
		boolean inLava,
		boolean isPassenger,
		boolean onClimbable
	) {
		return onGround || inWater || inLava || isPassenger || onClimbable;
	}

	public static double[] calculateNewVelocity(
		double currentVx,
		double currentVz,
		double lookX,
		double lookZ,
		double jumpY,
		double forwardBoost
	) {
		double newX = currentVx + (lookX * forwardBoost);
		double newZ = currentVz + (lookZ * forwardBoost);
		return new double[]{newX, jumpY, newZ};
	}
}
