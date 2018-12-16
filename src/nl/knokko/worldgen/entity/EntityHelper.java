package nl.knokko.worldgen.entity;

import net.minecraft.server.v1_12_R1.Entity;
import net.minecraft.server.v1_12_R1.WorldServer;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.attribute.Attributable;
import org.bukkit.attribute.Attribute;
import org.bukkit.craftbukkit.v1_12_R1.CraftWorld;
import org.bukkit.entity.LivingEntity;
import org.bukkit.inventory.ItemStack;

import static org.bukkit.attribute.Attribute.*;

public class EntityHelper {
	
	public static void setMaxHealth(LivingEntity entity, double value){
		setAttribute(GENERIC_MAX_HEALTH, entity, value);
		entity.setHealth(value);
	}
	
	public static void setMovementSpeed(LivingEntity entity, double value){
		setAttribute(GENERIC_MOVEMENT_SPEED, entity, value);
	}
	
	public static void setFollowRange(LivingEntity entity, double value){
		setAttribute(GENERIC_FOLLOW_RANGE, entity, value);
	}
	
	public static void setAttackDamage(LivingEntity entity, double value){
		setAttribute(GENERIC_ATTACK_DAMAGE, entity, value);
	}
	
	public static void setAttribute(Attribute attribute, Attributable entity, double value){
		entity.getAttribute(attribute).setBaseValue(value);
	}
	
	public static void setAttributes(LivingEntity entity, double maxHealth, double attackDamage, double movementSpeed, double followRange){
		setMaxHealth(entity, maxHealth);
		setAttackDamage(entity, attackDamage);
		setMovementSpeed(entity, movementSpeed);
		setFollowRange(entity, followRange);
	}
	
	public static void setHelmet(LivingEntity entity, ItemStack helmet){
		entity.getEquipment().setHelmet(helmet);
	}
	
	public static void setHelmet(LivingEntity entity, Material helmet){
		setHelmet(entity, new ItemStack(helmet));
	}
	
	public static void setChestplate(LivingEntity entity, ItemStack chestplate){
		entity.getEquipment().setChestplate(chestplate);
	}
	
	public static void setChestplate(LivingEntity entity, Material chestplate){
		setChestplate(entity, new ItemStack(chestplate));
	}
	
	public static void setLeggings(LivingEntity entity, ItemStack leggings){
		entity.getEquipment().setLeggings(leggings);
	}
	
	public static void setLeggings(LivingEntity entity, Material leggings){
		setLeggings(entity, new ItemStack(leggings));
	}
	
	public static void setBoots(LivingEntity entity, ItemStack boots){
		entity.getEquipment().setBoots(boots);
	}
	
	public static void setBoots(LivingEntity entity, Material boots){
		setBoots(entity, new ItemStack(boots));
	}
	
	public static void setArmor(LivingEntity entity, ItemStack helmet, ItemStack chestplate, ItemStack leggings, ItemStack boots){
		setHelmet(entity, helmet);
		setChestplate(entity, chestplate);
		setLeggings(entity, leggings);
		setBoots(entity, boots);
	}
	
	public static void setArmor(LivingEntity entity, Material helmet, Material chestplate, Material leggings, Material boots){
		setHelmet(entity, helmet);
		setChestplate(entity, chestplate);
		setLeggings(entity, leggings);
		setBoots(entity, boots);
	}
	
	public static void setWeapon(LivingEntity entity, ItemStack weapon){
		entity.getEquipment().setItemInMainHand(weapon);
	}
	
	public static void setWeapon(LivingEntity entity, Material weapon){
		setWeapon(entity, new ItemStack(weapon));
	}
	
	public static void setEquipment(LivingEntity entity, ItemStack weapon, ItemStack helmet, ItemStack chestplate, ItemStack leggings, ItemStack boots){
		setArmor(entity, helmet, chestplate, leggings, boots);
		setWeapon(entity, weapon);
	}
	
	public static void setEquipment(LivingEntity entity, Material weapon, Material helmet, Material chestplate, Material leggings, Material boots){
		setArmor(entity, helmet, chestplate, leggings, boots);
		setWeapon(entity, weapon);
	}
	
	public static void spawn(Entity entity, Location location){
		entity.setLocation(location.getX(), location.getY(), location.getZ(), 0, 0);
		WorldServer world = ((CraftWorld)location.getWorld()).getHandle();
		world.addEntity(entity);
		//world.h(Entity)
		//world.g(Entity)
	}
}
