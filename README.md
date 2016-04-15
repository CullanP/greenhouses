== Greenhouses ==

Greenhouses is an essential plugin to power-up your SkyBlock or AcidIsland world! It enables players to build their own biome greenhouses complete with weather, friendly mob spawning, unique plant growth and even block erosion!

Greenhouses are made out of glass and must contain the blocks found in the Biome Recipe to be valid. There is a recipe GUI. Once built, the greenhouse can be used to grow plants with bonemeal, and it may spawn biome-specific mobs. If you include a hopper with water in it, snow will form inside the greenhouse when it rains. If you put bonemeal in the hopper, biome-specific plants will grow. Some blocks can also transform over time due to "erosion".

Features

* Craft your own self-contained biome greenhouse on an island (or elsewhere if you like)
* Greenhouses can grow plants that cannot normally be grown, like sunflowers
* Friendly mobs can spawn if your greenhouse is well designed - need slimes? Build a swamp greenhouse!
* Blocks change in biomes - dirt becomes sand in a desert, dirt becomes clay in a river, for example.
* Greenhouses can run in multiple worlds.
* Easy to use GUI shows greenhouse recipes (/g)
* Admins can fully customize biomes and recipes

How to Build A Greenhouse (Simple version)

1. Make glass blocks and build a rectangular set of walls with a flat roof.
2. Put a hopper in the wall or roof.
3. Put a door in the wall so you can get in and out.
4. Type /g and read the rules for the greenhouse you want.
5. Exit the GUI and place blocks, water, lava and ice so that you make your desired biome.
6. Type /g again and click on the biome to make it.

Once made:

* Use bonemeal to grow small plants on grass immediately in the greenhouse.
* Or place bonemeal in the hopper to have the greenhouse sprinkle bonemeal automatically. Come back later to see what grows!
* Place a bucket of water (or more) in the hopper to cause snow to fall in cold biomes. Snow will fall when it rains in the world. Each snowfall empties one bucket of water.
* Friendly biome-specific mobs may spawn in your greenhouse - the usual rules apply (be more than 24 blocks away).

FAQ

* Can I use stained glass? Yes, you can. It's pretty.
* Can I fill my greenhouse full of water? Yes. That's an ocean.
* Will a squid spawn there? Maybe... okay, yes it will, if it's a big enough ocean.
* How do I place a door high up in the wall if the wall is all glass? Place it on a hopper.
* How do I place a door on a hopper? Crouch and then place it.
* Can I use metal doors? Yes.
* Can I grow swamp flowers with this? Yes. Make a swamp biome and use bonemeal.
* How much bonemeal is used to grow plants? One per successful plant.
* How much water do I need to put into the hopper to make it snow? One bucket of water (just the water) is used up every time it rains. This only happens in cold biomes.
* Can I build a Nether greenhouse? Try it and see... (Actually, you may need permission)
* What kind of mobs spawn in the biomes? It's what you would expect, wolves in Cold Taiga, horses on plains, etc.

Required Plugin

1. Vault for permissions - make sure you use the latest version!

Installation and Configuration

1. Download and install Vault if you haven't done so already
2. Download the plugin
3. Place into your plugins folder
4. Restart your server (or reload plugins)
5. The plugin will make a folder called Greenhouses. Open that folder.
6. Check config.yml and edit to be what you want, note the list of world names.
7. Configure the biomes.yml if you like (advanced) Here is an example one.
8. Type /gadmin reload in the game to reload the config or restart the server.
9. Done! To make your first greenhouse, build a glass box and type /g make to see what kind of greenhouse you get. Type /g recipe to see the recipes.

Upgrading

Read the file release notes for changes and instructions on how to upgrade.

Player Commands

/greenhouse or /g can be used for short.
/greenhouse help - lists these commands
/greenhouse make: Tries to make a greenhouse
/greenhouse remove: Removes a greenhouse that you are standing in if you are the owner
/greenhouse list: Lists all the recipes available
/greenhouse recipe: Displays the recipe GUI - clicking on a recipe will try to make a greenhouse

Admin Commands

/gadmin reload : Reloads config files
/gadmin info <player>: provides info on the player
/gadmin info: provides info on the greenhouse you are in
Permissions

Permission to use specific biomes can be added in biomes.yml.

For example, the permission for the Nether (Hell) biome is greenhouses.biome.nether and is set here:

 HELL:
    permission: greenhouses.biome.nether

The permission can also be a rank permission, for example, "myserver.VIPs" or anything you like.

General permissions are:

  greenhouses.player:
     description: Gives access to player commands
     default: true
  greenhouses.admin:
     description: Gives access to admin commands
     default: op

Stats Disclosure

This plugin utilizes a plugin metrics system, which means that the following information is collected and sent to mcstats.org:

A unique identifier
The server's version of Java
Whether the server is in offline or online mode
The plugin's version
The server's version
The OS version/name and architecture
The core count for the CPU
The number of players online
The Metrics version
Visit mcstats.org if you would like to learn more about the stats collected. Go into /plugins/PluginMetrics/config.yml to disable stat collecting if you wish.