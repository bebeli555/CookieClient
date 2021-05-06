package me.bebeli555.cookieclient.mods.combat;

import me.bebeli555.cookieclient.Mod;
import me.bebeli555.cookieclient.events.bus.EventHandler;
import me.bebeli555.cookieclient.events.bus.Listener;
import me.bebeli555.cookieclient.events.player.PlayerMoveEvent;
import me.bebeli555.cookieclient.gui.Group;
import me.bebeli555.cookieclient.gui.Mode;
import me.bebeli555.cookieclient.gui.Setting;
import me.bebeli555.cookieclient.hud.components.ArrayListComponent;
import me.bebeli555.cookieclient.utils.InventoryUtil;
import net.minecraft.client.gui.inventory.GuiChest;
import net.minecraft.client.gui.inventory.GuiShulkerBox;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent.ClientTickEvent;

public class AutoTotem extends Mod {
	public static AutoTotem instance;
	private int lastNumber;
	
	public static Setting stopMovement = new Setting(Mode.BOOLEAN, "StopMovement", false, "Stops ur motion when its trying to replace the totem", "Because some servers like 2b2t will not", "Allow u to click the totem if ur moving");
	
	public AutoTotem() {
		super(Group.COMBAT, "AutoTotem", "Keeps a totem in ur offhand");
		instance = this;
	}
	
	@Override
	public void onEnabled() {
		lastNumber = -1;
		
		//Turn off offhand otherwise their both trying to set stuff to offhand slot
		if (Offhand.instance.isOn()) {
			Offhand.instance.disable();
		}
	}
	
	@SubscribeEvent
	public void onTick(ClientTickEvent e) {
		if (mc.player == null) {
			return;
		}
		
		if (Offhand.toggleBackOn && mc.player.getHealth() + mc.player.getAbsorptionAmount() > Offhand.toggleHealth.doubleValue()) {
			Offhand.instance.enable();
			return;
		}
		
		if (mc.player.getHeldItemOffhand().getItem() != Items.TOTEM_OF_UNDYING && InventoryUtil.hasItem(Items.TOTEM_OF_UNDYING) && !isContainerOpen()) {
			if (stopMovement.booleanValue()) {
				NoMovement.toggle(true);
			}
			
			Item oldItem = mc.player.getHeldItemOffhand().getItem();
			int slot = InventoryUtil.getSlot(Items.TOTEM_OF_UNDYING);
			InventoryUtil.clickSlot(slot);
			InventoryUtil.clickSlot(45);
			if (oldItem != Items.AIR) {
				InventoryUtil.clickSlot(slot);
			}
		} else {
			NoMovement.toggle(false);
		}
		
		this.setRenderNumber(InventoryUtil.getAmountOfItem(Items.TOTEM_OF_UNDYING));
		if (mc.player.getHeldItemOffhand().getItem() == Items.TOTEM_OF_UNDYING) {
			this.setRenderNumber(this.getRenderNumber() + 1);
		}
		
		if (this.getRenderNumber() != lastNumber) {
			//Update arraylist sorting
			ArrayListComponent.lastArraylistSize = -1;
		}
		
		lastNumber = this.getRenderNumber();
	}
	
	/**
	 * Checks if a container is open that is also open server side
	 * Like chest or a shulker box
	 */
	public static boolean isContainerOpen() {
		if (mc.currentScreen != null) {
			return mc.currentScreen instanceof GuiChest || mc.currentScreen instanceof GuiShulkerBox;
		}
		
		return false;
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
