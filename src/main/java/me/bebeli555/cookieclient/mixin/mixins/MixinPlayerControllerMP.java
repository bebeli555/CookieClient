package me.bebeli555.cookieclient.mixin.mixins;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;

import me.bebeli555.cookieclient.Mod;
import me.bebeli555.cookieclient.events.block.GetBlockReachDistanceEvent;
import me.bebeli555.cookieclient.events.other.ProcessRightClickBlockEvent;
import me.bebeli555.cookieclient.events.player.PlayerDamageBlockEvent;
import me.bebeli555.cookieclient.events.player.PlayerDamageBlockEvent2;
import me.bebeli555.cookieclient.events.player.PlayerDestroyBlockEvent;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.client.multiplayer.PlayerControllerMP;
import net.minecraft.client.multiplayer.WorldClient;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;

@Mixin(PlayerControllerMP.class)
public class MixinPlayerControllerMP {

	@Inject(method = "clickBlock", at = @At(value = "HEAD"), locals = LocalCapture.CAPTURE_FAILSOFT, cancellable = true)
    private void onPlayerDamageBlock(BlockPos loc, EnumFacing face, CallbackInfoReturnable<Boolean> cir) {
    	PlayerDamageBlockEvent event = new PlayerDamageBlockEvent(loc, face);
    	Mod.EVENT_BUS.post(event);
    	
    	if (event.isCancelled()) {
    		cir.cancel();
    	}
	}
	
    @Inject(method = "getBlockReachDistance", at = @At("HEAD"), cancellable = true)
    public void getBlockReachDistance(CallbackInfoReturnable<Float> callbackInfo) {
        GetBlockReachDistanceEvent event = new GetBlockReachDistanceEvent();
        Mod.EVENT_BUS.post(event);
        
        if (event.reach > 0) {
        	callbackInfo.setReturnValue(event.reach);
        	callbackInfo.cancel();
        }
    }
    
    @Inject(method = "onPlayerDestroyBlock", at = @At("HEAD"), cancellable = true)
    public void onPlayerDestroyBlock(BlockPos pos, CallbackInfoReturnable<Boolean> info) {
        PlayerDestroyBlockEvent event = new PlayerDestroyBlockEvent(pos);
        Mod.EVENT_BUS.post(event);

        if (event.isCancelled()) {
            info.setReturnValue(false);
            info.cancel();
        }
    }
    
    @Inject(method = "processRightClickBlock", at = @At("HEAD"), cancellable = true)
    public void processRightClickBlock(EntityPlayerSP player, WorldClient world, BlockPos pos, EnumFacing facing, Vec3d vec, EnumHand hand, CallbackInfoReturnable<EnumActionResult> info) {
        ProcessRightClickBlockEvent event = new ProcessRightClickBlockEvent(player, world, pos, facing, vec, hand);
        Mod.EVENT_BUS.post(event);

        if (event.isCancelled()) {
            info.setReturnValue(EnumActionResult.SUCCESS);
            info.cancel();
        }
    }
    
    @Inject(method = "onPlayerDamageBlock", at = @At("HEAD"), cancellable = true)
    public void onPlayerDamageBlock2(BlockPos posBlock, EnumFacing directionFacing, CallbackInfoReturnable<Boolean> p_Info) {
        PlayerDamageBlockEvent2 event = new PlayerDamageBlockEvent2(posBlock, directionFacing);
        Mod.EVENT_BUS.post(event);
        
        if (event.isCancelled()) {
            p_Info.setReturnValue(false);
            p_Info.cancel();
        }
    }
}
