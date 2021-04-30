package me.bebeli555.cookieclient.mixin.mixins;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import me.bebeli555.cookieclient.Mod;
import me.bebeli555.cookieclient.events.render.RenderBossHealthEvent;
import net.minecraft.client.gui.GuiBossOverlay;

@Mixin(GuiBossOverlay.class)
public class MixinGuiBossOverlay {
    @Inject(method = "renderBossHealth", at = @At("HEAD"), cancellable = true)
    public void renderBossHealth(CallbackInfo info) {
        RenderBossHealthEvent event = new RenderBossHealthEvent();
        Mod.EVENT_BUS.post(event);
        
        if (event.isCancelled()) {
            info.cancel();
        }
    }
}
