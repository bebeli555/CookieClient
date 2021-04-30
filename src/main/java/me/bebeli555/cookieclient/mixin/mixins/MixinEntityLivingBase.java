package me.bebeli555.cookieclient.mixin.mixins;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import me.bebeli555.cookieclient.Mod;
import me.bebeli555.cookieclient.events.other.IsPotionEffectActiveEvent;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.potion.Potion;

@Mixin(EntityLivingBase.class)
public class MixinEntityLivingBase {
    @Inject(method = "isPotionActive", at = @At("HEAD"), cancellable = true)
    public void isPotionActive(Potion potionIn, final CallbackInfoReturnable<Boolean> callbackInfoReturnable) {
        IsPotionEffectActiveEvent event = new IsPotionEffectActiveEvent(potionIn);
        Mod.EVENT_BUS.post(event);

        if (event.isCancelled()) {
        	callbackInfoReturnable.setReturnValue(false);
        }
    }
}
