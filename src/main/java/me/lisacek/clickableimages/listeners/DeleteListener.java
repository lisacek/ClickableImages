package me.lisacek.clickableimages.listeners;

import com.google.common.collect.Lists;
import me.lisacek.clickableimages.ClickableImages;
import me.lisacek.clickableimages.cons.ClickableImage;
import me.lisacek.clickableimages.managers.Managers;
import me.lisacek.clickableimages.managers.impl.ClickableImagesManager;
import me.lisacek.clickableimages.managers.impl.DeleteManager;
import me.lisacek.clickableimages.utils.Colors;
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

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;


public class DeleteListener implements Listener {

    @EventHandler
    public void onClick(PlayerInteractAtEntityEvent event) {
        if (!Managers.getManager(DeleteManager.class).isRunning(event.getPlayer().getName())) return;

        if (event.getRightClicked() == null) return;
        if (event.getRightClicked().getType() != EntityType.ITEM_FRAME) return;

        Location location = event.getRightClicked().getLocation();
        ClickableImage image = Managers.getManager(ClickableImagesManager.class).getImage(location);

        if (image == null) {
            return;
        }

        location.getNearbyEntitiesByType(ItemFrame.class, 10).forEach(itemFrame -> {
            //check if image grid locations contain the item frame location
            image.getGrid().forEach(row -> {
                row.forEach(gridLocation -> {
                    if (gridLocation.equals(itemFrame.getLocation())) {
                        itemFrame.setVisible(true);
                        itemFrame.setItem(new ItemStack(Material.AIR));
                    }
                });
            });
        } );

        image.delete();
        Managers.getManager(ClickableImagesManager.class).getImages().remove(image);
        event.getPlayer().sendMessage(Colors.translateColors(ClickableImages.getInstance().getConfig().getString("messages.deleted")));
        Managers.getManager(DeleteManager.class).stop(event.getPlayer().getName());
    }

    public void unregister() {
        HandlerList.unregisterAll(this);
    }

}
