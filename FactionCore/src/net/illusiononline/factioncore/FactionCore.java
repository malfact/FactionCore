package net.illusiononline.factioncore;

import java.util.logging.Logger;
import listeners.EntityListener;
import org.bukkit.plugin.java.JavaPlugin;
import net.illusiononline.EmeraldEconomy.EmeraldEconomy;
import net.illusiononline.factioncore.backends.MySQLManager;

public class FactionCore extends JavaPlugin{
	
	private Logger log = Logger.getLogger("Minecraft");
	private static MySQLManager sqlmanager;
	private boolean economy_is_present = false;
	private static FactionManager factionmanager;
	private net.illusiononline.EmeraldEconomy.backends.MySQLManager economy_sqlmanager;
	
	@Override
	public void onEnable() { 
		
		/* Init Config */
		getConfig().options().copyDefaults(true);
		saveConfig();
		/* End Init Config */
		
		if (EmeraldEconomy.getSQLManager() != null) {
			economy_sqlmanager = EmeraldEconomy.getSQLManager();
			log.info("Economy plugin is present: Allowing advanced features!");
			economy_is_present = true;
		} else {
			log.info("Economy plugin is not present: Disallowing advanced features!");
		}
		
		sqlmanager = new MySQLManager(this);
		factionmanager = new FactionManager(this);
		this.getServer().getPluginManager().registerEvents(new EntityListener(this), this);
		getCommand("faction").setExecutor(new FactionCommandExecutor());
		
		if (sqlmanager.getMySQL() == null)
			getPluginLoader().disablePlugin(this);
	}
	 @Override
	public void onDisable() {  
		 if (sqlmanager.getMySQL() != null)
				sqlmanager.getMySQL().close();
	}

	public static MySQLManager getSqlManager(){return sqlmanager;}
	public static FactionManager getFactionManager(){return factionmanager;}
	 
	public boolean getEconomyIsPresent(){return economy_is_present;}
	public net.illusiononline.EmeraldEconomy.backends.MySQLManager getEconomySQLManager(){return economy_sqlmanager;}
}

