package sins.johnny.clickableimages.cons;

import org.bukkit.Location;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import sins.johnny.clickableimages.ClickableImages;
import sins.johnny.clickableimages.managers.Managers;
import sins.johnny.clickableimages.managers.impl.AssetsManager;
import sins.johnny.clickableimages.utils.ActionsUtils;

import java.io.File;
import java.io.IOException;
import java.util.List;

public class ClickableImage {

    private final String image;
    private final List<String> actions;
    private final List<List<Location>> locations;

    public ClickableImage(String image, List<String> actions, List<List<Location>> locations) {
        this.image = image;
        this.actions = actions;
        this.locations = locations;
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

        File file = new File(folder, image + "_" + System.currentTimeMillis() + ".yml");
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
}
