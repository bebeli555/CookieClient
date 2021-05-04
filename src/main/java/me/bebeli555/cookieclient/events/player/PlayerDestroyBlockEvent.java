package me.bebeli555.cookieclient.events.player;

import me.bebeli555.cookieclient.events.bus.Cancellable;
import net.minecraft.util.math.BlockPos;

public class PlayerDestroyBlockEvent extends Cancellable {
	public BlockPos pos;
	
	public PlayerDestroyBlockEvent(BlockPos pos) {
		this.pos = pos;
	}
}
