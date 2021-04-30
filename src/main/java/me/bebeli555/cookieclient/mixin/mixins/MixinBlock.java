package me.bebeli555.cookieclient.mixin.mixins;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import me.bebeli555.cookieclient.Mod;
import me.bebeli555.cookieclient.mods.render.XRay;
import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.util.BlockRenderLayer;
import net.minecraft.util.EnumBlockRenderType;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;

@Mixin(Block.class)
public class MixinBlock {
    @Inject(method = "getRenderLayer", at = @At("HEAD"), cancellable = true)
    public void getRenderLayer(CallbackInfoReturnable<BlockRenderLayer> cir) {
    	if (!XRay.isToggled) return;
    	if (XRay.shouldRender((Block)(Object)this)) {
    		cir.setReturnValue(BlockRenderLayer.CUTOUT);
    	}
    }
    
    @Inject(method = "isOpaqueCube", at = @At("HEAD"), cancellable = true)
    public void isOpaqueCube(IBlockState blockState, CallbackInfoReturnable<Boolean> cir) {
    	if (!XRay.isToggled) return;
    	cir.setReturnValue(XRay.shouldRender((Block)(Object)this));
    }
    
    @Inject(method = "shouldSideBeRendered", at = @At("HEAD"), cancellable = true)
    public void shouldSideBeRendered(IBlockState state, IBlockAccess access, BlockPos pos, EnumFacing side, CallbackInfoReturnable<Boolean> cir) {
    	if (!XRay.isToggled) return;
    	cir.setReturnValue(XRay.shouldRender((Block)(Object)this));
    }
    
    @Inject(method = "getLightValue", at = @At("HEAD"), cancellable = true)
    public void getLightValue(IBlockState state, CallbackInfoReturnable<Integer> cir) {
    	if (!XRay.isToggled) return;
    	
    	//For some reason if ur in the nether and return any other value than 0 here ur game will freeze
    	//But in the overworld it has no problem. So for now it doesnt change the light value for blocks in the nether
    	//Maybe its because nether has some blocks that cause that or the lighting is calculated differently there
    	if (Mod.mc.renderViewEntity != null && Mod.mc.renderViewEntity.dimension != -1) {
    		cir.setReturnValue(Integer.MAX_VALUE);
    	}
    }
    
    @Inject(method = "getLightOpacity", at = @At("HEAD"), cancellable = true)
    public void getLightOpacity(IBlockState state, CallbackInfoReturnable<Integer> cir) {
    	if (!XRay.isToggled) return;
    	cir.setReturnValue(Integer.MAX_VALUE);
    }
    
    @Inject(method = "getRenderType", at = @At("HEAD"), cancellable = true)
    public void getRenderType(IBlockState state, CallbackInfoReturnable<EnumBlockRenderType> cir) {
    	if (!XRay.isToggled) return;
    	if (!XRay.shouldRender((Block)(Object)this)) {
        	cir.setReturnValue(EnumBlockRenderType.INVISIBLE);	
    	}
    }
}
