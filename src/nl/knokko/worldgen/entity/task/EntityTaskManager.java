package nl.knokko.worldgen.entity.task;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;

import org.bukkit.Bukkit;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDeathEvent;

import nl.knokko.worldgen.WorldsPlugin;

public class EntityTaskManager implements Runnable, Listener {
	
	private Collection<CustomEntityTask> tasks;

	public EntityTaskManager() {
		tasks = new ArrayList<CustomEntityTask>();
	}
	
	public void load(){
		try {
			FileInputStream input = new FileInputStream(WorldsPlugin.getInstance().getDataFolder() + "/tasks.dat");
			DataInputStream data = new DataInputStream(input);
			int size = data.readInt();
			tasks = new ArrayList<CustomEntityTask>(size);
			for(int i = 0; i < size; i++){
				try {
					tasks.add(CustomEntityTask.load(data));
				} catch(IllegalArgumentException ex){
					Bukkit.getLogger().warning("It looks like the custom task data has been corrupted: " + ex.getMessage());
				}
			}
			input.close();
		} catch(IOException ioex){
			Bukkit.getLogger().info("Could not load custom entity tasks: " + ioex.getMessage());
			Bukkit.getLogger().info("There is probably nothing wrong.");
		}
	}
	
	public void save(){
		if(tasks.isEmpty()) return;
		try {
			File folder = WorldsPlugin.getInstance().getDataFolder();
			folder.mkdirs();
			FileOutputStream output = new FileOutputStream(folder + "/tasks.dat");
			DataOutputStream data = new DataOutputStream(output);
			data.writeInt(tasks.size());
			for(CustomEntityTask task : tasks)
				task.save(data);
			output.close();
		} catch(IOException ioex){
			Bukkit.getLogger().warning("Failed to save custom entity tasks: " + ioex.getMessage());
		}
	}
	
	@EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
	public void onEntityDeath(EntityDeathEvent event){
		for(CustomEntityTask task : tasks)
			task.onEntityDeath(event.getEntity());
	}
	
	public void run(){
		Iterator<CustomEntityTask> iterator = tasks.iterator();
		while(iterator.hasNext()){
			CustomEntityTask task = iterator.next();
			if(task.isCompleted())
				iterator.remove();
			else
				task.update();
		}
	}
	
	public void registerTask(CustomEntityTask task){
		tasks.add(task);
	}
}
