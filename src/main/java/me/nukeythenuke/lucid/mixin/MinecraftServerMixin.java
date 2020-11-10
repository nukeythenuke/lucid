package me.nukeythenuke.lucid.mixin;

import net.minecraft.entity.LivingEntity;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.PlayerManager;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.world.GameRules;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.List;
import java.util.function.BooleanSupplier;

@Mixin(MinecraftServer.class)
public abstract class MinecraftServerMixin {
    @Shadow private PlayerManager playerManager;
    @Shadow public abstract Iterable<ServerWorld> getWorlds();

    @Shadow public abstract GameRules getGameRules();

    // Tick all worlds 9 more times if they have at least 1 player and all players are sleeping
    @Inject(method = "tick", at = @At(value = "INVOKE", target = "Lnet/minecraft/server/MinecraftServer;tickWorlds(Ljava/util/function/BooleanSupplier;)V", shift = At.Shift.AFTER)) // After vanilla ticks worlds
    private void tickWarp(BooleanSupplier shouldKeepTicking, CallbackInfo ci) {
        if (!playerManager.getPlayerList().isEmpty() && getGameRules().getBoolean(GameRules.DO_DAYLIGHT_CYCLE)) { // Don't do anything if there are no players on the server or doDaylightCycle is false
            getWorlds().forEach((ServerWorld world) -> {
                List<ServerPlayerEntity> players = world.getPlayers();
                if (!players.isEmpty() && // World has players
                        players.stream().anyMatch(LivingEntity::isSleeping) && // At least 1 player is sleeping
                        players.stream().allMatch((ServerPlayerEntity player) -> player.isSleeping() || player.isSpectator()) // Every player is either sleeping or a spectator
                ) {
                    for (int i = 0; i < 9; ++i) {
                        world.tick(shouldKeepTicking);
                    }
                }
            });
        }
    }
}
