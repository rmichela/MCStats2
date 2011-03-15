package com.ryanmichela.MCStats2.model;

import org.bukkit.util.config.Configuration;
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


public class StatsConfig {
	private Configuration config;
	private String dataFolder;
	
	public StatsConfig (Configuration config, String dataFolder) {
		this.config = config;
		this.dataFolder = dataFolder + "/";
	}
	
	//base configuration
	public String getStatsCacheFile(){
		return dataFolder + config.getString("statsCacheFile", "statsCache");
	}
	
	public String getStatsBaseResource() {
		return config.getString("statsBaseResource", "mcstats"); 
	}
	
	public String getResourceSaveDirectory() {
		return dataFolder + config.getString("resourceSaveDirectory", "stats");
	}
	
	public int getSecondsBetweenSaves() {
		return config.getInt("secondsBetweenSaves", 60);
	}
	
	public int getSecondsBetweenPageRefreshes() {
		return config.getInt("secondsBetweenPageRefreshes", 60);
	}
	
	public boolean getIgnoreGrouplessPlayers () {
		return config.getBoolean("ignoreGrouplessPlayers", false);
	}
	
	public String getHttpPostUrl() {
		return config.getString("httpPostUrl", "");
	}
	
	public int getHttpPostConnectTimeout() {
		return config.getInt("httpPostConnectTimeout", 300);
	}
		
	//webserver configuration
	public boolean getWebserverEnabled() {
		return config.getBoolean("webserverEnabled", false);
	}
	
	public int getHttpBacklog() {
		return config.getInt("httpBacklog", 8);
	}
	
	public int getHttpPort() {
		return config.getInt("httpPort", 8080);
	}
	
	public String getHttpServerContextRoot() {
		return config.getString("httpServerContextRoot", "/");
	}
	
	public String[] getPlayersToPurge() {
		return config.getString("playersToPurge", "").split(" ");
	}
	
	public void clearPlayersToPurge() {
		config.setProperty("playersToPurge", "");
		config.save();
	}
	
	public boolean getOverwriteHtmlReport() {
		return config.getBoolean("overwriteHtmlReport", true);
	}
	
	public boolean getResetPlaytime() {
		return config.getBoolean("resetPlaytime", false);
	}
	
	public void clearResetPlaytime() {
		config.setProperty("resetPlaytime", false);
		config.save();
	}
	
	public boolean getEnableSerializerCache() {
		return config.getBoolean("enableSerializerCache", true);
	}
}
