package me.bebeli555.cookieclient.events.render;

import me.bebeli555.cookieclient.events.bus.Cancellable;
import net.minecraft.client.renderer.culling.ICamera;
import net.minecraft.entity.Entity;

public class RenderEntityEvent extends Cancellable {
	public Entity entity;
	public ICamera camera;
	public double x, y, z;
	
	public RenderEntityEvent(Entity entity, ICamera camera, double x, double y, double z) {
		this.entity = entity;
		this.camera = camera;
		this.z = z;
	}
}
