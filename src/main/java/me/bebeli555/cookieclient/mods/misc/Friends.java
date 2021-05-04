package me.bebeli555.cookieclient.mods.misc;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStreamWriter;
import java.util.ArrayList;
import java.util.Scanner;

import com.mojang.realmsclient.gui.ChatFormatting;

import me.bebeli555.cookieclient.Mod;
import me.bebeli555.cookieclient.gui.Group;
import me.bebeli555.cookieclient.gui.Mode;
import me.bebeli555.cookieclient.gui.Setting;
import me.bebeli555.cookieclient.gui.Settings;
import me.bebeli555.cookieclient.utils.PlayerUtil;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

public class Friends extends Mod {
	public static boolean toggled;
 	public static ArrayList<String> friends = new ArrayList<String>();
 	public static File file = new File(Settings.path + "/Friends.txt");
 	
 	public static Setting message = new Setting(Mode.BOOLEAN, "Message", false, "/msg's the player that he has", "Been added/removed on the client");
 	
	public Friends() {
		super(Group.MISC, "Friends", "Makes the friend system work", "Disabling this will disable all friend features");
		this.defaultOn = true;
		this.defaultHidden = true;
	}
	
	@Override
	public void onEnabled() {
		toggled = true;

		for (EntityPlayer player : PlayerUtil.getAll()) {
			player.refreshDisplayName();
		}
	}
	
	@Override
	public void onDisabled() {
		toggled = false;

		for (EntityPlayer player : PlayerUtil.getAll()) {
			player.refreshDisplayName();
		}
	}
	
	@SubscribeEvent
	public void renderName(PlayerEvent.NameFormat event) {
		if (event.getEntityPlayer() != mc.player && Friends.isFriend(event.getEntityPlayer())) {
			event.setDisplayname(ChatFormatting.AQUA + event.getDisplayname());
		}
	}
	
	//Loads the friends from the file
	public static void loadFriends() {
		try {
			if (!file.exists()) {
				file.createNewFile();
				return;
			}
			
			Scanner s = new Scanner(file);
			while(s.hasNextLine()) {
				String line = s.nextLine();
				
				if (!line.isEmpty()) {
					friends.add(line);
				}
			}
			
			s.close();
		} catch (Exception e) {
			System.out.println(NAME + " - Error loading friends");
			e.printStackTrace();
		}
	}
	
	//Saves the friends to the file
	public static void saveFriends() {
		try {
			file.delete();
			file.createNewFile();
			
			BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(file)));
			for (String name : friends) {
				bw.write(name);
				bw.newLine();
			}
			
			bw.close();
		} catch (Exception e) {
			System.out.println(NAME + " - Error saving friends");
			e.printStackTrace();
		}
	}
	
	//Add friend and update file
	public static void addFriend(String name) {
		if (message.booleanValue()) {
			mc.player.sendChatMessage("/msg " + name + " You have been added to friends in " + NAME);
		}
		
		friends.add(name);
		saveFriends();
		 
		for (EntityPlayer player : PlayerUtil.getAll()) {
			player.refreshDisplayName();
		}
	}
	
	//Remove friend and update file
	public static void removeFriend(String name) {
		if (message.booleanValue()) {
			mc.player.sendChatMessage("/msg " + name + " You have been removed from friends in " + NAME);
		}
		
		friends.remove(name);
		saveFriends();
		
		for (EntityPlayer player : PlayerUtil.getAll()) {
			player.refreshDisplayName();
		}
	}
	
	public static boolean isFriend(String name) {
		if (!toggled) return false;
		return friends.contains(name);
	}
	
	public static boolean isFriend(Entity entity) {
		if (!toggled) return false;
		return friends.contains(entity.getName());
	}
	
	public static class OldName {
		public String player, name;
		
		public OldName(String player, String name) {
			this.player = player;
			this.name = name;
		}
	}
}
