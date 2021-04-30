package me.bebeli555.cookieclient.mixin.mixins;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import org.spongepowered.asm.mixin.injection.At;

import me.bebeli555.cookieclient.Mod;
import me.bebeli555.cookieclient.events.entity.EntitySaddledEvent;
import me.bebeli555.cookieclient.events.entity.SteerEntityEvent;
import net.minecraft.entity.passive.AbstractHorse;

@Mixin(AbstractHorse.class)
public class MixinAbstractHorse {
    @Inject(method = "canBeSteered", at = @At("HEAD"), cancellable = true)
    public void canBeSteered(CallbackInfoReturnable<Boolean> cir) {
        SteerEntityEvent event = new SteerEntityEvent();
        Mod.EVENT_BUS.post(event);

        if (event.isCancelled()) {
            cir.cancel();
            cir.setReturnValue(true);
        }
    }

    @Inject(method = "isHorseSaddled", at = @At("HEAD"), cancellable = true)
    public void isHorseSaddled(CallbackInfoReturnable<Boolean> cir) {
        EntitySaddledEvent event = new EntitySaddledEvent();
        Mod.EVENT_BUS.post(event);

        if (event.isCancelled()) {
            cir.cancel();
            cir.setReturnValue(true);
        }
    }
}
