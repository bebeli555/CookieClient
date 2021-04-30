package me.bebeli555.cookieclient.hud.components;

import me.bebeli555.cookieclient.gui.GuiSettings;
import me.bebeli555.cookieclient.hud.HudComponent;
import net.minecraft.client.gui.GuiChat;

public class CoordsComponent extends HudComponent {
	public CoordsComponent() {
		super(HudCorner.BOTTOM_LEFT, "Coords");
	}
	
	@Override
 	public void onRender(float partialTicks) {
		super.onRender(partialTicks);
		float amount = 0;
		if (mc.currentScreen instanceof GuiChat) {
			amount = 14;
		}
		
		String text = g + "XYZ " + w + decimal(mc.renderViewEntity.posX, 1) + g + ", " + w + decimal(mc.renderViewEntity.posY, 1) + g + ", " + w + decimal(mc.renderViewEntity.posZ, 1) + " ";
		if (GuiSettings.netherCoords.booleanValue()) {
			if (mc.player.dimension == 0) {
				text += g + "[" + w + decimal(mc.renderViewEntity.posX / 8, 1) + g + ", " + w + decimal(mc.renderViewEntity.posZ / 8, 1) + g + "]";
			} else {
				text += g + "[" + w + decimal(mc.renderViewEntity.posX * 8, 1) + g + ", " + w + decimal(mc.renderViewEntity.posZ * 8, 1) + g + "]";
			}
		}
		
		drawString(text, 0, -amount, 0xFF000000, true);
	}
	
	@Override
	public boolean shouldRender() {
		return GuiSettings.coords.booleanValue();
	}
}
