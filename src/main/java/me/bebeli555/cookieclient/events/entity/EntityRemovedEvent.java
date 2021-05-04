package me.bebeli555.cookieclient.events.entity;

import me.bebeli555.cookieclient.events.bus.Cancellable;
import net.minecraft.entity.Entity;

public class EntityRemovedEvent extends Cancellable {
	public Entity entity;
	
	public EntityRemovedEvent(Entity entity) {
		this.entity = entity;
	}
}
