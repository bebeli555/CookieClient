package me.bebeli555.cookieclient.mods.movement;

import me.bebeli555.cookieclient.Mod;
import me.bebeli555.cookieclient.events.bus.EventHandler;
import me.bebeli555.cookieclient.events.bus.Listener;
import me.bebeli555.cookieclient.events.other.PacketEvent;
import me.bebeli555.cookieclient.gui.Group;
import me.bebeli555.cookieclient.utils.InventoryUtil;
import net.minecraft.init.Items;
import net.minecraft.network.play.client.CPacketPlayer;

public class NoFall extends Mod {
	public NoFall() {
		super(Group.MOVEMENT, "NoFall", "Prevents fall damage");
	}
	
    @EventHandler
    private Listener<PacketEvent> packetEvent = new Listener<>(event -> {
    	if (event.packet instanceof CPacketPlayer) {
    		if (mc.player.isElytraFlying() || InventoryUtil.getItemStack(38).getItem() == Items.ELYTRA && mc.gameSettings.keyBindJump.isKeyDown()) {
    			return;
    		}
    		
    		CPacketPlayer packet = (CPacketPlayer)event.packet;
			packet.onGround = true;
    	}
    });
}
