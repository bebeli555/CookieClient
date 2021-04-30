package me.bebeli555.cookieclient.mixin.mixins;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import me.bebeli555.cookieclient.Mod;
import me.bebeli555.cookieclient.events.render.RenderEntityNameEvent;
import me.bebeli555.cookieclient.mods.render.Freecam;
import net.minecraft.client.entity.AbstractClientPlayer;
import net.minecraft.client.model.ModelBase;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.entity.RenderLivingBase;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.client.renderer.entity.RenderPlayer;

@Mixin(RenderPlayer.class)
public abstract class MixinRenderPlayer extends RenderLivingBase<AbstractClientPlayer> {
    public MixinRenderPlayer(RenderManager renderManagerIn, ModelBase modelBaseIn, float shadowSizeIn) {
		super(renderManagerIn, modelBaseIn, shadowSizeIn);
	}

    @Shadow protected abstract void setModelVisibilities(AbstractClientPlayer clientPlayer);
    
	@Inject(method = "doRender", at = @At(value = "FIELD", target = "Lnet/minecraft/client/renderer/entity/RenderManager;renderViewEntity:Lnet/minecraft/entity/Entity;"))
    public void doRenderGetRenderViewEntity(AbstractClientPlayer entity, double x, double y, double z, float entityYaw, float partialTicks, CallbackInfo ci) {
        if (Freecam.isToggled && !Mod.mc.renderViewEntity.equals(entity)) {
            double renderY = y;

            if (entity.isSneaking()) {
                renderY = y - 0.125D;
            }

            this.setModelVisibilities(entity);
            GlStateManager.enableBlendProfile(GlStateManager.Profile.PLAYER_SKIN);
            super.doRender(entity, x, renderY, z, entityYaw, partialTicks);
            GlStateManager.disableBlendProfile(GlStateManager.Profile.PLAYER_SKIN);
        }
    }
	
    @Inject(method = "renderEntityName", at = @At("HEAD"), cancellable = true)
    public void renderLivingLabel(AbstractClientPlayer entityIn, double x, double y, double z, String name, double distanceSq, CallbackInfo info) {
        RenderEntityNameEvent event = new RenderEntityNameEvent(entityIn, x, y, z, name, distanceSq);
        Mod.EVENT_BUS.post(event);
        
        if (event.isCancelled()) {
            info.cancel();
        }
    }
}
