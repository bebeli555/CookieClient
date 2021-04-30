package me.bebeli555.cookieclient.hud.components;

import com.mojang.realmsclient.gui.ChatFormatting;

import me.bebeli555.cookieclient.Mod;
import me.bebeli555.cookieclient.gui.GuiSettings;
import me.bebeli555.cookieclient.hud.HudComponent;

public class WatermarkComponent extends HudComponent {
	public WatermarkComponent() {
		super(HudCorner.TOP_LEFT, "Watermark");
	}

	@Override
	public void onRender(float partialTicks) {
		super.onRender(partialTicks);
		drawString(ChatFormatting.BLUE + Mod.NAME + w + " v" + Mod.VERSION, 0, 0, -1, true);
	}
	
	@Override
	public boolean shouldRender() {
		return GuiSettings.waterMark.booleanValue();
	}
}
