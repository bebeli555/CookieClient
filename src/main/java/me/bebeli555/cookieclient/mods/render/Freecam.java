package me.bebeli555.cookieclient.mods.render;

import org.lwjgl.input.Mouse;

import me.bebeli555.cookieclient.Mod;
import me.bebeli555.cookieclient.events.bus.EventHandler;
import me.bebeli555.cookieclient.events.bus.Listener;
import me.bebeli555.cookieclient.events.other.PacketEvent;
import me.bebeli555.cookieclient.events.player.PlayerUpdateMoveStateEvent;
import me.bebeli555.cookieclient.gui.Group;
import me.bebeli555.cookieclient.gui.Mode;
import me.bebeli555.cookieclient.gui.Setting;
import me.bebeli555.cookieclient.utils.BaritoneUtil;
import net.minecraft.client.entity.EntityOtherPlayerMP;
import net.minecraft.entity.MoverType;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.network.play.client.CPacketUseEntity;
import net.minecraft.util.math.Vec3d;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.entity.living.LivingEvent.LivingUpdateEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.InputEvent.MouseInputEvent;

public class Freecam extends Mod {
	public static boolean isToggled;
	public static EntityPlayer camera;
	private boolean clicked;
	
	public static Setting speed = new Setting(Mode.DOUBLE, "Speed", 1, "How fast to move");
	public static Setting baritoneMiddleClick = new Setting(Mode.BOOLEAN, "BaritoneMiddleClick", true, "Middleclick blocks while in freecam", "To make baritone walk there");
	
	public Freecam() {
		super(Group.RENDER, "Freecam", "Allows you to fly out of your body");
	}
	
	@Override
	public void onEnabled() {
		clicked = false;
		isToggled = true;
		if (mc.player == null || mc.world == null) {
			disable();
			return;
		}
		
		Mod.EVENT_BUS.subscribe(this);
		MinecraftForge.EVENT_BUS.register(this);
		mc.renderChunksMany = false;

		camera = new EntityOtherPlayerMP(mc.world, mc.getSession().getProfile());
		camera.copyLocationAndAnglesFrom(mc.player);
		camera.prevRotationYaw = mc.player.rotationYaw;
		camera.rotationYawHead = mc.player.rotationYawHead;
		camera.inventory.copyInventory(mc.player.inventory);
        mc.world.addEntityToWorld(-100, camera);
        mc.renderViewEntity = camera;
	}
	
	@Override
	public void onDisabled() {		
		isToggled = false;
		Mod.EVENT_BUS.unsubscribe(this);
		MinecraftForge.EVENT_BUS.unregister(this);
		mc.renderChunksMany = true;
		
		if (mc.player != null && mc.world != null && mc.renderViewEntity != null) {
			mc.player.moveStrafing = 0;
			mc.player.moveForward = 0;
	        mc.world.removeEntity(camera);
	        mc.renderViewEntity = mc.player;
		}
	}
	
	@SubscribeEvent
	public void onUpdate(LivingUpdateEvent e) {
    	if (!e.getEntity().equals(camera) || mc.currentScreen != null) {
    		return;
    	}

    	if (camera == null) {
    		disable();
    		return;
    	}
    	
    	//Update motion
    	if (mc.gameSettings.keyBindJump.isKeyDown()) {
    		camera.motionY = speed.doubleValue();
    	} else if (mc.gameSettings.keyBindSneak.isKeyDown()) {
    		camera.motionY = -speed.doubleValue();
    	} else {
    		camera.motionY = 0;
    	}
    	
    	if (mc.gameSettings.keyBindForward.isKeyDown()) {
    		camera.moveForward = 1;
    	} else if (mc.gameSettings.keyBindBack.isKeyDown()) {
    		camera.moveForward = -1;
    	} else {
    		camera.moveForward = 0;
    	}
    	
    	if (mc.gameSettings.keyBindLeft.isKeyDown()) {
    		camera.moveStrafing = -1;
    	} else if (mc.gameSettings.keyBindRight.isKeyDown()) {
    		camera.moveStrafing = 1;
    	} else {
    		camera.moveStrafing = 0;
    	}
    	
        if (camera.moveStrafing != 0 || camera.moveForward != 0) {
        	double yawRad = Math.toRadians(camera.rotationYaw - getRotationFromVec(new Vec3d(camera.moveStrafing, 0.0, camera.moveForward))[0]);

        	camera.motionX = -Math.sin(yawRad) * speed.doubleValue();
        	camera.motionZ = Math.cos(yawRad) * speed.doubleValue();
        	
        	if (mc.gameSettings.keyBindSprint.isKeyDown()) {
        		camera.setSprinting(true);
        		camera.motionX *= 1.5;
        		camera.motionZ *= 1.5;
        	} else {
        		camera.setSprinting(false);
        	}
        } else {
        	camera.motionX = 0;
        	camera.motionZ = 0;
        }
        
        camera.inventory.copyInventory(mc.player.inventory);
        camera.noClip = true;
        camera.rotationYawHead = camera.rotationYaw;
        
		camera.move(MoverType.SELF, camera.motionX, camera.motionY, camera.motionZ);
	}
	
    @EventHandler
    private Listener<PacketEvent> packetEvent = new Listener<>(event -> {
    	try {
        	if (event.packet instanceof CPacketUseEntity) {
        		CPacketUseEntity packet = (CPacketUseEntity)event.packet;
        		
        		if (packet.getEntityFromWorld(mc.world).equals(mc.player)) {
        			event.cancel();
        		}
        	}
    	} catch (NullPointerException e) {
    		
    	}
    });
	
	@SubscribeEvent
	public void onMouseInput(MouseInputEvent e) {
		if (Mouse.isButtonDown(2) && baritoneMiddleClick.booleanValue()) {
			if (clicked) {
				return;
			}
			
			clicked = true;
			if (mc.objectMouseOver == null || mc.objectMouseOver.getBlockPos() == null) {
				return;
			}
			
			BaritoneUtil.walkTo(mc.objectMouseOver.getBlockPos().add(0, 1, 0), false);
		} else {
			clicked = false;
		}
	}
	
    @EventHandler
    private Listener<PlayerUpdateMoveStateEvent> onKeyPress = new Listener<>(event -> {
		mc.player.movementInput.moveForward = 0;
		mc.player.movementInput.moveStrafe = 0;
		mc.player.movementInput.jump = false;
		mc.player.movementInput.sneak = false;
		mc.player.setSprinting(false);
    	
    	event.cancel();
    });
    
    public static double[] getRotationFromVec(Vec3d vec) {
        double xz = Math.sqrt(vec.x * vec.x + vec.z * vec.z);
        double yaw = normalizeAngle(Math.toDegrees(Math.atan2(vec.z, vec.x)) - 90.0);
        double pitch = normalizeAngle(Math.toDegrees(-Math.atan2(vec.y, xz)));
        return new double[]{yaw, pitch};
    }
    
    public static double normalizeAngle(double angle) {
        angle %= 360.0;
        
        if (angle >= 180.0) {
            angle -= 360.0;
        }
        
        if (angle < -180.0) {
            angle += 360.0;
        }
        
        return angle;
    }
}
