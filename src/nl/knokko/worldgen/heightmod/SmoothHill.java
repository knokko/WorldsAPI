package nl.knokko.worldgen.heightmod;

public class SmoothHill implements HeightModifier {
	
	public static String fix(double value, int decimals){
		String s = Double.toString(value);
		if(!s.contains("E")){
			int index = s.indexOf(".");
			if(index != -1){
				if(s.length() > index + decimals + 1)
					return s.substring(0, index + decimals + 1);
			}
		}
		return s;
	}
	
	final int centerX;
	final int centerZ;
	
	final int height;
	final double multiplier;
	final double power;

	public SmoothHill(int centerX, int centerZ, int height, double multiplier, double power) {
		this.centerX = centerX;
		this.centerZ = centerZ;
		this.height = height;
		this.multiplier = multiplier;
		this.power = power;
	}
	
	public int getExtraHeight(int x, int z) {
		return (int) (height - multiplier * Math.pow(Math.sqrt((centerX - x) * (centerX - x) + (centerZ - z) * (centerZ - z)), power));
		
		/*
		 * height - multiplier * (distance ^ power) = 0
		 * -multiplier * (distance ^ power) = -height
		 * distance ^ power = height / multiplier
		 * distance = (height / multiplier) ^ (1 / power)
		 */
	}
	
	@Override
	public String toString(){
		return "SmoothHill(" + centerX + ", " + centerZ + ", " + height + ", " + fix(multiplier, 3) + ", " + fix(power, 3) + ")";
	}

	public boolean canIncrease() {
		return true;
	}

	public boolean canDecrease() {
		return false;
	}
}
