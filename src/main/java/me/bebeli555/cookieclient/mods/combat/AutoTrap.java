package me.bebeli555.cookieclient.mods.combat;

import java.util.ArrayList;

import me.bebeli555.cookieclient.Mod;
import me.bebeli555.cookieclient.gui.Group;
import me.bebeli555.cookieclient.gui.Mode;
import me.bebeli555.cookieclient.gui.Setting;
import me.bebeli555.cookieclient.utils.BlockUtil;
import me.bebeli555.cookieclient.utils.InventoryUtil;
import me.bebeli555.cookieclient.utils.PlayerUtil;
import me.bebeli555.cookieclient.utils.RotationUtil;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.util.math.BlockPos;

public class AutoTrap extends Mod {
	public static Thread thread;
	public static int oldSlot = -1;
	
	public static Setting toggle = new Setting(Mode.BOOLEAN, "Toggle", false, "Toggle the module off after a trap");
	public static Setting range = new Setting(Mode.DOUBLE, "Range", 4, "How far the player can be for autotrap to work");
	public static Setting delay = new Setting(Mode.INTEGER, "Delay", 50, "Delay in ms to wait between placing blocks");
	
	public AutoTrap() {
		super(Group.COMBAT, "AutoTrap", "Traps the nearby enemy with obsidian");
	}
	
	@Override
	public void onEnabled() {
		thread = new Thread() {
			public void run() {
				while(thread != null && thread.equals(this)) {
					loop();
				}
			}
		};
		
		thread.start();
	}
	
	@Override
	public void onDisabled() {
		RotationUtil.stopRotating();
		suspend(thread);
		thread = null;
	}
	
	public void loop() {
		if (mc.player == null) {
			return;
		}
		
		if (!InventoryUtil.hasBlock(Blocks.OBSIDIAN)) {
			sendMessage("You dont have any obsidian", true);
			disable();
		}
		
		EntityPlayer closest = PlayerUtil.getClosestEnemy();
		if (closest != null && mc.player.getDistance(closest) <= range.doubleValue()) {
			BlockPos p = new BlockPos(closest.posX, closest.posY, closest.posZ);
			
			ArrayList<BlockPos> positions = new ArrayList<BlockPos>();
			positions.add(p.add(1, 0, 0));
			positions.add(p.add(-1, 0, 0));
			positions.add(p.add(0, 0, 1));
			positions.add(p.add(0, 0, -1));
			positions.add(p.add(1, 1, 0));
			positions.add(p.add(-1, 1, 0));
			positions.add(p.add(0, 1, -1));
			positions.add(p.add(0, 1, 1));
			positions.add(p.add(0, 2, 0));
			
			if (!isSolid(p.add(0, 3, 0))) {
				BlockPos best = null;
				double highestDistance = Integer.MIN_VALUE;
				
				for (BlockPos pos : new BlockPos[]{p.add(1, 2, 0), p.add(-1, 2, 0), p.add(0, 2, 1), p.add(0, 2, -1)}) {
					if (mc.player.getDistanceSq(pos) > highestDistance) {
						best = pos;
						highestDistance = mc.player.getDistanceSq(pos);
					}
				}
				
				positions.add(best);
			}
			
			//Find the furthest away placable block and place it
			BlockPos best = null;
			double highestDistance = Integer.MIN_VALUE;
			
			for (BlockPos pos : positions) {
				if (BlockUtil.canPlaceBlock(pos) && BlockUtil.canBeClicked(pos) && mc.player.getDistanceSq(pos) > highestDistance) {
					best = pos;
					highestDistance = mc.player.getDistanceSq(pos);
				}
			}
			
			if (best != null) {
				if (oldSlot == -1) {
					oldSlot = mc.player.inventory.currentItem;
				}
				
				BlockUtil.placeBlockNoSleep(Blocks.OBSIDIAN, best, true);
				sleep(delay.intValue());
			} else if (toggle.booleanValue()) {
				disable();
			} else {
				RotationUtil.stopRotating();
				if (oldSlot != -1) mc.player.inventory.currentItem = oldSlot;
				oldSlot = -1;
				sleep(25);
			}
		}
	}
}
