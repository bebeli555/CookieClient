package me.bebeli555.cookieclient.mods.world;

import me.bebeli555.cookieclient.Mod;
import me.bebeli555.cookieclient.events.bus.EventHandler;
import me.bebeli555.cookieclient.events.bus.Listener;
import me.bebeli555.cookieclient.events.player.PlayerDamageBlockEvent;
import me.bebeli555.cookieclient.events.player.PlayerMotionUpdateEvent;
import me.bebeli555.cookieclient.gui.Group;
import me.bebeli555.cookieclient.gui.Mode;
import me.bebeli555.cookieclient.gui.Setting;
import me.bebeli555.cookieclient.rendering.RenderUtil;
import net.minecraft.init.Blocks;
import net.minecraft.network.play.client.CPacketPlayerDigging;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;

public class PacketMine extends Mod {
	public BlockPos broken;
	public long startMs;
	
	public static Setting renderBroken = new Setting(Mode.BOOLEAN, "RenderBroken", true, "Renders the block that its currently breaking");
		public static Setting color = new Setting(renderBroken, Mode.TEXT, "Color", "0x36b84e40", "Hex");
	
	public PacketMine() {
		super(Group.WORLD, "PacketMine", "Mine using packets");
	}
	
	@EventHandler
	private Listener<PlayerMotionUpdateEvent> onMotionUpdate = new Listener<>(event -> {
		if (mc.player == null || mc.objectMouseOver == null || mc.objectMouseOver.getBlockPos() == null || mc.objectMouseOver.sideHit == null) {
			return;
		}
		
		if (mc.gameSettings.keyBindAttack.isKeyDown()) {
			//Dont do it if its an unbreakable block or its not gonna work ever again unless u disconnect
			if (mc.world.getBlockState(mc.objectMouseOver.getBlockPos()).getBlockHardness(mc.world, mc.objectMouseOver.getBlockPos()) == -1) {
				return;
			}
			
			if (getBlock(mc.objectMouseOver.getBlockPos()) != Blocks.AIR) {
				if (broken == null || getBlock(broken) == Blocks.AIR || Math.abs(startMs - System.currentTimeMillis()) > 15000) {
					broken = mc.objectMouseOver.getBlockPos();
					startMs = System.currentTimeMillis();
				}
				
		        mc.player.connection.sendPacket(new CPacketPlayerDigging(CPacketPlayerDigging.Action.START_DESTROY_BLOCK, mc.objectMouseOver.getBlockPos(), mc.objectMouseOver.sideHit));
		        mc.player.connection.sendPacket(new CPacketPlayerDigging(CPacketPlayerDigging.Action.STOP_DESTROY_BLOCK, mc.objectMouseOver.getBlockPos(), mc.objectMouseOver.sideHit));
		        mc.player.swingArm(EnumHand.MAIN_HAND);
			}
		}
	});
	
	@EventHandler
	private Listener<PlayerDamageBlockEvent> playerDamageBlockEvent = new Listener<>(event -> {
		event.cancel();
	});
	
	@Override
	public void onRenderWorld(float partialTicks) {
		if (broken != null && renderBroken.booleanValue()) {
			RenderUtil.drawFilledBox(RenderUtil.getBB(broken, 1), color.intValue());
		}
		
		if (getBlock(broken) == Blocks.AIR) {
			broken = null;
		}
	}
}