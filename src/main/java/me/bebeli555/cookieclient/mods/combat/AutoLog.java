package me.bebeli555.cookieclient.mods.combat;

import me.bebeli555.cookieclient.Mod;
import me.bebeli555.cookieclient.events.bus.EventHandler;
import me.bebeli555.cookieclient.events.bus.Listener;
import me.bebeli555.cookieclient.events.entity.EntityAddedEvent;
import me.bebeli555.cookieclient.events.other.PacketEvent;
import me.bebeli555.cookieclient.gui.Group;
import me.bebeli555.cookieclient.gui.Mode;
import me.bebeli555.cookieclient.gui.Setting;
import me.bebeli555.cookieclient.mods.misc.Friends;
import me.bebeli555.cookieclient.utils.InventoryUtil;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Items;
import net.minecraft.network.play.server.SPacketEntityStatus;
import net.minecraft.util.text.TextComponentString;
import net.minecraftforge.event.entity.living.LivingHurtEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

public class AutoLog extends Mod {
	public static Setting mode = new Setting(null, "Mode", "TotemPop", new String[]{"TotemPop"}, new String[]{"OnRender"}, new String[]{"Health"});
		public static Setting onRenderFriend = new Setting(mode, "OnRender", Mode.BOOLEAN, "Friend", true, "Log even if its a friend");
		public static Setting onPop = new Setting(mode, "TotemPop", Mode.BOOLEAN, "OnPop", true, "Logs when u pop a totem");
		public static Setting onPopCount = new Setting(mode, "TotemPop", Mode.INTEGER, "Count", 1, "If ur inventory has less or equal to this amount", "Of totems when u pop then it logs", "Turn off OnPop if u wanna use this");
		public static Setting onDamageHealth = new Setting(mode, "Health", Mode.INTEGER, "Health", 20, "If ur health is less or equal to this", "When you take damage then it logs");
	
	public AutoLog() {
		super(Group.COMBAT, "AutoLog", "Logs out of the server if the", "Given parameters are reached");
	}

	@SubscribeEvent
	public void onDamage(LivingHurtEvent event) {
		if (event.getEntity().equals(mc.player)) {
			if (mode.stringValue().equals("Health") && mc.player.getHealth() <= onDamageHealth.intValue()) {
				log("CookieClient AutoLog - Your health was lover than the given min health");
			}
		}
	}
	
	@EventHandler
	private Listener<PacketEvent> packetEvent = new Listener<>(event -> {
		if (event.packet instanceof SPacketEntityStatus) {
			SPacketEntityStatus packet = (SPacketEntityStatus)event.packet;
			
			if (packet.getOpCode() == 35 && packet.getEntity(mc.world) == mc.player) {
				if (mode.stringValue().equals("TotemPop")) {
					if (onPop.booleanValue() || InventoryUtil.getAmountOfItem(Items.TOTEM_OF_UNDYING) <= onPopCount.intValue()) {
						if (onPop.booleanValue()) {
							log("CookieClient AutoLog - You popped a totem");
						} else {
							log("CookieClient AutoLog - less or equal amount of totems as TotemPopCount");
						}
					}
				}
			}
		}
	});
	
    @EventHandler
    private Listener<EntityAddedEvent> onEntityAdded = new Listener<>(event -> {
    	if (event.entity instanceof EntityPlayer && mode.stringValue().equals("OnRender")) {
    		if (Friends.isFriend(event.entity) && !onRenderFriend.booleanValue()) {
    			return;
    		}
    		
    		log("CookieClient AutoLog - " + event.entity.getName() + " entered render distance");
    	}
    });
    
	public void log(String message) {
		if (mc.player == null || mc.player.connection == null) {
			return;
		}
		
		mc.player.connection.getNetworkManager().closeChannel(new TextComponentString(message));
		disable();
	}
}
