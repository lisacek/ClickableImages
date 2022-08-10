package sins.johnny.clickableimages.managers.impl;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.configuration.file.YamlConfiguration;
import sins.johnny.clickableimages.cons.ClickableImage;
import sins.johnny.clickableimages.listeners.ClickListener;
import sins.johnny.clickableimages.listeners.RenderListener;
import sins.johnny.clickableimages.managers.Manager;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class ClickableImagesManager implements Manager {

    private File folder;
    private final List<ClickableImage> images = new ArrayList<>();

    @Override
    public void onEnable() {
        folder = new File(getPlugin().getDataFolder(), "images");
        folder.mkdirs();

        loadImages();

        Bukkit.getPluginManager().registerEvents(new ClickListener(), getPlugin());
        Bukkit.getPluginManager().registerEvents(new RenderListener(), getPlugin());
    }

    @Override
    public void onDisable() {
        images.clear();
    }

    public void loadImages() {
        images.clear();

        for (File file : Objects.requireNonNull(folder.listFiles())) {
            if (!file.getName().endsWith(".yml")) continue;

            YamlConfiguration config = YamlConfiguration.loadConfiguration(file);

            String image = config.getString("image");
            List<String> actions = config.getStringList("actions");
            List<Location> locations = (List<Location>) config.getList("locations");

            images.add(new ClickableImage(image, actions, locations));
        }
    }

    public List<ClickableImage> getImages() {
        return images;
    }

    public ClickableImage getImage(Location location) {
        return images.stream().filter(image -> image.getLocations().contains(location)).findFirst().orElse(null);
    }

}
