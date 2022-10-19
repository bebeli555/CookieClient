package me.bebeli555.cookieclient.mods.movement;

import me.bebeli555.cookieclient.Mod;
import me.bebeli555.cookieclient.events.bus.EventHandler;
import me.bebeli555.cookieclient.events.bus.Listener;
import me.bebeli555.cookieclient.events.player.PlayerUpdateMoveStatePostEvent;
import me.bebeli555.cookieclient.gui.Group;
import me.bebeli555.cookieclient.gui.Mode;
import me.bebeli555.cookieclient.gui.Setting;

public class AutoSprint extends Mod {
	public static Setting holdingSprint = new Setting(Mode.BOOLEAN, "HoldingSprint", false, "Only force sprints when holding the sprint button", "This is useful if having issues with sprinting in vanilla", "As it sets sprinting state every game update");

	public AutoSprint() {
		super(Group.MOVEMENT, "AutoSprint", "Makes you allways sprint when walking");
	}
	
    @EventHandler
    private Listener<PlayerUpdateMoveStatePostEvent> onUpdate = new Listener<>(event -> {
		if (holdingSprint.booleanValue() && mc.gameSettings.keyBindSprint.isKeyDown()) {
			mc.player.setSprinting(true);
		} else if (!holdingSprint.booleanValue()) {
			mc.player.setSprinting(true);
		}
    });
}
