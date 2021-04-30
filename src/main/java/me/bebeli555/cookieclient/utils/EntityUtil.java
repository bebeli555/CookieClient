package me.bebeli555.cookieclient.utils;

import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityAgeable;
import net.minecraft.entity.EnumCreatureType;
import net.minecraft.entity.monster.EntityEnderman;
import net.minecraft.entity.monster.EntityIronGolem;
import net.minecraft.entity.monster.EntityPigZombie;
import net.minecraft.entity.passive.EntityAmbientCreature;
import net.minecraft.entity.passive.EntityAnimal;
import net.minecraft.entity.passive.EntitySquid;
import net.minecraft.entity.passive.EntityTameable;
import net.minecraft.entity.passive.EntityWolf;

public class EntityUtil {
	
	/**
	 * Checks if the entity is passive. Like an animal
	 */
    public static boolean isPassive(Entity e) {
        if (e instanceof EntityWolf && ((EntityWolf) e).isAngry()) {
            return false;
        }
        
        if (e instanceof EntityAnimal || e instanceof EntityAgeable || e instanceof EntityTameable || e instanceof EntityAmbientCreature || e instanceof EntitySquid) {
            return true;
        }
        
        if (e instanceof EntityIronGolem && ((EntityIronGolem) e).getRevengeTarget() == null) {
            return true;        	
        }

        return false;
    }
    
    /**
     * The mob wont attack player but will if player attacks it
     */
    public static boolean isNeutralMob(Entity entity) {
        return entity instanceof EntityPigZombie || entity instanceof EntityWolf || entity instanceof EntityEnderman;
    }
    
    /**
     * Is the mob hostile. Like will allways try to attack player
     */
    public static boolean isHostileMob(Entity entity) {
        return (entity.isCreatureType(EnumCreatureType.MONSTER, false) && !EntityUtil.isNeutralMob(entity));
    }
}
