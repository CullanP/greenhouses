package com.wasteofplastic.greenhouses;

import java.util.Date;
import java.util.UUID;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

/**
 * This class handles commands for admins
 * 
 */
public class AdminCmd implements CommandExecutor {
    private Greenhouses plugin;
    private PlayerCache players;
    public AdminCmd(Greenhouses greenhouses, PlayerCache players) {
	this.plugin = greenhouses;
	this.players = players;
    }


    /*
     * (non-Javadoc)
     * 
     * @see
     * org.bukkit.command.CommandExecutor#onCommand(org.bukkit.command.CommandSender
     * , org.bukkit.command.Command, java.lang.String, java.lang.String[])
     */
    public boolean onCommand(final CommandSender sender, final Command command, final String label, final String[] split) {
	// Check for permissions
	if (sender instanceof Player) {
	    if (!VaultHelper.checkPerm(((Player)sender), "greenhouses.admin")) {
		sender.sendMessage(ChatColor.RED + Locale.errorNoPermission);
		return true;
	    }
	}
	// Check for zero parameters e.g., /gadmin
	switch (split.length) {
	case 0:
	    sender.sendMessage(ChatColor.YELLOW + "/gadmin reload:" + ChatColor.WHITE + " " + Locale.adminHelpreload);
	    sender.sendMessage(ChatColor.YELLOW + "/gadmin info <player>:" + ChatColor.WHITE + " " + Locale.adminHelpinfo);
	    sender.sendMessage(ChatColor.YELLOW + "/gadmin info:" + ChatColor.WHITE + " provides info on the greenhouse you are in");
	    return true;
	case 1:
	    if (split[0].equalsIgnoreCase("reload")) {
		plugin.reloadConfig();
		plugin.loadPluginConfig();
		plugin.ecoTick();
		sender.sendMessage(ChatColor.YELLOW + Locale.reloadconfigReloaded);
		return true;
	    } else if (split[0].equalsIgnoreCase("info")) {
		if (!(sender instanceof Player)) {
		    sender.sendMessage(ChatColor.RED + "Greenhouse info only available in-game");
		    return true;
		}
		Player player = (Player)sender;
		Greenhouse d = players.getInGreenhouse(player.getUniqueId());
		if (d == null) {
		    sender.sendMessage(ChatColor.RED + "Put yourself in a greenhouse to see info.");
		    return true;
		}
		sender.sendMessage(ChatColor.GREEN + "[Greenhouse Info]");
		sender.sendMessage(ChatColor.GREEN + "Owner:" + players.getName(d.getOwner()));
		String trusted = "";
		for (String name : d.getOwnerTrusted()) {
		   trusted += name + ",";
		}
		if (!trusted.isEmpty()) {
		    sender.sendMessage(ChatColor.GREEN + "Owner trustees: " + ChatColor.WHITE + trusted.substring(0, trusted.length() - 1));
		}
		if (d.getRenter() != null)
		    sender.sendMessage(ChatColor.GREEN + "Renter:" + players.getName(d.getRenter()));
		trusted = "";
		for (String name : d.getRenterTrusted()) {
		   trusted += name + ",";
		}
		if (!trusted.isEmpty()) {
		    sender.sendMessage(ChatColor.GREEN + "Renter trustees: " + ChatColor.WHITE + trusted.substring(0, trusted.length() - 1));
		}
		sender.sendMessage(ChatColor.GREEN + "Greenhouse Flags:");
		for (String flag : d.getFlags().keySet()) {
		    sender.sendMessage(flag + ": " + d.getFlags().get(flag));
		}
		return true;
	    } else {
		sender.sendMessage(ChatColor.RED + Locale.errorUnknownCommand);
		return false;
	    }
	case 2:
	    if (split[0].equalsIgnoreCase("balance")) {
		final UUID playerUUID = players.getUUID(split[1]);
		if (playerUUID == null) {
		    sender.sendMessage(ChatColor.RED + Locale.errorUnknownPlayer);
		    return true;
		} else {	
		    sender.sendMessage(ChatColor.GOLD + "Block balance: " + players.getBlockBalance(playerUUID));
		}
		return true;
	    } else if (split[0].equalsIgnoreCase("info")) {
		// Convert name to a UUID
		final UUID playerUUID = players.getUUID(split[1]);
		if (playerUUID == null) {
		    sender.sendMessage(ChatColor.RED + Locale.errorUnknownPlayer);
		    return true;
		} else {	
		    sender.sendMessage(ChatColor.GREEN + players.getName(playerUUID));
		    sender.sendMessage(ChatColor.WHITE + "UUID:" + playerUUID.toString());
		    try {
			Date d = new Date(plugin.getServer().getOfflinePlayer(playerUUID).getLastPlayed() * 1000);
			sender.sendMessage(ChatColor.WHITE + "Last login:" + d.toString());
		    } catch (Exception e) {}
		    sender.sendMessage(ChatColor.GOLD + "Block balance: " + players.getBlockBalance(playerUUID));
		}
		return true;
	    } else if (split[0].equalsIgnoreCase("delete")) {
		sender.sendMessage(ChatColor.YELLOW + "Command not implemented yet");
		return true;
	    } else {
		// Unknown command
		return false;
	    }
	case 3:
	    if (split[0].equalsIgnoreCase("give")) {
		final UUID playerUUID = players.getUUID(split[1]);
		if (playerUUID == null) {
		    sender.sendMessage(ChatColor.RED + Locale.errorUnknownPlayer);
		    return true;
		} else {	
		    try {
			int blocks = Integer.parseInt(split[2]);
			sender.sendMessage(ChatColor.GOLD + "New block balance: " +players.addBlocks(playerUUID, blocks));
		    } catch (Exception e) {
			sender.sendMessage(ChatColor.RED + "Unrecognized number of blocks");
			return true;
		    }

		}
		return true;
	    } else if (split[0].equalsIgnoreCase("take")) {
		final UUID playerUUID = players.getUUID(split[1]);
		if (playerUUID == null) {
		    sender.sendMessage(ChatColor.RED + Locale.errorUnknownPlayer);
		    return true;
		} else {	
		    try {
			int blocks = Integer.parseInt(split[2]);
			sender.sendMessage(ChatColor.GOLD + "New block balance: " +players.removeBlocks(playerUUID, blocks));
		    } catch (Exception e) {
			sender.sendMessage(ChatColor.RED + "Unrecognized number of blocks");
			return true;
		    }

		}
		return true;
	    } else if (split[0].equalsIgnoreCase("set")) {
		final UUID playerUUID = players.getUUID(split[1]);
		if (playerUUID == null) {
		    sender.sendMessage(ChatColor.RED + Locale.errorUnknownPlayer);
		    return true;
		} else {	
		    try {
			int blocks = Integer.parseInt(split[2]);
			sender.sendMessage(ChatColor.GOLD + "New block balance: " + players.setBlocks(playerUUID, blocks));
		    } catch (Exception e) {
			sender.sendMessage(ChatColor.RED + "Unrecognized number of blocks");
			return true;
		    }

		}
		return true;
	    } 
	    return false;
	default:
	    return false;
	}
    }
}