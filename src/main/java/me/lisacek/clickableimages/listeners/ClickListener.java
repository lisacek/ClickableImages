package me.lisacek.clickableimages.listeners;

import me.lisacek.clickableimages.ClickableImages;
import me.lisacek.clickableimages.cons.Button;
import me.lisacek.clickableimages.cons.ClickableImage;
import me.lisacek.clickableimages.cons.Pair;
import me.lisacek.clickableimages.utils.Colors;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.player.PlayerInteractAtEntityEvent;
import me.lisacek.clickableimages.managers.Managers;
import me.lisacek.clickableimages.managers.impl.ClickableImagesManager;
import org.bukkit.event.player.PlayerInteractEntityEvent;

public class ClickListener implements Listener {

    @EventHandler
    public void onClick(PlayerInteractEntityEvent event) {
        if (event.getRightClicked() == null) return;
        if (event.getRightClicked().getType() != EntityType.ITEM_FRAME) return;
        Location location = event.getRightClicked().getLocation();
        ClickableImage image = Managers.getManager(ClickableImagesManager.class).getImage(location);
        if (image == null) return;
        if (!event.getPlayer().hasPermission("clickableimages.use")) {
            event.setCancelled(true);
            event.getPlayer().sendMessage(Colors.translateColors(ClickableImages.getInstance().getConfig().getString("messages.no-permission")));
            return;
        }
        Pair<Integer, Integer> coords = Managers.getManager(ClickableImagesManager.class).findGrid(image, location);
        String coordsString = coords.getFirst() + "+" + coords.getSecond();
        event.setCancelled(true);
        if(image.isButton(coordsString)) {
            Button button = image.getButton(coordsString);
            if (!button.getPermission().equalsIgnoreCase("none") && !event.getPlayer().hasPermission(button.getPermission())) {
                event.setCancelled(true);
                event.getPlayer().sendMessage(Colors.translateColors(ClickableImages.getInstance().getConfig().getString("messages.no-permission")));
                return;
            }
            image.run(event.getPlayer(), button);
            return;
        }
        if (!image.getPermission().equalsIgnoreCase("none") && !event.getPlayer().hasPermission(image.getPermission())) {
            event.setCancelled(true);
            event.getPlayer().sendMessage(Colors.translateColors(ClickableImages.getInstance().getConfig().getString("messages.no-permission")));
            return;
        }
        image.run(event.getPlayer());
    }

    @EventHandler
    public void onDamage(EntityDamageByEntityEvent event) {
        if (event.getEntityType() != EntityType.ITEM_FRAME) return;
        if (!(event.getDamager() instanceof Player)) return;
        Player player = (Player) event.getDamager();

        Location location = event.getEntity().getLocation();
        ClickableImage image = Managers.getManager(ClickableImagesManager.class).getImage(location);
        if (image == null) return;
        if (!player.hasPermission("clickableimages.use") && !player.hasPermission("clickableimages.admin")) {
            event.setCancelled(true);
            player.sendMessage(Colors.translateColors(ClickableImages.getInstance().getConfig().getString("messages.no-permission")));
            return;
        }
        Pair<Integer, Integer> coords = Managers.getManager(ClickableImagesManager.class).findGrid(image, location);
        String coordsString = coords.getFirst() + "+" + coords.getSecond();
        event.setCancelled(true);
        if(image.isButton(coordsString)) {
            Button button = image.getButton(coordsString);
            if (!button.getPermission().equalsIgnoreCase("none") && !player.hasPermission(button.getPermission())) {
                event.setCancelled(true);
                player.sendMessage(Colors.translateColors(ClickableImages.getInstance().getConfig().getString("messages.no-permission")));
                return;
            }
            image.run(player, button);
            return;
        }
        if (!image.getPermission().equalsIgnoreCase("none") && !player.hasPermission(image.getPermission())) {
            event.setCancelled(true);
            player.sendMessage(Colors.translateColors(ClickableImages.getInstance().getConfig().getString("messages.no-permission")));
            return;
        }
        image.run(player);
    }

    public void unregister() {
        HandlerList.unregisterAll(this);
    }

}
