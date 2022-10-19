package me.bebeli555.cookieclient.mods.combat;

import me.bebeli555.cookieclient.Mod;
import me.bebeli555.cookieclient.events.bus.EventHandler;
import me.bebeli555.cookieclient.events.bus.Listener;
import me.bebeli555.cookieclient.events.player.PlayerUpdateEvent;
import me.bebeli555.cookieclient.gui.Group;
import me.bebeli555.cookieclient.gui.Mode;
import me.bebeli555.cookieclient.gui.Setting;
import me.bebeli555.cookieclient.utils.InventoryUtil;
import net.minecraft.init.Items;
import net.minecraft.item.Item;

public class Offhand extends Mod {
	public static boolean autoTotem, toggleBackOn;
	public static Offhand instance;
	
	public static Setting mode = new Setting(null, "Mode", "Gap", new String[]{"Gap"}, new String[]{"Crystal"}, new String[]{"Bow"}, new String[]{"XP"});
	public static Setting toggleHealth = new Setting(Mode.DOUBLE, "ToggleHealth", 5, "When your health goes below or equal to this amount", "It will toggle this off and enabled autototem", "If you had it on previously");
	public static Setting toggleBack = new Setting(Mode.BOOLEAN, "ToggleBack", false, "If offhand gets toggled off because low health", "Then it will toggle it back on when", "Health is above the set amount above");
	
	public Offhand() {
		super(Group.COMBAT, "Offhand", "Pauses autototem and puts something else to your offhand");
		instance = this;
	}
	
	@Override
	public void onEnabled() {
		autoTotem = AutoTotem.instance.isOn();
		if (autoTotem) {
			AutoTotem.instance.disable();
		}
	}
	
	@Override
	public void onDisabled() {
		toggleBackOn = false;
		if (autoTotem) {
			AutoTotem.instance.enable();
		}
	}
	
    @EventHandler
    private Listener<PlayerUpdateEvent> onPlayerUpdate = new Listener<>(event -> {
    	if (mc.player.getHealth() + mc.player.getAbsorptionAmount() <= toggleHealth.doubleValue()) {
    		disable();
    		toggleBackOn = toggleBack.booleanValue();
    		return;
    	}
    	
    	if (!AutoTotem.isContainerOpen()) {
    		Item item = null;
    		if (mode.stringValue().equals("Gap")) {
    			item = Items.GOLDEN_APPLE;
    		} else if (mode.stringValue().equals("Crystal")) {
    			item = Items.END_CRYSTAL;
    		} else if (mode.stringValue().equals("Bow")) {
    			item = Items.BOW;
    		} else if (mode.stringValue().equals("XP")) {
    			item = Items.EXPERIENCE_BOTTLE;
    		}
    		
    		if (mc.player.getHeldItemOffhand().getItem() != item) {
    			int slot = InventoryUtil.getSlot(item);
    			
    			if (slot != -1) {
    				InventoryUtil.clickSlot(slot);
    				InventoryUtil.clickSlot(45);
    				InventoryUtil.clickSlot(slot);
    			}
    		}
    	}
    });
}
