package net.danh.mmoshop.File;

import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.io.IOException;

import static net.danh.mmoshop.MMOShop.getInstance;
import static net.danh.mmoshop.MMOShop.loadShop;

public class Files {

    private static File configFile;
    private static File languageFile;
    private static FileConfiguration config;
    private static FileConfiguration language;

    public static void create() {
        configFile = new File(getInstance().getDataFolder(), "config.yml");
        languageFile = new File(getInstance().getDataFolder(), "language.yml");

        if (!configFile.exists()) getInstance().saveResource("config.yml", false);
        if (!languageFile.exists()) getInstance().saveResource("language.yml", false);
        language = new YamlConfiguration();
        config = new YamlConfiguration();

        try {
            language.load(languageFile);
            config.load(configFile);
        } catch (IOException | InvalidConfigurationException e) {
            e.printStackTrace();
        }
    }

    public static FileConfiguration getConfig() {
        return config;
    }

    public static FileConfiguration getLanguage() {
        return language;
    }

    public static void reload() {
        language = YamlConfiguration.loadConfiguration(languageFile);
        config = YamlConfiguration.loadConfiguration(configFile);
        loadShop();
    }

    public static void save() {
        saveConfig();
        saveLanguage();
    }

    private static void saveConfig() {
        try {
            config.save(configFile);
        } catch (IOException ignored) {
        }
    }

    private static void saveLanguage() {
        try {
            language.save(languageFile);
        } catch (IOException ignored) {
        }
    }

}
