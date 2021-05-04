package me.bebeli555.cookieclient.mods.movement;

import me.bebeli555.cookieclient.Mod;
import me.bebeli555.cookieclient.events.bus.EventHandler;
import me.bebeli555.cookieclient.events.bus.Listener;
import me.bebeli555.cookieclient.events.player.PlayerMoveEvent;
import me.bebeli555.cookieclient.gui.Group;

public class SafeWalk extends Mod {
	public static SafeWalk instance;
	
	public SafeWalk() {
		super(Group.MOVEMENT, "SafeWalk", "Stops you from walking off ledges", "Acts like u would be sneaking");
		instance = this;
	}
	
    @EventHandler
    private Listener<PlayerMoveEvent> onPlayerMove = new Listener<>(event -> {
        double x = event.x;
        double y = event.y;
        double z = event.z;
        
        if (mc.player.onGround && !mc.player.noClip) {
            double increment;
            for (increment = 0.05D; x != 0.0D && isOffsetBBEmpty(x, -1, 0.0D);) {
                if (x < increment && x >= -increment) {
                    x = 0.0D;
                } else if (x > 0.0D) {
                    x -= increment;
                } else {
                    x += increment;
                }
            }
            
            for (; z != 0.0D && isOffsetBBEmpty(0.0D, -1, z);) {
                if (z < increment && z >= -increment) {
                    z = 0.0D;
                } else if (z > 0.0D) {
                    z -= increment;
                } else {
                    z += increment;
                }
            }
            
            for (; x != 0.0D && z != 0.0D && isOffsetBBEmpty(x, -1, z);) {
                if (x < increment && x >= -increment) {
                    x = 0.0D;
                } else if (x > 0.0D) {
                    x -= increment;
                } else {
                    x += increment;
                } 
                
                if (z < increment && z >= -increment) {
                    z = 0.0D;
                } else if (z > 0.0D) {
                    z -= increment;
                } else {
                    z += increment;
                }
            }
        }
        
        event.x = x;
        event.y = y;
        event.z = z;
        event.cancel();
    });

    public static boolean isOffsetBBEmpty(double x, double y, double z) {
        return mc.world.getCollisionBoxes(mc.player, mc.player.getEntityBoundingBox().offset(x, y, z)).isEmpty();
    }
}
