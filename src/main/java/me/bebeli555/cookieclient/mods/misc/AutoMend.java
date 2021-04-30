package me.bebeli555.cookieclient.mods.misc;

import me.bebeli555.cookieclient.Mod;
import me.bebeli555.cookieclient.gui.Group;
import me.bebeli555.cookieclient.gui.Mode;
import me.bebeli555.cookieclient.gui.Setting;
import me.bebeli555.cookieclient.utils.InventoryUtil;
import me.bebeli555.cookieclient.utils.PlayerUtil;
import me.bebeli555.cookieclient.utils.RotationUtil;
import me.bebeli555.cookieclient.utils.InventoryUtil.ItemStackUtil;
import net.minecraft.entity.EntityLiving;
import net.minecraft.init.Items;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.item.ItemArmor;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemTool;
import net.minecraft.util.math.Vec3d;

public class AutoMend extends Mod {
	public static Thread thread;
	
	public static Setting armor = new Setting(Mode.BOOLEAN, "Armor", true, "Mends your armor");
	public static Setting tools = new Setting(Mode.BOOLEAN, "Tools", false, "Mends your tools like pickaxe and others", "Remember to disable your AutoTotem if you want to use this");
	public static Setting delay = new Setting(Mode.INTEGER, "Delay", 100, "Delay in ms to wait between the clicks on the XP-Bottle");
	
	public AutoMend() {
		super(Group.MISC, "AutoMend", "Mends your armor and tools using xp bottles");
	}
	
	@Override
	public void onEnabled() {
		thread = new Thread() {
			public void run() {
				while(thread != null && thread.equals(this)) {
					loop();
					
					Mod.sleep(50);
				}
			}
		};
		
		thread.start();
	}
	
	@Override
	public void onDisabled() {
		clearStatus();
		suspend(thread);
		thread = null;
	}
	
	public void loop() {
		if (mc.player == null) {
			return;
		}
		
		if (!InventoryUtil.hasItem(Items.EXPERIENCE_BOTTLE)) {
			sendMessage("No xp bottles in inventory", true);
			disable();
			return;
		}
		
		if (!isSolid(getPlayerPos().add(0, -1, 0))) {
			sendMessage("Block below the player is not solid", true);
			disable();
			return;
		}
		
		//Face down so we can do xp and drop items and that kind of stuff
		RotationUtil.rotate(new Vec3d(getPlayerPos().add(0, -1, 0)).add(0.5, 0.5, 0.5), false);
		
		boolean allArmorMended = true;
		if (armor.booleanValue()) {
			ItemStackUtil[] armor = new ItemStackUtil[]{new ItemStackUtil(InventoryUtil.getItemStack(39), 39), new ItemStackUtil(InventoryUtil.getItemStack(38), 38), 
														new ItemStackUtil(InventoryUtil.getItemStack(37), 37), new ItemStackUtil(InventoryUtil.getItemStack(36), 36)};
			boolean mended = false;
			
			//Mend armor or take it off if max durability
			for (ItemStackUtil stack : armor) {
				if (stack.itemStack.getItem() != Items.AIR) {
					if (!isMaxDurability(stack.itemStack)) {
						mended = true;
						setStatus("Mending armor");
						
						for (int i = 0; i < 5; i++) {
							clickOnXp();
						}
					} else {
						int freeSlot = InventoryUtil.getEmptySlot();
						if (freeSlot == -1) {
							InventoryUtil.switchItem(8, false);
							mc.player.dropItem(true);
							freeSlot = 8;
						}
						
						InventoryUtil.clickSlot(stack.slotId);
						sleep(100);
						InventoryUtil.clickSlot(freeSlot);
						sleep(100);
					}
				}
			}
			
			//Put armor back in to mend it
			if (!mended) {
				for (ItemStackUtil itemStack : InventoryUtil.getAllItems()) {
					if (itemStack.itemStack.getItem() instanceof ItemArmor && !isMaxDurability(itemStack.itemStack)) {
						allArmorMended = false;
						
						int slot = 39;
						EntityEquipmentSlot type = EntityLiving.getSlotForItemStack(itemStack.itemStack);
						if (type.equals(EntityEquipmentSlot.CHEST)) {
							slot = 38;
						} else if (type.equals(EntityEquipmentSlot.LEGS)) {
							slot = 37;
						} else if (type.equals(EntityEquipmentSlot.FEET)) {
							slot = 36;
						}
						
						InventoryUtil.clickSlot(itemStack.slotId);
						sleep(100);
						InventoryUtil.clickSlot(slot);
						sleep(100);
					}
				}
			} else {
				allArmorMended = false;
			}
		}
		
		//Mend tools or if everything is mended then toggle off
		if (tools.booleanValue() && allArmorMended) {
			ItemStack offHand = InventoryUtil.getItemStack(40);
			
			if (!isMaxDurability(offHand)) {
				setStatus("Mending tools");
				for (int i = 0; i < 5; i++) {
					clickOnXp();
				}
			} else {
				for (ItemStackUtil itemStack : InventoryUtil.getAllItems()) {
					if (itemStack.itemStack.getItem() instanceof ItemTool && !isMaxDurability(itemStack.itemStack)) {
						InventoryUtil.clickSlot(itemStack.slotId);
						sleep(100);
						InventoryUtil.clickSlot(40);
						sleep(100);
						InventoryUtil.clickSlot(itemStack.slotId);
						sleep(100);
						return;
					}
				}
				
				disable();
			}
		} else if (allArmorMended) {
			disable();
		}
	}
	
	public void clickOnXp() {
		if (mc.player.getHeldItemMainhand().getItem() != Items.EXPERIENCE_BOTTLE) {
			InventoryUtil.switchItem(InventoryUtil.getSlot(Items.EXPERIENCE_BOTTLE), true);
		}
		
		PlayerUtil.rightClick();
		sleep(delay.intValue());
	}
	
	public static int getDurability(ItemStack itemStack) {
		return itemStack.getMaxDamage() - itemStack.getItemDamage();
	}
	
	public static boolean isMaxDurability(ItemStack itemStack) {
		return itemStack.getItemDamage() == 0;
	}
}
