package me.lisacek.clickableimages.managers.impl;

import me.lisacek.clickableimages.cons.ClickableImage;
import me.lisacek.clickableimages.enums.Action;
import me.lisacek.clickableimages.listeners.ButtonListener;
import me.lisacek.clickableimages.managers.Manager;
import org.bukkit.Bukkit;

import java.util.HashMap;
import java.util.Map;
public class ButtonManager implements Manager {

    private final Map<String, Action> queue = new HashMap<>();

    private final Map<String, ClickableImage> imagesQueue = new HashMap<>();
    private final ButtonListener listener = new ButtonListener();

    @Override
    public void onEnable() {
        Bukkit.getPluginManager().registerEvents(listener, getPlugin());
    }

    @Override
    public void onDisable() {
        listener.unregister();
    }

    public Map<String, Action> getQueue() {
        return queue;
    }

    public Map<String, ClickableImage> getImagesQueue() {
        return imagesQueue;
    }
}
