package net.farlands.odyssey.mechanic;

import com.comphenix.protocol.wrappers.WrappedBlockData;
import net.farlands.odyssey.FarLands;
import net.farlands.odyssey.util.FireworkBuilder;
import net.farlands.odyssey.util.Utils;
import net.minecraft.server.v1_15_R1.NBTTagCompound;
import org.bukkit.*;
import org.bukkit.block.Block;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.entity.TNTPrimed;
import org.bukkit.event.EventHandler;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityShootBowEvent;
import org.bukkit.event.entity.ProjectileHitEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import org.bukkit.util.Vector;

import java.util.*;
import java.util.stream.Collectors;

public class Items extends Mechanic {
    private final Map<UUID, TNTArrow> tntArrows;

    private static final List<Material> UNBREAKABLE_BLOCKS = Arrays.asList(Material.CHEST, Material.ENDER_CHEST, Material.BEDROCK,
            Material.BARRIER, Material.AIR);
    private static final List<EntityType> PASSIVES = Arrays.asList(EntityType.PIG, EntityType.COW, EntityType.SHEEP, EntityType.CHICKEN,
            EntityType.HORSE, EntityType.LLAMA, EntityType.SKELETON_HORSE, EntityType.ZOMBIE_HORSE, EntityType.MUSHROOM_COW,
            EntityType.RABBIT, EntityType.OCELOT, EntityType.SNOWMAN);
    private static final List<EntityType> HOSTILES = Arrays.asList(EntityType.ZOMBIE, EntityType.BLAZE, EntityType.SKELETON,
            EntityType.CREEPER, EntityType.SLIME, EntityType.WITCH, EntityType.VINDICATOR, EntityType.SPIDER, EntityType.CAVE_SPIDER,
            EntityType.ENDERMAN, EntityType.SILVERFISH, EntityType.WITHER_SKELETON);

    public Items() {
        this.tntArrows = new HashMap<>();
    }

    @EventHandler
    public void onEntityShootBow(EntityShootBowEvent event) {
        if(event.getEntity() == null || !EntityType.PLAYER.equals(event.getEntity().getType()))
            return;
        PlayerInventory inv = ((Player)event.getEntity()).getInventory();
        ItemStack arrow = null;
        int arrowIndex = -1;
        if(inv.getItemInOffHand() != null && Material.ARROW.equals(inv.getItemInOffHand().getType())) {
            arrow = inv.getItemInOffHand();
            arrowIndex = 0;
        }else{
            for(int i = 27;i < 36;++ i) {
                if(inv.getItem(i) != null && Material.ARROW.equals(inv.getItem(i).getType())) {
                    arrow = inv.getItem(i);
                    arrowIndex = i;
                    break;
                }
            }
            if(arrow == null) {
                arrowIndex = inv.first(Material.ARROW);
                if(arrowIndex < 0)
                    return;
                arrow = inv.getItem(arrowIndex);
            }
        }

        NBTTagCompound nbt = Utils.getTag(arrow);
        if(nbt != null && nbt.hasKey("tntArrow")) {
            // Infinity doesn't apply to these arrows
            if((inv.getItemInMainHand() != null && inv.getItemInMainHand().getType() == Material.BOW
                    ? inv.getItemInMainHand().getEnchantmentLevel(Enchantment.ARROW_INFINITE) > 0
                    : inv.getItemInOffHand().getEnchantmentLevel(Enchantment.ARROW_INFINITE) > 0) && arrowIndex >= 0 &&
                    ((Player)event.getEntity()).getGameMode() == GameMode.SURVIVAL) {
                if(arrow.getAmount() == 1)
                    inv.setItem(arrowIndex, null);
                else
                    arrow.setAmount(arrow.getAmount() - 1);
            }

            tntArrows.put(event.getProjectile().getUniqueId(), new TNTArrow(nbt.getCompound("tntArrow")));
        }
    }

    private static void entityExplosion(Location location, List<EntityType> selectionPool) {
        List<Entity> entities = new ArrayList<>();
        for (int i = 0; i < 15; ++i) {
            Entity entity = location.getWorld().spawnEntity(location, Utils.selectRandom(selectionPool));
            entity.setVelocity(new Vector(Utils.randomDouble(-1, 1), Utils.randomDouble(-1, 1), Utils.randomDouble(-1, 1)));
            entities.add(entity);
        }
        Bukkit.getScheduler().runTaskLater(FarLands.getInstance(), () -> entities.stream().filter(Entity::isValid).forEach(Entity::remove), 60 * 20L);

    }

    @EventHandler
    public void onProjectileHit(ProjectileHitEvent event) {
        if(tntArrows.containsKey(event.getEntity().getUniqueId())) {
            TNTArrow data = tntArrows.get(event.getEntity().getUniqueId());
            switch(data.type) {
                case 0:
                    fakeExplosion(event.getEntity().getLocation(), data.strength * 5.0, data.duration);
                    break;
                case 1:
                    event.getEntity().getWorld().createExplosion(event.getEntity().getLocation(), data.strength * 4.0F, true);
                    break;
                case 2:
                {
                    Location loc = event.getEntity().getLocation();
                    FireworkBuilder.randomFirework(1, 1, 1).spawnEntity(loc);
                    double dx, dy, dz;
                    for(int i = 0;i < 2;++ i) {
                        double theta = Utils.RNG.nextDouble() * 2 * Math.PI;
                        dx = Utils.randomDouble(1.5, 2.5) * Math.cos(theta);
                        dy = Utils.randomDouble(2, 3) * Math.sin(Utils.RNG.nextDouble() * 0.5 * Math.PI);
                        dz = Utils.randomDouble(1.5, 2.5) * Math.sin(theta);
                        FireworkBuilder.randomFirework(1, 1, 1).spawnEntity(loc.clone().add(dx, dy, dz));
                    }
                    break;
                }
                case 3:
                {
                    entityExplosion(event.getEntity().getLocation().add(0, 1, 0), PASSIVES);
                    break;
                }
                case 4:
                {
                    entityExplosion(event.getEntity().getLocation().add(0, 1, 0), HOSTILES);
                    break;
                }
                case 5:
                {
                    Location loc = event.getEntity().getLocation().add(0, 3, 0);
                    double speed = Math.min(2, Math.max(0.3, data.strength * 0.25));
                    for(int i = 0;i < data.strength * 5;++ i) {
                        TNTPrimed tnt = (TNTPrimed)loc.getWorld().spawnEntity(loc, EntityType.PRIMED_TNT);
                        tnt.setFuseTicks(100); // 5 seconds
                        tnt.setVelocity(new Vector(speed * Utils.randomDouble(-1, 1), speed * Utils.randomDouble(-1, 1),
                                speed * Utils.randomDouble(-1, 1)));
                    }
                    break;
                }
                case 6:
                {
                    final Location loc = event.getEntity().getLocation().clone();
                    loc.setY(loc.getWorld().getMaxHeight() * 0.75);
                    final double maxRadius = Math.min(15, data.strength * 3);
                    final int taskId = FarLands.getScheduler().scheduleSyncRepeatingTask(() -> {
                        double theta = Utils.randomDouble(0, 2 * Math.PI), radius = Utils.randomDouble(0, maxRadius);
                        TNTPrimed tnt = (TNTPrimed)loc.getWorld().spawnEntity(loc.clone().add(radius * (2 * Math.cos(theta) - 1), 0,
                                radius * (2 * Math.sin(theta) - 1)), EntityType.PRIMED_TNT);
                        tnt.setFuseTicks(200); // 10 seconds
                    }, 0, 2);
                    FarLands.getScheduler().scheduleSyncDelayedTask(() -> FarLands.getScheduler().cancelTask(taskId), (long)(data.strength * 81));
                    break;
                }
            }
            event.getEntity().remove();
            Bukkit.getScheduler().runTaskLater(FarLands.getInstance(), () -> tntArrows.remove(event.getEntity().getUniqueId()), 1L);
        }
    }

    @EventHandler(ignoreCancelled=true)
    public void onEntityDamageEntity(EntityDamageByEntityEvent event) {
        if(tntArrows.containsKey(event.getDamager().getUniqueId()) && tntArrows.get(event.getDamager().getUniqueId()).type == 2)
            event.setCancelled(true);
    }

    private void fakeExplosion(Location location, double radius, int duration) {
        List<Player> players = location.getWorld().getEntities().stream().filter(ent -> EntityType.PLAYER.equals(ent.getType()) &&
                ent.getLocation().distanceSquared(location) < 75 * 75).map(ent -> (Player) ent).collect(Collectors.toList());
        if(radius <= 10.0) {
            Map<Block, WrappedBlockData> exploded = getExplodedBlocks(location, radius);
            players.forEach(player -> {
                Utils.changeBlocks(player, exploded);
                player.playSound(location, Sound.ENTITY_GENERIC_EXPLODE, 5.0F, 1.0F);
            });
            Bukkit.getScheduler().runTaskLater(FarLands.getInstance(), () -> {
                Map<Block, WrappedBlockData> reset = new HashMap<>();
                exploded.keySet().forEach(block -> reset.put(block, WrappedBlockData.createData(block.getBlockData())));
                players.forEach(player -> Utils.changeBlocks(player, reset));
            }, duration * 20L);
        }else{
            (new Thread(() -> {
                Map<Block, WrappedBlockData> exploded = getExplodedBlocks(location, radius);
                players.forEach(player -> {
                    Utils.changeBlocksAsync(player, exploded);
                    player.playSound(location, Sound.ENTITY_GENERIC_EXPLODE, 5.0F, 1.0F);
                });
                Bukkit.getScheduler().runTaskLater(FarLands.getInstance(), () -> {
                    Map<Block, WrappedBlockData> reset = new HashMap<>();
                    exploded.keySet().forEach(block -> reset.put(block, WrappedBlockData.createData(block.getBlockData())));
                    players.forEach(player -> Utils.changeBlocksAsync(player, reset));
                }, duration * 20L);
            })).start();
        }
    }

    private Map<Block, WrappedBlockData> getExplodedBlocks(Location location, double radius) {
        final double r2 = radius * radius;
        Map<Block, WrappedBlockData> exploded = new HashMap<>();
        for(double y = -radius;y < radius;++ y) {
            for(double x = -radius;x < radius;++ x) {
                for(double z = -radius;z < radius;++ z) {
                    Block current = location.clone().add(x, y, z).getBlock();
                    if(!UNBREAKABLE_BLOCKS.contains(current.getType()) && current.getLocation().distanceSquared(location) <= r2) {
                        Location loc = current.getLocation().clone().subtract(0, 1, 0);
                        exploded.put(current, WrappedBlockData.createData((loc.distanceSquared(location) > r2 ||
                                UNBREAKABLE_BLOCKS.contains(loc.getBlock().getType())) && loc.getBlock().getType().isSolid() &&
                                        Utils.randomChance(0.2) ? Material.FIRE : Material.AIR));
                    }
                }
            }
        }
        return exploded;
    }

    private static final class TNTArrow {
        float strength;
        int duration;
        int type;

        TNTArrow(NBTTagCompound nbt) {
            this.strength = nbt.getFloat("strength");
            this.duration = nbt.hasKey("duration") ? nbt.getInt("duration") : 15;
            this.type = nbt.hasKey("type") ? nbt.getInt("type") : 0;
        }
    }
}
