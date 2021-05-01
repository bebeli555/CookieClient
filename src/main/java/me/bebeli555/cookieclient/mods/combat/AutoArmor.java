package me.bebeli555.cookieclient.mods.combat;

import me.bebeli555.cookieclient.Mod;
import me.bebeli555.cookieclient.gui.Group;
import me.bebeli555.cookieclient.gui.Mode;
import me.bebeli555.cookieclient.gui.Setting;
import me.bebeli555.cookieclient.utils.InventoryUtil;
import me.bebeli555.cookieclient.utils.InventoryUtil.ItemStackUtil;
import net.minecraft.init.Items;
import net.minecraft.item.Item;

public class AutoArmor extends Mod {
	private static Thread thread;
	
	public static Setting delay = new Setting(Mode.INTEGER, "Delay", 200, "Delay in ms to wait between placing armor");
	
	public AutoArmor() {
		super(Group.COMBAT, "AutoArmor", "Automatically wears armor if u have any");
	}
	
	@Override
	public void onEnabled() {
		thread = new Thread() {
			public void run() {
				while(thread != null && thread.equals(this)) {
					loop();
					
					Mod.sleep(150);
				}
			}
		};
		
		thread.start();
	}
	
	@Override
	public void onDisabled() {
		thread = null;
	}
	
	public void loop() {
		if (mc.player == null || AutoTotem.isContainerOpen()) {
			return;
		}
		
		//Helmet
		if (InventoryUtil.getItemStack(39).getItem() == Items.AIR) {
			switchSlot(getBestSlot(new Item[]{Items.DIAMOND_HELMET, Items.IRON_HELMET, Items.CHAINMAIL_HELMET, Items.GOLDEN_HELMET, Items.LEATHER_HELMET}), 39);
		}
		
		//Chestplate
		if (InventoryUtil.getItemStack(38).getItem() == Items.AIR) {
			switchSlot(getBestSlot(new Item[]{Items.DIAMOND_CHESTPLATE, Items.IRON_CHESTPLATE, Items.CHAINMAIL_CHESTPLATE, Items.GOLDEN_CHESTPLATE, Items.LEATHER_CHESTPLATE}), 38);
		}
		
		//Leggings
		if (InventoryUtil.getItemStack(37).getItem() == Items.AIR) {
			switchSlot(getBestSlot(new Item[]{Items.DIAMOND_LEGGINGS, Items.IRON_LEGGINGS, Items.CHAINMAIL_LEGGINGS, Items.GOLDEN_LEGGINGS, Items.LEATHER_LEGGINGS}), 37);
		}
		
		//Boots
		if (InventoryUtil.getItemStack(36).getItem() == Items.AIR) {
			switchSlot(getBestSlot(new Item[]{Items.DIAMOND_BOOTS, Items.IRON_BOOTS, Items.CHAINMAIL_BOOTS, Items.GOLDEN_BOOTS, Items.LEATHER_BOOTS}), 36);
		}
	}
	
	public void switchSlot(int slot, int slot2) {
		if (slot == -1) {
			return;
		}
		
		InventoryUtil.clickSlot(slot);
		InventoryUtil.clickSlot(slot2);
		sleep(delay.intValue());
	}

	public static int getBestSlot(Item[] items) {
		for (Item item : items) {
			for (ItemStackUtil itemStack : InventoryUtil.getAllItems()) {
				if (itemStack.itemStack.getItem() == item) {
					return itemStack.slotId;
				}
			}
		}
		
		return -1;
	}
}
