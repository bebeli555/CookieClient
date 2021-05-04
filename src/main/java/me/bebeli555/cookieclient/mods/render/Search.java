package me.bebeli555.cookieclient.mods.render;

import java.awt.Color;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStreamWriter;
import java.util.ArrayList;
import java.util.Scanner;

import me.bebeli555.cookieclient.Mod;
import me.bebeli555.cookieclient.events.bus.EventHandler;
import me.bebeli555.cookieclient.events.bus.Listener;
import me.bebeli555.cookieclient.events.other.PacketEvent;
import me.bebeli555.cookieclient.gui.Group;
import me.bebeli555.cookieclient.gui.Mode;
import me.bebeli555.cookieclient.gui.Setting;
import me.bebeli555.cookieclient.gui.Settings;
import me.bebeli555.cookieclient.rendering.RenderUtil;
import me.bebeli555.cookieclient.utils.BlockUtil;
import net.minecraft.block.Block;
import net.minecraft.init.Blocks;
import net.minecraft.network.play.server.SPacketBlockChange;
import net.minecraft.network.play.server.SPacketChunkData;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;

public class Search extends Mod {
	private static Thread thread;
	private static boolean loaded;
	private static ArrayList<Block> blocks = new ArrayList<Block>();
	private static ArrayList<BlockPos> foundBlocks = new ArrayList<BlockPos>();
	
	public static Setting esp = new Setting(Mode.BOOLEAN, "ESP", true, "Render a rectangle around the block");
		public static Setting espAlpha = new Setting(esp, Mode.DOUBLE, "Alpha", 1, "How transparent the rendered blocks are", "1 = max");
		public static Setting espWidth = new Setting(esp, Mode.DOUBLE, "Width", 1, "The width of the rendered lines");
	public static Setting tracers = new Setting(Mode.BOOLEAN, "Tracers", true, "Render tracers on the block");
		public static Setting tracersWidth = new Setting(tracers, Mode.DOUBLE, "Width", 0.5, "The width of the rendered lines");
		
	public Search() {
		super(Group.RENDER, "Search", "Search for blocks", "You can add blocks with \"search add id\"", "And remove blocks with \"search remove id\"");
	}
	
	@Override
	public void onEnabled() {
		new Thread() {
			public void run() {
				if (loaded) {
					searchForBlocks();
					return;
				}
				
				loaded = true;
				try {
					File file = new File(Settings.path + "/Search.txt");
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
					Block[] defaultBlocks = {Blocks.PORTAL, Blocks.ENDER_CHEST, Blocks.CHEST, Blocks.TRAPPED_CHEST, Blocks.DISPENSER, Blocks.DROPPER, Blocks.BLACK_SHULKER_BOX, Blocks.BLUE_SHULKER_BOX, Blocks.BROWN_SHULKER_BOX,
					Blocks.CYAN_SHULKER_BOX, Blocks.CYAN_SHULKER_BOX, Blocks.GRAY_SHULKER_BOX, Blocks.GREEN_SHULKER_BOX, Blocks.LIGHT_BLUE_SHULKER_BOX, Blocks.LIME_SHULKER_BOX, Blocks.MAGENTA_SHULKER_BOX,
					Blocks.ORANGE_SHULKER_BOX, Blocks.PINK_SHULKER_BOX, Blocks.PINK_SHULKER_BOX, Blocks.PURPLE_SHULKER_BOX, Blocks.RED_SHULKER_BOX, Blocks.SILVER_SHULKER_BOX, Blocks.WHITE_SHULKER_BOX, Blocks.WHITE_SHULKER_BOX,
					Blocks.MOB_SPAWNER, Blocks.END_PORTAL_FRAME};
					for (Block block : defaultBlocks) {
						blocks.add(block);
					}
				}
				
				searchForBlocks();
			}
		}.start();
	}
	
	@Override
	public void onDisabled() {
		thread = null;
		suspend(thread);
		foundBlocks.clear();
	}
	
	@EventHandler
	private Listener<PacketEvent> onPacket = new Listener<>(event -> {
		if (event.packet instanceof SPacketBlockChange) {
			SPacketBlockChange packet = (SPacketBlockChange)event.packet;
			
			if (isValid(packet.getBlockPosition())) {
				foundBlocks.add(packet.getBlockPosition());
			}
		} else if (event.packet instanceof SPacketChunkData) {
			SPacketChunkData packet = (SPacketChunkData)event.packet;
			int chunkX = (packet.getChunkX() * 16) - 16;
			int chunkZ = (packet.getChunkZ() * 16) - 16;

			new Thread() {
				public void run() {
					for (int x = chunkX; x < chunkX + 16; x++) {
						for (int z = chunkZ; z < chunkZ + 16; z++) {
							for (int y = 0; y < 260; y++) {
								BlockPos pos = new BlockPos(x, y, z);
								
								if (isValid(pos)) {
									foundBlocks.add(pos);
								}
							}
						}
					}
				}
			}.start();
		}
	});
	
	@Override
	public void onRenderWorld(float partialTicks) {
		try {
			ArrayList<BlockPos> remove = new ArrayList<BlockPos>();
			
			for (BlockPos pos : foundBlocks) {
				Block block = getBlock(pos);
				if (block == Blocks.AIR) {
					remove.add(pos);
					continue;
				}
				Color c = getColor(block);
				
				if (esp.booleanValue()) {
					RenderUtil.drawBoundingBox(RenderUtil.getBB(pos, 1), (float)espWidth.doubleValue(), c.getRed() / 255, c.getGreen() / 255, c.getBlue() / 255, (float)espAlpha.doubleValue());
				}
				
				if (tracers.booleanValue()) {
					Vec3d vec = new Vec3d(pos.getX() + 0.5, pos.getY() + 0.5, pos.getZ() + 0.5).subtract(mc.getRenderManager().renderPosX, mc.getRenderManager().renderPosY, mc.getRenderManager().renderPosZ);
					Tracers.renderTracer(vec, c, (float)tracersWidth.doubleValue(), partialTicks);
				}
			}
			
			foundBlocks.removeAll(remove);
		} catch (Exception e) {
			
		}
	}
	
	//Searches for blocks around the player
	//This is only called on start after that it looks at the chunks the server sends
	public static void searchForBlocks() {
		if (mc.player == null) {
			return;
		}
		
		suspend(thread);
		thread = new Thread() {
			public void run() {
				for (BlockPos pos : BlockUtil.getAllNoSort(125)) {
					if (isValid(pos)) {
						foundBlocks.add(pos);
					}
				}
			}
		};
		
		thread.start();
	}
	
	//Get color for tracer and esp for this block
	public static Color getColor(Block block) {
		if (block == Blocks.PORTAL) {
			return Color.MAGENTA;
		} else if (block == Blocks.MOB_SPAWNER) {
			return Color.BLUE;
		} else if (block == Blocks.CHEST || block == Blocks.TRAPPED_CHEST) {
			return Color.YELLOW;
		} else if (block == Blocks.ENDER_CHEST) {
			return Color.BLACK;
		} else {
			return Color.LIGHT_GRAY;
		}
	}
	
	public static boolean isValid(BlockPos pos) {
		Block block = getBlock(pos);
		if (block == Blocks.AIR) {
			return false;
		}
		
		for (Block block2 : blocks) {
			if (block == block2 && !foundBlocks.contains(pos)) {
				return true;
			}
		}
		
		return false;
	}
	
	//Adds the block to the list and the file
	public static void addBlock(int id) {
		blocks.add(Block.getBlockById(id));
		updateFile();
		searchForBlocks();
	}
	
	//Removes the block from the list and the file
	public static void removeBlock(int id) {
		blocks.remove(Block.getBlockById(id));
		updateFile();
		foundBlocks.clear();
		searchForBlocks();
	}
	
	//Updates the file
	public static void updateFile() {
		try {
			File file = new File(Settings.path + "/Search.txt");
			file.delete();
			file.createNewFile();
			 
			BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(file)));
			for (Block block : blocks) {
				bw.write("" + Block.getIdFromBlock(block));
				bw.newLine();
			}
			
			bw.close();
		} catch (Exception e) {
			System.out.println(NAME + " - Error updating Search file");
			e.printStackTrace();
		}
	}
	
	public static class SearchBlock {
		public Color color;
		public BlockPos pos;
		public static ArrayList<SearchBlock> list = new ArrayList<SearchBlock>();
		
		public SearchBlock(Color color, BlockPos pos) {
			this.color = color;
			this.pos = pos;
			list.add(this);
		}
	}
}
