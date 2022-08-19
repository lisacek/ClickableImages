package me.lisacek.clickableimages.managers;

import me.lisacek.clickableimages.ClickableImages;

public interface Manager {

    void onEnable();

    void onDisable();

    default ClickableImages getPlugin() {
        return ClickableImages.getInstance();
    }

    default <T extends Manager> T getManager(Class<T> clazz) {
        return Managers.getManager(clazz);
    }

}
