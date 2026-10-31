package net.frank.mc3ver.peace;

public final class PacifistLogic {

    public static final int TOGGLE_COOLDOWN_TICKS = 20;
    public static final int BROKEN_PEACE_LOCK_TICKS = 600;
    public static final int EFFECT_COLOR = 0xF2F2F2;

    public enum ToggleResult {
        ACTIVATED,
        DEACTIVATED,
        LOCKED
    }

    private PacifistLogic() {}

    public static ToggleResult toggle(boolean isPacifist, boolean flagOnCooldown) {
        if (flagOnCooldown) return ToggleResult.LOCKED;
        return isPacifist ? ToggleResult.DEACTIVATED : ToggleResult.ACTIVATED;
    }

    public static boolean shouldBlockDamage(boolean victimIsPacifist, boolean attackerIsEnemy) {
        return victimIsPacifist && attackerIsEnemy;
    }

    public static boolean breaksPeace(boolean attackerIsPacifistPlayer, boolean victimIsEnemy) {
        return attackerIsPacifistPlayer && victimIsEnemy;
    }

    public static boolean canBeTargetedByMonsters(boolean isPacifist) {
        return !isPacifist;
    }
}
