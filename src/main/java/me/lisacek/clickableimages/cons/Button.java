package me.lisacek.clickableimages.cons;

import java.util.List;

public class Button {

    private final String coords;

    private String permission;

    private final List<String> actions;

    public Button(String coords, List<String> actions, String permission) {
        this.coords = coords;
        this.permission = permission;
        this.actions = actions;
    }

    public String getCoords() {
        return coords;
    }

    public String getPermission() {
        return permission;
    }

    public void setPermission(String permission) {
        this.permission = permission;
    }

    public List<String> getActions() {
        return actions;
    }
}
