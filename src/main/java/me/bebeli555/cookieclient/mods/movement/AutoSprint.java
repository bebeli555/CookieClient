package me.bebeli555.cookieclient.mods.movement;

import me.bebeli555.cookieclient.Mod;
import me.bebeli555.cookieclient.events.player.PlayerUpdateMoveStatePostEvent;
import me.bebeli555.cookieclient.gui.Group;
import me.zero.alpine.listener.EventHandler;
import me.zero.alpine.listener.Listener;

public class AutoSprint extends Mod {
	public AutoSprint() {
		super(Group.MOVEMENT, "AutoSprint", "Makes you allways sprint when walking");
	}
	
    @EventHandler
    private Listener<PlayerUpdateMoveStatePostEvent> onUpdate = new Listener<>(event -> {
    	mc.player.setSprinting(true);
    });
}
