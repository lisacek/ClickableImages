package me.lisacek.clickableimages.commands;

import me.lisacek.clickableimages.ClickableImages;
import me.lisacek.clickableimages.cons.Asset;
import me.lisacek.clickableimages.managers.Managers;
import me.lisacek.clickableimages.managers.impl.AssetsManager;
import me.lisacek.clickableimages.managers.impl.DeleteManager;
import me.lisacek.clickableimages.managers.impl.PlacingManager;
import me.lisacek.clickableimages.utils.Colors;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.YamlConfiguration;
import org.jetbrains.annotations.NotNull;

public class ClickableImagesCommand implements CommandExecutor {

    private final ClickableImages plugin = ClickableImages.getInstance();
    private final YamlConfiguration config = plugin.getConfig();

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String s, @NotNull String[] args) {
        if (args.length == 0) {
            config.getStringList("messages.help").forEach(m -> {
                sender.sendMessage(Colors.translateColors(m));
            });
            return true;
        }

        switch (args[0].toLowerCase()) {
            case "asset":
                if (args.length < 2) {
                    sender.sendMessage(Colors.translateColors("&7/cimg &basset &c<asset>"));
                    return true;
                }
                Asset asset = Managers.getManager(AssetsManager.class).getAsset(args[1]);
                if (asset == null) {
                    sender.sendMessage(Colors.translateColors(config.getString("messages.asset-not-found")));
                    return true;
                }
                config.getStringList("messages.asset-info").forEach(m -> {
                    sender.sendMessage(Colors.translateColors(m)
                            .replace("%rows%", asset.getRows() + "")
                            .replace("%columns%", asset.getColumns() + ""));
                });
                return true;
            case "delete":
                Managers.getManager(DeleteManager.class).start();
                sender.sendMessage(Colors.translateColors(config.getString("messages.delete")));
                return true;
            case "place":
                if (args.length < 2) {
                    sender.sendMessage(Colors.translateColors("&7/cimg &bplace &c<asset>"));
                    return true;
                }
                asset = Managers.getManager(AssetsManager.class).getAsset(args[1]);
                if (asset == null) {
                    sender.sendMessage(Colors.translateColors(config.getString("messages.asset-not-found")));
                    return true;
                }
                Managers.getManager(PlacingManager.class).prepareToPlace(asset, sender.getName());
                sender.sendMessage(Colors.translateColors(config.getString("messages.place"))
                        .replace("%rows%", "" + asset.getRows())
                        .replace("%columns%", "" + asset.getColumns()));
                return true;
            case "reload":
                Managers.restart();
                sender.sendMessage(Colors.translateColors("&aPlugin was reloaded!"));
                return true;
            default:
                config.getStringList("messages.help").forEach(m -> {
                    sender.sendMessage(Colors.translateColors(m));
                });
                return true;
        }
    }

}
