package me.nukeythenuke.lucid;

import net.fabricmc.loader.api.FabricLoader;

import java.io.IOException;
import java.nio.file.Files;
import java.util.HashMap;
import java.util.Map;

public class Config {
    static Map<String, String> options = new HashMap<>();

    static {
        try {
            Files.readAllLines(FabricLoader.getInstance().getConfigDir().resolve("lucid-lite.config")).forEach(line -> {
                String[] splitLine = line.split(":", 2);
                if (splitLine.length == 2) {
                    options.put(splitLine[0].trim(), splitLine[1].trim());
                }
            });
        } catch (IOException ignored){}
    }

    public static int getIntOrDefault(String key, Integer def) {
        int num = def;
        try {
            num = Integer.parseInt(options.getOrDefault(key, "none"));
        } catch (NumberFormatException ignored) {}
        return num;
    }
}
