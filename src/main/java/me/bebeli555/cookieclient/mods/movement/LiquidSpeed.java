package me.bebeli555.cookieclient.mods.movement;

import me.bebeli555.cookieclient.Mod;
import me.bebeli555.cookieclient.events.bus.EventHandler;
import me.bebeli555.cookieclient.events.bus.Listener;
import me.bebeli555.cookieclient.events.player.PlayerMoveEvent;
import me.bebeli555.cookieclient.gui.Group;
import me.bebeli555.cookieclient.gui.Mode;
import me.bebeli555.cookieclient.gui.Setting;
import me.bebeli555.cookieclient.mods.render.Freecam;
import net.minecraft.init.Blocks;
import net.minecraft.util.math.Vec3d;

public class LiquidSpeed extends Mod {
	public static Setting speedAdd = new Setting(Mode.DOUBLE, "SpeedAdd", 0.18, "How much speed to add", "Higher = faster");
	public static Setting upSpeed = new Setting(Mode.DOUBLE, "UpSpeed", 0.1, "How fast it goes up when pressing jump key", "This is added to the vanilla speed");
	public static Setting downSpeed = new Setting(Mode.DOUBLE, "DownSpeed", 0.1, "How fast it goes down when pressing sneak key", "This is added to the vanilla speed");
	
	public LiquidSpeed() {
		super(Group.MOVEMENT, "LiquidSpeed", "Move faster in water/lava");
	}
	
    @EventHandler
    private Listener<PlayerMoveEvent> onMove = new Listener<>(event -> {
    	if (mc.player == null || getBlock(getPlayerPos()) != Blocks.WATER && getBlock(getPlayerPos()) != Blocks.LAVA) {
    		return;
    	}
    	
		if (EntitySpeed.isInputting()) {
			double yawRad = Math.toRadians(mc.player.rotationYaw - Freecam.getRotationFromVec(new Vec3d(-mc.player.moveStrafing, 0.0, mc.player.moveForward))[0]);
			event.x += -Math.sin(yawRad) * speedAdd.doubleValue();
			event.z += Math.cos(yawRad) * speedAdd.doubleValue();
		}
		
		if (mc.gameSettings.keyBindJump.isKeyDown()) event.y += upSpeed.doubleValue();
		if (mc.gameSettings.keyBindSneak.isKeyDown()) event.y -= downSpeed.doubleValue();
		
		event.cancel();
    });
}