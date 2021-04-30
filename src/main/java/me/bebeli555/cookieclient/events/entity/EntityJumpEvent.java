package me.bebeli555.cookieclient.events.entity;

import me.zero.alpine.type.Cancellable;
import net.minecraft.entity.player.EntityPlayer;

public class EntityJumpEvent extends Cancellable {
	public EntityPlayer entity;
	
	public EntityJumpEvent(EntityPlayer entity) {
		this.entity = entity;
	}
}
