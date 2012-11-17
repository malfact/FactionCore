package net.illusiononline.factioncore;

import net.illusiononline.factioncore.hashmaps.*;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

public class FactionManager {
	
	FactionCore plugin;
	PermissionHashMap permissions;
	PlayerConfirmationHashMap playerconfirm;
	
	public FactionManager(FactionCore plugin){
		this.plugin = plugin;
		playerconfirm = new PlayerConfirmationHashMap(plugin);
		permissions = new PermissionHashMap();
	}
	
	public void factionInfo(Player player, String name){
		if (name == null) name = "";
		String faction = "";
		if (name.equalsIgnoreCase(""))
			faction = FactionCore.getSqlManager().getFaction(player.getName());
		else
			faction = FactionCore.getSqlManager().getFaction(name);
		
		if (faction.equalsIgnoreCase("")){
			if (name.equalsIgnoreCase(""))
				player.sendMessage(ChatColor.RED+"You are not in a faction!");
			else
				player.sendMessage(ChatColor.RED+"Invalid faction!");
			return;
		}
		String owners = FactionCore.getSqlManager().getListedUnit(faction, "owner");
		String admins = FactionCore.getSqlManager().getListedUnit(faction, "admins");
		String members = FactionCore.getSqlManager().getListedUnit(faction, "member");
		String allies = FactionCore.getSqlManager().getListedUnit(faction, "ally");
		String enemies = FactionCore.getSqlManager().getListedUnit(faction, "enemy");
		String balance = FactionCore.getSqlManager().getFlag(faction, "money");
		String isopen = FactionCore.getSqlManager().getFlag(faction, "open");
		if (isopen == null) isopen = "false";
		
		player.sendMessage(ChatColor.GOLD+":|"+faction+"|:\n" +
				ChatColor.AQUA+"Owner(s):  "+ChatColor.DARK_GREEN+owners+"\n" +
				ChatColor.AQUA+"Admins:  "+ChatColor.DARK_GREEN+admins+"\n" +
				ChatColor.AQUA+"Balance:  "+ChatColor.DARK_GREEN+balance+"\n" +
				ChatColor.AQUA+"Allies:  "+ChatColor.BLUE+allies+"\n" +
				ChatColor.AQUA+"Enemies:  "+ChatColor.RED+enemies+"\n" +
				ChatColor.AQUA+"Open:  "+ChatColor.DARK_GREEN+isopen+"\n" +
				ChatColor.AQUA+"Members:  "+ChatColor.RED+members);
	}
	
	public boolean createFaction(Player player, String name){
		if (!FactionCore.getSqlManager().getFaction(player.getName()).equalsIgnoreCase("")){
			player.sendMessage(ChatColor.RED+"You must leave your faction before creating a new one!");
			return false;
		}
		String confirm = playerconfirm.getConfirmation(player.getName());
		if (confirm != null){
			if (confirm.equalsIgnoreCase("create "+name)) {
				if (plugin.getEconomyIsPresent()){
					int $e = plugin.getEconomySQLManager().getBalance(player.getName());
					plugin.getEconomySQLManager().setBalance(player.getName(), $e-500);
					player.sendMessage(ChatColor.AQUA+player.getName()+"'s Balance: "+($e-500));
				}
				boolean w = FactionCore.getSqlManager().newFaction(player.getName(), name);
				if (w) {
					playerconfirm.removeConfirmation(player.getName());
					player.sendMessage(ChatColor.AQUA+"Faction "+name+" created!");
					return true;
				} else {
					player.sendMessage(ChatColor.RED+"ERROR! Unable to create faction!");
				}
			}
		}
		if (plugin.getEconomyIsPresent()){
			int $e = plugin.getEconomySQLManager().getBalance(player.getName());
			if ($e < 500) {
				player.sendMessage(ChatColor.RED+"You need $500 to create a faction!");
				return false;
			}
		}
		if (FactionCore.getSqlManager().getFlag(name, "owner").equalsIgnoreCase("")){
			player.sendMessage(ChatColor.GOLD+"Say \"/faction create "+name+"\" again to create the faction!\n" +
					"It will cost $500!");
			playerconfirm.addConfirmation(player.getName(), "create "+name);
		}
		return false;
	} 
	public boolean disbandFaction(Player player,String name){
		if (name == null) name = "";
		String faction = name;
		if (player == null && name.equalsIgnoreCase("")) return false;
		if (player == null){
			boolean w = FactionCore.getSqlManager().deleteFaction(name);
			if (w)
				return true;
		} else {
			if (faction.equalsIgnoreCase("")) {
				faction = FactionCore.getSqlManager().getFaction(player.getName());
				if (faction.equalsIgnoreCase("")){
					return false;
				}
			}
			String confirm = playerconfirm.getConfirmation(player.getName());
			if (confirm != null){
				if (confirm.equalsIgnoreCase("disband "+faction)) {
					boolean w = FactionCore.getSqlManager().deleteFaction(faction);
					if (w) {
						playerconfirm.removeConfirmation(player.getName());
						player.sendMessage(ChatColor.RED+"Faction "+faction+" disbanded!");
						return true;
					}
				}
			}
			if (FactionCore.getSqlManager().getFlag(faction, "owner").equalsIgnoreCase(player.getName()) ||
					player.hasPermission(permissions.getPermission("disbandother"))){
				if (name.equalsIgnoreCase("")|| name == null)
					player.sendMessage(ChatColor.GOLD+"Say \"/faction disband\" again to disband the faction!");
				else
					player.sendMessage(ChatColor.GOLD+"Say \"/faction disband "+faction+"\" again to disband the faction!");
				playerconfirm.addConfirmation(player.getName(), "disband "+faction);
			} else {
				player.sendMessage(ChatColor.RED+"You must own the faction to delete it!");
			}
		}
		return false;
	}
	
	public boolean joinFaction(Player player, String name){
		if (!FactionCore.getSqlManager().getFaction(player.getName()).equalsIgnoreCase("")){
			player.sendMessage(ChatColor.RED+"You must leave your faction before joining a new one!");
			return false;
		}
		String confirm = playerconfirm.getConfirmation(player.getName());
		if (confirm.equalsIgnoreCase("join "+name)) {
			boolean w = FactionCore.getSqlManager().addListedUnit(name, "member", player.getName());
			if (w) {
				player.sendMessage(ChatColor.AQUA+"You have joined "+name+"!");
				Player players[] = plugin.getServer().getOnlinePlayers();
				for (int i = 0;i <= players.length-1;i++){
					if (FactionCore.getSqlManager().getFaction(players[i].getName()).equalsIgnoreCase(name) && players[i] != player){
						players[i].sendMessage(ChatColor.AQUA+player.getName()+" has joined your faction!");
					}
				}
				return true;
			}
			return false;
		}
		String isopen = FactionCore.getSqlManager().getFlag(name, "open");
		if (isopen.equalsIgnoreCase("true") || player.hasPermission(permissions.getPermission("joinoverride"))){
			player.sendMessage(ChatColor.GOLD+"Say \"/faction join "+name+"\" again to join the faction!");
			playerconfirm.addConfirmation(player.getName(), "join "+name);
		} else {
			player.sendMessage(ChatColor.RED+"Faction is closed!");
		}
		return false;
	}
	
	public boolean inviteToFaction(Player player, String name){
		Player target = plugin.getServer().getPlayer(name);
		if (target == null) return false;
		if (player.getName().equalsIgnoreCase(target.getName())) return false;
		
		String faction = FactionCore.getSqlManager().getFaction(player.getName());
		if (faction.equalsIgnoreCase("")){
			player.sendMessage(ChatColor.RED+"You are not in a faction!");
			return false;
		}
		
		boolean can_invite = false;
		if (FactionCore.getSqlManager().getFlag(faction, "open").equalsIgnoreCase("true"))
			can_invite = true;
		if (FactionCore.getSqlManager().getFlag(faction, "invite").equalsIgnoreCase("true"))
			can_invite = true;
		String list[] = (FactionCore.getSqlManager().getListedUnit(faction, "owner")+", "+FactionCore.getSqlManager().getListedUnit(faction, "admin")).split(",");
		for (int i=0;i <= list.length-1;i++){
			if (list[i].trim().equalsIgnoreCase(player.getName())){
				can_invite = true;
				break;
			}
		}
		
		if (can_invite) {
			playerconfirm.addConfirmation(target.getName(), "join "+faction);
			player.sendMessage(ChatColor.AQUA+"You have invited "+target.getName()+" to join your faction!");
			target.sendMessage(ChatColor.AQUA+"You have been invited to join "+faction+"!\n" +
								"Type \"/faction join "+faction+"\" to join the faction!");
			return true;
		} else {
			player.sendMessage(ChatColor.RED+"You don't have permission to invite players!");
		}
		return false;
	}
	
	public boolean kickFromFaction(Player player, String name){
		Player target = plugin.getServer().getPlayer(name);
		if (target != null) {
			if (player.getName().equalsIgnoreCase(target.getName())) 
				return false;
		}
		
		String faction = FactionCore.getSqlManager().getFaction(player.getName());
		if (faction.equalsIgnoreCase("")){
			player.sendMessage(ChatColor.RED+"You are not in a faction!");
			return false;
		}
		String faction2 = FactionCore.getSqlManager().getFaction(name);
		if (faction2.equalsIgnoreCase("")){
			player.sendMessage(ChatColor.RED+name+" is not in a faction!");
			return false;
		}
		if (!faction.equalsIgnoreCase(faction2)){
			player.sendMessage(ChatColor.RED+"You are not in the same faction!");
			return false;
		}
		
		boolean can_kick = false;
		String list[] = (FactionCore.getSqlManager().getListedUnit(faction, "owner")+", "+FactionCore.getSqlManager().getListedUnit(faction, "admin")).split(" ");
		for (int i=0;i <= list.length-1;i++){
			if (list[i].trim().equalsIgnoreCase(player.getName())){
				can_kick = true;
				break;
			}
		}
		
		if (can_kick) {
			FactionCore.getSqlManager().removeListedUnit(faction, "owner", name);
			FactionCore.getSqlManager().removeListedUnit(faction, "admin", name);
			FactionCore.getSqlManager().removeListedUnit(faction, "member", name);
			player.sendMessage(ChatColor.AQUA+"You have kicked "+target.getName()+" from your faction!");
			if (target != null)
				target.sendMessage(ChatColor.RED+"You have been kicked from "+faction+"!");
			return true;
		} else {
			player.sendMessage(ChatColor.RED+"You don't have permission to kick players!");
		}
		
		return false;
	}
	
	public boolean leaveFaction(Player player, String name){
		if (name == null) name = "";
		String faction = FactionCore.getSqlManager().getFaction(player.getName());
		if (faction.equalsIgnoreCase("")){
			player.sendMessage(ChatColor.RED+"You are not in a faction!");
			return false;
		}
		String confirm = playerconfirm.getConfirmation(player.getName());
		if (confirm.equalsIgnoreCase("leave "+name)) {
			boolean w = FactionCore.getSqlManager().removeListedUnit(name, "member", player.getName());
			if (w) {
				player.sendMessage(ChatColor.RED+"You have left "+name+"!");
				Player players[] = plugin.getServer().getOnlinePlayers();
				for (int i = 0;i <= players.length-1;i++){
					if (FactionCore.getSqlManager().getFaction(players[i].getName()).equalsIgnoreCase(name)){
						players[i].sendMessage(ChatColor.RED+player.getName()+"has left your faction!");
					}
				}
				return true;
			}
			return false;
		}
		String list[] = FactionCore.getSqlManager().getListedUnit(faction, "owner").split(",");
		for (int i=0;i <= list.length-1;i++){
			if (list[i].trim().equalsIgnoreCase(player.getName())){
				player.sendMessage(ChatColor.RED+"You can not leave while you are an Owner!");
				return false;
			}
		}
		if (name.equalsIgnoreCase("")) {
			player.sendMessage(ChatColor.GOLD+"Say \"/faction leave\" again to leave the faction!");
			playerconfirm.addConfirmation(player.getName(), "leave "+faction);
		} else
			player.sendMessage(ChatColor.GOLD+"Say \"/faction leave "+faction+"\" again to leave the faction!");
		playerconfirm.addConfirmation(player.getName(), "leave "+faction);
		
		return false;
	}
	
}