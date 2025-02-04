package net.farlands.sanctuary.command.player;

import net.farlands.sanctuary.FarLands;
import net.farlands.sanctuary.command.Category;
import net.farlands.sanctuary.command.PlayerCommand;
import net.farlands.sanctuary.data.FLPlayerSession;
import net.farlands.sanctuary.data.Rank;
import net.farlands.sanctuary.util.ComponentColor;
import org.bukkit.Material;
import org.bukkit.block.BlockFace;
import org.bukkit.craftbukkit.v1_17_R1.entity.CraftPig;
import org.bukkit.craftbukkit.v1_17_R1.entity.CraftPlayer;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Pig;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

public class CommandSit extends PlayerCommand {
    public CommandSit() {
        super(Rank.KNIGHT, Category.MISCELLANEOUS, "Have a seat.", "/sit", "sit");
    }

    @Override
    public boolean canUse(Player sender) {
        if (sender.getVehicle() != null && FarLands.getDataHandler().getSession(sender).seatExit == null) {
            sender.sendMessage(ComponentColor.red("You are already sitting"));
            return false;
        }
        return super.canUse(sender);
    }

    @Override
    public boolean execute(Player sender, String[] args) {
        FLPlayerSession session = FarLands.getDataHandler().getSession(sender);
        if (session.unsit())
            return true;

        if (!sender.isOnGround() || // can't go in canUse as it prevents /sit exit
                sender.getLocation().getBlock().getRelative(BlockFace.DOWN).getType() == Material.AIR) { // Check if player is standing directly on top of block, not off of a side
            sender.sendMessage(ComponentColor.red("You must be on the ground to use this command."));
            return true;
        }

        session.seatExit = sender.getLocation().clone();
        Pig seat = (Pig) sender.getWorld().spawnEntity(sender.getLocation().clone().subtract(0, .875, 0), EntityType.PIG);
        seat.setAdult();
        seat.setGravity(false);
        seat.setSilent(true);
        seat.setInvulnerable(true);
        seat.setAI(false);
        seat.addPotionEffect(new PotionEffect(PotionEffectType.INVISIBILITY, Integer.MAX_VALUE, 1));
        seat.addPotionEffect(new PotionEffect(PotionEffectType.SLOW,         Integer.MAX_VALUE, 1));

        // Throws error
        //ReflectionHelper.setFieldValue("au", net.minecraft.world.entity.Entity.class,  // vehicle field is now au
        //        ((CraftPlayer) sender).getHandle(), ((CraftPig) seat).getHandle());

        // Seems to work?
        ((CraftPlayer) sender).getHandle().a(((CraftPig) seat).getHandle(), true);
        ((CraftPig) seat).setPassenger(sender);

        return true;
    }
}
