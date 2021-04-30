package me.bebeli555.cookieclient.mods.world;

import me.bebeli555.cookieclient.Mod;
import me.bebeli555.cookieclient.events.other.ProcessRightClickBlockEvent;
import me.bebeli555.cookieclient.events.player.PlayerDestroyBlockEvent;
import me.bebeli555.cookieclient.gui.Group;
import me.bebeli555.cookieclient.gui.Mode;
import me.bebeli555.cookieclient.gui.Setting;
import me.zero.alpine.listener.EventHandler;
import me.zero.alpine.listener.Listener;
import net.minecraft.network.play.client.CPacketPlayerTryUseItemOnBlock;

public class NoGlitchBlocks extends Mod {
	public static Setting place = new Setting(Mode.BOOLEAN, "Place", true);
	public static Setting destroy = new Setting(Mode.BOOLEAN, "Destroy", true);
	
	public NoGlitchBlocks() {
		super(Group.WORLD, "NoGlitchBlocks", "Only places and destroys blocks after the server has confirmed it");
	}
	
    @EventHandler
    private Listener<PlayerDestroyBlockEvent> onPlayerDestroyBlock = new Listener<>(event -> {
    	if (destroy.booleanValue()) {
    		event.cancel();
    	}
    });
    
    @EventHandler
    private Listener<ProcessRightClickBlockEvent> onRightClickBlock = new Listener<>(event -> {
    	if (place.booleanValue()) {
    		event.cancel();
    		
            float f = (float)(event.vec.x - (double)event.pos.getX());
            float f1 = (float)(event.vec.y - (double)event.pos.getY());
            float f2 = (float)(event.vec.z - (double)event.pos.getZ());
            mc.player.connection.sendPacket(new CPacketPlayerTryUseItemOnBlock(event.pos, event.facing, event.hand, f, f1, f2));
    	}
    });
}
