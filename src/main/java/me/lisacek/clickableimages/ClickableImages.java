package me.lisacek.clickableimages;

import me.lisacek.clickableimages.commands.ClickableImagesCommand;
import me.lisacek.clickableimages.cons.ConsoleOutput;
import me.lisacek.clickableimages.managers.impl.*;
import me.lisacek.clickableimages.utils.CPUDaemon;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.java.JavaPlugin;
import me.lisacek.clickableimages.managers.Managers;
import org.jetbrains.annotations.NotNull;

import java.util.Objects;

public final class ClickableImages extends JavaPlugin {

    private static ClickableImages instance;
    private final ConsoleOutput console = new ConsoleOutput("&#03fc7bClickableImages &8| &7");

    private final YamlConfiguration config = new YamlConfiguration();

    @Override
    public void onEnable() {
        instance = this;
        CPUDaemon.get();
        registerManagers();
        Objects.requireNonNull(getCommand("clickableimages")).setExecutor(new ClickableImagesCommand());
        console.info("Plugin was enabled! Made with &#fc0303<3 &7by &#fc0390Lisacek&7.");
    }

    @Override
    public void onDisable() {
        Managers.stop();
        instance = null;
        console.info("Plugin was disabled!");
    }

    public static ClickableImages getInstance() {
        return instance;
    }

    public void registerManagers() {
        Managers.register(ConfigurationsManager.class);
        Managers.register(AssetsManager.class);
        Managers.register(ClickableImagesManager.class);
        Managers.register(PlacingManager.class);
        Managers.register(DeleteManager.class);
        Managers.register(ButtonManager.class);
    }

    @NotNull
    @Override
    public YamlConfiguration getConfig() {
        return config;
    }

    public ConsoleOutput getConsole() {
        return console;
    }
}
