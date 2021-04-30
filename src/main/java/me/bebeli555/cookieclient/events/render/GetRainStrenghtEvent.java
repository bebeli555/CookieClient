package me.bebeli555.cookieclient.events.render;

import me.zero.alpine.type.Cancellable;

public class GetRainStrenghtEvent extends Cancellable {
	public float value;
	
	public GetRainStrenghtEvent(float value) {
		this.value = value;
	}
}
