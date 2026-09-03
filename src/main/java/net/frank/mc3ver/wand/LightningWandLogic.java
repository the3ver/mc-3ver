package net.frank.mc3ver.wand;

public class LightningWandLogic {

    public static final int COOLDOWN_TICKS = 30;
    public static final int MAX_DURABILITY = 256;
    public static final int MAX_CHARGE_TICKS = 100; // 5 Sekunden
    public static final float MAX_DAMAGE_MULTIPLIER = 3.0f; // Bis zu 3x Schaden
    public static final int MIN_DURABILITY_COST = 1;
    public static final int MAX_DURABILITY_COST = 5;
    public static final float THUNDER_HIT_DAMAGE = 5.0f; // Vanilla Lightning Damage
    public static final double RANGE = 20.0;
    public static final double CHAIN_RADIUS = 8.0;
    public static final int MAX_CHAINS = 4;
    public static final float BASE_DAMAGE = 8.0f;
    public static final float DAMAGE_DECAY = 0.20f;

    public static boolean canUse(boolean hasCooldown, boolean isSpectator) {
        return !hasCooldown && !isSpectator;
    }

    public static float calculateDamage(float baseDamage, float decay, int chainIndex) {
        if (chainIndex <= 0) {
            return baseDamage;
        }
        double factor = Math.pow(1.0 - decay, chainIndex);
        float damage = (float) (baseDamage * factor);
        return Math.max(1.0f, damage);
    }

    public static boolean isWithinRange(
        double x1, double y1, double z1,
        double x2, double y2, double z2,
        double maxRange
    ) {
        double dx = x2 - x1;
        double dy = y2 - y1;
        double dz = z2 - z1;
        return (dx * dx + dy * dy + dz * dz) <= (maxRange * maxRange);
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

    public static boolean isRayAimingAtTarget(
        double eyeX, double eyeY, double eyeZ,
        double lookX, double lookY, double lookZ,
        double targetX, double targetY, double targetZ,
        double maxRange,
        double tolerance
    ) {
        double vx = targetX - eyeX;
        double vy = targetY - eyeY;
        double vz = targetZ - eyeZ;

        double lookLen = Math.sqrt(lookX * lookX + lookY * lookY + lookZ * lookZ);
        if (lookLen < 1.0E-6) {
            return false;
        }

        double lx = lookX / lookLen;
        double ly = lookY / lookLen;
        double lz = lookZ / lookLen;

        double t = vx * lx + vy * ly + vz * lz;
        if (t <= 0.0 || t > maxRange) {
            return false;
        }

        double px = eyeX + lx * t;
        double py = eyeY + ly * t;
        double pz = eyeZ + lz * t;

        double dx = targetX - px;
        double dy = targetY - py;
        double dz = targetZ - pz;

        return (dx * dx + dy * dy + dz * dz) <= (tolerance * tolerance);
    }

    public static double calculateAimDistance(
        double eyeX, double eyeY, double eyeZ,
        double lookX, double lookY, double lookZ,
        double targetX, double targetY, double targetZ,
        double maxRange,
        double tolerance
    ) {
        if (!isRayAimingAtTarget(eyeX, eyeY, eyeZ, lookX, lookY, lookZ, targetX, targetY, targetZ, maxRange, tolerance)) {
            return -1.0;
        }
        double vx = targetX - eyeX;
        double vy = targetY - eyeY;
        double vz = targetZ - eyeZ;

        double lookLen = Math.sqrt(lookX * lookX + lookY * lookY + lookZ * lookZ);
        double lx = lookX / lookLen;
        double ly = lookY / lookLen;
        double lz = lookZ / lookLen;

        return vx * lx + vy * ly + vz * lz;
    }

    public static boolean shouldIgniteBlock(boolean isFlammable, boolean canPlaceFire) {
        return isFlammable && canPlaceFire;
    }

    public static float calculateChargeRatio(int chargeTicks, int maxChargeTicks) {
        if (maxChargeTicks <= 0 || chargeTicks <= 0) {
            return 0.0f;
        }
        return Math.min(1.0f, (float) chargeTicks / (float) maxChargeTicks);
    }

    public static float calculateChargedDamage(float baseDamage, float maxMultiplier, int chargeTicks, int maxChargeTicks) {
        float ratio = calculateChargeRatio(chargeTicks, maxChargeTicks);
        float multiplier = 1.0f + ratio * (maxMultiplier - 1.0f);
        return baseDamage * multiplier;
    }

    public static int calculateDurabilityCost(int minCost, int maxCost, int chargeTicks, int maxChargeTicks) {
        float ratio = calculateChargeRatio(chargeTicks, maxChargeTicks);
        return Math.round(minCost + ratio * (maxCost - minCost));
    }

    public static float calculateExtraDamage(float wandDamage, float thunderHitDamage) {
        return Math.max(0.0f, wandDamage - thunderHitDamage);
    }
}
