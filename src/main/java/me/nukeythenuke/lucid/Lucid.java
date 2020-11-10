package me.nukeythenuke.lucid;

import net.fabricmc.api.ModInitializer;
import org.apache.logging.log4j.Logger;

import static org.apache.logging.log4j.LogManager.getLogger;

public class Lucid implements ModInitializer {
    @Override
    public void onInitialize() {
        Logger logger = getLogger();
        logger.info("I have these lucid dreams...");
    }
}
