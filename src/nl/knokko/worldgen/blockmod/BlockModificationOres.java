package nl.knokko.worldgen.blockmod;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Random;

import org.bukkit.Material;
import org.bukkit.material.MaterialData;

public class BlockModificationOres implements BlockModification {
	
	private final MaterialData ore;
	
	private final Collection<int[]> positions;
	
	public BlockModificationOres(Random random, Material ore, int size, int amount, int minY, int maxY, int chunkX, int chunkZ){
		this.ore = new MaterialData(ore);
		this.positions = new ArrayList<int[]>(size * amount);
		for(int a = 0; a < amount; a++){
			int x = chunkX * 16 + random.nextInt(16);
			int y = minY + random.nextInt(maxY - minY);
			int z = chunkZ * 16 + random.nextInt(16);
			positions.add(new int[]{x, y, z});
			for(int i = 1; i < size; i++){
				int direction = random.nextInt(6);
				switch(direction){
					case 0: z--;
					case 1: x++;
					case 2: z++;
					case 3: x--;
					case 4: y++;
					case 5: y--;
				}
				positions.add(new int[]{x, y, z});
			}
		}
	}

	public MaterialData getType() {
		return ore;
	}

	public Collection<int[]> getPositions() {
		return positions;
	}

}
