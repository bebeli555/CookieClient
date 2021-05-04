package me.bebeli555.cookieclient.events.other;

import me.bebeli555.cookieclient.events.bus.Cancellable;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.client.multiplayer.WorldClient;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;

public class ProcessRightClickBlockEvent extends Cancellable {
	public EntityPlayerSP player;
	public WorldClient world;
	public BlockPos pos;
	public EnumFacing facing;
	public Vec3d vec;
	public EnumHand hand;
	
	public ProcessRightClickBlockEvent(EntityPlayerSP player, WorldClient world, BlockPos pos, EnumFacing facing, Vec3d vec, EnumHand hand) {
		this.player = player;
		this.world = world;
		this.pos = pos;
		this.facing = facing;
		this.vec = vec;
		this.hand = hand;
	}
}
