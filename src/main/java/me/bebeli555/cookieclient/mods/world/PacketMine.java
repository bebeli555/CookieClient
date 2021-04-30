package me.bebeli555.cookieclient.mods.world;

import me.bebeli555.cookieclient.Mod;
import me.bebeli555.cookieclient.events.player.PlayerDamageBlockEvent;
import me.bebeli555.cookieclient.events.player.PlayerMotionUpdateEvent;
import me.bebeli555.cookieclient.gui.Group;
import me.zero.alpine.listener.EventHandler;
import me.zero.alpine.listener.Listener;
import net.minecraft.network.play.client.CPacketPlayerDigging;

public class PacketMine extends Mod {
	public PacketMine() {
		super(Group.WORLD, "PacketMine", "Mine using packets");
	}
	
	@EventHandler
	private Listener<PlayerMotionUpdateEvent> onMotionUpdate = new Listener<>(event -> {
		if (mc.player == null || mc.objectMouseOver == null || mc.objectMouseOver.getBlockPos() == null) {
			return;
		}
		
		if (mc.gameSettings.keyBindAttack.isKeyDown()) {
	        mc.player.connection.sendPacket(new CPacketPlayerDigging(CPacketPlayerDigging.Action.START_DESTROY_BLOCK, mc.objectMouseOver.getBlockPos(), mc.objectMouseOver.sideHit));
	        mc.player.connection.sendPacket(new CPacketPlayerDigging(CPacketPlayerDigging.Action.STOP_DESTROY_BLOCK, mc.objectMouseOver.getBlockPos(), mc.objectMouseOver.sideHit));
		}
	});
	
	@EventHandler
	private Listener<PlayerDamageBlockEvent> playerDamageBlockEvent = new Listener<>(event -> {
		event.cancel();
	});
}