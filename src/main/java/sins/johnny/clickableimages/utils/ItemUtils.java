package sins.johnny.clickableimages.utils;

import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.MapMeta;

public class ItemUtils {

    public static short getMapIdFromItemStack(final ItemStack item) {
        return item.getDurability();
    }

}
