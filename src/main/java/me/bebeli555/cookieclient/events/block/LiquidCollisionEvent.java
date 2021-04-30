package me.bebeli555.cookieclient.events.block;

import baritone.api.event.events.type.Cancellable;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;

public class LiquidCollisionEvent extends Cancellable {
	public BlockPos pos;
	public AxisAlignedBB boundingBox;
	
	public LiquidCollisionEvent(BlockPos pos) {
		this.pos = pos;
	}
}
