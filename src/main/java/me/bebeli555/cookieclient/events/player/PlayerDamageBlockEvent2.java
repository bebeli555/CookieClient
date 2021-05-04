package me.bebeli555.cookieclient.events.player;

import me.bebeli555.cookieclient.events.bus.Cancellable;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;

public class PlayerDamageBlockEvent2 extends Cancellable {
	public BlockPos pos;
	public EnumFacing direction;
	
	public PlayerDamageBlockEvent2(BlockPos pos, EnumFacing direction) {
		this.pos = pos;
		this.direction = direction;
	}
}
