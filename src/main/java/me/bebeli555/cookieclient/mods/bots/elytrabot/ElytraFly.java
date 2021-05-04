package me.bebeli555.cookieclient.mods.bots.elytrabot;

import me.bebeli555.cookieclient.Mod;
import me.bebeli555.cookieclient.events.bus.EventHandler;
import me.bebeli555.cookieclient.events.bus.Listener;
import me.bebeli555.cookieclient.events.player.TravelEvent;
import net.minecraft.util.math.BlockPos;

public class ElytraFly extends Mod {
	public static ElytraFly elytraFly = new ElytraFly();
	
	public static void toggle(boolean on) {
		if (on) {
			Mod.EVENT_BUS.subscribe(elytraFly);
		} else {
			Mod.EVENT_BUS.unsubscribe(elytraFly);
		}
	}
	
	/**
	 * Sets the players motion so the player will move to the given blockpos from current pos
	 */
	public static void setMotion(BlockPos pos, BlockPos next, BlockPos previous) {
		double x = 0, y = 0, z = 0;
		double xDiff = (pos.getX() + 0.5) - mc.player.posX;
		double yDiff = (pos.getY() + 0.4) - mc.player.posY;
		double zDiff = (pos.getZ() + 0.5) - mc.player.posZ;
		
		double speed = ElytraBot.elytraFlySpeed.doubleValue();
		
		//If the previous pos is not 0 for 2 coords then it will use the slow speed otherwise the target would probably be missed
		int amount = 0;
		try {
			if (Math.abs(next.getX() - previous.getX()) > 0) amount++;
			if (Math.abs(next.getY() - previous.getY()) > 0) amount++;
			if (Math.abs(next.getZ() - previous.getZ()) > 0) amount++;
			if (amount > 1) {
				speed = ElytraBot.elytraFlyManuverSpeed.doubleValue();
				
				//If the previous and next is both diagonal then use real speed
				if (next.getX() - previous.getX() == next.getZ() - previous.getZ() && next.getY() - previous.getY() == 0) {
					if (xDiff >= 1 && zDiff >= 1 || xDiff <= -1 && zDiff <= -1) {
						speed = ElytraBot.elytraFlySpeed.doubleValue();	
					}
				}
			}
		} catch (Exception nullPointerProbablyIdk) {
			speed = ElytraBot.elytraFlyManuverSpeed.doubleValue();
		}
		
		if ((int)xDiff > 0) {
			x = speed;
		} else if ((int)xDiff < 0) {
			x = -speed;
		}
		
		if ((int)yDiff > 0) {
			y = ElytraBot.elytraFlyManuverSpeed.doubleValue();
		} else if ((int)yDiff < 0) {
			y = -ElytraBot.elytraFlyManuverSpeed.doubleValue();
		}
		
		if ((int)zDiff > 0) {
			z = speed;
		} else if ((int)zDiff < 0) {
			z = -speed;
		}
		
		mc.player.motionX = x;
		mc.player.motionY = y;
		mc.player.motionZ = z;
		
		//Center
		double centerSpeed = 0.2;
		double centerCheck = 0.1;
		if (x == 0) {
			if (xDiff > centerCheck) {
				mc.player.motionX = centerSpeed;
			} else if (xDiff < -centerCheck) {
				mc.player.motionX = -centerSpeed;
			} else {
				mc.player.motionX = 0;
			}
		}
		
		if (y == 0) {
			if (yDiff > centerCheck) {
				mc.player.motionY = centerSpeed;
			} else if (yDiff < -centerCheck) {
				mc.player.motionY = -centerSpeed;
			} else {
				mc.player.motionY = 0;
			}
		}
		
		if (z == 0) {
			if (zDiff > centerCheck) {
				mc.player.motionZ = centerSpeed;
			} else if (zDiff < -centerCheck) {
				mc.player.motionZ = -centerSpeed;
			} else {
				mc.player.motionZ = 0;
			}
		}
	}
	
	@EventHandler
	private Listener<TravelEvent> onTravel = new Listener<>(event -> {
		event.cancel();
	});
}
