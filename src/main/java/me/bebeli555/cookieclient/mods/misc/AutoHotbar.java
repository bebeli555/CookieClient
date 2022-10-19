package me.bebeli555.cookieclient.mods.misc;

import me.bebeli555.cookieclient.Mod;
import me.bebeli555.cookieclient.gui.Group;
import me.bebeli555.cookieclient.utils.InventoryUtil;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent.ClientTickEvent;

public class AutoHotbar extends Mod {
	private int lastSlot = -1;
	private Item lastItem;
	
	public AutoHotbar() {
		super(Group.MISC, "AutoHotbar", "When u use a stack from ur hotbar this will automatically", "Put a new stack of the same item to the same slot");
	}
	
	@Override
	public void onDisabled() {
		lastSlot = -1;
		lastItem = null;
	}
	
	@SubscribeEvent
	public void onTick(ClientTickEvent e) {
		if (mc.player == null) {
			return;
		}
		
		Item held = mc.player.getHeldItemMainhand().getItem();
		if (lastSlot == mc.player.inventory.currentItem && lastItem != held && held == Items.AIR && mc.currentScreen == null) {
			if (!mc.gameSettings.keyBindDrop.isKeyDown() || mc.gameSettings.keyBindDrop.isKeyDown() && mc.gameSettings.keyBindUseItem.isKeyDown()) {
				int slot = InventoryUtil.getSlot(lastItem);
				if (slot != -1) {
					InventoryUtil.clickSlot(slot);
					InventoryUtil.clickSlot(mc.player.inventory.currentItem);
				}	
			}
		}
		
		lastSlot = mc.player.inventory.currentItem;
		lastItem = mc.player.getHeldItemMainhand().getItem();
	}
}
