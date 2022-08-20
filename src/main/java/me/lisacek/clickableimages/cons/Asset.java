package me.lisacek.clickableimages.cons;

import com.google.common.collect.Lists;
import me.lisacek.clickableimages.ClickableImages;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.ItemFrame;
import org.bukkit.entity.Player;
import me.lisacek.clickableimages.managers.Managers;
import me.lisacek.clickableimages.managers.impl.ClickableImagesManager;

import java.awt.image.BufferedImage;
import java.io.File;
import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

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

    public Pair<Boolean, NodeList> canPlace(ItemFrame frame) {
        Location l = frame.getLocation();
        NodeList list = new NodeList();
        int indexX = 0;
        int indexY = 0;

        BlockFace facing = frame.getFacing();
        if (facing == BlockFace.NORTH) {
            // x + | y
            checkRowX(frame, l, list, -indexX, indexY);
        }
        else if (facing == BlockFace.SOUTH) {
            // x - | y
            checkRowX(frame, l, list, indexX, indexY);
        }
        else if (facing == BlockFace.EAST) {
            // z - | y
            checkRowZ(frame, l, list, indexX, indexY);
        }
        else if (facing == BlockFace.WEST) {
            // z + | y
            checkRowZ(frame, l, list, -indexX, indexY);
        }
        else if (facing == BlockFace.DOWN) {
            // x + | z
            checkRowY(frame, l, list, indexX, indexY);
        }
        else if (facing == BlockFace.UP) {
            // x - | z
            checkRowY(frame, l, list, -indexX, indexY);
        }

        list.sort(facing == BlockFace.NORTH || facing == BlockFace.EAST);
        return new Pair<>(getTotalImages() == list.size() && list.size() <= ClickableImages.getInstance().getConfig().getInt("plugin.max-item-frames", 64), list);
    }

    public ClickableImage place(NodeList list, Player p, ItemFrame frame) {
        BlockFace facing = frame.getFacing();

        List<List<Location>> grid = Lists.newArrayList();

        for (int i = 0; i < list.rows; i++) {
            List<Location> row = Lists.newArrayList();
            for (int j = 0; j < list.columns; j++) {
                row.add(list.getNodeAt(i, j).frame.getLocation());
            }
            grid.add(row);
        }

        if(facing == BlockFace.WEST || facing == BlockFace.SOUTH || facing == BlockFace.DOWN) {
            Collections.reverse(grid);
        }

        ClickableImage image = new ClickableImage(file.getName() + "-" + Managers.getManager(ClickableImagesManager.class).getImages().size() + ".yml", file.getName(), "none", Lists.newArrayList("[MSG] &eThis image was created by " + p.getName() + "."), grid);
        image.save();
        Managers.getManager(ClickableImagesManager.class).getImages().add(image);
        return image;
    }


    private void checkRowX(ItemFrame frame, Location l, NodeList list, int indexX, int indexY) {
        list.add(indexX, indexY, frame);
        checkColumnX(l, list, indexX);
        int columns = 1;
        while(true) {
            indexX++;
            Collection<ItemFrame> frames = l.clone().add(indexX, 0, 0).getNearbyEntitiesByType(ItemFrame.class, 0.5, 0, 0).stream().filter(f -> f.getItem().getType() == Material.AIR).collect(Collectors.toList());
            if (frames.size() == 0) {
                break;
            }
            for (ItemFrame f : frames) {
                list.add(indexX, indexY, f);
            }
            checkColumnX(l, list, indexX);
            columns++;
        }
        indexX = 0;
        while(true) {
            indexX++;
            Collection<ItemFrame> frames = l.clone().add(-indexX, 0, 0).getNearbyEntitiesByType(ItemFrame.class, 0.5, 0.5, 0).stream().filter(f -> f.getItem().getType() == Material.AIR).collect(Collectors.toList());
            if (frames.size() == 0) {
                break;
            }
            for (ItemFrame f : frames) {
                list.add(-indexX, indexY, f);
            }
            checkColumnX(l, list, -indexX);
            columns++;
        }
        list.columns = columns;
        list.rows = list.size() / columns;
    }

    private void checkColumnX(Location l, NodeList list, int indexX) {
        int indexY = 0;
        while(true) {
            indexY++;
            Collection<ItemFrame> frames1 = l.clone().add(indexX, indexY, 0).getNearbyEntitiesByType(ItemFrame.class, 0.5, 0, 0).stream().filter(f -> f.getItem().getType() == Material.AIR).collect(Collectors.toList());
            if (frames1.size() == 0) {
                break;
            }
            for (ItemFrame f : frames1) {
                list.add(indexX, indexY, f);
            }
        }
        indexY = 0;
        while(true) {
            indexY++;
            Collection<ItemFrame> frames1 = l.clone().add(indexX, -indexY, 0).getNearbyEntitiesByType(ItemFrame.class, 0.5, 0, 0).stream().filter(f -> f.getItem().getType() == Material.AIR).collect(Collectors.toList());
            if (frames1.size() == 0) {
                break;
            }
            for (ItemFrame f : frames1) {
                list.add(indexX, -indexY, f);
            }
        }
    }

    private void checkRowZ(ItemFrame frame, Location l, NodeList list, int indexX, int indexY) {
        list.add(indexX, indexY, frame);
        checkColumnZ(l, list, indexX);
        int columns = 1;
        while(true) {
            indexX++;
            Collection<ItemFrame> frames = l.clone().add(0, 0, indexX).getNearbyEntitiesByType(ItemFrame.class, 0, 0, 0.5).stream().filter(f -> f.getItem().getType() == Material.AIR).collect(Collectors.toList());
            if (frames.size() == 0) {
                break;
            }
            for (ItemFrame f : frames) {
                list.add(indexX, indexY, f);
            }
            checkColumnZ(l, list, indexX);
            columns++;
        }
        indexX = 0;
        while(true) {
            indexX++;
            Collection<ItemFrame> frames = l.clone().add(0, 0, -indexX).getNearbyEntitiesByType(ItemFrame.class, 0, 0.5, 0.5).stream().filter(f -> f.getItem().getType() == Material.AIR).collect(Collectors.toList());
            if (frames.size() == 0) {
                break;
            }
            for (ItemFrame f : frames) {
                list.add(-indexX, indexY, f);
            }
            checkColumnZ(l, list, -indexX);
            columns++;
        }
        list.columns = columns;
        list.rows = list.size() / columns;
    }

    private void checkColumnZ(Location l, NodeList list, int indexX) {
        int indexY = 0;
        while(true) {
            indexY++;
            Collection<ItemFrame> frames1 = l.clone().add(0, indexY, indexX).getNearbyEntitiesByType(ItemFrame.class, 0, 0, 0.5).stream().filter(f -> f.getItem().getType() == Material.AIR).collect(Collectors.toList());
            if (frames1.size() == 0) {
                break;
            }
            for (ItemFrame f : frames1) {
                list.add(indexX, indexY, f);
            }
        }
        indexY = 0;
        while(true) {
            indexY++;
            Collection<ItemFrame> frames1 = l.clone().add(0, -indexY, indexX).getNearbyEntitiesByType(ItemFrame.class, 0, 0, 0.5).stream().filter(f -> f.getItem().getType() == Material.AIR).collect(Collectors.toList());
            if (frames1.size() == 0) {
                break;
            }
            for (ItemFrame f : frames1) {
                list.add(indexX, -indexY, f);
            }
        }
    }

    private void checkRowY(ItemFrame frame, Location l, NodeList list, int indexX, int indexY) {
        list.add(indexX, indexY, frame);
        checkColumnY(l, list, indexX);
        int columns = 1;
        while(true) {
            indexX++;
            Collection<ItemFrame> frames = l.clone().add(indexX, 0, 0).getNearbyEntitiesByType(ItemFrame.class, 0.5, 0, 0).stream().filter(f -> f.getItem().getType() == Material.AIR).collect(Collectors.toList());
            if (frames.size() == 0) {
                break;
            }
            for (ItemFrame f : frames) {
                list.add(indexX, indexY, f);
            }
            checkColumnY(l, list, indexX);
            columns++;
        }
        indexX = 0;
        while(true) {
            indexX++;
            Collection<ItemFrame> frames = l.clone().add(-indexX, 0, 0).getNearbyEntitiesByType(ItemFrame.class, 0.5, 0.5, 0).stream().filter(f -> f.getItem().getType() == Material.AIR).collect(Collectors.toList());
            if (frames.size() == 0) {
                break;
            }
            for (ItemFrame f : frames) {
                list.add(-indexX, indexY, f);
            }
            checkColumnY(l, list, -indexX);
            columns++;
        }
        list.columns = columns;
        list.rows = list.size() / columns;
    }

    private void checkColumnY(Location l, NodeList list, int indexX) {
        int indexY = 0;
        while(true) {
            indexY++;
            Collection<ItemFrame> frames1 = l.clone().add(indexX, 0, indexY).getNearbyEntitiesByType(ItemFrame.class, 0.5, 0, 0).stream().filter(f -> f.getItem().getType() == Material.AIR).collect(Collectors.toList());
            if (frames1.size() == 0) {
                break;
            }
            for (ItemFrame f : frames1) {
                list.add(indexX, indexY, f);
            }
        }
        indexY = 0;
        while(true) {
            indexY++;
            Collection<ItemFrame> frames1 = l.clone().add(indexX, 0, -indexY).getNearbyEntitiesByType(ItemFrame.class, 0.5, 0, 0).stream().filter(f -> f.getItem().getType() == Material.AIR).collect(Collectors.toList());
            if (frames1.size() == 0) {
                break;
            }
            for (ItemFrame f : frames1) {
                list.add(indexX, -indexY, f);
            }
        }
    }


}