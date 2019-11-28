package net.farlands.odyssey.command;

import net.farlands.odyssey.FarLands;
import org.bukkit.Bukkit;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

public class FLShutdownEvent extends Event {
    private static final HandlerList HANDLER_LIST = new HandlerList();

    public FLShutdownEvent() {
        FarLands.getScheduler().scheduleSyncDelayedTask(Bukkit::shutdown, 5L);
    }

    @Override
    public HandlerList getHandlers() {
        return HANDLER_LIST;
    }

    public static HandlerList getHandlerList() {
        return HANDLER_LIST;
    }
}