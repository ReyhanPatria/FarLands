package net.farlands.odyssey.command;

import net.farlands.odyssey.data.Rank;

public abstract class DiscordCommand extends Command {
    protected DiscordCommand(Rank minRank, String description, String usage, boolean requiresAlias, String name, String... aliases) {
        super(minRank, description, usage, requiresAlias, name, aliases);
    }

    protected DiscordCommand(Rank minRank, String description, String usage, String name, String... aliases) {
        this(minRank, description, usage, false, name, aliases);
    }

    public boolean deleteOnUse() {
        return false;
    }

    public boolean requiresMessageID() {
        return false;
    }
}
