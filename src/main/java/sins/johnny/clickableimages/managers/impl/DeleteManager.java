package sins.johnny.clickableimages.managers.impl;

import org.bukkit.Bukkit;
import sins.johnny.clickableimages.listeners.DeleteListener;
import sins.johnny.clickableimages.listeners.PlaceListener;
import sins.johnny.clickableimages.managers.Manager;

public class DeleteManager implements Manager {

    @Override
    public void onEnable() {
        Bukkit.getPluginManager().registerEvents(new DeleteListener(), getPlugin());
    }

    @Override
    public void onDisable() {

    }
}
