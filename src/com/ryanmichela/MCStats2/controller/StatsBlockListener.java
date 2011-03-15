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

import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockListener;
import org.bukkit.event.block.BlockPlaceEvent;


public class StatsBlockListener extends BlockListener {

	private StatsController controller;
	
	public StatsBlockListener(StatsController controller) {
		this.controller = controller;
	}
	
	@Override
	public void onBlockPlace(BlockPlaceEvent event) {
		if(!event.isCancelled()) {
			controller.placeABlock(event.getPlayer(), event.getBlock());
		}
	}

	@Override
	public void onBlockBreak(BlockBreakEvent event) {
		if(!event.isCancelled()) {
			controller.destroyABlock(event.getPlayer(), event.getBlock());
		}
	}

}
