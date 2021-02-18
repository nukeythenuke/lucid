package me.nukeythenuke.lucid.mixin;

import me.nukeythenuke.lucid.Lucid;
import net.minecraft.entity.Entity;
import net.minecraft.server.world.ServerChunkManager;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.profiler.Profiler;
import net.minecraft.util.registry.RegistryKey;
import net.minecraft.village.raid.RaidManager;
import net.minecraft.world.MutableWorldProperties;
import net.minecraft.world.World;
import net.minecraft.world.dimension.DimensionType;
import org.objectweb.asm.Opcodes;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.function.BooleanSupplier;
import java.util.function.Consumer;
import java.util.function.Supplier;

@Mixin(ServerWorld.class)
public abstract class ServerWorldMixin extends World {

    protected ServerWorldMixin(MutableWorldProperties properties, RegistryKey<World> registryRef, DimensionType dimensionType, Supplier<Profiler> profiler, boolean isClient, boolean debugWorld, long seed) {
        super(properties, registryRef, dimensionType, profiler, isClient, debugWorld, seed);
    }

    @Shadow private boolean allPlayersSleeping;

    // Determine whether or not we are in a Lucid induced world tick
    private boolean inWarpTick;
    @Inject(method = "tick", at = @At(value = "HEAD"))
    private void setInWarpTick(CallbackInfo ci) {
        inWarpTick = Lucid.shouldWarp.contains(this);
    }

    // Set allPlayersSleeping back to true as we are not waking players
    @Inject(method = "tick", at = @At(value = "FIELD", target = "Lnet/minecraft/server/world/ServerWorld;allPlayersSleeping:Z", opcode = Opcodes.PUTFIELD, shift = At.Shift.AFTER))
    private void allPlayersStillSleeping(BooleanSupplier shouldKeepTicking, CallbackInfo ci) {
        this.allPlayersSleeping = true;
    }

    // Do not skip night, instead add the world to Lucid.shouldWarp
    @Redirect(method = "tick", at = @At(value = "INVOKE", target = "Lnet/minecraft/server/world/ServerWorld;setTimeOfDay(J)V"))
    private void warpNotSkip(ServerWorld serverWorld, long timeOfDay) {
        Lucid.shouldWarp.add(serverWorld);
    }

    // Do not wake players
    @Redirect(method = "tick", at = @At(value = "INVOKE", target = "Lnet/minecraft/server/world/ServerWorld;wakeSleepingPlayers()V"))
    private void preventWakeSleepingPlayers(ServerWorld serverWorld) { }

    // Warp chunk manager if in a vanilla induced tick or chunk manager warping is enabled
    @Redirect(method = "tick", at = @At(value = "INVOKE", target = "Lnet/minecraft/server/world/ServerChunkManager;tick(Ljava/util/function/BooleanSupplier;)V"))
    private void warpChunks(ServerChunkManager serverChunkManager, BooleanSupplier shouldKeepTicking) {
        if (!inWarpTick || Lucid.isEnabledChunkManagerWarping()) {
            serverChunkManager.tick(shouldKeepTicking);
        }
    }

    @Redirect(method = "tick", at = @At(value = "INVOKE", target = "Lnet/minecraft/village/raid/RaidManager;tick()V"))
    private void warpRaidManager(RaidManager raidManager) {
        if (!inWarpTick || Lucid.isEnabledRaidManagerWarping()) {
            raidManager.tick();
        }
    }

    // Warp block entities if in a vanilla induced world tick or block entity warping is enabled
    @Redirect(method = "tick", at = @At(value = "INVOKE", target = "Lnet/minecraft/server/world/ServerWorld;tickBlockEntities()V"))
    private void warpBlockEntities(ServerWorld serverWorld) {
        if (!inWarpTick || Lucid.isEnabledBlockEntityWarping()) {
            tickBlockEntities();
        }
    }

    // Warp entities if in a vanilla induced world tick or entity warping is enabled
    @Redirect(method = "tick", at = @At(value = "INVOKE", target = "Lnet/minecraft/server/world/ServerWorld;tickEntity(Ljava/util/function/Consumer;Lnet/minecraft/entity/Entity;)V"))
    private void warpEntities(ServerWorld serverWorld, Consumer<Entity> consumer, Entity entity) {
        if (!inWarpTick || Lucid.isEnabledEntityWarping()) {
            tickEntity(consumer, entity);
        }
    }
}
