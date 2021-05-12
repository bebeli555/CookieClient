package me.bebeli555.cookieclient.mods.movement;

import me.bebeli555.cookieclient.Mod;
import me.bebeli555.cookieclient.events.bus.EventHandler;
import me.bebeli555.cookieclient.events.bus.Listener;
import me.bebeli555.cookieclient.events.other.PacketEvent;
import me.bebeli555.cookieclient.gui.Group;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.network.play.client.CPacketConfirmTeleport;
import net.minecraft.network.play.client.CPacketPlayer;
import net.minecraft.network.play.server.SPacketPlayerPosLook;

public class NoRotate extends Mod {
	public NoRotate() {
		super(Group.MOVEMENT, "NoRotate", "Cancels server rotations");
	}
	
    @EventHandler
    private Listener<PacketEvent> packetEvent = new Listener<>(event -> {
        if (event.packet instanceof SPacketPlayerPosLook) {
            if (mc.player != null && mc.currentScreen == null) {
            	event.cancel();
            	
                EntityPlayer player = mc.player;
                final SPacketPlayerPosLook packetIn = (SPacketPlayerPosLook) event.packet;
		
		float serverYaw = packetIn.getYaw();
		float serverPitch = packetIn.getPitch();
                double serverX = packetIn.getX();
                double serverY = packetIn.getY();
                double serverZ = packetIn.getZ();

                player.setPosition(serverX, serverY, serverZ);
                mc.getConnection().sendPacket(new CPacketConfirmTeleport(packetIn.getTeleportId()));
                mc.getConnection().sendPacket(new CPacketPlayer.PositionRotation(serverX, serverY, serverZ, serverYaw, serverPitch, false));
            }
        }
    });
}
