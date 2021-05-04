package me.bebeli555.cookieclient.mods.bots.elytrabot;

import me.bebeli555.cookieclient.Mod;
import me.bebeli555.cookieclient.events.bus.EventHandler;
import me.bebeli555.cookieclient.events.bus.Listener;
import me.bebeli555.cookieclient.events.other.PacketEvent;
import me.bebeli555.cookieclient.events.player.PlayerMotionUpdateEvent;
import me.bebeli555.cookieclient.utils.Timer;
import net.minecraft.network.play.client.CPacketConfirmTeleport;
import net.minecraft.network.play.client.CPacketPlayer;
import net.minecraft.network.play.server.SPacketPlayerPosLook;

public class PacketFly extends Mod {
	private static PacketFly packetFly = new PacketFly();
	private static Timer antiKickTimer = new Timer();
	private static double startY;
	public static boolean toggled;
	
	public static void toggle(boolean on) {
		toggled = on;
		
		if (on) {
			startY = mc.player.posY;
			Mod.EVENT_BUS.subscribe(packetFly);
		} else {
			Mod.EVENT_BUS.unsubscribe(packetFly);
		}
	}
	
    @EventHandler
    public Listener<PlayerMotionUpdateEvent> playerMotionUpdateEvent = new Listener<>(event -> {
        mc.player.setVelocity(0, 0, 0);
        event.cancel();

        float speedY = 0;
        if (mc.player.posY < startY) {
            if (!antiKickTimer.hasPassed(3000)) {
                speedY = mc.player.ticksExisted % 20 == 0 ? -0.1f : 0.031f;
            } else {
            	antiKickTimer.reset();
            	speedY = -0.1f;
            }
        } else if (mc.player.ticksExisted % 4 == 0) {
        	speedY = -0.1f;
        }

        mc.player.motionY = speedY;
        mc.player.connection.sendPacket(new CPacketPlayer.PositionRotation(mc.player.posX + mc.player.motionX, mc.player.posY + mc.player.motionY, mc.player.posZ + mc.player.motionZ, mc.player.rotationYaw, mc.player.rotationPitch, mc.player.onGround));
        
        double y = mc.player.posY + mc.player.motionY;
        y += 1337;
        mc.player.connection.sendPacket(new CPacketPlayer.PositionRotation(mc.player.posX + mc.player.motionX, y, mc.player.posZ + mc.player.motionZ, mc.player.rotationYaw, mc.player.rotationPitch, mc.player.onGround));
    });
    
    @EventHandler
    public Listener<PacketEvent> onPacket = new Listener<>(event -> {
		if (event.packet instanceof SPacketPlayerPosLook && mc.currentScreen == null) {
            SPacketPlayerPosLook packet = (SPacketPlayerPosLook)event.packet;
            mc.player.connection.sendPacket(new CPacketConfirmTeleport(packet.getTeleportId()));
            mc.player.connection.sendPacket(new CPacketPlayer.PositionRotation(packet.getX(), packet.getY(), packet.getZ(), packet.getYaw(), packet.getPitch(), false));
            mc.player.setPosition(packet.getX(), packet.getY(), packet.getZ());

            event.cancel();
		}
    });
}
