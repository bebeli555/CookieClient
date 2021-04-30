package me.bebeli555.cookieclient.mixin.mixins;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import me.bebeli555.cookieclient.Mod;
import me.bebeli555.cookieclient.events.render.RenderMapEvent;
import net.minecraft.client.gui.MapItemRenderer;
import net.minecraft.world.storage.MapData;

@Mixin(MapItemRenderer.class)
public class MixinMapItemRenderer {
    @Inject(method = "renderMap", at = @At("HEAD"), cancellable = true)
    public void render(MapData mapdataIn, boolean noOverlayRendering, CallbackInfo info) {
        RenderMapEvent event = new RenderMapEvent();
        Mod.EVENT_BUS.post(event);
        
        if (event.isCancelled()) {
        	info.cancel();
        }
    }
}