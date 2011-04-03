package com.ryanmichela.MCStats2.controller;
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

import java.util.Date;
import java.util.HashMap;

import org.bukkit.block.Block;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import com.ryanmichela.MCStats2.StatsPlugin;
import com.ryanmichela.MCStats2.model.PlayerStatistics;
import com.ryanmichela.MCStats2.model.StatsConfig;

public class StatsController {
	private HashMap<String, PlayerStatistics> stats;
	private StatsConfig config;
	
	public StatsController(HashMap<String, PlayerStatistics> stats, StatsConfig config) {
		this.stats = stats;
		this.config = config;
	}
	
	//Mark the player's connect time in the playclockStart field of stats
	public void logIn(Player player) {
		if(ignorePlayer(player)) return;
		PlayerStatistics ps = getPlayerStats(player);
		ps.sessionMarkTime = new Date();
		ps.playerGroups = StatsPlugin.groupService.getGroups(player);
		ps.lastLogin = new Date();
	}
	
	//Logs in any players who are active when the mod starts
	public void logInOnlinePlayers() {
		for(Player player : StatsPlugin.currentServer.getOnlinePlayers()) {
			logIn(player);
		}
	}
	
	//Add total play time to the player's secondsOnServer
	public void logOut(Player player) {
		if(ignorePlayer(player)) return;
		PlayerStatistics ps = getPlayerStats(player);
		ps.flushSessionPlaytime();
		ps.sessionMarkTime = null;
	}
	
	//Logs out all players - called at shutdown
	public void logOutAllPlayers() {
		synchronized(stats) {
			for(PlayerStatistics ps : stats.values()) {
				ps.flushSessionPlaytime();
				ps.sessionMarkTime = null;
			}
		}
	}
	
	//Note that the player has traveled a meter
	public void travelAMeter(Player player) {
		if(ignorePlayer(player)) return;
		PlayerStatistics ps = getPlayerStats(player);
		ps.metersTraveled++;
	}
	
	//Note that the player has placed a block
	public void placeABlock(Player player, Block block) {
		if (block.getType().getId() > 0) {
			if(ignorePlayer(player)) return;
			PlayerStatistics ps = getPlayerStats(player);
			if (!ps.blocksPlaced.containsKey(block.getType().getId())) {
				ps.blocksPlaced.put(block.getType().getId(), 0L);
			}
			ps.blocksPlaced.put(block.getType().getId(),
					ps.blocksPlaced.get(block.getType().getId()) + 1);
		}
	}
	
	//Note that the player has destroyed a block
	public void destroyABlock(Player player, Block block) {
		if (block.getType().getId() > 0) {
			if(ignorePlayer(player)) return;
			PlayerStatistics ps = getPlayerStats(player);
			if (!ps.blocksDestroyed.containsKey(block.getType().getId())) {
				ps.blocksDestroyed.put(block.getType().getId(), 0L);
			}
			ps.blocksDestroyed.put(block.getType().getId(),
					ps.blocksDestroyed.get(block.getType().getId()) + 1);
		}
	}
	
	//Note that the player disposed of an item
	public void dropAnItem(Player player, ItemStack stack) { 
		if (stack.getType().getId() > 0) {
			if(ignorePlayer(player)) return;
			PlayerStatistics ps = getPlayerStats(player);
			if (!ps.itemsDropped.containsKey(stack.getType().getId())) {
				ps.itemsDropped.put(stack.getType().getId(), 0L);
			}
			ps.itemsDropped.put(stack.getType().getId(),
					ps.itemsDropped.get(stack.getType().getId()) + stack.getAmount());
		}
	}
	
	//Note that the player has died
	public void die(Player player) {
		if(ignorePlayer(player)) return;
		PlayerStatistics ps = getPlayerStats(player);
		ps.deaths++;
	}
	
	//Note that the player killed something
	public void kill(Player attacker, LivingEntity victim) {
		if(ignorePlayer(attacker)) return;
		PlayerStatistics ps = getPlayerStats(attacker);
		
		if(victim instanceof Player) {
			// Increment the correct player kill counter
			String victimName = ((Player)victim).getName();
			if(!ps.playerKills.containsKey(victimName)) {
				ps.playerKills.put(victimName, 0L);
			}
			ps.playerKills.put(victimName, ps.playerKills.get(victimName) + 1);
		} else {
			String victimName = victim.getClass().getName();
			if(!ps.creatureKills.containsKey(victimName)) {
				ps.creatureKills.put(victimName, 0L);
			}
			ps.creatureKills.put(victimName, ps.creatureKills.get(victimName) + 1);
		}
	}
	
	// Return a player's total play time
	public String getPlaytime(Player player) {
		PlayerStatistics ps = getPlayerStats(player);
		return ps.getTotalPlaytime();
	}
	
	private PlayerStatistics getPlayerStats(Player player)
	{
		if(!stats.containsKey(player.getName()))
		{
			synchronized (stats) {
				PlayerStatistics newStats = new PlayerStatistics();
				newStats.playerName = player.getName();
				newStats.playerSince = new Date(); //initialize to now
				stats.put(player.getName(), newStats);
			}
		}
		return stats.get(player.getName());
	}
	
	// Ignore players with no group if ignoreGrouplessPlayers is true.
	private boolean ignorePlayer(Player player) {
		return StatsPlugin.groupService.getGroups(player).length == 0 && config.getIgnoreGrouplessPlayers();
	}
}
