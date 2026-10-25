package net.frank.mc3ver.mixin;

import net.frank.mc3ver.peace.ModPeace;
import net.minecraft.world.entity.player.Player;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

// Vanilla prüft canBeSeenAsEnemy in LivingEntity.canAttack und TargetingConditions,
// Verfolger lassen über canContinueToUse ab.
@Mixin(Player.class)
public class PacifistPlayerMixin {

    @Inject(method = "canBeSeenAsEnemy", at = @At("HEAD"), cancellable = true)
    private void mc3ver$pacifistIsNoEnemy(CallbackInfoReturnable<Boolean> cir) {
        if (((Player) (Object) this).hasEffect(ModPeace.PACIFIST)) {
            cir.setReturnValue(false);
        }
    }
}
