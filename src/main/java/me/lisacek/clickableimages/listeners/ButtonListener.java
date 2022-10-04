package me.lisacek.clickableimages.listeners;

import io.papermc.paper.event.player.AsyncChatEvent;
import me.lisacek.clickableimages.ClickableImages;
import me.lisacek.clickableimages.cons.Button;
import me.lisacek.clickableimages.cons.ClickableImage;
import me.lisacek.clickableimages.cons.Pair;
import me.lisacek.clickableimages.enums.Action;
import me.lisacek.clickableimages.managers.Manager;
import me.lisacek.clickableimages.managers.Managers;
import me.lisacek.clickableimages.managers.impl.ButtonManager;
import me.lisacek.clickableimages.managers.impl.ClickableImagesManager;
import me.lisacek.clickableimages.utils.Colors;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.event.player.PlayerInteractEntityEvent;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class ButtonListener implements Listener {

    private final Map<String, String> actionToAdd = new HashMap<>();

    @EventHandler
    public void onClick(PlayerInteractEntityEvent event) {
        ClickableImagesManager manager = Managers.getManager(ClickableImagesManager.class);
        ButtonManager buttonManager = Managers.getManager(ButtonManager.class);
        if (event.getRightClicked() == null) return;
        if (event.getRightClicked().getType() != EntityType.ITEM_FRAME) return;
        Action action = buttonManager.getQueue().get(event.getPlayer().getName());
        if(action == null) return;
        Location location = event.getRightClicked().getLocation();
        ClickableImage image = manager.getImage(location);
        if (image == null) return;
        Pair<Integer, Integer> coords = manager.findGrid(image, location);
        String coordsString = coords.getFirst() + "+" + coords.getSecond();
        event.setCancelled(true);
        switch (action) {
            case ADD:
                Button button = new Button(coords.getFirst() + "+" + coords.getSecond(), new ArrayList<>(), "none");
                image.getButtons().add(button);
                event.getPlayer().sendMessage(Colors.translateColors("&aButton added!"));
                break;
            case REMOVE:
                boolean deleted = false;
                for (Button b : image.getButtons()) {
                    if (b.getCoords().equals(coordsString)) {
                        image.getButtons().remove(b);
                        deleted = true;
                        break;
                    }
                }
                if (!deleted) {
                    event.getPlayer().sendMessage(Colors.translateColors("&cButton not found!"));
                    return;
                }
                event.getPlayer().sendMessage(Colors.translateColors("&aButton removed!"));
                break;
            default:
                return;
        }

        image.save();
        buttonManager.getQueue().remove(event.getPlayer().getName());
    }

    @EventHandler
    public void onDamage(EntityDamageByEntityEvent event) {
        ClickableImagesManager manager = Managers.getManager(ClickableImagesManager.class);
        ButtonManager buttonManager = Managers.getManager(ButtonManager.class);
        if (!buttonManager.getQueue().containsKey(event.getDamager().getName())) return;
        if (event.getEntityType() != EntityType.ITEM_FRAME) return;
        if (!(event.getDamager() instanceof Player)) return;

        Action action = buttonManager.getQueue().get(event.getDamager().getName());
        Location location = event.getEntity().getLocation();
        ClickableImage image = manager.getImage(location);
        if (image == null) return;
        Pair<Integer, Integer> coords = manager.findGrid(image, location);
        String coordsString = coords.getFirst() + "+" + coords.getSecond();
        switch (action) {
            case ADD:
                Button button = new Button(coords.getFirst() + "+" + coords.getSecond(), new ArrayList<>(), "none");
                image.getButtons().add(button);
                break;
            case REMOVE:
                boolean deleted = false;
                for (Button b : image.getButtons()) {
                    if (b.getCoords().equals(coordsString)) {
                        image.getButtons().remove(b);
                        deleted = true;
                        break;
                    }
                }
                if (!deleted) {
                    event.getDamager().sendMessage(Colors.translateColors("&cButton not found!"));
                }
                break;
            default:
                return;
        }

        image.save();
        buttonManager.getQueue().remove(event.getDamager().getName());
        event.setCancelled(true);
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void onChat(AsyncPlayerChatEvent e) {
        ButtonManager buttonManager = Managers.getManager(ButtonManager.class);
        if (!buttonManager.getQueue().containsKey(e.getPlayer().getName())) return;
        Action action = buttonManager.getQueue().get(e.getPlayer().getName());
        if (action != Action.EDIT) return;
        buttonManager.getQueue().remove(e.getPlayer().getName());
        actionToAdd.put(e.getPlayer().getName(), e.getMessage());
        e.setCancelled(true);
        e.getPlayer().sendMessage("Now please click on button to add the action.");
    }

    @EventHandler
    public void actionAdd(EntityDamageByEntityEvent event) {
        if(!actionToAdd.containsKey(event.getDamager().getName())) return;
        Location location = event.getEntity().getLocation();
        ClickableImage image = Managers.getManager(ClickableImagesManager.class).getImage(location);
        Pair<Integer, Integer> coords = Managers.getManager(ClickableImagesManager.class).findGrid(image, location);
        String coordsString = coords.getFirst() + "+" + coords.getSecond();
        if(!image.isButton(coordsString)) {
            event.getDamager().sendMessage("&cButton not found!");
            event.setCancelled(true);
            return;
        }
        image.getButton(coordsString).getActions().add(actionToAdd.get(event.getDamager().getName()));
        image.save();
        event.getDamager().sendMessage("Action added!");
        actionToAdd.remove(event.getDamager().getName());
        event.setCancelled(true);
    }

    @EventHandler
    public void actionAdd(PlayerInteractEntityEvent event) {
        if(!actionToAdd.containsKey(event.getPlayer().getName())) return;
        Location location = event.getRightClicked().getLocation();
        ClickableImage image = Managers.getManager(ClickableImagesManager.class).getImage(location);
        Pair<Integer, Integer> coords = Managers.getManager(ClickableImagesManager.class).findGrid(image, location);
        String coordsString = coords.getFirst() + "+" + coords.getSecond();
        if(!image.isButton(coordsString)) {
            event.getPlayer().sendMessage("&cButton not found!");
            event.setCancelled(true);
            return;
        }
        image.getButton(coordsString).getActions().add(actionToAdd.get(event.getPlayer().getName()));
        image.save();
        event.getPlayer().sendMessage("Action added!");
        actionToAdd.remove(event.getPlayer().getName());
        event.setCancelled(true);
    }

    public void unregister() {
        HandlerList.unregisterAll(this);
    }
}
