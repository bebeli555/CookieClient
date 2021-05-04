package me.bebeli555.cookieclient.mods.world;

import me.bebeli555.cookieclient.Mod;
import me.bebeli555.cookieclient.events.bus.EventHandler;
import me.bebeli555.cookieclient.events.bus.Listener;
import me.bebeli555.cookieclient.events.other.PacketEvent;
import me.bebeli555.cookieclient.gui.Group;
import me.bebeli555.cookieclient.gui.Mode;
import me.bebeli555.cookieclient.gui.Setting;
import me.bebeli555.cookieclient.utils.PlayerUtil;
import me.bebeli555.cookieclient.utils.Timer;
import net.minecraft.network.play.server.SPacketSoundEffect;

public class AutoFish extends Mod {
	private static Thread thread;
	private static boolean splash;
	private static Timer timer = new Timer();
	
	public static Setting castDelay = new Setting(Mode.INTEGER, "CastDelay", 500, "How long to wait in ms before casting the rod again");
	public static Setting catchDelay = new Setting(Mode.INTEGER, "CatchDelay", 500, "How long to wait in ms before taking the fish out");
	
	public AutoFish() {
		super(Group.WORLD, "AutoFish", "Automatically fishes for you");
	}
	
	@Override
	public void onEnabled() {
		thread = new Thread() {
			public void run() {
				while(thread != null && thread.equals(this)) {
					loop();
					
					Mod.sleep(50);
				}
			}
		};
		
		thread.start();
	}
	
	@Override
	public void onDisabled() {
		thread = null;
		splash = false;
	}
	
	public void loop() {
		if (mc.player == null) {
			return;
		}
		
		if (mc.player.fishEntity == null) {
			sleep(castDelay.intValue());
			PlayerUtil.rightClick();
			timer.reset();
		} else if (splash) {
			sleep(catchDelay.intValue());
			PlayerUtil.rightClick();
			splash = false;
		}
	}
	
    @EventHandler
    private Listener<PacketEvent> packetEvent = new Listener<>(event -> {
    	if (event.packet instanceof SPacketSoundEffect) {
    		SPacketSoundEffect packet = (SPacketSoundEffect)event.packet;
    		
    		if (packet.getSound().getSoundName().getPath().contains("entity.bobber.splash") && timer.hasPassed(3500)) {
    			splash = true;
    		}
    	}
    });
}
