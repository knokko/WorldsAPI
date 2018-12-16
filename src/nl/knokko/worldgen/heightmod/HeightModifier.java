package nl.knokko.worldgen.heightmod;

public interface HeightModifier {
	
	int getExtraHeight(int x, int z);
	
	boolean canIncrease();
	
	boolean canDecrease();
}
