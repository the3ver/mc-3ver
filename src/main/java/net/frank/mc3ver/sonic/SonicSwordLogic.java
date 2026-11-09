package net.frank.mc3ver.sonic;

// Werte des Schallschwerts. Schallwelle per Rechtsklick (aufladen, loslassen), Schallstrahl per Linksklick
// (gedrückt halten, loslassen). Beide feuern erst ab MIN_CHARGE_TICKS, darunter ist es ein normaler Klick.
public final class SonicSwordLogic {

    // Spieler schlagen ohne Waffe mit 1 Schaden zu, das Material legt seinen Bonus drauf
    public static final float PLAYER_BASE_DAMAGE = 1.0f;
    public static final float TOTAL_ATTACK_DAMAGE = 8.5f;
    public static final float ATTACK_SPEED = -2.4f;

    public static final int MIN_CHARGE_TICKS = 10;
    public static final int WAVE_FULL_CHARGE_TICKS = 40;
    public static final int BEAM_FULL_CHARGE_TICKS = 30;
    public static final int WAVE_COOLDOWN_TICKS = 40;
    public static final int BEAM_COOLDOWN_TICKS = 30;
    public static final int DURABILITY_COST = 1;

    // Langsamkeit 10 hält Mobs fest, Schwäche nimmt ihren Hieben die Kraft
    public static final int PARALYSIS_AMPLIFIER = 9;

    public static final double BEAM_WIDTH = 1.0;
    public static final double BEAM_LIFT = 0.35;

    public record Wave(double radius, float damage, int paralysisTicks) {}

    public record Beam(double range, float damage, double knockback) {}

    private SonicSwordLogic() {}

    public static float attackDamageBonus(float materialBonus) {
        return TOTAL_ATTACK_DAMAGE - PLAYER_BASE_DAMAGE - materialBonus;
    }

    public static boolean canCharge(boolean onCooldown, boolean spectator) {
        return !onCooldown && !spectator;
    }

    public static boolean fires(long chargeTicks) {
        return chargeTicks >= MIN_CHARGE_TICKS;
    }

    // 0 unterhalb der Mindestzeit, sonst Anteil an der Vollladung bis höchstens 1
    public static float charge(long chargeTicks, int fullChargeTicks) {
        if (!fires(chargeTicks)) return 0.0f;
        return Math.min(1.0f, (float) chargeTicks / fullChargeTicks);
    }

    public static long chargeTicks(long startTick, long nowTick) {
        return Math.max(0, nowTick - startTick);
    }

    public static Wave wave(float charge) {
        return new Wave(3.0 + 3.0 * charge, 1.5f + 2.5f * charge, 20 + Math.round(40 * charge));
    }

    public static Beam beam(float charge) {
        return new Beam(8.0 + 8.0 * charge, 1.0f + charge, 0.8 + 1.7 * charge);
    }

    // Abstand entlang des Strahls, wenn das Ziel ihn berührt, sonst -1. Richtung muss normiert sein.
    public static double beamHitDistance(
            double dirX, double dirY, double dirZ, double toX, double toY, double toZ, double range, double width) {
        double along = toX * dirX + toY * dirY + toZ * dirZ;
        if (along < 0 || along > range) return -1;
        double offX = toX - dirX * along;
        double offY = toY - dirY * along;
        double offZ = toZ - dirZ * along;
        double offSq = offX * offX + offY * offY + offZ * offZ;
        return offSq <= width * width ? along : -1;
    }

    // Rückstoß wird von Rüstung mit Rückstoßresistenz gedämpft (Netherit, Wächter)
    public static double knockbackAfterResistance(double knockback, double resistance) {
        return knockback * Math.max(0.0, 1.0 - resistance);
    }
}
