package sins.johnny.clickableimages.cons;

import org.bukkit.entity.Player;
import org.bukkit.map.MapCanvas;
import org.bukkit.map.MapRenderer;
import org.bukkit.map.MapView;

import java.awt.image.BufferedImage;

public class Renderer extends MapRenderer {
    private BufferedImage image;

    protected Renderer() {
        this(null);
    }

    protected Renderer(BufferedImage image) {
        this.image = image;
    }

    public static Renderer installRenderer(MapView map) {
        Renderer renderer = new Renderer();
        removeRenderers(map);
        map.addRenderer(renderer);
        return renderer;
    }

    public static void removeRenderers(MapView map) {
        for (MapRenderer renderer : map.getRenderers()) {
            map.removeRenderer(renderer);
        }
    }

    @Override
    public void render(MapView v, final MapCanvas canvas, Player p) {
        if (image == null) {
            return;
        }
        canvas.drawImage(0, 0, image);
        image = null;
    }

    public BufferedImage getImage() {
        return image;
    }

    public void setImage(BufferedImage image) {
        this.image = image;
    }
}
