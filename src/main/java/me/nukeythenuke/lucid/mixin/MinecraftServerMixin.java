package me.nukeythenuke.lucid.mixin;

import net.minecraft.entity.LivingEntity;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.PlayerManager;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
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

    // Tick all worlds 9 more times if they have at least 1 player and all players are sleeping
    @Inject(method = "tick", at = @At(value = "INVOKE", target = "Lnet/minecraft/server/MinecraftServer;tickWorlds(Ljava/util/function/BooleanSupplier;)V", shift = At.Shift.AFTER))
    private void tickWarp(BooleanSupplier shouldKeepTicking, CallbackInfo ci) {
        if (!playerManager.getPlayerList().isEmpty()) { // Don't do anything if there are no players on the server
            getWorlds().forEach((ServerWorld world) -> {
                List<ServerPlayerEntity> players = world.getPlayers();
                if (!players.isEmpty() && players.stream().allMatch(LivingEntity::isSleeping)) {
                    for (int i = 0; i < 9; ++i) {
                        world.tick(shouldKeepTicking);
                    }
                }
            });
        }
    }
}
