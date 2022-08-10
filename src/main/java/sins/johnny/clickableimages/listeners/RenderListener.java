package sins.johnny.clickableimages.listeners;

import org.bukkit.Bukkit;
import org.bukkit.entity.Entity;
import org.bukkit.entity.ItemFrame;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.world.ChunkLoadEvent;
import org.bukkit.map.MapView;
import sins.johnny.clickableimages.cons.Renderer;
import sins.johnny.clickableimages.cons.ClickableImage;
import sins.johnny.clickableimages.cons.Pair;
import sins.johnny.clickableimages.managers.Managers;
import sins.johnny.clickableimages.managers.impl.ClickableImagesManager;
import sins.johnny.clickableimages.utils.ItemUtils;

import java.awt.image.BufferedImage;

public class RenderListener implements Listener {

    @EventHandler
    public void onChunkLoad(ChunkLoadEvent event) {
        for (Entity entity : event.getChunk().getEntities()) {
            if (entity instanceof ItemFrame) {
                ItemFrame item = (ItemFrame) entity;
                if(item.isEmpty()) continue;

                ClickableImage image = Managers.getManager(ClickableImagesManager.class).getImage(item.getLocation());
                Pair<Integer, Integer> axis = image.getGridLocation(item.getLocation());
                if(axis == null) continue;

                BufferedImage bfIm = image.getAsset().getImage(axis.getFirst(), axis.getSecond());
                MapView map = Bukkit.getServer().getMap(ItemUtils.getMapIdFromItemStack(item.getItem()));

                map.getRenderers().clear();

                Renderer renderer = Renderer.installRenderer(map);
                renderer.setImage(bfIm);
                map.addRenderer(renderer);
            }
        }
    }

}
