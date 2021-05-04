package me.bebeli555.cookieclient.mods.movement;

import me.bebeli555.cookieclient.Mod;
import me.bebeli555.cookieclient.events.bus.EventHandler;
import me.bebeli555.cookieclient.events.bus.Listener;
import me.bebeli555.cookieclient.events.other.PacketEvent;
import me.bebeli555.cookieclient.events.player.PlayerMoveEvent;
import me.bebeli555.cookieclient.events.player.PlayerUpdateEvent;
import me.bebeli555.cookieclient.gui.Group;
import me.bebeli555.cookieclient.gui.Mode;
import me.bebeli555.cookieclient.gui.Setting;
import me.bebeli555.cookieclient.utils.Timer;
import net.minecraft.init.MobEffects;
import net.minecraft.network.play.server.SPacketExplosion;

public class Strafe extends Mod {
	private Timer knockbackTimer = new Timer();
	private double boostedX, boostedZ;
	
	public static Setting speed = new Setting(Mode.DOUBLE, "Speed", 0.285, "How fast it goes");
	public static Setting height = new Setting(Mode.DOUBLE, "Height", 0.41, "How far up it will jump");
	public static Setting crystalBoost = new Setting(Mode.BOOLEAN, "CrystalBoost", false, "Applies the knockback u take from crystals to the strafe speed");
		public static Setting knockbackAmount = new Setting(crystalBoost, Mode.INTEGER, "Amount", 850, "How long to keep the boosted motion in milliseconds");
	
	public Strafe() {
		super(Group.MOVEMENT, "Strafe", "Goes a bit faster than normal", "Also when u take knockback it will make the speed faster");
	}
	
    @EventHandler
    private Listener<PlayerUpdateEvent> onPlayerUpdate = new Listener<>(event ->  {
        if (!EntitySpeed.isInputting() || mc.player.isRiding() || !mc.player.onGround || !mc.renderViewEntity.equals(mc.player)) {
            return;
        }

    	mc.player.motionY = height.doubleValue();
    });
    
    @EventHandler
    private Listener<PlayerMoveEvent> onPlayerMove = new Listener<>(event -> {
    	if (!mc.renderViewEntity.equals(mc.player)) {
    		return;
    	}
    	
        float playerSpeed = (float)speed.doubleValue();
        float moveForward = mc.player.movementInput.moveForward;
        float moveStrafe = mc.player.movementInput.moveStrafe;
        float rotationYaw = mc.player.rotationYaw;

        if (mc.player.isPotionActive(MobEffects.SPEED)) {
            final int amplifier = mc.player.getActivePotionEffect(MobEffects.SPEED).getAmplifier();
            playerSpeed *= (1.0f + 0.2f * (amplifier + 1));
        }

        if (moveForward == 0.0f && moveStrafe == 0.0f) {
            event.x = (0.0d);
            event.z = (0.0d);
        } else {
            if (moveForward != 0.0f) {
				if (moveStrafe > 0.0f) {
					rotationYaw += ((moveForward > 0.0f) ? -45 : 45);
				} else if (moveStrafe < 0.0f) {
					rotationYaw += ((moveForward > 0.0f) ? 45 : -45);
				}
				
				moveStrafe = 0.0f;
				if (moveForward > 0.0f) {
					moveForward = 1.0f;
				} else if (moveForward < 0.0f) {
					moveForward = -1.0f;
				}
            }
                        
            if (crystalBoost.booleanValue() && !knockbackTimer.hasPassed(knockbackAmount.intValue())) {
            	playerSpeed = (float)Math.abs(boostedX);
            	playerSpeed += Math.abs(boostedZ);
            }
            
            event.x = ((moveForward * playerSpeed) * Math.cos(Math.toRadians((rotationYaw + 90.0f))) + (moveStrafe * playerSpeed) * Math.sin(Math.toRadians((rotationYaw + 90.0f))));
            event.z = ((moveForward * playerSpeed) * Math.sin(Math.toRadians((rotationYaw + 90.0f))) - (moveStrafe * playerSpeed) * Math.cos(Math.toRadians((rotationYaw + 90.0f))));
        }
        
        event.cancel();
    });
    
	@EventHandler
	private Listener<PacketEvent> packetEvent = new Listener<>(event -> {
		if (event.packet instanceof SPacketExplosion) {
			SPacketExplosion packet = (SPacketExplosion)event.packet;

        	boostedX = packet.getMotionX();
        	boostedZ = packet.getMotionZ();
        	knockbackTimer.reset();
		}
	});
}
