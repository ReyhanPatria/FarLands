package net.farlands.sanctuary.command.player;

import static com.kicas.rp.util.TextUtils.sendFormatted;
import com.kicas.rp.command.TabCompleterBase;
import com.kicas.rp.util.Utils;

import net.farlands.sanctuary.FarLands;
import net.farlands.sanctuary.command.Category;
import net.farlands.sanctuary.command.PlayerCommand;
import net.farlands.sanctuary.data.struct.Particles;
import net.farlands.sanctuary.data.Rank;

import org.bukkit.Location;
import org.bukkit.Particle;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

public class CommandParticles extends PlayerCommand {
    private static final List<Particle> ILLEGAL_PARTICLES = Arrays.asList(Particle.MOB_APPEARANCE, Particle.BARRIER,
            Particle.REDSTONE, Particle.BLOCK_CRACK);

    public CommandParticles() {
        super(Rank.DONOR, Category.COSMETIC, "Choose a particle to spawn around you. Particles are visible to all players.",
                "/particles <type> [location=above-head]", "particles");
    }

    @Override
    public boolean execute(Player sender, String[] args) {
        if (args.length == 0)
            return false;

        if ("none".equalsIgnoreCase(args[0])) {
            // Removes particles
            FarLands.getDataHandler().getOfflineFLPlayer(sender).setParticles(null, null);
            sendFormatted(sender, "&(green)Particles removed.");
        } else {
            // Get and check the type
            Particle type = Utils.valueOfFormattedName(args[0], Particle.class);
            if (type == null) {
                sendFormatted(sender, "&(red)Invalid particle type: %0", args[0]);
                return true;
            } else if (ILLEGAL_PARTICLES.contains(type)) {
                sendFormatted(sender, "&(red)You cannot use that particle type.");
                return true;
            }

            // Get and check the location
            Particles.ParticleLocation location = args.length == 1
                    ? Particles.ParticleLocation.ABOVE_HEAD
                    : Utils.valueOfFormattedName(args[1], Particles.ParticleLocation.class);
            if (location == null) {
                sendFormatted(sender, "&(red)Invalid particle location: %0", args[1]);
                return true;
            }

            FarLands.getDataHandler().getOfflineFLPlayer(sender).setParticles(type, location);
            sendFormatted(sender, "&(green)Particles set.");
        }

        return true;
    }

    @Override
    public List<String> tabComplete(CommandSender sender, String alias, String[] args, Location location) throws IllegalArgumentException {
        switch (args.length) {
            case 0:
            case 1: {
                List<String> completions = Arrays.stream(Particle.values())
                        .filter(p -> !ILLEGAL_PARTICLES.contains(p))
                        .map(Utils::formattedName)
                        .filter(p -> p.startsWith(args[0]))
                        .collect(Collectors.toCollection(ArrayList::new));
                completions.add("none");
                return completions;
            }
            case 2:
                return TabCompleterBase.filterStartingWith(args[1], Arrays.stream(Particles.ParticleLocation.VALUES)
                        .map(Utils::formattedName));
            default:
                return Collections.emptyList();
        }
    }
}
