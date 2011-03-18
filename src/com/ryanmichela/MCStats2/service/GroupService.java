package com.ryanmichela.MCStats2.service;
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

import org.bukkit.Server;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;

import com.nijiko.permissions.PermissionHandler;
import com.nijikokun.bukkit.Permissions.Permissions;

public class GroupService {

	private PermissionHandler permissions;
	
	public GroupService(Server server) {
		Plugin test = server.getPluginManager().getPlugin("Permissions");

	    if (test != null) {
	        permissions = ((Permissions)test).getHandler();
	    } else {
	        server.getLogger().info("[MCStats] Permissions plugin not detected, disabling group support.");
        }
	}
	
	public String[] getGroups(Player player) {
		if(permissions != null) {
			return permissions.getGroups(player.getWorld().getName(), player.getName());
		} else {
			return new String[]{};
		}
	}
}
