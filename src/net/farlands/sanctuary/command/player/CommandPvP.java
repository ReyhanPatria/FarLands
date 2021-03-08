package net.farlands.sanctuary.command.player;

import static com.kicas.rp.util.TextUtils.sendFormatted;

import com.kicas.rp.command.TabCompleterBase;
import net.farlands.sanctuary.FarLands;
import net.farlands.sanctuary.command.Category;
import net.farlands.sanctuary.command.PlayerCommand;
import net.farlands.sanctuary.data.struct.OfflineFLPlayer;
import net.farlands.sanctuary.data.Rank;

import org.bukkit.Location;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class CommandPvP extends PlayerCommand {
    public CommandPvP() {
        super(Rank.INITIATE, Category.PLAYER_SETTINGS_AND_INFO, "Toggle on and off PvP.", "/pvp <on|off>", "pvp");
    }

    @Override
    public boolean execute(Player sender, String[] args) {
        OfflineFLPlayer flp = FarLands.getDataHandler().getOfflineFLPlayer(sender);

        if (args.length == 0) {
            sendFormatted(sender, "&(gold)Your PvP is currently %0. To %1 it, run {&(aqua)/pvp %2}.",
                    (flp.pvp ? "on" : "off"),
                    (flp.pvp ? "disable" : "enable"),
                    (flp.pvp ? "off" : "on")
            );
            return true;
        }

        if (!args[0].equals("off") && !args[0].equals("on")) {
            return false;
        }

        flp.pvp = args[0].equals("on");

        sendFormatted(sender, "&(green)PvP %0.", flp.pvp ? "enabled" : "disabled");
        return true;
    }

    @Override
    public List<String> tabComplete(CommandSender sender, String alias, String[] args, Location location) throws IllegalArgumentException {
        return args.length == 1
                ? TabCompleterBase.filterStartingWith(args[0], Arrays.asList("on", "off"))
                : Collections.emptyList();
    }
}
