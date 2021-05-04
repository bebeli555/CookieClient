package me.bebeli555.cookieclient.events.block;

import me.bebeli555.cookieclient.events.bus.Cancellable;
import net.minecraft.block.state.IBlockState;

public class CanCollideEvent extends Cancellable {
	public IBlockState state;
	
	public CanCollideEvent(IBlockState state) {
		this.state = state;
	}
}
