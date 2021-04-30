package me.bebeli555.cookieclient.utils;

import java.util.ArrayList;

import me.bebeli555.cookieclient.Mod;
import me.bebeli555.cookieclient.mods.misc.Friends;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent.ClientTickEvent;

public class PlayerUtil extends Mod {
	private static PlayerUtil playerUtil = new PlayerUtil();
	
	/**
	 * Gets all the players the client knows except yourself.
	 */
	public static ArrayList<EntityPlayer> getAll() {
		try {
			ArrayList<EntityPlayer> players = new ArrayList<EntityPlayer>();
			
			for (EntityPlayer player : mc.world.playerEntities) {
				if (!player.isEntityEqual(mc.player)) {
					players.add(player);
				}
			}
			
			return players;
		} catch (NullPointerException ignored) {
			return new ArrayList<EntityPlayer>();
		}
	}
	
	/**
	 * Gets all players but not friends
	 */
	public static ArrayList<EntityPlayer> getAllEnemies() {
		try {
			ArrayList<EntityPlayer> players = new ArrayList<EntityPlayer>();
			
			for (EntityPlayer player : mc.world.playerEntities) {
				if (!player.isEntityEqual(mc.player) && !Friends.isFriend(player)) {
					players.add(player);
				}
			}
			
			return players;
		} catch (NullPointerException ignored) {
			return new ArrayList<EntityPlayer>();
		}
	}
	
	/**
	 * Gets a player with the given name
	 */
	public static EntityPlayer getPlayer(String name) {
		for (EntityPlayer player : getAll()) {
			if (player.getName().equals(name)) {
				return player;
			}
		}
		
		return null;
	}
	
	/**
	 * Gets the closest player
	 */
	public static EntityPlayer getClosest() {
		double lowestDistance = Integer.MAX_VALUE;
		EntityPlayer closest = null;
		
		for (EntityPlayer player : getAll()) {
			if (player.getDistance(mc.player) < lowestDistance) {
				lowestDistance = player.getDistance(mc.player);
				closest = player;
			}
		}
		
		return closest;
	}
	
	/**
	 * Gets the closest non friend
	 */
	public static EntityPlayer getClosestEnemy() {
		double lowestDistance = Integer.MAX_VALUE;
		EntityPlayer closest = null;
		
		for (EntityPlayer player : getAllEnemies()) {
			if (player.getDistance(mc.player) < lowestDistance) {
				lowestDistance = player.getDistance(mc.player);
				closest = player;
			}
		}
		
		return closest;
	}
	
	/**
	 * Checks if these 2 players are in the same position
	 * @y how much the y difference can be
	 */
	public static boolean isInSameBlock(EntityPlayer player, EntityPlayer other, int y) {
		BlockPos first = new BlockPos((int)player.posX, (int)player.posY, (int)player.posZ);
		BlockPos second = new BlockPos((int)other.posX, (int)other.posY, (int)other.posZ);
		
		return first.getX() == second.getX() && Math.abs(first.getY() - second.getY()) <= y && first.getZ() == second.getZ();
	}
	
	/**
	 * Gets ip for current server
	 * Or Singleplayer if in singleplayer
	 */
	public static String getServerIp() {
		try {
			return mc.getCurrentServerData().serverIP;
		} catch (NullPointerException e) {
			return "Singleplayer";
		}
	}
	
	/**
	 * Makes the player do a right click
	 * Its run on the client thread because otherwise it yeets all kinds of exceptions
	 */
	public static void rightClick() {
		MinecraftForge.EVENT_BUS.register(playerUtil);
	}
	
	/**
	 * Gets the speed the given entity is moving at
	 * Like the difference between current and last tick position
	 */
	public static double getSpeed(Entity entity) {
		return new Vec3d(mc.player.posX, mc.player.posY, mc.player.posZ).distanceTo(new Vec3d(mc.player.lastTickPosX, mc.player.lastTickPosY, mc.player.lastTickPosZ));
	}
	
	public static boolean isMoving(Entity entity) {
		return getSpeed(entity) == 0;
	}
	
	@SubscribeEvent
	public void onTick(ClientTickEvent e) {
		mc.playerController.processRightClick(mc.player, mc.world, EnumHand.MAIN_HAND);
		MinecraftForge.EVENT_BUS.unregister(playerUtil);
	}
}
