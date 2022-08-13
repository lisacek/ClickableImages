package sins.johnny.clickableimages.managers.impl;

import com.google.common.collect.Lists;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Entity;
import org.bukkit.entity.ItemFrame;
import org.bukkit.inventory.meta.MapMeta;
import org.bukkit.map.MapView;
import sins.johnny.clickableimages.cons.ClickableImage;
import sins.johnny.clickableimages.cons.Pair;
import sins.johnny.clickableimages.cons.Renderer;
import sins.johnny.clickableimages.listeners.ClickListener;
import sins.johnny.clickableimages.managers.Manager;
import sins.johnny.clickableimages.managers.Managers;
import sins.johnny.clickableimages.utils.ItemUtils;

import java.awt.image.BufferedImage;
import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
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
        initMaps();
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
            List<List<Location>> locations = Lists.newArrayList();
            int rows = config.getConfigurationSection("locations").getKeys(false).size();
            for (int i = 0; i < rows; i++) {
                locations.add((List<Location>) config.getList("locations." + i));
            }
            images.add(new ClickableImage(image, actions, locations));
            System.out.println("Loaded image: " + image);
        }
    }

    public void initMaps() {
        for (World world : Bukkit.getWorlds()) {
            for (ItemFrame frame : world.getEntitiesByClass(ItemFrame.class)) {
                initMap(frame);
            }
        }
    }

    public void initMap(ItemFrame item) {
        ClickableImage image = getImage(item.getLocation());
        if (image == null) return;

        Pair<Integer, Integer> axis = findGrid(image, item.getLocation());
        if (axis == null) return;

        BufferedImage bfIm = image.getAsset().getImage(axis.getFirst(), axis.getSecond());
        MapView map = Bukkit.createMap(item.getWorld());

        map.getRenderers().clear();

        Renderer renderer = Renderer.installRenderer(map);
        renderer.setImage(bfIm);
        map.addRenderer(renderer);
        item.setItem(ItemUtils.createMapItem(map.getId()));
    }

    public Pair<Integer, Integer> findGrid(ClickableImage image, Location location) {
        List<List<Location>> grid = image.getGrid();
        for (int i = 0; i < grid.size(); i++) {
            for (int j = 0; j < grid.get(i).size(); j++) {
                if (grid.get(i).get(j).equals(location)) {
                    return new Pair<>(i, j);
                }
            }
        }
        return null;
    }

    public List<ClickableImage> getImages() {
        return images;
    }

    public ClickableImage getImage(Location location) {
        return images.stream().filter(image -> image.getGrid().stream().anyMatch(row -> row.stream().anyMatch(loc -> loc.equals(location)))).findFirst().orElse(null);
    }

}
