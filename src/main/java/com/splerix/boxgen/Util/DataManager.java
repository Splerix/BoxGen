package com.splerix.boxgen.Util;

import com.splerix.boxgen.BoxGen;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.Plugin;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.logging.Level;

public class DataManager {
    //Data Manager stuff
    private Plugin plugin;
    private FileConfiguration dataConfig = null;
    private File configFile = null;

    public DataManager(Plugin plugin) {
        this.plugin = plugin;
        configFile = new File(this.plugin.getDataFolder(), "data.yml");
        //Initializes Config
        saveDefaultConfig();
    }

    public void reloadConfig() {
        if (configFile == null) configFile = new File(this.plugin.getDataFolder(), "data.yml");


        //Gets the stuff inside the file
        dataConfig = YamlConfiguration.loadConfiguration(this.configFile);

        InputStream defaultStream = this.plugin.getResource("data.yml");
        if (defaultStream != null) {
            YamlConfiguration defaultConfig = YamlConfiguration.loadConfiguration(new InputStreamReader(defaultStream));
            dataConfig.setDefaults(defaultConfig);
        }
    }

    public FileConfiguration getConfig() {
        //Makes it if it doesn't exist
        if (dataConfig == null) reloadConfig();
        return dataConfig;
    }

    public void saveConfig() {
        if (dataConfig == null || configFile == null) return;

        try {
            getConfig().save(configFile);
        } catch (IOException e) {
            plugin.getLogger().log(Level.SEVERE, "Couldn't save config to " + configFile,e);
        }
    }

    public void saveDefaultConfig() {
        if (configFile == null) configFile = new File(this.plugin.getDataFolder(), "data.yml");

        if (!configFile.exists()) plugin.saveResource("data.yml", false);
    }
}
