package com.wasteofplastic.districts;

import java.awt.geom.Rectangle2D;
import java.util.HashSet;
import java.util.UUID;

import net.milkbowl.vault.economy.EconomyResponse;

import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.util.Vector;

public class DistrictCmd implements CommandExecutor {
    public boolean busyFlag = true;
    public Location Islandlocation;
    private Districts plugin;
    private PlayerCache players;

    /**
     * Constructor
     * 
     * @param acidIsland
     * @param players 
     */
    public DistrictCmd(Districts acidIsland, PlayerCache players) {

	// Plugin instance
	this.plugin = acidIsland;
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
	if (!(sender instanceof Player)) {
	    return false;
	}
	final Player player = (Player) sender;
	// Basic permissions check to even use /district
	if (!VaultHelper.checkPerm(player, "districts.create")) {
	    player.sendMessage(ChatColor.RED + Locale.errorNoPermission);
	    return true;
	}
	/*
	 * Grab data for this player - may be null or empty
	 * playerUUID is the unique ID of the player who issued the command
	 */
	final UUID playerUUID = player.getUniqueId();
	switch (split.length) {
	// /district command by itself
	case 0:
	case 1:
	    // /district <command>
	    if (split.length == 0 || split[0].equalsIgnoreCase("help")) { 
		player.sendMessage(ChatColor.GREEN + "Districts " + plugin.getDescription().getVersion() + " help:");

		player.sendMessage(ChatColor.YELLOW + "/district claim <radius>: " + ChatColor.WHITE + "Claims a square district with you in the middle of it");
		player.sendMessage(ChatColor.YELLOW + "/district pos: " + ChatColor.WHITE + "Sets a position for a district corner");
		player.sendMessage(ChatColor.YELLOW + "/district balance: " + ChatColor.WHITE + "Shows you how many blocks you have to use for districts");
		player.sendMessage(ChatColor.YELLOW + "/district remove: " + ChatColor.WHITE + "Removes a district that you are standing in if you are the owner");
		player.sendMessage(ChatColor.YELLOW + "/district info: " + ChatColor.WHITE + "Shows info on the district you are in");
		player.sendMessage(ChatColor.YELLOW + "/district buy: " + ChatColor.WHITE + "Attempts to buy the district you are in");
		player.sendMessage(ChatColor.YELLOW + "/district rent: " + ChatColor.WHITE + "Attempts to rent the district you are in");
		player.sendMessage(ChatColor.YELLOW + "/district rent <price>: " + ChatColor.WHITE + "Puts the district you are in up for rent for a weekly rent");
		player.sendMessage(ChatColor.YELLOW + "/district sell <price>: " + ChatColor.WHITE + "Puts the district you are in up for sale");
		player.sendMessage(ChatColor.YELLOW + "/district cancel: " + ChatColor.WHITE + "Cancels any For Sale or For Rent");
		return true;
	    } else if (split[0].equalsIgnoreCase("pos")) {
		// TODO: Put more checks into the setting of a district
		if (players.getInDistrict(playerUUID) != null) {
		    player.sendMessage(ChatColor.RED + "You are already in a district!");
		    return true;
		}
		if (plugin.getPos1s().containsKey(playerUUID)) {
		    player.sendMessage("Position 1 : " + plugin.getPos1s().get(playerUUID).getBlockX() + ", " + plugin.getPos1s().get(playerUUID).getBlockZ());
		    player.sendMessage("Position 2 : " + player.getLocation().getBlockX() + ", " + player.getLocation().getBlockZ());
		    player.sendMessage("Creating district!");
		    DistrictRegion d = new DistrictRegion(plugin, plugin.getPos1s().get(playerUUID), player.getLocation(), player.getUniqueId());
		    d.setEnterMessage("Welcome to " + player.getDisplayName() + "'s district!");
		    d.setFarewellMessage("Now leaving " + player.getDisplayName() + "'s district.");
		    plugin.getDistricts().add(d);
		    plugin.getPos1s().remove(playerUUID);
		} else {
		    plugin.getPos1s().put(playerUUID, player.getLocation());
		    player.sendMessage("Setting position 1 : " + player.getLocation().getBlockX() + ", " + player.getLocation().getBlockZ());
		}
		return true;
	    } else if (split[0].equalsIgnoreCase("remove")) {
		DistrictRegion d = players.getInDistrict(playerUUID);
		if (d != null) {
		    if (d.getOwner().equals(playerUUID)) {
			player.sendMessage(ChatColor.RED + "Removing district!");
			DistrictGuard.devisualize(player);
			// Remove the district
			HashSet<DistrictRegion> ds = plugin.getDistricts();
			ds.remove(d);
			plugin.setDistricts(ds);
			// Return blocks
			int height = Math.abs(d.getPos1().getBlockX() - d.getPos2().getBlockX()) + 1;
			int width = Math.abs(d.getPos1().getBlockX() - d.getPos2().getBlockX()) + 1;
			int blocks = height * width;
			int balance = plugin.players.addBlocks(playerUUID, blocks);
			player.sendMessage("Recovered " + blocks + " blocks. Your balance is " + balance);
			players.setInDistrict(playerUUID, null);
			return true;
		    }
		    player.sendMessage(ChatColor.RED + "This is not your district!");
		} else {
		    player.sendMessage(ChatColor.RED + "You are not in a district!"); 
		}
		return true;
	    } else if (split[0].equalsIgnoreCase("balance")) {
		int balance = plugin.players.getBlockBalance(playerUUID);
		player.sendMessage("Your block balance is " + balance);
		return true;
	    } else if (split[0].equalsIgnoreCase("buy")) {
		DistrictRegion d = players.getInDistrict(playerUUID);
		if (d != null) {
		    if (!d.isForSale()) {
			player.sendMessage(ChatColor.RED + "This district is not for sale!");
			return true;
		    }
		    if (d.getOwner().equals(playerUUID)) {
			player.sendMessage(ChatColor.RED + "You already own this district!");
			return true;
		    } 
		    // See if the player can afford it
		    if (!VaultHelper.econ.has(player, d.getPrice())) {
			player.sendMessage(ChatColor.RED + "You cannot afford " + VaultHelper.econ.format(d.getPrice()));
			return true;
		    }
		    // It's for sale, the player can afford it and it's not the owner - sell!
		    EconomyResponse resp = VaultHelper.econ.withdrawPlayer(player, d.getPrice());
		    if (resp.transactionSuccess()) {
			// Check if owner is online
			Player owner = plugin.getServer().getPlayer(d.getOwner());
			if (owner != null) {
			    DistrictGuard.devisualize(owner);
			    owner.sendMessage("You successfully sold a district for " + VaultHelper.econ.format(d.getPrice()) + " to " + player.getDisplayName());
			}	
			Location pos1 = d.getPos1();
			Location pos2 = d.getPos2();
			player.sendMessage("You purchased the district for "+ VaultHelper.econ.format(d.getPrice()) + "!");
			// Remove the district
			HashSet<DistrictRegion> ds = plugin.getDistricts();
			ds.remove(d);
			plugin.setDistricts(ds);
			// Recreate the district for this player
			DistrictRegion newRegion = new DistrictRegion(plugin, pos1,pos2,playerUUID);
			players.setInDistrict(playerUUID, newRegion);
			return true;
		    } else {
			player.sendMessage(ChatColor.RED + "There was an economy problem trying to purchase the district for "+ VaultHelper.econ.format(d.getPrice()) + "!");
			player.sendMessage(ChatColor.RED + resp.errorMessage);
			return true;
		    }
		}
		player.sendMessage(ChatColor.RED + "This is not your district!");
	    } else if (split[0].equalsIgnoreCase("cancel")) {
		DistrictRegion d = players.getInDistrict(playerUUID);
		if (d != null) {
		    if (d.getOwner().equals(playerUUID)) {
			player.sendMessage(ChatColor.GOLD + "District is no longer for sale or rent.");
			d.setForSale(false);
			d.setForRent(false);
			d.setPrice(0D);
			return true;
		    }
		    player.sendMessage(ChatColor.RED + "This is not your district!");
		} else {
		    player.sendMessage(ChatColor.RED + "You are not in a district!"); 
		}
		return true;


	    }
	case 2:
	    if (split[0].equalsIgnoreCase("claim")) {
		// TODO: Put more checks into the setting of a district
		if (players.getInDistrict(playerUUID) != null) {
		    player.sendMessage(ChatColor.RED + "Move out of this district first.");
		    return true;
		}
		int blocks = 0;
		try {
		    blocks = Integer.parseInt(split[1]);
		} catch (Exception e) {
		    player.sendMessage(ChatColor.RED + "/district claim <number of blocks radius>");
		    return true;		    
		}
		// Check if they have enough blocks
		int blocksRequired = (blocks*2+1)*(blocks*2+1);
		if (blocksRequired > players.getBlockBalance(playerUUID)) {
		    player.sendMessage(ChatColor.RED + "You do not have enough blocks!");
		    player.sendMessage(ChatColor.RED + "Blocks available: " + players.getBlockBalance(playerUUID));
		    player.sendMessage(ChatColor.RED + "Blocks required: " + blocksRequired);
		    return true;  
		}
		if (blocks < 5) {
		    player.sendMessage(ChatColor.RED + "The minimum radius is 5 blocks");
		    return true;		    
		}
		// Find the corners of this district
		Location pos1 = new Location(player.getWorld(),player.getLocation().getBlockX()-blocks,0,player.getLocation().getBlockZ()-blocks);
		Location pos2 = new Location(player.getWorld(),player.getLocation().getBlockX()+blocks,0,player.getLocation().getBlockZ()+blocks);
		if (!plugin.checkDistrictIntersection(pos1, pos2)) {
		    DistrictRegion d = new DistrictRegion(plugin, pos1, pos2, playerUUID);
		    d.setEnterMessage("Welcome to " + player.getDisplayName() + "'s district!");
		    d.setFarewellMessage("Now leaving " + player.getDisplayName() + "'s district.");
		    plugin.getDistricts().add(d);
		    plugin.getPos1s().remove(playerUUID);
		    players.setInDistrict(playerUUID, d);
		    players.removeBlocks(playerUUID, blocksRequired);
		    player.sendMessage(ChatColor.GOLD + "District created!");
		    player.sendMessage(ChatColor.GOLD + "You have " + players.getBlockBalance(playerUUID) + " blocks left.");
		} else {
		    player.sendMessage(ChatColor.RED + "That sized district could not be made because it overlaps another district");		    		    
		}
		return true;
	    } else if (split[0].equalsIgnoreCase("sell")) { 
		DistrictRegion d = players.getInDistrict(playerUUID);
		if (d != null) {
		    if (d.getOwner().equals(playerUUID)) {
			double price = 0D;
			try {
			    price = Double.parseDouble(split[1]);
			} catch (Exception e) {
			    player.sendMessage(ChatColor.RED+"The price is invalid (must be >= "+ VaultHelper.econ.format(1D)+")");
			    return true;
			}
			if (price <1D) {
			    player.sendMessage(ChatColor.RED+"The price is invalid (must be >= "+ VaultHelper.econ.format(1D)+")");
			    return true;  
			}
			player.sendMessage(ChatColor.GOLD + "Putting district up for sale for " + VaultHelper.econ.format(price));
			d.setForSale(true);
			d.setPrice(price);
			d.setForRent(false);
			return true;
		    }
		    player.sendMessage(ChatColor.RED + "This is not your district!");
		} else {
		    player.sendMessage(ChatColor.RED + "You are not in a district!"); 
		}
		return true;

	    } else if (split[0].equalsIgnoreCase("rent")) { 
		DistrictRegion d = players.getInDistrict(playerUUID);
		if (d != null) {
		    if (d.getOwner().equals(playerUUID)) {
			double price = 0D;
			try {
			    price = Double.parseDouble(split[1]);
			} catch (Exception e) {
			    player.sendMessage(ChatColor.RED+"The rent is invalid (must be >= "+ VaultHelper.econ.format(1D)+")");
			    return true;
			}
			if (price <1D) {
			    player.sendMessage(ChatColor.RED+"The rent is invalid (must be >= "+ VaultHelper.econ.format(1D)+")");
			    return true;  
			}
			player.sendMessage(ChatColor.GOLD + "Putting district up for rent for " + VaultHelper.econ.format(price));
			d.setForRent(true);
			d.setForSale(false);
			d.setPrice(price);
			return true;
		    }
		    player.sendMessage(ChatColor.RED + "This is not your district!");
		} else {
		    player.sendMessage(ChatColor.RED + "You are not in a district!"); 
		}
		return true;
	    }

	}
	return false;
    }
}