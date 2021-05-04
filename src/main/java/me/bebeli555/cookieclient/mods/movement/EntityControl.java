package me.bebeli555.cookieclient.mods.movement;

import me.bebeli555.cookieclient.Mod;
import me.bebeli555.cookieclient.events.bus.EventHandler;
import me.bebeli555.cookieclient.events.bus.Listener;
import me.bebeli555.cookieclient.events.entity.EntitySaddledEvent;
import me.bebeli555.cookieclient.events.entity.SteerEntityEvent;
import me.bebeli555.cookieclient.gui.Group;

public class EntityControl extends Mod {
	public EntityControl() {
		super(Group.MOVEMENT, "EntityControl", "Allows you to control entities without saddle");
	}
	
    @EventHandler
    private Listener<SteerEntityEvent> onSteerEntity = new Listener<>(event -> {
    	event.cancel();
    });

    @EventHandler
    private Listener<EntitySaddledEvent> onEntitySaddled = new Listener<>(event -> {
        event.cancel();
    });
}
