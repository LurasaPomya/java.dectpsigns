package com.decstudios.programs.minecraft.tpsigns;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.block.Sign;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;

public class DecTpSignsPlayerListener implements Listener {
	
	//Plugin instance of DecTpSigns plugin
	public static DecTpSigns plugin;
	
	//Constructor
	public DecTpSignsPlayerListener(DecTpSigns instance)
	{
		plugin = instance;
	}
	
	//Handles player interact events to see if they are signs and if they need to be processed by this plugin
	@EventHandler
	public void onPlayerInteract(PlayerInteractEvent event) {
		
		Block clickedBlock = event.getClickedBlock();
		Player player = event.getPlayer();
		
		//Checks to make sure the interaction is a right click and that it's not air and an actual block
		if (event.getAction() != Action.RIGHT_CLICK_AIR && event.getAction() == Action.RIGHT_CLICK_BLOCK)
		{
			//Checks to make sure block right clicked is a sign
			if (clickedBlock.getState() instanceof Sign)
			{
				Sign clickedSign = (Sign)clickedBlock.getState();
				String[] lines = new String[4];
				
				//raplces spaces in the lines of text on the sign with _'s
				lines[0] = clickedSign.getLine(0).toLowerCase().replace(' ', '_');
				lines[1] = clickedSign.getLine(1).toLowerCase().replace(' ', '_');
				lines[2] = clickedSign.getLine(2).toLowerCase().replace(' ', '_');
				lines[3] = clickedSign.getLine(3).toLowerCase().replace(' ', '_');
				
				//Checks if the top of the sign has tpsign, so we know it's a dectp sign and not just any other boring sign
				if (lines[0].contains("tpsign"))
				{
					//checks if oponly is active and then checks if the player is not an op. Sends message that they need to be an op to use signs then
					if (plugin.getConfig().getBoolean("general.oponly") && !event.getPlayer().isOp())
					{
						event.getPlayer().sendMessage("Op-Only is active and you are not an Op. You cannot use DecTpSigns.");
						return;
					}
					else if (!plugin.getConfig().contains("worldlist." + player.getWorld().getName()))	//Checks if dectpsigns is usable on the selected world
					{
						if (!plugin.getConfig().getBoolean("worldlist." + player.getWorld().getName()))
						{
							player.sendMessage("DecTPSigns are not allowed in this world");
							return;
						}
					}
					else if (plugin.getConfig().getBoolean("general.checkallowed"))		//Checks if sign type is usable on the server
					{
						String checkString;
						if (lines[0].contains("world"))
							checkString = "world" + lines[1];
						else
							checkString = lines[0].split("_")[1];
						
						if (!plugin.getConfig().getBoolean("allowedsigns." + checkString))
						{							
							player.sendMessage("That sign is not allowed to be used.");
							return;
						}																			
					}
					
					//Processes world TP Signs (Not Point or hub signs though)
					World currentWorld = player.getWorld();
					if (lines[0].endsWith("world"))
					{
						if (lines[1].equalsIgnoreCase("branch") || lines[1].equalsIgnoreCase("opbranch") || lines[1].equalsIgnoreCase("jump") || lines[1].equalsIgnoreCase("opjump"))
						{
							World targetWorld = null;
							if (plugin.getServer().getWorlds().contains(lines[2]))		//Checks if the world in sign is actually on the server
							{
								targetWorld = plugin.getServer().getWorld(lines[2]);
							}
							else
							{
								player.sendMessage("World Not found or sign is not set up right, 3rd line for world name.");
								return;
							}
							
							//Checks what type of sign it is and processes it.
							String tpString = lines[3];
							
							//Checks if sign is op or normal. This process is creating a string that will be used to check a yml file for the sign and it's coords.
							if (lines[1].equalsIgnoreCase("opbranch") || lines[1].equalsIgnoreCase("opjump"))
								tpString = "op." + tpString;
							else
								tpString = "normal." + tpString;
							
							//Checks for the actual sign in config.yml and teleports the player. If the sign was a jump sign it also removes the sign and places a sign in the players inventory.
							if (plugin.getConfig().contains("signs." + tpString))
							{
								if (checkForCost(event))
								{
									Location tpLoc = new Location(targetWorld, plugin.getConfig().getDouble("signs." + tpString + ".x"), plugin.getConfig().getDouble("signs." + tpString + ".y"), plugin.getConfig().getDouble("signs." + tpString + ".z"));
									player.teleport(tpLoc);
									if (lines[1].equalsIgnoreCase("jump") || lines[1].equalsIgnoreCase("opjump"))
									{
										ItemStack is = new ItemStack(Material.SIGN);
										is.setAmount(1);
										player.getInventory().addItem(is);
										clickedBlock.setType(Material.AIR);
									}
								}
						}
							else
								player.sendMessage("No Hub found by that name.");				
						}			
					}
					else if (lines[0].endsWith("opjump") || lines[0].endsWith("jump") || lines[0].endsWith("opbranch") || lines[0].endsWith("branch"))		//Processes non-world signs (Not point or hub signs though
					{
						String tpString = lines[1];
						
						if ((lines[0].endsWith("opjump") || lines[0].endsWith("opbranch")) && player.isOp())
							tpString = "op." + tpString;
						else
							tpString = "normal." + tpString;
						
						if (plugin.getConfig().contains("signs." + tpString))
						{
							if (checkForCost(event))
							{
								Location tpLoc = new Location(currentWorld, plugin.getConfig().getDouble("signs." + tpString + ".x"), plugin.getConfig().getDouble("signs." + tpString + ".y"), plugin.getConfig().getDouble("signs." + tpString + ".z"));
								player.teleport(tpLoc);
								if (lines[0].endsWith("jump") || lines[0].endsWith("opjump"))
								{
									ItemStack is = new ItemStack(Material.SIGN);
									is.setAmount(1);
									player.getInventory().addItem(is);
									clickedBlock.setType(Material.AIR);
								}
							}
						}
						else
							player.sendMessage("No Hub found by that name.");
					}
					else if (lines[0].endsWith("point") || lines[0].endsWith("pointjump")) 			//Processes point signs
					{
						if (checkForCost(event))
						{
							Location tpLoc = new Location(currentWorld,Double.parseDouble(lines[1]), Double.parseDouble(lines[2]), Double.parseDouble(lines[3]));
							player.teleport(tpLoc);
							if (lines[0].equalsIgnoreCase("pointjump"))
							{
								ItemStack is = new ItemStack(Material.SIGN);
								is.setAmount(1);
								player.getInventory().addItem(is);
								clickedBlock.setType(Material.AIR);
							}
						}
					}
					else if (lines[0].equals("tpsign_hub") || lines[0].equals("tpsign_ophub"))		//Processes hub signs
					{
						if (lines[0].equals("tpsign_hub"))		//Normal hubs
						{
							//Checks to see if this sign already exists on the config.yml and theoretically means it's already in the world. Adds hub if it's not in the .yml gives error msg otherwise
							if (!plugin.getConfig().contains("signs.normal." + lines[1]))
							{
								plugin.getConfig().set("signs.normal." + lines[1] + ".x", player.getLocation().getX());
								plugin.getConfig().set("signs.normal." + lines[1] + ".y", player.getLocation().getY());
								plugin.getConfig().set("signs.normal." + lines[1] + ".z", player.getLocation().getZ());
								player.sendMessage("Added " + lines[1] + " to the Hub List");
							}
							else
							{
								player.sendMessage("That hub already exists, please destroy the old one first.");
								event.getClickedBlock().setType(Material.AIR);
							}								
						}
						else if (lines[0].equals("tpsign_ophub"))	//Op Only hubs
						{
							if (!plugin.getConfig().contains("signs.op." + lines[1]))
							{
								plugin.getConfig().set("signs.op." + lines[1] + ".x", player.getLocation().getX());
								plugin.getConfig().set("signs.op." + lines[1] + ".y", player.getLocation().getY());
								plugin.getConfig().set("signs.op." + lines[1] + ".z", player.getLocation().getZ());
								player.sendMessage("Added " + lines[1] + " to the OpHub List");
							}
							else
							{
								player.sendMessage("That hub already exists, please destroy the old one first.");
								event.getClickedBlock().setType(Material.AIR);
							}
						}			
					}
					event.setCancelled(true);
				}
			}
		}		
	}
	
	@SuppressWarnings("deprecation")
	private boolean checkForCost(PlayerInteractEvent event)
	{	
		if (plugin.getConfig().getBoolean("general.tpcost"))
		{
			Material costMaterial = Material.getMaterial(plugin.getConfig().getInt("general.tpcostmat"));
			
			if (!event.getPlayer().getInventory().contains(costMaterial))
			{
				event.getPlayer().sendMessage("You do not have the required cost to use this TPSign. You need " + costMaterial.toString());
				return false;
			}
			else
			{
				Player tempPlayer = event.getPlayer();
				PlayerInventory tempInventory = tempPlayer.getInventory();		//Gets the players inventory and places it into a temp inventory we will turn into the players inventory
				int tempInt = tempInventory.first(costMaterial.getId());
				ItemStack tempStack = tempInventory.getItem(tempInt);		//Gets the first stack of the cost material
				tempStack.setAmount(tempStack.getAmount() - 1);		//Removes 1 of the cost material
				event.getPlayer().getInventory().setItem(tempInt, tempStack); //Replaces the item stack in the players inventory
				event.getPlayer().updateInventory();	//Using depreciated method because to my knowledge there is no replacement
				return true;				
			}
		}
		else
			return true;
	}
}
