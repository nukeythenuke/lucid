package me.nukeythenuke.lucid.mixin;

import net.minecraft.entity.player.PlayerEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(PlayerEntity.class)
public class PlayerEntityMixin {
    // Prevent vanilla sleeping
    @Inject(method = "isSleepingLongEnough", at = @At("RETURN"))
    public void neverLongEnough(CallbackInfoReturnable<Boolean> cir) {
        cir.setReturnValue(false);
    }
}
