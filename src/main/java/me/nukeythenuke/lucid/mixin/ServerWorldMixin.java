package me.nukeythenuke.lucid.mixin;

import net.fabricmc.loader.api.FabricLoader;
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

import java.io.IOException;
import java.nio.file.Files;
import java.time.Clock;
import java.time.Duration;
import java.time.Instant;
import java.util.function.BooleanSupplier;
import java.util.function.Supplier;

@Mixin(ServerWorld.class)
public abstract class ServerWorldMixin extends World {
    protected ServerWorldMixin(MutableWorldProperties properties, RegistryKey<World> registryRef, DimensionType dimensionType, Supplier<Profiler> profiler, boolean isClient, boolean debugWorld, long seed) {
        super(properties, registryRef, dimensionType, profiler, isClient, debugWorld, seed);
    }

    private static final Logger LUCID_LOGGER = LogManager.getLogger("lucid-lite");
    private static int tickBudget = 10; // Max time in ms to spend ticking block entities per tick
    private long ticksToWarp = 0;

    // Read config if it exists
    static {
        try {
            Files.readAllLines(FabricLoader.getInstance().getConfigDir().resolve("lucid-lite.config")).forEach(line -> {
                String[] splitLine = line.split(":", 2);
                if (splitLine.length == 2 && splitLine[0].trim().equals("tick_budget")) {
                    try {
                        tickBudget = Integer.parseInt(splitLine[1].trim());
                    } catch (NumberFormatException ignored) {
                        LUCID_LOGGER.warn("Invalid value for option \"tick_budget\" in \"lucid-lite.config\", using default.");
                    }
                }
            });
        } catch (IOException ignored){};
    }

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

        int ticksWarped = 0;
        Instant start = Clock.systemUTC().instant();
        for (; ticksWarped < ticksToWarp; ++ticksWarped) {
            this.tickBlockEntities();

            // Limit how long we can spend ticking block entities
            if (Clock.systemUTC().instant().isAfter(start.plusMillis(tickBudget))) {
                break;
            }
        }

        ticksToWarp -= ticksWarped;
        LUCID_LOGGER.info("Ticked block entities " + ticksWarped + " times in " + Duration.between(start, Clock.systemUTC().instant()).toMillis() + " ms");
    }
}
