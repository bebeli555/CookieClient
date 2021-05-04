package me.bebeli555.cookieclient.utils;

import me.bebeli555.cookieclient.Mod;
import me.bebeli555.cookieclient.events.bus.EventHandler;
import me.bebeli555.cookieclient.events.bus.Listener;
import me.bebeli555.cookieclient.events.player.PlayerMotionUpdateEvent;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.math.Vec3d;

public class MiningUtil extends Mod {
	public static MiningUtil miningUtil = new MiningUtil();
	public static EnumFacing facing;
	public static BlockPos pos;
	public static boolean start, spoofRotation, isMining;
	
	/**
	 * Switches to pickaxe and mines the block in the given position
	 */
	public static boolean mine(BlockPos pos, boolean spoofRotation) {
		if (!hasPickaxe()) {
			return false;
		}
		
		InventoryUtil.switchItem(InventoryUtil.getSlot(Items.DIAMOND_PICKAXE), false);
		
		if (mc.player.inventory.getCurrentItem().getItem() == Items.DIAMOND_PICKAXE) {
			MiningUtil.pos = pos;
			facing = getFacing(pos);
			start = true;
			MiningUtil.spoofRotation = spoofRotation;
			
			isMining = true;
			Mod.EVENT_BUS.subscribe(miningUtil);
			sleepUntil(() -> !isSolid(pos), 15000);
			Mod.EVENT_BUS.unsubscribe(miningUtil);
			isMining = false;
			RotationUtil.stopRotating();
			return !isSolid(pos);
		}
		
		return false;
	}
	
	/**
	 * Mines the block even if you dont have a pickaxe
	 */
	public static void mineAnyway(BlockPos pos, boolean spoofRotation) {
		if (hasPickaxe()) {
			InventoryUtil.switchItem(InventoryUtil.getSlot(Items.DIAMOND_PICKAXE), false);
		}
		
		MiningUtil.pos = pos;
		facing = getFacing(pos);
		start = true;
		MiningUtil.spoofRotation = spoofRotation;
		
		isMining = true;
		Mod.EVENT_BUS.subscribe(miningUtil);
		sleepUntil(() -> !isSolid(pos), 15000);
		Mod.EVENT_BUS.unsubscribe(miningUtil);
		isMining = false;
		RotationUtil.stopRotating();
	}
	
	/**
	 * Mines the blockpos without switching items
	 */
	public static boolean mineWithoutSwitch(BlockPos pos) {
		MiningUtil.pos = pos;
		facing = getFacing(pos);
		start = true;
		
		Mod.EVENT_BUS.subscribe(miningUtil);
		sleepUntil(() -> !isSolid(pos) && getBlock(pos) != Blocks.WEB, 6000);
		Mod.EVENT_BUS.unsubscribe(miningUtil);
		return !isSolid(pos);
	}
	
	@EventHandler
	private Listener<PlayerMotionUpdateEvent> onMotionUpdate = new Listener<>(event -> {
		try {
			if (mc.player == null || getBlock(pos) == Blocks.AIR) {
				return;
			}
			
			if (spoofRotation) {
				RotationUtil.rotateSpoof(new Vec3d(pos).add(0.5, 0.5, 0.5).add(new Vec3d(facing.getDirectionVec()).scale(0.5)));
			} else {
				RotationUtil.rotate(new Vec3d(pos).add(0.5, 0.5, 0.5).add(new Vec3d(facing.getDirectionVec()).scale(0.5)), false);
			}
	        mc.player.swingArm(EnumHand.MAIN_HAND);
			
	        if (start) {
	        	start = false;
	        	if (!spoofRotation) RotationUtil.rotate(new Vec3d(pos).add(0.5, 0.5, 0.5).add(new Vec3d(facing.getDirectionVec()).scale(0.5)), true);
	        } else {
	        	mc.playerController.onPlayerDamageBlock(pos, facing);
	        }
		} catch (Exception ignored) {
			
		}
	});
	
	/**
	 * Check if the player has a diamond pickaxe
	 */
	public static boolean hasPickaxe() {
		return InventoryUtil.hasItem(Items.DIAMOND_PICKAXE);
	}
	
	/**
	 * Checks if the blockpos can be mined legitimately
	 */
	public static boolean canMine(BlockPos pos) {
		if (!isSolid(pos) || getBlock(pos) == Blocks.BEDROCK || mc.player.getDistanceSq(pos) > 7) {
			return false;
		}
		
		Vec3d start = new Vec3d(mc.player.posX, mc.player.posY, mc.player.posZ);
		Vec3d end = new Vec3d(pos.getX() + 0.5, pos.getY() + 0.5, pos.getZ() + 0.5);
		RayTraceResult result = mc.world.rayTraceBlocks(start, end);
		
		if (result != null && result.getBlockPos().equals(pos)) {
			return true;
		} else {
			return false;
		}
	}
	
	public static EnumFacing getFacing(BlockPos pos) {
		EnumFacing closest = null;
		double lowestDistance = Integer.MAX_VALUE;
		for (EnumFacing facing : EnumFacing.values()) {
			BlockPos neighbor = pos.offset(facing);

            if (isSolid(neighbor)) {
               continue;
            }
            
            double distance = mc.player.getDistanceSq(neighbor);
            if (distance < lowestDistance) {
            	closest = facing;
            	lowestDistance = distance;
            }
		}
		
		return closest;
	}
}
