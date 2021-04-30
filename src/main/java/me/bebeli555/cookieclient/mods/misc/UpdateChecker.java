package me.bebeli555.cookieclient.mods.misc;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.URL;

import com.mojang.realmsclient.gui.ChatFormatting;

import me.bebeli555.cookieclient.Mod;
import me.bebeli555.cookieclient.gui.Group;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent.ClientTickEvent;

//Checks the UpdateChecker.txt file in the main repository for new updates
public class UpdateChecker extends Mod {
	public static UpdateChecker instance;
	public static String link = "https://raw.githubusercontent.com/bebeli555/CookieClient/main/UpdateChecker.txt";
	private String newVersion = null;
	
	public UpdateChecker() {
		super(Group.MISC, "UpdateChecker", "Checks if theres a new version of CookieClient in startup", "And notifies you in chat if you are running an outdated version");
		this.defaultOn = true;
		this.defaultHidden = true;
		this.autoSubscribe = false;
		instance = this;
	}
	
	@Override
	public void onEnabled() {
		new Thread() {
			public void run() {
				try {
			        URL url = new URL(link);
			        BufferedReader in = new BufferedReader(new InputStreamReader(url.openStream()));

			        String inputLine;
			        while ((inputLine = in.readLine()) != null) {
			            if (inputLine.contains("1.12.2")) {
			    	        newVersion = inputLine.replace("1.12.2=", "");
			    	        MinecraftForge.EVENT_BUS.register(instance);
			    	        break;
			            }
			        }
			        
			        in.close();
				} catch (Exception ignored) {

				}
			}
		}.start();
	}
	
	@SubscribeEvent
	public void onTick(ClientTickEvent e) {
		if (mc.player != null) {
			if (newVersion != null && !newVersion.equals(VERSION)) {
				sendMessage("New version of " + ChatFormatting.GREEN + NAME + ChatFormatting.WHITE + " is available! Download it from " + ChatFormatting.GREEN + "https://github.com/bebeli555/CookieClient "
				+ ChatFormatting.WHITE + "New Version: " + ChatFormatting.GREEN + newVersion + ChatFormatting.WHITE + " Your version: " + ChatFormatting.GREEN + VERSION, false);
			}
			
	        MinecraftForge.EVENT_BUS.unregister(instance);
		}
	}
}