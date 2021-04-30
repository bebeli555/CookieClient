package me.bebeli555.cookieclient.mixin.mixins;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import me.bebeli555.cookieclient.Mod;
import me.bebeli555.cookieclient.events.render.RenderArmorLayerEvent;
import net.minecraft.client.renderer.entity.layers.LayerArmorBase;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.inventory.EntityEquipmentSlot;

@Mixin(LayerArmorBase.class)
public class MixinLayerArmorBase {
    @Inject(method = "renderArmorLayer", at = @At("HEAD"), cancellable = true)
    public void renderArmorLayer(EntityLivingBase entity, float limbSwing, float limbSwingAmount, float partialTicks, float ageInTicks, float netHeadYaw, float headPitch, float scale, EntityEquipmentSlot slotIn, CallbackInfo info) {
        RenderArmorLayerEvent event = new RenderArmorLayerEvent();
        Mod.EVENT_BUS.post(event);
        
        if (event.isCancelled()) {
            info.cancel();
        }
    }
}