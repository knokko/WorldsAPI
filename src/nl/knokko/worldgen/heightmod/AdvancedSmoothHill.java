package nl.knokko.worldgen.heightmod;

public class AdvancedSmoothHill extends SmoothHill {
	
	private final double delay;

	public AdvancedSmoothHill(int centerX, int centerZ, int height, double multiplier, double delay, double power) {
		super(centerX, centerZ, height, multiplier, power);
		this.delay = delay;
	}
	
	@Override
	public int getExtraHeight(int x, int z) {
		return (int) (height - multiplier * Math.pow(delay * Math.sqrt((centerX - x) * (centerX - x) + (centerZ - z) * (centerZ - z)), power));
		//height - multiplier * (distance * delay) ^ power = 0
		//-multiplier * (distance * delay) ^ power = -height
		//(distance * delay) ^ power = height / multiplier
		//distance * delay = (height / multiplier) ^ (1 / power)
		//distance = ((height / multiplier) ^ (1 / power)) / delay
	}
	
	@Override
	public String toString(){
		return "AdvancedSmoothHill(" + centerX + ", " + centerZ + ", " + height + ", " + fix(multiplier, 3) + ", " + fix(delay, 4) + ", " + fix(power, 3) + ")";
	}
}
