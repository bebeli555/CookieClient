package me.bebeli555.cookieclient.mods.movement;

import me.bebeli555.cookieclient.Mod;
import me.bebeli555.cookieclient.events.bus.EventHandler;
import me.bebeli555.cookieclient.events.bus.Listener;
import me.bebeli555.cookieclient.events.player.TravelEvent;
import me.bebeli555.cookieclient.gui.Group;
import me.bebeli555.cookieclient.gui.Mode;
import me.bebeli555.cookieclient.gui.Setting;
import me.bebeli555.cookieclient.mods.render.Freecam;
import net.minecraft.entity.Entity;
import net.minecraft.entity.passive.AbstractHorse;
import net.minecraft.entity.passive.EntityPig;
import net.minecraft.util.math.Vec3d;

public class EntitySpeed extends Mod {
	public static Setting speed = new Setting(Mode.DOUBLE, "Speed", 0.5, "How fast it will go", "Higher = faster");
	public static Setting antiStuck = new Setting(Mode.BOOLEAN, "AntiStuck", true, "Prevents getting stuck");
	public static Setting fly = new Setting(Mode.BOOLEAN, "Fly", false, "Makes ur entity fly when holding jump key");
		public static Setting glideSpeed = new Setting(fly, Mode.DOUBLE, "GlideSpeed", 0.1, "How fast it glides down");
		public static Setting upSpeed = new Setting(fly, Mode.DOUBLE, "UpSpeed", 1, "How fast to go up when holding jump key");
	
	public EntitySpeed() {
		super(Group.MOVEMENT, "EntitySpeed", "Make entities go faster");
	}
	
    @EventHandler
    private Listener<TravelEvent> onTravel = new Listener<>(event -> {
    	if (mc.player == null || mc.player.ridingEntity == null) {
    		return;
    	}
    	
    	if (mc.player.ridingEntity instanceof EntityPig || mc.player.ridingEntity instanceof AbstractHorse && mc.player.ridingEntity.getControllingPassenger().equals(mc.player)) {
    		moveEntity(mc.player.ridingEntity, speed.doubleValue(), antiStuck.booleanValue());
    		
    		if (mc.player.ridingEntity instanceof AbstractHorse) {
    			mc.player.ridingEntity.rotationYaw = mc.player.rotationYaw;
    		}
    		
    		if (fly.booleanValue()) {
    			fly(mc.player.ridingEntity);
    		}
    	}
    });
    
	public static void moveEntity(Entity entity, double speed, boolean antiStuck) {
		double yawRad = Math.toRadians(mc.player.rotationYaw - Freecam.getRotationFromVec(new Vec3d(-mc.player.moveStrafing, 0.0, mc.player.moveForward))[0]);

		if (isInputting()) {
			entity.motionX = -Math.sin(yawRad) * speed;
			entity.motionZ = Math.cos(yawRad) * speed;
		} else {
			entity.motionX = 0;
			entity.motionZ = 0;
		}
		
		if (antiStuck && entity.posY > entity.lastTickPosY) {
			entity.motionX = -Math.sin(yawRad) * 0.1;
			entity.motionZ = Math.cos(yawRad) * 0.1;
		}
	}
	
	public static boolean isInputting() {
		return mc.gameSettings.keyBindForward.isKeyDown() || mc.gameSettings.keyBindBack.isKeyDown() || mc.gameSettings.keyBindRight.isKeyDown() || mc.gameSettings.keyBindLeft.isKeyDown();
	}
	
	public static void fly(Entity entity) {
		if (!entity.isInWater()) {
			entity.motionY = -glideSpeed.doubleValue();
		}
		
		if (mc.gameSettings.keyBindJump.isKeyDown()) {
			entity.motionY += upSpeed.doubleValue();
		}
	}
}
