package net.farlands.sanctuary.command.discord;

import static com.kicas.rp.util.TextUtils.sendFormatted;

import net.dv8tion.jda.api.entities.Message;

import net.farlands.sanctuary.FarLands;
import net.farlands.sanctuary.command.DiscordCommand;
import net.farlands.sanctuary.command.DiscordSender;
import net.farlands.sanctuary.command.FLShutdownEvent;
import net.farlands.sanctuary.data.Config;
import net.farlands.sanctuary.data.Rank;
import net.farlands.sanctuary.data.struct.OfflineFLPlayer;

import net.farlands.sanctuary.util.Logging;
import org.bukkit.ChatColor;
import org.bukkit.command.BlockCommandSender;
import org.bukkit.command.CommandSender;
import org.bukkit.command.ConsoleCommandSender;

import java.io.File;
import java.util.List;

public class CommandArtifact extends DiscordCommand {
    public CommandArtifact() {
        super(Rank.ADMIN, "Set the current plugin artifact on the server.",
                "/artifact [forcePush=false] [updateSpigot=false] {add jar as attachment}", "artifact");
    }

    @Override
    public boolean execute(CommandSender sender, String[] args) {
        if (!canUse(sender)) // Extra security
            return true;

        if (!(sender instanceof DiscordSender)) {
            sendFormatted(sender, "&(red)This command must be used from discord.");
            return false;
        }

        if (FarLands.getFLConfig().isScreenSessionNotSet()) {
            sendFormatted(sender, "&(red)The screen session for this server instance is not specified. " +
                    "This command requires that field to run.");
            return true;
        }

        // Locate the attachment
        String channelId = args[0].substring(0, args[0].indexOf(':')), messageId = args[0].substring(args[0].indexOf(':') + 1);
        Message message = FarLands.getDiscordHandler().getNativeBot().getTextChannelById(channelId).retrieveMessageById(messageId).complete();
        List<Message.Attachment> attachments = message.getAttachments();
        if (attachments.isEmpty()) {
            sender.sendMessage("You must attach the jar to the command message.");
            return true;
        }

        File dest = FarLands.getDataHandler().getTempFile(attachments.get(0).getFileName());
        if (dest.exists())
            dest.delete();

        try {
            attachments.get(0).downloadToFile(dest).get();
        } catch (Exception ex) {
            sender.sendMessage(ChatColor.RED + "Failed to upload artifact due to an internal error.");
            Logging.error(ex.getMessage());
            ex.printStackTrace(System.out);
            return true;
        }

        Config cfg = FarLands.getFLConfig();
        if (args.length > 1 && "true".equals(args[1])) {
            FarLands.executeScript("artifact.sh", cfg.screenSession, cfg.paperDownload, cfg.dedicatedMemory,
                    args.length > 2 ? args[2] : "false");
            FarLands.getInstance().getServer().getPluginManager().callEvent(new FLShutdownEvent());
        }

        return true;
    }

    @Override
    public boolean canUse(CommandSender sender) {
        if (sender instanceof ConsoleCommandSender)
            return true;
        else if (sender instanceof BlockCommandSender) // Prevent people circumventing permissions by using a command block
            return false;

        OfflineFLPlayer flp = FarLands.getDataHandler().getOfflineFLPlayer(sender);
        if (flp == null || !FarLands.getFLConfig().jsUsers.contains(flp.uuid.toString())) {
            sender.sendMessage(ChatColor.RED + "You cannot use this command.");
            return false;
        }

        return super.canUse(sender);
    }

    @Override
    public boolean requiresMessageID() {
        return true;
    }
}
