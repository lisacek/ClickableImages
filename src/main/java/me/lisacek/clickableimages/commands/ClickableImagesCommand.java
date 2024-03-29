package me.lisacek.clickableimages.commands;

import me.lisacek.clickableimages.ClickableImages;
import me.lisacek.clickableimages.cons.Asset;
import me.lisacek.clickableimages.cons.Button;
import me.lisacek.clickableimages.cons.ClickableImage;
import me.lisacek.clickableimages.enums.Action;
import me.lisacek.clickableimages.managers.Managers;
import me.lisacek.clickableimages.managers.impl.*;
import me.lisacek.clickableimages.utils.CPUDaemon;
import me.lisacek.clickableimages.utils.Colors;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabExecutor;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.ItemFrame;
import org.bukkit.entity.Player;
import org.bukkit.util.StringUtil;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.text.DecimalFormat;
import java.util.*;

public class ClickableImagesCommand implements CommandExecutor, TabExecutor {

    private final ClickableImages plugin = ClickableImages.getInstance();
    private final YamlConfiguration config = plugin.getConfig();

    private final DecimalFormat df = new DecimalFormat("#.##");

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
                if (!sender.hasPermission("clickableimages.asset") && !sender.hasPermission("clickableimages.admin")) {
                    sender.sendMessage(Colors.translateColors(config.getString("messages.no-permission")));
                    return true;
                }
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
                        row.append("☐");
                    }
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
                if (!sender.hasPermission("clickableimages.delete") && !sender.hasPermission("clickableimages.admin")) {
                    sender.sendMessage(Colors.translateColors(config.getString("messages.no-permission")));
                    return true;
                }
                if (Managers.getManager(DeleteManager.class).isRunning(sender.getName())) {
                    Managers.getManager(DeleteManager.class).stop(sender.getName());
                    sender.sendMessage(Colors.translateColors(config.getString("messages.delete-cancel")));
                    return true;
                }
                Managers.getManager(DeleteManager.class).start(sender.getName());
                sender.sendMessage(Colors.translateColors(config.getString("messages.delete")));
                return true;
            case "images":
                if (!sender.hasPermission("clickableimages.images") && !sender.hasPermission("clickableimages.admin")) {
                    sender.sendMessage(Colors.translateColors(config.getString("messages.no-permission")));
                    return true;
                }
                if (args.length < 2) {
                    String[] newArgs = new String[args.length + 1];
                    System.arraycopy(args, 0, newArgs, 0, args.length);
                    newArgs[args.length] = "1";
                    args = newArgs;
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
                if (!sender.hasPermission("clickableimages.assets") && !sender.hasPermission("clickableimages.admin")) {
                    sender.sendMessage(Colors.translateColors(config.getString("messages.no-permission")));
                    return true;
                }
                if (args.length < 2) {
                    String[] newArgs = new String[args.length + 1];
                    System.arraycopy(args, 0, newArgs, 0, args.length);
                    newArgs[args.length] = "1";
                    args = newArgs;
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
                if (!sender.hasPermission("clickableimages.place") && !sender.hasPermission("clickableimages.admin")) {
                    sender.sendMessage(Colors.translateColors(config.getString("messages.no-permission")));
                    return true;
                }
                if (Managers.getManager(PlacingManager.class).getAsset(sender.getName()) != null) {
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
                if (args.length < 2) {
                    if (!sender.hasPermission("clickableimages.info") && !sender.hasPermission("clickableimages.admin")) {
                        sender.sendMessage(Colors.translateColors(config.getString("messages.no-permission")));
                        return true;
                    }
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

                    buildInfo(player, clickableImage);
                } else {
                    String imageName = args[1];
                    ClickableImage clickableImage = Managers.getManager(ClickableImagesManager.class).getImage(imageName);
                    if (clickableImage == null) {
                        sender.sendMessage(Colors.translateColors(config.getString("messages.image-not-found")));
                        return true;
                    }
                    buildInfo(sender, clickableImage);
                }
                return true;
            case "status":
                if (!sender.hasPermission("clickableimages.status") && !sender.hasPermission("clickableimages.admin")) {
                    sender.sendMessage(Colors.translateColors(config.getString("messages.no-permission")));
                    return true;
                }
                config.getStringList("messages.status").forEach(l -> {
                    sender.sendMessage(Colors.translateColors(
                            l.replace("%images%", Managers.getManager(ClickableImagesManager.class).getImages().size() + "")
                                    .replace("%assets%", Managers.getManager(AssetsManager.class).getAssets().size() + "")
                                    .replace("%frames%", Managers.getManager(ClickableImagesManager.class).getTotalLocations() + "")
                                    .replace("%cpu%", df.format(CPUDaemon.get()) + "")));
                });
                return true;
            case "action":
                if (!sender.hasPermission("clickableimages.action") && !sender.hasPermission("clickableimages.admin")) {
                    sender.sendMessage(Colors.translateColors(config.getString("messages.no-permission")));
                    return true;
                }
                if (args.length < 2) {
                    sender.sendMessage(Colors.translateColors("&7/cimg &baction &c<action>"));
                    return true;
                }
                switch (args[1].toLowerCase()) {
                    case "add":
                        if (args.length < 4) {
                            sender.sendMessage(Colors.translateColors("&7/cimg &baction &cadd <image> <action>"));
                            return true;
                        }
                        if (Managers.getManager(DeleteManager.class).isRunning(sender.getName())) {
                            Managers.getManager(DeleteManager.class).stop(sender.getName());
                            sender.sendMessage(Colors.translateColors(config.getString("messages.delete-cancel")));
                            return true;
                        }
                        ClickableImage image = Managers.getManager(ClickableImagesManager.class).getImage(args[2]);
                        if (image == null) {
                            sender.sendMessage(Colors.translateColors(config.getString("messages.image-not-found")));
                            return true;
                        }
                        String action = String.join(" ", args).substring(11)
                                .replace(args[2] + " ", "");

                        String type = action.split(" ")[0];
                        switch (type.toUpperCase()) {
                            case "[PERM]":
                                image.setPermission(action.replace("[PERM] ", ""));
                                break;
                            case "[MSG]":
                            case "[CMD]":
                                image.getActions().add(action);
                                break;
                            default:
                                sender.sendMessage(Colors.translateColors(config.getString("messages.action-not-found")));
                                return true;
                        }
                        image.save();
                        sender.sendMessage(Colors.translateColors(config.getString("messages.action-added")));
                        return true;
                    case "remove":
                        if (args.length < 4) {
                            sender.sendMessage(Colors.translateColors("&7/cimg &baction &cremove &c<action>"));
                            return true;
                        }
                        image = Managers.getManager(ClickableImagesManager.class).getImage(args[2]);
                        if (image == null) {
                            sender.sendMessage(Colors.translateColors(config.getString("messages.image-not-found")));
                            return true;
                        }

                        if (args[3].equalsIgnoreCase("[PERM]")) {
                            image.setPermission("none");
                        } else {
                            try {
                                Integer.parseInt(args[3]);
                            } catch (NumberFormatException e) {
                                sender.sendMessage(Colors.translateColors(args[3] + " is not a number!"));
                                return true;
                            }
                            int index = Integer.parseInt(args[3]);
                            if (image.getActions().size() - 1 < index) {
                                sender.sendMessage(Colors.translateColors(config.getString("messages.action-not-found")));
                                return true;
                            }
                            Managers.getManager(ClickableImagesManager.class).removeAction(image, index);
                        }
                        sender.sendMessage(Colors.translateColors(config.getString("messages.action-removed")));
                        return true;
                    default:
                        sender.sendMessage(Colors.translateColors("&7/cimg &baction &c<add|remove>"));
                        return true;
                }
            case "reload":
                if (!sender.hasPermission("clickableimages.reload") && !sender.hasPermission("clickableimages.admin")) {
                    sender.sendMessage(Colors.translateColors(config.getString("messages.no-permission")));
                    return true;
                }
                Managers.restart();
                sender.sendMessage(Colors.translateColors("&aPlugin was reloaded!"));
                return true;
            case "button":
                if (!sender.hasPermission("clickableimages.button") && !sender.hasPermission("clickableimages.admin")) {
                    sender.sendMessage(Colors.translateColors(config.getString("messages.no-permission")));
                    return true;
                }
                if (args.length < 2) {
                    sender.sendMessage(Colors.translateColors("&7/cimg &bbutton &c<add|remove|action> [add|remove]"));
                    return true;
                }
                switch (args[1].toLowerCase()) {
                    case "add":
                        if (Managers.getManager(ButtonManager.class).getQueue().containsKey(sender.getName()) && Managers.getManager(ButtonManager.class).getQueue().get(sender.getName()) == Action.ADD) {
                            Managers.getManager(ButtonManager.class).getQueue().remove(sender.getName());
                            sender.sendMessage(Colors.translateColors(config.getString("messages.button.cancel")));
                            return true;
                        }
                        Managers.getManager(ButtonManager.class).getQueue().put(sender.getName(), Action.ADD);
                        sender.sendMessage(Colors.translateColors(config.getString("messages.button.add")));
                        return true;
                    case "remove":
                        if (Managers.getManager(ButtonManager.class).getQueue().containsKey(sender.getName()) && Managers.getManager(ButtonManager.class).getQueue().get(sender.getName()) == Action.REMOVE) {
                            Managers.getManager(ButtonManager.class).getQueue().remove(sender.getName());
                            sender.sendMessage(Colors.translateColors(config.getString("messages.button.cancel")));
                            return true;
                        }
                        Managers.getManager(ButtonManager.class).getQueue().put(sender.getName(), Action.REMOVE);
                        sender.sendMessage(Colors.translateColors(config.getString("messages.button.remove")));
                        return true;
                    case "edit":
                        if (Managers.getManager(ButtonManager.class).getQueue().containsKey(sender.getName()) && Managers.getManager(ButtonManager.class).getQueue().get(sender.getName()) == Action.EDIT) {
                            Managers.getManager(ButtonManager.class).getQueue().remove(sender.getName());
                            sender.sendMessage(Colors.translateColors(config.getString("messages.button.cancel")));
                            return true;
                        }
                        if(args.length < 3) {
                            sender.sendMessage(Colors.translateColors("&7/cimg &bbutton &cedit &c<image>"));
                            return true;
                        }
                        ClickableImage image = Managers.getManager(ClickableImagesManager.class).getImage(args[2]);
                        if (image == null) {
                            sender.sendMessage(Colors.translateColors(config.getString("messages.image-not-found")));
                            return true;
                        }
                        Managers.getManager(ButtonManager.class).getQueue().put(sender.getName(), Action.EDIT);
                        Managers.getManager(ButtonManager.class).getImagesQueue().put(sender.getName(), image);
                        sender.sendMessage(Colors.translateColors(config.getString("messages.button.edit")));
                        return true;
                    case "action":
                        if (args.length < 3) {
                            sender.sendMessage(Colors.translateColors("&7/cimg &bbutton &caction &c<add|remove> <image> <coords> <action>"));
                            return true;
                        }
                        switch (args[2].toLowerCase()) {
                            case "add":
                                if (args.length < 5) {
                                    sender.sendMessage(Colors.translateColors("&7/cimg &bbutton &caction &cadd &c<image> <coords> <action>"));
                                    return true;
                                }
                                image = Managers.getManager(ClickableImagesManager.class).getImage(args[3]);
                                if (image == null) {
                                    sender.sendMessage(Colors.translateColors(config.getString("messages.image-not-found")));
                                    return true;
                                }
                                String coords = args[4];
                                if(image.getButton(coords) == null) {
                                    image.getButtons().add(new Button(coords, new ArrayList<>(), "none"));
                                }
                                StringBuilder builder = new StringBuilder();
                                for (int y = 5; y < args.length; y++) {
                                    builder.append(args[y]).append(" ");
                                }
                                String action = builder.toString();
                                if (!action.startsWith("[CMD] ") && !action.startsWith("[MSG] ")) {
                                    sender.sendMessage(Colors.translateColors(config.getString("messages.button.invalid-action")));
                                    return true;
                                }
                                image.getButton(coords).getActions().add(action);
                                image.save();
                                sender.sendMessage(Colors.translateColors("&aAction added!"));
                                return true;
                            case "remove":
                                if (args.length < 5) {
                                    sender.sendMessage(Colors.translateColors("&7/cimg &bbutton &caction &cremove &c<image> <coords> <index>"));
                                    return true;
                                }
                                image = Managers.getManager(ClickableImagesManager.class).getImage(args[3]);
                                if (image == null) {
                                    sender.sendMessage(Colors.translateColors(config.getString("messages.image-not-found")));
                                    return true;
                                }
                                coords = args[4];
                                if(image.getButton(coords) == null) {
                                    sender.sendMessage(Colors.translateColors(config.getString("messages.button.not-found")));
                                    return true;
                                }
                                try {
                                    Integer.parseInt(args[5]);
                                } catch (NumberFormatException e) {
                                    sender.sendMessage(Colors.translateColors("&cInvalid number " + args[5] + "!"));
                                    return true;
                                }
                                int index = Integer.parseInt(args[5]);
                                image.getButton(coords).getActions().remove(index);
                                image.save();
                                sender.sendMessage(Colors.translateColors(config.getString("messages.button.invalid-action")));
                                return true;
                            default:
                                sender.sendMessage(Colors.translateColors("&7/cimg &bbutton &caction &c<add|remove> <image> <coords> <action|index>"));
                                return true;
                        }
                }
            default:
                if (!sender.hasPermission("clickableimages.help") && !sender.hasPermission("clickableimages.admin")) {
                    sender.sendMessage(Colors.translateColors(config.getString("messages.no-permission")));
                    return true;
                }
                config.getStringList("messages.help").forEach(m -> {
                    sender.sendMessage(Colors.translateColors(m));
                });
                return true;
        }
    }

    private void buildInfo(@NotNull CommandSender sender, ClickableImage clickableImage) {
        config.getStringList("messages.image-info").forEach(m -> {
            if (m.contains("%actions%")) {
                clickableImage.getActions().forEach(a -> {
                    sender.sendMessage(Colors.translateColors("&7- &e" + a));
                });
            } else {
                sender.sendMessage(Colors.translateColors(m)
                        .replace("%rows%", clickableImage.getRows() + "")
                        .replace("%file%", clickableImage.getName())
                        .replace("%permission%", clickableImage.getPermission())
                        .replace("%columns%", clickableImage.getColumns() + ""));
            }
        });
    }


    @Override
    public @Nullable List<String> onTabComplete(@NotNull CommandSender sender, @NotNull Command command, @NotNull String s, @NotNull String[] args) {
        List<String> data = new ArrayList<>();
        data.add("place");
        data.add("delete");
        data.add("button");
        data.add("asset");
        data.add("images");
        data.add("assets");
        data.add("status");
        data.add("action");
        data.add("info");
        data.add("help");
        data.add("reload");
        List<String> finalData = new ArrayList<>();
        if (args.length == 1) {
            StringUtil.copyPartialMatches(args[0], data, finalData);
            Collections.sort(finalData);
            return finalData;
        }
        if (args.length == 2) {
            switch (args[0]) {
                case "action":
                    data.clear();
                    data.add("add");
                    data.add("remove");
                    StringUtil.copyPartialMatches(args[1], data, finalData);
                    Collections.sort(finalData);
                    return finalData;
                case "place":
                case "asset":
                    data.clear();
                    Managers.getManager(AssetsManager.class).getAssets().forEach(a -> {
                        data.add(a.getFile().getName());
                    });
                    StringUtil.copyPartialMatches(args[1], data, finalData);
                    Collections.sort(finalData);
                    return finalData;
                case "images":
                    data.clear();
                    int size = Managers.getManager(ClickableImagesManager.class).getImages().size();
                    int pages = size / 10;
                    for (int i = 0; i <= pages; i++) {
                        data.add((i + 1) + "");
                    }
                    return data;
                case "info":
                    data.clear();
                    Managers.getManager(ClickableImagesManager.class).getImages().forEach(i -> {
                        data.add(i.getName());
                    });
                    StringUtil.copyPartialMatches(args[1], data, finalData);
                    Collections.sort(finalData);
                    return finalData;
                case "button":
                    data.clear();
                    data.add("add");
                    data.add("remove");
                    data.add("edit");
                    data.add("action");
                    StringUtil.copyPartialMatches(args[1], data, finalData);
                    Collections.sort(finalData);
                    return finalData;
                case "assets":
                    data.clear();
                    size = Managers.getManager(AssetsManager.class).getAssets().size();
                    pages = size / 10;
                    for (int i = 0; i <= pages; i++) {
                        data.add((i + 1) + "");
                    }
                    return data;
                default:
                    return Collections.emptyList();
            }
        }
        if (args.length == 3) {
            if ("action".equals(args[0])) {
                data.clear();
                Player player = (Player) sender;
                Collection<ItemFrame> frames = player.getLocation().getNearbyEntitiesByType(ItemFrame.class, 2);
                ClickableImage nearest = null;
                for (ItemFrame frame : frames) {
                    ClickableImage image = Managers.getManager(ClickableImagesManager.class).getImage(frame.getLocation());
                    if (image != null) {
                        nearest = image;
                        break;
                    }
                }

                if (nearest != null) {
                    data.add(nearest.getName());
                }
                return data;
            }
            if ("action".equals(args[1])) {
                data.clear();
                data.add("add");
                data.add("remove");
                StringUtil.copyPartialMatches(args[2], data, finalData);
                Collections.sort(finalData);
                return data;
            }

            if ("button".equals(args[0]) && "edit".equals(args[1])) {
                data.clear();
                Player player = (Player) sender;
                Collection<ItemFrame> frames = player.getLocation().getNearbyEntitiesByType(ItemFrame.class, 2);
                ClickableImage nearest = null;
                for (ItemFrame frame : frames) {
                    ClickableImage image = Managers.getManager(ClickableImagesManager.class).getImage(frame.getLocation());
                    if (image != null) {
                        nearest = image;
                        break;
                    }
                }

                if (nearest != null) {
                    data.add(nearest.getName());
                }
                return data;
            }
            return Collections.emptyList();
        }
        if (args.length == 4) {
            if ("action".equals(args[0]) && "add".equals(args[1])) {
                data.clear();
                data.add("[PERM]");
                data.add("[MSG]");
                data.add("[CMD]");
                StringUtil.copyPartialMatches(args[3], data, finalData);
                Collections.sort(finalData);
                return finalData;
            }
            if ("action".equals(args[0]) && "remove".equals(args[1])) {
                data.clear();
                data.add("[PERM]");
                ClickableImage ci = Managers.getManager(ClickableImagesManager.class).getImages().stream().filter(i -> Objects.equals(i.getName(), args[2])).findFirst().orElse(null);
                if (ci != null) {
                    for (int i = 0; i < ci.getActions().size(); i++) {
                        data.add(i + "");
                    }
                }
                StringUtil.copyPartialMatches(args[3], data, finalData);
                Collections.sort(finalData);
                return finalData;
            }
            if ("action".equals(args[1]) && "add".equals(args[2])) {
                data.clear();
                Player player = (Player) sender;
                Collection<ItemFrame> frames = player.getLocation().getNearbyEntitiesByType(ItemFrame.class, 2);
                ClickableImage nearest = null;
                for (ItemFrame frame : frames) {
                    ClickableImage image = Managers.getManager(ClickableImagesManager.class).getImage(frame.getLocation());
                    if (image != null) {
                        nearest = image;
                        break;
                    }
                }

                if (nearest != null) {
                    data.add(nearest.getName());
                }
                return data;
            }
            if ("action".equals(args[1]) && "remove".equals(args[2])) {
                data.clear();
                Player player = (Player) sender;
                Collection<ItemFrame> frames = player.getLocation().getNearbyEntitiesByType(ItemFrame.class, 2);
                ClickableImage nearest = null;
                for (ItemFrame frame : frames) {
                    ClickableImage image = Managers.getManager(ClickableImagesManager.class).getImage(frame.getLocation());
                    if (image != null) {
                        nearest = image;
                        break;
                    }
                }

                if (nearest != null) {
                    data.add(nearest.getName());
                }
                return data;
            }
            return Collections.emptyList();
        }
        if(args.length == 5) {
            if ("action".equals(args[1]) && "add".equals(args[2])) {
                finalData = new ArrayList<>();
                ClickableImage image = Managers.getManager(ClickableImagesManager.class).getImage(args[3]);
                List<String> coords = new ArrayList<>();
                if (image != null) {
                    for (int x = 0; x < image.getGrid().size(); x++) {
                        for (int y = 0; y < image.getGrid().get(x).size(); y++) {
                            coords.add(x + "+" + y);
                        }
                    }
                }
                StringUtil.copyPartialMatches(args[4], coords, finalData);
                Collections.sort(finalData);
                return finalData;
            }
            return Collections.emptyList();
        }
        if(args.length == 6) {
            if ("action".equals(args[1]) && "add".equals(args[2])) {
                data.clear();
                data.add("[PERM]");
                data.add("[MSG]");
                data.add("[CMD]");
                StringUtil.copyPartialMatches(args[5], data, finalData);
                Collections.sort(finalData);
                return finalData;
            }
            return Collections.emptyList();
        }
        return Collections.emptyList();
    }
}
