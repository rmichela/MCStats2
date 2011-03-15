package com.ryanmichela.MCStats2;
//Copyright (C) 2010  Ryan Michela
//
//This program is free software: you can redistribute it and/or modify
//it under the terms of the GNU General Public License as published by
//the Free Software Foundation, either version 3 of the License, or
//(at your option) any later version.
//
//This program is distributed in the hope that it will be useful,
//but WITHOUT ANY WARRANTY; without even the implied warranty of
//MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
//GNU General Public License for more details.
//
//You should have received a copy of the GNU General Public License
//along with this program.  If not, see <http://www.gnu.org/licenses/>.

import java.io.IOException;
import java.net.InetSocketAddress;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.bukkit.Server;
import org.bukkit.event.Event.Priority;
import org.bukkit.event.Event.Type;
import org.bukkit.plugin.java.JavaPlugin;

import com.ryanmichela.MCStats2.controller.PlayedCommand;
import com.ryanmichela.MCStats2.controller.StatsBlockListener;
import com.ryanmichela.MCStats2.controller.StatsController;
import com.ryanmichela.MCStats2.controller.StatsEntityListener;
import com.ryanmichela.MCStats2.controller.StatsPlayerListener;
import com.ryanmichela.MCStats2.model.StatsConfig;
import com.ryanmichela.MCStats2.model.StatsModel;
import com.ryanmichela.MCStats2.reporting.StatsHttpHandler;
import com.ryanmichela.MCStats2.reporting.StatsSerializer;
import com.sun.net.httpserver.*;

public class StatsPlugin extends JavaPlugin {

	private StatsModel model;
	private StatsConfig config;
	private StatsController controller;
	private static final Logger log = Logger.getLogger("Minecraft");
	
	public static Server currentServer;
	
	private ShutdownHook hook = new ShutdownHook();
	private HttpServer server;
	
	public StatsPlugin() {
		log.info("Starting MCStats");
	}

	
	
	@Override
	//Attach listener hooks
	public void onEnable() {
		currentServer = getServer();		
		config = new StatsConfig(getConfiguration(), this.getDataFolder().toString());
		model = new StatsModel(config, log);
		controller = new StatsController(config, model.getStats());
		
		// Configure the serializer cache
		StatsSerializer.enableSerializerCache = config.getEnableSerializerCache();
		
		// Register command
		getCommand("played").setExecutor(new PlayedCommand(controller));
		
		//configure event hooks
		StatsPlayerListener spl = new StatsPlayerListener(controller);
		getServer().getPluginManager().registerEvent(Type.PLAYER_JOIN, spl, Priority.Monitor, this);
		getServer().getPluginManager().registerEvent(Type.PLAYER_QUIT, spl, Priority.Monitor, this);
		getServer().getPluginManager().registerEvent(Type.PLAYER_KICK, spl, Priority.Monitor, this);
		getServer().getPluginManager().registerEvent(Type.PLAYER_MOVE, spl, Priority.Monitor, this);
		getServer().getPluginManager().registerEvent(Type.PLAYER_DROP_ITEM, spl, Priority.Monitor, this);
		
		StatsEntityListener sel = new StatsEntityListener(controller);
		getServer().getPluginManager().registerEvent(Type.ENTITY_DEATH, sel, Priority.Monitor, this);
		getServer().getPluginManager().registerEvent(Type.ENTITY_DAMAGED, sel, Priority.Monitor, this);
		
		StatsBlockListener sbl = new StatsBlockListener(controller);
		getServer().getPluginManager().registerEvent(Type.BLOCK_PLACED, sbl, Priority.Monitor, this);
		getServer().getPluginManager().registerEvent(Type.BLOCK_BREAK, sbl, Priority.Monitor, this);
		
		//purge any users marked for removal
		for(String playerName : config.getPlayersToPurge()) {
			model.purgePlayer(playerName);
		}
		config.clearPlayersToPurge();
		
		//reset all playtimes if requested
		if(config.getResetPlaytime()) {
			log.log(Level.INFO, "Resetting all player play times");
			model.resetAllPlaytimes();
			config.clearResetPlaytime();
		}
		
		//start the http server if it's enabled
		if (config.getWebserverEnabled()) {
			String resource = config.getStatsBaseResource();
			String contextRoot = config.getHttpServerContextRoot();
			
			log.log(Level.INFO, "Starting MCStats web server.");
			try {
				server = HttpServer.create(
						new InetSocketAddress(config.getHttpPort()),
						config.getHttpBacklog());
				server.createContext(contextRoot, new StatsHttpHandler(
						model, config));
				server.setExecutor(null); // creates a default executor
				server.start();
				log.log(Level.INFO, String.format(
						"Server stats available at http://[hostname]:%s%s%s, %s, %s, and %s",
						config.getHttpPort(),
						contextRoot,
						resource + ".xml",
						resource + ".json",
						resource + ".js",
						resource + ".html"));
			} catch (IOException e) {
				log.log(Level.SEVERE, "MCStats failed to start http server", e);
			}
		}
		//register a shutdown hook
		Runtime.getRuntime().addShutdownHook(hook);
		controller.logOutAllPlayers();
		controller.logInOnlinePlayers();
		model.startPersisting();
	}

	@Override
	//Detach listener hooks
	public void onDisable() {
		Runtime.getRuntime().removeShutdownHook(hook);
		controller.logOutAllPlayers();
		model.stopPersisting();
		
		model.saveStats();
		model.saveUserFiles();
		
		//stop the http server if it's enabled
		if(config.getWebserverEnabled()) {
			log.log(Level.INFO, "MCStats stopping web server.");
			server.stop(1);
			server.removeContext(config.getHttpServerContextRoot());
			server = null;
		}
	}
	
	private class ShutdownHook extends Thread {
		public void run() { 
			controller.logOutAllPlayers();
			model.saveStats(); 
			System.out.println("MCStats persisting player statistics on exit.");
		}
	}

	@Override
	public void onLoad() {
		// TODO Auto-generated method stub
		
	}

}
