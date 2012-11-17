package net.illusiononline.factioncore;

import net.illusiononline.factioncore.hashmaps.PermissionHashMap;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class FactionCommandExecutor implements CommandExecutor{

	PermissionHashMap permissions;
	
	public FactionCommandExecutor(){
		permissions = new PermissionHashMap();
	}
	
	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String list, String args[]) {
		Player player = null;
		if (sender instanceof Player) player = (Player) sender;
		
		if (cmd.getName().equalsIgnoreCase("faction")){
			switch (args.length){
				case 0:
					if (player == null)
						sender.sendMessage("This command requires a player index!");
					else {
						FactionCore.getFactionManager().factionInfo(player, "");
						return true;
					}
					break;
				case 1:
					if (args[0].equalsIgnoreCase("help")){
						sender.sendMessage(ChatColor.AQUA+"|FactionCore 'faction' Commands|\n"+
											ChatColor.GREEN+"/faction help\n" +
											ChatColor.GREEN+"/faction list [#]\n" +
											ChatColor.GREEN+"/faction create <name>\n" +
											ChatColor.GREEN+"/faction disband [name]\n" +
											ChatColor.GREEN+"/faction join <name>\n" +
											ChatColor.GREEN+"/faction leave [name]\n" +
											ChatColor.GREEN+"/faction invite <name>\n" +
											"/faction ally <name>\n" +
											"/faction unally <name>\n" +
											"/faction enemy <name>\n" +
											"/faction unenemy <name>\n" +
											ChatColor.GREEN+"/faction kick <name>\n" +
											"/faction chat <join/leave> [name]\n" +
											"/faction sethome [name]\n" +
											"/faction home [name]\n" +
											"/faction clearhome [name]\n" +
											"/faction money [faction]\n" +
											"/faction sendmoney [faction] <#>\n" +
											"/faction takemoney [faction] <#>\n" +
											"/faction flag [faction] <flag> [option]");
						return true;
					} else if (args[0].equalsIgnoreCase("list")){
						String data[][] = FactionCore.getSqlManager().getFactionList(50);
						sender.sendMessage(ChatColor.AQUA+"|50 Factions|");
						int i = 0;
						while (i<50){
							if(data[i][0].equalsIgnoreCase(".")) break;
							sender.sendMessage(ChatColor.AQUA+data[i][0]+" "+data[i][1]);
							i++;
						}
						return true;
					} else if (args[0].equalsIgnoreCase("disband")){
						if (!sender.hasPermission(permissions.getPermission("disband")))
							return false;
						if (player == null)
							sender.sendMessage("This command requires a player index!");
						else {
							FactionCore.getFactionManager().disbandFaction(player, "");
							return true;
						}
					} else if (args[0].equalsIgnoreCase("leave")){
						if (!sender.hasPermission(permissions.getPermission("leave")))
							return false;
						if (player == null)
							sender.sendMessage("This command requires a player index!");
						else {
							FactionCore.getFactionManager().leaveFaction(player, "");
							return true;
						}
					} else if (args[0].equalsIgnoreCase("sethome")){
						
					} else if (args[0].equalsIgnoreCase("home")){
						
					} else if (args[0].equalsIgnoreCase("clearhome")){
						
					} else if (args[0].equalsIgnoreCase("money")){
						
					} else {
						if (player == null)
							sender.sendMessage("This command requires a player index!");
						else {
							FactionCore.getFactionManager().factionInfo(player, args[0]);
							return true;
						}
					}
					break;
				case 2:
					if (args[0].equalsIgnoreCase("create")){
						if (!sender.hasPermission(permissions.getPermission("create")))
							return false;
						if (player == null)
							sender.sendMessage("This command requires a player index!");
						else {
							FactionCore.getFactionManager().createFaction(player, args[1]);
							return true;
							}
					} else if (args[0].equalsIgnoreCase("disband")){
						if (!sender.hasPermission(permissions.getPermission("disband")))
							return false;
						if (player == null) {
							FactionCore.getFactionManager().disbandFaction(null, args[1]);
							return true;
						} else {
							FactionCore.getFactionManager().disbandFaction(player, args[1]);
							return true;
						}
					} else if (args[0].equalsIgnoreCase("join")){
						if (!sender.hasPermission(permissions.getPermission("join")))
							return false;
						if (player == null)
							sender.sendMessage("This command requires a player index!");
						else {
							FactionCore.getFactionManager().joinFaction(player, args[1]);
							return true;
						}
					} else if (args[0].equalsIgnoreCase("leave")){
						if (!sender.hasPermission(permissions.getPermission("leave")))
							return false;
						if (player == null)
							sender.sendMessage("This command requires a player index!");
						else {
							FactionCore.getFactionManager().leaveFaction(player, args[1]);
							return true;
						}
					} else if (args[0].equalsIgnoreCase("invite")){
						if (!sender.hasPermission(permissions.getPermission("invite")))
							return false;
						if (player == null)
							sender.sendMessage("This command requires a player index!");
						else {
							FactionCore.getFactionManager().inviteToFaction(player, args[1]);
							return true;
						}
					} else if (args[0].equalsIgnoreCase("ally")){
						
					} else if (args[0].equalsIgnoreCase("unally")){
						
					} else if (args[0].equalsIgnoreCase("enemy")){
						
					} else if (args[0].equalsIgnoreCase("unenemy")){
						
					} else if (args[0].equalsIgnoreCase("kick")){
						if (!sender.hasPermission(permissions.getPermission("kick")))
							return false;
						if (player == null)
							sender.sendMessage("This command requires a player index!");
						else {
							FactionCore.getFactionManager().kickFromFaction(player, args[1]);
							return true;
						}
					} else if (args[0].equalsIgnoreCase("chat")){
						
					} else if (args[0].equalsIgnoreCase("sethome")){
						
					} else if (args[0].equalsIgnoreCase("home")){
						
					} else if (args[0].equalsIgnoreCase("clearhome")){
						
					} else if (args[0].equalsIgnoreCase("money")){
						
					} else if (args[0].equalsIgnoreCase("sendmoney")){
						
					} else if (args[0].equalsIgnoreCase("takemoney")){
						
					} else if (args[0].equalsIgnoreCase("flag")){
						
					} else if (args[0].equalsIgnoreCase("list")){
						Integer numb = toInt(args[1]);
						if (numb == null){
							sender.sendMessage(ChatColor.RED+"Use a number value!");
							return true;
						}
						String data[][] = FactionCore.getSqlManager().getFactionList(numb);
						sender.sendMessage(ChatColor.AQUA+"|"+numb+" Factions|");
						int i = 0;
						while (i<numb){
							if(data[i][0].equalsIgnoreCase(".")) break;
							sender.sendMessage(ChatColor.AQUA+data[i][0]+" "+data[i][1]);
							i++;
						}
						return true;
					}
					break;
				case 3:
					if (args[0].equalsIgnoreCase("chat")){
						
					} else if (args[0].equalsIgnoreCase("sendmoney")){
						
					} else if (args[0].equalsIgnoreCase("takemoney")){
						
					} else if (args[0].equalsIgnoreCase("flag")){
						
					} 
					break;
				case 4:
					if (args[0].equalsIgnoreCase("flag")){
						
					} 
				default:
					break;
			}
		}
		
		return false;
	}
	
	private Integer toInt(String string) {
		Integer a;
		try {
			a = Integer.parseInt(string);
			return a;
		} catch (NumberFormatException e){}
		return null;
	}
}
