package me.lisacek.clickableimages.commands;

import me.lisacek.clickableimages.ClickableImages;
import me.lisacek.clickableimages.cons.Asset;
import me.lisacek.clickableimages.cons.ClickableImage;
import me.lisacek.clickableimages.managers.Managers;
import me.lisacek.clickableimages.managers.impl.AssetsManager;
import me.lisacek.clickableimages.managers.impl.ClickableImagesManager;
import me.lisacek.clickableimages.managers.impl.DeleteManager;
import me.lisacek.clickableimages.managers.impl.PlacingManager;
import me.lisacek.clickableimages.utils.Colors;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.ItemFrame;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

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

                long fileSizeInBytes = asset.getFile().length();
                long fileSizeInKB = fileSizeInBytes / 1024;

                List<String> grid = new ArrayList<>();
                for (int i = 0; i < asset.getRows(); i++) {
                    StringBuilder row = new StringBuilder();
                    for (int j = 0; j < asset.getColumns(); j++) {
                        row.append("|_");
                    }
                    row.append("|");
                    grid.add(row.toString());
                }

                config.getStringList("messages.asset-info").forEach(m -> {
                    if (m.contains("%grid%")) {
                        m = m.replace("%grid%", "")
                                .replace("%rows%", asset.getRows() + "")
                                .replace("%columns%", asset.getColumns() + "");
                        sender.sendMessage(Colors.translateColors(m));
                        grid.forEach(l -> {
                            sender.sendMessage(Colors.translateColors("      &e" + l));
                        });
                    } else {
                        sender.sendMessage(Colors.translateColors(m)
                                .replace("%rows%", asset.getRows() + "")
                                .replace("%columns%", asset.getColumns() + "")
                                .replace("%grid%", grid.toString())
                                .replace("%image%", asset.getFile().getName())
                                .replace("%fileSize%", fileSizeInKB + " KB")
                        );
                    }
                });
                return true;
            case "delete":
                if(Managers.getManager(DeleteManager.class).isRunning(sender.getName())) {
                    Managers.getManager(DeleteManager.class).stop(sender.getName());
                    sender.sendMessage(Colors.translateColors(config.getString("messages.delete-cancel")));
                    return true;
                }
                Managers.getManager(DeleteManager.class).start(sender.getName());
                sender.sendMessage(Colors.translateColors(config.getString("messages.delete")));
                return true;
            case "images":
                if (args.length < 2) {
                    sender.sendMessage(Colors.translateColors("&7/cimg &bimages &c<page>"));
                    return true;
                }
                int page = Integer.parseInt(args[1]);
                Collection<ClickableImage> images = Managers.getManager(ClickableImagesManager.class).getImages();
                if (images.isEmpty()) {
                    sender.sendMessage(Colors.translateColors(config.getString("messages.images-empty")));
                    return true;
                }

                //check if page is valid
                if (page == 0) {
                    sender.sendMessage(Colors.translateColors("&cInvalid page number!"));
                    return true;
                }
                if (page > (int) Math.ceil(images.size() / 10.0)) {
                    sender.sendMessage(Colors.translateColors(config.getString("messages.images-empty")));
                    return true;
                }

                sender.sendMessage(Colors.translateColors(config.getString("messages.images.header")));
                int i = 0;
                for (ClickableImage image : images) {
                    if (page > 1 && i < (page - 1) * 10) {
                        i++;
                        continue;
                    }
                    if (i == 10 * page) {
                        break;
                    }
                    sender.sendMessage(Colors.translateColors(config.getString("messages.images.image"))
                            .replace("%image%", image.getName())
                            .replace("%rows%", image.getRows() + "")
                            .replace("%columns%", image.getColumns() + "")
                            .replace("%asset%", image.getAsset().getFile().getName())
                            .replace("%location%", image.getGrid().get(0).get(0).getWorld().getName() + " " + image.getGrid().get(0).get(0).getBlockX() + " " + image.getGrid().get(0).get(0).getBlockY() + " " + image.getGrid().get(0).get(0).getBlockZ())
                    );
                    i++;
                }
                sender.sendMessage(Colors.translateColors(config.getString("messages.images.footer"))
                        .replace("%page%", page + "")
                        .replace("%max%", (int) Math.ceil(images.size() / 10.0) + "")
                );
                return true;
            case "assets":
                if (args.length < 2) {
                    sender.sendMessage(Colors.translateColors("&7/cimg &bassets &c<page>"));
                    return true;
                }
                page = Integer.parseInt(args[1]);
                Collection<Asset> assets = Managers.getManager(AssetsManager.class).getAssets();
                if (assets.isEmpty()) {
                    sender.sendMessage(Colors.translateColors(config.getString("messages.assets-empty")));
                    return true;
                }

                //check if page is valid
                if (page == 0) {
                    sender.sendMessage(Colors.translateColors("&cInvalid page number!"));
                    return true;
                }
                if (page > (int) Math.ceil(assets.size() / 10.0)) {
                    sender.sendMessage(Colors.translateColors(config.getString("messages.assets-empty")));
                    return true;
                }

                sender.sendMessage(Colors.translateColors(config.getString("messages.assets.header")));
                int j = 0;
                for (Asset a : assets) {
                    if (page > 1 && j < (page - 1) * 10) {
                        j++;
                        continue;
                    }
                    if (j == 10 * page) {
                        break;
                    }
                    sender.sendMessage(Colors.translateColors(config.getString("messages.assets.asset"))
                            .replace("%name%", a.getFile().getName())
                            .replace("%rows%", a.getRows() + "")
                            .replace("%columns%", a.getColumns() + "")
                    );
                    j++;
                }
                sender.sendMessage(Colors.translateColors(config.getString("messages.assets.footer"))
                        .replace("%page%", page + "")
                        .replace("%max%", (int) Math.ceil(assets.size() / 10.0) + ""));
                return true;
            case "place":
                if(Managers.getManager(PlacingManager.class).getAsset(sender.getName()) != null) {
                    Managers.getManager(PlacingManager.class).removeAsset(sender.getName());
                    sender.sendMessage(Colors.translateColors(config.getString("messages.place-cancel")));
                    return true;
                }
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
            case "info":
                if (!(sender instanceof Player)) return true;
                Player player = (Player) sender;
                Collection<ItemFrame> itemFrames = player.getWorld().getNearbyEntitiesByType(ItemFrame.class, player.getLocation(), 1);
                if (itemFrames.size() == 0) {
                    player.sendMessage(Colors.translateColors(config.getString("messages.no-item-frame")));
                    return true;
                }
                ItemFrame itemFrame = itemFrames.iterator().next();
                ClickableImage clickableImage = Managers.getManager(ClickableImagesManager.class).getImage(itemFrame.getLocation());
                if (clickableImage == null) {
                    player.sendMessage(Colors.translateColors(config.getString("messages.no-item-frame")));
                    return true;
                }

                config.getStringList("messages.image-info").forEach(m -> {
                    if (m.contains("%actions%")) {
                        clickableImage.getActions().forEach(a -> {
                            player.sendMessage(Colors.translateColors("&7- &e" + a));
                        });
                    } else {
                        player.sendMessage(Colors.translateColors(m)
                                .replace("%rows%", clickableImage.getRows() + "")
                                .replace("%file%", clickableImage.getName())
                                .replace("%columns%", clickableImage.getColumns() + ""));
                    }
                });
                return true;
            case "action":
                if (args.length < 2) {
                    sender.sendMessage(Colors.translateColors("&7/cimg &baction &c<action>"));
                    return true;
                }
                switch (args[1].toLowerCase()) {
                    case "add":
                        if (args.length < 3) {
                            sender.sendMessage(Colors.translateColors("&7/cimg &baction &cadd <image> <action>"));
                            return true;
                        }
                        ClickableImage image = Managers.getManager(ClickableImagesManager.class).getImage(args[2]);
                        if (image == null) {
                            sender.sendMessage(Colors.translateColors(config.getString("messages.image-not-found")));
                            return true;
                        }
                        Managers.getManager(ClickableImagesManager.class).addAction(image, String.join(" ", args).substring(11)
                                .replace(args[2] + " ", ""));
                        sender.sendMessage(Colors.translateColors(config.getString("messages.action-added")));
                        return true;
                    case "remove":
                        if (args.length < 3) {
                            sender.sendMessage(Colors.translateColors("&7/cimg &baction &cremove &c<action>"));
                            return true;
                        }
                        image = Managers.getManager(ClickableImagesManager.class).getImage(args[2]);
                        if (image == null) {
                            sender.sendMessage(Colors.translateColors(config.getString("messages.image-not-found")));
                            return true;
                        }
                        int index = Integer.parseInt(args[3]);
                        if (image.getActions().size() - 1 < index) {
                            sender.sendMessage(Colors.translateColors(config.getString("messages.action-not-found")));
                            return true;
                        }
                        Managers.getManager(ClickableImagesManager.class).removeAction(image, index);
                        sender.sendMessage(Colors.translateColors(config.getString("messages.action-removed")));
                        return true;
                    default:
                        sender.sendMessage(Colors.translateColors("&7/cimg &baction &c<add|remove>"));
                        return true;
                }
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
