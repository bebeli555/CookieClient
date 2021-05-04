package me.bebeli555.cookieclient.events.other;

import me.bebeli555.cookieclient.events.bus.Cancellable;
import net.minecraft.network.Packet;

@SuppressWarnings("rawtypes")
public class PacketPostEvent extends Cancellable {
	public Packet packet;
	
	public PacketPostEvent(Packet packet) {
		this.packet = packet;
	}
}
