package me.bebeli555.cookieclient.mods.misc;

import java.util.ArrayList;

import me.bebeli555.cookieclient.Mod;
import me.bebeli555.cookieclient.gui.Group;
import me.bebeli555.cookieclient.gui.Mode;
import me.bebeli555.cookieclient.gui.Setting;
import net.minecraft.client.network.NetworkPlayerInfo;
import net.minecraft.network.play.client.CPacketChatMessage;

public class AutoMessager extends Mod {
	private static Thread thread;
	
	public static Setting message = new Setting(Mode.TEXT, "Message", "", "The message to send");
	public static Setting mode = new Setting(null, "Mode", "Normal", new String[]{"Normal", "Just sends the message until toggled off"}, new String[]{"Everyone", "Sends a message for every player online.", "<player> in the message will be replaced with the players name", "Example: /msg <player> hi"});
		public static Setting everyoneToggle = new Setting(mode, "Everyone", Mode.BOOLEAN, "Toggle", false, "Toggles the module off after sending the message", "To everyone online if true");
	public static Setting delay = new Setting(Mode.DOUBLE, "Delay", 3.5, "How many seconds to wait between sending the messages");
	public static Setting packet = new Setting(Mode.BOOLEAN, "Packet", false, "Sends a packet instead of using the method to send the message", "If you use this then other clients prefixes wont work usually");
	
	public AutoMessager() {
		super(Group.MISC, "AutoMessager", "Sends messages automatically");
	}
	
	@Override
	public void onEnabled() {
		thread = new Thread() {
			public void run() {
				while (thread != null && thread.equals(this)) {
					loop();
					
					Mod.sleep(50);
				}
			}
		};
		
		thread.start();
	}
	
	@Override
	public void onDisabled() {
		suspend(thread);
		thread = null;
	}
	
	public void loop() {
		if (mc.player == null) {
			return;
		}
		
		if (mode.stringValue().equals("Normal")) {
			sendMessage(message.stringValue());
			sleep((int)(delay.doubleValue() * 1000));
		} else if (mode.stringValue().equals("Everyone")) {
			ArrayList<String> players = new ArrayList<String>();
			for (NetworkPlayerInfo player : mc.player.connection.getPlayerInfoMap()) {
				try {
					players.add(player.getGameProfile().getName());
				} catch (Exception e) {
					
				}
			}
			
			for (String player : players) {
				if (mc.player == null || player.equals(mc.player.getName()) || !isOnline(player)) {
					continue;
				}
				
				sendMessage(message.stringValue().replace("<player>", player));
				sleep((int)(delay.doubleValue() * 1000));
			}
			
			if (everyoneToggle.booleanValue()) {
				disable();
			}
		}
	}
	
	public void sendMessage(String message) {
		if (packet.booleanValue()) {
			mc.player.connection.sendPacket(new CPacketChatMessage(message));
		} else {
			mc.player.sendChatMessage(message);
		}
	}
	
	public static boolean isOnline(String playerName) {
		for (NetworkPlayerInfo player : mc.player.connection.getPlayerInfoMap()) {
			try {
				if (player.getGameProfile().getName().equals(playerName)) {
					return true;
				}
			} catch (Exception e) {
				
			}
		}
		
		return false;
	}
}
