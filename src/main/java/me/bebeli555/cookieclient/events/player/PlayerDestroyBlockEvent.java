package me.bebeli555.cookieclient.events.player;

import me.zero.alpine.type.Cancellable;
import net.minecraft.util.math.BlockPos;

public class PlayerDestroyBlockEvent extends Cancellable {
	public BlockPos pos;
	
	public PlayerDestroyBlockEvent(BlockPos pos) {
		this.pos = pos;
	}
}
