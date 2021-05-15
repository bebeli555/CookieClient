package me.bebeli555.cookieclient.mods.movement;

import me.bebeli555.cookieclient.Mod;
import me.bebeli555.cookieclient.events.bus.EventHandler;
import me.bebeli555.cookieclient.events.bus.Listener;
import me.bebeli555.cookieclient.events.other.PacketEvent;
import me.bebeli555.cookieclient.gui.Group;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.network.play.client.CPacketConfirmTeleport;
import net.minecraft.network.play.client.CPacketPlayer;
import net.minecraft.network.play.server.SPacketPlayerPosLook;

public class NoRotate extends Mod {
	public NoRotate() {
		super(Group.MOVEMENT, "NoRotate", "Cancels server rotations");
	}
	
    @EventHandler
    private Listener<PacketEvent> packetEvent = new Listener<>(event -> {
        if (event.packet instanceof SPacketPlayerPosLook && mc.player != null && !mc.player.isDead) {
        	SPacketPlayerPosLook packet = (SPacketPlayerPosLook)event.packet;
        	event.cancel();
        	
            double x = packet.getX();
            double y = packet.getY();
            double z = packet.getZ();

            if (packet.getFlags().contains(SPacketPlayerPosLook.EnumFlags.X)) {
                x += mc.player.posX;
            } else {
            	mc.player.motionX = 0.0D;
            }

            if (packet.getFlags().contains(SPacketPlayerPosLook.EnumFlags.Y)) {
                y += mc.player.posY;
            } else {
            	mc.player.motionY = 0.0D;
            }

            if (packet.getFlags().contains(SPacketPlayerPosLook.EnumFlags.Z)) {
                z += mc.player.posZ;
            } else {
            	mc.player.motionZ = 0.0D;
            }

            mc.player.setPosition(x, y, z);
            mc.player.connection.sendPacket(new CPacketConfirmTeleport(packet.getTeleportId()));
            mc.player.connection.sendPacket(new CPacketPlayer.PositionRotation(mc.player.posX, mc.player.getEntityBoundingBox().minY, mc.player.posZ, mc.player.rotationYaw, mc.player.rotationPitch, false));

            if (!mc.player.connection.doneLoadingTerrain) {
                mc.player.prevPosX = mc.player.posX;
                mc.player.prevPosY = mc.player.posY;
                mc.player.prevPosZ = mc.player.posZ;
                mc.player.connection.doneLoadingTerrain = true;
                mc.displayGuiScreen((GuiScreen)null);
            }
        }
    });
}
