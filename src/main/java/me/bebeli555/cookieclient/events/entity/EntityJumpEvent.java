package me.bebeli555.cookieclient.events.entity;

import me.bebeli555.cookieclient.events.bus.Cancellable;
import net.minecraft.entity.player.EntityPlayer;

public class EntityJumpEvent extends Cancellable {
	public EntityPlayer entity;
	
	public EntityJumpEvent(EntityPlayer entity) {
		this.entity = entity;
	}
}
