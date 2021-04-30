package me.bebeli555.cookieclient.events.entity;

import me.zero.alpine.type.Cancellable;
import net.minecraft.entity.Entity;

public class EntityPushEvent extends Cancellable {
	public Entity entity;
	
	public EntityPushEvent(Entity entity) {
		this.entity = entity;
	}
}
