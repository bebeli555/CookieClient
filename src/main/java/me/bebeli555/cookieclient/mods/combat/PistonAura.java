package me.bebeli555.cookieclient.mods.combat;

import me.bebeli555.cookieclient.Mod;
import me.bebeli555.cookieclient.gui.Group;
import me.bebeli555.cookieclient.gui.Mode;
import me.bebeli555.cookieclient.gui.Setting;
import me.bebeli555.cookieclient.utils.BlockUtil;
import me.bebeli555.cookieclient.utils.CrystalUtil;
import me.bebeli555.cookieclient.utils.InventoryUtil;
import me.bebeli555.cookieclient.utils.PlayerUtil;
import me.bebeli555.cookieclient.utils.Timer;
import net.minecraft.block.Block;
import net.minecraft.entity.item.EntityEnderCrystal;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent.ClientTickEvent;

/**
 * @author bebeli555
 */
public class PistonAura extends Mod {
	private Timer breakTimer = new Timer();
	private EnumFacing placed;
	private int counter, ticksCounter;
	
	public static Setting breakDelay = new Setting(Mode.INTEGER, "BreakDelay", 250, "How long to wait in ms when breaking the crystal");
	public static Setting waitTicks = new Setting(Mode.INTEGER, "WaitTicks", 30, "How many ticks to wait until the old place", "Position is free again");
	public static Setting ticks = new Setting(Mode.INTEGER, "Ticks", 2, "How many ticks to ignore");
	
	public PistonAura() {
		super(Group.COMBAT, "PistonAura", "Places crystals and pushes them with pistons", "To do full damage to players that are sitting in a hole", "Note: To get the pistons placed with right direction", "You need to be at the edge of the block", "Next to ur target", "You need piston, redstone torch or block and crystals");
	}
	
	@Override
	public void onDisabled() {
		placed = null;
		ticksCounter = 0;
	}
	
	@SubscribeEvent
	public void onTick(ClientTickEvent e) {
		EntityPlayer target = PlayerUtil.getClosestEnemy();
		if (mc.player == null || target == null) {
			return;
		}
		
		ticksCounter++;
		if (ticksCounter <= ticks.intValue()) return;
		ticksCounter = 0;
		
		//Break crystal
		EntityEnderCrystal crystal = CrystalUtil.getCrystalInPos(new BlockPos(target.posX, target.posY + 1, target.posZ));
		if (crystal != null) {
			if (breakTimer.hasPassed(breakDelay.intValue())) {
				mc.playerController.attackEntity(mc.player, crystal);
				breakTimer.reset();
			}
			
			return;
		}
		
		//Dont place again until the crystal is destroyed
		if (placed != null && !canPlace(new BlockPos(target.posX, target.posY, target.posZ), placed)) {
			counter++;
			int ticks2 = 1;
			if (ticks.intValue() > 0) ticks2 = ticks.intValue();
 			if (counter > waitTicks.intValue() / ticks2) {
 				counter = 0;
 				placed = null;
 			}
			
			return;
		}
		
		//Place crystal
		place(target);
	}
	
	public void place(EntityPlayer player) {
		EnumFacing closest = null;
		double lowestDistance = Integer.MAX_VALUE;
		BlockPos pos = new BlockPos(player.posX, player.posY, player.posZ);
		
		for (EnumFacing facing : EnumFacing.values()) {
			if (facing == EnumFacing.UP || facing == EnumFacing.DOWN) {
				continue;
			}
			
			if (closest == null || BlockUtil.distance(getPlayerPos(), pos.offset(facing)) < lowestDistance) {
				if (canPlace(pos, facing)) {
					lowestDistance = BlockUtil.distance(getPlayerPos(), pos.offset(facing));
					closest = facing;	
				}
			}
		}
		
		if (closest != null && InventoryUtil.hasItem(Items.END_CRYSTAL) && getPiston() != null && getRedstone() != null) {
			BlockUtil.placeBlockOnThisThread(getPiston(), pos.offset(closest).offset(closest).add(0, 1, 0), true);
			BlockUtil.placeItemOnThisThread(Items.END_CRYSTAL, pos.offset(closest), true);
			BlockUtil.placeBlockOnThisThread(getRedstone(), pos.offset(closest).offset(closest).offset(closest).add(0, 1, 0), true);
			placed = closest;
		}
	}
	
	public static boolean canPlace(BlockPos pos, EnumFacing facing) {
		return CrystalUtil.canPlaceCrystal(pos.offset(facing)) && BlockUtil.canPlaceBlock(pos.offset(facing).offset(facing).add(0, 1, 0)) && BlockUtil.canPlaceBlock(pos.offset(facing).offset(facing).offset(facing).add(0, 1, 0));
	}
	
	public static Block getPiston() {
		if (InventoryUtil.hasBlock(Blocks.PISTON)) {
			return Blocks.PISTON;
		} else if (InventoryUtil.hasBlock(Blocks.STICKY_PISTON)) {
			return Blocks.STICKY_PISTON;
		}
		
		return null;
	}
	
	public static Block getRedstone() {
		if (InventoryUtil.hasBlock(Blocks.REDSTONE_TORCH)) {
			return Blocks.REDSTONE_TORCH;
		} else if (InventoryUtil.hasBlock(Blocks.REDSTONE_BLOCK)) {
			return Blocks.REDSTONE_BLOCK;
		}
		
		return null;
	}
}
