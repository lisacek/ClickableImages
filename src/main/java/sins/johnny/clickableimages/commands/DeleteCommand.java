package sins.johnny.clickableimages.commands;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.jetbrains.annotations.NotNull;
import sins.johnny.clickableimages.managers.Managers;
import sins.johnny.clickableimages.managers.impl.DeleteManager;
import sins.johnny.clickableimages.managers.impl.PlacingManager;

public class DeleteCommand implements CommandExecutor {

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String s, @NotNull String[] strings) {
        Managers.getManager(DeleteManager.class).start();
        sender.sendMessage("Click image to delete it!");
        return true;
    }

}
