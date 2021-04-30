package me.bebeli555.cookieclient.mods.render;

import me.bebeli555.cookieclient.Mod;
import me.bebeli555.cookieclient.gui.Group;
import me.bebeli555.cookieclient.gui.Mode;
import me.bebeli555.cookieclient.gui.Setting;
import me.bebeli555.cookieclient.rendering.RenderUtil;
import me.bebeli555.cookieclient.utils.EntityUtil;
import net.minecraft.entity.Entity;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.player.EntityPlayer;

public class EntityESP extends Mod {
	public static Setting players = new Setting(Mode.BOOLEAN, "Players", true);
	public static Setting monsters = new Setting(Mode.BOOLEAN, "Monsters", true);
	public static Setting neutrals = new Setting(Mode.BOOLEAN, "Neutrals", true);
	public static Setting passive = new Setting(Mode.BOOLEAN, "Passive", true);
	public static Setting items = new Setting(Mode.BOOLEAN, "Items", true);
	public static Setting everything = new Setting(Mode.BOOLEAN, "Everything", false, "Highlight every entity");
	public static Setting red = new Setting(Mode.INTEGER, "Red", 66, "RBG");
	public static Setting green = new Setting(Mode.INTEGER, "Green", 245, "RBG");
	public static Setting blue = new Setting(Mode.INTEGER, "Blue", 185, "RBG");
	public static Setting alpha = new Setting(Mode.INTEGER, "Alpha", 255, "RBG");
	public static Setting width = new Setting(Mode.DOUBLE, "Width", 1, "The width of the rendered lines");
	
	public EntityESP() {
		super(Group.RENDER, "EntityESP", "Highlight entities hitboxes");
	}
	
	@Override
	public void onRenderWorld(float partialTicks) {
		for (Entity entity : mc.world.loadedEntityList) {
			if (isValid(entity)) {
				RenderUtil.renderHitBox(entity, red.intValue() / 255.0f, green.intValue() / 255.0f, blue.intValue() / 255.0f, alpha.intValue() / 255.0f, (float)width.doubleValue(), partialTicks);
			}
		}
	}
	
	public static boolean isValid(Entity entity) {
		if (entity.equals(mc.renderViewEntity)) {
			return false;
		}
		
		if (everything.booleanValue()) {
			return true;
		}
		
		if (players.booleanValue() && entity instanceof EntityPlayer) {
			return true;
		} else if (monsters.booleanValue() && EntityUtil.isHostileMob(entity)) {
			return true;
		} else if (neutrals.booleanValue() && EntityUtil.isNeutralMob(entity)) {
			return true;
		} else if (passive.booleanValue() && EntityUtil.isPassive(entity)) {
			return true;
		} else if (items.booleanValue() && entity instanceof EntityItem) {
			return true;
		}
		
		return false;
	}
}
