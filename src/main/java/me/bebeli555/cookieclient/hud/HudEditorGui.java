package me.bebeli555.cookieclient.hud;

import org.lwjgl.input.Mouse;

import me.bebeli555.cookieclient.Mod;
import me.bebeli555.cookieclient.gui.Gui;
import me.bebeli555.cookieclient.hud.HudComponent.HudCorner;
import me.bebeli555.cookieclient.hud.HudComponent.HudPoint;
import me.bebeli555.cookieclient.hud.components.ArmorComponent;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent.ClientTickEvent;

public class HudEditorGui extends GuiScreen {
	public static Minecraft mc = Mod.mc;
	public static int lastMouseX, lastMouseY;
	public static HudComponent dragging;
	public static int EXTEND = 3;
	
	@Override
	public void initGui() {
		super.initGui();
	}

	@Override
	public boolean doesGuiPauseGame() {
		return false;
	}
	
	@Override
	public void drawScreen(int mouseX, int mouseY, float partialTicks) {
		GlStateManager.pushMatrix();
		double guiScale = Gui.getGuiScale(1);
		GlStateManager.scale(guiScale, guiScale, guiScale);
		
		this.setGuiSize(Integer.MAX_VALUE, Integer.MAX_VALUE);
		this.drawDefaultBackground();
		
		mouseX = (int)(mouseX / guiScale);
		mouseY = (int)(mouseY / guiScale);
		
		//Render rectangle below the components
		for (HudComponent component : HudComponent.components) {
			if (component.shouldRender()) {
				for (HudPoint point : component.renderedPoints) {
					drawRect((int)point.x - EXTEND, (int)point.y - EXTEND, (int)point.x2 + EXTEND, (int)point.y2 + EXTEND, 0xFF000000);
				}
			}
		}
		
		//Render components. Usually its done at Rendering class but its done here if the module is on
		//So the rectangles dont overlay the text
		for (HudComponent component : HudComponent.components) {
			if (component.shouldRender()) {
				component.onRender(partialTicks);
			}
		}
		
		//Drag components
		if (dragging != null) {
			if (Mouse.isButtonDown(0)) {
				if (dragging.name.equals("Armor")) {
					double scale = ArmorComponent.getScale();
					dragging.xAdd += (mouseX - lastMouseX) / scale;
					dragging.yAdd += (mouseY - lastMouseY) / scale;
				} else {
					dragging.xAdd += mouseX - lastMouseX;
					dragging.yAdd += mouseY - lastMouseY;
				}
			} else {
				dragging = null;
			}
		}
		
		lastMouseX = mouseX;
		lastMouseY = mouseY;
		GlStateManager.popMatrix();
	}
	
	@Override
	protected void mouseClicked(int x, int y, int button) {
		HudComponent component = null;
		for (HudComponent component2 : HudComponent.components) {
			if (component2.shouldRender()) {
				for (HudPoint point : component2.renderedPoints) {
					if (point.x2 + EXTEND > lastMouseX && point.x - EXTEND < lastMouseX && point.y2 + EXTEND > lastMouseY && point.y - EXTEND < lastMouseY) {
						component = component2;
						break;
					}
				}
			}
		}
		
		if (component == null) {
			return;
		}
		
		if (button == 0) {
			dragging = component;
		} else if (button == 1) {
			if (component.corner == HudCorner.BOTTOM_RIGHT) {
				component.corner = HudCorner.BOTTOM_LEFT;
			} else if (component.corner == HudCorner.BOTTOM_LEFT) {
				component.corner = HudCorner.TOP_LEFT;
			} else if (component.corner == HudCorner.TOP_LEFT) {
				component.corner = HudCorner.TOP_RIGHT;
			} else if (component.corner == HudCorner.TOP_RIGHT) {
				component.corner = HudCorner.BOTTOM_RIGHT;
			}
			
			component.xAdd = 0;
			component.yAdd = 0;
		} else if (button == 2) {
			component.xAdd = 0;
			component.yAdd = 0;
		}
	}
	
	@SubscribeEvent
	public void onTick(ClientTickEvent event) {
		if (!(mc.currentScreen instanceof HudEditorGui)) {
			HudEditor.module.disable();
		}
	}
}
