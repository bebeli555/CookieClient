package me.bebeli555.cookieclient.mixin.mixins;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import me.bebeli555.cookieclient.Mod;
import me.bebeli555.cookieclient.events.entity.AttackEntityEvent;
import me.bebeli555.cookieclient.events.entity.EntityJumpEvent;
import me.bebeli555.cookieclient.events.entity.EntityPushEvent;
import me.bebeli555.cookieclient.events.player.TravelEvent;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.MoverType;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.world.World;

@Mixin(EntityPlayer.class)
public abstract class MixinEntityPlayer extends EntityLivingBase {
	
	public MixinEntityPlayer(World worldIn) {
		super(worldIn);
	}
	
    @Inject(method = "applyEntityCollision", at = @At("HEAD"), cancellable = true)
    public void applyEntityCollision(Entity entity, CallbackInfo callbackInfo) {
    	EntityPushEvent event = new EntityPushEvent(entity);
    	Mod.EVENT_BUS.post(event);
    	
    	if (event.isCancelled()) {
    		callbackInfo.cancel();
    	}
    }
	
    @Inject(method = "isPushedByWater()Z", at = @At("HEAD"), cancellable = true)
    public void isPushedByWater(CallbackInfoReturnable<Boolean> callbackInfo) {
    	EntityPushEvent event = new EntityPushEvent(null);
    	Mod.EVENT_BUS.post(event);
    	
    	if (event.isCancelled()) {
    		callbackInfo.cancel();
    	}
    }
    
    @Inject(method = "travel", at = @At("HEAD"), cancellable = true)
    public void travel(float strafe, float vertical, float forward, CallbackInfo callbackInfo) {
    	TravelEvent event = new TravelEvent();
    	Mod.EVENT_BUS.post(event);
    	
    	if (event.isCancelled()) {
    		move(MoverType.SELF, motionX, motionY, motionZ);
    		callbackInfo.cancel();
    	}
    }
    
    @Inject(method = "jump", at = @At("HEAD"), cancellable = true)
    public void jump(CallbackInfo callbackInfo) {
    	EntityJumpEvent event = new EntityJumpEvent((EntityPlayer)(Object)this);
    	Mod.EVENT_BUS.post(event);
    	
    	if (event.isCancelled()) {
    		callbackInfo.cancel();
    	}
    }
    
    @Inject(method = "attackTargetEntityWithCurrentItem", at = @At("HEAD"), cancellable = true)
    public void attackTargetEntityWithCurrentItem(Entity target, CallbackInfo callbackInfo) {
    	AttackEntityEvent event = new AttackEntityEvent(target);
    	Mod.EVENT_BUS.post(event);
    	
    	if (event.isCancelled()) {
    		callbackInfo.cancel();
    	}
    }
}
