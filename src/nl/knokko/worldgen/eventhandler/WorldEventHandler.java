package nl.knokko.worldgen.eventhandler;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.entity.CreatureSpawnEvent;
import org.bukkit.event.entity.EntityPortalEvent;
import org.bukkit.event.player.PlayerPortalEvent;
import org.bukkit.event.player.PlayerTeleportEvent.TeleportCause;
import org.bukkit.inventory.ItemStack;

public abstract class WorldEventHandler implements Listener {

	private final String name;

	public WorldEventHandler() {
		name = worldName();
	}

	public boolean active(World world) {
		return world.getName().equals(name);
	}

	@EventHandler(priority = EventPriority.HIGH, ignoreCancelled = true)
	public void onPortal(PlayerPortalEvent event) {
		World world = event.getFrom().getWorld();
		if (active(world)) {
			if (event.getCause() == TeleportCause.END_PORTAL) {
				Location dest = getEndPortalDestination(event.getPlayer());
				if (dest != null) {
					event.setTo(dest);
				} else {
					event.setCancelled(true);
				}
			} else if (event.getCause() == TeleportCause.NETHER_PORTAL) {
				Location loc = event.getFrom();
				int x = loc.getBlockX();
				int y = loc.getBlockY();
				int z = loc.getBlockZ();
				if (world.getBlockAt(x, y, z).getType() != Material.PORTAL) {
					if (world.getBlockAt(x, y, z + 1).getType() == Material.PORTAL)
						z++;
					else if (world.getBlockAt(x, y, z - 1).getType() == Material.PORTAL)
						z--;
					else if (world.getBlockAt(x + 1, y, z).getType() == Material.PORTAL)
						x++;
					else if (world.getBlockAt(x - 1, y, z).getType() == Material.PORTAL)
						x--;
					else {
						Bukkit.getLogger().warning("Can't find nether portal at " + loc);
					}
				}
				while (world.getBlockAt(x - 1, y, z).getType() == Material.PORTAL) {
					x--;
				}
				while (world.getBlockAt(x, y - 1, z).getType() == Material.PORTAL) {
					y--;
				}
				while (world.getBlockAt(x, y, z - 1).getType() == Material.PORTAL) {
					z--;
				}
				Location dest = getNetherPortalDestination(world, event.getPlayer(), x, y, z);
				if (dest != null) {
					event.setTo(dest);
				} else {
					event.setCancelled(true);
				}
			}
		}
	}

	@EventHandler(priority = EventPriority.HIGH, ignoreCancelled = true)
	public void onPortal(EntityPortalEvent event) {
		// I don't know what kind of portal the entity is using, so this seems the
		// safest thing to do
		event.setCancelled(true);
	}

	@EventHandler(priority = EventPriority.HIGH, ignoreCancelled = true)
	public void onEntitySpawn(CreatureSpawnEvent event) {
		if (active(event.getEntity().getWorld()) && cancelSpawn(event.getEntity())) {
			event.setCancelled(true);
		}
	}

	@EventHandler(priority = EventPriority.HIGH, ignoreCancelled = true)
	public void onBlockBreak(BlockBreakEvent event) {
		if (active(event.getBlock().getWorld()) && event.isDropItems()) {
			ItemStack tool = event.getPlayer().getInventory().getItemInMainHand();
			int fortune;
			if (tool != null) {
				fortune = tool.getEnchantmentLevel(Enchantment.LOOT_BONUS_BLOCKS);
			} else {
				fortune = 0;
			}
			ItemStack overrideDrop = overrideDrop(event.getBlock(), fortune);
			if (overrideDrop != null) {
				event.setDropItems(false);
				Location loc = event.getBlock().getLocation();
				loc.getWorld().dropItemNaturally(loc, overrideDrop);
			}
		}
	}

	/**
	 * @return The unique name of the world this event handler is for
	 */
	protected abstract String worldName();
	
	/**
	 * Override this method to specify the destination of nether portals in the world of this handler.
	 * If the player should be teleported, the destination should be returned. Eventually build a return
	 * portal if there is none yet. If null is returned, the player will not be teleported.
	 * @param from The world the player is coming from
	 * @param player The player that is about to be teleported
	 * @param x The x-coordinate of the most negative portal block
	 * @param y The y-coorindate of the lowest portal block
	 * @param z The z-coordinate of the most negative portal block
	 * @return the destination or null to cancel the teleportation
	 */
	protected abstract Location getNetherPortalDestination(World from, Player player, int x, int y, int z);

	protected abstract Location getEndPortalDestination(Player player);

	/**
	 * This method allows the event handler of the world to change the item that
	 * gets dropped upon breaking a block. If this method returns null, the default
	 * item will be dropped. Otherwise, the drop will be changed to the returned
	 * ItemStack.
	 * 
	 * @param block   The block that is being broken
	 * @param fortune The fortune enchantment level that is being used or 0
	 * @return the replacement for the drop or null if the default item should be
	 *         dropped
	 */
	protected abstract ItemStack overrideDrop(Block block, int fortune);

	/**
	 * This method allows the event handler to prevent certain entities from
	 * spawning or to modify the entity before it spawns.
	 * 
	 * @param entity The entity that is about to be spawned
	 * @return true if the spawn should be prevented, false if the spawn should
	 *         continue
	 */
	protected abstract boolean cancelSpawn(LivingEntity entity);
}