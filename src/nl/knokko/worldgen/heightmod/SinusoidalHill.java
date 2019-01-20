package nl.knokko.worldgen.heightmod;

public class SinusoidalHill implements HeightModifier {
	
	private int centerX;
	private int centerZ;
	
	private int height;
	private double multiplier;
	private double power;
	
	private double amplitude;
	private double frequency;
	private double phase;
	
	public SinusoidalHill(int centerX, int centerZ, int height, double multiplier, double power, 
			double amplitude, double frequency, double phase) {
		this.centerX = centerX;
		this.centerZ = centerZ;
		this.height = height;
		this.multiplier = multiplier;
		this.power = power;
		this.amplitude = amplitude;
		this.frequency = frequency;
		this.phase = phase;
	}

	@Override
	public int getExtraHeight(int x, int z) {
		double distance = Math.sqrt((x - centerX) * (x - centerX) + (z - centerZ) * (z - centerZ));
		if (x != centerX || z != centerZ) {
			double angle = Math.atan2(z - centerZ, x - centerX);
			distance *= 1 + amplitude * Math.sin(angle * frequency + phase);
		}
		return height - (int) (multiplier * Math.pow(distance, power));
	}

	@Override
	public boolean canIncrease() {
		return true;
	}

	@Override
	public boolean canDecrease() {
		return false;
	}
}