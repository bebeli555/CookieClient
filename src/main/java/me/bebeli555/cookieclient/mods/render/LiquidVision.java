package me.bebeli555.cookieclient.mods.render;

import me.bebeli555.cookieclient.Mod;
import me.bebeli555.cookieclient.events.bus.EventHandler;
import me.bebeli555.cookieclient.events.bus.Listener;
import me.bebeli555.cookieclient.events.render.SetupFogEvent;
import me.bebeli555.cookieclient.gui.Group;

public class LiquidVision extends Mod {
	public LiquidVision() {
		super(Group.RENDER, "LiquidVision", "Allows you to see clearly in water/lava", "(Stops fog from being rendered)");
	}
	
    @EventHandler
    private Listener<SetupFogEvent> setupFog = new Listener<>(event -> {
    	if (mc.player.ticksExisted < 20) {
    		return;
    	}
    	
    	event.cancel();
    });
}
