package me.bebeli555.cookieclient.utils;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import me.bebeli555.cookieclient.Mod;
import net.minecraft.block.Block;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.item.EntityEnderCrystal;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.potion.Potion;
import net.minecraft.util.CombatRules;
import net.minecraft.util.DamageSource;
import net.minecraft.util.NonNullList;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.Explosion;

public class CrystalUtil extends Mod{
	
	/**
	 * Gets all the crystals around you if the distance is lower or equal
	 *
	 */
	public static ArrayList<EntityEnderCrystal> getCrystals(double distance) {
		ArrayList<EntityEnderCrystal> list = new ArrayList<EntityEnderCrystal>();
		
		for (Entity entity : mc.world.loadedEntityList) {
			if (entity instanceof EntityEnderCrystal) {
				if (entity.getDistance(mc.player) <= distance) {
					list.add((EntityEnderCrystal)entity);
				}
			}
		} 
		
		return list;
	}
	
	/**
	 * Gets the end crystal in the given blockpos. If theres none then returns null
	 */
	public static EntityEnderCrystal getCrystalInPos(BlockPos pos) {
		for (Entity entity : mc.world.getEntitiesWithinAABBExcludingEntity(null, new AxisAlignedBB(pos.add(0, 1, 0)))) {
			if (entity instanceof EntityEnderCrystal && entity.isEntityAlive()) {
				return (EntityEnderCrystal)entity;
			}
		}
		
		return null;
	}
	
    /**
     * Calculates crystal damage if the crystal is on pos to the entity
     */
    public static float calculateDamage(Vec3d pos, EntityLivingBase entity) {
    	try {
    		if (entity.getDistance(pos.x, pos.y, pos.z) > 12) {
    			return 0;
    		}
    		
            double blockDensity = entity.world.getBlockDensity(pos, entity.getEntityBoundingBox());
            double power = (1.0D - (entity.getDistance(pos.x, pos.y, pos.z) / 12.0D)) * blockDensity;
            float damage = (float) ((int) ((power * power + power) / 2.0D * 7.0D * 12.0D + 1.0D));

            int difficulty = mc.world.getDifficulty().getId();
            damage *= (difficulty == 0 ? 0 : (difficulty == 2 ? 1 : (difficulty == 1 ? 0.5f : 1.5f)));

            return getReduction(entity, damage, new Explosion(mc.world, null, pos.x, pos.y, pos.z, 6F, false, true));
    	} catch (NullPointerException e) {
    		return 0;
    	}
    }

    public static float calculateDamage(BlockPos pos, EntityLivingBase entity) {
    	try {
    		return calculateDamage(new Vec3d(pos.getX() + 0.5, pos.getY() + 1, pos.getZ() + 0.5), entity);
    	} catch (NullPointerException e) {
    		return 0;
    	}
    }
    
    public static float getReduction(EntityLivingBase player, float damage, Explosion explosion) {
        damage = CombatRules.getDamageAfterAbsorb(damage, (float) player.getTotalArmorValue(), (float) player.getEntityAttribute(SharedMonsterAttributes.ARMOR_TOUGHNESS).getAttributeValue());
        damage *= (1.0F - (float) EnchantmentHelper.getEnchantmentModifierDamage(player.getArmorInventoryList(), DamageSource.causeExplosionDamage(explosion)) / 25.0F);
        
        if (player.isPotionActive(Potion.getPotionById(11))) {
        	damage -= damage / 4;
        }

        return damage;
    }
    
    public static boolean canPlaceCrystal(BlockPos pos) {
        Block block = getBlock(pos);

         if (block == Blocks.OBSIDIAN || block == Blocks.BEDROCK) {
             Block floor = mc.world.getBlockState(pos.add(0, 1, 0)).getBlock();
             Block ceil = mc.world.getBlockState(pos.add(0, 2, 0)).getBlock();

             if (floor == Blocks.AIR && ceil == Blocks.AIR) {
            	 ArrayList<Entity> entities = new ArrayList<Entity>();
            	 entities.addAll(mc.world.getEntitiesWithinAABBExcludingEntity(null, new AxisAlignedBB(pos.add(0, 1, 0))));
            	 for (Entity entity : entities) {
            		 if (entity.isEntityAlive()) {
            			 return false;
            		 }
            	 }
            	 
            	 return true;
             }
         }

         return false;
     }
    
    public static List<BlockPos> findCrystalBlocks(EntityPlayer player, float range) {
        NonNullList<BlockPos> positions = NonNullList.create();
        positions.addAll(getSphere(GetPlayerPosFloored(player), range, (int) range, false, true, 0)
        		.stream().filter(CrystalUtil::canPlaceCrystal).collect(Collectors.toList()));
        return positions;
    }
    
    public static List<BlockPos> getSphere(BlockPos pos, float r, int h, boolean hollow, boolean sphere, int plusY) {
        List<BlockPos> circleblocks = new ArrayList<>();
        int cx = pos.getX();
        int cy = pos.getY();
        int cz = pos.getZ();
        for (int x = cx - (int) r; x <= cx + r; x++) {
            for (int z = cz - (int) r; z <= cz + r; z++) {
                for (int y = (sphere ? cy - (int) r : cy); y < (sphere ? cy + r : cy + h); y++) {
                    double dist = (cx - x) * (cx - x) + (cz - z) * (cz - z) + (sphere ? (cy - y) * (cy - y) : 0);
                    if (dist < r * r && !(hollow && dist < (r - 1) * (r - 1))) {
                        circleblocks.add(new BlockPos(x, y + plusY, z));
                    }
                }
            }
        }
        
        return circleblocks;
    }
    
    public static BlockPos GetPlayerPosFloored(EntityPlayer player) {
        return new BlockPos(Math.floor(player.posX), Math.floor(player.posY), Math.floor(player.posZ));
    }
}
