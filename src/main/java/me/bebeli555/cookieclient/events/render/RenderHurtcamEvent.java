package me.bebeli555.cookieclient.events.render;

import me.zero.alpine.type.Cancellable;

public class RenderHurtcamEvent extends Cancellable {
	public float ticks;
	
	public RenderHurtcamEvent(float ticks) {
		this.ticks = ticks;
	}
}
