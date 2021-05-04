package me.bebeli555.cookieclient.events.render;

import me.bebeli555.cookieclient.events.bus.Cancellable;
import net.minecraft.client.entity.AbstractClientPlayer;

public class RenderEntityNameEvent extends Cancellable {
	public AbstractClientPlayer entity;
	public double x, y, z;
	public String name;
	public double distance;
	
	public RenderEntityNameEvent(AbstractClientPlayer entity, double x, double y, double z, String name, double distance) {
		this.entity = entity;
		this.x = x;
		this.y = y;
		this.z = z;
		this.name = name;
		this.distance = distance;
	}
}
