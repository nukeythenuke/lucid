package me.nukeythenuke.lucid;

import net.fabricmc.loader.api.FabricLoader;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;

public class Config {
    static class ConfigEntry {
        private final String key;
        private final String value;
        private final String description;

        ConfigEntry(String key, String value, String description) {
            this.key = key;
            this.value = value;
            this.description = description;
        }
    }

    public static final HashMap<String, String> options = new HashMap<>();

    private final Path configPath;

    Config(String filename) {
        configPath = FabricLoader.getInstance().getConfigDir().resolve(filename + ".config");
    }

    public void save(String header, List<ConfigEntry> options) throws IOException {
        List<String> lines = new java.util.ArrayList<>(Collections.emptyList());
        // Split header into lines and add '#' at the start of each
        for (String line : header.split("\n")) {
            lines.add("# " + line);
        }
        lines.add("");

        for (ConfigEntry option : options) {
            if (!option.description.equals("")) {
                // Split description into lines adding '#' at the start of each
                for (String line : option.description.split("\n")) {
                    lines.add("# " + line);
                }
            }
            lines.add(option.key + ": " + option.value);
            lines.add("");
        }

        Files.write(configPath, lines);
    }

    public void load() throws IOException {
        // Read config file into a list of lines
        List<String> optionStrings = Files.readAllLines(configPath);
        for (String option : optionStrings) {
            // Split line into name and value
            String[] optionParts = option.split(":", 2);

            String name = optionParts[0].trim();
            // Check for a value
            if (optionParts.length != 2) {
                // Don't complain about whitespace or comments
                if (!name.isEmpty() && !(name.charAt(0) == '#')) {
                    Lucid.LOGGER.warn("Invalid option: " + option);
                }

                continue;
            }

            String value = optionParts[1];
            // Trim line comment from value
            value = value.split("#")[0];
            value = value.trim();

            options.put(name, value);
        }
    }
}
