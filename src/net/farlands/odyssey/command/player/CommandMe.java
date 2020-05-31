package net.farlands.odyssey.command.player;

import com.kicas.rp.util.TextUtils;
import net.farlands.odyssey.FarLands;
import net.farlands.odyssey.command.PlayerCommand;
import net.farlands.odyssey.data.Rank;
import net.farlands.odyssey.data.struct.OfflineFLPlayer;
import net.farlands.odyssey.mechanic.Chat;

import org.bukkit.command.BlockCommandSender;
import org.bukkit.command.CommandSender;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.entity.Player;

public class CommandMe extends PlayerCommand {
    public CommandMe() {
        super(Rank.INITIATE, "Broadcast an action.", "/me <action>", "me", "emote", "action");
    }

    @Override
    public boolean execute(Player sender, String[] args) {
        if (args.length <= 0)
            return false;

        OfflineFLPlayer flp = FarLands.getDataHandler().getOfflineFLPlayer(sender);
        Chat.chat(flp, sender, " * " + Chat.removeColorCodes(flp.getDisplayName()) + " ", String.join(" ", args));
        return true;
    }

    @Override
    public boolean canUse(CommandSender sender) {
        if (!(sender instanceof BlockCommandSender || sender instanceof ConsoleCommandSender ||
                !FarLands.getDataHandler().getOfflineFLPlayer(sender).isMuted())) {
            TextUtils.sendFormatted(sender, "&(red)You cannot use this command while muted.");
            return false;
        }

        return super.canUse(sender);
    }
}
