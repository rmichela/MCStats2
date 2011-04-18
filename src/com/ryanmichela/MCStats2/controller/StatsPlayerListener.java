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

import org.bukkit.Location;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerKickEvent;
import org.bukkit.event.player.PlayerListener;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.event.player.PlayerQuitEvent;


public class StatsPlayerListener extends PlayerListener {

	private StatsController controller;
	
	public StatsPlayerListener(StatsController controller) {
		this.controller = controller;
	}
	
	@Override
	public void onPlayerJoin(PlayerJoinEvent event) {
		controller.logIn(event.getPlayer());
	}

	@Override
	public void onPlayerQuit(PlayerQuitEvent event) {
		controller.logOut(event.getPlayer());
	}

	@Override
	public void onPlayerKick(PlayerKickEvent event) {
		if(!event.isCancelled()) {
			//log out players who are banned so they don't keep accumulating play time
			controller.logOut(event.getPlayer());
		}
	}

	@Override
	public void onPlayerMove(PlayerMoveEvent event) {
		if(!event.isCancelled() && !samePlace(event.getFrom(), event.getTo())) {
			controller.travelAMeter(event.getPlayer());
		}
	}

	@Override
	public void onPlayerDropItem(PlayerDropItemEvent event) {
		if(!event.isCancelled()) {
			controller.dropAnItem(event.getPlayer(), event.getItemDrop().getItemStack());
		}
	}
	
	private boolean samePlace(Location l1, Location l2) {
		return (l1.getBlockX() == l2.getBlockX()) &&
		       (l1.getBlockY() == l2.getBlockY()) &&
		       (l1.getBlockZ() == l2.getBlockZ());
	}

}
