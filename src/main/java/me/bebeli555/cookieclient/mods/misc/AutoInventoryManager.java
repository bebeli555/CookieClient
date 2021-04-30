package me.bebeli555.cookieclient.mods.misc;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStreamWriter;
import java.util.ArrayList;
import java.util.Scanner;

import me.bebeli555.cookieclient.Mod;
import me.bebeli555.cookieclient.gui.Group;
import me.bebeli555.cookieclient.gui.GuiNode;
import me.bebeli555.cookieclient.gui.Mode;
import me.bebeli555.cookieclient.gui.Setting;
import me.bebeli555.cookieclient.gui.Settings;
import me.bebeli555.cookieclient.gui.GuiNode.ClickListener;
import me.bebeli555.cookieclient.utils.InventoryUtil;
import me.bebeli555.cookieclient.utils.InventoryUtil.ItemStackUtil;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;

public class AutoInventoryManager extends Mod {
	public static Thread thread;
	public static ArrayList<ItemUtil> layout = new ArrayList<ItemUtil>();
	
	public static Setting saveLayout = new Setting(Mode.BOOLEAN, "SaveLayout", false, "Saves the current inventory layout");
	public static Setting delay = new Setting(Mode.INTEGER, "Delay", 150, "Delay in ms between the slot clicks");
	
	public AutoInventoryManager() {
		super(Group.MISC, "AutoInventoryManager", "Keeps items in ur inventry in the same layout", "As the one you have saved");
	}
	
	@Override
	public void onPostInit() {
		GuiNode node = Settings.getGuiNodeFromId(saveLayout.id);
		
		node.addClickListener(new ClickListener() {
			public void clicked() {
				node.toggled = false;
				node.setSetting();
				
				layout.clear();
				for (ItemStackUtil itemStack : InventoryUtil.getAllItems()) {
					layout.add(new ItemUtil(itemStack.itemStack.getItem(), itemStack.slotId));
				}
				
				saveFile();
			}
		});
	}
	
	@Override
	public void onEnabled() {
		if (layout.isEmpty()) {
			readFile();
			
			if (layout.isEmpty()) {
				return;
			}
		}
		
		thread = new Thread() {
			public void run() {
				while(thread != null && thread.equals(this)) {
					loop();
					
					Mod.sleep(250);
				}
			}
		};
		
		thread.start();
	}

	@Override
	public void onDisabled() {
		suspend(thread);
		thread = null;
	}
	
	public void loop() {
		if (mc.player == null) {
			return;
		}
		
		for (ItemUtil itemUtil : layout) {
			ItemStack current = InventoryUtil.getItemStack(itemUtil.slotId);
			
			if (itemUtil.item != current.getItem()) {				
				outer: for (ItemStackUtil itemStack2 : InventoryUtil.getAllItems()) {
					if (itemStack2.itemStack.getItem() == itemUtil.item) {
						if (itemStack2.slotId != itemUtil.slotId) {
							for (ItemUtil itemUtil2 : layout) {
								if (itemUtil2.item == itemStack2.itemStack.getItem()) {
									if (itemUtil2.slotId == itemStack2.slotId) {
										continue outer;
									}
								}
							}
							
							int otherSlots = mc.player.openContainer.inventorySlots.size() - 46;
							if (otherSlots != 0) {
								otherSlots++;
							}
							
							//If the player is holding an item then put it to a free slot
							if (mc.player.inventory.getCurrentItem().getItem() != Items.AIR) {
								int freeSlot = InventoryUtil.getEmptySlot();
								
								if (freeSlot != -1) {
									InventoryUtil.clickSlot(freeSlot, otherSlots);
								}
							}
							
							InventoryUtil.clickSlot(itemStack2.slotId, otherSlots);
							InventoryUtil.clickSlot(itemUtil.slotId, otherSlots);
							InventoryUtil.clickSlot(itemStack2.slotId, otherSlots);
							sleep(delay.intValue());
							break;
						}
					}
				}
			}
		}		
	}
	
	//Saves the layout to a file
	//0 = SlotID, 1 = ItemID.
	//Prefix change = ,
	public void saveFile() {
		try {
			File file = new File(Settings.path + "/AutoInventoryManager.txt");
			file.delete();
			file.createNewFile();
			
			BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(file)));
			for (ItemUtil itemUtil : layout) {
				bw.write(itemUtil.slotId + "," + Item.getIdFromItem(itemUtil.item));
				bw.newLine();
			}
			 
			bw.close();
			sendMessage("Layout saved successfully", false);	
		} catch (Exception e) {
			e.printStackTrace();
			sendMessage("Error saving layout. More info in ur games log", true);
		}
	}
	
	//Reads the layout file and sets the layout variable
	public void readFile() {
		try {
			File file = new File(Settings.path + "/AutoInventoryManager.txt");
			if (!file.exists()) {
				sendMessage("Save a layout first", true);
				disable();
				return;
			}
			
			Scanner scanner = new Scanner(file);
			layout.clear();

			while(scanner.hasNextLine()) {
				String line = scanner.nextLine();
				
				if (!line.isEmpty()) {
					String[] split = line.split(",");
					layout.add(new ItemUtil(Item.getItemById(Integer.parseInt(split[1])), Integer.parseInt(split[0])));
				}
			}
			
			scanner.close();
		} catch (Exception e) {
			sendMessage("Error reading layout file. More info in ur games log", true);
			e.printStackTrace();
		}
	}
	
	public static class ItemUtil {
		public Item item;
		public int slotId;
		
		public ItemUtil(Item item, int slotId) {
			this.item = item;
			this.slotId = slotId;
		}
	}
}
