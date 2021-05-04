package me.bebeli555.cookieclient.mods.movement;

import java.awt.Color;
import java.util.ArrayList;

import me.bebeli555.cookieclient.Mod;
import me.bebeli555.cookieclient.events.bus.EventHandler;
import me.bebeli555.cookieclient.events.bus.Listener;
import me.bebeli555.cookieclient.events.other.PacketEvent;
import me.bebeli555.cookieclient.gui.Group;
import me.bebeli555.cookieclient.rendering.RenderUtil;
import net.minecraft.client.entity.EntityOtherPlayerMP;
import net.minecraft.network.Packet;
import net.minecraft.network.play.client.CPacketConfirmTeleport;
import net.minecraft.network.play.client.CPacketPlayer;
import net.minecraft.util.math.Vec3d;

public class Blink extends Mod {
	private static EntityOtherPlayerMP original;
	private static ArrayList<Packet<?>> packets = new ArrayList<Packet<?>>();
	private static ArrayList<Vec3d> lines = new ArrayList<Vec3d>();
	
	public Blink() {
		super(Group.MOVEMENT, "Blink", "Holds movement packets until toggled off like fakelag", "(Dont use this for long duration or u will get kicked)");
	}
	
	@Override
	public void onEnabled() {
		original = new EntityOtherPlayerMP(mc.world, mc.session.getProfile());
		original.copyLocationAndAnglesFrom(mc.player);
		original.rotationYaw = mc.player.rotationYaw;
		original.rotationYawHead = mc.player.rotationYawHead;
		original.inventory.copyInventory(mc.player.inventory);
        mc.world.addEntityToWorld(-100, original);
	}
	
	@Override
	public void onDisabled() {
        if (mc.world != null) {
            while (!packets.isEmpty()) {
                mc.getConnection().sendPacket(packets.get(0));
                packets.remove(0);
            }
        }
        
        mc.world.removeEntity(original);
        packets.clear();
        lines.clear();
	}
	
    @EventHandler
    private Listener<PacketEvent> packetEvent = new Listener<>(event -> {
        if (event.packet instanceof CPacketPlayer || event.packet instanceof CPacketConfirmTeleport) {
        	event.cancel();
            packets.add(event.packet);
            
            this.setRenderNumber(packets.size());
            lines.add(new Vec3d(mc.player.posX, mc.player.posY, mc.player.posZ));
        }
    });
    
    @Override
    public void onRenderWorld(float partialTicks) {
    	try {
        	for (int i = 0; i < lines.size(); i++) {
        		RenderUtil.drawLine(lines.get(i), lines.get(i + 1), 1, Color.CYAN);
        	}
    	} catch (Exception e) {
    		
    	}
    }
}
