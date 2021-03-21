package me.nukeythenuke.lucid.mixin;

import me.nukeythenuke.lucid.Config;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.profiler.Profiler;
import net.minecraft.util.registry.RegistryKey;
import net.minecraft.world.MutableWorldProperties;
import net.minecraft.world.World;
import net.minecraft.world.dimension.DimensionType;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.time.Clock;
import java.time.Instant;
import java.util.function.BooleanSupplier;
import java.util.function.Supplier;

@Mixin(ServerWorld.class)
public abstract class ServerWorldMixin extends World {
    protected ServerWorldMixin(MutableWorldProperties properties, RegistryKey<World> registryRef, DimensionType dimensionType, Supplier<Profiler> profiler, boolean isClient, boolean debugWorld, long seed) {
        super(properties, registryRef, dimensionType, profiler, isClient, debugWorld, seed);
    }

    private static final Logger LUCID_LOGGER = LogManager.getLogger("lucid-lite");
    private static final int tickBudget = Config.getIntOrDefault("tickBudget", 10); // Max time in ms to spend ticking block entities per tick

    private long ticksToWarp = 0;
    private long ticksWarped = 0;
    private long ticksSpanned = 1;

    // When the world skips the night due to players sleeping store how many ticks are skipped
    @Inject(method = "tick", at = @At(value = "INVOKE", target = "Lnet/minecraft/server/world/ServerWorld;setTimeOfDay(J)V", shift = At.Shift.BEFORE))
    private void onSleep(BooleanSupplier shouldKeepTicking, CallbackInfo ci) {
        this.ticksToWarp = 24000L - this.getTimeOfDay();
    }

    // Tick block entities at an increased rate
    @Inject(method = "tick", at = @At(value = "INVOKE", target = "Lnet/minecraft/server/world/ServerWorld;tickBlockEntities()V"))
    private void applyExtraTicks(BooleanSupplier shouldKeepTicking, CallbackInfo ci) {
        if (this.ticksToWarp == 0) {
            return;
        }

        Instant start = Clock.systemUTC().instant();
        for (; ticksWarped < ticksToWarp; ++ticksWarped) {
            this.tickBlockEntities();

            // Limit how long we can spend ticking block entities - if tickBudget is set to 0 we don't budget our time
            if (tickBudget != 0 && Clock.systemUTC().instant().isAfter(start.plusMillis(tickBudget))) {
                ++ticksSpanned;
                break;
            }
        }

        // Reset variables and log info
        if (ticksWarped == ticksToWarp) {
            LUCID_LOGGER.info("Ticked block entities " + ticksWarped + " times over " + ticksSpanned + " ticks");
            ticksWarped = 0;
            ticksToWarp = 0;
            ticksSpanned = 1;
        }
    }
}
