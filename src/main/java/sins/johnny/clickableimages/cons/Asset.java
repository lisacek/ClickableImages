package sins.johnny.clickableimages.cons;

import com.google.common.collect.Lists;
import org.bukkit.Location;
import org.bukkit.entity.ItemFrame;
import org.bukkit.entity.Player;
import sins.johnny.clickableimages.managers.Managers;
import sins.johnny.clickableimages.managers.impl.ClickableImagesManager;

import java.awt.image.BufferedImage;
import java.io.File;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

public class Asset {

    private final File file;
    private final List<List<BufferedImage>> grid;

    public Asset(File file, List<List<BufferedImage>> grid) {
        this.file = file;
        this.grid = grid;
    }

    public File getFile() {
        return file;
    }

    public List<List<BufferedImage>> getGrid() {
        return grid;
    }

    public List<BufferedImage> getRow(int row) {
        return grid.get(row);
    }

    public BufferedImage getImage(int row, int column) {
        return grid.get(row).get(column);
    }

    public int getRows() {
        return grid.size();
    }

    public int getColumns(int row) {
        return grid.get(row).size();
    }

    public int getColumns() {
        return getColumns(0);
    }

    public int getTotalImages() {
        AtomicInteger total = new AtomicInteger(0);
        grid.forEach(row -> total.addAndGet(row.size()));
        return total.get();
    }

    public boolean canPlace(ItemFrame itemFrame) {
        Location location = itemFrame.getLocation();

        AtomicInteger count = new AtomicInteger();
        itemFrame.getNearbyEntities(getColumns(), getRows(), getColumns()).forEach(entity -> {
            if (entity instanceof ItemFrame) {
                ItemFrame other = (ItemFrame) entity;
                count.getAndIncrement();
            }
        });

        return count.get() > getTotalImages();
    }

    public ClickableImage place(Player p, ItemFrame itemFrame) {
        if (!canPlace(itemFrame)) {
            return null;
        }

        Location location = itemFrame.getLocation();
        int rows = getRows();
        int columns = getColumns();
        List<Location> locations = Lists.newArrayList();
        for (int row = 0; row < rows; row++) {
            for (int column = 0; column < columns; column++) {
                locations.add(location.clone().add(column, row, 0));
            }
        }

        ClickableImage image = new ClickableImage(file.getName(), Lists.newArrayList("[MSG] Hi! This image is created by " + p.getName()), locations);
        image.save();
        Managers.getManager(ClickableImagesManager.class).getImages().add(image);

        return image;
    }
}
