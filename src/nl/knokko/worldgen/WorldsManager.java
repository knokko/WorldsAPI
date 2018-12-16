package nl.knokko.worldgen;

import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.World.Environment;
import org.bukkit.WorldCreator;
import org.bukkit.generator.ChunkGenerator;

import nl.knokko.worldgen.eventhandler.WorldEventHandler;

public class WorldsManager {
	
	/**
	 * Registers a world if it doesn't yet exist. It also sets the gamerules and the event handler for the
	 * world. This method should be called once for every world during the onEnable method.
	 * @param name The name of the world to register
	 * @param handler The world event handler for the world
	 * @param generator The generator that should be used to generate the world
	 * @param environment The dimension/environment of the world
	 * @param gameRules A Map containing preset game rule values for the world. The keys are the gamerules
	 * and the values are the values for the game rules. It will be ignored if it is null.
	 */
	public void registerWorld(String name, WorldEventHandler handler, ChunkGenerator generator,
			Environment environment, Map<String,String> gameRules) {
		WorldCreator creator = new WorldCreator(name);
		creator.environment(environment);
		// The chunk generator should manage all structures
		creator.generateStructures(false);
		creator.generator(generator);
		// This will create the world if it didn't exist already
		World world = creator.createWorld();
		if (gameRules != null) {
			Set<Entry<String,String>> entries = gameRules.entrySet();
			for (Entry<String,String> entry : entries) {
				world.setGameRuleValue(entry.getKey(), entry.getValue());
			}
		}
		Bukkit.getPluginManager().registerEvents(handler, WorldsPlugin.getInstance());
	}
}