package me.bebeli555.cookieclient.mods.movement;

import me.bebeli555.cookieclient.Mod;
import me.bebeli555.cookieclient.events.other.PacketEvent;
import me.bebeli555.cookieclient.gui.Group;
import me.zero.alpine.listener.EventHandler;
import me.zero.alpine.listener.Listener;
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
                final SPacketPlayerPosLook packetIn = (SPacketPlayerPosLook)event.packet;
                double d0 = packetIn.getX();
                double d1 = packetIn.getY();
                double d2 = packetIn.getZ();

                if (packetIn.getFlags().contains(SPacketPlayerPosLook.EnumFlags.X)) {
                    d0 += player.posX;
                } else {
                	player.motionX = 0.0D;
                }

                if (packetIn.getFlags().contains(SPacketPlayerPosLook.EnumFlags.Y)) {
                    d1 += player.posY;
                } else {
                	player.motionY = 0.0D;
                }

                if (packetIn.getFlags().contains(SPacketPlayerPosLook.EnumFlags.Z)) {
                    d2 += player.posZ;
                } else {
                	player.motionZ = 0.0D;
                }
                
                player.setPosition(d0, d1, d2);
                mc.getConnection().sendPacket(new CPacketConfirmTeleport(packetIn.getTeleportId()));
                mc.getConnection().sendPacket(new CPacketPlayer.PositionRotation(player.posX, player.getEntityBoundingBox().minY, player.posZ, packetIn.getYaw(), packetIn.getPitch(), false));
            }
        }
    });
}
