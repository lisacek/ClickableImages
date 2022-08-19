package me.lisacek.clickableimages.managers.impl;

import com.google.common.collect.Lists;
import me.lisacek.clickableimages.managers.Manager;
import org.bukkit.Bukkit;
import me.lisacek.clickableimages.listeners.DeleteListener;

import java.util.List;

public class DeleteManager implements Manager {

    private final DeleteListener listener = new DeleteListener();

    private final List<String> players = Lists.newArrayList();

    @Override
    public void onEnable() {

    }

    @Override
    public void onDisable() {

    }

    public void start(String player) {
        players.add(player);
        Bukkit.getPluginManager().registerEvents(listener, getPlugin());
    }

    public void stop(String player) {
        players.remove(player);
        if (players.isEmpty()) {
            listener.unregister();
        }
    }

    public boolean isRunning(String player) {
        return players.contains(player);
    }
}
