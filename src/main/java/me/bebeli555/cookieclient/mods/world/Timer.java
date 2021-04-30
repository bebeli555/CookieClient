package me.bebeli555.cookieclient.mods.world;

import me.bebeli555.cookieclient.Mod;
import me.bebeli555.cookieclient.events.player.PlayerUpdateEvent;
import me.bebeli555.cookieclient.gui.Group;
import me.bebeli555.cookieclient.gui.Mode;
import me.bebeli555.cookieclient.gui.Setting;
import me.bebeli555.cookieclient.hud.components.LagNotifierComponent;
import me.zero.alpine.listener.EventHandler;
import me.zero.alpine.listener.Listener;

public class Timer extends Mod {
	public static Setting tps = new Setting(Mode.DOUBLE, "TPS", 20, "The clientsided tps", "Lower = slower. Higher = faster");
	public static Setting sync = new Setting(Mode.BOOLEAN, "Sync", false, "Sync client-sided tps with server tps");
	
	public Timer() {
		super(Group.WORLD, "Timer", "Changes the client-sided TPS");
	}
	
	@Override
	public void onDisabled() {
		mc.timer.tickLength = 50;
	}
	
    @EventHandler
    private Listener<PlayerUpdateEvent> onPlayerUpdate = new Listener<>(event -> {
    	if (sync.booleanValue()) {
    		mc.timer.tickLength = (int)(1000 / LagNotifierComponent.getTps());
    	} else {
    		mc.timer.tickLength = (int)(1000 / tps.doubleValue());
    	}
    });
}
