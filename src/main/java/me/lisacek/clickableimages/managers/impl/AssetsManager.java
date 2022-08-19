package me.lisacek.clickableimages.managers.impl;

import me.lisacek.clickableimages.ClickableImages;
import me.lisacek.clickableimages.cons.Asset;
import me.lisacek.clickableimages.managers.Manager;

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

    private static final int WIDTH = 128;
    private static final int HEIGHT = 128;

    private File folder;
    private final List<Asset> assets = new ArrayList<>();

    @Override
    public void onEnable() {
        folder = new File(getPlugin().getDataFolder(), "assets");
        folder.mkdirs();
        loadAssets();
        ClickableImages.getInstance().getConsole().info("Assets loaded! (&#03fc7b" + assets.size() + "&7)");
    }

    @Override
    public void onDisable() {
        assets.clear();
        ClickableImages.getInstance().getConsole().info("Assets unloaded!");
    }

    public void loadAssets() {
        assets.clear();

        for (File file : getAllAssets()) {
            try {
                BufferedImage image = ImageIO.read(file);
                double rows = (double) (image.getHeight() / HEIGHT);
                double columns = (double) (image.getWidth() / WIDTH);

                rows = (int) Math.ceil(rows);
                columns = (int) Math.ceil(columns);

                List<List<BufferedImage>> grid = new ArrayList<>();
                for (int i = 0; i < rows; i++) {
                    List<BufferedImage> row = new ArrayList<>();
                    for (int j = 0; j < columns; j++) {
                        row.add(image.getSubimage(j * WIDTH, i * HEIGHT, WIDTH, HEIGHT));
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
