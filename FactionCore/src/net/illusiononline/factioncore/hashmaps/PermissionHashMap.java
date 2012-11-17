package net.illusiononline.factioncore.hashmaps;

import java.util.HashMap;

public class PermissionHashMap {

	private static HashMap<String, String> permissions = new HashMap<String, String>();
	
	public PermissionHashMap(){
		permissions.put("create", 			"factioncore.faction.create");
		permissions.put("disband", 			"factioncore.faction.disband");
		permissions.put("disbandother", 	"factioncore.faction.disband.other");
		permissions.put("join", 			"factioncore.faction.join");
		permissions.put("leave", 			"factioncore.faction.leave");
		permissions.put("joinoverride", 	"factioncore.faction.join.override");
		permissions.put("invite", 			"factioncore.faction.invite");
		permissions.put("ally", 			"factioncore.faction.ally");
		permissions.put("enemy", 			"factioncore.faction.enemy");
		permissions.put("kick", 			"factioncore.faction.kick");
		permissions.put("kickoverride",		"factioncore.faction.kick.override");
		permissions.put("chat", 			"factioncore.faction.chat");
		permissions.put("chatother", 		"factioncore.faction.chat.other");
		permissions.put("sethome", 			"factioncore.faction.home.set");
		permissions.put("sethomeother", 	"factioncore.faction.home.set.other");
		permissions.put("home", 			"factioncore.faction.home");
		permissions.put("homeother", 		"factioncore.faction.home.other");
		permissions.put("clearhome", 		"factioncore.faction.home.clear");
		permissions.put("clearhomeother", 	"factioncore.faction.home.clear.other");
		permissions.put("moneycheck", 		"factioncore.faction.money.check");
		permissions.put("moneycheckother", 	"factioncore.faction.money.check.other");
		permissions.put("sendmoney", 		"factioncore.faction.money.send");
		permissions.put("sendmoneyother", 	"factioncore.faction.money.send.other");
		permissions.put("takemoney", 		"factioncore.faction.money.take");
		permissions.put("takemoneyother", 	"factioncore.faction.money.take.other");
		permissions.put("flag", 			"factioncore.faction.flag");
		permissions.put("flagother", 		"factioncore.faction.flag.other");
	}
	
	public String getPermission(String key){
		String perm = "";
		if (permissions.containsKey(key))
			perm = permissions.get(key);
		return perm;
	}
}
