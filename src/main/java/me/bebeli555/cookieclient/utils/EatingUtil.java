package me.bebeli555.cookieclient.utils;

import me.bebeli555.cookieclient.Mod;
import net.minecraft.client.settings.KeyBinding;
import net.minecraft.item.Item;
import net.minecraft.util.EnumHand;
import net.minecraftforge.event.entity.living.LivingEntityUseItemEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

public class EatingUtil extends Mod {
	private static boolean eating;
	private static long eatingMs;
	
	//Eats the given item if u have it
	public static boolean eatItem(Item item, boolean sleepUntilDone) {
		if (!InventoryUtil.hasItem(item)) {
			return false;
		}
		
		InventoryUtil.switchItem(InventoryUtil.getSlot(item), false);
		
		if (mc.player.inventory.getCurrentItem().getItem() == item) {
			if (mc.currentScreen != null) {
				mc.playerController.processRightClick(mc.player, mc.world, EnumHand.MAIN_HAND); 
			} else {
				KeyBinding.setKeyBindState(mc.gameSettings.keyBindUseItem.getKeyCode(), true);
			}
			eating = true;
			
			//Set eating to false after a timeout in case the event never got called
			if (!sleepUntilDone) {
				new Thread() {
					public void run() {
						eatingMs = System.currentTimeMillis();
						long check = eatingMs;
						
						sleepUntil(() -> !eating, 5000);
						if (eatingMs == check) {
							eating = false;
						}
					}
				}.start();
			} 
			
			//Sleep this thread if the sleepUntilDone is true
			else {
				sleepUntil(() -> !eating, 5000);
				eating = false;
			}
			
			return true;
		} else {
			return false;
		}
	}
	
	public static boolean isEating() {
		return eating;
	}
	
	//Stop pressing key when ate food
	@SubscribeEvent
	public void finishedEating(LivingEntityUseItemEvent.Finish e) {
		if (eating) {
			KeyBinding.setKeyBindState(mc.gameSettings.keyBindUseItem.getKeyCode(), false);
			eating = false;
		}
	}
}
