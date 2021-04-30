package me.bebeli555.cookieclient.mods.movement;

import me.bebeli555.cookieclient.Mod;
import me.bebeli555.cookieclient.gui.Group;
import me.bebeli555.cookieclient.gui.Mode;
import me.bebeli555.cookieclient.gui.Setting;
import net.minecraft.init.Blocks;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent.ClientTickEvent;

@SuppressWarnings("deprecation")
public class IceSpeed extends Mod {
	public static Setting speed = new Setting(Mode.DOUBLE, "Speed", 1, "How slippery it will make the ice", "Vanilla value = 0.97");
	
	public IceSpeed() {
		super(Group.MOVEMENT, "IceSpeed", "Move faster on ice");
	}

	@Override
    public void onDisabled() {
        Blocks.ICE.slipperiness = Blocks.PACKED_ICE.slipperiness = Blocks.FROSTED_ICE.slipperiness = 0.97f;
    }
	
	@SubscribeEvent
	public void onTick(ClientTickEvent e) {
		Blocks.ICE.slipperiness = Blocks.PACKED_ICE.slipperiness = Blocks.FROSTED_ICE.slipperiness = (float)speed.doubleValue();
	}
}
