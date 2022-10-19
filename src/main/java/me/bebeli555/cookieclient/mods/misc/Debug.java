package me.bebeli555.cookieclient.mods.misc;

import me.bebeli555.cookieclient.Mod;
import me.bebeli555.cookieclient.gui.Group;

public class Debug extends Mod {
    public static Debug instance;

    public Debug() {
        super(Group.MISC, "Debug", "Sends debug chat messages on what some modules are doing", "Useful for development but dont use it otherwise");
        instance = this;
    }

    public static void debug(String message) {
        if (instance.isOn()) {
            Mod.sendMessage(message, false, "Debug");
        }
    }
}
