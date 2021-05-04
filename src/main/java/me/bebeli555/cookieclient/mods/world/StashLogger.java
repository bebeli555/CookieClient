package me.bebeli555.cookieclient.mods.world;

import java.awt.Image;
import java.awt.SystemTray;
import java.awt.Toolkit;
import java.awt.TrayIcon;
import java.awt.TrayIcon.MessageType;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStreamWriter;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;

import me.bebeli555.cookieclient.Mod;
import me.bebeli555.cookieclient.gui.Group;
import me.bebeli555.cookieclient.gui.Mode;
import me.bebeli555.cookieclient.gui.Setting;
import me.bebeli555.cookieclient.gui.Settings;
import me.bebeli555.cookieclient.utils.Timer;
import net.minecraft.init.SoundEvents;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.tileentity.TileEntityChest;
import net.minecraft.tileentity.TileEntityDispenser;
import net.minecraft.tileentity.TileEntityDropper;
import net.minecraft.tileentity.TileEntityShulkerBox;
import net.minecraft.util.SoundCategory;
import net.minecraft.world.chunk.Chunk;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent.ClientTickEvent;

public class StashLogger extends Mod {
	private Timer timer = new Timer();
	private HashMap<Chunk, ArrayList<TileEntity>> map = new HashMap<>();
	private ArrayList<Chunk> loggedChunks = new ArrayList<Chunk>();
	
	public static Setting amount = new Setting(Mode.INTEGER, "Amount", 15, "If the chunk has this many allowed container blocks", "It will log it");
	public static Setting chests = new Setting(Mode.BOOLEAN, "Chests", true, "Checks for chests also trapped chests");
	public static Setting droppers = new Setting(Mode.BOOLEAN, "Droppers", true, "Checks for droppers");
	public static Setting dispensers = new Setting(Mode.BOOLEAN, "Dispensers", true, "Checks for dispensers");
	public static Setting shulkers = new Setting(Mode.BOOLEAN, "Shulkers", true, "Checks for shulker boxes");
	public static Setting hoppers = new Setting(Mode.BOOLEAN, "Hoppers", true, "Checks for hoppers");
	public static Setting chatMessage = new Setting(Mode.BOOLEAN, "ChatMessage", true, "Also sends a message in ingame chat when it found a chunk");
	public static Setting sound = new Setting(Mode.BOOLEAN, "Sound", true, "Also playes xp sound when it found a chunk");
	public static Setting windowsAlert = new Setting(Mode.BOOLEAN, "WindowsAlert", false, "Sends a windows alert message when it found a chunk");
	
	public StashLogger() {
		super(Group.WORLD, "StashLogger", "Logs chunks that have many container blocks in them", "It logs them to .minecraft/CookieClient/StashLogger.txt");
	}
	
	@Override
	public void onDisabled() {
		loggedChunks.clear();
	}
	
	@SubscribeEvent
	public void onTick(ClientTickEvent e) {
		if (timer.hasPassed(500) && mc.player != null && mc.world != null && mc.world.loadedEntityList != null) {
			timer.reset();
			
			map.clear();
			for (TileEntity tileEntity : mc.world.loadedTileEntityList) {
				if (isValid(tileEntity)) {
					Chunk chunk = mc.world.getChunk(tileEntity.getPos());
					
					ArrayList<TileEntity> list = new ArrayList<TileEntity>();
					if (map.containsKey(chunk)) list = map.get(chunk);
					list.add(tileEntity);
					map.put(chunk, list);
				}
			}
			
			for (Chunk chunk : map.keySet()) {
				if (map.get(chunk).size() >= amount.intValue()) {
					if (!loggedChunks.contains(chunk)) {
						loggedChunks.add(chunk);
						log(chunk, map.get(chunk));
					}
				}
			}
		}
	}
	
	public void log(Chunk chunk, ArrayList<TileEntity> list) {
		//Someone put amount to 0?
		if (list.size() <= 0) {
			return;
		}
		
		int x = list.get(0).getPos().getX();
		int z = list.get(0).getPos().getZ();
		
		//Send chat message
		if (chatMessage.booleanValue()) {
			sendMessage("Found chunk with " + list.size() + " container blocks at X: " + x + " Z: " + z, false);
		}
		
		//Play sound
		if (sound.booleanValue()) {
			mc.world.playSound(getPlayerPos(), SoundEvents.ENTITY_PLAYER_LEVELUP, SoundCategory.AMBIENT, 100.0f, 18.0F, true);
		}
		
		//Send windows alert
		if (windowsAlert.booleanValue()) {
			sendWindowsAlert("Found a stash chunk!");
		}
		
		//Log it to the file
		new Thread() {
			public void run() {
				try {
					File file = new File(Settings.path + "/StashLogger.txt");
					if (!file.exists()) file.createNewFile();
					
					BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(file, true)));
					DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyy/MM/dd HH:mm");  
					LocalDateTime now = LocalDateTime.now();  
					   
					bw.write("X: " + x + " Z: " + z + " Found " + list.size() + " container blocks - " + dtf.format(now));
					bw.newLine();
					bw.close();
				} catch (Exception e) {
					System.out.println(NAME + " - Error logging chunk. StashLogger");
					e.printStackTrace();
				}
			}
		}.start();
	}
	
	public static void sendWindowsAlert(String message) {
		try {
	        SystemTray tray = SystemTray.getSystemTray();
	        Image image = Toolkit.getDefaultToolkit().createImage("icon.png");
	        TrayIcon trayIcon = new TrayIcon(image, "Tray Demo");
	        trayIcon.setImageAutoSize(true);
	        trayIcon.setToolTip("System tray icon demo");
	        tray.add(trayIcon);

	        trayIcon.displayMessage(NAME, message, MessageType.INFO);
		} catch (Exception ignored) {
			
		}
	}
	
	public boolean isValid(TileEntity tileEntity) {
		return chests.booleanValue() && tileEntity instanceof TileEntityChest
			|| droppers.booleanValue() && tileEntity instanceof TileEntityDropper
			|| dispensers.booleanValue() && tileEntity instanceof TileEntityDispenser
			|| shulkers.booleanValue() && tileEntity instanceof TileEntityShulkerBox
			|| hoppers.booleanValue() && tileEntity instanceof TileEntityDropper;
	}
}
