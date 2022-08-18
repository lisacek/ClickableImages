package sins.johnny.clickableimages.listeners;

import org.bukkit.Rotation;
import org.bukkit.entity.Entity;
import org.bukkit.entity.ItemFrame;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerInteractAtEntityEvent;
import sins.johnny.clickableimages.cons.Asset;
import sins.johnny.clickableimages.cons.NodeList;
import sins.johnny.clickableimages.cons.Pair;
import sins.johnny.clickableimages.managers.Managers;
import sins.johnny.clickableimages.managers.impl.ClickableImagesManager;
import sins.johnny.clickableimages.managers.impl.PlacingManager;

public class PlaceListener implements Listener {

    @EventHandler(priority = EventPriority.MONITOR)
    public void onPlace(PlayerInteractAtEntityEvent e) {
        Entity entity = e.getRightClicked();
        if (entity == null) {
            return;
        }
        if (!(entity instanceof ItemFrame)) {
            return;
        }

        PlacingManager placingManager = Managers.getManager(PlacingManager.class);
        Asset asset = placingManager.getAsset(e.getPlayer().getName());
        if (asset == null) {
            return;
        }
        Pair<Boolean, NodeList> pair = asset.canPlace((ItemFrame) entity);

        if (!pair.getFirst()) {
            e.getPlayer().sendMessage("Cannot place asset here!");
            return;
        }
        asset.place(pair.getSecond(), e.getPlayer(), (ItemFrame) entity);
        placingManager.removeAsset(e.getPlayer().getName());
        Managers.getManager(ClickableImagesManager.class).initMaps();
        e.setCancelled(true);
    }

}
