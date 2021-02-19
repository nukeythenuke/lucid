package me.nukeythenuke.lucid.mixin;

import me.nukeythenuke.lucid.Lucid;
import net.minecraft.server.MinecraftServer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.function.BooleanSupplier;

@Mixin(MinecraftServer.class)
public abstract class MinecraftServerMixin {
    // Warp worlds
    @Inject(method = "tick", at = @At(value = "INVOKE", target = "Lnet/minecraft/server/MinecraftServer;tickWorlds(Ljava/util/function/BooleanSupplier;)V", shift = At.Shift.AFTER))
    private void tickWarp(BooleanSupplier shouldKeepTicking, CallbackInfo ci) {
        Lucid.shouldWarp.forEach( world -> {
            // If tickSpeedMultiplier is 0 then skip the night in 1 tick
            if (Lucid.tickSpeedMultiplier() == 0) {
                for (long t = world.getTimeOfDay() % 24000L; t != 24000; ++t) {
                    world.tick(shouldKeepTicking);
                }
            } else {
                long t = world.getTimeOfDay() % 24000L;
                for(int i = 0; i < Lucid.tickSpeedMultiplier() && t != 24000; ++i) {
                    world.tick(shouldKeepTicking);
                    ++t;
                }
            }
        });

        Lucid.shouldWarp.clear();
    }
}
