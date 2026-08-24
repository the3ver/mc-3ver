package net.frank.mc3ver;

public class DoubleJumpLogic {

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
