package me.bebeli555.cookieclient.gui;

import java.lang.reflect.Field;

import me.bebeli555.cookieclient.Mod;

public class SetGuiNodes {
	
	//Sets the GuiNodes by looping through the modules
	//Then checking all the variables in the class and if its an Setting variable add that as a GuiNode
	public static void setGuiNodes() {
		try {			
			for (Mod module : Mod.modules) {
				GuiNode mainNode;
				Setting s = null;
				
				if (module.name.isEmpty()) {
					mainNode = new GuiNode(true);
					mainNode.group = module.group;
				} else {
					mainNode = new GuiNode();
					mainNode.group = module.group;
					mainNode.name = module.name;
					mainNode.description = module.description;
					mainNode.group = module.group;
					mainNode.isVisible = true;
					mainNode.setId();
					s = new Setting(Mode.BOOLEAN, mainNode.name, false, mainNode.description);
				}
				
				for (Field field : module.getClass().getFields()) {
					Class<?> myType = Setting.class;
					
					if (field.getType().isAssignableFrom(myType)) {
						Setting setting = (Setting)field.get(module);
						if (!mainNode.id.isEmpty()) {
							setting.id = mainNode.id + setting.id;
						}

						GuiNode node = new GuiNode();
						node.name = setting.name;
						node.description = setting.description;
						node.defaultValue = String.valueOf(setting.defaultValue);
						node.group = mainNode.group;
						node.modeName = setting.modeName;
						
						if (setting.parent != null) {
							GuiNode p = Settings.getGuiNodeFromId(setting.parent.id);
							node.parent = p;
							p.parentedNodes.add(node);
						} else if (!mainNode.id.isEmpty()) {
							node.parent = mainNode;
							mainNode.parentedNodes.add(node);
						} else {
							node.isVisible = true;
						}
						
						if (setting.mode == Mode.TEXT) {
							node.isTypeable = true;
						} else if (setting.mode == Mode.INTEGER) {
							node.isTypeable = true;
							node.onlyNumbers = true;
						} else if (setting.mode == Mode.DOUBLE) {
							node.isTypeable = true;
							node.onlyNumbers = true;
							node.acceptDoubleValues = true; 
						} else if (setting.mode == Mode.LABEL) {
							node.isLabel = true;
						} else if (setting.modes.size() != 0) {
							node.modes = setting.modes;
							node.modeDescriptions = setting.modeDescriptions;
						}
						
						if (node.isTypeable || node.modes.size() != 0) {
							node.defaultValue = setting.stringValue();
							node.stringValue = setting.stringValue();
						} else {
							try {
								node.toggled = setting.booleanValue();
							} catch (Exception e) {
								node.defaultValue = setting.stringValue();
								node.stringValue = setting.stringValue();
							}
						}

						node.setId();
					}
				}
				
				//Keybind setting and node
				GuiNode keybind = new GuiNode();
				keybind.isVisible = true;
				if (s != null) {
					mainNode.parentedNodes.add(keybind);
					keybind.description = new String[]{"Keybind for " + mainNode.name};
					keybind.parent = mainNode;
					keybind.isVisible = false;
				} else {
					keybind.description = new String[]{"Keybind for " + module.group.name};
				}
				keybind.group = module.group;
				keybind.isTypeable = true;
				keybind.isKeybind = true;
				keybind.name = "Keybind";
				keybind.setId();
				
				//Hidden setting
				if (s == null) {
					continue;
				}
				GuiNode hidden = new GuiNode();
				hidden.isVisible = true;
				mainNode.parentedNodes.add(hidden);
				hidden.description = new String[]{"Hides the module in the HUD arraylist"};
				hidden.parent = mainNode;
				hidden.isVisible = false;
				hidden.group = module.group;
				hidden.name = "Hidden";
				hidden.setId();
				
			}
		} catch (Exception e) {
			System.out.println(Mod.NAME + " - Exception setting gui nodes");
			e.printStackTrace();
		}
	}
	
	//Set default things if no setting file
	public static void setDefaults() {
		//Set defaults if no settings file
		if (!Settings.settings.exists()) {
			for (Mod module : Mod.modules) {
				if (module.defaultOn) {
					Settings.getGuiNodeFromId(module.name).click();
				}
				
				if (module.defaultHidden) {
					Settings.getGuiNodeFromId(module.name + "Hidden").click();
				}
			}
		}
	}
}
