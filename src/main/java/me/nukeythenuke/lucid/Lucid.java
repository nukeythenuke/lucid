package me.nukeythenuke.lucid;

import it.unimi.dsi.fastutil.objects.*;
import net.fabricmc.api.ModInitializer;
import net.minecraft.server.world.ServerWorld;
import org.apache.logging.log4j.Logger;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;

import static org.apache.logging.log4j.LogManager.getLogger;

public class Lucid implements ModInitializer {
    public static final String MOD_ID = "lucid";
    public static final Logger LOGGER = getLogger();
    private static final Config config = new Config(MOD_ID);

    public static ObjectArraySet<ServerWorld> shouldWarp = new ObjectArraySet<>();

    private static final String TICK_SPEED_MULTIPLIER_NAME = "tickSpeedMultiplier";
    private static final String IS_ENABLED_CHUNK_MANAGER_WARPING_NAME = "enableChunkManagerWarping";
    private static final String IS_ENABLED_RAID_MANAGER_WARPING_NAME = "enableRaidManagerWarping";
    private static final String IS_ENABLED_ENTITY_WARPING_NAME = "enableEntityWarping";
    private static final String IS_ENABLED_BLOCK_ENTITY_WARPING_NAME = "enableBlockEntityWarping";

    private static final int DEFAULT_TICK_SPEED_MULTIPLIER = 10;
    private static final boolean DEFAULT_IS_ENABLED_CHUNK_MANAGER_WARPING = false;
    private static final boolean DEFAULT_IS_ENABLED_RAID_MANAGER_WARPING = false;
    private static final boolean DEFAULT_IS_ENABLED_ENTITY_WARPING = false;
    private static final boolean DEFAULT_IS_ENABLED_BLOCK_ENTITY_WARPING = true;

    public static int tickSpeedMultiplier() {
        return Config.options.containsKey(TICK_SPEED_MULTIPLIER_NAME) ? Integer.parseInt(Config.options.get(TICK_SPEED_MULTIPLIER_NAME)) : DEFAULT_TICK_SPEED_MULTIPLIER;
    }

    public static boolean isEnabledChunkManagerWarping() {
        return Config.options.containsKey(IS_ENABLED_CHUNK_MANAGER_WARPING_NAME) ? Boolean.parseBoolean(Config.options.get(IS_ENABLED_CHUNK_MANAGER_WARPING_NAME)) : DEFAULT_IS_ENABLED_CHUNK_MANAGER_WARPING;
    }

    public static boolean isEnabledRaidManagerWarping() {
        return Config.options.containsKey(IS_ENABLED_RAID_MANAGER_WARPING_NAME) ? Boolean.parseBoolean(Config.options.get(IS_ENABLED_RAID_MANAGER_WARPING_NAME)) : DEFAULT_IS_ENABLED_RAID_MANAGER_WARPING;
    }

    public static boolean isEnabledEntityWarping() {
        return Config.options.containsKey(IS_ENABLED_ENTITY_WARPING_NAME) ? Boolean.parseBoolean(Config.options.get(IS_ENABLED_ENTITY_WARPING_NAME)) : DEFAULT_IS_ENABLED_ENTITY_WARPING;
    }

    public static boolean isEnabledBlockEntityWarping() {
        return Config.options.containsKey(IS_ENABLED_BLOCK_ENTITY_WARPING_NAME) ? Boolean.parseBoolean(Config.options.get(IS_ENABLED_BLOCK_ENTITY_WARPING_NAME)) : DEFAULT_IS_ENABLED_BLOCK_ENTITY_WARPING;
    }

    @Override
    public void onInitialize() {
        LOGGER.info("I have these lucid dreams...");

        try {
            config.load();
        } catch (IOException e) {
            LOGGER.info("Could not load config file, creating a new one with default settings");
            try {
                List<Config.ConfigEntry> configEntries = Arrays.asList(
                        new Config.ConfigEntry(
                                TICK_SPEED_MULTIPLIER_NAME,
                                String.valueOf(tickSpeedMultiplier()),
                                "How many times faster than normal the night should pass.\nDefault: " + DEFAULT_TICK_SPEED_MULTIPLIER),
                        new Config.ConfigEntry(
                                IS_ENABLED_CHUNK_MANAGER_WARPING_NAME,
                                String.valueOf(isEnabledChunkManagerWarping()),
                                "Mob spawning etc.\nDefault: " + DEFAULT_IS_ENABLED_CHUNK_MANAGER_WARPING),
                        new Config.ConfigEntry(
                                IS_ENABLED_RAID_MANAGER_WARPING_NAME,
                                String.valueOf(isEnabledRaidManagerWarping()),
                                "Raid timers etc.\nDefault: " + DEFAULT_IS_ENABLED_RAID_MANAGER_WARPING),
                        new Config.ConfigEntry(
                                IS_ENABLED_ENTITY_WARPING_NAME,
                                String.valueOf(isEnabledEntityWarping()),
                                "Mob movement etc.\nDefault: " + DEFAULT_IS_ENABLED_ENTITY_WARPING),
                        new Config.ConfigEntry(
                                IS_ENABLED_BLOCK_ENTITY_WARPING_NAME,
                                String.valueOf(isEnabledBlockEntityWarping()),
                                "Furnaces etc.\nDefault: " + DEFAULT_IS_ENABLED_BLOCK_ENTITY_WARPING));

                config.save(
                        "This file is generated automatically by lucid if it is not found.\n" +
                                "To reset to defaults just delete this file.",
                        configEntries);
            } catch (IOException ioException) {
                LOGGER.warn("Tried to create a new config file and failed!");
            }
        }
    }
}
