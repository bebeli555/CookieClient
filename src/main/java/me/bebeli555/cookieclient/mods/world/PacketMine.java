package me.bebeli555.cookieclient.mods.world;

import me.bebeli555.cookieclient.Mod;
import me.bebeli555.cookieclient.events.bus.EventHandler;
import me.bebeli555.cookieclient.events.bus.Listener;
import me.bebeli555.cookieclient.events.player.PlayerDamageBlockEvent;
import me.bebeli555.cookieclient.events.player.PlayerMotionUpdateEvent;
import me.bebeli555.cookieclient.gui.Group;
import net.minecraft.network.play.client.CPacketPlayerDigging;
import net.minecraft.util.EnumHand;

public class PacketMine extends Mod {
	public PacketMine() {
		super(Group.WORLD, "PacketMine", "Mine using packets");
	}
	
	@EventHandler
	private Listener<PlayerMotionUpdateEvent> onMotionUpdate = new Listener<>(event -> {
		if (mc.player == null || mc.objectMouseOver == null || mc.objectMouseOver.getBlockPos() == null || mc.objectMouseOver.sideHit == null) {
			return;
		}
		
		if (mc.gameSettings.keyBindAttack.isKeyDown()) {
	        mc.player.connection.sendPacket(new CPacketPlayerDigging(CPacketPlayerDigging.Action.START_DESTROY_BLOCK, mc.objectMouseOver.getBlockPos(), mc.objectMouseOver.sideHit));
	        mc.player.connection.sendPacket(new CPacketPlayerDigging(CPacketPlayerDigging.Action.STOP_DESTROY_BLOCK, mc.objectMouseOver.getBlockPos(), mc.objectMouseOver.sideHit));
	        mc.player.swingArm(EnumHand.MAIN_HAND);
		}
	});
	
	@EventHandler
	private Listener<PlayerDamageBlockEvent> playerDamageBlockEvent = new Listener<>(event -> {
		event.cancel();
	});
}