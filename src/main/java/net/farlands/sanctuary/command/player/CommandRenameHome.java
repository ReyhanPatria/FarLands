package net.farlands.sanctuary.command.player;

import com.kicas.rp.command.TabCompleterBase;
import net.farlands.sanctuary.FarLands;
import net.farlands.sanctuary.command.Category;
import net.farlands.sanctuary.command.PlayerCommand;
import net.farlands.sanctuary.data.Rank;
import net.farlands.sanctuary.data.struct.Home;
import net.farlands.sanctuary.data.struct.OfflineFLPlayer;
import net.farlands.sanctuary.mechanic.Chat;
import net.farlands.sanctuary.util.ComponentColor;
import org.bukkit.Location;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.Collections;
import java.util.List;

public class CommandRenameHome extends PlayerCommand {
    public CommandRenameHome() {
        super(Rank.INITIATE, Category.HOMES, "Change the name of one of your homes.",
                "/renamehome <oldName> <newName>", "renamehome");
    }

    @Override
    protected boolean execute(Player sender, String[] args) {
        if (args.length < 2)
            return false;

        OfflineFLPlayer flp = FarLands.getDataHandler().getOfflineFLPlayer(sender);
        String oldName = args[0];
        String newName = args[1];

        if (!flp.hasHome(oldName)) {
            sender.sendMessage(ComponentColor.red("You do not have a home named %s.", oldName));
            return true;
        }

        // Make sure the home name is valid
        if (args[1].isEmpty() || args[1].matches("\\s+") || Chat.getMessageFilter().isProfane(newName)) {
            sender.sendMessage(ComponentColor.red("You cannot set a home with that name."));
            return true;
        }

        if (newName.length() > 32) {
            sender.sendMessage(ComponentColor.red("Home names are limited to 32 characters. Please choose a different name."));
            return true;
        }

        // Make sure the player doesn't already have a home with the new name
        if (flp.hasHome(newName)) {
            sender.sendMessage(ComponentColor.red("You have already set a home with this name."));
            return true;
        }

        flp.renameHome(oldName, newName);
        sender.sendMessage(
            ComponentColor.green("Successfully renamed home ")
                .append(ComponentColor.aqua(oldName))
                .append(ComponentColor.green(" to "))
                .append(ComponentColor.aqua(newName))
                .append(ComponentColor.green("."))
        );
        return true;
    }

    @Override
    public List<String> tabComplete(CommandSender sender, String alias, String[] args, Location location) throws IllegalArgumentException {
        return args.length <= 1
                ? TabCompleterBase.filterStartingWith(args[0], FarLands.getDataHandler().getOfflineFLPlayer(sender)
                .homes.stream().map(Home::getName)) : Collections.emptyList();
    }
}
