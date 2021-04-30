package me.bebeli555.cookieclient.mixin.mixins;

import java.util.EnumSet;
import java.util.Set;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import org.spongepowered.asm.mixin.injection.At;

import me.bebeli555.cookieclient.Mod;
import me.bebeli555.cookieclient.mods.render.Freecam;
import me.bebeli555.cookieclient.mods.render.Tracers;
import net.minecraft.client.renderer.chunk.VisGraph;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;

@Mixin(VisGraph.class)
public class MixinVisGraph {
    @Inject(method = "getVisibleFacings", at = @At("HEAD"), cancellable = true)
    public void getVisibleFacings(CallbackInfoReturnable<Set<EnumFacing>> ci) {
        if (!Freecam.isToggled || Mod.mc.world == null) return;
        
        Vec3d camPos = Tracers.interpolateEntity(Mod.mc.renderViewEntity, Mod.mc.getRenderPartialTicks());
        BlockPos blockPos = new BlockPos(camPos.x, camPos.y, camPos.z);
        if (Mod.mc.world.getBlockState(blockPos).isFullBlock()) {
            ci.setReturnValue(EnumSet.allOf(EnumFacing.class));
        }
    }
}
