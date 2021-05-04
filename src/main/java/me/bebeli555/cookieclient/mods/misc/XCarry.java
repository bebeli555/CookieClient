package me.bebeli555.cookieclient.mods.misc;

import me.bebeli555.cookieclient.Mod;
import me.bebeli555.cookieclient.events.bus.EventHandler;
import me.bebeli555.cookieclient.events.bus.Listener;
import me.bebeli555.cookieclient.events.other.PacketEvent;
import me.bebeli555.cookieclient.gui.Group;
import net.minecraft.network.play.client.CPacketCloseWindow;

public class XCarry extends Mod {
	public XCarry() {
		super(Group.MISC, "XCarry", "Allows you to carry items in ur crafting slots");
	}
	
	@Override
	public void onDisabled() {
        if (mc.world != null) {
            mc.player.connection.sendPacket(new CPacketCloseWindow(mc.player.inventoryContainer.windowId));
        }
	}
	
    @EventHandler
    private Listener<PacketEvent> packetEvent = new Listener<>(event -> {
        if (event.packet instanceof CPacketCloseWindow) {
            CPacketCloseWindow packet = (CPacketCloseWindow)event.packet;
            
            if (packet.windowId == mc.player.inventoryContainer.windowId) {
            	event.cancel();
            }
        }
    });
}
