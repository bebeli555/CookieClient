package me.bebeli555.cookieclient.hud.components;

import java.util.ArrayList;

import me.bebeli555.cookieclient.Mod;
import me.bebeli555.cookieclient.gui.GuiSettings;
import me.bebeli555.cookieclient.hud.HudComponent;
import me.bebeli555.cookieclient.utils.RainbowUtil;

public class ArrayListComponent extends HudComponent {
	private static RainbowUtil rainbow = new RainbowUtil();
	public static int lastArraylistSize;
	public static ArrayList<Mod> arraylist = new ArrayList<Mod>();
	
	public ArrayListComponent() {
		super(HudCorner.BOTTOM_RIGHT, "ArrayList");
	}

	@Override
	public void onRender(float partialTicks) {
		super.onRender(partialTicks);

		//Sort so longest name will be at bottom
		if (lastArraylistSize != arraylist.size()) {
			ArrayList<Mod> temp = new ArrayList<Mod>();
			
			while (!arraylist.isEmpty()) {
				Mod best = null;
				int longest = Integer.MIN_VALUE;
				ArrayList<Mod> temp2 = new ArrayList<Mod>();
				temp2.addAll(arraylist);
				for (Mod module : temp2) {
					String name = module.name;
					if (module.getRenderNumber() != -1) {
						name += " [" + module.getRenderNumber() + "]";
					}
					
					int lenght = mc.fontRenderer.getStringWidth(name);
					if (lenght > longest) {
						best = module;
						longest = lenght;
					}
				}
				
				temp.add(best);
				arraylist.remove(best);
			}
			
			arraylist = temp;
		}
		
		int i = 0;
		rainbow.setSpeed(GuiSettings.arrayListRainbowSpeed.intValue());
		rainbow.onUpdate();
		
		int amount = 0;
		ArrayList<Mod> temp = new ArrayList<Mod>();
		temp.addAll(arraylist);
		for (Mod module : temp) {
			if (module.isOn() && !module.isHidden()) {
				String text = module.name;
				if (module.getRenderNumber() != -1) {
					text += " " + g + "[" + w + module.getRenderNumber() + g + "]";
				}
				
				if (!GuiSettings.arrayListRainbowStatic.booleanValue()) {
	                i += 20;
	                if (i >= 355) {
	                    i = 0;
	                }
				}
				
				int color = 0xFF34eb71;
				if (GuiSettings.arrayListColorMode.stringValue().equals("Rainbow")) {
					color = rainbow.getRainbowColorAt(i);
				}
				
				boolean shadow = GuiSettings.arrayListShadow.booleanValue();
	
				if (corner == HudCorner.BOTTOM_RIGHT || corner == HudCorner.BOTTOM_LEFT) {
					drawString(text, 0, -(amount * 10), color, shadow);
				} else {
					drawString(text, 0, amount * 10, color, shadow);
				}

				amount++;
			}
		}
		
		lastArraylistSize = arraylist.size();
	}
	
	@Override
	public boolean shouldRender() {
		return GuiSettings.arrayList.booleanValue();
	}
}
