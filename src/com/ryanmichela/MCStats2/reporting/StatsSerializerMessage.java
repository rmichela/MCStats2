package com.ryanmichela.MCStats2.reporting;
import java.util.ArrayList;
import java.util.List;

import com.ryanmichela.MCStats2.model.PlayerStatistics;

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

public class StatsSerializerMessage {
	private List<OnlinePlayer> playersOnline;
	private List<PlayerStatistics> playerStats;
	
	public StatsSerializerMessage()
	{
		playersOnline = new ArrayList<OnlinePlayer>();
		playerStats = new ArrayList<PlayerStatistics>();
	}
	
	public List<OnlinePlayer> getPlayersOnline() {
		OnlinePlayer noOne = new OnlinePlayer();
		noOne.playerName = "No One";
		ArrayList<OnlinePlayer> noOneList = new ArrayList<OnlinePlayer>();
		noOneList.add(noOne);
		
		return playersOnline.size() == 0 ? noOneList : playersOnline;
	}
	
	public void setPlayersOnline(List<OnlinePlayer> playerList) {
		this.playersOnline = playerList;
	}
	
	public List<PlayerStatistics> getPlayerStats() {
		return playerStats;
	}
	
	public void setPlayerStats(List<PlayerStatistics> playerStats) {
		this.playerStats = playerStats;
	}
}
