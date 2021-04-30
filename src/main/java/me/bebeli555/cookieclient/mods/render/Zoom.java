package me.bebeli555.cookieclient.mods.render;

import org.lwjgl.input.Keyboard;

import me.bebeli555.cookieclient.Mod;
import me.bebeli555.cookieclient.gui.Group;
import me.bebeli555.cookieclient.gui.Keybind;
import me.bebeli555.cookieclient.gui.Keybind.KeybindValue;
import me.bebeli555.cookieclient.gui.Mode;
import me.bebeli555.cookieclient.gui.Setting;
import me.bebeli555.cookieclient.gui.Setting.ValueChangedListener;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.InputEvent.KeyInputEvent;

public class Zoom extends Mod {
	private float oldFov;
	
	public static Setting fov = new Setting(Mode.INTEGER, "FOV", 30, "What to change the FOV to when toggled");
	
	public Zoom() {
		super(Group.RENDER, "Zoom", "Zoom like optifine");
	}
	
	@Override
	public void onEnabled() {
		oldFov = mc.gameSettings.fovSetting;
		update();
	}
	
	@Override
	public void onDisabled() {
		mc.gameSettings.fovSetting = oldFov;
		mc.gameSettings.smoothCamera = false;
		mc.entityRenderer.renderHand = true;
	}
	
	@Override
	public void onPostInit() {
		fov.addValueChangedListener(new ValueChangedListener(this, true) {
			public void valueChanged() {
				update();
			}
		});
	}
	
	public void update() {		
		mc.gameSettings.smoothCamera = true;
		mc.gameSettings.fovSetting = fov.intValue();
		mc.entityRenderer.renderHand = false;
	}
	
	@SubscribeEvent
	public void onKeyPress(KeyInputEvent e) {
		for (KeybindValue keybind : Keybind.keybinds) {
			if (keybind.id.replace("Keybind", "").equals(this.name)) {
				if (!Keyboard.isKeyDown(Keyboard.getKeyIndex(keybind.name))) {
					disable();
					return;
				}
			}
		}
	}
}
