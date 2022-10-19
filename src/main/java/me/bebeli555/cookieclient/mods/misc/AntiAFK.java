package me.bebeli555.cookieclient.mods.misc;

import java.util.ArrayList;

import me.bebeli555.cookieclient.Mod;
import me.bebeli555.cookieclient.gui.Group;
import me.bebeli555.cookieclient.gui.Mode;
import me.bebeli555.cookieclient.gui.Setting;
import net.minecraft.util.EnumHand;

public class AntiAFK extends Mod {
	private static Thread thread;
	
	public static Setting delay = new Setting(Mode.DOUBLE, "Delay", 2, "How often it does the things", "In seconds");
	public static Setting rotate = new Setting(Mode.BOOLEAN, "Rotate", true, "Rotates ur head");
	public static Setting punch = new Setting(Mode.BOOLEAN, "Punch", true, "Swings ur hand");
	public static Setting jump = new Setting(Mode.BOOLEAN, "Jump", true, "Jumps");
	public static Setting random = new Setting(Mode.BOOLEAN, "Random", true, "Chooses one random action to do from the allowed actions", "If false then it does all of them at the same time");
	
	public AntiAFK() {
		super(Group.MISC, "AntiAFK", "Tries to prevent you from", "Getting kicked from servers if you afk");
	}
	
	@Override
	public void onEnabled() {
		thread = new Thread() {
			public void run() {
				while(thread != null && thread.equals(this)) {
					loop();
					
					Mod.sleep((int)(delay.doubleValue() * 1000));
				}
			}
		};
		
		thread.start();
	}
	
	@Override
	public void onDisabled() {
		thread = null;
	}
	
	public void loop() {
		if (mc.player == null) {
			return;
		}
		
		ArrayList<Integer> actions = new ArrayList<Integer>();
		if (rotate.booleanValue()) actions.add(1);
		if (punch.booleanValue()) actions.add(2);
		if (jump.booleanValue()) actions.add(3);
		
		if (!actions.isEmpty()) {
			if (random.booleanValue()) {
				int action = actions.get(random(0, actions.size()));
				doAction(action);
			} else {
				for (int action : actions) {
					doAction(action);
				}
			}
		}
	}
	
	public static void doAction(int id) {
		if (id == 1) {
			mc.player.rotationYaw = random(0, 170);
			mc.player.rotationPitch = random(0, 80);
		} else if (id == 2) {
			mc.player.swingArm(EnumHand.MAIN_HAND);
		} else if (id == 3) {
			mc.player.jump();
		}
	}
}
