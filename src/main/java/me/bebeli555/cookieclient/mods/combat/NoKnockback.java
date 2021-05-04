package me.bebeli555.cookieclient.mods.combat;

import me.bebeli555.cookieclient.Mod;
import me.bebeli555.cookieclient.events.bus.EventHandler;
import me.bebeli555.cookieclient.events.bus.Listener;
import me.bebeli555.cookieclient.events.entity.EntityPushEvent;
import me.bebeli555.cookieclient.events.other.PacketEvent;
import me.bebeli555.cookieclient.gui.Group;
import net.minecraft.entity.Entity;
import net.minecraft.entity.projectile.EntityFishHook;
import net.minecraft.network.play.server.SPacketEntityStatus;
import net.minecraft.network.play.server.SPacketEntityVelocity;
import net.minecraft.network.play.server.SPacketExplosion;

public class NoKnockback extends Mod {
	public NoKnockback() {
		super(Group.COMBAT, "NoKnockback", "Disables knockback");
	}
	
    @EventHandler
    private Listener<EntityPushEvent> entityPushEvent = new Listener<>(event -> {
    	event.cancel();
    });
    
	@EventHandler
	private Listener<PacketEvent> packetEvent = new Listener<>(event -> {
        if (event.packet instanceof SPacketEntityStatus) {
            SPacketEntityStatus packet = (SPacketEntityStatus)event.packet;
            
            if (packet.getOpCode() == 31) {
                Entity entity = packet.getEntity(mc.world);
                
                if (entity != null && entity instanceof EntityFishHook) {
                    EntityFishHook fishHook = (EntityFishHook) entity;
                    
                    if (fishHook.caughtEntity == mc.player) {
                    	event.cancel();
                    }
                }
            }
        } else if (event.packet instanceof SPacketEntityVelocity) {
        	SPacketEntityVelocity packet = (SPacketEntityVelocity)event.packet;
        	if (mc.player != null && packet.getEntityID() != mc.player.getEntityId()) {
        		return;
        	}
        	
        	event.cancel();
		} else if (event.packet instanceof SPacketExplosion) {
			event.cancel();
		}
	});
}
