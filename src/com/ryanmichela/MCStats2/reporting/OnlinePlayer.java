package com.ryanmichela.MCStats2.reporting;
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

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement(name="player")
@XmlAccessorType(XmlAccessType.PROPERTY)
public class OnlinePlayer {
	public String playerName;
	public String[] groups;
	
	@XmlElement
	public String getPlayerName() {
		return playerName;
	}
	
	@XmlElement
	public String[] getGroups() {
		return groups;
	}
	
	public OnlinePlayer() {
		playerName = "";
		groups = new String[0];
	}
}
