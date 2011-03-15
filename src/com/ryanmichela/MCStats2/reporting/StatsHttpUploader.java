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

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.util.logging.Logger;

import com.ryanmichela.MCStats2.model.StatsConfig;

public class StatsHttpUploader {
	
	public static void UploadStats(String json, StatsConfig config, Logger log) {
		try {
	        URL u = new URL(config.getHttpPostUrl());
	        URLConnection c = u.openConnection();
	
	        c.setDoOutput(true);
	        c.setConnectTimeout(config.getHttpPostConnectTimeout());
	        
	        if (c instanceof HttpURLConnection) {
	        	((HttpURLConnection)c).setRequestMethod("POST");
	        }
	
	        OutputStreamWriter out = new OutputStreamWriter(
	                c.getOutputStream());
	
	        out.write(json);
	        out.close();
	
	        BufferedReader in = new BufferedReader(new InputStreamReader(c.getInputStream()));
	
	        String s = null;
	        StringBuilder sb = new StringBuilder();
	        while ((s = in.readLine()) != null) {
	        	sb.append(s);
	        }
	        in.close();
	        
	        if(!sb.toString().contains("200")) {
	        	log.severe("MCStats attempted to post statistics to " + config.getHttpPostUrl() + " but did not recieve a 200-OK response."); 
	        }
		} catch(MalformedURLException e) {
			log.severe("The httpPostUrl is malformed in MCStats.config");
			log.severe(e.toString());
		} catch(IOException e) {
			log.severe("Error reading or writing to " + config.getHttpPostUrl());
			log.severe(e.toString());
		}
	}
}
