package me.lisacek.clickableimages.managers.impl;

import com.google.common.collect.Maps;
import me.lisacek.clickableimages.cons.PIterator;
import me.lisacek.clickableimages.managers.Manager;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;

public class PagesManager implements Manager {

    private final Map<String, List<PIterator>> actions = Maps.newHashMap();

    @Override
    public void onEnable() {

    }

    @Override
    public void onDisable() {

    }

    public void createPI(String player, String action) {
        if (actions.containsKey(action)) {
            actions.get(action).add(new PIterator(player));
        } else {
            List<PIterator> list = new ArrayList<>();
            list.add(new PIterator(player));
            actions.put(action, list);
        }
    }

    public PIterator getPI(String player) {
        List<PIterator> playerPIterators = actions.get(player);
        return playerPIterators.stream().filter(pIterator -> Objects.equals(pIterator.getPlayer(), player)).findFirst().orElse(null);
    }

    public int getPage(String player) {
        PIterator pIterator = getPI(player);
        return pIterator.getPage();
    }

    public void nextPage(String player) {
        PIterator pIterator = getPI(player);
        pIterator.nextPage();
    }

    public void prevPage(String player) {
        PIterator pIterator = getPI(player);
        pIterator.prevPage();
    }

}
