package nl.knokko.worldgen;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Random;

import org.bukkit.Material;
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
					height = (int) ((maxHeight * height) / (float)(height + 90));
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
}
