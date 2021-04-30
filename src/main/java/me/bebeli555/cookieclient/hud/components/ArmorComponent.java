package me.bebeli555.cookieclient.hud.components;

import me.bebeli555.cookieclient.gui.GuiSettings;
import me.bebeli555.cookieclient.hud.HudComponent;
import me.bebeli555.cookieclient.utils.InventoryUtil;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.item.ItemStack;

public class ArmorComponent extends HudComponent {
	public ArmorComponent() {
		super(HudCorner.NONE, "Armor");
	}

	@Override
	public void onRender(float partialTicks) {
		super.onRender(partialTicks);
		ItemStack[] stacks = {InventoryUtil.getItemStack(36), InventoryUtil.getItemStack(37), InventoryUtil.getItemStack(38), InventoryUtil.getItemStack(39)};
		
		double scale = getScale();
		GlStateManager.pushMatrix();
		GlStateManager.enableBlend();
		GlStateManager.tryBlendFuncSeparate(770, 771, 1, 0);
		RenderHelper.enableGUIStandardItemLighting();
		GlStateManager.scale(scale, scale, scale);
		
		int x = (int)(((mc.displayWidth / 4) + 66 * scale) / scale);
		int y = (int)(((mc.displayHeight / 2) - 56 * scale) / scale);
		if (scale == 1.5) {
			x -= 18;
			y -= 15;
		}
		
		for (ItemStack stack : stacks) {
			renderItemAndEffectIntoGUI(stack, x, y);
			renderItemOverlays(mc.fontRenderer, stack, x, y);
			x -= 17;
		}
		
		RenderHelper.disableStandardItemLighting();
		GlStateManager.disableBlend();
		GlStateManager.color(1, 1, 1, 1);
		GlStateManager.popMatrix();
	}
	
	@Override
	public boolean shouldRender() {
		return GuiSettings.armor.booleanValue();
	}
	
	public static double getScale() {
		double scale = 1;
		ScaledResolution resolution = new ScaledResolution(mc);
		if (resolution.getScaleFactor() == 4) {
			scale = 2;
		} else if (resolution.getScaleFactor() == 1) {
			scale = 0.5;
		} else if (resolution.getScaleFactor() == 3) {
			scale = 1.5;
		}
		
		return scale;
	}
}
