package me.bebeli555.cookieclient.mods.misc;

import org.lwjgl.input.Mouse;

import com.mojang.realmsclient.gui.ChatFormatting;

import me.bebeli555.cookieclient.Mod;
import me.bebeli555.cookieclient.gui.Group;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.math.RayTraceResult;
import net.minecraftforge.client.event.MouseEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

public class MiddleClickFriends extends Mod {
	private boolean clicked;
	
	public MiddleClickFriends() {
		super(Group.MISC, "MiddleClickFriends", "Add and remove friends by middleclicking", "On their players");
	}
	
	@SubscribeEvent
	public void onClick(MouseEvent e) {
		if (mc.currentScreen != null) {
			return;
		}
		
		if (!Mouse.isButtonDown(2)) {
			clicked = false;
			return;
		}
		
		if (!clicked) {
			clicked = true;
			
			if (!Friends.toggled) {
				return;
			}

			RayTraceResult result = mc.objectMouseOver;

			if (result == null || result.typeOfHit != RayTraceResult.Type.ENTITY) {
				return;
			}

			Entity entity = result.entityHit;

			if (entity == null || !(entity instanceof EntityPlayer)) {
				return;
			}

			if (Friends.friends.contains(entity.getName())) {
				Friends.removeFriend(entity.getName());
				sendMessage(ChatFormatting.RED + "Removed " + entity.getName(), false);
			} else {
				Friends.addFriend(entity.getName());
				sendMessage(ChatFormatting.GREEN + "Added " + entity.getName(), false);
			}
		}
	}
}
