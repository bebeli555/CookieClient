package me.bebeli555.cookieclient.mods.render;

import me.bebeli555.cookieclient.Mod;
import me.bebeli555.cookieclient.gui.Group;

public class FullBright extends Mod {
	public static float oldGamma;
	
	public FullBright() {
		super(Group.RENDER, "FullBright", "Full brightness");
	}
	
	@Override
	public void onEnabled() {
		oldGamma = mc.gameSettings.gammaSetting;
		mc.gameSettings.gammaSetting = 69f;
	}
	
	@Override
	public void onDisabled() {
		if (mc.gameSettings.gammaSetting == oldGamma) {
			mc.gameSettings.gammaSetting = 1f;
		} else {
			mc.gameSettings.gammaSetting = oldGamma;	
		}
	}
}
