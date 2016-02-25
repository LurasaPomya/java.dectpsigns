package com.decstudios.programs.minecraft.tpsigns; 

import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.block.Sign;

public class DecTpSignsBlockListener implements Listener {
	//Plugin instance of DecTpSigns plugin
	public static DecTpSigns plugin;
	
	//Constructor
	public DecTpSignsBlockListener(DecTpSigns instance) {
		plugin = instance;
	}

	//Handles Block Breaking Events to see if plugin needs to do anything with it
	@EventHandler
	public void onBlockBreak(BlockBreakEvent event) 
	{		
		Player player = event.getPlayer();
		Block blockBroken = event.getBlock();
		
		//Checks if the block broken is a sign
		if (blockBroken.getState() instanceof Sign)
		{			
			Sign sign = (Sign)blockBroken.getState();
			
			//This replaces spaces and turns them into _
			String line1 = sign.getLine(0).toLowerCase().replace(' ', '_');
			String line2 = sign.getLine(1).toLowerCase().replace(' ', '_');
			
			if (line1.equals("tpsign_hub"))
			{
				if (plugin.getConfig().contains("signs.normal." + line2))
				{
					plugin.getConfig().set("signs.normal." + line2, null);
					player.sendMessage("Removed " + line2 + " from the Hub List");
				}
				else
				{
					player.sendMessage("This is a TPSign Hub but wasn't on the list of hubs...");
				}
			}
			else if (line1.equals("tpsign_ophub"))
			{
				if (plugin.getConfig().contains("signs.op." + line2))
				{
					plugin.getConfig().set("signs.op." + line2, null);
					player.sendMessage("Removed " + line2 + " from the OpHub List");
				}
				else
				{
					player.sendMessage("This is a TPSign OpHub but wasn't on the list of hubs...");
				}
			}
		}	
	}

	//Handles Block Placing events to see if plugin needs to do anything with it
	@EventHandler
	public void onBlockPlace(BlockPlaceEvent event)
	{
		
	}
}