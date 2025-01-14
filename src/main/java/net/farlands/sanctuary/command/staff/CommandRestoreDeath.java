package net.farlands.sanctuary.command.staff;

import static com.kicas.rp.util.TextUtils.sendFormatted;

import net.farlands.sanctuary.FarLands;
import net.farlands.sanctuary.command.Command;
import net.farlands.sanctuary.data.Rank;
import net.farlands.sanctuary.data.struct.PlayerDeath;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class CommandRestoreDeath extends Command {
    public CommandRestoreDeath() {
        super(Rank.BUILDER, "Restore the players previous deaths.", "/restoredeath <player> [death] [preview]", "restoredeath");
    }
    
    @Override
    public boolean execute(CommandSender sender, String[] args) {
        Player player = args.length < 1 ? null : getPlayer(args[0], sender);
        if (player == null) {
            sendFormatted(sender, "&(red)Player not found.");
            return true;
        }

        List<PlayerDeath> deaths = FarLands.getDataHandler().getDeaths(player.getUniqueId());
        if(deaths.isEmpty()) {
            sendFormatted(sender, "&(red)This player has no deaths on record.");
            return true;
        }

        boolean preview = "preview".equalsIgnoreCase(args[args.length - 1]);

        // newest deaths at tail of list
        int death;
        if (args.length < 2 || (args.length == 2 && preview)) {
            death = deaths.size() - 1;
        } else {
            try {
                death = deaths.size() - Integer.parseInt(args[1]);
            } catch (NumberFormatException e) {
                sendFormatted(sender, "&(red)Death number must be a number between 1 and " + deaths.size());
                return true;
            }
            if (deaths.size() - 1 < death || death < 0) {
                sendFormatted(sender, "&(red)Death number must be between 1 and " + deaths.size());
                return true;
            }
        }

        PlayerDeath deathData = deaths.get(death);
        List<ItemStack> deathInv = deathData.getInventory();

        if (preview) {
            if (!(sender instanceof Player)) {
                sender.sendMessage(ChatColor.RED + "You must be in-game to preview a death.");
                return true;
            }

            Inventory inv = Bukkit.createInventory(null, 45, "Death Inventory Preview");
            for (int i = deathInv.size(); --i >= 0;) {
                int index = (4 - i / 9) * 9 + i % 9;
                inv.setItem(index, deathInv.get(i));
            }
            ((Player) sender).openInventory(inv);
            return true;
        }

        player.setLevel(deathData.getXpLevels());
        player.setExp(deathData.getXpPoints());
        for (int i = deathInv.size(); --i >= 0;)
            player.getInventory().setItem(i, deathInv.get(i));

        return true;
    }
    
    @Override
    public List<String> tabComplete(CommandSender sender, String alias, String[] args, Location location) throws IllegalArgumentException {
        if (!Rank.getRank(sender).isStaff())
            return Collections.emptyList();
        switch (args.length) {
            case 0:
                return getOnlinePlayers("", sender);
            case 1:
                return getOnlinePlayers(args[0], sender);
            case 2:
                return Arrays.asList("1", "2", "3", "preview");
            case 3:
                return Collections.singletonList("preview");
            default:
                return Collections.emptyList();
        }
    }
}
