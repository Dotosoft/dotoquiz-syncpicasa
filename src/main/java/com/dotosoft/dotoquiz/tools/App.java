/*
	Copyright 2015 Denis Prasetio
	
	Licensed under the Apache License, Version 2.0 (the "License");
	you may not use this file except in compliance with the License.
	You may obtain a copy of the License at
	
	http://www.apache.org/licenses/LICENSE-2.0
	
	Unless required by applicable law or agreed to in writing, software
	distributed under the License is distributed on an "AS IS" BASIS,
	WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
	See the License for the specific language governing permissions and
	limitations under the License.
*/

package com.dotosoft.dotoquiz.tools;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;

import com.dotosoft.dot4command.chain.Command;
import com.dotosoft.dotoquiz.tools.config.DotoQuizContext;
import com.dotosoft.dotoquiz.tools.util.CatalogLoader;

public class App {
	
	public App(String args[]) 
	{
		try {
			DotoQuizContext ctx = new DotoQuizContext();
			if( ctx.getSettings().loadSettings(args) ) {
				Command command = CatalogLoader.getInstance().getCatalog().getCommand(ctx.getSettings().getApplicationType());
				if(command != null) {
					command.execute(ctx);
					ctx = null;
					System.exit(0);
				}
			} 
		} catch (Exception ex) {
			ex.printStackTrace();
		}
		showError();
	}
	
	public void showError() 
	{
		System.err.println("Error: Could not run SyncQuizParser.");
		System.err.println("Run: java -jar SyncQuizParser.jar "+ Arrays.toString(listCommands()) + " [file config]");
	}
	
	public Object[] listCommands() 
	{
		try {
			Iterator<String> keyCommand = CatalogLoader.getInstance().getCatalog().getNames();
			List<String> commands = new ArrayList<String>();
			while(keyCommand.hasNext()) {
				String command = keyCommand.next();
				if(command.indexOf("do") == 0) continue;
				commands.add(command);
			}
			return commands.toArray();
		} catch (Exception ex) {
			ex.printStackTrace();
			showError();
		}
		
		return new String[] {};
	}
	
	public static void main(String args[]) 
	{
		new App(args);
	}
}
