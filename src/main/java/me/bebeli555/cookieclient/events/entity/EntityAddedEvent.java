package me.bebeli555.cookieclient.events.entity;

import me.zero.alpine.type.Cancellable;
import net.minecraft.entity.Entity;

public class EntityAddedEvent extends Cancellable {
	public Entity entity;
	
	public EntityAddedEvent(Entity entity) {
		this.entity = entity;
	}
}
