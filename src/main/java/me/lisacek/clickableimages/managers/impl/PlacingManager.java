package me.lisacek.clickableimages.managers.impl;

import me.lisacek.clickableimages.cons.Asset;
import me.lisacek.clickableimages.listeners.PlaceListener;
import me.lisacek.clickableimages.managers.Manager;
import org.bukkit.Bukkit;
import org.bukkit.event.player.PlayerInteractAtEntityEvent;

import java.util.HashMap;

public class PlacingManager implements Manager {

    private final HashMap<String, Asset> assets = new HashMap<>();

    private final PlaceListener listener = new PlaceListener();

    @Override
    public void onEnable() {
        Bukkit.getPluginManager().registerEvents(listener, getPlugin());
    }

    @Override
    public void onDisable() {
        listener.unregister();
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
