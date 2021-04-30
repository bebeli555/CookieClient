package me.bebeli555.cookieclient.mods.world;

import me.bebeli555.cookieclient.Mod;
import me.bebeli555.cookieclient.events.other.PacketEvent;
import me.bebeli555.cookieclient.events.player.TravelEvent;
import me.bebeli555.cookieclient.gui.Group;
import me.bebeli555.cookieclient.gui.Mode;
import me.bebeli555.cookieclient.gui.Setting;
import me.bebeli555.cookieclient.utils.BlockUtil;
import me.bebeli555.cookieclient.utils.InventoryUtil;
import me.bebeli555.cookieclient.utils.InventoryUtil.ItemStackUtil;
import me.bebeli555.cookieclient.utils.RotationUtil;
import me.bebeli555.cookieclient.utils.Timer;
import me.zero.alpine.listener.EventHandler;
import me.zero.alpine.listener.Listener;
import net.minecraft.block.Block;
import net.minecraft.init.Blocks;
import net.minecraft.item.Item;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.network.play.client.CPacketConfirmTeleport;
import net.minecraft.network.play.client.CPacketPlayer;
import net.minecraft.network.play.server.SPacketPlayerPosLook;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent.ClientTickEvent;

public class Scaffold extends Mod {
	private static Timer towerTimer = new Timer();
	private static int counter, counter2;
	
	public static Setting tower = new Setting(Mode.BOOLEAN, "Tower", true, "Bridge up fast");
	public static Setting delay = new Setting(Mode.INTEGER, "Delay", 3, "Delay in ticks to wait after placing block");
	
	public Scaffold() {
		super(Group.WORLD, "Scaffold", "Places blocks under you");
	}
	
	@Override
	public void onDisabled() {
		RotationUtil.stopRotating();
	}
	
	@SubscribeEvent
	public void onTick(ClientTickEvent e) {
		if (mc.player == null) {
			return;
		}

		counter++;
		if (counter < delay.intValue()) {
			return;
		}
		
		if (!BlockUtil.canPlaceBlock(getPlayerPos().add(0, -1, 0))) {
			counter2++;
			if (counter2 > 40) {
				RotationUtil.stopRotating();
			}
			
			return;
		}

		counter2 = 0;
		
		if (isSolid(getPlayerPos().add(0, -1, 0)) || !BlockUtil.canPlaceBlock(getPlayerPos().add(0, -1, 0))) {
			RotationUtil.stopRotating();
			return;
		}
		
		int slot = getBlockSlot();
		if (slot == -1) {
			sendMessage("You dont have any blocks", true);
			disable();
			return;
		}
		
		BlockUtil.Place place = new BlockUtil.Place(null, Block.getBlockFromItem(InventoryUtil.getItemStack(slot).getItem()), getPlayerPos().add(0, -1, 0), true);
		place.dontStopRotating = true;
		place.rotateSpoofNoPacket = true;
		place.onTick(null);
		
		counter = 0;
	}
	
    @EventHandler
    private Listener<TravelEvent> travelEvent = new Listener<>(event -> {
    	if (tower.booleanValue() && mc.gameSettings.keyBindJump.isKeyDown() && isSolid(getPlayerPos().add(0, -2, 0))) {
    		if (towerTimer.hasPassed(500)) {
    			if (!mc.player.onGround && mc.player.posY - Math.floor(mc.player.posY) <= 0.1) {
        			mc.player.motionY = 0.41999998688697815;
    			}
    		} else if (mc.player.fallDistance <= 2.0f) {
    			mc.player.motionY = -0.169;
    		}
    	}
    });
    
    @EventHandler
    private Listener<PacketEvent> packetEvent = new Listener<>(event -> {
    	if (event.packet instanceof SPacketPlayerPosLook && mc.player != null) {
    		event.cancel();
    		towerTimer.reset();
    		
    		SPacketPlayerPosLook packet = (SPacketPlayerPosLook)event.packet;
    		mc.player.setPosition(packet.getX(), packet.getY(), packet.getZ());
    		
            mc.getConnection().sendPacket(new CPacketConfirmTeleport(packet.getTeleportId()));
            mc.getConnection().sendPacket(new CPacketPlayer.PositionRotation(packet.getX(), packet.getY(), packet.getZ(), packet.getYaw(), packet.getPitch(), false));
    	}
    });
    
	public static int getBlockSlot() {
		if (isValidBlock(mc.player.getHeldItemMainhand().getItem())) {
			return mc.player.inventory.currentItem;
		}
		
		//Search hotbar
		for (int i = 0; i < 9; i++) {
			ItemStack itemStack = InventoryUtil.getItemStack(i);
			
			if (isValidBlock(itemStack.getItem())) {
				return i;
			}
		}
		
		//Then search whole inventory if hotbar had no blocks
		for (ItemStackUtil itemStack : InventoryUtil.getAllItems()) {
			if (isValidBlock(itemStack.itemStack.getItem())) {
				return itemStack.slotId;
			}
		}
		
		return -1;
	}
	
	public static boolean isValidBlock(Item item) {
		return item instanceof ItemBlock && Block.getBlockFromItem(item) != Blocks.WEB;
	}
}
