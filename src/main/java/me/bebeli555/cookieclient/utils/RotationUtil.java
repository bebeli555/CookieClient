package me.bebeli555.cookieclient.utils;

import me.bebeli555.cookieclient.Mod;
import me.bebeli555.cookieclient.events.bus.EventHandler;
import me.bebeli555.cookieclient.events.bus.Listener;
import me.bebeli555.cookieclient.events.other.PacketEvent;
import me.bebeli555.cookieclient.events.player.PlayerMotionUpdateEvent;
import net.minecraft.network.play.client.CPacketPlayer;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent.ClientTickEvent;

public class RotationUtil extends Mod {
	public static boolean isRotateSpoofing;
	public static RotationUtil rotationUtil = new RotationUtil();
	public static float yaw, pitch;
	private static int yawPlusCount, yawMinusCount;
	
	/**
	 * Rotates to the given vector
	 * @param sendPacket if true then it will also send a packet to the server
	 */
	public static void rotate(Vec3d vec, boolean sendPacket) {
        float[] rotations = getRotations(vec);
		
        if (sendPacket) mc.player.connection.sendPacket(new CPacketPlayer.Rotation(rotations[0], rotations[1], mc.player.onGround));
        mc.player.rotationYaw = rotations[0];
        mc.player.rotationPitch = rotations[1];
	}
	
	/**
	 * Rotates to the given vector only serverside
	 */
	public static void rotateSpoof(Vec3d vec) {
		float[] rotations = getRotations(vec);
		yaw = rotations[0];
		pitch = rotations[1];
		
		mc.player.connection.sendPacket(new CPacketPlayer.Rotation(yaw, pitch, mc.player.onGround));
		isRotateSpoofing = true;
		Mod.EVENT_BUS.subscribe(rotationUtil);
		MinecraftForge.EVENT_BUS.register(rotationUtil);
	}
	
	/**
	 * Rotates to the given vector only serverside
	 * Doesnt send a extra packet when called
	 */
	public static void rotateSpoofNoPacket(Vec3d vec) {
		float[] rotations = getRotations(vec);
		yaw = rotations[0];
		pitch = rotations[1];
		isRotateSpoofing = true;
		Mod.EVENT_BUS.subscribe(rotationUtil);
		MinecraftForge.EVENT_BUS.register(rotationUtil);
	}
	
	/**
	 * Stops rotating if you called the rotateSpoof then u need to call this
	 */
	public static void stopRotating() {
		isRotateSpoofing = false;
		Mod.EVENT_BUS.unsubscribe(rotationUtil);
		MinecraftForge.EVENT_BUS.unregister(rotationUtil);
	}
	
	public static float[] getRotations(Vec3d vec) {
		Vec3d eyesPos = new Vec3d(mc.player.posX, mc.player.posY + mc.player.getEyeHeight(), mc.player.posZ);
		double diffX = vec.x - eyesPos.x;
		double diffY = vec.y - eyesPos.y;
		double diffZ = vec.z - eyesPos.z;
		double diffXZ = Math.sqrt(diffX * diffX + diffZ * diffZ);
		float yaw = (float) Math.toDegrees(Math.atan2(diffZ, diffX)) - 90F;
		float pitch = (float) -Math.toDegrees(Math.atan2(diffY, diffXZ));

		return new float[]{mc.player.rotationYaw + MathHelper.wrapDegrees(yaw - mc.player.rotationYaw), mc.player.rotationPitch + MathHelper.wrapDegrees(pitch - mc.player.rotationPitch)};
	}
	
    @EventHandler
    private Listener<PacketEvent> onPacket = new Listener<>(event -> {
    	if (event.packet instanceof CPacketPlayer.Rotation) {
    		CPacketPlayer.Rotation packet = (CPacketPlayer.Rotation)event.packet;
    		packet.yaw = yaw;
    		packet.pitch = pitch;
    	} else if (event.packet instanceof CPacketPlayer.PositionRotation) {
    		CPacketPlayer.PositionRotation packet = (CPacketPlayer.PositionRotation)event.packet;
    		packet.yaw = yaw;
    		packet.pitch = pitch;
    	}
    });
    
    @EventHandler
    private Listener<PlayerMotionUpdateEvent> onMotionUpdate = new Listener<>(event -> {
    	Mod.mc.player.rotationYawHead = RotationUtil.yaw;
    });
    
    @SubscribeEvent
    public void onTick(ClientTickEvent e) {
    	//Rotates ur yaw a little bit so minecraft will send the rotation packet next tick. Otherwise it doesnt send it if it hasent moved.
    	//You could probably do this some other way but this way theres no risk of sending extra packets and getting desynced.
    	if (yawMinusCount < 10) {
    		yawMinusCount++;
        	mc.player.rotationYaw -= 0.005;
    	} else if (yawPlusCount < 10) {
    		yawPlusCount++;
        	mc.player.rotationYaw += 0.005;
    	} else {
    		yawMinusCount = 0;
    		yawPlusCount = 0;
    	}
    }
}
