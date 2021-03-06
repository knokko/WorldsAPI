package nl.knokko.worldgen;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Random;

import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.generator.ChunkGenerator.ChunkData;
import org.bukkit.material.MaterialData;

import nl.knokko.worldgen.blockmod.BlockModFactory;
import nl.knokko.worldgen.blockmod.BlockModification;
import nl.knokko.worldgen.heightmod.HeightModFactory;
import nl.knokko.worldgen.heightmod.HeightModifier;

public class WorldGenHelper {
	
	public static void setHeights(ChunkData chunk, Material type, int chunkX, int chunkZ, int baseHeight, int minHeight, int maxHeight, Collection<HeightModifier> hms){
		for(int x = 0; x < 16; x++){
			for(int z = 0; z < 16; z++){
				int height = getHeight(baseHeight, minHeight, maxHeight, chunkX * 16 + x, chunkZ * 16 + z, hms);
				if(height > 165)
					height = (int) ((255 * height) / (float)(height + 90));
				chunk.setRegion(x, 0, z, x + 1, height, z + 1, type);
			}
		}//TODO test this new method
		//height <= 128 --> height
		//height > 255 --> height < 255
	}
	
	public static int[] getHeights(int chunkX, int chunkZ, int baseHeight, int minHeight, int maxHeight, Collection<HeightModifier> hms){
		int[] heights = new int[256];
		int d = (int) (0.7 * maxHeight);
		int i = 0;
		for(int x = 0; x < 16; x++){
			for(int z = 0; z < 16; z++){
				int height = getHeight(baseHeight, minHeight, maxHeight, chunkX * 16 + x, chunkZ * 16 + z, hms);
				if(height > d)
					height = d + ((maxHeight - d) * (height - d)) / (maxHeight + height - 2 * d);
				heights[i++] = height;
			}
		}
		return heights;
	}
	
	public static void setBlocks(ChunkData chunk, Material replace, int chunkX, int chunkZ, Collection<BlockModification> bms){
		for(BlockModification bm : bms){
			MaterialData md = bm.getType();
			Collection<int[]> positions = bm.getPositions();
			for(int[] position : positions){
				int x = position[0] - chunkX * 16;
				int z = position[2] - chunkZ * 16;
				if(x >= 0 && x < 16 && z >= 0 && z < 16 && chunk.getType(x, position[1], z) == replace)
					chunk.setBlock(x, position[1], z, md);
			}
		}
	}
	
	public static Random createRandom(int regionX, int regionZ, long seed){
		return new Random(seed * 4 * regionX - seed * 3 * regionZ);
	}
	
	public static int getHeight(int baseHeight, int minHeight, int maxHeight, int x, int z, Collection<HeightModifier> mods){
		int height = baseHeight;
		for(HeightModifier mod : mods){
			int extra = mod.getExtraHeight(x, z);
			if((extra > 0 && mod.canIncrease()) || (extra < 0 && mod.canDecrease()))
				height += extra;
		}
		if(height < minHeight)
			height = minHeight;
		if(height > maxHeight)
			height = maxHeight;
		return height;
	}
	
	public static void addRegionModifiers(Collection<HeightModifier> mods, HeightModFactory factory, int minRegionX, int minRegionZ, int maxRegionX, int maxRegionZ, long seed){
		for(int x = minRegionX; x <= maxRegionX; x++)
			for(int z = minRegionZ; z <= maxRegionZ; z++)
				factory.addHeightModifiers(mods, x, z, seed);
	}
	
	public static void addBlockModifications(Collection<BlockModification> mods, BlockModFactory factory, int minRegionX, int minRegionZ, int maxRegionX, int maxRegionZ, long seed){
		for(int x = minRegionX; x <= maxRegionX; x++)
			for(int z = minRegionZ; z <= maxRegionZ; z++)
				factory.addBlockModifications(mods, x, z, seed);
	}
	
	public static Collection<BlockModification> getBlockModifications(BlockModFactory factory, int maxRadius, int regionSize, int chunkX, int chunkZ, long seed){
		List<BlockModification> mods = new ArrayList<BlockModification>();
		int[] bounds = getBounds(chunkX, chunkZ, maxRadius, regionSize);
		addBlockModifications(mods, factory, bounds[0], bounds[1], bounds[2], bounds[3], seed);
		return mods;
	}
	
	public static Collection<HeightModifier> getHeightModifiers(HeightModFactory factory, int maxRadius, int regionSize, int chunkX, int chunkZ, long seed){
		List<HeightModifier> mods = new ArrayList<HeightModifier>();
		int[] bounds = getBounds(chunkX, chunkZ, maxRadius, regionSize);
		addRegionModifiers(mods, factory, bounds[0], bounds[1], bounds[2], bounds[3], seed);
		return mods;
	}
	
	public static int[] getBounds(int chunkX, int chunkZ, int maxRadius, int regionSize){
		int minX = chunkX * 16 - maxRadius;
		int maxX = minX + 15 + 2 * maxRadius;
		int minZ = chunkZ * 16 - maxRadius;
		int maxZ = minZ + 15 + 2 * maxRadius;
		int minRegionX = Math.floorDiv(minX, regionSize);
		int maxRegionX = Math.floorDiv(maxX, regionSize);
		int minRegionZ = Math.floorDiv(minZ, regionSize);
		int maxRegionZ = Math.floorDiv(maxZ, regionSize);
		/*
		if(minX < 0)
			minRegionX--;
		if(maxX < 0)
			maxRegionX--;
		if(minZ < 0)
			minRegionZ--;
		if(maxZ < 0)
			maxRegionZ--;
			*/
		return new int[]{minRegionX, minRegionZ, maxRegionX, maxRegionZ};
	}
	
	/**
	 * @param world The world to search in
	 * @param x The x-coordinate
	 * @param z The z-coordinate
	 * @return The y-coordinate of the highest block of the surface at the given x and z coordinates or
	 * -1 if there is no surface at the given coordinates
	 */
	public static int getSurfaceHeight(World world, int x, int z) {
		for (int y = world.getMaxHeight(); y >= 0; y--) {
			Material type = world.getBlockAt(x, y, z).getType();
			if (type != Material.AIR && type != Material.LONG_GRASS && type != Material.DEAD_BUSH) {
				return y;
			}
		}
		return -1;
	}
	
	/**
	 * Builds a nether portal at the given coordinates if there is no nether portal there already.
	 * @param nether The world to build the portal in
	 * @param x The x-coordinate of the obsidian block under the portal block with the smallest x-coordinate
	 * @param y The y-coordinate of the obsidian floor
	 * @param z The z-coordinate of the portal (the entire portal will have the same z-coordinate)
	 */
	public static void buildNetherPortal(World nether, int x, int y, int z) {
		// TODO create a solid system for building nether portals
		if (nether.getBlockAt(x, y + 1, z).getType() != Material.PORTAL) {
			// Floor
			nether.getBlockAt(x, y, z).setType(Material.OBSIDIAN);
			nether.getBlockAt(x + 1, y, z).setType(Material.OBSIDIAN);
			nether.getBlockAt(x - 1, y, z).setType(Material.OBSIDIAN);
			nether.getBlockAt(x + 2, y, z).setType(Material.OBSIDIAN);
			// Top
			nether.getBlockAt(x, y + 4, z).setType(Material.OBSIDIAN);
			nether.getBlockAt(x + 1, y + 4, z).setType(Material.OBSIDIAN);
			nether.getBlockAt(x - 1, y + 4, z).setType(Material.OBSIDIAN);
			nether.getBlockAt(x + 2, y + 4, z).setType(Material.OBSIDIAN);
			// Left
			nether.getBlockAt(x - 1, y + 1, z).setType(Material.OBSIDIAN);
			nether.getBlockAt(x - 1, y + 2, z).setType(Material.OBSIDIAN);
			nether.getBlockAt(x - 1, y + 3, z).setType(Material.OBSIDIAN);
			// Right
			nether.getBlockAt(x + 2, y + 1, z).setType(Material.OBSIDIAN);
			nether.getBlockAt(x + 2, y + 2, z).setType(Material.OBSIDIAN);
			nether.getBlockAt(x + 2, y + 3, z).setType(Material.OBSIDIAN);
			// Light the portal
			nether.getBlockAt(x, y + 1, z).setType(Material.FIRE);
		}
	}
	
	public static boolean isNetherPortalTop(World world, int x, int y, int z) {
		return world.getBlockAt(x, y, z).getType() == Material.OBSIDIAN && world.getBlockAt(x, y - 1, z).getType() == Material.PORTAL;
	}
}