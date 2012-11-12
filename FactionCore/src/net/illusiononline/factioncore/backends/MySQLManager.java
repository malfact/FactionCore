package net.illusiononline.factioncore.backends;

import java.sql.ResultSet;
import java.sql.SQLException;

import org.bukkit.ChatColor;

import net.illusiononline.factioncore.FactionCore;

import lib.PatPeter.SQLibrary.MySQL;

public class MySQLManager {

	String hostname	= "localhost";
	String portnmbr	= "3306";
	String database	= "MineLink";
	String username = "minelink";
	String password = "1orangekiwimja";
	
	FactionCore plugin;
	private MySQL sql;
	boolean economy_is_present = false;
	
	public MySQL getMySQL(){return sql;}
	
	public MySQLManager(FactionCore plugin){
		this.plugin = plugin;
		economy_is_present = plugin.getEconomyIsPresent();
		sql = new MySQL(plugin.getLogger(),"[FactionCore]",hostname,portnmbr,database,username,password);
		this.Setup();
	}
	
	public Boolean newFaction(String owner_name, String faction_name){
		try {
			sql.open();
			if (sql.checkTable("Faction_Data")){
				//Check for existing Faction
				try {
					ResultSet result = sql.query("SELECT * FROM Faction_Data WHERE name='"+faction_name+"';");
					if (result.next()) {
						result.close();
						sql.close();
						return false;
					}
				} catch (SQLException ex) {
					plugin.getLogger().severe("[FactionCore] "+ex);
					sql.close();
					return false;
				}
				sql.query("INSERT INTO Faction_Data (name,flag,data) " +
						  "VALUES ('"+faction_name+"',null,'*')");
				sql.query("INSERT INTO Faction_Data (name,flag,data) " +
						  "VALUES ('"+faction_name+"','owner','"+owner_name+"')");
				sql.query("INSERT INTO Faction_Data (name,flag,data) " +
						  "VALUES ('"+faction_name+"','member','"+owner_name+"')");
				sql.query("INSERT INTO Faction_Data (name,flag,data) " +
						  "VALUES ('"+faction_name+"','open','false')");
				sql.query("INSERT INTO Faction_Data (name,flag,data) " +
						  "VALUES ('"+faction_name+"','money','0')");
				sql.close();
				return true;
			}
			sql.close();
		} catch (SQLException e) {
			plugin.getLogger().info(e.getMessage());
		}
		return false;
	}
	
	public String[][] getFactionList(int number){
		String list[][] = new String[number][2];
		try {
			sql.open();
			if (sql.checkTable("SurvivalCore_Data")){
				try {
					ResultSet result = sql.query("SELECT * FROM Faction_Data WHERE data='*';");
					for (int i=0;i<number;i++) {
						if (result.next())
							list[i][0] = result.getString("name");
						else 
							list[i][0] = ".";
					}
					result.close();
					
					for (int i=0;i<number;i++){
						ResultSet tluser = sql.query("SELECT * FROM Faction_Data WHERE name='"+list[i][0]+"' AND flag='open';");
						if (tluser.next())
							if (tluser.getString("data").equalsIgnoreCase("true"))
								list[i][1] = ChatColor.DARK_GREEN+"Open";
							else
								list[i][1] = ChatColor.RED+"Closed";
						else
							list[i][1] = ChatColor.RED+"Closed";
						tluser.close();
					}
				} catch (SQLException ex) {
					plugin.getLogger().severe("[FactionCore] "+ex);
				}
			}
		} catch (SQLException e) {
			plugin.getLogger().info(e.getMessage());
		}
		sql.close();
		return list;
	}
	
	public Boolean deleteFaction(String name){
		try {
			sql.open();
			if (sql.checkTable("Faction_Data")){
				//Check for existing Unit
				try {
					sql.query("DELETE FROM Faction_Data WHERE name='"+name+"';");
					sql.query("DELETE FROM Faction_ChunkData WHERE flag='owner.faction' AND data='"+name+"';");
					sql.close();
					return true;
				} catch (SQLException ex) {
					plugin.getLogger().severe("[FactionCore] "+ex);
					sql.close();
					return false;
				}
			}
			sql.close();
		} catch (SQLException e) {
			plugin.getLogger().info(e.getMessage());
		}
		return false;
	}
	
	public String getFlag(String name,String flag){
		String option = "";
		try {
			sql.open();
			if (sql.checkTable("SurvivalCore_Data")){
				try {
					ResultSet result = sql.query("SELECT * FROM Faction_Data WHERE name='"+name+"' AND flag='"+flag+"';");
					if (result.next()) {
							option = result.getString("data");
							result.close();
					}
					result.close();
				} catch (SQLException ex) {
					plugin.getLogger().severe("[FactionCore] "+ex);
				}
			}
		} catch (SQLException e) {
			plugin.getLogger().info(e.getMessage());
		}
		sql.close();
		return option;
	}
	
	public Boolean setFlag(String name,String flag,String option){
		if (!flag.equalsIgnoreCase("owner") && !flag.equalsIgnoreCase("member") &&
				!flag.equalsIgnoreCase("ally") && !flag.equalsIgnoreCase("enemy")){
			try {
				sql.open();
				if (sql.checkTable("Faction_Data")){
					try {
						ResultSet result = sql.query("SELECT * FROM Faction_Data WHERE name='"+name+"' AND flag='"+flag+"'");
						if (result.next()) {
								sql.query("UPDATE Faction_Data " +
										  "SET data="+option+" " +
										  "WHERE name='"+name+"' " +
										  "AND flag='"+flag+"'");
								result.close();
								sql.close();
								return true;
						}
						result.close();
						sql.query("INSERT INTO Faction_Data (name,flag,data) " +
								  "VALUES ('"+name+"','"+flag+"','"+option+"')");
						sql.close();
						return true;
					} catch (SQLException ex) {
						plugin.getLogger().severe("[FactionCore] "+ex);
					}
				}
			} catch (SQLException e) {
				plugin.getLogger().info(e.getMessage());
			}
			sql.close();
		}
		return false;
	}
	
	public Boolean clearFlag(String name,String flag){
		if (!flag.equalsIgnoreCase("owner") && !flag.equalsIgnoreCase("member") &&
				!flag.equalsIgnoreCase("ally") && !flag.equalsIgnoreCase("enemy")){
			try {
				sql.open();
				if (sql.checkTable("Faction_Data")){
					try {
						sql.query("DELETE FROM Faction_Data WHERE name='"+name+"' AND flag='"+flag+"'");
						return true;
					} catch (SQLException ex) {
						plugin.getLogger().severe("[FactionCore] "+ex);
					}
				}
			} catch (SQLException e) {
				plugin.getLogger().info(e.getMessage());
			}
			sql.close();
		}
		return false;
	}
	
	public Boolean addListedUnit(String name,String list, String unit){
		if (list.equalsIgnoreCase("owner") || list.equalsIgnoreCase("member") ||
				list.equalsIgnoreCase("ally") || list.equalsIgnoreCase("enemy")){
			try {
				sql.open();
				if (sql.checkTable("Faction_Data")){
					//Check for existing Faction
					try {
						ResultSet result = sql.query("SELECT * FROM Faction_Data WHERE name='"+name+"';");
						if (!result.next()) {
							result.close();
							return false;
						}
					} catch (SQLException ex) {
						plugin.getLogger().severe("[FactionCore] "+ex);
						sql.close();
						return false;
					}
					sql.query("INSERT INTO Faction_Data (name,flag,data) " +
							  "VALUES ('"+name+"','"+list+"','"+unit+"')");
					sql.close();
					return true;
				}
				sql.close();
			} catch (SQLException e) {
				plugin.getLogger().info(e.getMessage());
			}
		}
		return false;
	}
	
	public String getListedUnit(String name,String flag){
		String option = "";
		try {
			sql.open();
			if (sql.checkTable("SurvivalCore_Data")){
				try {
					ResultSet result = sql.query("SELECT * FROM Faction_Data WHERE name='"+name+"' AND flag='"+flag+"';");
					while (result.next()) {
							option = option+result.getString("data")+" ";
					}
					result.close();
				} catch (SQLException ex) {
					plugin.getLogger().severe("[FactionCore] "+ex);
				}
			}
		} catch (SQLException e) {
			plugin.getLogger().info(e.getMessage());
		}
		sql.close();
		return option;
	}
	
	public Boolean removeListedUnit(String name,String list, String unit){
		if (list.equalsIgnoreCase("owner") || list.equalsIgnoreCase("member") ||
				list.equalsIgnoreCase("ally") || list.equalsIgnoreCase("enemy")){
			try {
				sql.open();
				if (sql.checkTable("Faction_Data")){
					//Check for existing Faction
					try {
						sql.query("DELETE FROM Faction_Data WHERE name='"+name+"' AND flag='"+list+"' AND data='"+unit+"'");
						return true;
					} catch (SQLException ex) {
						plugin.getLogger().severe("[FactionCore] "+ex);
						sql.close();
						return false;
					}
				}
				sql.close();
			} catch (SQLException e) {
				plugin.getLogger().info(e.getMessage());
			}
		}
		return false;
	}
	
	public String getFaction(String name){
		String faction = "";
		try {
			sql.open();
			if (sql.checkTable("Faction_Data")){
				try {
					ResultSet result = sql.query("SELECT * FROM Faction_Data WHERE flag='member' AND data='"+name+"';");
					if (result.next()) {
							faction = result.getString("name");
							result.close();
							sql.close();
							return faction;
					}
					result.close();
				} catch (SQLException ex) {
					plugin.getLogger().severe("[FactionCore] "+ex);
				}
			}
		} catch (SQLException e) {
			plugin.getLogger().info(e.getMessage());
		}
		sql.close();
		return faction;
	}
	
	public void Setup() {
		try {
			sql.open();
			if (!sql.checkTable("Faction_Data")){
				plugin.getLogger().info("Creating Table: Faction_Data");
				sql.createTable("CREATE TABLE Faction_Data" +
						  "(" +
						  "name VARCHAR(255)," +
						  "flag VARCHAR(255)," +
						  "data VARCHAR(255)" +
						  ");");
			}
			if (!sql.checkTable("Faction_ChunkData")){
				plugin.getLogger().info("Creating Table: Faction_ChunkData");
				sql.createTable("CREATE TABLE Faction_ChunkData" +
						  "(" +
						  "x INT(255)," +
						  "y INT(255)," +
						  "flag VARCHAR(255)," +
						  "data VARCHAR(255)" +
						  ");");
			}
			sql.close();
		} catch (SQLException e) {
			sql.close();
            plugin.getLogger().info(e.getMessage());
            plugin.getPluginLoader().disablePlugin(plugin);
		}
	}
}
