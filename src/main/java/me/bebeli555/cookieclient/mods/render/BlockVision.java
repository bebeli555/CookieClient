package me.bebeli555.cookieclient.mods.render;

import me.bebeli555.cookieclient.Mod;
import me.bebeli555.cookieclient.gui.Group;
import net.minecraftforge.client.event.RenderBlockOverlayEvent;
import net.minecraftforge.client.event.RenderBlockOverlayEvent.OverlayType;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

public class BlockVision extends Mod {
	public BlockVision() {
		super(Group.RENDER, "BlockVision", "See clearly when inside blocks");
	}
	
	@SubscribeEvent
	public void onRenderBlockOverlayEvent(RenderBlockOverlayEvent event) {
		if (event.getOverlayType() == OverlayType.BLOCK) {
			event.setCanceled(true);
		}
	}
}
