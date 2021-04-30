package me.bebeli555.cookieclient.hud;

import me.bebeli555.cookieclient.Mod;
import me.bebeli555.cookieclient.gui.Group;
import me.bebeli555.cookieclient.hud.components.ArmorComponent;
import me.bebeli555.cookieclient.hud.components.ArrayListComponent;
import me.bebeli555.cookieclient.hud.components.CoordsComponent;
import me.bebeli555.cookieclient.hud.components.DirectionComponent;
import me.bebeli555.cookieclient.hud.components.InfoComponent;
import me.bebeli555.cookieclient.hud.components.LagNotifierComponent;
import me.bebeli555.cookieclient.hud.components.WatermarkComponent;
import net.minecraftforge.common.MinecraftForge;

public class HudEditor extends Mod {
	public static HudEditor module;
	public static HudEditorGui hudEditorGui = new HudEditorGui();
	
	public HudEditor() {
		super(Group.GUI, "HudEditor", "Change the position of the HUD components");
		initComponents();
		module = this;
	}
	
	@Override
	public void onEnabled() {
		MinecraftForge.EVENT_BUS.register(hudEditorGui);
		mc.displayGuiScreen(hudEditorGui);
	}
	
	@Override
	public void onDisabled() {
		MinecraftForge.EVENT_BUS.unregister(hudEditorGui);
		HudSettings.saveSettings();
	}
	
	public static void initComponents() {
		new ArrayListComponent();
		new WatermarkComponent();
		new ArmorComponent();
		new CoordsComponent();
		new DirectionComponent();
		new InfoComponent();
		new LagNotifierComponent();
	}
}