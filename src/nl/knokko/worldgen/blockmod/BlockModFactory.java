package nl.knokko.worldgen.blockmod;

import java.util.Collection;

public abstract class BlockModFactory {
	
	public abstract void addBlockModifications(Collection<BlockModification> mods, int regionX, int regionZ, long seed);
}
