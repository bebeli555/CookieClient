package me.bebeli555.cookieclient.hud;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStreamWriter;
import java.util.Scanner;

import me.bebeli555.cookieclient.Mod;
import me.bebeli555.cookieclient.gui.Settings;
import me.bebeli555.cookieclient.hud.HudComponent.HudCorner;

public class HudSettings extends Mod {
	public static String path = Settings.path + "/Hud.txt";
	
	//Saves the current corner and position of components
	//Name,CornerID,X,Y
	public static void saveSettings() {
		try {
			File file = new File(path);
			file.delete();
			file.createNewFile();
			
			BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(file)));
			for (HudComponent component : HudComponent.components) {
				bw.write(component.name + "," + component.corner.id + "," + component.xAdd + "," + component.yAdd);
				bw.newLine();
			}
			
			bw.close();
		} catch (Exception e) {
			System.out.println(NAME + " - Error saving HUD settings");
			e.printStackTrace();
		}
	}
	
	//Loads the corners and positions of the components and sets them
	public static void loadSettings() {
		try {
			File file = new File(path);
			if (file.exists()) {
				Scanner s = new Scanner(file);
				while (s.hasNextLine()) {
					String line = s.nextLine();
					 
					if (!line.isEmpty()) {
						String[] split = line.split(",");
						for (HudComponent component : HudComponent.components) {
							if (component.name.equals(split[0])) {
								component.corner = HudCorner.getCornerFromId(Integer.parseInt(split[1]));
								component.xAdd = Integer.parseInt(split[2]);
								component.yAdd = Integer.parseInt(split[3]);
								break;
							}
						}
					}
				}
				
				s.close();
			}
		} catch (Exception e) {
			System.out.println(NAME + " - Error loading HUD settings");
			e.printStackTrace();
		}
	}
}
