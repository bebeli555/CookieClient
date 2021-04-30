package me.bebeli555.cookieclient.mixin.mixins;

import org.spongepowered.asm.lib.Opcodes;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

import me.bebeli555.cookieclient.Mod;
import me.bebeli555.cookieclient.events.render.RenderSignEvent;
import net.minecraft.client.renderer.tileentity.TileEntitySignRenderer;
import net.minecraft.tileentity.TileEntitySign;
import net.minecraft.util.text.ITextComponent;

@Mixin(TileEntitySignRenderer.class)
public class MixinTileEntitySignRenderer {
    @Redirect(method = "render", at = @At(value = "FIELD", target = "Lnet/minecraft/tileentity/TileEntitySign;signText:[Lnet/minecraft/util/text/ITextComponent;", opcode = Opcodes.GETFIELD))
    public ITextComponent[] getRenderViewEntity(TileEntitySign sign) {
    	RenderSignEvent event = new RenderSignEvent();
        Mod.EVENT_BUS.post(event);
        
        if (event.isCancelled()) {
        	return new ITextComponent[]{};
        }
        
        return sign.signText;
    }
}
