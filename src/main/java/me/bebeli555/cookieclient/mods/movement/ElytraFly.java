package me.bebeli555.cookieclient.mods.movement;

import me.bebeli555.cookieclient.Mod;
import me.bebeli555.cookieclient.events.bus.EventHandler;
import me.bebeli555.cookieclient.events.bus.Listener;
import me.bebeli555.cookieclient.events.player.TravelEvent;
import me.bebeli555.cookieclient.gui.Group;
import me.bebeli555.cookieclient.gui.Mode;
import me.bebeli555.cookieclient.gui.Setting;
import me.bebeli555.cookieclient.mods.render.Freecam;
import me.bebeli555.cookieclient.utils.InventoryUtil;
import me.bebeli555.cookieclient.utils.Timer;
import net.minecraft.init.Items;
import net.minecraft.network.play.client.CPacketEntityAction;
import net.minecraft.util.math.Vec3d;

public class ElytraFly extends Mod {
	private static Timer elytraOpenPacketTimer = new Timer();
	private static Timer equipTimer = new Timer();
	private static boolean isPacketFlying;
	
	public static Setting mode = new Setting(null, "Mode", "Control", new String[]{"Control", "Doesnt change y level unless u jump or sneak"}, new String[]{"Boost", "Boosts ur velocity to the direction ur looking"}, new String[]{"Packet"});
		//Boost
		public static Setting speedBoost = new Setting(mode, "Boost", Mode.DOUBLE, "Speed", 1, "Speed Boost");
		public static Setting upSpeedBoost = new Setting(mode, "Boost", Mode.DOUBLE, "UpSpeed", 1, "UpSpeed Boost");
		public static Setting downSpeedBoost = new Setting(mode, "Boost", Mode.DOUBLE, "DownSpeed", 1, "DownSpeed Boost");
		//Control
		public static Setting controlSpeed = new Setting(mode, "Control", Mode.DOUBLE, "Speed", 2);
		public static Setting controlGlideSpeed = new Setting(mode, "Control", Mode.DOUBLE, "GlideSpeed", 0.0001);
		public static Setting controlUpSpeed = new Setting(mode, "Control", Mode.DOUBLE, "UpSpeed", 1);
		public static Setting controlDownSpeed = new Setting(mode, "Control", Mode.DOUBLE, "DownSpeed", 1);
		//Packet
		public static Setting packetSpeed = new Setting(mode, "Packet", Mode.DOUBLE, "Speed", 1.81);
		public static Setting packetGlideSpeed = new Setting(mode, "Packet", Mode.DOUBLE, "GlideSpeed", 0.0001);
		public static Setting packetUpSpeed = new Setting(mode, "Packet", Mode.DOUBLE, "UpSpeed", 1);
		public static Setting packetDownSpeed = new Setting(mode, "Packet", Mode.DOUBLE, "DownSpeed", 1);
	public static Setting autoTakeoff = new Setting(Mode.BOOLEAN, "AutoTakeoff", true, "Automatically takes off when ur falling with elytra");
		public static Setting timerTps = new Setting(autoTakeoff, Mode.DOUBLE, "TimerTPS", 10, "The value to change client-sided tps to give more time to open elytra", "Lower = slower", "20 = No timer");
		public static Setting autoEquip = new Setting(autoTakeoff, Mode.BOOLEAN, "AutoEquip", true, "Equips elytra automatically");
		public static Setting waitAmount = new Setting(autoTakeoff, Mode.INTEGER, "WaitAmount", 500, "How long to wait between sending elytra open packets in ms");
		
	public ElytraFly() {
		super(Group.MOVEMENT, "ElytraFly", "Allows you to fly faster and better with elytras");
	}
	
	@Override
	public void onDisabled() {
    	mc.timer.tickLength = 50;
    	isPacketFlying = false;
	}
	
    @EventHandler
    private Listener<TravelEvent> onTravel = new Listener<>(event -> {
    	if (mc.player == null) {
    		return;
    	}
    	
    	//Set normal timer speed back
    	if (mc.player.isElytraFlying() || mc.player.onGround) {
        	mc.timer.tickLength = 50;
    	}
    	
    	if (!mc.player.isElytraFlying() && !isPacketFlying) {
        	//Auto takeoff
        	if (autoTakeoff.booleanValue()) {
        		if (mc.player.posY < mc.player.lastTickPosY || mc.player.fallDistance > 1) {
            		//Equip elytra
            		if (autoEquip.booleanValue() && equipTimer.hasPassed(250) && InventoryUtil.getItemStack(38).getItem() != Items.ELYTRA && InventoryUtil.hasItem(Items.ELYTRA)) {
            			equipTimer.reset();
            			int elytraSlot = InventoryUtil.getSlot(Items.ELYTRA);
            			InventoryUtil.clickSlot(elytraSlot);
            			InventoryUtil.clickSlot(38);
            			InventoryUtil.clickSlot(elytraSlot);
            		}
            		
            		//Open elytra
            		if (elytraOpenPacketTimer.hasPassed(waitAmount.intValue())) {        			
                		//Set timer speed
                		mc.timer.tickLength = 1000 / (float)timerTps.doubleValue();
            			
            			elytraOpenPacketTimer.reset();
            			mc.player.connection.sendPacket(new CPacketEntityAction(mc.player, CPacketEntityAction.Action.START_FALL_FLYING));
            		}
        		}
        		
        		return;
        	}
    	}
    	
    	//Do the stuff for modes
    	if (mode.stringValue().equals("Control")) {
    		handleControlMode(event);
    	} else if (mode.stringValue().equals("Boost")) {
    		handleBoostMode();
    	} else if (mode.stringValue().equals("Packet")) {
    		handlePacketMode(event);
    	}
    });
    
    public void handleControlMode(TravelEvent event) {
		if (EntitySpeed.isInputting()) {
			double yawRad = Math.toRadians(mc.player.rotationYaw - Freecam.getRotationFromVec(new Vec3d(-mc.player.moveStrafing, 0.0, mc.player.moveForward))[0]);
			mc.player.motionX = -Math.sin(yawRad) * controlSpeed.doubleValue();
			mc.player.motionZ = Math.cos(yawRad) * controlSpeed.doubleValue();
		} else {
			mc.player.motionX = 0;
			mc.player.motionZ = 0;
		}
    	
        if (mc.player.movementInput.jump) {
        	mc.player.motionY = controlUpSpeed.doubleValue();
        } else if (mc.player.movementInput.sneak) {
        	mc.player.motionY = -controlDownSpeed.doubleValue();
        } else {
        	mc.player.motionY = -controlGlideSpeed.doubleValue();
        }
		
    	event.cancel();
    }
    
    public void handleBoostMode() {
        float yaw = (float)Math.toRadians(mc.player.rotationYaw);
        mc.player.motionX -= mc.player.movementInput.moveForward * Math.sin(yaw) * speedBoost.doubleValue() / 20;
        mc.player.motionZ += mc.player.movementInput.moveForward * Math.cos(yaw) * speedBoost.doubleValue() / 20;
        
        if (mc.player.movementInput.jump) {
        	mc.player.motionY += upSpeedBoost.doubleValue() / 15;
        } else if (mc.player.movementInput.sneak) {
        	mc.player.motionY -= downSpeedBoost.doubleValue() / 15;
        }
    }
    
    public void handlePacketMode(TravelEvent event) {
    	if (mc.player.onGround) {
    		isPacketFlying = false;
    		return;
    	}
    	
    	isPacketFlying = true;
    	mc.player.connection.sendPacket(new CPacketEntityAction(mc.player, CPacketEntityAction.Action.START_FALL_FLYING));

		if (EntitySpeed.isInputting()) {
			double yawRad = Math.toRadians(mc.player.rotationYaw - Freecam.getRotationFromVec(new Vec3d(-mc.player.moveStrafing, 0.0, mc.player.moveForward))[0]);
			mc.player.motionX = -Math.sin(yawRad) * controlSpeed.doubleValue();
			mc.player.motionZ = Math.cos(yawRad) * controlSpeed.doubleValue();
		} else {
			mc.player.motionX = 0;
			mc.player.motionZ = 0;
		}
        
        if (mc.player.movementInput.sneak) {
        	mc.player.motionY = -packetDownSpeed.doubleValue();
        } else if (mc.player.movementInput.jump) {
        	mc.player.motionY = packetUpSpeed.doubleValue();
        } else {
        	mc.player.motionY = -packetGlideSpeed.doubleValue();
        }
        
        event.cancel();
    }
}