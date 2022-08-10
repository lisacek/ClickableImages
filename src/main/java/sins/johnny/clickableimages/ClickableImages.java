package sins.johnny.clickableimages;

import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;
import sins.johnny.clickableimages.commands.PlaceCommand;
import sins.johnny.clickableimages.managers.Managers;
import sins.johnny.clickableimages.managers.impl.AssetsManager;
import sins.johnny.clickableimages.managers.impl.ClickableImagesManager;
import sins.johnny.clickableimages.managers.impl.ConfigurationsManager;
import sins.johnny.clickableimages.managers.impl.PlacingManager;

public final class ClickableImages extends JavaPlugin {

    private static ClickableImages instance;

    @Override
    public void onEnable() {
        instance = this;

        Managers.register(ConfigurationsManager.class);
        Managers.register(AssetsManager.class);
        Managers.register(ClickableImagesManager.class);
        Managers.register(PlacingManager.class);

        getCommand("place").setExecutor(new PlaceCommand());
    }

    @Override
    public void onDisable() {
        Managers.stop();

        instance = null;
    }

    public static ClickableImages getInstance() {
        return instance;
    }
}
