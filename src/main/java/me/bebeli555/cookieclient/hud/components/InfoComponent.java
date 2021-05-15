package me.bebeli555.cookieclient.hud.components;

import com.mojang.realmsclient.gui.ChatFormatting;

import me.bebeli555.cookieclient.gui.GuiSettings;
import me.bebeli555.cookieclient.hud.HudComponent;
import me.bebeli555.cookieclient.utils.ItemUtil;
import me.bebeli555.cookieclient.utils.PlayerUtil;
import net.minecraft.client.Minecraft;
import net.minecraft.client.resources.I18n;
import net.minecraft.item.ItemStack;
import net.minecraft.potion.Potion;
import net.minecraft.potion.PotionEffect;

public class InfoComponent extends HudComponent {
	public InfoComponent() {
		super(HudCorner.TOP_RIGHT, "Info");
	}
	
	@Override
	public void onRender(float partialTicks) {
		super.onRender(partialTicks);
		int amount = 0;
		
		//Render potions
		if (GuiSettings.potions.booleanValue()) {
			for (PotionEffect effect : mc.player.getActivePotionEffects()) {
				String text = I18n.format(effect.getPotion().getName());
				if (effect.getAmplifier() > 0) {
					text += " " + (effect.getAmplifier() + 1);
				}
				text += " " + w + Potion.getPotionDurationString(effect, 1);
				if (corner == HudCorner.TOP_LEFT || corner == HudCorner.TOP_RIGHT) {
					drawString(text, 0, amount * 10, effect.getPotion().getLiquidColor(), GuiSettings.infoShadow.booleanValue());
				} else {
					drawString(text, 0, -(amount * 10), effect.getPotion().getLiquidColor(), GuiSettings.infoShadow.booleanValue());
				}

				amount++;
			}
		}
		
		//Render speed
		if (GuiSettings.speed.booleanValue()) {
			renderInfo(g + "Speed " + w + decimal(PlayerUtil.getSpeed(mc.player) * 71.35, 1) + "km/h", amount);
			amount++;
		}
		
		//Render Ping
		if (GuiSettings.ping.booleanValue()) {
			if (!mc.isSingleplayer()) {
				try {
					renderInfo(g + "Ping " + w + mc.getConnection().getPlayerInfo(mc.renderViewEntity.getUniqueID()).getResponseTime() + "ms", amount);
					amount++;
				} catch (NullPointerException e) {

				}
			}
		}
		
		//Render durability
		if (GuiSettings.durability.booleanValue()) {
			ItemStack itemStack = mc.player.getHeldItemMainhand();
			if (ItemUtil.hasDurability(itemStack)) {
				renderInfo(g + "Durability " + ItemUtil.getDurabilityColor(itemStack) + ItemUtil.getDurability(itemStack), amount);
				amount++;
			}
		}
		
		//Render TPS
		if (GuiSettings.tps.booleanValue()) {
			renderInfo(g + "TPS " + w + decimal(LagNotifierComponent.getTps(), 2), amount);
			amount++;
		}
		
		//Render FPS
		if (GuiSettings.fps.booleanValue()) {
			renderInfo(g + "FPS " + w + Minecraft.getDebugFPS(), amount);
		}
	}
	
	@Override
	public boolean shouldRender() {
		return GuiSettings.speed.booleanValue() || GuiSettings.ping.booleanValue() || GuiSettings.durability.booleanValue() 
				|| GuiSettings.tps.booleanValue() || GuiSettings.fps.booleanValue() || GuiSettings.potions.booleanValue();
	}
	
	public void renderInfo(String text, int amount) {
		if (corner == HudCorner.TOP_LEFT || corner == HudCorner.TOP_RIGHT) {
			drawString(text, 0, amount * 10, -1, GuiSettings.infoShadow.booleanValue());
		} else {
			drawString(text, 0, -(amount * 10), -1, GuiSettings.infoShadow.booleanValue());
		}
	}

	//There doesnt seem to be an enum class i could compare them in 1.12.2 so ill just use the names
	public static ChatFormatting getPotionColor(PotionEffect potion) {
		return ChatFormatting.GRAY;
	}
}
 