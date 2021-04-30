package me.bebeli555.cookieclient.mixin.mixins;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import me.bebeli555.cookieclient.gui.GuiSettings;
import net.minecraft.client.gui.GuiIngame;
import net.minecraft.client.gui.ScaledResolution;

@Mixin(GuiIngame.class)
public class MixinGuiIngame {
    @Inject(method = "renderPotionEffects", at = @At("HEAD"), cancellable = true)
    public void renderPotionEffects(ScaledResolution resolution, CallbackInfo info) {
    	if (GuiSettings.hud.booleanValue() && GuiSettings.potions.booleanValue()) {
    		info.cancel();
    	}
    }
}