package sins.johnny.clickableimages.cons;

import org.bukkit.Location;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import sins.johnny.clickableimages.ClickableImages;
import sins.johnny.clickableimages.managers.Managers;
import sins.johnny.clickableimages.managers.impl.AssetsManager;
import sins.johnny.clickableimages.managers.impl.ClickableImagesManager;
import sins.johnny.clickableimages.utils.ActionsUtils;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

public class ClickableImage {

    private final String name;

    private final String image;
    private final List<String> actions;
    private final List<List<Location>> locations;

    public ClickableImage(String name, String image, List<String> actions, List<List<Location>> locations) {
        this.name = name;
        this.image = image;
        this.actions = actions;
        this.locations = locations;
    }

    public String getName() {
        return name;
    }

    public String getImage() {
        return image;
    }

    public List<String> getActions() {
        return actions;
    }

    public List<List<Location>> getGrid() {
        return locations;
    }

    //rows
    public int getRows() {
        return locations.size();
    }

    //total columns
    public int getColumns() {
        return locations.get(0).size();
    }


    public Asset getAsset() {
        return Managers.getManager(AssetsManager.class).getAsset(image);
    }

    public void run(Player p) {
        for (String action : actions) {
            ActionsUtils.runAction(p, action);
        }
    }

    public void save() {
        File folder = new File(ClickableImages.getInstance().getDataFolder(), "images");
        if (!folder.exists()) {
            folder.mkdirs();
        }

        AtomicInteger count = new AtomicInteger(0);
        File[] files = folder.listFiles();
        if (files != null) {
            for (File file : files) {
                if (file.isFile()) {
                    count.incrementAndGet();
                }
            }
        }

        File file = new File(folder, image + "-" + Managers.getManager(ClickableImagesManager.class).getImages().size() + ".yml");
        try {
            file.createNewFile();
            YamlConfiguration config = YamlConfiguration.loadConfiguration(file);
            config.set("image", image);
            config.set("actions", actions);

            List<List<Location>> grid = getGrid();
            for (int i = 0; i < grid.size(); i++) {
                List<Location> row = grid.get(i);
                config.set("locations." + i, row);
            }

            config.save(file);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public void delete() {
        File folder = new File(ClickableImages.getInstance().getDataFolder(), "images");
        if (!folder.exists()) {
            folder.mkdirs();
        }
        File file = new File(folder, name);
        file.delete();
    }
}
