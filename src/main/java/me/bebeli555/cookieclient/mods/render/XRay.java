package me.bebeli555.cookieclient.mods.render;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStreamWriter;
import java.util.ArrayList;
import java.util.Scanner;

import me.bebeli555.cookieclient.Mod;
import me.bebeli555.cookieclient.gui.Group;
import me.bebeli555.cookieclient.gui.Settings;
import net.minecraft.block.Block;
import net.minecraft.init.Blocks;

public class XRay extends Mod {
	private static boolean loaded;
	public static boolean isToggled;
	public static ArrayList<Block> blocks = new ArrayList<Block>();
	 
	public XRay() {
		super(Group.RENDER, "XRay", "Only renders the important blocks", "You can add a block with \"xray add id\"", "And delete block with \"xray remove id\"", "You need to send that as a command in chat");
	}
	
	@Override
	public void onEnabled() {
		isToggled = true;
		if (loaded) {
			mc.renderGlobal.loadRenderers();
			return;
		}
		
		loaded = true;
		try {
			File file = new File(Settings.path + "/XRay.txt");
			if (file.exists()) {
				blocks.clear();
				
				Scanner s = new Scanner(file);
				while(s.hasNextLine()) {
					String line = s.nextLine();
					if (!line.isEmpty()) {
						blocks.add(Block.getBlockById(Integer.parseInt(line)));
					}
				}
				
				s.close();
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		//Set the default blocks if no blocks were loaded from file
		if (blocks.isEmpty()) {
			Block[] defaultBlocks = {Blocks.EMERALD_ORE, Blocks.GOLD_ORE, Blocks.IRON_ORE, Blocks.COAL_ORE, Blocks.LAPIS_ORE, Blocks.DIAMOND_ORE, Blocks.REDSTONE_ORE, Blocks.LIT_REDSTONE_ORE,
			Blocks.TNT, Blocks.EMERALD_ORE, Blocks.FURNACE, Blocks.LIT_FURNACE, Blocks.DIAMOND_BLOCK, Blocks.IRON_BLOCK, Blocks.GOLD_BLOCK, Blocks.EMERALD_BLOCK, Blocks.QUARTZ_ORE,
			Blocks.BEACON, Blocks.MOB_SPAWNER, Blocks.CHEST, Blocks.TRAPPED_CHEST, Blocks.ENDER_CHEST, Blocks.DROPPER, Blocks.DISPENSER, Blocks.PORTAL, Blocks.ENCHANTING_TABLE};
			for (Block block : defaultBlocks) {
				blocks.add(block);
			}
		}
		
		mc.renderGlobal.loadRenderers();
	}
	
	@Override
	public void onDisabled() {
		isToggled = false;
		mc.renderGlobal.loadRenderers();
	}
	
	//Adds the block to the list and the file
	public static void addBlock(int id) {
		blocks.add(Block.getBlockById(id));
		updateFile();
		mc.renderGlobal.loadRenderers();
	}
	
	//Removes the block from the list and the file
	public static void removeBlock(int id) {
		blocks.remove(Block.getBlockById(id));
		updateFile();
		mc.renderGlobal.loadRenderers();
	}
	
	//Updates the file
	public static void updateFile() {
		try {
			File file = new File(Settings.path + "/XRay.txt");
			file.delete();
			file.createNewFile();
			 
			BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(file)));
			for (Block block : blocks) {
				bw.write("" + Block.getIdFromBlock(block));
				bw.newLine();
			}
			
			bw.close();
		} catch (Exception e) {
			System.out.println(NAME + " - Error updating XRay file");
			e.printStackTrace();
		}
	}
	
	public static boolean shouldRender(Block block) {
		if (block == Blocks.AIR) {
			return true;
		}
		
		for (Block block2 : blocks) {
			if (block2 == block) {
				return true;
			}
		}
		
		return false;
	}
}
