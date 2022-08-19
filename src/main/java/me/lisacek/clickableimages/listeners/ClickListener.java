package me.lisacek.clickableimages.listeners;

import me.lisacek.clickableimages.cons.ClickableImage;
import org.bukkit.Location;
import org.bukkit.entity.EntityType;
import org.bukkit.event.EventHandler;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerInteractAtEntityEvent;
import me.lisacek.clickableimages.managers.Managers;
import me.lisacek.clickableimages.managers.impl.ClickableImagesManager;

public class ClickListener implements Listener {

    @EventHandler
    public void onClick(PlayerInteractAtEntityEvent event) {
        if(event.getRightClicked() == null) return;
        if(event.getRightClicked().getType() != EntityType.ITEM_FRAME) return;

        Location location = event.getRightClicked().getLocation();
        ClickableImage image = Managers.getManager(ClickableImagesManager.class).getImage(location);

        if(image == null) return;
        event.setCancelled(true);
        image.run(event.getPlayer());
    }

    public void unregister() {
        HandlerList.unregisterAll(this);
    }

}
