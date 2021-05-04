package me.bebeli555.cookieclient.events.player;

import me.bebeli555.cookieclient.events.bus.Cancellable;
import net.minecraft.entity.MoverType;

public class PlayerMoveEvent extends Cancellable {
	public MoverType type;
	public double x, y, z;
	
	public PlayerMoveEvent(MoverType type, double x, double y, double z) {
		this.type = type;
		this.x = x;
		this.y = y;
		this.z = z;
	}
}
