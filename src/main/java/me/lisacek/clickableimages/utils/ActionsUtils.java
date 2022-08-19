package me.lisacek.clickableimages.utils;

import org.bukkit.entity.Player;

public class ActionsUtils {

    public static void runAction(Player p, String action) {
        if(!action.startsWith("[")) return;

        String[] args = action.split(" ");
        if(args.length < 2) return;

        String type = args[0].replace("[", "").replace("]", "").toUpperCase();
        String[] args2 = new String[args.length - 1];
        System.arraycopy(args, 1, args2, 0, args.length - 1);

        switch (type) {
            case "MSG":
                p.sendMessage(Colors.translateColors(String.join(" ", args2)
                        .replace("%player%", p.getName())));
                break;
            case "CMD":
                p.getServer().dispatchCommand(p.getServer().getConsoleSender(), String.join(" ", args2)
                        .replace("%player%", p.getName()));
                break;
        }
    }

}
