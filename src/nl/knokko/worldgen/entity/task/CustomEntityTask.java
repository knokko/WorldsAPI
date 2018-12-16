package nl.knokko.worldgen.entity.task;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

import org.bukkit.entity.Entity;

public abstract class CustomEntityTask {
	
	private static final Class<?>[] TYPES = {TaskOceanZombie.class};
	
	public static CustomEntityTask load(DataInputStream input) throws IllegalArgumentException, IOException {
		byte type = input.readByte();
		try {
			return (CustomEntityTask) TYPES[type + 128].getConstructor(DataInputStream.class).newInstance(input);
		} catch(Exception ex){
			throw new IllegalArgumentException(ex);
		}
	}
	
	public abstract void update();
	
	protected abstract boolean isDead();
	
	protected abstract void saveExtra(DataOutputStream output) throws IOException;
	
	public abstract void onEntityDeath(Entity entity);
	
	protected abstract byte getClassID();
	
	public void save(DataOutputStream output) throws IOException {
		output.writeByte(getClassID());
		saveExtra(output);
	}
	
	public boolean isCompleted(){
		return isDead();
	}
}
