package me.bebeli555.cookieclient.mods.world;

import me.bebeli555.cookieclient.Mod;
import me.bebeli555.cookieclient.events.bus.EventHandler;
import me.bebeli555.cookieclient.events.bus.Listener;
import me.bebeli555.cookieclient.events.player.PlayerMotionUpdateEvent;
import me.bebeli555.cookieclient.gui.Group;
import me.bebeli555.cookieclient.gui.Mode;
import me.bebeli555.cookieclient.gui.Setting;
import net.minecraft.init.Items;

public class FastUse extends Mod {
	public static Setting xpOnly = new Setting(Mode.BOOLEAN, "XPOnly", false, "Only works for xp");
	
	public FastUse() {
		super(Group.WORLD, "FastUse", "Use items and place blocks with no click delay");
	}
	
    @EventHandler
    private Listener<PlayerMotionUpdateEvent> onPlayerUpdate = new Listener<>(event -> {
    	if (!xpOnly.booleanValue() || xpOnly.booleanValue() && mc.player.getHeldItemMainhand().getItem() == Items.EXPERIENCE_BOTTLE) {
    		mc.rightClickDelayTimer = 0;
    	}
    });
}
