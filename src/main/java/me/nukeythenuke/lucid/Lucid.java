package me.nukeythenuke.lucid;

import it.unimi.dsi.fastutil.objects.*;
import net.fabricmc.api.ModInitializer;
import net.minecraft.server.world.ServerWorld;
import org.apache.logging.log4j.Logger;

import static org.apache.logging.log4j.LogManager.getLogger;

public class Lucid implements ModInitializer {
    public static final Logger LOGGER = getLogger();
    public static final int TICK_SPEED_MULTIPLIER = 10;
    public static ObjectArraySet<ServerWorld> shouldWarp = new ObjectArraySet<>();

    @Override
    public void onInitialize() {
        LOGGER.info("I have these lucid dreams...");
    }
}
