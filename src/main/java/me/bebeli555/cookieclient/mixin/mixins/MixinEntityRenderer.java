package me.bebeli555.cookieclient.mixin.mixins;

import java.util.ArrayList;
import java.util.List;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import com.google.common.base.Predicate;

import me.bebeli555.cookieclient.Mod;
import me.bebeli555.cookieclient.events.entity.GetEntitiesEvent;
import me.bebeli555.cookieclient.events.render.RenderHurtcamEvent;
import me.bebeli555.cookieclient.events.render.RenderUpdateLightMapEvent;
import me.bebeli555.cookieclient.events.render.SetupFogEvent;
import me.bebeli555.cookieclient.rendering.RenderUtil;
import net.minecraft.client.multiplayer.WorldClient;
import net.minecraft.client.renderer.EntityRenderer;
import net.minecraft.entity.Entity;
import net.minecraft.util.math.AxisAlignedBB;

@Mixin(EntityRenderer.class)
public class MixinEntityRenderer {

    @Inject(method = "setupFog", at = @At("HEAD"), cancellable = true)
    public void setupFog(int startCoords, float partialTicks, CallbackInfo callbackInfo) {
    	SetupFogEvent event = new SetupFogEvent();
    	Mod.EVENT_BUS.post(event);
    	
    	if (event.isCancelled()) {
    		callbackInfo.cancel();
    	}
    }
    
    @SuppressWarnings("unchecked")
	@Redirect(method = "getMouseOver", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/multiplayer/WorldClient;getEntitiesInAABBexcluding(Lnet/minecraft/entity/Entity;Lnet/minecraft/util/math/AxisAlignedBB;Lcom/google/common/base/Predicate;)Ljava/util/List;"))
    public List<Entity> getEntitiesInAABBexcluding(WorldClient worldClient, Entity entityIn, AxisAlignedBB boundingBox, @SuppressWarnings("rawtypes") Predicate predicate) {
        GetEntitiesEvent event = new GetEntitiesEvent();
        Mod.EVENT_BUS.post(event);
        
        if (event.isCancelled()) {
            return new ArrayList<>();
        } else {
            return worldClient.getEntitiesInAABBexcluding(entityIn, boundingBox, predicate);
        }
    }
    
    @Inject(method = "hurtCameraEffect", at = @At("HEAD"), cancellable = true)
    public void hurtCameraEffect(float ticks, CallbackInfo info) {
        RenderHurtcamEvent event = new RenderHurtcamEvent(ticks);
        Mod.EVENT_BUS.post(event);
        
        if (event.isCancelled()) {
            info.cancel();
        }
    }
    
    @Inject(method = "updateLightmap", at = @At("HEAD"), cancellable = true)
    private void updateLightmap(float partialTicks, CallbackInfo info) {
        RenderUpdateLightMapEvent event = new RenderUpdateLightMapEvent();
        Mod.EVENT_BUS.post(event);
        
        if (event.isCancelled()) {
            info.cancel();
        }
    }
    
    @Inject(method = "renderWorldPass", at = @At(value = "FIELD", target = "Lnet/minecraft/client/renderer/EntityRenderer;renderHand:Z", shift = At.Shift.AFTER))
    private void renderWorldPassPost(int pass, float partialTicks, long finishTimeNano, CallbackInfo callbackInfo) {
        RenderUtil.updateModelViewProjectionMatrix();
    }
}
