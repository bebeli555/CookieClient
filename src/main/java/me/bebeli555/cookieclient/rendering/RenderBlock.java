package me.bebeli555.cookieclient.rendering;

import java.awt.Color;
import java.util.ArrayList;

import net.minecraft.util.math.BlockPos;

public class RenderBlock {
	public static ArrayList<BlockColor> list = new ArrayList<BlockColor>();
	
	public static void add(BlockPos pos, Color color, float width) {
		list.add(new BlockColor(pos, color, width));
	}
	
	public static void remove(BlockPos pos) {
		ArrayList<BlockColor> remove = new ArrayList<BlockColor>();
		for (BlockColor blockColor : list) {
			if (blockColor.pos.equals(pos)) {
				remove.add(blockColor);
			}
		}
		
		list.removeAll(remove);
	}
	
	public static void clear() {
		list.clear();
	}
	
	public static class BlockColor {
		public BlockPos pos;
		public Color color;
		public float width;
		
		public BlockColor(BlockPos pos, Color color, float width) {
			this.pos = pos;
			this.color = color;
			this.width = width;
		}
	}
}
