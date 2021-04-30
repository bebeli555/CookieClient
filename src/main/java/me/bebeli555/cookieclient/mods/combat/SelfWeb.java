package me.bebeli555.cookieclient.mods.combat;

import me.bebeli555.cookieclient.Mod;
import me.bebeli555.cookieclient.gui.Group;
import me.bebeli555.cookieclient.gui.Mode;
import me.bebeli555.cookieclient.gui.Setting;
import me.bebeli555.cookieclient.utils.BlockUtil;
import me.bebeli555.cookieclient.utils.InventoryUtil;
import me.bebeli555.cookieclient.utils.PlayerUtil;
import me.bebeli555.cookieclient.utils.RotationUtil;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent.ClientTickEvent;

public class SelfWeb extends Mod {
	public static Setting toggle = new Setting(Mode.BOOLEAN, "Toggle", false, "Toggle the module off after the place");
	public static Setting autoDetect = new Setting(Mode.BOOLEAN, "AutoDetect", false, "Detects when a nearby player is trying", "To enter your hole and then places the web");
	
	public SelfWeb() {
		super(Group.COMBAT, "SelfWeb", "Places a web inside you");
	}
	
	@Override
	public void onDisabled() {
		RotationUtil.stopRotating();
	}
	
	@SubscribeEvent
	public void onTick(ClientTickEvent e) {
		if (mc.player == null || getBlock(getPlayerPos()) == Blocks.WEB) {
			return;
		}
		
		if (!InventoryUtil.hasBlock(Blocks.WEB)) {
			sendMessage("You have no webs", true);
			disable();
			return;
		}
		
		//Return if autoDetect is true and the player is not trying to enter the hole
		if (autoDetect.booleanValue()) {
			EntityPlayer player = PlayerUtil.getClosest();

			if (player == null || player.posY - mc.player.posY < 0.25 || Math.abs(mc.player.posX - player.posX) > 2 || Math.abs(mc.player.posZ - player.posZ) > 2) {
				return;
			}
		}
		
		//Place web
		BlockUtil.placeBlockOnThisThread(Blocks.WEB, getPlayerPos(), true);
		RotationUtil.stopRotating();
		
		//Toggle off if toggle is true
		if (toggle.booleanValue()) {
			disable();
		}
	}
}
