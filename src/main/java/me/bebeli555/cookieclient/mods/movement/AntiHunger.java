package me.bebeli555.cookieclient.mods.movement;

import me.bebeli555.cookieclient.Mod;
import me.bebeli555.cookieclient.events.bus.EventHandler;
import me.bebeli555.cookieclient.events.bus.Listener;
import me.bebeli555.cookieclient.events.other.PacketEvent;
import me.bebeli555.cookieclient.gui.Group;
import net.minecraft.network.play.client.CPacketEntityAction;

public class AntiHunger extends Mod {
	public AntiHunger() {
		super(Group.MOVEMENT, "AntiHunger", "Reduces lost hunger");
	}
	
	@Override
	public void onEnabled() {
		if (mc.player != null && mc.player.isSprinting()) {
			mc.player.connection.sendPacket(new CPacketEntityAction(mc.player, CPacketEntityAction.Action.STOP_SPRINTING));
		}
	}
	
    @EventHandler
    private Listener<PacketEvent> packetEvent = new Listener<>(event -> {
    	if (event.packet instanceof CPacketEntityAction) {
        	CPacketEntityAction packet = (CPacketEntityAction)event.packet;
            if (packet.action == CPacketEntityAction.Action.START_SPRINTING || packet.action == CPacketEntityAction.Action.STOP_SPRINTING) {
                event.cancel();
            }
    	}
    });
}
