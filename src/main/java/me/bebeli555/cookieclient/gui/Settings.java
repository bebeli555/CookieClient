package me.bebeli555.cookieclient.gui;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStreamWriter;
import java.util.Scanner;

import me.bebeli555.cookieclient.Mod;
import me.bebeli555.cookieclient.utils.Timer;

public class Settings extends Mod {
	public static Timer lastSave = new Timer();
	public static String path = mc.gameDir.getPath() + "/CookieClient";
	public static File settings = new File(path + "/Settings.txt");
	
	/**
	 * Saves the settings from the GUI to a file located at .minecraft/CookieClient/Settings.txt
	 */
	public static void saveSettings() {
		if (!lastSave.hasPassed(1000)) {
			return;
		}
		lastSave.reset();
		
		new Thread() {
			public void run() {
				try {
					settings.delete();
					settings.createNewFile();
					
					BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(settings)));
					for (GuiNode node : GuiNode.all) {
						bw.write(node.id + "=");
						if (!node.isTypeable && node.modes.size() == 0) {
							bw.write("" + node.toggled);
						} else {
							bw.write("" + node.stringValue);
						}
						
						bw.newLine();
					}
					
					//Also save the group coordinates
					for (Group group : Group.values()) {
						bw.write("Group88" + group.name + "=" + group.x + "," + group.y);
						bw.newLine();
					}
					
					bw.close();
				} catch (Exception e) {
					System.out.println(NAME + " - Error saving settings");
					e.printStackTrace();
				}
			}
		}.start();
	}
	
	/**
	 * Loads the settings saved in the file
	 */
	public static void loadSettings() {
		try {
			if (!settings.exists()) {
				return;
			}
			
			Scanner scanner = new Scanner(settings);
			while(scanner.hasNextLine()) {
				String split[] = scanner.nextLine().split("=");
				String id = split[0];
				String value;
				try {
					value = split[1];
				} catch (IndexOutOfBoundsException e) {
					value = "";
				}
				
				//If setting is group then do this trick. What? Trick. Ok! Haha get tricked. Shut up
				if (id.startsWith("Group88")) {
					String name = id.replace("Group88", "");
					int x = Integer.parseInt(value.split(",")[0]);
					int y = Integer.parseInt(value.split(",")[1]);
					
					for (Group group : Group.values()) {
						if (group.name.equals(name)) {
							group.x = x;
							group.y = y;
						}
					}
					
					continue;
				}
				
				GuiNode node = getGuiNodeFromId(id);
				if (node == null) {
					continue;
				}
				
				if (isBoolean(value)) {
					node.toggled = Boolean.parseBoolean(value);
					try {
						Setting.getSettingWithId(node.id).setValue(node.toggled);
					} catch (Exception ignored) {
						
					}
					
					for (Mod module : modules) {
						if (module.name.equals(id)) {
							if (node.toggled) {
								module.enable();
							}
						}
					}
				} else {
					node.stringValue = value;
					try {
						node.setSetting();
					} catch (NullPointerException e) {
						//Ingore exception bcs its probably caused by the keybind which doesnt have a setting only the node
					}
				}
			}
			scanner.close();
		} catch (Exception e) {
			System.out.println(NAME + " - Error loading settings");
			e.printStackTrace();
		}
	}
	
	/**
	 * Checks if the setting with given ID is toggled
	 */
	public static boolean isOn(String id) {
		return getGuiNodeFromId(id).toggled;
	}
	
	/**
	 * @return String value of GuiNode with given ID
	 */
	public static String getStringValue(String id) {
		return getGuiNodeFromId(id).stringValue;
	}
	
	/**
	 * String value of this setting turned into integer
	 */
	public static int getIntValue(String id) {
		return Integer.parseInt(getGuiNodeFromId(id).stringValue);
	}
	
	/**
	 * String value of this setting turned into double
	 */
	public static double getDoubleValue(String id) {
		return Double.parseDouble(getGuiNodeFromId(id).stringValue);
	}
	
	/**
	 * Get GuiNode with given ID
	 */
	public static GuiNode getGuiNodeFromId(String id) {
		for (GuiNode node : GuiNode.all) {
			if (node.id.equals(id)) {
				return node;
			}
		}
		
		return null;
	}
	
	//Checks if string is boolean
	public static boolean isBoolean(String string) {
		return "true".equals(string) || "false".equals(string);
	}
}
