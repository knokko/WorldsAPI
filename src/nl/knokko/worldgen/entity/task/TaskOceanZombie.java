package nl.knokko.worldgen.entity.task;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.util.Collection;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.attribute.Attribute;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.entity.Zombie;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.util.Vector;

public class TaskOceanZombie extends CustomEntityTask {
	
	protected static final double MAX_DISTANCE_SQRT = 200 * 200;
	protected static final double ATTACK_RANGE_SQRT = 1 * 1;
	protected static final double LOSE_TARGET_RANGE = 300 * 300;
	
	protected UUID zombieID;
	protected Zombie zombie;
	
	protected LivingEntity target;
	
	protected byte targetTimer;
	protected byte attackTimer;
	protected byte zombieTimer;

	public TaskOceanZombie(Zombie zombie) {
		this.zombie = zombie;
		this.zombieID = zombie.getUniqueId();
	}
	
	public TaskOceanZombie(DataInputStream input) throws IOException {
		zombieID = new UUID(input.readLong(), input.readLong());
		Entity entity = Bukkit.getEntity(zombieID);
		if(entity instanceof Zombie)
			zombie = (Zombie) entity;
		
	}

	@Override
	public void update() {
		if(zombieTimer == 0)
			zombie = findZombie();
		else
			zombieTimer--;
		if(zombie == null) {
			target = null;
			return;
		}
		if(zombie.getLocation().getBlock().isLiquid()){
			zombie.setRemainingAir(120);
			zombie.setAI(false);
			if(target == null && targetTimer == 0){
				Collection<Player> players = zombie.getWorld().getEntitiesByClass(Player.class);
				double distance = MAX_DISTANCE_SQRT;
				for(Player player : players){
					if(player.getLocation().getBlock().isLiquid() && !player.hasPotionEffect(PotionEffectType.INVISIBILITY) && (player.getGameMode() == GameMode.ADVENTURE || player.getGameMode() == GameMode.SURVIVAL)){
						double current = player.getLocation().distanceSquared(zombie.getLocation());
						if(current < distance){
							distance = current;
							target = player;
						}
					}
				}
				targetTimer = 40;
			}
			if(targetTimer > 0)
				targetTimer--;
			if(target != null){
				if(target.getWorld() != zombie.getWorld()){
					target = null;
					return;
				}
				Vector vector = target.getLocation().subtract(zombie.getLocation()).toVector();
				double distanceSQ = vector.lengthSquared();
				if(distanceSQ > LOSE_TARGET_RANGE){
					target = null;
					return;
				}
				if(distanceSQ > ATTACK_RANGE_SQRT){
					Location old = zombie.getLocation().clone();
					vector.normalize();
					vector.multiply(0.15);
					zombie.teleport(zombie.getLocation().add(vector.getX(), 0, 0));
					if(!zombie.getLocation().getBlock().isLiquid())
						zombie.teleport(old);
					zombie.teleport(zombie.getLocation().add(0, vector.getY(), 0));
					if(!zombie.getLocation().getBlock().isLiquid())
						zombie.teleport(old);
					zombie.teleport(zombie.getLocation().add(0, 0, vector.getZ()));
					if(!zombie.getLocation().getBlock().isLiquid())
						zombie.teleport(old);
				}
				else if(attackTimer == 0){
					target.damage(zombie.getAttribute(Attribute.GENERIC_ATTACK_DAMAGE).getValue(), zombie);
					attackTimer = 30;
					vector.normalize();
				}
				zombie.teleport(zombie.getLocation().setDirection(vector));
			}
			if(attackTimer > 0)
				attackTimer--;
		}
		else
			zombie.setAI(true);
	}

	@Override
	protected boolean isDead() {
		return zombieID == null || (zombie != null && !zombie.isValid());
	}

	@Override
	protected void saveExtra(DataOutputStream output) throws IOException {
		output.writeLong(zombieID.getMostSignificantBits());
		output.writeLong(zombieID.getLeastSignificantBits());
	}

	@Override
	protected byte getClassID() {
		return -128;
	}
	
	@Override
	public void onEntityDeath(Entity entity){
		if(zombieID != null && entity.getUniqueId().equals(zombieID)){
			zombieID = null;
			zombie = null;
			target = null;
		}
	}
	
	protected Zombie findZombie(){
		zombieTimer = 100;
		Entity entity = Bukkit.getEntity(zombieID);
		if(entity instanceof Zombie)
			return (Zombie) entity;
		return null;
	}
}
