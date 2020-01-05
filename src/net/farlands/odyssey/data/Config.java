package net.farlands.odyssey.data;

import net.farlands.odyssey.data.struct.JsonItemStack;
import net.farlands.odyssey.discord.DiscordBotConfig;
import net.farlands.odyssey.util.Pair;
import net.farlands.odyssey.util.Utils;

import java.util.ArrayList;
import java.util.List;

/**
 * Contains all configuration fields for the FarLands plugin. This class is loaded and managed by the data handler.
 */
public class Config {
    public int fds; // Flight detection sensitivity, a number between 0 and 99 inclusive, 0 being least sensitive
    public int publicItems;
    public int totalItems;
    public int rotatingMessageGap;
    public int gcCycleTime;
    public long restartTime; // Time in ms since midnight UTC
    public String dedicatedMemory;
    public String screenSession;
    public String discordInvite;
    public String appealsLink;
    public String donationLink;
    public String paperDownload;
    public JsonItemStack patronCollectable;
    public List<String> rotatingMessages;
    public List<String> jsUsers; // UUIDs of the people who may use /js
    public DiscordBotConfig discordBotConfig;
    public VoteConfig voteConfig;

    public Config() {
        this.fds = 80;
        this.publicItems = 3;
        this.totalItems = 4;
        this.rotatingMessageGap = 5;
        this.gcCycleTime = 5;
        this.restartTime = 14400000L;
        this.dedicatedMemory = "1G";
        this.screenSession = "";
        this.discordInvite = "";
        this.appealsLink = "";
        this.donationLink = "";
        this.paperDownload = "";
        this.patronCollectable = null;
        this.rotatingMessages = new ArrayList<>();
        this.jsUsers = new ArrayList<>();
        this.discordBotConfig = new DiscordBotConfig("", 0L,
            new Pair<>("output", 0L),
            new Pair<>("archives", 0L),
            new Pair<>("ingame", 0L),
            new Pair<>("announcements", 0L),
            new Pair<>("reports", 0L),
            new Pair<>("warpproposals", 0L),
            new Pair<>("debug", 0L),
            new Pair<>("alerts", 0L),
            new Pair<>("devreports", 0L),
            new Pair<>("suggestions", 0L),
            new Pair<>("bugreports", 0L),
            new Pair<>("staffcommands", 0L),
            new Pair<>("commandlog", 0L)
        );
        this.voteConfig = new VoteConfig();
    }

    public int getFDS() {
        return (int)Utils.constrain(fds, 0.0, 99.0);
    }

    public boolean isScreenSessionNotSet() {
        return screenSession.isEmpty();
    }
}
