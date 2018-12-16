package nl.knokko.worldgen.blockmod;

import java.util.Collection;

import org.bukkit.material.MaterialData;

public interface BlockModification {
	
	MaterialData getType();
	
	Collection<int[]> getPositions();
}
