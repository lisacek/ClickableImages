package me.lisacek.clickableimages.managers.impl;

import me.lisacek.clickableimages.ClickableImages;
import me.lisacek.clickableimages.managers.Manager;
import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.io.IOException;

public class ConfigurationsManager implements Manager {

    @Override
    public void onEnable() {
        loadConfig();
    }

    @Override
    public void onDisable() {
        loadConfig();
    }

    public void loadConfig() {
        ClickableImages.getInstance().getConsole().info("Loading config...");
        File customConfigFile = new File(ClickableImages.getInstance().getDataFolder(), "config.yml");
        if (!customConfigFile.exists()) {
            customConfigFile.getParentFile().mkdirs();
            ClickableImages.getInstance().saveResource("config.yml", false);
        }
        try {
            ClickableImages.getInstance().getConfig().load(customConfigFile);
        } catch (IOException | InvalidConfigurationException e) {
            e.printStackTrace();
        }
    }

}
