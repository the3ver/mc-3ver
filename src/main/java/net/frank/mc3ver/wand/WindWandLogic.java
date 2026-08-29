package net.frank.mc3ver.wand;

public class WindWandLogic {

    public static final int COOLDOWN_TICKS = 20;
    public static final int MAX_DURABILITY = 256;
    public static final float PROJECTILE_SPEED = 1.5f;
    public static final float DEFAULT_INACCURACY = 0.05f;

    public static double[] calculateVelocity(
        double lookX,
        double lookY,
        double lookZ,
        double spreadX,
        double spreadY,
        double spreadZ,
        float speed
    ) {
        double lengthSq = lookX * lookX + lookY * lookY + lookZ * lookZ;
        double normX = 0;
        double normY = 0;
        double normZ = 0;

        if (lengthSq > 1.0E-7D) {
            double length = Math.sqrt(lengthSq);
            normX = lookX / length;
            normY = lookY / length;
            normZ = lookZ / length;
        }

        double vx = (normX + spreadX) * speed;
        double vy = (normY + spreadY) * speed;
        double vz = (normZ + spreadZ) * speed;

        return new double[]{vx, vy, vz};
    }

    public static int calculateNewDamage(int currentDamage, int amount) {
        return currentDamage + amount;
    }

    public static int calculateRemainingDurability(int currentDamage, int maxDurability) {
        return Math.max(0, maxDurability - currentDamage);
    }

    public static boolean shouldBreak(int currentDamage, int maxDurability) {
        return currentDamage >= maxDurability;
    }

    public static boolean canUse(boolean hasCooldown, boolean isSpectator) {
        return !hasCooldown && !isSpectator;
    }
}
