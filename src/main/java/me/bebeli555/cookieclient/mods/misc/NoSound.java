package me.bebeli555.cookieclient.mods.misc;

import me.bebeli555.cookieclient.Mod;
import me.bebeli555.cookieclient.gui.Group;
import me.bebeli555.cookieclient.gui.Mode;
import me.bebeli555.cookieclient.gui.Setting;
import net.minecraftforge.client.event.sound.PlaySoundEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

public class NoSound extends Mod {
	public static Setting portal = new Setting(Mode.BOOLEAN, "Portal", false, "Doesnt play the nether portal sounds");
	
	public NoSound() {
		super(Group.MISC, "NoSound", "Prevents some sounds from playing");
	}
	
	@SubscribeEvent
	public void onSound(PlaySoundEvent event) {
		if (portal.booleanValue() && event.getName().equals("block.portal.ambient") || portal.booleanValue() && event.getName().equals("block.portal.travel") 
		|| portal.booleanValue() && event.getName().equals("block.portal.trigger")) {
			event.setResultSound(null);
		}
	}
}
