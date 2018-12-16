package nl.knokko.worldgen.command;

import java.util.Arrays;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import nl.knokko.core.plugin.player.Players;

public class CommandWorlds implements CommandExecutor {
	
	private void sendUseage(CommandSender sender) {
		sender.sendMessage(ChatColor.RED + "You should use /worlds tp ...");
	}
	
	@Override
	public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
		if (args.length > 0) {
			if (args[0].equals("tp")) {
				processTeleport(sender, Arrays.copyOfRange(args, 1, args.length));
			} else {
				sendUseage(sender);
			}
		} else {
			sendUseage(sender);
		}
		return false;
	}
	
	private void processTeleport(CommandSender sender, String[] args) {
		if (args.length > 0) {
			String worldName = args[0];
			Player target;
			if (args.length > 1) {
				target = Players.getOnline(args[1]);
				if (target == null) {
					sender.sendMessage(ChatColor.RED + "The player with name '" + args[1] + "' is not online.");
					return;
				}
			} else {
				if (sender instanceof Player) {
					target = (Player) sender;
				} else {
					sender.sendMessage("You should use /worlds tp <world> <player>");
					return;
				}
			}
			if (!sender.hasPermission("worlds.teleport.self") && sender == target) {
				sender.sendMessage(ChatColor.DARK_RED + "You are not authorised to teleport yourself.");
				return;
			}
			if (!sender.hasPermission("worlds.teleport.others") && sender != target) {
				sender.sendMessage(ChatColor.DARK_RED + "You are not authorised to teleport other players.");
				return;
			}
			World world = Bukkit.getWorld(worldName);
			if (world == null) {
				sender.sendMessage(ChatColor.RED + "There is no world with name '" + worldName + "'");
				// prevent null pointers
				return;
			}
			double x;
			if (args.length > 2) {
				try {
					x = Double.parseDouble(args[2]);
				} catch (NumberFormatException nfe) {
					sender.sendMessage(ChatColor.RED + "The x-coordinate '" + args[2] + "' should be a number.");
					return;
				}
			} else {
				x = world.getSpawnLocation().getX();
			}
			double y;
			if (args.length > 3) {
				try {
					y = Double.parseDouble(args[3]);
				} catch (NumberFormatException nfe) {
					sender.sendMessage(ChatColor.RED + "The y-coordinate '" + args[3] + "' should be a number.");
					return;
				}
			} else {
				y = world.getSpawnLocation().getY();
			}
			double z;
			if (args.length > 4) {
				try {
					z = Double.parseDouble(args[4]);
				} catch (NumberFormatException nfe) {
					sender.sendMessage(ChatColor.RED + "The z-coordinate '" + args[4] + "' should be a number.");
					return;
				}
			} else {
				z = world.getSpawnLocation().getZ();
			}
			target.teleport(new Location(world, x, y, z));
			sender.sendMessage(ChatColor.GREEN + "Teleported " + target.getName() + " to world " + worldName);
		} else {
			sender.sendMessage(ChatColor.RED + "You should use /worlds tp <world> [player] [x] [y] [z]");
		}
	}
}