package me.bebeli555.cookieclient.mods.world;

import me.bebeli555.cookieclient.Mod;
import me.bebeli555.cookieclient.events.bus.EventHandler;
import me.bebeli555.cookieclient.events.bus.Listener;
import me.bebeli555.cookieclient.events.entity.GetEntitiesEvent;
import me.bebeli555.cookieclient.gui.Group;
import me.bebeli555.cookieclient.gui.Mode;
import me.bebeli555.cookieclient.gui.Setting;
import net.minecraft.item.Item;
import net.minecraft.item.ItemTool;

public class NoEntityTrace extends Mod {
	public static Setting toolsOnly = new Setting(Mode.BOOLEAN, "ToolsOnly", true, "Only works if ur holding a tool", "(Pickaxe, Axe, Shovel)");
	
	public NoEntityTrace() {
		super(Group.WORLD, "NoEntityTrace", "Allows you to mine through entities");
	}
	
	@Override
	public void onEnabled() {
		Mod.EVENT_BUS.subscribe(this);
	}
	
	@Override
	public void onDisabled() {
		Mod.EVENT_BUS.unsubscribe(this);
	}
	
    @EventHandler
    private Listener<GetEntitiesEvent> getEntities = new Listener<>(event -> {
    	Item item = mc.player.getHeldItemMainhand().getItem();
        if (toolsOnly.booleanValue() && !(item instanceof ItemTool)) {
        	return;
        }
    	
        event.cancel();
    });
}
