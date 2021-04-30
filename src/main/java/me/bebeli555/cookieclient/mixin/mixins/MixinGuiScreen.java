package me.bebeli555.cookieclient.mixin.mixins;

import java.util.List;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.At;

import me.bebeli555.cookieclient.Mod;
import me.bebeli555.cookieclient.events.render.RenderTooltipEvent;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.item.ItemStack;

@Mixin(GuiScreen.class)
public class MixinGuiScreen {
    @Shadow protected List<GuiButton> buttonList;
    @Shadow public int width;
    @Shadow public int height;
    @Shadow protected FontRenderer fontRenderer;
	
    @Inject(method = "renderToolTip", at = @At("HEAD"), cancellable = true)
    public void renderToolTip(ItemStack stack, int x, int y, CallbackInfo callbackInfo) {
        RenderTooltipEvent event = new RenderTooltipEvent(stack, x, y);
        Mod.EVENT_BUS.post(event);
        
        if (event.isCancelled()) {
        	callbackInfo.cancel();
        }
    }
}
