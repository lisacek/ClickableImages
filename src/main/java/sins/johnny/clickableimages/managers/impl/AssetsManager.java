package sins.johnny.clickableimages.managers.impl;

import sins.johnny.clickableimages.cons.Asset;
import sins.johnny.clickableimages.managers.Manager;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

public class AssetsManager implements Manager {

    private File folder;
    private final List<Asset> assets = new ArrayList<>();

    @Override
    public void onEnable() {
        folder = new File(getPlugin().getDataFolder(), "assets");
        folder.mkdirs();

        loadAssets();
    }

    @Override
    public void onDisable() {
        assets.clear();
    }

    public void loadAssets() {
        assets.clear();

        for (File file : getAllAssets()) {
            try {
                BufferedImage image = ImageIO.read(file);
                double rows = (double) (image.getHeight() / 256);
                double columns = (double) (image.getWidth() / 256);

                rows = (int) Math.ceil(rows);
                columns = (int) Math.ceil(columns);

                List<List<BufferedImage>> grid = new ArrayList<>();
                for (int i = 0; i < rows; i++) {
                    List<BufferedImage> row = new ArrayList<>();
                    for (int j = 0; j < columns; j++) {
                        row.add(image.getSubimage(j * 256, i * 256, 256, 256));
                    }
                    grid.add(row);
                }

                assets.add(new Asset(file, grid));
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
    }

    public List<File> getAllFiles() {
        return Arrays.asList(Objects.requireNonNull(folder.listFiles()));
    }

    public List<File> getAllAssets() {
        return getAllFiles().stream().filter(file -> file.getName().endsWith(".png")).collect(Collectors.toList());
    }

    public List<Asset> getAssets() {
        return assets;
    }

    public Asset getAsset(File file) {
        return assets.stream().filter(asset -> asset.getFile().equals(file)).findFirst().orElse(null);
    }

    public Asset getAsset(String name) {
        return assets.stream().filter(asset -> asset.getFile().getName().equals(name)).findFirst().orElse(null);
    }

}
