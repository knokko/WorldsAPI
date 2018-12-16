package nl.knokko.worldgen;

import nl.knokko.worldgen.command.CommandWorlds;
import nl.knokko.worldgen.entity.task.EntityTaskManager;

import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;

public class WorldsPlugin extends JavaPlugin {
	
	private static WorldsPlugin instance;
	
	public static WorldsPlugin getInstance(){
		return instance;
	}
	
	private EntityTaskManager entityTaskManager;
	private WorldsManager worldsManager;
	
	@Override
	public void onEnable(){
		super.onEnable();
		instance = this;
		entityTaskManager = new EntityTaskManager();
		worldsManager = new WorldsManager();
		getCommand("worlds").setExecutor(new CommandWorlds());
		entityTaskManager.load();
		Bukkit.getPluginManager().registerEvents(entityTaskManager, this);
		Bukkit.getScheduler().scheduleSyncRepeatingTask(this, entityTaskManager, 0, 1);
	}
	
	@Override
	public void onDisable(){
		entityTaskManager.save();
		instance = null;
		super.onDisable();
	}
	
	public EntityTaskManager getTaskManager(){
		return entityTaskManager;
	}
	
	public WorldsManager getWorldsManager() {
		return worldsManager;
	}
}