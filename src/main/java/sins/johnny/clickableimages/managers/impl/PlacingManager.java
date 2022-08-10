package sins.johnny.clickableimages.managers.impl;

import org.bukkit.Bukkit;
import sins.johnny.clickableimages.cons.Asset;
import sins.johnny.clickableimages.listeners.PlaceListener;
import sins.johnny.clickableimages.managers.Manager;

import java.util.HashMap;

public class PlacingManager implements Manager {

    private final HashMap<String, Asset> assets = new HashMap<>();

    @Override
    public void onEnable() {
        Bukkit.getPluginManager().registerEvents(new PlaceListener(), getPlugin());
    }

    @Override
    public void onDisable() {

    }

    public void prepareToPlace(Asset asset, String name) {
        assets.remove(name);
        assets.put(name, asset);
    }

    public Asset getAsset(String name) {
        return assets.getOrDefault(name, null);
    }

    public void removeAsset(String name) {
        assets.remove(name);
    }

}
