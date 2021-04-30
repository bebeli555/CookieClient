package me.bebeli555.cookieclient.mixin.mixins;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import me.bebeli555.cookieclient.Mod;
import me.bebeli555.cookieclient.events.entity.EntityAddedEvent;
import me.bebeli555.cookieclient.events.entity.EntityRemovedEvent;
import me.bebeli555.cookieclient.events.render.GetRainStrenghtEvent;
import net.minecraft.entity.Entity;
import net.minecraft.world.World;

@Mixin(World.class)
public class MixinWorld {

    @Inject(method = "onEntityAdded", at = @At("HEAD"), cancellable = true)
    public void onEntityAdded(Entity entity, CallbackInfo callbackInfo) {
        EntityAddedEvent event = new EntityAddedEvent(entity);

        Mod.EVENT_BUS.post(event);
        if (event.isCancelled()) {
        	callbackInfo.cancel();
        }
    }

    @Inject(method = "onEntityRemoved", at = @At("HEAD"), cancellable = true)
    public void onEntityRemoved(Entity entity, CallbackInfo callbackInfo) {
        EntityRemovedEvent event = new EntityRemovedEvent(entity);

        Mod.EVENT_BUS.post(event);
        if (event.isCancelled()) {
        	callbackInfo.cancel();
        }
    }
    
    @Inject(method = "getRainStrength", at = @At("HEAD"), cancellable = true)
    private void getRainStrengthHead(float delta, CallbackInfoReturnable<Float> cir) {
        GetRainStrenghtEvent event = new GetRainStrenghtEvent(delta);

        Mod.EVENT_BUS.post(event);
        if (event.isCancelled()) {
        	cir.setReturnValue(event.value);
        }
    }
}
