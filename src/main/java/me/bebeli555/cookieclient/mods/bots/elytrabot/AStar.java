package me.bebeli555.cookieclient.mods.bots.elytrabot;

import java.util.ArrayList;

import me.bebeli555.cookieclient.Mod;
import me.bebeli555.cookieclient.utils.BlockUtil;
import net.minecraft.init.Blocks;
import net.minecraft.util.math.BlockPos;

public class AStar extends Mod {
	private static boolean check;
	
	/**
	 * Generates a path to the given goal.
	 * @goal The goal it will path to
	 * @positions The nearby positions that can possibly by added to the open list
	 * @checkPositions The positions it will check not to be solid when iterating the above list
	 */
	public static ArrayList<BlockPos> generatePath(BlockPos start, BlockPos goal, BlockPos[] positions, ArrayList<BlockPos> checkPositions, int loopAmount) {
		AStarNode.nodes.clear();
		BlockPos current = start;
		BlockPos closest = current;
		ArrayList<BlockPos> open = new ArrayList<BlockPos>();
		ArrayList<BlockPos> closed = new ArrayList<BlockPos>();
		
		int noClosest = 0;
		for (int i = 0; i < loopAmount; i++) {
			//Check if were in the goal
			if (current.equals(goal)) {
				check = false;
				return getPath(current);
			}
			
			//Get the pos with lowest f cost from open list and put it to closed list
			double lowestFCost = Integer.MAX_VALUE;
			for (BlockPos pos : open) {
				double fCost = fCost(pos, goal, start);
				
				if (fCost < lowestFCost) {
					lowestFCost = fCost;
					current = pos;
				}
			}
			
			//Update the lists
			closed.add(current);
			open.remove(current);

			ArrayList<BlockPos> addToOpen = addToOpen(positions, checkPositions, current, goal, start, open, closed);
			if (addToOpen != null) {
				open.addAll(addToOpen);
			} else {
				break;
			}
			
			//Set the closest pos.
			if (lowestFCost < fCost(closest, goal, start)) {
				closest = current;
				noClosest = 0;
			} else {
				noClosest++;
				
				//If there hasent been a closer pos found in x times then break
				if (noClosest > 200) {
					break;
				}
			}
		}
		
		//If there was no path found to the goal then return path to the closest pos.
		//As the goal is probably out of render distance.
		if (!check) {
			check = true;
			return generatePath(start, closest, positions, checkPositions, loopAmount);
		} else {
			check = false;
			return new ArrayList<BlockPos>();
		}
	}
	
	/**
	 * Adds the nearby positions to the open list. And updates the best parent for the AStarNodes
	 */
	public static ArrayList<BlockPos> addToOpen(BlockPos[] positions, ArrayList<BlockPos> checkPositions, BlockPos current, BlockPos goal, BlockPos start, ArrayList<BlockPos> open, ArrayList<BlockPos> closed) {
		ArrayList<BlockPos> list = new ArrayList<BlockPos>();
		
		ArrayList<BlockPos> positions2 = new ArrayList<BlockPos>();
		for (BlockPos pos : positions) {
			positions2.add(current.add(pos.getX(), pos.getY(), pos.getZ()));
		}
		
		outer: for (BlockPos pos : positions2) {
			if (!isSolid(pos) && !closed.contains(pos)) {
				ArrayList<BlockPos> checkPositions2 = new ArrayList<BlockPos>();
				for (BlockPos b : checkPositions) {
					checkPositions2.add(pos.add(b.getX(), b.getY(), b.getZ()));
				}
				
				for (BlockPos check : checkPositions2) {
					if (ElytraBot.mode.stringValue().equals("Highway") && !BlockUtil.isInRenderDistance(check)) {
						return null;
					}
					
					if (isSolid(check) || !BlockUtil.isInRenderDistance(check)) {
						continue outer;
					}
					
					if (getBlock(check) == Blocks.LAVA && ElytraBot.avoidLava.booleanValue()) {
						continue outer;
					}
					
					if (ElytraBot.maxY.intValue() != -1 && check.getY() > ElytraBot.maxY.intValue()) {
						continue outer;
					}
				}
				
				AStarNode n = AStarNode.getNodeFromBlockpos(pos);
				if (n == null) {
					n = new AStarNode(pos);
				}
				
				if (!open.contains(pos)) {
					list.add(pos);
				}
				
				if (n.parent == null || gCost(current, start) < gCost(n.parent, start)) {
					n.parent = current;
				}
			}
		}
		
		return list;
	}
	
	/**
	 * Calculates the f cost between pos and goal
	 */
	public static double fCost(BlockPos pos, BlockPos goal, BlockPos start) {
		// H cost
		double dx = goal.getX() - pos.getX();
		double dz = goal.getZ() - pos.getZ();
		double h = Math.sqrt(dx * dx + dz * dz);
		double fCost = gCost(pos, start) + h;
		
		return fCost;
	}
	
	/**
	 * Calculates the G Cost
	 */
	public static double gCost(BlockPos pos, BlockPos start) {
		double dx = start.getX() - pos.getX();
		double dy = start.getY() - pos.getY();
		double dz = start.getZ() - pos.getZ();
		return Math.sqrt(Math.abs(dx) + Math.abs(dy) + Math.abs(dz));
	}
	
	/**
	 * Gets the path by backtracing the closed list with the AStarNode things
	 */
	private static ArrayList<BlockPos> getPath(BlockPos current) {
		ArrayList<BlockPos> path = new ArrayList<BlockPos>();
		
		try {
			AStarNode n = AStarNode.getNodeFromBlockpos(current);
			if (n == null) {
				n = AStarNode.nodes.get(AStarNode.nodes.size() - 1);
			}
			path.add(n.pos);

			while (n != null && n.parent != null) {
				path.add(n.parent);
				n = AStarNode.getNodeFromBlockpos(n.parent);
			}
		} catch (IndexOutOfBoundsException e) {
			//Ingored. The path is zero in lenght
		}
		
		return path;
	}
	
	/**
	 * Used for backtracing the closed list to get the actual path
	 */
	public static class AStarNode {
		public static ArrayList<AStarNode> nodes = new ArrayList<AStarNode>();
		public BlockPos pos;
		public BlockPos parent;
		
		public AStarNode(BlockPos pos) {
			this.pos = pos;
			nodes.add(this);
		}
		
		public static AStarNode getNodeFromBlockpos(BlockPos pos) {
			for (AStarNode node : nodes) {
				if (node.pos.equals(pos)) {
					return node;
				}
			}
			
			return null;
		}
	}
}
