package me.bebeli555.cookieclient.mods.combat;

import me.bebeli555.cookieclient.Mod;
import me.bebeli555.cookieclient.gui.Group;
import me.bebeli555.cookieclient.gui.Mode;
import me.bebeli555.cookieclient.gui.Setting;
import me.bebeli555.cookieclient.utils.BlockUtil;
import me.bebeli555.cookieclient.utils.InventoryUtil;
import me.bebeli555.cookieclient.utils.RotationUtil;
import net.minecraft.init.Blocks;
import net.minecraft.util.math.BlockPos;

public class Surround extends Mod {
	private static Thread thread;
	
	public static Setting delay = new Setting(Mode.INTEGER, "Delay", 25, "Delay in ms to wait between block places");
	public static Setting center = new Setting(Mode.BOOLEAN, "Center", true, "Center before so it can place all the blocks");
	
	public Surround() {
		super(Group.COMBAT, "Surround", "Surrounds your feet with obsidian", "Useful for blocking crystal damage");
	}
	
	@Override
	public void onEnabled() {
		thread = new Thread() {
			public void run() {
				while(thread != null && thread.equals(this)) {
					loop();
					
					Mod.sleep(30);
				}
			}
		};
		
		thread.start();
	}
	
	@Override
	public void onDisabled() {
		thread = null;
		RotationUtil.stopRotating();
	}
	
	public void loop() {
		if (mc.player == null) {
			return;
		}
		
		if (!InventoryUtil.hasBlock(Blocks.OBSIDIAN)) {
			disable();
			sendMessage("You need obsidian", true);
			return;
		}
		
		for (BlockPos pos : getBlocksToPlace()) {
			if (BlockUtil.distance(getPlayerPos(), pos) > 2) {
				break;
			}
			
			if (!isSolid(pos)) {
				if (InventoryUtil.hasBlock(Blocks.OBSIDIAN)) {
					if (center.booleanValue()) {
						center();
					}
					
					if (BlockUtil.canPlaceBlock(pos)) {
						BlockUtil.placeBlockNoSleep(Blocks.OBSIDIAN, pos, true);
					} else if (BlockUtil.canPlaceBlock(pos.add(0, -1, 0))){
						BlockUtil.placeBlockNoSleep(Blocks.OBSIDIAN, pos.add(0, -1, 0), true);
					} else {
						continue;
					}
					
					RotationUtil.stopRotating();
					sleep(delay.intValue());
				}
			}
		}
	}
	
	/**
	 * Centers the player
	 */
	public static void center() {
		if (isCentered()) {
			return;
		}
		
		double[] centerPos = {Math.floor(mc.player.posX) + 0.5, Math.floor(mc.player.posY), Math.floor(mc.player.posZ) + 0.5};
		
		mc.player.motionX = (centerPos[0] - mc.player.posX) / 2;
		mc.player.motionZ = (centerPos[2] - mc.player.posZ) / 2;
		
		sleepUntil(() -> Math.abs(centerPos[0] - mc.player.posX) <= 0.1 && Math.abs(centerPos[2] - mc.player.posZ) <= 0.1, 1000);
		mc.player.motionX = 0;
		mc.player.motionZ = 0;
	}
	
	/**
	 * Checks if the player is centered on the block
	 */
	public static boolean isCentered() {
		double[] centerPos = {Math.floor(mc.player.posX) + 0.5, Math.floor(mc.player.posY), Math.floor(mc.player.posZ) + 0.5};
		return Math.abs(centerPos[0] - mc.player.posX) <= 0.2 && Math.abs(centerPos[2] - mc.player.posZ) <= 0.2;
	}
	
	/**
	 * Checks if the given BlockPos is surrounded with obby or bedrock
	 */
	public static boolean isSurrounded(BlockPos p) {
		BlockPos[] positions = {p.add(1, 0, 0), p.add(-1, 0, 0), p.add(0, 0, 1), p.add(0, 0, -1)};
		
 		for (BlockPos pos : positions) {
 			if (getBlock(pos) != Blocks.OBSIDIAN && getBlock(pos) != Blocks.BEDROCK) {
 				return false;
 			}
 		}
 		
 		return true;
	}
	
	/**
	 * Get the blockpositions where to place obby
	 */
	public static BlockPos[] getBlocksToPlace() {
		BlockPos p = getPlayerPos();
		return new BlockPos[]{p.add(1, 0, 0), p.add(-1, 0, 0), p.add(0, 0, 1), p.add(0, 0, -1)};
	}
}