package me.bebeli555.cookieclient.events.other;

import me.zero.alpine.type.Cancellable;
import net.minecraft.potion.Potion;

public class IsPotionEffectActiveEvent extends Cancellable {
	public Potion potion;
	
	public IsPotionEffectActiveEvent(Potion potion) {
		this.potion = potion;
	}
}
