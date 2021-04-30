package me.bebeli555.cookieclient.mods.combat;

import java.awt.Color;
import java.util.ArrayList;

import me.bebeli555.cookieclient.Mod;
import me.bebeli555.cookieclient.gui.Group;
import me.bebeli555.cookieclient.gui.Mode;
import me.bebeli555.cookieclient.gui.Setting;
import me.bebeli555.cookieclient.rendering.RenderUtil;
import me.bebeli555.cookieclient.utils.BlockUtil;
import me.bebeli555.cookieclient.utils.InventoryUtil;
import me.bebeli555.cookieclient.utils.RotationUtil;
import net.minecraft.init.Blocks;
import net.minecraft.util.math.BlockPos;

public class HoleFiller extends Mod {
	private static Thread thread;
	private static ArrayList<BlockPos> holes = new ArrayList<BlockPos>();
	
	public static Setting radius = new Setting(Mode.INTEGER, "Radius", 4, "Radius around the player to search for holes to fill");
	public static Setting delay = new Setting(Mode.INTEGER, "Delay", 100, "Delay in ms to wait between placing the blocks");
	public static Setting render = new Setting(Mode.BOOLEAN, "Render", true, "Render the holes its trying to fill");
	
	public HoleFiller() {
		super(Group.COMBAT, "HoleFiller", "Fills nearby holes with obsidian");
	}
	
	@Override
	public void onEnabled() {
		thread = new Thread() {
			public void run() {
				while(thread != null && thread.equals(this)) {
					loop();
					
					Mod.sleep(150);
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
		if (!InventoryUtil.hasBlock(Blocks.OBSIDIAN)) {
			disable();
			sendMessage("You need obsidian", true);
			return;
		}
		
		holes = getHoles();
		ArrayList<BlockPos> temp = new ArrayList<BlockPos>();
		temp.addAll(holes);
		
		for (BlockPos pos : temp) {
			if (!InventoryUtil.hasBlock(Blocks.OBSIDIAN)) {
				return;
			}
			
			BlockUtil.placeBlock(Blocks.OBSIDIAN, pos, true);
			RotationUtil.stopRotating();
			holes.remove(pos);
			sleep(delay.intValue());
		}
	}
	
	@Override
	public void onRenderWorld(float partialTicks) {
		if (!render.booleanValue()) {
			return;
		}
		
		Color c = Color.CYAN;
		try {
			for (BlockPos pos : holes) {
				RenderUtil.drawBoundingBox(RenderUtil.getBB(pos, 1), 1, c.getRed() / 255, c.getGreen() / 255, c.getBlue() / 255, 1f);
			}
		} catch (Exception e) {
			
		}
	}
	
	public static ArrayList<BlockPos> getHoles() {
		ArrayList<BlockPos> list = new ArrayList<BlockPos>();
		
		outer: for (BlockPos pos : BlockUtil.getAll(radius.intValue())) {
			if (BlockUtil.distance(getPlayerPos(), pos) <= radius.intValue()) {
				if (getBlock(pos) != Blocks.AIR) {
					continue;
				}
				
				BlockPos[] solid = {pos.add(1, 0, 0), pos.add(-1, 0, 0), pos.add(0, 0, 1), pos.add(0, 0, -1), pos.add(0, -1, 0)};
				for (BlockPos check : solid) {
					if (!isSolid(check)) {
						continue outer;
					}
				}
				
				BlockPos[] notSolid = {pos.add(0, 1, 0), pos.add(0, 2, 0)};
				for (BlockPos check : notSolid) {
					if (isSolid(check)) {
						continue outer;
					}
				}
				
				list.add(pos);
			}
		}
		
		return list;
	}
}
