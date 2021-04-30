package me.bebeli555.cookieclient.hud;

import java.util.ArrayList;

import com.mojang.realmsclient.gui.ChatFormatting;

import me.bebeli555.cookieclient.Mod;
import me.bebeli555.cookieclient.hud.components.ArmorComponent;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.item.ItemStack;

public class HudComponent extends Mod {
	public static ArrayList<HudComponent> components = new ArrayList<HudComponent>();
	public HudCorner corner;
	public int xAdd, yAdd;
	public String name;
	public ArrayList<HudPoint> renderedPoints = new ArrayList<HudPoint>();
	public static ChatFormatting w = ChatFormatting.WHITE;
	public static ChatFormatting g = ChatFormatting.GRAY;
	
	public HudComponent(HudCorner defaultCorner, String name) {
		this.corner = defaultCorner;
		this.name = name;
		components.add(this);
	}
	
	public void onRender(float partialTicks) {
		renderedPoints.clear();
	}
	
	public boolean shouldRender() {
		return true;
	}
	
	public void renderItemAndEffectIntoGUI(ItemStack itemStack, int x, int y) {
		mc.getRenderItem().renderItemAndEffectIntoGUI(itemStack, x + xAdd, y + yAdd);
	}
	
	public void renderItemOverlays(FontRenderer fontRenderer, ItemStack itemStack, int x, int y) {
		int plus = mc.fontRenderer.FONT_HEIGHT;
		int minus = -3;
		double scale = ArmorComponent.getScale();
		renderedPoints.add(new HudPoint((x + xAdd - minus) * (float)scale, (y + yAdd - minus) * (float)scale, (x + xAdd + plus - minus) * (float)scale, (y + yAdd + plus - minus) * (float)scale));
		mc.getRenderItem().renderItemOverlays(fontRenderer, itemStack, x + xAdd, y + yAdd);
	}
	
	public void drawString(String text, float x, float y, int color, boolean shadow) {
		int displayWidth = mc.displayWidth / 2;
		int displayHeight = mc.displayHeight / 2;
		
		//The large scale fucks everything up for some reason. But every other scale works fine. So its just gonna divide it a bit
		//Sometimes stuff just makes no sense
		ScaledResolution sr = new ScaledResolution(mc);
		if (sr.getScaleFactor() == 3) {
			displayWidth /= 1.05;
			displayHeight /= 1.05;
		}
		
		if (corner == HudCorner.BOTTOM_RIGHT) {
			drawString2(text, (displayWidth - 1 - mc.fontRenderer.getStringWidth(text)) + x, (displayHeight - 9) + y, color, shadow);
		} else if (corner == HudCorner.BOTTOM_LEFT) {
			drawString2(text, x + 1, (displayHeight - 9) + y, color, shadow);
		} else if (corner == HudCorner.TOP_LEFT) {
			drawString2(text, x + 1, y + 1, color, shadow);
		} else if (corner == HudCorner.TOP_RIGHT) {
			drawString2(text, displayWidth - mc.fontRenderer.getStringWidth(text) - 1, y + 1, color, shadow);
		} else if (corner == HudCorner.NONE) {
			drawString2(text, x, y, color, shadow);
		}
	}
	
	private void drawString2(String text, float x, float y, int color, boolean shadow) {
		if (shadow) {
			mc.fontRenderer.drawStringWithShadow(text, x + xAdd, y + yAdd, color);
		} else {
			mc.fontRenderer.drawString(text, x + xAdd, y + yAdd, color, false);
		}
		
		renderedPoints.add(new HudPoint(x + xAdd, y + yAdd, x + xAdd + mc.fontRenderer.getStringWidth(text), y + yAdd + mc.fontRenderer.FONT_HEIGHT));
	}
	
	//Turn double into one decimal string
	public static String decimal(double d, int decimal) {
		String s = Double.toString(d);
		try {
			return s.substring(0, s.indexOf(".") + 1 + decimal);
		} catch (IndexOutOfBoundsException e) {
			return s.substring(0, s.indexOf(".") + 1);
		}
	}
	
	public enum HudCorner {
		TOP_RIGHT(0),
		TOP_LEFT(1),
		BOTTOM_RIGHT(2),
		BOTTOM_LEFT(3),
		NONE(4);
		
		public int id;
		HudCorner(int id) {
			this.id = id;
		}
		
		public static HudCorner getCornerFromId(int id) {
			for (HudCorner corner : HudCorner.values()) {
				if (corner.id == id) {
					return corner;
				}
			}
			
			return null;
		}
	}
	
	public class HudPoint {
		public float x, y, x2, y2;
		
		public HudPoint(float x, float y, float x2, float y2) {
			this.x = x;
			this.y = y;
			this.x2 = x2;
			this.y2 = y2;
		}
	}
}
