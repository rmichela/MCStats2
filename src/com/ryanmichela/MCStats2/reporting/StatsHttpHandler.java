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

import java.io.IOException;
import java.io.OutputStream;

import com.ryanmichela.MCStats2.model.StatsConfig;
import com.ryanmichela.MCStats2.model.StatsModel;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;

public class StatsHttpHandler implements HttpHandler {

	private StatsConfig config;
	private StatsModel stats;
	
	public StatsHttpHandler(StatsModel stats, StatsConfig config)
	{
		this.config = config;
		this.stats = stats;
	}
	
	@Override
	public void handle(HttpExchange t) throws IOException {
		String request = t.getRequestURI().getPath();
        if(request.endsWith(config.getStatsBaseResource() + ".xml")) {
        	handleXML(t);
        } else if(request.endsWith(config.getStatsBaseResource() + ".json")) {
        	handleJson(t);
        } else if(request.endsWith(config.getStatsBaseResource() + ".js")) {
        	handleJavaScript(t);
        } else if(request.endsWith(config.getStatsBaseResource() + ".html")) {
        	handleHtml(t);
        } else {
        	handle404(t);
        }
	}
	
	private void handleXML(HttpExchange t) throws IOException {
		String response = StatsSerializer.statsAsXml(stats.getRawStats());
		
        t.sendResponseHeaders(200, response.length());
        OutputStream os = t.getResponseBody();
        os.write(response.getBytes());
        os.close();		
	}
	
	private void handleJson(HttpExchange t) throws IOException {
		String response = StatsSerializer.statsAsJson(stats.getRawStats());
		
		t.sendResponseHeaders(200, response.length());
		OutputStream os = t.getResponseBody();
        os.write(response.getBytes());
        os.close();	
	}
	
	private void handleJavaScript(HttpExchange t) throws IOException {
		String response = StatsSerializer.statsAsJavascript(stats.getRawStats());
		
		t.sendResponseHeaders(200, response.length());
		OutputStream os = t.getResponseBody();
        os.write(response.getBytes());
        os.close();	
	}
	
	private void handleHtml(HttpExchange t) throws IOException {
		String response = StatsSerializer.statsAsHtml(config);
		
		t.sendResponseHeaders(200, response.length());
		OutputStream os = t.getResponseBody();
        os.write(response.getBytes());
        os.close();	
	}
	
	private void handle404(HttpExchange t) throws IOException {
		String response = t.getRequestURI().getPath() + " NOT FOUND";
        t.sendResponseHeaders(404, response.length());
        OutputStream os = t.getResponseBody();
        os.write(response.getBytes());
        os.close();		
	}
}
