package me.lisacek.clickableimages.utils;

import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.MapMeta;

public class ItemUtils {

    public static short getMapIdFromItemStack(final ItemStack item) {
        return item.getDurability();
    }

    public static ItemStack createMapItem(final short mapId) {
        final ItemStack item = new ItemStack(Material.MAP, 1, mapId);
        final ItemMeta meta = item.getItemMeta();
        final MapMeta mapMeta = (MapMeta) meta;
        mapMeta.setScaling(false);
        item.setItemMeta(mapMeta);
        return item;
    }

}
