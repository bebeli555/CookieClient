package me.bebeli555.cookieclient.mods.misc;

import java.util.ArrayList;

import com.mojang.realmsclient.gui.ChatFormatting;

import me.bebeli555.cookieclient.Mod;
import me.bebeli555.cookieclient.events.bus.EventHandler;
import me.bebeli555.cookieclient.events.bus.Listener;
import me.bebeli555.cookieclient.events.entity.EntityAddedEvent;
import me.bebeli555.cookieclient.events.entity.EntityRemovedEvent;
import me.bebeli555.cookieclient.gui.Group;
import me.bebeli555.cookieclient.gui.Mode;
import me.bebeli555.cookieclient.gui.Setting;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.SoundEvents;
import net.minecraft.util.SoundCategory;

public class VisualRange extends Mod {
	private static ArrayList<String> entities = new ArrayList<String>();
	
	public static Setting mode = new Setting(null, "Mode", "Message", new String[]{"Message", "Sends a message in chat"}, new String[]{"Sound", "Plays xp sound"}, new String[]{"Both", "Message and sound"});
	public static Setting friends = new Setting(Mode.BOOLEAN, "Friends", true, "Notifies if the player is friend");
	public static Setting enter = new Setting(Mode.BOOLEAN, "Enter", true, "Notifies when they enter visual range");
	public static Setting leave = new Setting(Mode.BOOLEAN, "Leave", true, "Notifies when they leave visual range");
	
	public VisualRange() {
		super(Group.MISC, "VisualRange", "Notifies you when people", "Enter and leave ur visual distance");
	}
	
    @EventHandler
    private Listener<EntityAddedEvent> onEntityAdded = new Listener<>(event -> {
    	if (!isValid(event.entity)) {
    		return;
    	}
    	
    	if (!entities.contains(event.entity.getName())) {
    		entities.add(event.entity.getName());
    	} else {
    		return;
    	}
    	
    	if (enter.booleanValue()) {
    		notify(event.entity, true);
    	}
    });
    
    @EventHandler
    private Listener<EntityRemovedEvent> onEntityRemoved = new Listener<>(event -> {
    	if (!isValid(event.entity)) {
    		return;
    	}
    	
    	if (entities.contains(event.entity.getName())) {
    		entities.remove(event.entity.getName());
    	} else {
    		return;
    	}
    	
    	if (leave.booleanValue()) {
    		notify(event.entity, false);
    	}
    });
    
    public void notify(Entity entity, boolean enter) {	
		String message = "";
		if (Friends.isFriend(entity)) {
			message = ChatFormatting.AQUA + entity.getName();
		} else {
			message = ChatFormatting.GRAY + entity.getName();
		}

		if (enter) {
			message += ChatFormatting.GREEN + " entered";
		} else {
			message += ChatFormatting.RED + " left";
		}

		if (mode.stringValue().equals("Message") || mode.stringValue().equals("Both")) {
			sendMessage(message, false);
		}

		if (mode.stringValue().equals("Sound") || mode.stringValue().equals("Both")) {
			try {
				if (enter) {
					mc.world.playSound(getPlayerPos(), SoundEvents.ENTITY_PLAYER_LEVELUP, SoundCategory.AMBIENT, 150.0f, 10.0F, true);
				} else {
					mc.world.playSound(getPlayerPos(), SoundEvents.ENTITY_EXPERIENCE_ORB_PICKUP, SoundCategory.NEUTRAL, 150.0f, 10.0F, true);
				}
			} catch (Exception ignored) {
				
			}
		}

    }
    
    public boolean isValid(Entity entity) {
    	if (mc.player == null || !(entity instanceof EntityPlayer)) {
    		return false;
    	}
    	
		if (entity.isEntityEqual(mc.player) || Friends.isFriend(entity) && !friends.booleanValue() || entity.getName().equals(mc.player.getName())) {
			return false;
		}
		
		//Fakeplayer
		if (entity.getEntityId() == -100) {
			return false;
		}
		
		return true;
    }
}
