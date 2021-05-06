package me.bebeli555.cookieclient.gui;

import java.awt.Dimension;
import java.awt.Point;
import java.awt.Toolkit;
import java.awt.datatransfer.DataFlavor;
import java.net.URI;
import java.util.ArrayList;
import java.util.Arrays;

import org.lwjgl.input.Keyboard;
import org.lwjgl.input.Mouse;

import com.mojang.realmsclient.gui.ChatFormatting;

import me.bebeli555.cookieclient.Commands;
import me.bebeli555.cookieclient.Mod;
import me.bebeli555.cookieclient.mods.games.Snake;
import me.bebeli555.cookieclient.mods.games.tetris.Tetris;
import me.bebeli555.cookieclient.utils.Timer;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiConfirmOpenLink;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraftforge.client.event.GuiScreenEvent;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent.ClientTickEvent;
import net.minecraftforge.fml.relauncher.ReflectionHelper;

public class Gui extends GuiScreen {
	public static ArrayList<Runnable> renderList = new ArrayList<Runnable>();
	public static Timer timer = new Timer();
	public static Minecraft mc = Minecraft.getMinecraft();
	public static ArrayList<GuiClick> visibleNodes = new ArrayList<GuiClick>();
	public static boolean isOpen;
	public static GuiClick selected, description;
	public static Group dragging;
	public static int lastMouseX, lastMouseY, oldScale;
	public static boolean pasting;
	public static char pasteChar;
	public static Gui gui = new Gui();
	public static ArrayList<Point> groupCoords = new ArrayList<Point>();
	
	@Override
	public void initGui() {
		super.initGui();
	}

	@Override
	public boolean doesGuiPauseGame() {
		return false;
	}
	
	@Override
	public void drawScreen(int mouseX2, int mouseY2, float partialTicks) {		
		//Scale the gui to match the resolution and the gui scale.
		Dimension resolution = Toolkit.getDefaultToolkit().getScreenSize();
		GlStateManager.pushMatrix();
		float guiScale = (float)((float)mc.displayWidth / resolution.getWidth());
		this.setGuiSize(Integer.MAX_VALUE, Integer.MAX_VALUE);
		
		guiScale = (float)getGuiScale(guiScale) + (float)GuiSettings.scale.doubleValue();
		final int mouseX = (int)(mouseX2 / guiScale);
		final int mouseY = (int)(mouseY2 / guiScale);
		
		GlStateManager.scale(guiScale, guiScale, guiScale);
		
		//Render the old things because theres no need to recalculate all the things like 140 times a second
		if (!timer.hasPassed(25)) {
			for (Runnable render : renderList) {
				render.run();
			}
			
			renderGames(mouseX, mouseY, partialTicks);	
			GlStateManager.popMatrix();
			return;
		}
		
		renderList.clear();
		timer.reset();
		
		//Draw background
		drawRect(0, 0, 2000, 2000, 0x99000000);
		drawRect(0, 0, 2000, 2000, 0x45000000);
		
		visibleNodes.clear();
		description = null;
		
		//Draw changelog
		if (GuiSettings.changelog.booleanValue()) {
			renderList.add(() -> ChangeLog.drawChangelog(mouseX, mouseY));
		}
		
		//Drag group
		if (dragging != null) {
			if (Mouse.isButtonDown(1) || Mouse.isButtonDown(0)) {
				if (mouseY > 10) {
					dragging.x += mouseX - lastMouseX;
					dragging.y += mouseY - lastMouseY;
					updateGuiGroups();
				}
			} else {
				dragging = null;
			}
		}
		
		//Top left info text
		if (GuiSettings.infoBox.booleanValue()) {
			renderList.add(() -> GlStateManager.pushMatrix());
			renderList.add(() -> GlStateManager.scale(0.9, 0.9, 0.9));
			int infoWidth = mc.fontRenderer.getStringWidth(ChatFormatting.RED + "Discord: " + ChatFormatting.GREEN + Mod.DISCORD + 2);
			drawRect(0, 0, infoWidth, 44, 0xFF000000);
			drawRect(0, 43, infoWidth, 44, 0x99d303fc);
			drawRect(infoWidth, 0, infoWidth + 1, 44, 0x99d303fc);
			drawStringWithShadow(ChatFormatting.RED + Mod.NAME + ChatFormatting.GREEN + " v" + Mod.VERSION, 2, 2, 0xFF000000);
			drawStringWithShadow(ChatFormatting.RED + "Made by: " + ChatFormatting.GREEN + "bebeli555", 2, 12, 0xFF000000);
			drawStringWithShadow(ChatFormatting.RED + "Discord: " + ChatFormatting.GREEN + Mod.DISCORD, 2, 22, 0xFF000000);
			drawStringWithShadow(ChatFormatting.RED + "Github: " + ChatFormatting.GREEN + "bebeli555/CookieClient", 2, 32, 0xFF000000);
			renderList.add(() -> GlStateManager.popMatrix());
		}
		
		//Draw all visible nodes
		for (Group group : Group.values()) {
			//All visible nodes
			int nodes = 0;
			for (GuiNode node : GuiNode.all) {
				if (node.isVisible && node.group == group) {
					nodes++;
				}
			}
			
			int count = 0;
			for (GuiNode node : GuiNode.all) {
				if (node.group == group && node.isVisible) {
					drawGuiNode(mouseX, mouseY, node, count, nodes);
					count++;
				}
			}
			
			//Draw the Group thing
			int x = group.x;
			int x2 = group.x + GuiSettings.width.intValue();
			int y = group.y - GuiSettings.height.intValue();
			int y2 = group.y;
			GuiNode guiNode = new GuiNode(true);
			guiNode.description = group.description;
			GuiClick guiClick = new GuiClick(x, y, x2, y2, guiNode);
			
			drawRect(x, y, x2, y2, GuiSettings.groupBackground.intValue());
			drawBorder(true, true, true, true, GuiSettings.borderColor.intValue(), guiClick, GuiSettings.borderSize.doubleValue());
			renderList.add(() -> GlStateManager.pushMatrix());
			float scale = (float)GuiSettings.groupScale.doubleValue();
			scale(scale);
			drawStringWithShadow(group.name, (((x2 / scale) - (x / scale)) / 2) + (x / scale) - ((mc.fontRenderer.getStringWidth(group.name)) / 2), (y + (5 / scale)) / scale, GuiSettings.groupTextColor.intValue());
			renderList.add(() -> GlStateManager.popMatrix());
			
			if (x < mouseX && x2 > mouseX && y < mouseY && y2 > mouseY) {
				description = guiClick;
				
				//Drag the group if holding mouse
				if (Mouse.isButtonDown(1) || Mouse.isButtonDown(0)) {
					if (dragging == null) {
						dragging = group;
					}
				}
			}
		}
		
		//Draw descriptions so they will overlay everything else
		for (GuiClick g : new GuiClick[]{selected, description}) {
			if (g != null) {
				String[] description = g.guiNode.description;
				
				if (g.guiNode.modes.size() != 0) {
					Object[] array = null;
					try {
						array = g.guiNode.modeDescriptions.get(g.guiNode.modes.indexOf(g.guiNode.stringValue)).toArray();
					} catch (Exception e) {
						//This is probably caused because the saved setting had a mode value that is no longer existing so it will just set it to the default
						//Probably because a new version update that modified the mode names
						g.guiNode.stringValue = g.guiNode.modes.get(0);
						break;
					}
					description = (String[])Arrays.copyOf(array, array.length, String[].class);
					description = Mod.addToArray(description, ChatFormatting.GREEN + "Click to switch modes");
				}
				
		 		for (GuiNode node : g.guiNode.parentedNodes) {
					if (!node.modeName.isEmpty() && !node.modeName.equals(g.guiNode.stringValue)) {
						continue;
					}
					
					if (description == null || description.length == 0) {
						description = new String[]{ChatFormatting.GREEN + "Right click to extend"};
					} else {
						description = Mod.addToArray(description, ChatFormatting.GREEN + "Right click to extend");
					}
					break;
		 		}
				
				if (selected != null && selected.equals(g)) {
					if (g.guiNode.onlyNumbers) {
						description = new String[]{ChatFormatting.GOLD + "Type numbers in your keyboard to set this"};
					} else if (g.guiNode.isKeybind) {
						description = new String[]{ChatFormatting.GOLD + "Click a key to set the keybind"};
					} else {
						description = new String[]{ChatFormatting.GOLD + "Type with your keyboard to set this"};
					}
				}
				
				if (description != null) {
					int longestWidth = 0;
					boolean left = false;
					for (String s : description) {
						int width = (int)mc.fontRenderer.getStringWidth(s);
						width += Math.abs(g.x - g.x2) + 6;
						
						if (width > longestWidth) {
							longestWidth = width;
							
							if (g.x + width > mc.displayWidth / 2) {
								left = true;
							}
						}
					}
					
					for (int i = 0; i < description.length; i++) {
						int y = (int)(g.y + 6) + (i * 10);
						int width = (int)mc.fontRenderer.getStringWidth(description[i]);
						
						if (left) {
							drawRect(g.x2 - longestWidth, y - 2, g.x - 2, y + 10, 0xFF000000);
							drawStringWithShadow(description[i], (g.x2) - longestWidth + 2, ((g.y + 6) + (i * 10)), 0xffff);
						} else {
							drawRect(g.x2 + 8, y - 2, g.x2 + width + 12, y + 10, 0xFF000000);
							drawStringWithShadow(description[i], (g.x2) + 10, ((g.y + 6) + (i * 10)), 0xffff);	
						}
					}
				}
				
				break;
			}
		}
		
		for (Runnable render : renderList) {
			render.run();
		}
		
		renderGames(mouseX, mouseY, partialTicks);
		lastMouseX = mouseX;
		lastMouseY = mouseY;
		GlStateManager.popMatrix();
	}
	
	//This calculates the coordinates for the node and draws everything for the node.
	public void drawGuiNode(int mouseX, int mouseY, GuiNode node, int aboveNodes, int nodes) {
		int extendMoveMultiplier = node.getAllParents().size() * GuiSettings.extendMove.intValue();
		GuiClick g = new GuiClick(node.group.x + extendMoveMultiplier, 
				node.group.y + (aboveNodes * GuiSettings.height.intValue()), 
				node.group.x + GuiSettings.width.intValue() + extendMoveMultiplier, 
				node.group.y + GuiSettings.height.intValue() + (aboveNodes * GuiSettings.height.intValue()), node);
		
		//Draw box
		drawRect(g.x, g.y, g.x2, g.y2, GuiSettings.backgroundColor.intValue());

		//Draw text if its too big for the width then lower the scale on it
		String text = g.guiNode.name;
		if (g.guiNode.isTypeable) {
			ChatFormatting color = ChatFormatting.AQUA;
			if (g.guiNode.isKeybind) {
				color = ChatFormatting.LIGHT_PURPLE;
			}
			
			if (g.guiNode.stringValue.isEmpty()) {
				g.guiNode.stringValue = "";
				text = color + g.guiNode.name + ": " + ChatFormatting.RED + "NONE";
			} else {
				text = color + g.guiNode.name + ": " + ChatFormatting.GOLD + g.guiNode.stringValue;
			}
		} else if (g.guiNode.modes.size() != 0) {
			text = ChatFormatting.GREEN + g.guiNode.name + ": " + ChatFormatting.WHITE + g.guiNode.stringValue;
		}
		
		float scale = 1F;
		if (mc.fontRenderer.getStringWidth(text) > g.x2 - g.x) {
			renderList.add(() -> GlStateManager.pushMatrix());
			
			int width = (int)mc.fontRenderer.getStringWidth(text);
			while (width * scale > g.x2 - g.x) {
				scale -= 0.03;
			}
			
			scale(scale);
		}
		
		//This is some serious math
		drawStringWithShadow(text, (((g.x2 / scale) - (g.x / scale)) / 2) + (g.x / scale) - ((mc.fontRenderer.getStringWidth(text)) / 2), (int)(g.y + (g.y2 - g.y) / 3) / scale, g.guiNode.getTextColor());
		
		if (scale != 1F) {
			renderList.add(() -> GlStateManager.popMatrix());
		}
		
		//Draw border
		drawBorder(true, true, true, true, GuiSettings.borderColor.intValue(), g, GuiSettings.borderSize.doubleValue());
		
		//also calculate the thing to draw above it so it will match the other border if its more x than it is
		if (!visibleNodes.isEmpty()) {
			GuiClick last = visibleNodes.get(visibleNodes.size() - 1);
			
			if (last.guiNode.group == node.group) {
				//Last is more on left
	 			if (last.x < g.x) {
	 				drawRectDouble(last.x, g.y, last.x + GuiSettings.borderSize.doubleValue(), g.y + GuiSettings.borderSize.doubleValue(), GuiSettings.borderColor.intValue());
	 				drawRectDouble(last.x2, g.y, last.x2 + GuiSettings.extendMove.intValue(), g.y + GuiSettings.borderSize.doubleValue(), GuiSettings.borderColor.intValue());
	 			} 
	 			
	 			//Last is more on right
	 			else if (last.x > g.x) {
	 				drawRectDouble(last.x, g.y, last.x - GuiSettings.extendMove.intValue(), g.y + GuiSettings.borderSize.doubleValue(), GuiSettings.borderColor.intValue());
	 				drawRectDouble(last.x2, g.y, last.x2 - GuiSettings.extendMove.intValue(), g.y + GuiSettings.borderSize.doubleValue(), GuiSettings.borderColor.intValue());
	 			}
			}
		}
		
		//Set description
		if (g.x < mouseX && g.x2 > mouseX && g.y < mouseY && g.y2 > mouseY) {
			description = g;
		}
		
		//Add GuiClick to visibleNodes list
		visibleNodes.add(g);
	}
	
	@Override
	protected void mouseClicked(int x, int y, int button) {		
		//Just use the lastMouse positions saved by the rendering loop because these are different
		x = lastMouseX;
		y = lastMouseY;
		
		for (Mod module : Mod.modules) {
			if (module.isOn()) {
				if (module.onGuiClick(x, y, button)) {
					return;
				}
			}
		}
		
		if (selected != null) {
			selected.guiNode.setSetting();
		}
		selected = null;
		
		//Open discord link if the thing is clicked
		if (GuiSettings.infoBox.booleanValue() && 0 < x && 150 > x && 0 < y && 34 > y) {
			try {
				URI link = new URI("https://" + Mod.DISCORD);
				ReflectionHelper.setPrivateValue(GuiScreen.class, this, link, "clickedLinkURI", "field_175286_t");
				mc.displayGuiScreen(new GuiConfirmOpenLink(this, link.toString(), 31102009, true));
			} catch (Exception e) {
				e.printStackTrace();
			}
			
			return;
		}
		
		for (GuiClick guiClick : visibleNodes) {
			if (guiClick.x < x && guiClick.x2 > x && guiClick.y < y && guiClick.y2 > y) {
				if (button == 1) {
					if (!guiClick.guiNode.parentedNodes.isEmpty()) {
						int index = 0;
						if (!guiClick.guiNode.modes.isEmpty()) {
							for (int i = 0; i < guiClick.guiNode.parentedNodes.size(); i++) {
								GuiNode node = guiClick.guiNode.parentedNodes.get(i);
								if (!node.modeName.isEmpty() && node.modeName.equals(guiClick.guiNode.stringValue)) {
									index = i;
									break;
								}
							}
						}
						
						guiClick.guiNode.extend(!guiClick.guiNode.parentedNodes.get(index).isVisible);
					}
				} else {
					if (guiClick.guiNode.isTypeable) {
						selected = guiClick;
					}
					
					guiClick.guiNode.click();
				}
			}
		}
	}
	
	@SubscribeEvent
	public void onKeyPress(GuiScreenEvent.KeyboardInputEvent.Post e) {
		if (selected != null && Keyboard.isKeyDown(Keyboard.getEventKey())) {
			char key = Keyboard.getEventCharacter();
			if (pasting) {
				key = pasteChar;
			}
			
			//Paste
			if (!pasting && Keyboard.isKeyDown(Keyboard.KEY_V) && Keyboard.isKeyDown(Keyboard.KEY_LCONTROL)) {
				pasting = true;

				try {
					String clipboard = (String)Toolkit.getDefaultToolkit().getSystemClipboard().getData(DataFlavor.stringFlavor); 
					
					for (char c : clipboard.toCharArray()) {
						pasteChar = c;
						onKeyPress(null);
					}
				} catch (Exception ignored) {
					
				}
				
				pasting = false;
				return;
			}
			
			//Stuff for keybinds
			if (selected.guiNode.isKeybind) {
				if (Keyboard.getEventKey() != Keyboard.KEY_BACK) {
					selected.guiNode.stringValue = Keyboard.getKeyName(Keyboard.getEventKey());
				} else {
					selected.guiNode.stringValue = "";
				}
				
				Keybind.setKeybinds();
				selected.guiNode.notifyKeyListeners();
				return;
			}
			
			//Ignore if the key is shift as the user is probably trying to write uppercased letter
			if (Keyboard.getEventKey() == Keyboard.KEY_LSHIFT || Keyboard.getEventKey() == Keyboard.KEY_RSHIFT) {
				return;
			}
			
			//Backspace one key and if already empty then set to default value
			if (Keyboard.isKeyDown(Keyboard.KEY_BACK) || Keyboard.isKeyDown(Keyboard.KEY_DELETE)) {
				if (!selected.guiNode.stringValue.isEmpty()) {
					selected.guiNode.stringValue = selected.guiNode.stringValue.substring(0, selected.guiNode.stringValue.length() - 1);
				} else {
					selected.guiNode.stringValue = selected.guiNode.defaultValue;
				}
				
				selected.guiNode.notifyKeyListeners();
				return;
			}
			
			char[] acceptedKeys;
			if (selected.guiNode.onlyNumbers) {
				if (selected.guiNode.acceptDoubleValues) {
					acceptedKeys = new char[]{'0','1','2','3','4','5','6','7','8','9','-','.'};
				} else {
					acceptedKeys = new char[]{'0','1','2','3','4','5','6','7','8','9','-'};
				}
			} else {
				if ((int)key != 0) {
					selected.guiNode.stringValue += key;
					selected.guiNode.notifyKeyListeners();	
				}
				return;
			}
			
			//Check if key is in the acceptedKeys list and then put it to the stringValue
			for (char accept : acceptedKeys) {
				if (accept == key) {
					if (key == '-') {
						selected.guiNode.stringValue = "";
					}
					
					if ((int)key != 0) {
						selected.guiNode.stringValue += key;
						selected.guiNode.notifyKeyListeners();	
					}
					return;
				}
 			}
		}
		
		for (Mod module : Mod.modules) {
			if (module.isOn()) {
				module.onGuiKeyPress(e);
			}
		}
	}
	
	@SubscribeEvent
	public void onMouseEvent(GuiScreenEvent.MouseInputEvent event) {
		int wheel = Mouse.getDWheel();
		int amount = GuiSettings.scrollAmount.intValue() * Math.abs(wheel) / 120;
		
		//Up
		if (wheel > 0) {
			int multiplier = 1;
			if (wheel > 120) {
				multiplier++;
			}
			
			if (GuiSettings.changelog.booleanValue() && ChangeLog.isMouseOver(lastMouseX, lastMouseY)) {
				ChangeLog.scroll(false, multiplier);
				return;
			}
			
			for (Group group : Group.values()) {
				group.y += amount * multiplier;
			}
		}
		
		//Down
		else if (wheel < 0) {
			int multiplier = 1;
			if (wheel < -120) {
				multiplier++;
			}

			if (GuiSettings.changelog.booleanValue() && ChangeLog.isMouseOver(lastMouseX, lastMouseY)) {
				ChangeLog.scroll(true, multiplier);
				return;
			}
			
			for (Group group : Group.values()) {
				group.y += -amount * multiplier;
			}
		}
	}
	
	@SubscribeEvent
	public void onTick(ClientTickEvent e) {
		//Have to open the GUI this way because if you try to open it in the chat event it wont work and if you try to put it to a new thread the mouse will be invisible.
		if (Commands.openGui) {
			//If gui scale is auto or large in windowed mode then force normal scale
			oldScale = mc.gameSettings.guiScale;
			if (!mc.isFullScreen()) {
				if (mc.gameSettings.guiScale == 3 || mc.gameSettings.guiScale == 0) {
					mc.gameSettings.guiScale = 2;
				}
			}
			
			mc.displayGuiScreen(new Gui());
			Commands.openGui = false;
			isOpen = true;
			updateGuiGroups();
			return;
		}
		
		//Save settings when GUI is closed
		if (isOpen && mc.currentScreen == null) {
			if (!GuiSettings.scrollSave.booleanValue()) {
				for (int i = 0; i < Group.values().length; i++) {
					Group.values()[i].x = groupCoords.get(i).x;
					Group.values()[i].y = groupCoords.get(i).y;
				}
			}
			
			Settings.saveSettings();
			mc.gameSettings.guiScale = oldScale;
			isOpen = false;
			selected = null;
			pasting = false;
			dragging = null;
			description = null;
			Tetris.instance.disable();
			Snake.instance.disable();
			MinecraftForge.EVENT_BUS.unregister(gui);
		}
	}
	
	public static double getGuiScale(float start) {
		ScaledResolution scaledResolution = new ScaledResolution(mc);
		if (scaledResolution.getScaleFactor() == 4) {
			start += -0.5 * start;
		} else if (scaledResolution.getScaleFactor() == 1) {
			start += 1 * start;
		} else if (scaledResolution.getScaleFactor() == 3) {
			start += -0.3 * start;
		}
		
		return start;
	}
	
	public static void drawBorder(boolean right, boolean left, boolean up, boolean down, int color, GuiClick n, double borderSize) {
		if (up) drawRectDouble(n.x, n.y, n.x2, n.y + borderSize, color);
		if (down) drawRectDouble(n.x, n.y2, n.x2, n.y2 + borderSize, color);
		if (left) drawRectDouble(n.x, n.y, n.x + borderSize, n.y2, color);
		if (right) drawRectDouble(n.x2, n.y, n.x2 + borderSize, n.y2 + borderSize, color);
	}
	
	public static void drawRectDouble(double left, double top, double right, double bottom, int color) {
        if (left < right) {
            double i = left;
            left = right;
            right = i;
        }

        if (top < bottom) {
        	double j = top;
            top = bottom;
            bottom = j;
        }

        float f3 = (float)(color >> 24 & 255) / 255.0F;
        float f = (float)(color >> 16 & 255) / 255.0F;
        float f1 = (float)(color >> 8 & 255) / 255.0F;
        float f2 = (float)(color & 255) / 255.0F;
        Tessellator tessellator = Tessellator.getInstance();
        BufferBuilder bufferbuilder = tessellator.getBuffer();
        GlStateManager.enableBlend();
        GlStateManager.disableTexture2D();
        GlStateManager.tryBlendFuncSeparate(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA, GlStateManager.SourceFactor.ONE, GlStateManager.DestFactor.ZERO);
        GlStateManager.color(f, f1, f2, f3);
        bufferbuilder.begin(7, DefaultVertexFormats.POSITION);
        bufferbuilder.pos(left, bottom, 0.0D).endVertex();
        bufferbuilder.pos(right, bottom, 0.0D).endVertex();
        bufferbuilder.pos(right, top, 0.0D).endVertex();
        bufferbuilder.pos(left, top, 0.0D).endVertex();
        tessellator.draw();
        GlStateManager.enableTexture2D();
        GlStateManager.disableBlend();
	}
	
	//Update gui groups. So these are set when gui closes not the scrolled ones or the gui could go out of screen and confusion strikes
	public static void updateGuiGroups() {
		groupCoords.clear();
		
		for (Group group : Group.values()) {
			groupCoords.add(new Point(group.x, group.y));
		}
	}
	
	public static void drawRect(int x, int y, int x2, int y2, int color) {
		renderList.add(() -> GuiScreen.drawRect(x, y, x2, y2, color));
	}
	
	public static void drawStringWithShadow(String text, float x, float y, int color) {
		renderList.add(() -> mc.fontRenderer.drawStringWithShadow(text, x, y, color));
	}
	
	public static void scale(float scale) {
		renderList.add(() -> GlStateManager.scale(scale, scale, scale));
	}
	
	public static void renderGames(int mouseX, int mouseY, float partialTicks) {
		for (Mod module : Mod.modules) {
			if (module.isOn()) {
				module.onGuiDrawScreen(mouseX, mouseY, partialTicks);
			}
		}
	}
	
	public static class GuiClick {
		public int x, y, x2, y2;
		public GuiNode guiNode;
		
		public GuiClick(int x, int y, int x2, int y2, GuiNode guiNode) {
			this.x = x;
			this.y = y;
			this.x2 = x2;
			this.y2 = y2;
			this.guiNode = guiNode;
		}
	}
}
