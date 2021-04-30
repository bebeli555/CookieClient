package me.bebeli555.cookieclient.mods.bots.elytrabot;

import net.minecraft.client.Minecraft;
import net.minecraft.util.EnumFacing;

/**
 * A list of directions including diagonal directions
 * P = Plus
 * M = Minus
 */
public enum Direction {
	XP("X-Plus"),
	XM("X-Minus"),
	ZP("Z-Plus"),
	ZM("Z-Minus"),
	XP_ZP("X-Plus, Z-Plus"),
	XM_ZP("X-Minus, Z-Plus"),
	XM_ZM("X-Minus, Z-Minus"),
	XP_ZM("X-Plus, Z-Minus");
	
	public String name;
	Direction(String name) {
		this.name = name;
	}
	
	/**
	 * Gets the direction the player is looking at
	 */
	public static Direction getDirection() {
		EnumFacing facing = Minecraft.getMinecraft().player.getHorizontalFacing();
		return facing == EnumFacing.NORTH ? ZM : facing == EnumFacing.WEST ? XM : facing == EnumFacing.SOUTH ? ZP : XP;
	}
	
	/**
	 * Gets the closest diagonal direction player is looking at
	 */
	public static Direction getDiagonalDirection() {
		EnumFacing facing = Minecraft.getMinecraft().player.getHorizontalFacing();
		
		if (facing.equals(EnumFacing.NORTH)) {
			double closest = getClosest(135, -135);
			return closest == -135 ? XP_ZM : XM_ZM;
		} else if (facing.equals(EnumFacing.WEST)) {
			double closest = getClosest(135, 45);
			return closest == 135 ? XM_ZM : XM_ZP;
		} else if (facing.equals(EnumFacing.EAST)) {
			double closest = getClosest(-45, -135);
			return closest == -135 ? XP_ZM : XP_ZP;
		} else {
			double closest = getClosest(45, -45);
			return closest == 45 ? XM_ZP : XP_ZP;
		}
	}
	
	//Returns the closer given yaw to the real yaw from a and b
	private static double getClosest(double a, double b) {
		double yaw = Minecraft.getMinecraft().player.rotationYaw;
		yaw = yaw < -180 ? yaw += 360 : yaw > 180 ? yaw -= 360 : yaw;
		
		if (Math.abs(yaw - a) < Math.abs(yaw - b)) {
			return a;
		} else {
			return b;
		}
	}
}