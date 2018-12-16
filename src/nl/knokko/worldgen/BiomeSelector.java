package nl.knokko.worldgen;

import org.bukkit.block.Biome;

public abstract class BiomeSelector {
	
	public abstract Biome getBiome(int x, int z, int height);
}
