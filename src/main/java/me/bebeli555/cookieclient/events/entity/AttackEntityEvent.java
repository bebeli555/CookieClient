package me.bebeli555.cookieclient.events.entity;

import me.bebeli555.cookieclient.events.bus.Cancellable;
import net.minecraft.entity.Entity;

public class AttackEntityEvent extends Cancellable {
	public Entity target;
	
	public AttackEntityEvent(Entity target) {
		this.target = target;
	}
}
