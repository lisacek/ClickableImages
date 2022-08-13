package sins.johnny.clickableimages.cons;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.ItemFrame;
import org.bukkit.entity.Player;
import sins.johnny.clickableimages.managers.Managers;
import sins.johnny.clickableimages.managers.impl.ClickableImagesManager;

import java.awt.image.BufferedImage;
import java.io.File;
import java.util.*;
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
        boolean changingX = false;
        BlockFace direction = itemFrame.getFacing();
        switch (direction) {
            case NORTH:
            case SOUTH:
                changingX = true;
                break;
            default:
                break;
        }

        AtomicInteger count = new AtomicInteger();
        itemFrame.getNearbyEntities(changingX ? getColumns(): 0, getRows(), changingX ? 0 : getColumns()).forEach(entity -> {
            if (entity instanceof ItemFrame) {
                ItemFrame other = (ItemFrame) entity;
                count.getAndIncrement();
            }
        });
        return count.get() + 1 == getTotalImages();
    }

    public ClickableImage place(Player p, ItemFrame itemFrame) {
        boolean changingX = false;
        BlockFace direction = itemFrame.getFacing();
        switch (direction) {
            case NORTH:
            case SOUTH:
                changingX = true;
                break;
            default:
                break;
        }

        Set<ItemFrame> frames = Sets.newHashSet();
        frames.add(itemFrame);

        itemFrame.getNearbyEntities(changingX ? getColumns(): 0, getRows(), changingX ? 0 : getColumns()).forEach(entity -> {
            if (entity instanceof ItemFrame) {
                frames.add((ItemFrame) entity);
            }
        });

        //Rows
        Set<Integer> r = Sets.newHashSet();
        // Y to Row
        Map<Integer, Integer> fck = Maps.newHashMap();

        // generate row size
        frames.forEach(it -> {
            if (!r.contains(it.getLocation().getBlockY())) {
                fck.put(it.getLocation().getBlockY(), r.size() + 1);
            }
            r.add(it.getLocation().getBlockY());
        });

        // final grid
        List<List<Location>> g = Lists.newArrayList();

        // add rows
        for (int x = 0; x < r.size(); x++) {
            List<Location> row = Lists.newArrayList();
            g.add(row);
        }

        // add frames to its row and short it
        boolean finalChangingX = changingX;
        frames.forEach(it -> {
            g.get(fck.get(it.getLocation().getBlockY()) - 1).add(it.getLocation());
            if (finalChangingX) {
                g.get(fck.get(it.getLocation().getBlockY()) - 1).sort(Comparator.comparingInt(Location::getBlockX));
            } else {
                g.get(fck.get(it.getLocation().getBlockY()) - 1).sort(Comparator.comparingInt(Location::getBlockZ));
            }
        });

        // get number of rows from fck
        int rows = fck.size();
        // get total values from each row from fck
        int total =frames.size();
        int totalRows = grid.size();

        AtomicInteger count = new AtomicInteger();
        grid.forEach(row -> {
            row.forEach(image -> {
                count.getAndIncrement();
            } );
        } );


        if (count.get() > total) {
            Bukkit.getLogger().info("Too many images to place");
            return null;
        }

        if (rows > totalRows) {
            Bukkit.getLogger().info("Too many rows to place");
            return null;
        }

        Collections.reverse(g);
        ClickableImage image = new ClickableImage(file.getName(), Lists.newArrayList("[MSG] Hi! This image is created by " + p.getName()), g);
        image.save();
        Managers.getManager(ClickableImagesManager.class).getImages().add(image);
        return image;
    }
}
