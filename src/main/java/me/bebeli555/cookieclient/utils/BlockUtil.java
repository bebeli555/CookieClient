package me.bebeli555.cookieclient.utils;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.ConcurrentModificationException;
import java.util.List;

import me.bebeli555.cookieclient.Mod;
import net.minecraft.block.Block;
import net.minecraft.entity.Entity;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.init.Blocks;
import net.minecraft.item.Item;
import net.minecraft.network.play.client.CPacketEntityAction;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.math.Vec3d;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent.ClientTickEvent;

public class BlockUtil extends Mod {
	
	/**
	 * Searches around the player to find the given block.
	 * @radius the radius to search around the player
	 */
	public static BlockPos findBlock(Block block, int radius) {
        for (int x = (int) (mc.player.posX - radius); x < mc.player.posX + radius; x++) {
            for (int z = (int) (mc.player.posZ - radius); z < mc.player.posZ + radius; z++) {
                for (int y = (int) (mc.player.posY + radius); y > mc.player.posY - radius; y--) {
                	BlockPos pos = new BlockPos(x, y, z);
                	if (mc.world.getBlockState(pos).getBlock().equals(block)) {
                		return pos;
                	}
                }
            }
        }
		
		return null;
	}
	
	/**
	 * Gets all the BlockPositions in the given radius around the player
	 */
	public static List<BlockPos> getAll(int radius) {
		List<BlockPos> list = new ArrayList<BlockPos>();
		try {
	        for (int x = (int) (mc.player.posX - radius); x < mc.player.posX + radius; x++) {
	            for (int z = (int) (mc.player.posZ - radius); z < mc.player.posZ + radius; z++) {
	                for (int y = (int) (mc.player.posY + radius); y > mc.player.posY - radius; y--) {
	            		list.add(new BlockPos(x, y, z));
	                }
	            }
	        }
	        
	        Collections.sort(list, new Comparator<BlockPos>() {
	            @Override
	            public int compare(BlockPos lhs, BlockPos rhs) {
	                return mc.player.getDistanceSq(lhs) > mc.player.getDistanceSq(rhs) ? 1 : (mc.player.getDistanceSq(lhs) < mc.player.getDistanceSq(rhs)) ? -1 : 0;
	            }
	        });
	        
	        return list;
		} catch (Exception e) {
			return list;
		}
	}
	
	public static ArrayList<BlockPos> getAllNoSort(int radius) {
		ArrayList<BlockPos> list = new ArrayList<BlockPos>();
		
        for (int x = (int) (mc.player.posX - radius); x < mc.player.posX + radius; x++) {
            for (int z = (int) (mc.player.posZ - radius); z < mc.player.posZ + radius; z++) {
                for (int y = (int) (mc.player.posY + radius); y > mc.player.posY - radius; y--) {
            		list.add(new BlockPos(x, y, z));
                }
            }
        }
        
        return list;
	}
	
	/**
	 * Gets all the BlockPositions in the given radius around the pos
	 */
	public static ArrayList<BlockPos> getAll(Vec3d pos, int radius) {
		ArrayList<BlockPos> list = new ArrayList<BlockPos>();
		
        for (int x = (int) (pos.x - radius); x < pos.x + radius; x++) {
            for (int z = (int) (pos.z - radius); z < pos.z + radius; z++) {
                for (int y = (int) (pos.y + radius); y > pos.y - radius; y--) {
            		list.add(new BlockPos(x, y, z));
                }
            }
        }
        
        return list;
	}
	
	/**
	 * Checks if a block can be placed to this position
	 */
	public static boolean canPlaceBlock(BlockPos pos) {
		try {
			for (Entity entity : mc.world.getEntitiesWithinAABBExcludingEntity(null, new AxisAlignedBB(pos))) {
				if (entity instanceof EntityItem == false) {
					return false;
				}
			}
		} catch (ConcurrentModificationException ignored) {
			
		}
		
		if (!isSolid(pos) && canBeClicked(pos)) {
			return true;
		}
		
		return false;
	}
	
	/**
	 * Places a block to the given BlockPosition
	 * This is run on the client thread
	 * @pos Places the block to this position
	 * @block The block to place. Must be in ur inventory!
	 */
	public static boolean placeBlock(Block block, BlockPos pos, boolean spoofRotation) {
		Place place = new Place(null, block, pos, spoofRotation);
		sleepUntil(() -> place.done, -1);
		return place.success;
	}
	
	public static void placeBlockNoSleep(Block block, BlockPos pos, boolean spoofRotation) {
		new Place(null, block, pos, spoofRotation);
	}
	
	public static void placeBlockOnThisThread(Block block, BlockPos pos, boolean spoofRotation) {
		new Place(null, block, pos, spoofRotation).onTick(null);
	}
	
	/**
	 * Same as the placeBlock but it interacts with the given block with the given item
	 * This is run on the client thread
	 */
	public static boolean placeItem(Item item, BlockPos pos, boolean spoofRotation) {
		Place place = new Place(item, null, pos, spoofRotation);
		sleepUntil(() -> place.done, -1);
		return place.success;
	}
	
	public static void placeItemNoSleep(Item item, BlockPos pos, boolean spoofRotation) {
		new Place(item, null, pos, spoofRotation);
	}
	
	public static void placeItemOnThisThread(Item item, BlockPos pos, boolean spoofRotation) {
		new Place(item, null, pos, spoofRotation).onTick(null);
	}
	
	/**
	 * Distance between these 2 blockpositions
	 */
	public static int distance(BlockPos first, BlockPos second) {
		return Math.abs(first.getX() - second.getX()) + Math.abs(first.getY() - second.getY()) + Math.abs(first.getZ() - second.getZ());
	}
	
	/**
	 * Checks if the block is in render distance or known by the client.
	 */
	public static boolean isInRenderDistance(BlockPos pos) {
		return mc.world.getChunk(pos).isLoaded();
	}
	
	/**
	 * Checks if any neighbor block can be right clicked
	 */
	public static boolean canBeClicked(BlockPos pos) {
		for (EnumFacing facing : EnumFacing.values()) {
			BlockPos neighbor = pos.offset(facing);

			//If neighbor cant be clicked then continue
            if (!mc.world.getBlockState(neighbor).getBlock().canCollideCheck(mc.world.getBlockState(neighbor), false) && getBlock(neighbor) != Blocks.WATER) {
               continue;
            }
            
            return true;
		}
		
		return false;
	}
	
	/**
	 * Checks if the pos can be seen
	 */
	public static boolean canSeePos(BlockPos pos) {
		return getLegitFacing(pos) != null;
	}
	
	/**
	 * Gets a facing for the block that can be looked at with ray tracing
	 */
	public static EnumFacing getLegitFacing(BlockPos pos) {
		for (EnumFacing facing : EnumFacing.values()) {
			Vec3d start = new Vec3d(mc.player.posX, mc.player.posY + mc.player.eyeHeight, mc.player.posZ);
			Vec3d end = new Vec3d(pos).add(0.5, 0.499, 0.5).add(new Vec3d(facing.getDirectionVec()).scale(0.5));
			RayTraceResult result = mc.world.rayTraceBlocks(start, end);
			
			if (result != null && result.getBlockPos().equals(pos)) {
				return facing;
			}
		}
		
		return null;
	}
	
	public static class Place {
		public boolean done, success, spoofRotation, dontRotate, rotateSpoofNoPacket, dontStopRotating;
		public Item item;
		public Block block;
		public BlockPos pos;
		public EnumFacing setFacing;
		
		public Place(Item item, Block block, BlockPos pos, boolean spoofRotation) {
			this.item = item;
			this.pos = pos;
			this.block = block;
			this.spoofRotation = spoofRotation;
			MinecraftForge.EVENT_BUS.register(this);
		}
		
		@SubscribeEvent
		public void onTick(ClientTickEvent e) {
			if (block != null || item != null) {
				int slot = -1;
				if (block != null) {
					slot = InventoryUtil.getSlot(block);
				} else if (item != null) {
					slot = InventoryUtil.getSlot(item);
				}
				 
				//If item wasent found on inventory then return false
				if (slot == -1) {
					done(false);
					return;
				}
				
				//Put the given item into the hand so it can be "placed"
				if (InventoryUtil.getHandSlot() != slot) {
					InventoryUtil.switchItem(slot, false);
				}
			}
			
			for (EnumFacing facing : EnumFacing.values()) {
				if (setFacing != null) {
					facing = setFacing;
				}
				
				BlockPos neighbor = pos.offset(facing);
				EnumFacing side = facing.getOpposite();

				//If neighbor cant be clicked then continue
	            if (setFacing == null && !mc.world.getBlockState(neighbor).getBlock().canCollideCheck(mc.world.getBlockState(neighbor), false) && getBlock(neighbor) != Blocks.WATER) {
	            	continue;
	            }
				
				Vec3d hitVec;
				if (item != null) {
					hitVec = new Vec3d(pos).add(0.5, 0.5, 0.5).add(new Vec3d(side.getDirectionVec()).scale(0.5));
				} else {
					hitVec = new Vec3d(neighbor).add(0.5, 0.5, 0.5).add(new Vec3d(side.getDirectionVec()).scale(0.5));
				}
				
				if (!dontRotate) {
					if (spoofRotation) {
						RotationUtil.rotateSpoof(hitVec);
					} else {
						RotationUtil.rotate(hitVec, true);
					}
				} else if (rotateSpoofNoPacket) {
					RotationUtil.rotateSpoofNoPacket(hitVec);
				}
				
				mc.player.connection.sendPacket(new CPacketEntityAction(mc.player, CPacketEntityAction.Action.START_SNEAKING));
		        if (item != null) {
		        	mc.playerController.processRightClickBlock(mc.player, mc.world, pos, side, hitVec, EnumHand.MAIN_HAND);
		        } else {
		        	mc.playerController.processRightClickBlock(mc.player, mc.world, neighbor, side, hitVec, EnumHand.MAIN_HAND);
		        	mc.player.swingArm(EnumHand.MAIN_HAND);
		        }
				mc.player.connection.sendPacket(new CPacketEntityAction(mc.player, CPacketEntityAction.Action.STOP_SNEAKING));
				done(true);
				return;
			}
			
			done(false);
			return;
		}
		
		public void done(boolean success) {
			this.done = true;
			this.success = success;
			MinecraftForge.EVENT_BUS.unregister(this);
			if (!dontRotate && !dontStopRotating) {
				RotationUtil.stopRotating();
			}
		}
	}
}
