package sins.johnny.clickableimages.managers.impl;

import sins.johnny.clickableimages.managers.Manager;

public class ConfigurationsManager implements Manager {

    @Override
    public void onEnable() {
        getPlugin().getDataFolder().mkdir();
    }

    @Override
    public void onDisable() {

    }

}
