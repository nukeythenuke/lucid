package me.nukeythenuke.lucid.mixin;

import me.nukeythenuke.lucid.Config;
import me.nukeythenuke.lucid.Lucid;
import net.minecraft.server.MinecraftServer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import java.util.function.BooleanSupplier;
import java.util.stream.IntStream;

@Mixin(MinecraftServer.class)
public abstract class MinecraftServerMixin {
    // Warp worlds
    @Inject(method = "tick", at = @At(value = "INVOKE", target = "Lnet/minecraft/server/MinecraftServer;tickWorlds(Ljava/util/function/BooleanSupplier;)V", shift = At.Shift.AFTER))
    private void tickWarp(BooleanSupplier shouldKeepTicking, CallbackInfo ci) {
        Lucid.shouldWarp.forEach( world -> {
            for(int i = 0; i < Lucid.tickSpeedMultiplier(); i++) {
                world.tick(shouldKeepTicking);
            }
        });

        Lucid.shouldWarp.clear();
    }
}
