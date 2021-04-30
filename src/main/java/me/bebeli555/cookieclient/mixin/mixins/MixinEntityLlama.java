package me.bebeli555.cookieclient.mixin.mixins;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import me.bebeli555.cookieclient.Mod;
import me.bebeli555.cookieclient.events.entity.SteerEntityEvent;
import net.minecraft.entity.passive.EntityLlama;

@Mixin(EntityLlama.class)
public class MixinEntityLlama {
    @Inject(method = "canBeSteered", at = @At("HEAD"), cancellable = true)
    public void canBeSteered(CallbackInfoReturnable<Boolean> cir) {
        SteerEntityEvent event = new SteerEntityEvent();
        Mod.EVENT_BUS.post(event);

        if (event.isCancelled()) {
            cir.cancel();
            cir.setReturnValue(true);
        }
    }
}
