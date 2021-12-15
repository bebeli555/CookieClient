package me.bebeli555.cookieclient.mods.world;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStreamWriter;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

import me.bebeli555.cookieclient.Mod;
import me.bebeli555.cookieclient.events.bus.EventHandler;
import me.bebeli555.cookieclient.events.bus.Listener;
import me.bebeli555.cookieclient.events.other.PacketEvent;
import me.bebeli555.cookieclient.gui.Group;
import me.bebeli555.cookieclient.gui.Mode;
import me.bebeli555.cookieclient.gui.Setting;
import me.bebeli555.cookieclient.gui.Settings;
import net.minecraft.client.gui.inventory.GuiEditSign;
import net.minecraft.network.play.client.CPacketUpdateSign;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent.ClientTickEvent;

public class AutoSign extends Mod {
	public static Setting setContent = new Setting(Mode.BOOLEAN, "SetContent", false, "You can enable this and the module", "Then place a sign and type the text and place it", "After that the content is saved and the module", "Will set that text to all future placed signs");
	public List<String> lines = new ArrayList<>();
	public boolean firstToggle = true;
	public long lastWarningMs;
	
	public AutoSign() {
		super(Group.WORLD, "AutoSign", "Automatically sets the content on a sign you place", "From a previously saved one");
	}
	
	@Override
	public void onEnabled() {
		//Reads the saved content from file and sets lines variable
		if (firstToggle) {
			firstToggle = false;
			
			try {
				File file = new File(Settings.path + "/AutoSign.txt");
				if (!file.exists()) {
					return;
				}
 				
				Scanner scanner = new Scanner(file);
				while (scanner.hasNextLine()) {
					String line = scanner.nextLine();
					if (line != null && !line.isEmpty()) {
						this.lines.add(line);
					}
				}
				
				scanner.close();
			} catch (Exception e) {
				e.printStackTrace();
				sendMessage("Error reading saved sign data from file", true);
			}
		}
	}
	
	@SubscribeEvent
	public void onTick(ClientTickEvent e) {
		//Closes the edit sign gui so it will send the packet
		if (mc.currentScreen instanceof GuiEditSign && !setContent.booleanValue() && !this.lines.isEmpty()) {
			mc.player.closeScreen();
		}
	}
	
	@EventHandler
	private Listener<PacketEvent> packetEvent = new Listener<>(event -> {	
		if (event.packet instanceof CPacketUpdateSign) {
			CPacketUpdateSign packet = (CPacketUpdateSign)event.packet;

			//If set content is enabled then save this signs content
			if (setContent.booleanValue()) {
				this.lines.clear();
				for (String line : packet.lines) {
					this.lines.add(line);
				}
				
				//Save it to the file also
				try {
					File file = new File(Settings.path + "/AutoSign.txt");
					file.delete();
					file.createNewFile();
					
					BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(file)));
					for (String line : this.lines) {
						bw.write(line);
						bw.newLine();
					}
					
					bw.close();
				} catch (Exception e) {
					e.printStackTrace();
					sendMessage("Error saving content to file", true);
				}
				
				Settings.getGuiNodeFromId(setContent.id).click();
				sendMessage("Sign content saved! You can now use the module", false);
			}
			
			//Warn the user if lines arent set
			else if (lines.isEmpty()) {
				if (Math.abs(lastWarningMs - System.currentTimeMillis()) > 50) {
					sendMessage("You need to set the content with setContent first!", true);
					lastWarningMs = System.currentTimeMillis();
				}
			}
			
			//Modify the packets lines with the saved ones that it will write
			else {
				String[] array = new String[this.lines.size()];
			    this.lines.toArray(array);
			    
			    packet.lines = array;
			}
		}
	});
}
