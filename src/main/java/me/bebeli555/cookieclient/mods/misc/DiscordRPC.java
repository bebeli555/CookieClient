package me.bebeli555.cookieclient.mods.misc;

import club.minnced.discord.rpc.DiscordEventHandlers;
import club.minnced.discord.rpc.DiscordRichPresence;
import me.bebeli555.cookieclient.Mod;
import me.bebeli555.cookieclient.gui.Group;
import me.bebeli555.cookieclient.gui.Mode;
import me.bebeli555.cookieclient.gui.Setting;
import me.bebeli555.cookieclient.utils.PlayerUtil;

public class DiscordRPC extends Mod {
	public static DiscordRichPresence presence = new DiscordRichPresence();
	public static club.minnced.discord.rpc.DiscordRPC rpc = club.minnced.discord.rpc.DiscordRPC.INSTANCE;
	public static Thread thread;
	public static int index = 1;
	
	public static Setting topText = new Setting(Mode.TEXT, "TopText", "Username: <username>", "The top text. <username> will be turned to ur ingame username");
	public static Setting bottomText = new Setting(Mode.TEXT, "BottomText", "Playing on <server>", "The bottom text. <server> will be turned to the server ip if it has letters in it");
	
	public DiscordRPC() {
		super(Group.MISC, "DiscordRPC", "Discord rich presence");
		this.defaultOn = true;
	}
	
	@Override
	public void onEnabled() {
        DiscordEventHandlers handlers = new DiscordEventHandlers();
        rpc.Discord_Initialize("837013588396081233", handlers, true, "");
        presence.startTimestamp = System.currentTimeMillis() / 1000L;
        presence.details = topText.stringValue().replace("<username>", getUsername()) + " | v" + VERSION;
        presence.state = bottomText.stringValue().replace("<server>", getServer());
        presence.largeImageKey = "cookieclientimage";
        presence.largeImageText = "CookieClient is an utility client for anarchy servers. Github: github.com/bebeli555/CookieClient Discord: discord.gg/xSukBcyd8m";
        rpc.Discord_UpdatePresence(presence);
        
        thread = new Thread(() -> {
            while (thread != null) {
                rpc.Discord_RunCallbacks();
                presence.details = topText.stringValue().replace("<username>", getUsername())+ " | v" + VERSION;
                presence.state = bottomText.stringValue().replace("<server>", getServer());
                rpc.Discord_UpdatePresence(presence);
                Mod.sleep(2000);
            }
        }, "RPC-Callback-Handler");
        
        thread.start();
	}
	
	@Override
	public void onDisabled() {
		suspend(thread);
		thread = null;
        rpc.Discord_Shutdown();
	}
	
	//Gets the ip of the server but if the ip doesnt have any letters
	//Then returns Unknown as the user might be playing on a local server and that would risk leaking hes ip in the discordRPC
	public static String getServer() {
		if (mc.player == null) {
			return "Main menu";
		}
 		
		String ip = PlayerUtil.getServerIp();
		boolean hasLetters = false;
		
		for (char c : ip.toCharArray()) {
			if (Character.isLetter(c)) {
				hasLetters = true;
				break;
			}
		}
		
		if (!hasLetters) {
			return "Unknown";
		} else {
			return ip;
		}
	}
	
	public static String getUsername() {
		return mc.session.getUsername();
	}
}
