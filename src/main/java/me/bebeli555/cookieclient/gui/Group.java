package me.bebeli555.cookieclient.gui;

public enum Group {
	BOTS("Bots", 108,310, "The bots"),
	GUI("GUI", 218,285, "Stuff about the GUI and HUD"),
	GAMES("Games", 218,227, "Fun games to play"),
	COMBAT("Combat", 107,66, "Combat modules for pvp and stuff"),
	EXPLOITS("Exploits", 217,66, "Useful exploit modules"),
	MISC("Misc", 326,66, "Other modules"),
	MOVEMENT("Movement", 439,65, "Movement modules"),
	RENDER("Render", 553,65, "Client-sided render modules"),
	WORLD("World", 664,65, "Some other modules that like do something");
	
	public String name;
	public int x, y;
	public String[] description;
	Group(String name, int x, int y, String... description) {
		this.name = name;
		this.x = x;
		this.y = y;
		this.description = description;
	}
}
