package me.bebeli555.cookieclient.events.other;

import me.bebeli555.cookieclient.events.bus.Cancellable;
import net.minecraft.potion.Potion;

public class IsPotionEffectActiveEvent extends Cancellable {
	public Potion potion;
	
	public IsPotionEffectActiveEvent(Potion potion) {
		this.potion = potion;
	}
}
