package me.bebeli555.cookieclient.mods.render;

import java.util.ArrayList;

import me.bebeli555.cookieclient.Mod;
import me.bebeli555.cookieclient.events.bus.EventHandler;
import me.bebeli555.cookieclient.events.bus.Listener;
import me.bebeli555.cookieclient.events.player.PlayerUpdateEvent;
import me.bebeli555.cookieclient.gui.Group;
import me.bebeli555.cookieclient.gui.Mode;
import me.bebeli555.cookieclient.gui.Setting;
import me.bebeli555.cookieclient.rendering.RenderUtil;
import me.bebeli555.cookieclient.utils.BlockUtil;
import me.bebeli555.cookieclient.utils.Timer;
import net.minecraft.init.Blocks;
import net.minecraft.util.math.BlockPos;

public class VoidESP extends Mod {
	private static Timer timer = new Timer();
	private static ArrayList<BlockPos> voidBlocks = new ArrayList<BlockPos>();
	
	public static Setting radius = new Setting(Mode.INTEGER, "Radius", 15, "Radius around the player to search for them");
	public static Setting red = new Setting(Mode.INTEGER, "Red", 66, "RBG");
	public static Setting green = new Setting(Mode.INTEGER, "Green", 245, "RBG");
	public static Setting blue = new Setting(Mode.INTEGER, "Blue", 185, "RBG");
	public static Setting alpha = new Setting(Mode.INTEGER, "Alpha", 255, "RBG");
	public static Setting width = new Setting(Mode.DOUBLE, "Width", 1, "The width of the rendered lines");
	
	public VoidESP() {
		super(Group.RENDER, "VoidESP", "Highlighst void holes");
	}
	
    @EventHandler
    private Listener<PlayerUpdateEvent> onPlayerUpdate = new Listener<>(event -> {
		if (timer.hasPassed(250) && mc.player.ticksExisted > 10) {
			timer.reset();
			
			voidBlocks.clear();
			for (BlockPos pos : BlockUtil.getAll(radius.intValue())) {
				if (isVoidHole(pos)) {
					voidBlocks.add(pos);
				}
			}
		}
    });
	
	@Override
	public void onRenderWorld(float partialTicks) {
		for (BlockPos pos : voidBlocks) {
			RenderUtil.drawBoundingBox(RenderUtil.getBB(pos, 1), (float)width.doubleValue(), red.intValue() / 255.0f, green.intValue() / 255.0f, blue.intValue() / 255.0f, alpha.intValue() / 255.0f);
		}
	}
	
	public static boolean isVoidHole(BlockPos pos) {
        if (pos.getY() > 4 || pos.getY() <= 0) {
            return false;
        }

        BlockPos pos2 = pos;
        for (int i = pos.getY(); i >= 0; --i)  {
            if (mc.world.getBlockState(pos2).getBlock() != Blocks.AIR) {
                return false;
            }

            pos2 = pos2.down();
        }

        return true;
	}
}
