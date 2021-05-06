package me.bebeli555.cookieclient.rendering;

import java.awt.Color;
import java.util.ArrayList;

import me.bebeli555.cookieclient.Mod;
import me.bebeli555.cookieclient.gui.Gui;
import me.bebeli555.cookieclient.gui.GuiSettings;
import me.bebeli555.cookieclient.hud.HudComponent;
import me.bebeli555.cookieclient.hud.HudEditor;
import me.bebeli555.cookieclient.rendering.RenderBlock.BlockColor;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraftforge.client.event.RenderGameOverlayEvent;
import net.minecraftforge.client.event.RenderWorldLastEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

public class Renderer extends Mod {
	public static String[] status;
	public static ArrayList<BlockColor> rectangles = new ArrayList<BlockColor>();
    
	@SubscribeEvent
	public void renderText(RenderGameOverlayEvent.Text e) {
		if (mc.player == null) {
			return;
		}
		
		//Draw status
		if (status != null) {
			for (int i = 0; i < status.length; i++) {
				String text = status[i];
				if (text == null) {
					continue;
				}
				
				GuiScreen.drawRect(((mc.displayWidth / 4) - (mc.fontRenderer.getStringWidth(text) / 2)) - 3, (i * 10) - 1, ((mc.displayWidth / 4) + (mc.fontRenderer.getStringWidth(text) / 2)) + 3, (i + 1) * 10, 0xFF000000);
				GuiScreen.drawRect(((mc.displayWidth / 4) - (mc.fontRenderer.getStringWidth(text) / 2)) - 3, (i * 10) + 9, ((mc.displayWidth / 4) + (mc.fontRenderer.getStringWidth(text) / 2)) + 3, (i + 1) * 10, 0xFF27f5be);
				GuiScreen.drawRect(((mc.displayWidth / 4) - (mc.fontRenderer.getStringWidth(text) / 2)) - 3, (i * 10) - 1, ((mc.displayWidth / 4) - (mc.fontRenderer.getStringWidth(text) / 2)) - 2, (i + 1) * 10, 0xFF27f5be);
				GuiScreen.drawRect(((mc.displayWidth / 4) + (mc.fontRenderer.getStringWidth(text) / 2)) + 2, (i * 10) - 1, ((mc.displayWidth / 4) + (mc.fontRenderer.getStringWidth(text) / 2)) + 3, (i + 1) * 10, 0xFF27f5be);
				mc.fontRenderer.drawString(text, (mc.displayWidth / 4) - (mc.fontRenderer.getStringWidth(text) / 2), i * 10, 0xFF000000);
			}
		}
		
		//Render HUD. Design inspired by Future client
		if (!HudEditor.module.isOn()) {
			if (!GuiSettings.hud.booleanValue()) return;
			GlStateManager.pushMatrix();
			double guiScale = Gui.getGuiScale(1);
			GlStateManager.scale(guiScale, guiScale, guiScale);
			
			for (HudComponent component : HudComponent.components) {
				if (component.shouldRender()) {
					component.onRender(e.getPartialTicks());
				}
			}
			
			GlStateManager.popMatrix();
		}
	}
	
	@SubscribeEvent
	public void renderWorld(RenderWorldLastEvent event) {
		if (mc.player == null) {
			return;
		}
		
		try {
			//Render path
			BlockPos last = null;
			for (BlockPos pos : RenderPath.path) {
				if (last != null) {
					RenderUtil.drawLine(new Vec3d(pos.getX() + 0.5, pos.getY() + 0.5, pos.getZ() + 0.5), new Vec3d(last.getX() + 0.5, last.getY() + 0.5, last.getZ() + 0.5), 2, RenderPath.color);
				}
				
				last = pos;
			}
			
			//Render block bounding box
			for (BlockColor blockColor : RenderBlock.list) {
				Color c = blockColor.color;
				RenderUtil.drawBoundingBox(RenderUtil.getBB(blockColor.pos, 1), blockColor.width, c.getRed() / 255, c.getGreen() / 255, c.getBlue() / 255, 1f);
			}
			
			//Render 2d rectangles
			for (BlockColor rectangle : rectangles) {
				Color c = rectangle.color;
				RenderUtil.draw2DRec(RenderUtil.getBB(rectangle.pos, 1), rectangle.width, c.getRed() / 255, c.getGreen() / 255, c.getBlue() / 255, 1f);
			}
		} catch (Exception ignored) {
			
		}
		
		for (Mod module : Mod.modules) {
			if (module.isOn()) {
				module.onRenderWorld(event.getPartialTicks());
			}
		}
	}
}
