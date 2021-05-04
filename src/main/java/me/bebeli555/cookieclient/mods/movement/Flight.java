package me.bebeli555.cookieclient.mods.movement;

import me.bebeli555.cookieclient.Mod;
import me.bebeli555.cookieclient.events.bus.EventHandler;
import me.bebeli555.cookieclient.events.bus.Listener;
import me.bebeli555.cookieclient.events.player.PlayerMotionUpdateEvent;
import me.bebeli555.cookieclient.gui.Group;
import me.bebeli555.cookieclient.gui.Mode;
import me.bebeli555.cookieclient.gui.Setting;

public class Flight extends Mod {
	private static float defaultFlySpeed = -1;
	
	public static Setting speed = new Setting(Mode.DOUBLE, "Speed", 0.05, "Fly speed. Vanilla speed = 0.05");
	
	public Flight() {
		super(Group.MOVEMENT, "Flight", "Allows you to fly");
	}
	
    @EventHandler
    private Listener<PlayerMotionUpdateEvent> onMotion = new Listener<>(event -> {
    	if (defaultFlySpeed == -1) {
    		defaultFlySpeed = mc.player.capabilities.getFlySpeed();
    	}
    	
    	mc.player.capabilities.isFlying = true;
    	mc.player.capabilities.setFlySpeed((float)speed.doubleValue());
    });
    
    @Override
    public void onDisabled() {
    	mc.player.capabilities.isFlying = false;
    	mc.player.capabilities.setFlySpeed(defaultFlySpeed);
    }
}
