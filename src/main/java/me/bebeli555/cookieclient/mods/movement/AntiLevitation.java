package me.bebeli555.cookieclient.mods.movement;

import me.bebeli555.cookieclient.Mod;
import me.bebeli555.cookieclient.events.other.IsPotionEffectActiveEvent;
import me.bebeli555.cookieclient.gui.Group;
import me.zero.alpine.listener.EventHandler;
import me.zero.alpine.listener.Listener;
import net.minecraft.init.MobEffects;

public class AntiLevitation extends Mod {
	public AntiLevitation() {
		super(Group.MOVEMENT, "AntiLevitation", "Prevents you from levitating");
	}
	
    @EventHandler
    private Listener<IsPotionEffectActiveEvent> IsPotionActive = new Listener<>(event -> {
        if (event.potion == MobEffects.LEVITATION) {
        	event.cancel();
        }
    });
}
