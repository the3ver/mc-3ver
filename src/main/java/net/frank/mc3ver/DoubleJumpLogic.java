package net.frank.mc3ver;

public class DoubleJumpLogic {

	public static class DoubleJumpState {
		private boolean primed = false;
		private boolean canDoubleJump = true;

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
			// Wenn der Spieler den Boden berührt, schwimmt, klettert oder reitet, wird der Doppelsprung aufgeladen/zurückgesetzt
			if (onGround || inWater || inLava || isPassenger || onClimbable) {
				primed = false;
				canDoubleJump = true;
				return false;
			}

			// In besonderen Bewegungsmodi kein Doppelsprung
			if (isFlying || isFallFlying || isSpectator) {
				primed = false;
				canDoubleJump = false;
				return false;
			}

			// Wenn der Doppelsprung verfügbar ist und der Spieler in der Luft die Sprungtaste loslässt: scharfschalten
			if (canDoubleJump && !isJumpKeyDown) {
				primed = true;
			}

			// Wenn scharfgeschaltet und die Sprungtaste in der Luft neu gedrückt wird: Auslösen und verbrauchen!
			if (canDoubleJump && primed && isJumpKeyDown && !jumpWasDown) {
				primed = false;
				canDoubleJump = false;
				return true;
			}

			return false;
		}

		public boolean isPrimed() {
			return primed;
		}

		public boolean canDoubleJump() {
			return canDoubleJump;
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
