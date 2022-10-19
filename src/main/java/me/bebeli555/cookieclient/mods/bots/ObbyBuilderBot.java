package me.bebeli555.cookieclient.mods.bots;

import java.util.ArrayList;

import me.bebeli555.cookieclient.Mod;
import me.bebeli555.cookieclient.gui.Group;
import me.bebeli555.cookieclient.gui.Mode;
import me.bebeli555.cookieclient.gui.Setting;
import me.bebeli555.cookieclient.mods.combat.Surround;
import me.bebeli555.cookieclient.utils.BaritoneUtil;
import me.bebeli555.cookieclient.utils.BaritoneUtil.BaritoneSettings;
import me.bebeli555.cookieclient.utils.BlockUtil;
import me.bebeli555.cookieclient.utils.InventoryUtil;
import me.bebeli555.cookieclient.utils.InventoryUtil.ItemStackUtil;
import me.bebeli555.cookieclient.utils.MiningUtil;
import me.bebeli555.cookieclient.utils.PlayerUtil;
import me.bebeli555.cookieclient.utils.RotationUtil;
import net.minecraft.block.Block;
import net.minecraft.block.BlockShulkerBox;
import net.minecraft.block.material.Material;
import net.minecraft.client.gui.inventory.GuiChest;
import net.minecraft.client.gui.inventory.GuiShulkerBox;
import net.minecraft.entity.Entity;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.inventory.ClickType;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.NonNullList;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;

public class ObbyBuilderBot extends Mod {
	private static Thread thread;
	private static BaritoneSettings baritoneSettings;
	
	public static Setting name = new Setting(Mode.TEXT, "Name", "", "Name of the schematic it will build", "You need to give the extension too like test.schematic", "Needs to be stored at .minecraft/schematics/");
	public static Setting x = new Setting(Mode.INTEGER, "X", "", "X-Coordinate of the schematic");
	public static Setting y = new Setting(Mode.INTEGER, "Y", "", "Y-Coordinate of the schematic");
	public static Setting z = new Setting(Mode.INTEGER, "Z", "", "Z-Coordinate of the schematic");
	public static Setting shulkerName = new Setting(Mode.TEXT, "ShulkerName", "", "The exact name of the shulker where you have the echests", "It will take the shulker with the given name to get more echests to mine", "Note: If the shulker has no echests the bot will throw it away");
	
	public ObbyBuilderBot() {
		super(Group.BOTS, "ObbyBuilderBot", "Builds obsidian structures using baritone", "And mines echests for more obby when out", "Works on schematics that are built on the build limit!");
	}
	
	@Override
	public void onEnabled() {
		if (!InventoryUtil.hasBlock(Blocks.ENDER_CHEST)) {
			sendMessage("You need to have an enderchest in ur inventory", true);
			disable();
			return;
		}
		
		baritoneSettings = new BaritoneSettings();
		baritoneSettings.saveCurrentSettings();
		
		BaritoneUtil.setSetting("allowInventory", true);
		BaritoneUtil.setSetting("allowSprint", false);
		BaritoneUtil.setSetting("allowBreak", true);
		BaritoneUtil.setSetting("allowPlace", true);
		
		thread = new Thread() {
			public void run() {
				while(thread != null && thread.equals(this)) {
					if (mc.player != null) {
						try {
							loop();
						} catch (Exception e) {
							System.out.println("ObbyBuilderBot - Unhandled exception");
							e.printStackTrace();
						}
					}
					
					Mod.sleep(50);
				}
			}
		};
		
		thread.start();
	}
	
	@Override
	public void onDisabled() {
		Mod.EVENT_BUS.unsubscribe(MiningUtil.miningUtil);
		clearStatus();
		if (baritoneSettings != null) baritoneSettings.loadSettings();
		if (mc.player != null) {
			BaritoneUtil.forceCancel();
			BaritoneUtil.sendCommand("sel clear");
		}
		
		suspend(thread);
		thread = null;
	}
	
	public void loop() {
		//Get more echests from enderchest and mine them to get more obsidian
		if (InventoryUtil.getAmountOfBlock(Blocks.OBSIDIAN) <= 80) {
			BaritoneUtil.forceCancel();
			
			if (!MiningUtil.hasPickaxe()) {
				sendMessage("You need a diamond pickaxe!", true);
				disable();
				return;
			}
			
			if (mc.player.posY > 255 && !InventoryUtil.hasItem(Items.WATER_BUCKET)) {
				sendMessage("You need a water bucket!", true);
				disable();
				return;
			}
			
			if (mc.player.posY > 255 && !InventoryUtil.hasBlock(Blocks.OBSIDIAN)) {
				sendMessage("You need atleast 4 obsidian in your inventory!", true);
				disable();
				return;
			}
			
			//Search for a spot to start mining echests. Not solid above ground
			BlockPos bestPos = null;
			outer: for (BlockPos pos : BlockUtil.getAll(50)) {
				if (!isSolid(pos)) {
					BlockPos[] solid = new BlockPos[]{pos.add(0, -1, 0), pos.add(0, -1, 1), pos.add(-1, -1, 0), pos.add(-1, -1, 1), pos.add(1, -1, 0), pos.add(-2, -1, 0)};
					BlockPos[] air = new BlockPos[]{pos.add(0, 0, 1), pos.add(-1, 0, 0), pos.add(-1, 0, 1), pos.add(0, 1, 0), pos.add(0, 1, 1), pos.add(-1, 1, 0), pos.add(-1, 1, 1)};
					
					for (BlockPos solidCheck : solid) {
						if (!isSolid(solidCheck)) {
							continue outer;
						}
					}
					
					for (BlockPos airCheck : air) {
						if (getBlock(airCheck) != Blocks.AIR) {
							continue outer;
						}
					}
					
					if (BaritoneUtil.canPath(pos)) {
						bestPos = pos;
						break outer;
					}
				}
			}
			
			if (bestPos == null) {
				sendMessage("Found no suitable place nearby to break echests", true);
				disable();
				return;
			}
			
			//Make a little area below build limit so we can grind echests on it.
			if (bestPos.getY() > 255) {
				setStatus("Walking closer to location where to grind echests");
				BaritoneUtil.walkTo(bestPos.add(-2, 0, 0), true);
				Surround.centerMotionFull();
				
				BlockPos[] breakPositions = new BlockPos[]{bestPos.add(0, -1, 0), bestPos.add(0, -1, 1), bestPos.add(-1, -1, 0), bestPos.add(-1, -1, 1)};
				setStatus("Mining area to place echests on");
				while (thread != null && thread.equals(Thread.currentThread())) {
					boolean allAir = true;
					for (BlockPos pos : breakPositions) {
						if (getBlock(pos) != Blocks.AIR)  {
							allAir = false;
							MiningUtil.mine(pos, false);
							sleep(500);
						}
					}
					
					if (allAir) {
						break;
					}
				}
				
				//Now place water so we can use it to place a block below
				setStatus("Placing water so we can place a block below");
				InventoryUtil.switchItem(InventoryUtil.getSlot(Items.WATER_BUCKET), true);
				RotationUtil.rotate(new Vec3d(bestPos).add(-0.8, 1, 0.5), true);
				PlayerUtil.rightClick();
				BlockPos pos = bestPos.add(0, -3, 0);
				sleepUntil(() -> getBlock(pos) == Blocks.WATER, 15000);
				
				//Place one block using the water
				setStatus("Placing a block below");
				BlockPos pos2 = bestPos.add(-1, -2, 1);
				BlockUtil.placeBlock(Blocks.OBSIDIAN, pos2, false);
				sleepUntil(() -> getBlock(pos2) == Blocks.OBSIDIAN || getBlock(pos2.add(0, 0, -1)) == Blocks.OBSIDIAN, 15000);
				
				//Take water away
				setStatus("Taking water back");
				RotationUtil.rotate(new Vec3d(bestPos).add(-0.8, 1, 0.5), true);
				InventoryUtil.switchItem(InventoryUtil.getSlot(Items.BUCKET), true);
				PlayerUtil.rightClick();
				setStatus("Waiting for water to go down");
				sleepUntil(() -> mc.world.getBlockState(pos).getMaterial() != Material.WATER, 15000);
				
				//Make baritone place the other 3 blocks
				setStatus("Filling floor with obsidian");
				sleep(50);
				BaritoneUtil.sendCommand("sel clear");
				BaritoneUtil.sendCommand("sel pos1 " + bestPos.getX() + " " + (bestPos.getY() - 2) + " " + bestPos.getZ());
				BaritoneUtil.sendCommand("sel pos2 " + (bestPos.getX() - 1) + " " + (bestPos.getY() - 2) + " " + (bestPos.getZ() + 1));
				BaritoneUtil.sendCommand("sel set obsidian");
				sleepUntil(() -> !BaritoneUtil.isBuilding(), 15000);
				
				bestPos = bestPos.add(0, -1, 0);
			}
			
			int broken = 0;
			main: while(!InventoryUtil.isFull() && thread != null && thread.equals(Thread.currentThread())) {
				BlockPos pos = bestPos.add(0, 0, 1);
				
				if (!isSolid(pos.add(0, -1, 0)) || !isSolid(bestPos.add(0, -1, 0))) {
					break;
				}
				
				//Walk to best pos and center
				if (!getPlayerPos().equals(bestPos) || !Surround.isCentered()) {
					setStatus("Walking to position");
					BaritoneUtil.walkTo(bestPos, true);
					Surround.centerMotionFull();
				}
				
				//Place and destroy echests
				if (InventoryUtil.getAmountOfBlock(Blocks.ENDER_CHEST) > 4) {					
					if (isSolid(pos)) {
						setStatus("Mining enderchest");
						MiningUtil.mine(pos, false);
						broken++;
						Mod.sleep(300);
					} else {
						setStatus("Placing enderchest");
						Surround.centerMotionFull();
						BlockUtil.placeBlock(Blocks.ENDER_CHEST, pos, false);
						Mod.sleep(300);
					}
					
					//Collect nearby ground items every 64 enderchests.
					if (broken >= 64) {
						broken = 0;
						collectGroundItems();
					}
				}
				
				//Get more echests from enderchest or shulkers inside enderchest
				else {
					if (!isSolid(pos)) {
						setStatus("Placing enderchest");
						BlockUtil.placeBlock(Blocks.ENDER_CHEST, pos, false);
						sleep(1000);
					}
					
					BlockPos shulker = bestPos.add(-1, 0, 0);
					
					//Place shulker
					if (!isSolid(shulker)) {
						for (ItemStackUtil itemStack : InventoryUtil.getAllItems()) {
							if (itemStack.itemStack.getDisplayName().equals(shulkerName.stringValue())) {
								setStatus("Placing shulker");
								InventoryUtil.switchItem(itemStack.slotId, true);
								BlockUtil.placeBlock(null, shulker, false);
								sleep(500);
								break;
							}
						}
					}
					
					//Get the enderchests from the shulker and break it and place it back to echest
					if (isSolid(shulker)) {
						//If shulker position isnt a shulker then mine it as something went wrong
						if (getBlock(shulker) instanceof BlockShulkerBox == false) {
							setStatus("Mining block where shulker is supposed to be");
							MiningUtil.mine(shulker, false);
							continue main;
						}
						
						setStatus("Getting enderchests from shulker");
						Vec3d hitVec = new Vec3d(shulker).add(0.5, -0.5, 0.5);
						RotationUtil.rotate(hitVec, true);
				        mc.playerController.processRightClickBlock(mc.player, mc.world, shulker, EnumFacing.EAST, hitVec, EnumHand.MAIN_HAND);
						mc.player.swingArm(EnumHand.MAIN_HAND);
						sleepUntil(() -> mc.currentScreen instanceof GuiShulkerBox, 2500);
						sleep(1000);
						
						if (mc.currentScreen instanceof GuiShulkerBox) {
							NonNullList<ItemStack> items = mc.player.openContainer.getInventory();
							boolean empty = true;
							
							for (int i = 0; i < 27; i++) {
								if (InventoryUtil.getAmountOfBlock(Blocks.ENDER_CHEST) < 245) {
									if (Block.getBlockFromItem(items.get(i).getItem()) == Blocks.ENDER_CHEST) {
										empty = false;
										
										if (InventoryUtil.getEmptySlots() > 1) {
											mc.playerController.windowClick(mc.player.openContainer.windowId, i, 0, ClickType.PICKUP, mc.player);
											sleep(100);
											InventoryUtil.clickSlot(InventoryUtil.getEmptySlot(), 18);
											sleep(500);	
										}
									}
								}
							}

							//Break shulker
							setStatus("Mining shulker");
							mc.player.closeScreen();
							MiningUtil.mine(shulker, false);			
							sleepUntil(() -> hasShulker(), 3500);
							if (!hasShulker()) {
								collectGroundItems();
								sleep(500);
							}
							
							//Throw empty shulker away
							if (empty && hasShulker()) {
								setStatus("Throwing empty shulker away");
								
								mc.player.rotationYaw = 179;
								mc.player.rotationPitch = -25;
								
								for (ItemStackUtil itemStack : InventoryUtil.getAllItems()) {
									if (itemStack.itemStack.getDisplayName().equals(shulkerName.stringValue())) {
										InventoryUtil.switchItem(itemStack.slotId, true);
										break;
									}
								}
								
								sleep(500);
								mc.player.dropItem(false);
								sleep(500);
							}
						}
					}
					
					//Get the shulker from echest
					else if (!hasShulker()) {
						ItemStack held = mc.player.inventory.getCurrentItem();
						if (held != null && held.getDisplayName().equals(shulkerName.stringValue())) {
							InventoryUtil.clickSlot(8);
							sleep(150);
							InventoryUtil.clickSlot(InventoryUtil.getEmptySlot());
							sleep(150);
							continue;
						}
						
						setStatus("Getting shulker from enderchest");
						if (mc.currentScreen instanceof GuiChest == false) {
							Vec3d hitVec = new Vec3d(pos).add(0.5, -0.5, 0.5);
							RotationUtil.rotate(hitVec, true);
					        mc.playerController.processRightClickBlock(mc.player, mc.world, pos, EnumFacing.NORTH, hitVec, EnumHand.MAIN_HAND);
							mc.player.swingArm(EnumHand.MAIN_HAND);
							sleepUntil(() -> mc.currentScreen instanceof GuiChest, 5000);
							sleep(1000);
						}
						
						if (mc.currentScreen instanceof GuiChest) {
							NonNullList<ItemStack> items = mc.player.openContainer.getInventory();
							for (int i = 0; i < 27; i++) {
								if (items.get(i).getDisplayName().equals(shulkerName.stringValue())) {
									sleep(500);
									mc.playerController.windowClick(mc.player.openContainer.windowId, i, 0, ClickType.PICKUP, mc.player);
									sleep(300);
									InventoryUtil.clickSlot(InventoryUtil.getEmptySlot(), 18);
									mc.player.closeScreen();
									sleep(1000);
									continue main;
								}
							}
							
							sendMessage("No shulker box with name " + shulkerName.stringValue() + " was found in your enderchest", true);
							disable();
							return;
						}
					}
				}
			}
			
			collectGroundItems();
		}
		
		//Build the schematica
		else {
			setStatus("Building schematic named " + name.stringValue() + " to X: " + x.intValue() + " Y: " + y.intValue() + " Z: " + z.intValue());
			BaritoneUtil.forceCancel();
			BaritoneUtil.sendCommand("build " + name.stringValue() + " " + x.intValue() + " " + y.intValue() + " " + z.intValue());
			
			sleepUntil(() -> InventoryUtil.getAmountOfBlock(Blocks.OBSIDIAN) <= 80 || mc.player == null, -1, 1000);
			BaritoneUtil.forceCancel();
		}
	}
	
	//Collect the nearby ground items
	public static void collectGroundItems() {
		setStatus("Collecting nearby ground items", "ObbyBuilderBot");
		
		ArrayList<BlockPos> list = new ArrayList<BlockPos>();
		for (Entity entity : mc.world.loadedEntityList) {
			if (entity instanceof EntityItem && entity.getDistance(mc.player) <= 5) {
				list.add(entity.getPosition());
			}
		}
		
		for (BlockPos pos : list) {
			if (BaritoneUtil.canPath(pos)) {
				BaritoneUtil.walkTo(pos, true);
				sleep(1000);	
			}
		}
	}
	
	public static boolean hasShulker() {
		for (ItemStackUtil itemStack : InventoryUtil.getAllItems()) {
			if (itemStack.itemStack.getDisplayName().equals(shulkerName.stringValue())) {
				return true;
			}
		}
		
		return false;
	}
}
