package me.lisacek.clickableimages.managers.impl;

import com.google.common.collect.Lists;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import me.lisacek.clickableimages.ClickableImages;
import me.lisacek.clickableimages.cons.ClickableImage;
import me.lisacek.clickableimages.cons.Pair;
import me.lisacek.clickableimages.cons.Renderer;
import me.lisacek.clickableimages.listeners.ClickListener;
import me.lisacek.clickableimages.managers.Manager;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.ItemFrame;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.MapMeta;

import java.awt.image.BufferedImage;
import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.atomic.AtomicInteger;

public class ClickableImagesManager implements Manager {

    private File folder;
    private final List<ClickableImage> images = new ArrayList<>();

    private final ClickListener listener = new ClickListener();

    @Override
    public void onEnable() {
        folder = new File(getPlugin().getDataFolder(), "images");
        folder.mkdirs();

        loadImages();

        Bukkit.getPluginManager().registerEvents(listener, getPlugin());
        initMaps();
        ClickableImages.getInstance().getConsole().info("Images loaded! (&#03fc7b" + images.size() + "&7)");
    }

    @Override
    public void onDisable() {
        images.clear();
        listener.unregister();
        ClickableImages.getInstance().getConsole().info("Images unloaded!");
    }

    public void loadImages() {
        images.clear();

        for (File file : Objects.requireNonNull(folder.listFiles())) {
            if (!file.getName().endsWith(".yml")) continue;
            YamlConfiguration config = YamlConfiguration.loadConfiguration(file);
            String image = config.getString("image");
            String permission = config.getString("permission", "none");
            List<String> actions = config.getStringList("actions");
            List<List<Location>> locations = Lists.newArrayList();
            int rows = config.getConfigurationSection("locations").getKeys(false).size();
            boolean isValid = true;
            for (int i = 0; i < rows; i++) {
                List<JsonObject> lcs = Lists.newArrayList();

                config.getStringList("locations." + i).forEach(o -> {
                    JsonParser parser = new JsonParser();
                    JsonObject json = parser.parse(o).getAsJsonObject();
                    lcs.add(json);
                });

                List<Location> finalLocs = Lists.newArrayList();
                for (JsonObject lc : lcs) {
                    World world = Bukkit.getWorld(lc.get("world").getAsString());
                    if (world == null) {
                        isValid = false;
                        break;
                    }
                    finalLocs.add(new Location(Bukkit.getWorld(lc.get("world").getAsString()), lc.get("x").getAsDouble(), lc.get("y").getAsDouble(), lc.get("z").getAsDouble(), lc.get("yaw").getAsFloat(), lc.get("pitch").getAsFloat()));
                }
                locations.add(finalLocs);
            }
            if (!isValid) {
                ClickableImages.getInstance().getConsole().warn("&cWorld used for creating image: &e" + file.getName() + " &cdoesn't exist! This image will be ignored.");
            } else {
                images.add(new ClickableImage(file.getName(), image, permission, actions, locations));
            }
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
        if (image == null) {
            return;
        }

        Pair<Integer, Integer> axis = findGrid(image, item.getLocation());
        if (axis == null) {
            return;
        }

        if (image.getAsset() == null) {
            ClickableImages.getInstance().getConsole().warn("Image " + image.getName() + " is missing asset!");
            return;
        }

        ItemStack m = new ItemStack(Material.FILLED_MAP);
        MapMeta meta = (MapMeta) m.getItemMeta();

        BufferedImage bfIm;
        try {
            bfIm = image.getAsset().getImage(axis.getFirst(), axis.getSecond());
        } catch (Exception e) {
            ClickableImages.getInstance().getConsole().warn("Asset " + image.getAsset().getFile().getName() + " is smaller than " + image.getName() + " is!");
            return;
        }

        meta.setMapView(Bukkit.createMap(item.getWorld()));
        meta.getMapView().getRenderers().clear();

        Renderer renderer = Renderer.installRenderer(meta.getMapView());
        renderer.setImage(bfIm);
        meta.getMapView().addRenderer(renderer);

        m.setItemMeta(meta);
        item.setItem(m);
        item.setVisible(false);
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
        for (ClickableImage image : images) {
            if (image.getGrid().stream().anyMatch(l -> l.stream().anyMatch(l1 -> l1.equals(location)))) {
                return image;
            }
        }
        return null;
    }

    public void addAction(ClickableImage image, String action) {
        image.getActions().add(action);
        image.save();
    }

    public void addPermission(ClickableImage image, String permission) {
        image.setPermission(permission);
        image.save();
    }

    public void removePermission(ClickableImage image) {
        image.setPermission("none");
        image.save();
    }

    public void removeAction(ClickableImage image, int index) {
        image.getActions().remove(index);
        image.save();
    }

    public ClickableImage getImage(String name) {
        for (ClickableImage image : images) {
            if (image.getName().equals(name)) {
                return image;
            }
        }
        return null;
    }

    public int getTotalLocations() {
        AtomicInteger total = new AtomicInteger();
        for (ClickableImage image : images) {
            image.getGrid().forEach(l -> total.addAndGet(l.size()));
        }
        return total.get();
    }
}
