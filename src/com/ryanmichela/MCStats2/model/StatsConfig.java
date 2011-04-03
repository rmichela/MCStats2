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
		return config.getString("statsCacheFile", dataFolder + "statsCache");
	}
	
	public String getStatsBaseResource() {
		return config.getString("statsBaseResource", "mcstats"); 
	}
	
	public String getResourceSaveDirectory() {
		return config.getString("resourceSaveDirectory", dataFolder + "stats");
	}
	
	public int getSecondsBetweenSaves() {
		return config.getInt("secondsBetweenSaves", 60);
	}
	
	public int getSecondsBetweenPageRefreshes() {
		return config.getInt("secondsBetweenPageRefreshes", 60);
	}
	
	public String[] getIgnoreGroups () {
		return spaceSplit(config.getString("ignoreGroups", ""));
	}
	
	public boolean getIgnoreGrouplessPlayers() {
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
		return spaceSplit(config.getString("playersToPurge", ""));

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
	
	////////////////////////////
	
	public static String getInitialConfig() {
		StringBuilder sb = new StringBuilder();
		sb.append("# Check README for more settings\n");
		sb.append("# https://github.com/rmichela/MCStats2\n\n");
		sb.append("resourceSaveDirectory: plugins/MCStats2/stats\n");
		sb.append("statsBaseResource: mcstats\n");
		sb.append("# ignoreGroups: default\n");
		sb.append("# ignoreGrouplessPlayers: true\n");
		sb.append("webserverEnabled: false");
		return sb.toString();
	}
	
	private String[] spaceSplit(String str) {
		String[] splits = str.split(" ");
		if(splits[0] == "") {
			return new String[]{};
		} else {
			return splits;
		}
	}
}
