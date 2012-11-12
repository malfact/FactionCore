package net.illusiononline.factioncore.hashmaps;

import java.util.HashMap;
import net.illusiononline.factioncore.FactionCore;


public class PlayerConfirmationHashMap {
	FactionCore plugin;
	private static HashMap<String, String> confirmation_msg = new HashMap<String, String>();
	
	public PlayerConfirmationHashMap(FactionCore plugin) {
		this.plugin = plugin;
	}
	
	public void addConfirmation(final String name, String msg){
		confirmation_msg.put(name, msg);
		plugin.getServer().getScheduler().scheduleAsyncDelayedTask(plugin, new Runnable() {
			   public void run() {
			       if (confirmation_msg.containsKey(name)){
			    	   confirmation_msg.remove(name);
			       }
			   }
			}, 600L);
	}
	
	public String getConfirmation(String name){
		String confirm = "";
		if (confirmation_msg.containsKey(name))
			confirm = confirmation_msg.get(name);
		return confirm;
	}
	
	public void removeConfirmation(String name){
		if (confirmation_msg.containsKey(name)){
			confirmation_msg.remove(name);
		}
	}
}
