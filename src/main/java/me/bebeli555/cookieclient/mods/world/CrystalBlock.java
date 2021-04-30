package me.bebeli555.cookieclient.mods.world;

import me.bebeli555.cookieclient.Mod;
import me.bebeli555.cookieclient.gui.Group;
import me.bebeli555.cookieclient.gui.Mode;
import me.bebeli555.cookieclient.gui.Setting;
import me.bebeli555.cookieclient.utils.BlockUtil;
import me.bebeli555.cookieclient.utils.CrystalUtil;
import me.bebeli555.cookieclient.utils.InventoryUtil;
import me.bebeli555.cookieclient.utils.RotationUtil;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.item.EntityEnderCrystal;
import net.minecraft.init.Blocks;
import net.minecraft.util.math.BlockPos;

public class CrystalBlock extends Mod {
	public static Setting range = new Setting(Mode.INTEGER, "Range", 4, "Range around player to search for", "Spots to place the obby on");
	
	public CrystalBlock() {
		super(Group.WORLD, "CrystalBlock", "Places obsidian to the best spot between", "You and the end crystal to block damage");
	}
	
	@Override
	public void onEnabled() {
		//No obsidian
		if (!InventoryUtil.hasBlock(Blocks.OBSIDIAN)) {
			sendMessage("You need obsidian", true);
			disable();
			return;
		}
		
		//Find end crystal thats going to deal the most damage to us
		EntityEnderCrystal best = null;
		double mostDamage = Integer.MIN_VALUE;
		for (EntityEnderCrystal crystal : CrystalUtil.getCrystals(10)) {
			double damage = CrystalUtil.calculateDamage(crystal.getPositionVector(), mc.player);
			
			if (damage > 0 && damage > mostDamage) {
				mostDamage = damage;
				best = crystal;
			}
		}
		
		if (best == null) {
			sendMessage("Found no nearby crystals to block", true);
			disable();
			return;
		}
		
		//Find best spot to place block on to block damage
		BlockPos bestPlace = null;
		double lowestDamage = Integer.MAX_VALUE;
		for (BlockPos pos : BlockUtil.getAll(range.intValue())) {
			if (BlockUtil.canPlaceBlock(pos)) {
				IBlockState old = mc.world.getBlockState(pos);
				mc.world.setBlockState(pos, Blocks.OBSIDIAN.getDefaultState());
				double damage = CrystalUtil.calculateDamage(best.getPositionVector(), mc.player);
				mc.world.setBlockState(pos, old);
				
				if (damage < lowestDamage) {
					lowestDamage = damage;
					bestPlace = pos;
				}
			}
		}
		
		if (bestPlace == null) {
			sendMessage("Found no spot to place block on to block dmg", true);
			disable();
			return;
		}
		
		//Place block
		BlockUtil.placeBlockOnThisThread(Blocks.OBSIDIAN, bestPlace, true);
		disable();
	}
	
	@Override
	public void onDisabled() {
		RotationUtil.stopRotating();
	}
}
