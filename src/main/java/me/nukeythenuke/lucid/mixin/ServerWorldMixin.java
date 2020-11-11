package me.nukeythenuke.lucid.mixin;

import me.nukeythenuke.lucid.Lucid;
import net.minecraft.server.world.ServerWorld;
import org.objectweb.asm.Opcodes;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import java.util.function.BooleanSupplier;

@Mixin(ServerWorld.class)
public abstract class ServerWorldMixin {
    @Shadow private boolean allPlayersSleeping;

    // Set allPlayersSleeping back to true as we are not waking players
    @Inject(method = "tick", at = @At(value = "FIELD", target = "Lnet/minecraft/server/world/ServerWorld;allPlayersSleeping:Z", opcode = Opcodes.PUTFIELD, shift = At.Shift.AFTER))
    private void allPlayersStillSleeping(BooleanSupplier shouldKeepTicking, CallbackInfo ci) { this.allPlayersSleeping = true; }

    // Do not skip night, instead add world to Lucid.shouldWarp
    @Redirect(method = "tick", at = @At(value = "INVOKE", target = "Lnet/minecraft/server/world/ServerWorld;setTimeOfDay(J)V"))
    private void warpNotSkip(ServerWorld serverWorld, long timeOfDay) { Lucid.shouldWarp.add(serverWorld); }

    // Do not wake players
    @Redirect(method = "tick", at = @At(value = "INVOKE", target = "Lnet/minecraft/server/world/ServerWorld;wakeSleepingPlayers()V"))
    private void preventWakeSleepingPlayers(ServerWorld serverWorld) { }
}
