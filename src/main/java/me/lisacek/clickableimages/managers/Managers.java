package me.lisacek.clickableimages.managers;

import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class Managers {

    private static final List<Manager> managers = new ArrayList<>();

    public static void register(Class<? extends Manager> clazz) {
        try {
            Manager manager = clazz.getDeclaredConstructor().newInstance();
            manager.onEnable();
            managers.add(manager);
        } catch (InstantiationException | IllegalAccessException | InvocationTargetException | NoSuchMethodException e) {
            e.printStackTrace();
        }
    }

    public static void stop() {
        managers.forEach(Manager::onDisable);
    }

    public static void restart() {
        managers.forEach(Manager::onDisable);
        managers.forEach(Manager::onEnable);
    }

    public static <T extends Manager> T getManager(Class<T> clazz) {
        Optional<Manager> opt = managers.stream().filter(manager -> manager.getClass().equals(clazz)).findFirst();
        if (opt.isPresent()) {
            return clazz.cast(opt.get());
        }

        throw new IllegalStateException("Manager " + clazz.getName() + " not found");
    }

}
