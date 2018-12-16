package nl.knokko.worldgen.heightmod;

import java.util.Collection;

public abstract class HeightModFactory {
	
	public abstract void addHeightModifiers(Collection<HeightModifier> mods, int regionX, int regionZ, long seed);
}
