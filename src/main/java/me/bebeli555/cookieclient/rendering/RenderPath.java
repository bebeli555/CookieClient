package me.bebeli555.cookieclient.rendering;

import java.awt.Color;
import java.util.ArrayList;

import net.minecraft.util.math.BlockPos;

public class RenderPath {
	public static ArrayList<BlockPos> path = new ArrayList<BlockPos>();
	public static Color color;
	
	public static void setPath(ArrayList<BlockPos> path, Color color) {
		RenderPath.path = path;
		RenderPath.color = color;
	}
	
	public static void clearPath() {
		path.clear();
	}
}
