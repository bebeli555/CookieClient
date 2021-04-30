package me.bebeli555.cookieclient.mixin.mixins;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import me.bebeli555.cookieclient.Mod;
import me.bebeli555.cookieclient.utils.RotationUtil;
import net.minecraft.client.renderer.entity.RenderLivingBase;
import net.minecraft.entity.EntityLivingBase;

@Mixin(value = RenderLivingBase.class, priority = Integer.MAX_VALUE)
public class MixinRenderLivingBase {
	private float oldPitch, oldPrevPitch;
	
    @Inject(method = "doRender", at = @At("HEAD"), cancellable = true)
    public void doRender(EntityLivingBase entity, double x, double y, double z, float entityYaw, float partialTicks, CallbackInfo info) {
    	if (RotationUtil.isRotateSpoofing && entity == Mod.mc.player) {
    		oldPitch = entity.rotationPitch;
    		oldPrevPitch = entity.prevRotationPitch;
    		
    		Mod.mc.player.rotationPitch = RotationUtil.pitch;
    		Mod.mc.player.prevRotationPitch = RotationUtil.pitch;
    		Mod.mc.player.rotationYawHead = RotationUtil.yaw;
    	}
    }
    
    @Inject(method = "doRender", at = @At("RETURN"), cancellable = true)
    public void doRenderPost(EntityLivingBase entity, double x, double y, double z, float entityYaw, float partialTicks, CallbackInfo info) {
    	if (RotationUtil.isRotateSpoofing && entity == Mod.mc.player) {
    		Mod.mc.player.rotationPitch = oldPitch;
    		Mod.mc.player.prevRotationPitch = oldPrevPitch;
    	}
    }
}