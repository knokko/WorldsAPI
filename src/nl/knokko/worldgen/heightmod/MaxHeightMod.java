package nl.knokko.worldgen.heightmod;

public class MaxHeightMod implements HeightModifier {
	
	private final HeightModifier[] mods;
	
	private boolean canIncrease;
	private boolean canDecrease;
	
	public MaxHeightMod(HeightModifier... mods) {
		this.mods = mods;
		for (HeightModifier mod : mods) {
			if (mod.canIncrease()) {
				canIncrease = true;
				break;
			}
		}
		
		canDecrease = true;
		for (HeightModifier mod : mods) {
			if (!mod.canDecrease()) {
				canDecrease = false;
				break;
			}
		}
	}

	@Override
	public int getExtraHeight(int x, int z) {
		if (mods.length == 0)
			return 0;
		int height = mods[0].getExtraHeight(x, z);
		for (int index = 1; index < mods.length; index++) {
			int current = mods[index].getExtraHeight(x, z);
			if (current > height) {
				height = current;
			}
		}
		return height;
	}

	@Override
	public boolean canIncrease() {
		return canIncrease;
	}

	@Override
	public boolean canDecrease() {
		return canDecrease;
	}
}