package me.bebeli555.cookieclient.mods.combat;

import me.bebeli555.cookieclient.Mod;
import me.bebeli555.cookieclient.events.bus.EventHandler;
import me.bebeli555.cookieclient.events.bus.Listener;
import me.bebeli555.cookieclient.events.player.PlayerMotionUpdateEvent;
import me.bebeli555.cookieclient.gui.Group;
import me.bebeli555.cookieclient.gui.Mode;
import me.bebeli555.cookieclient.gui.Setting;
import me.bebeli555.cookieclient.utils.BlockUtil;
import me.bebeli555.cookieclient.utils.InventoryUtil;
import me.bebeli555.cookieclient.utils.RotationUtil;
import net.minecraft.block.Block;
import net.minecraft.init.Blocks;
import net.minecraft.network.play.client.CPacketPlayer;
import net.minecraft.util.math.BlockPos;

public class Surround extends Mod {
	private int oldSlot = -1;
	
	public static Setting blocksPerTick = new Setting(Mode.INTEGER, "BlocksPerTick", 1, "How many blocks to place per tick");
	public static Setting center = new Setting(Mode.BOOLEAN, "Center", true, "Center before so it can place all the blocks");
		public static Setting centerMode = new Setting(center, "Mode", "Motion", new String[]{"Motion"}, new String[]{"Teleport"});
	public static Setting toggle = new Setting(Mode.BOOLEAN, "Toggle", false, "Toggles it off when there is no blocks to place");
	public static Setting toggleOnSneak = new Setting(Mode.BOOLEAN, "ToggleOnSneak", false, "Only makes it work when ur sneaking");
	public static Setting toggleOnJump = new Setting(Mode.BOOLEAN, "ToggleOnJump", false, "Toggles surround off when u jump");
	
	public Surround() {
		super(Group.COMBAT, "Surround", "Surrounds your feet with obsidian", "Useful for blocking crystal damage");
	}
	
	@Override
	public void onDisabled() {		
		//Switch to old slot
		if (oldSlot != -1) {
			mc.player.inventory.currentItem = oldSlot;
		}
		
		oldSlot = -1;
		RotationUtil.stopRotating();
	}

    @EventHandler
    private Listener<PlayerMotionUpdateEvent> onMotionUpdate = new Listener<>(p_Event -> {
		if (mc.player == null || !mc.gameSettings.keyBindSneak.isKeyDown() && toggleOnSneak.booleanValue()) {
			return;
		}
		
		if (!InventoryUtil.hasBlock(Blocks.OBSIDIAN)) {
			disable();
			sendMessage("You need obsidian", true);
			return;
		}
		
		if (mc.gameSettings.keyBindJump.isKeyDown() && toggleOnJump.booleanValue()) {
			disable();
			return;
		}
		
		if (Block.getBlockFromItem(mc.player.getHeldItemMainhand().getItem()) != Blocks.OBSIDIAN) {
			oldSlot = mc.player.inventory.currentItem;
		}
		
		int blocksPlaced = 0;
		for (BlockPos pos : getBlocksToPlace()) {
			if (BlockUtil.distance(getPlayerPos(), pos) > 2) {
				break;
			}
			
			if (!isSolid(pos)) {
				if (InventoryUtil.hasBlock(Blocks.OBSIDIAN)) {
					boolean canPlace = BlockUtil.canPlaceBlock(pos);
					boolean canPlaceBelow = BlockUtil.canPlaceBlock(pos.add(0, -1, 0));
					
					if (center.booleanValue() && !canPlace) {
						if (centerMode.stringValue().equals("Motion") && !centerMotion()) {
							return;
						} else if (centerMode.stringValue().equals("Teleport")) {
							centerTeleport();
						}
					}
					
					if (canPlace) {
						BlockUtil.placeBlockOnThisThread(Blocks.OBSIDIAN, pos, true);
					} else if (canPlaceBelow){
						BlockUtil.placeBlockOnThisThread(Blocks.OBSIDIAN, pos.add(0, -1, 0), true);
					} else {
						continue;
					}
					
					RotationUtil.stopRotating();
					blocksPlaced++;
					if (blocksPlaced >= blocksPerTick.intValue()) {
						return;
					}
				}
			}
		}
		
		if (blocksPlaced == 0 && toggle.booleanValue()) {
			disable();
		} else if (blocksPlaced == 0) {
			if (oldSlot != -1) {
				mc.player.inventory.currentItem = oldSlot;
				oldSlot = -1;
			}
		}
    });
	
	/**
	 * Centers the player by using motion mode
	 */
	public static boolean centerMotion() {
		if (isCentered()) {
			return true;
		}
		
		double[] centerPos = {Math.floor(mc.player.posX) + 0.5, Math.floor(mc.player.posY), Math.floor(mc.player.posZ) + 0.5};
		mc.player.motionX = (centerPos[0] - mc.player.posX) / 2;
		mc.player.motionZ = (centerPos[2] - mc.player.posZ) / 2;
		return false;
	}
	
	/**
	 * Centers the player fully in one call. Needs to be called on a new thread because it sleeps
	 */
	public static void centerMotionFull() {
		if (isCentered()) {
			return;
		}
		
		double[] centerPos = {Math.floor(mc.player.posX) + 0.5, Math.floor(mc.player.posY), Math.floor(mc.player.posZ) + 0.5};
		
		mc.player.motionX = (centerPos[0] - mc.player.posX) / 2;
		mc.player.motionZ = (centerPos[2] - mc.player.posZ) / 2;
		
		sleepUntil(() -> Math.abs(centerPos[0] - mc.player.posX) <= 0.1 && Math.abs(centerPos[2] - mc.player.posZ) <= 0.1, 1000);
		mc.player.motionX = 0;
		mc.player.motionZ = 0;
	}
	
	/**
	 * Centers the player using teleport mode
	 */
	public static void centerTeleport() {
		if (isCentered()) {
			return;
		}
		
		double[] centerPos = {Math.floor(mc.player.posX) + 0.5, Math.floor(mc.player.posY), Math.floor(mc.player.posZ) + 0.5};
		mc.player.connection.sendPacket(new CPacketPlayer.Position(centerPos[0], mc.player.posY, centerPos[2], mc.player.onGround));
		mc.player.setPosition(centerPos[0], mc.player.posY, centerPos[2]);
	}
	
	/**
	 * Checks if the player is centered on the block
	 */
	public static boolean isCentered() {
		double[] centerPos = {Math.floor(mc.player.posX) + 0.5, Math.floor(mc.player.posY), Math.floor(mc.player.posZ) + 0.5};
		return Math.abs(centerPos[0] - mc.player.posX) <= 0.1 && Math.abs(centerPos[2] - mc.player.posZ) <= 0.1;
	}
	
	/**
	 * Checks if the given BlockPos is surrounded with obby or bedrock
	 */
	public static boolean isSurrounded(BlockPos p) {
		BlockPos[] positions = {p.add(1, 0, 0), p.add(-1, 0, 0), p.add(0, 0, 1), p.add(0, 0, -1)};
		
 		for (BlockPos pos : positions) {
 			if (getBlock(pos) != Blocks.OBSIDIAN && getBlock(pos) != Blocks.BEDROCK) {
 				return false;
 			}
 		}
 		
 		return true;
	}
	
	/**
	 * Get the blockpositions where to place obby
	 */
	public static BlockPos[] getBlocksToPlace() {
		BlockPos p = getPlayerPos();
		return new BlockPos[]{p.add(1, 0, 0), p.add(-1, 0, 0), p.add(0, 0, 1), p.add(0, 0, -1)};
	}
}