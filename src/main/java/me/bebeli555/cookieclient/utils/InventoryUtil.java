package me.bebeli555.cookieclient.utils;

import java.util.ArrayList;

import me.bebeli555.cookieclient.Mod;
import net.minecraft.block.Block;
import net.minecraft.init.Items;
import net.minecraft.inventory.ClickType;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;

public class InventoryUtil extends Mod {
	
	/**
	 * Gets the slot id for the slot where the hand is currently
	 */
	public static int getHandSlot() {
		return mc.player.inventory.currentItem;
	}
	
	/**
	 * Get slot id for this block if its on inventory
	 */
	public static int getSlot(Block block) {
		try {
			for (ItemStackUtil itemStack : getAllItems()) {
				if (Block.getBlockFromItem(itemStack.itemStack.getItem()).equals(block)) {
					return itemStack.slotId;
				}
			}
		} catch (Exception ignored) {
			
		}
		
		return -1;
	}
	
	/**
	 * Get slot id for this item if its on inventory
	 */
	public static int getSlot(Item item) {
		try {
			for (ItemStackUtil itemStack : getAllItems()) {
				if (itemStack.itemStack.getItem().equals(item)) {
					return itemStack.slotId;
				}
			}
		} catch (Exception ignored) {
			
		}
		
		return -1;
	}
	
	/**
	 * Clicks the inventory slot with given id
	 */
	public static void clickSlot(int id) {
		if (id != -1) {
			try {
				mc.playerController.windowClick(mc.player.openContainer.windowId, getClickSlot(id), 0, ClickType.PICKUP, mc.player);
			} catch (Exception ignored) {
				
			}
		}
	}

	/**
	 * Clicks the inventory slot with given id
	 * @otherRows How many other rows is present like shulker has 27 but you gotta put 18 here if shulker because thats how it works.
	 */
	public static void clickSlot(int id, int otherRows) {
		if (id != -1) {
			try {
				mc.playerController.windowClick(mc.player.openContainer.windowId, getClickSlot(id) + otherRows, 0, ClickType.PICKUP, mc.player);
			} catch (Exception ignored) {
				
			}
		}
	}
	
	/**
	 * Returns the click slot because the slots you click and the other slots are with different ids for some reason.
	 */
	public static int getClickSlot(int id) {
		if (id == -1) {
			return id;
		}
		
		if (id < 9) {
			id += 36;
			return id;
		}
		
		if (id == 39) {
			id = 5;
		} else if (id == 38) {
			id = 6;
		} else if (id == 37) {
			id = 7;
		} else if (id == 36) {
			id = 8;
		} else if (id == 40) {
			id = 45;
		}
		
		return id;
	}
	
	/**
	 * Switches the hand to the given slot or puts the item there if it aint in hotbar
	 */
	public static void switchItem(int slot, boolean sleep) {
		int oldSlot = mc.player.inventory.currentItem;
		if (slot < 9) {
			mc.player.inventory.currentItem = slot;
		} else {
			int freeSlot = 8;
			for (int i = 0; i < 9; i++) {
				if (getItemStack(i).getItem() == Items.AIR) {
					freeSlot = i;
					break;
				}
			}
			
			clickSlot(slot);
			if (sleep) sleep(200);
			clickSlot(freeSlot);
			if (sleep) sleep(200);
			clickSlot(slot);
			if (sleep) sleep(200);
			mc.player.inventory.currentItem = freeSlot;
			if (sleep) sleep(100);
		}
		
		if (oldSlot != mc.player.inventory.currentItem) {
			mc.playerController.updateController();
		}
	}
	
	/**
	 * @return the ItemStack in the given slotid
	 */
	public static ItemStack getItemStack(int id) {
		try {
			return mc.player.inventory.getStackInSlot(id);
		} catch (NullPointerException e) {
			return null;
		}
	}
	
	/**
	 * Gets the amount of the given items u have in ur inventory
	 */
	public static int getAmountOfItem(Item item) {
		int count = 0;
		
		for (ItemStackUtil itemStack : getAllItems()) {
			if (itemStack.itemStack != null && itemStack.itemStack.getItem().equals(item)) {
				count += itemStack.itemStack.getCount();
			}
		}
		
		return count;
	}
	
	/**
	 * Get the amount of the given blocks u have in inventory
	 */
	public static int getAmountOfBlock(Block block) {
		int count = 0;
		
		for (ItemStackUtil itemStack : getAllItems()) {
			if (Block.getBlockFromItem(itemStack.itemStack.getItem()).equals(block)) {
				count += itemStack.itemStack.getCount();
			}
		}
		
		return count;
	}
	
	/**
	 * Checks if u have the given item
	 */
	public static boolean hasItem(Item item) {
		return getAmountOfItem(item) != 0;
	}
	
	/**
	 * Check if hotbar has the given item
	 */
	public static boolean hasHotbarItem(Item item) {
		for (int i = 0; i < 9; i++) {
			if (getItemStack(i).getItem() == item) {
				return true;
			}
		}
		
		return false;
	}
	
	/**
	 * Gets slot for this item in hotbar
	 */
	public static int getSlotInHotbar(Item item) {
		for (int i = 0; i < 9; i++) {
			if (getItemStack(i).getItem() == item) {
				return i;
			}
		}
		
		return -1;
	}
	
	/**
	 * Check if ur inventory contains the given block
	 */
	public static boolean hasBlock(Block block) {
		return getSlot(block) != -1;
	}
	
	/**
	 * Checks if the players inventory is full
	 */
	public static boolean isFull() {
		for (ItemStackUtil itemStack : getAllItems()) {
			if (itemStack.itemStack.getItem() == Items.AIR) {
				return false;
			}
		}
		
		return true;
	}
	
	/**
	 * Gets the amount of empty slots in your inventory
	 */
	public static int getEmptySlots() {
		int count = 0;
		for (ItemStackUtil itemStack : getAllItems()) {
			if (itemStack.itemStack.getItem() == Items.AIR) {
				count++;
			}
		}

		return count;
	}
	
	/**
	 * Returns the slot id of an empty slot in ur inventory
	 */
	public static int getEmptySlot() {
		for (ItemStackUtil itemStack : getAllItems()) {
			if (itemStack.itemStack.getItem() == Items.AIR) {
				return itemStack.slotId;
			}
		}
		
		return -1;
	}
	
	/**
	 * @return a list of all items in your inventory
	 */
	public static ArrayList<ItemStackUtil> getAllItems() {
		ArrayList<ItemStackUtil> items = new ArrayList<ItemStackUtil>();

		for (int i = 0; i < 36; i++) {
			items.add(new ItemStackUtil(getItemStack(i), i));
		}
		
		return items;
	}
	
	public static class ItemStackUtil {
		public ItemStack itemStack;
		public int slotId;
		
		public ItemStackUtil(ItemStack itemStack, int slotId) {
			this.itemStack = itemStack;
			this.slotId = slotId;
		}
	}
}
