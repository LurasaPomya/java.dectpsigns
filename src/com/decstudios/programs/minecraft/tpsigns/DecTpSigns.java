package com.decstudios.programs.minecraft.tpsigns;

import java.io.BufferedInputStream;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.util.logging.Logger;

import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;


public class DecTpSigns extends JavaPlugin {
	
	Logger log = Logger.getLogger("Minecraft");	
	//Main Directory of the plugin, where files are saved
	protected static final String MAINDIRECTORY = "plugins/DecTpSigns";	
	//Prefix to show up for all log messages
	protected static final String LOGGERPREFIX = "[DecMods - TPSigns] ";	
	//Version File for storing plugin version for updating
	protected static final File VERSIONFILE = new File(MAINDIRECTORY + File.separator + "VERSION");
	
	protected static final double CURRENTVERSION = 3.2;
	
	//Stuff to do when plugin loads
	public void onLoad() {
		this.getDataFolder().mkdirs();
	}
	
	//Stuff to do when plugin is disabled
	public void onDisable() {		
		this.saveConfig();
		log.info(LOGGERPREFIX + "Disabled");		
	}
	
	//Stuff to do when plugin is enabled
	public void onEnable() {		
		this.getConfig();
		new File(MAINDIRECTORY).mkdir();		
		
		//Not Used, saved for later
		if(!VERSIONFILE.exists()) {
			writeVersion();
		} 
		else 
		{
			@SuppressWarnings("unused")
			String vnum = readVersion();
			updateFromLevel(-1);
		}
		
		//Creates the plugin manager needed and registers the listeners needed by this plugin
		PluginManager pm = this.getServer().getPluginManager();
		pm.registerEvents(new DecTpSignsPlayerListener(this), this);
		pm.registerEvents(new DecTpSignsBlockListener(this), this);
		pm.
		
		log.info(LOGGERPREFIX + "Enabled");
	}

	//Stuff to do when someone uses a command (only processes /dectp stuff after first If Statement	
	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
		if (cmd.getName().equalsIgnoreCase("dectp") && (args.length != 0))
		{
			if (args[0].equalsIgnoreCase("setuse") && args.length == 3)
			{
				if (args[2].equalsIgnoreCase("true") || args[2].equalsIgnoreCase("false"))
				{
					this.getConfig().set("worldlist." + args[1], Boolean.parseBoolean(args[2]));
					sender.sendMessage("Set use of signs on " + args[1] + " to " + args[2]);
					this.saveConfig();
					return true;
				}
				else
				{
					sender.sendMessage("Usage: /dectp setuse <world> <true|false>");
					return true;
				}
			}
			else if (args[0].equalsIgnoreCase("oponly") && args.length == 2)
			{
				if (args[1].equalsIgnoreCase("true") || args[1].equalsIgnoreCase("false"))
				{
					this.getConfig().set("general.oponly", args[1]);
					sender.sendMessage("Set OpOnly to " + args[1]);
					this.saveConfig();
					return true;
				}
				else
				{
					sender.sendMessage("Usage: /dectp oponly <true|false>");
					return true;
				}
			}
			else if (args[0].equalsIgnoreCase("status") && args.length == 2)
			{
				if (this.getConfig().contains("worldlist." + args[1]))
				{
					if (this.getConfig().getBoolean("allowedsigns." + args[1]))
						sender.sendMessage("Use of TP's on " + args[1] + " is allowed.");
					else
						sender.sendMessage("Use of TP's on " + args[1] + " is not allowed.");
					
					return true;
				}
				else
				{
					sender.sendMessage("Use of TP's on " + args[1] + "is allowed.");
					return true;
				}
			}
			else if (args[0].equalsIgnoreCase("currentworld"))
			{
				Player tempPlayer = (Player)sender;
				sender.sendMessage("You are currently in world: " + tempPlayer.getWorld().getName().replace(' ', '_'));
				return true;
			}
			else if (args[0].equalsIgnoreCase("usesign") && args.length == 3)
			{
				Player tempPlayer = (Player)sender;
				this.getConfig().set(args[1], args[2]);
				tempPlayer.sendMessage("turned " + args[1] + " use to " + args[2]);
				this.saveConfig();
				return true;
			}
			else if (args[0].equalsIgnoreCase("tpcost") && args.length == 2 && (args[1].equalsIgnoreCase("false") || args[1].equalsIgnoreCase("true")))
			{
				this.getConfig().set("general.tpcost", args[1]);
				Player tempPlayer = (Player)sender;
				tempPlayer.sendMessage("TPCost set to " + args[1]);
				this.saveConfig();
				return true;
			}
			else if (args[0].equalsIgnoreCase("tpcostmat") && args.length == 2)
			{
				this.getConfig().set("general.tpcostmat", args[1]);
				Player tempPlayer = (Player)sender;
				tempPlayer.sendMessage("TPCost set to " + args[1]);
				this.saveConfig();
				return true;
			}
		}
		return false;
	}

	//Method for writing version info to file
	public void writeVersion() {
		try {
			VERSIONFILE.createNewFile();
			BufferedWriter vout = new BufferedWriter(new FileWriter(VERSIONFILE));
			vout.write(this.getDescription().getVersion());
			vout.close();
		} catch (IOException ex) {
			ex.printStackTrace();
		} catch (SecurityException ex) {
				ex.printStackTrace();
		}
	}
	
	//Method for reading version info from file
	public String readVersion() {
		byte[] buffer = new byte[(int) VERSIONFILE.length()];
		BufferedInputStream f = null;
		try {
			f = new BufferedInputStream(new FileInputStream(VERSIONFILE));
			f.read(buffer);
		} catch (FileNotFoundException ex) {
			ex.printStackTrace();
		} catch (IOException ex) {
			ex.printStackTrace();
		} finally {
			if (f != null) try { f.close(); } catch (IOException ignored) { }
		}
	 
		return new String(buffer);
	}

	//Method to update from a specific version, not currently used
	public void updateFromLevel(int updateLevel) {
		//If we say we are updating from -1, no special code needs to be run, we just need to update the VERSION file.
		if(updateLevel == -1) {
			writeVersion();
		}
		return;
	}
}