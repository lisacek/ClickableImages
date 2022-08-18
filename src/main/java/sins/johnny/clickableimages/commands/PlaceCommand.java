package sins.johnny.clickableimages.commands;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import sins.johnny.clickableimages.cons.Asset;
import sins.johnny.clickableimages.managers.Managers;
import sins.johnny.clickableimages.managers.impl.AssetsManager;
import sins.johnny.clickableimages.managers.impl.PlacingManager;

public class PlaceCommand implements CommandExecutor {

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if(args.length != 1) {
            sender.sendMessage("Usage: /place <image>");
            return true;
        }

        Asset asset = Managers.getManager(AssetsManager.class).getAsset(args[0]);
        if(asset == null) {
            sender.sendMessage("Asset not found!");
            return true;
        }

        Managers.getManager(PlacingManager.class).prepareToPlace(asset, sender.getName());
        sender.sendMessage("Place the asset where you want it to be! (" + asset.getRows() + "x" + asset.getColumns() + ")");
        return true;
    }

}
