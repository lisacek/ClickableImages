package me.lisacek.clickableimages.managers.impl;

import me.lisacek.clickableimages.managers.Manager;
import org.bukkit.Bukkit;
import me.lisacek.clickableimages.listeners.DeleteListener;

public class DeleteManager implements Manager {

    @Override
    public void onEnable() {

    }

    @Override
    public void onDisable() {

    }

    public void start() {
        Bukkit.getPluginManager().registerEvents(new DeleteListener(), getPlugin());
    }
}
