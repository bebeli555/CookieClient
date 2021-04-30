package me.bebeli555.cookieclient.mods.combat;

import me.bebeli555.cookieclient.Mod;
import me.bebeli555.cookieclient.events.player.PlayerMoveEvent;
import me.bebeli555.cookieclient.gui.Group;
import me.bebeli555.cookieclient.gui.Mode;
import me.bebeli555.cookieclient.gui.Setting;
import me.bebeli555.cookieclient.utils.InventoryUtil;
import me.zero.alpine.listener.EventHandler;
import me.zero.alpine.listener.Listener;
import net.minecraft.client.gui.inventory.GuiChest;
import net.minecraft.client.gui.inventory.GuiShulkerBox;
import net.minecraft.init.Items;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent.ClientTickEvent;

public class AutoTotem extends Mod {
	public static Setting stopMovement = new Setting(Mode.BOOLEAN, "StopMovement", false, "Stops ur motion when its trying to replace the totem", "Because some servers like 2b2t will not", "Allow u to click the totem if ur moving");
	
	public AutoTotem() {
		super(Group.COMBAT, "AutoTotem", "Keeps a totem in ur offhand");
	}
	
	@SubscribeEvent
	public void onTick(ClientTickEvent e) {
		if (mc.player == null) {
			return;
		}
		
		if (mc.player.getHeldItemOffhand().getItem() != Items.TOTEM_OF_UNDYING && InventoryUtil.hasItem(Items.TOTEM_OF_UNDYING) && !(mc.currentScreen instanceof GuiChest) && !(mc.currentScreen instanceof GuiShulkerBox)) {
			if (stopMovement.booleanValue()) {
				NoMovement.toggle(true);
			}
			
			InventoryUtil.clickSlot(InventoryUtil.getSlot(Items.TOTEM_OF_UNDYING));
			InventoryUtil.clickSlot(45);
		} else {
			NoMovement.toggle(false);
		}
		
		this.setRenderNumber(InventoryUtil.getAmountOfItem(Items.TOTEM_OF_UNDYING));
		if (mc.player.getHeldItemOffhand().getItem() == Items.TOTEM_OF_UNDYING) {
			this.setRenderNumber(this.getRenderNumber() + 1);
		}
	}
	
	//Cancels all movement
	public static class NoMovement {
		private static NoMovement noMovement = new NoMovement();

		/**
		 * When toggled on it will not allow movement until toggled off.
		 */
		public static void toggle(boolean on) {
			if (on) {
				Mod.EVENT_BUS.subscribe(noMovement);
			} else {
				Mod.EVENT_BUS.unsubscribe(noMovement);
			}
		}
		
		@EventHandler
		private Listener<PlayerMoveEvent> moveEvent = new Listener<>(event -> {
			event.cancel();
		});
	}
}
