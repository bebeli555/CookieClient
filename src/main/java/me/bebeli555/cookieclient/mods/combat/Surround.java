package me.bebeli555.cookieclient.mods.combat;

import me.bebeli555.cookieclient.Mod;
import me.bebeli555.cookieclient.events.player.PlayerMotionUpdateEvent;
import me.bebeli555.cookieclient.gui.Group;
import me.bebeli555.cookieclient.gui.Mode;
import me.bebeli555.cookieclient.gui.Setting;
import me.bebeli555.cookieclient.utils.BlockUtil;
import me.bebeli555.cookieclient.utils.InventoryUtil;
import me.bebeli555.cookieclient.utils.RotationUtil;
import me.zero.alpine.listener.EventHandler;
import me.zero.alpine.listener.Listener;
import net.minecraft.init.Blocks;
import net.minecraft.util.math.BlockPos;

public class Surround extends Mod {
	public static Setting blocksPerTick = new Setting(Mode.INTEGER, "BlocksPerTick", 1, "How many blocks to place per tick");
	public static Setting center = new Setting(Mode.BOOLEAN, "Center", true, "Center before so it can place all the blocks");
	
	public Surround() {
		super(Group.COMBAT, "Surround", "Surrounds your feet with obsidian", "Useful for blocking crystal damage");
	}
	
	@Override
	public void onDisabled() {
		RotationUtil.stopRotating();
	}
	
    @EventHandler
    private Listener<PlayerMotionUpdateEvent> onMotionUpdate = new Listener<>(p_Event -> {
		if (mc.player == null) {
			return;
		}
		
		if (!InventoryUtil.hasBlock(Blocks.OBSIDIAN)) {
			disable();
			sendMessage("You need obsidian", true);
			return;
		}
		
		int blocksPlaced = 0;
		for (BlockPos pos : getBlocksToPlace()) {
			if (BlockUtil.distance(getPlayerPos(), pos) > 2) {
				break;
			}
			
			if (!isSolid(pos)) {
				if (InventoryUtil.hasBlock(Blocks.OBSIDIAN)) {
					boolean canPlace = BlockUtil.canPlaceBlock(pos);
					boolean canPlaceBelow = BlockUtil.canPlaceBlock(pos.add(0, -1, 0));
					
					if (center.booleanValue() && canPlace || center.booleanValue() && canPlaceBelow) {
						if (!center()) {
							return;
						}
					}
					
					if (canPlace) {
						BlockUtil.placeBlockOnThisThread(Blocks.OBSIDIAN, pos, true);
					} else if (canPlaceBelow){
						BlockUtil.placeBlockOnThisThread(Blocks.OBSIDIAN, pos.add(0, -1, 0), true);
					} else {
						continue;
					}
					
					RotationUtil.stopRotating();
					blocksPlaced++;
					if (blocksPlaced >= blocksPerTick.intValue()) {
						return;
					}
				}
			}
		}
    });
	
	/**
	 * Centers the player
	 */
	public static boolean center() {
		if (isCentered()) {
			return true;
		}
		
		double[] centerPos = {Math.floor(mc.player.posX) + 0.5, Math.floor(mc.player.posY), Math.floor(mc.player.posZ) + 0.5};
		mc.player.motionX = (centerPos[0] - mc.player.posX) / 2;
		mc.player.motionZ = (centerPos[2] - mc.player.posZ) / 2;
		return false;
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