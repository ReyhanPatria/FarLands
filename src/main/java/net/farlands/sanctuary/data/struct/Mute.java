package net.farlands.sanctuary.data.struct;

import net.farlands.sanctuary.util.TimeInterval;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;

/**
 * Handles player mutes.
 */
public final class Mute {
    private final long dateEnds; // In milliseconds since epoch
    private final String reason;

    public Mute(int duration, String reason) {
        this.dateEnds = System.currentTimeMillis() + duration * 1000L;
        this.reason = reason;
    }

    public Mute(int duration) {
        this(duration, "Muted by a staff member.");
    }

    public Mute(long dateEnds, String reason) {
        this.dateEnds = dateEnds;
        this.reason = reason;
    }

    Mute() {
        this(0, "");
    }

    public long getDateEnds() {
        return dateEnds;
    }

    public boolean hasExpired() {
        return System.currentTimeMillis() > dateEnds;
    }

    public long timeRemaining() {
        return dateEnds - System.currentTimeMillis();
    }

    public String getReason() {
        return reason;
    }

    public void sendMuteMessage(CommandSender sender) {
        sender.sendMessage(
                ChatColor.RED + "You may not type in chat. You were muted for: " + ChatColor.GOLD + reason +
                        ChatColor.RED + " Your mute expires in " + TimeInterval.formatTime(timeRemaining(), false) + "."
        );
    }
}
