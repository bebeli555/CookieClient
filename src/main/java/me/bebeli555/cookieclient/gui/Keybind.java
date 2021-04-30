package me.bebeli555.cookieclient.gui;

import java.util.ArrayList;

import org.lwjgl.input.Keyboard;

import me.bebeli555.cookieclient.Commands;
import me.bebeli555.cookieclient.Mod;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.InputEvent.KeyInputEvent;

public class Keybind extends Mod {
	public static ArrayList<KeybindValue> keybinds = new ArrayList<KeybindValue>();

	@SubscribeEvent
	public void onKeyPress(KeyInputEvent e) {
		if (!Keyboard.isKeyDown(Keyboard.getEventKey())) {
			String keyName = Keyboard.getKeyName(Keyboard.getEventKey());
			for (KeybindValue keybind : keybinds) {
				if (keybind.name.equals(keyName)) {
					keybind.clicked = false;
				}
			}
			
			return;
		}
		
		String keyName = Keyboard.getKeyName(Keyboard.getEventKey());
		for (KeybindValue keybind : keybinds) {
			if (keybind.name.equals(keyName)) {
				if (keybind.clicked) {
					continue;
				}
				
				if (keybind.id.equals("Keybind")) {
					Commands.openGui = true;
					MinecraftForge.EVENT_BUS.register(Gui.gui);
				} else {
					GuiNode node = Settings.getGuiNodeFromId(keybind.id.replace("Keybind", ""));
					node.click();
					keybind.clicked = true;
				}
			}
		}
	}
	
	//Sets the hashmap of keybinds so checking them will take less resources than looping all the nodes
	public static void setKeybinds() {
		keybinds.clear();
		
		for (GuiNode node : GuiNode.all) {
			if (node.isKeybind) {
				if (!node.stringValue.isEmpty()) {
					keybinds.add(new KeybindValue(node.stringValue, node.id));
				}
			}
		}
	}
	
	public static class KeybindValue {
		public String name;
		public String id;
		public boolean clicked;
		
		public KeybindValue(String name, String id) {
			this.name = name;
			this.id = id;
		}
	}
}
