package me.bebeli555.cookieclient.gui;

import me.bebeli555.cookieclient.Mod;

public class GuiSettings extends Mod {
	public GuiSettings() {
		super(Group.GUI);
	}
	
	public static Setting hud = new Setting(Mode.BOOLEAN, "HUD", true, "Settings about the HUD");
		public static Setting arrayList = new Setting(hud, Mode.BOOLEAN, "ArrayList", true, "Shows the toggled modules");
		public static Setting arrayListColorMode = new Setting(arrayList, "Color", "Rainbow", new String[]{"Green"}, new String[]{"Rainbow"});
			public static Setting arrayListRainbowStatic = new Setting(arrayListColorMode, "Rainbow", Mode.BOOLEAN, "Static", false, "All modules change to same rainbow", "If false then they change differently");
			public static Setting arrayListRainbowSpeed = new Setting(arrayListColorMode, "Rainbow", Mode.INTEGER, "Speed", 9, "Speed for rainbow change");
		public static Setting arrayListShadow = new Setting(arrayList, Mode.BOOLEAN, "Shadow", true, "Draws the string with shadow");
		public static Setting waterMark = new Setting(hud, Mode.BOOLEAN, "Watermark", true, "Shows CookieClient watermark");
		public static Setting direction = new Setting(hud, Mode.BOOLEAN, "Direction", true, "Shows the direction u are looking at");
		public static Setting armor = new Setting(hud, Mode.BOOLEAN, "Armor" , true, "Shows ur armor above ur hotbar");
		public static Setting lagNotifier = new Setting(hud, Mode.BOOLEAN, "LagNotifier", true, "Shows when the server is not responding");
		public static Setting tps = new Setting(hud, Mode.BOOLEAN, "TPS", true, "Shows server tps");
		public static Setting fps = new Setting(hud, Mode.BOOLEAN, "FPS", true, "Shows ur fps");
		public static Setting speed = new Setting(hud, Mode.BOOLEAN, "Speed", true, "Shows ur speed in blocks per second");
		public static Setting ping = new Setting(hud, Mode.BOOLEAN, "Ping", true, "Shows ur ping");
		public static Setting coords = new Setting(hud, Mode.BOOLEAN, "Coords", true, "Shows ur coords");
			public static Setting netherCoords = new Setting(coords, Mode.BOOLEAN, "NetherCoords", true, "Also renders nether coords", "Or overworld if ur in nether");
		public static Setting durability = new Setting(hud, Mode.BOOLEAN, "Durability", true, "Shows durability for ur item");
		public static Setting potions = new Setting(hud, Mode.BOOLEAN, "Potions", true, "Shows potion effects and doesnt render the vanilla hud overlays");
		public static Setting infoShadow = new Setting(hud, Mode.BOOLEAN, "InfoShadow", true, "Draws the strings with shadow that are in", "The top right corner at default");
		public static Setting portalGui = new Setting(hud, Mode.BOOLEAN, "PortalGui", true, "Allows you to open guis in portals");
	public static Setting guiSettings = new Setting(Mode.LABEL, "GUI", true, "Settings about the GUI design");
		public static Setting width = new Setting(guiSettings, Mode.INTEGER, "Width", 90, "Gui node width");
		public static Setting height = new Setting(guiSettings, Mode.INTEGER, "Height", 17, "Gui node height");
		public static Setting borderColor = new Setting(guiSettings, Mode.TEXT, "Border color", "0xFF32a86d", "Color of the border in hex and with 0xAA");
		public static Setting borderSize = new Setting(guiSettings, Mode.DOUBLE, "Border size", 0, "The size of the border in the node");
		public static Setting backgroundColor = new Setting(guiSettings, Mode.TEXT, "Color", "0x36325bc2", "The background color");
		public static Setting textColor = new Setting(guiSettings, Mode.TEXT, "Text Color", "0xFF00ff00", "Text color when module is toggled on");
		public static Setting textColorOff = new Setting(guiSettings, Mode.TEXT, "Text Color Off", "0xFFff0000", "Text color when module is toggled off");
		public static Setting labelColor = new Setting(guiSettings, Mode.TEXT, "Label color", "0xFF6b6b6b", "The color of the label text which is an toggleable module");
		public static Setting extendMove = new Setting(guiSettings, Mode.INTEGER, "Extend Move", 8, "How much to move in x coordinates when parent is extended");
		public static Setting groupTextColor = new Setting(guiSettings, Mode.TEXT, "Group color", "0xFFe3a520", "The text color of the group");
		public static Setting groupScale = new Setting(guiSettings, Mode.DOUBLE, "Group scale", 1.25, "The group text scale");
		public static Setting groupBackground = new Setting(guiSettings, Mode.TEXT, "Group background", "0x3650b57c", "The group background color");
		public static Setting scrollAmount = new Setting(guiSettings, Mode.INTEGER, "ScrollAmount", 15, "How many things to scroll with one wheel scroll");
		public static Setting scale = new Setting(guiSettings, Mode.DOUBLE, "Scale", 0, "How much more to scale it than default", "Higher = bigger", "You should only change this if the default scale is messed up");
		public static Setting scrollSave = new Setting(guiSettings, Mode.BOOLEAN, "ScrollSave", false, "Doesnt reset mouse scrolled position if true");
		public static Setting infoBox = new Setting(guiSettings, Mode.BOOLEAN, "InfoBox", true, "Shows the left top info box thing");
		public static Setting changelog = new Setting(guiSettings, Mode.BOOLEAN, "Changelog", true, "Shows changelog in gui");
	public static Setting prefix = new Setting(Mode.TEXT, "Prefix", "++", "The prefix for commands");
}