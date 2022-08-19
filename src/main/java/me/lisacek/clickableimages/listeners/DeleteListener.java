package me.lisacek.clickableimages.listeners;

import me.lisacek.clickableimages.cons.ClickableImage;
import me.lisacek.clickableimages.managers.Managers;
import me.lisacek.clickableimages.managers.impl.ClickableImagesManager;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.ItemFrame;
import org.bukkit.event.EventHandler;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerInteractAtEntityEvent;
import org.bukkit.inventory.ItemStack;
import me.lisacek.clickableimages.cons.NodeList;


public class DeleteListener implements Listener {

    @EventHandler
    public void onClick(PlayerInteractAtEntityEvent event) {
        if(event.getRightClicked() == null) return;
        if(event.getRightClicked().getType() != EntityType.ITEM_FRAME) return;

        Location location = event.getRightClicked().getLocation();
        ClickableImage image = Managers.getManager(ClickableImagesManager.class).getImage(location);

        if(image == null) {
            Bukkit.getLogger().info("Image is null");
            return;
        }
        NodeList list = image.getAsset().canPlace((ItemFrame) event.getRightClicked()).getSecond();
        for (int i = 0; i < list.rows; i++) {
            for (int j = 0; j < list.columns; j++) {
              list.getNodeAt(i, j).frame.setItem(new ItemStack(Material.AIR));
              list.getNodeAt(i, j).frame.setVisible(true);
            }
        }
        image.delete();
        Managers.getManager(ClickableImagesManager.class).getImages().remove(image);
        event.getPlayer().sendMessage("Deleted image!");
        HandlerList.unregisterAll(this);
        event.setCancelled(true);
    }

}
