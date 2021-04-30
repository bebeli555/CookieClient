package me.bebeli555.cookieclient.hud.components;

import me.bebeli555.cookieclient.gui.GuiSettings;
import me.bebeli555.cookieclient.hud.HudComponent;
import net.minecraft.client.gui.GuiChat;
import net.minecraft.util.EnumFacing;

public class DirectionComponent extends HudComponent {
	public DirectionComponent() {
		super(HudCorner.BOTTOM_LEFT, "Direction");
	}
	
	@Override
 	public void onRender(float partialTicks) {
		super.onRender(partialTicks);
		int amount = 0;
		if (corner == HudCorner.BOTTOM_LEFT || corner == HudCorner.BOTTOM_RIGHT) {
			amount = 10;
			if (mc.currentScreen instanceof GuiChat) {
				amount += 14;
			}
		}
		
		EnumFacing dir = mc.renderViewEntity.getHorizontalFacing();
		String text = w + "North " + g + "[" + w + "-Z" + g + "]";
		
		if (dir == EnumFacing.EAST) {
			text = w + "East " + g + "[" + w + "+X" + g + "]";
		} else if (dir == EnumFacing.SOUTH) {
			text = w + "South " + g + "[" + w + "+Z" + g + "]";
		} else if (dir == EnumFacing.WEST) {
			text = w + "West " + g + "[" + w + "-X" + g + "]";
		}
		
		drawString(text, 0, -amount, 0xFF000000, true);
	}
	
	@Override
	public boolean shouldRender() {
		return GuiSettings.direction.booleanValue();
	}
}
