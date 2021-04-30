package me.bebeli555.cookieclient.mods.misc;

import me.bebeli555.cookieclient.Mod;
import me.bebeli555.cookieclient.gui.Group;
import me.bebeli555.cookieclient.gui.Mode;
import me.bebeli555.cookieclient.gui.Setting;

public class AutoReconnect extends Mod {
	public static AutoReconnect module;
	public static Setting delay = new Setting(Mode.DOUBLE, "Delay", 3, "Delay to wait in seconds");

	public AutoReconnect() {
		super(Group.MISC, "AutoReconnect", "Automatically reconnects to the server");
		module = this;
	}
}