package sins.johnny.clickableimages.commands;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.jetbrains.annotations.NotNull;
import sins.johnny.clickableimages.cons.Asset;
import sins.johnny.clickableimages.managers.Managers;
import sins.johnny.clickableimages.managers.impl.AssetsManager;

public class AssetCommand implements CommandExecutor {

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String s, @NotNull String[] args) {
        if(args.length == 0) {
            sender.sendMessage("Usage: /asset <image>");
            return true;
        }
        Asset asset = Managers.getManager(AssetsManager.class).getAsset(args[0]);
        sender.sendMessage("Size: " + asset.getRows() + "x" + asset.getColumns());
        return true;
    }
}
