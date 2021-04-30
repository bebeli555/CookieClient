package me.bebeli555.cookieclient.mixin.mixins;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import io.netty.channel.ChannelHandlerContext;
import me.bebeli555.cookieclient.Mod;
import me.bebeli555.cookieclient.events.other.PacketEvent;
import me.bebeli555.cookieclient.events.other.PacketPostEvent;
import me.bebeli555.cookieclient.events.other.PacketServerEvent;
import me.bebeli555.cookieclient.gui.Settings;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.Packet;
import net.minecraft.util.text.ITextComponent;

@Mixin(NetworkManager.class)
public class MixinNetworkManager {
	
    @Inject(method = "sendPacket(Lnet/minecraft/network/Packet;)V", at = @At("HEAD"), cancellable = true)
    private void onPacketSend(Packet<?> packet, CallbackInfo callbackInfo) {
    	PacketEvent event = new PacketEvent(packet);
    	Mod.EVENT_BUS.post(event);
    	
    	if (event.isCancelled()) {
    		callbackInfo.cancel();
    	}
    }
    
    @Inject(method = "channelRead0", at = @At("HEAD"), cancellable = true)
    private void onChannelRead(ChannelHandlerContext context, Packet<?> packet, CallbackInfo callbackInfo) {
    	PacketEvent event = new PacketEvent(packet);
    	Mod.EVENT_BUS.post(event);
    	Mod.EVENT_BUS.post(new PacketServerEvent(packet));
    	
    	if (event.isCancelled()) {
    		callbackInfo.cancel();
    	}
    }
    
    @Inject(method = "sendPacket(Lnet/minecraft/network/Packet;)V", at = @At("RETURN"))
    private void onPostSendPacket(Packet<?> packet, CallbackInfo callbackInfo) {
      	PacketPostEvent event = new PacketPostEvent(packet);
    	Mod.EVENT_BUS.post(event);
    }

    @Inject(method = "channelRead0", at = @At("RETURN"))
    private void onPostChannelRead(ChannelHandlerContext context, Packet<?> packet, CallbackInfo callbackInfo) {
    	PacketPostEvent event = new PacketPostEvent(packet);
    	Mod.EVENT_BUS.post(event);
    }
    
    @Inject(method = "closeChannel", at = @At("RETURN"))
    private void closeChannel(ITextComponent message, CallbackInfo callbackInfo) {
    	//Also saves the settings when connection to server is closed.
    	//Usually it saves them when u close the gui but if you just use keybinds and never open it it wouldnt save so this will save it
    	Settings.saveSettings();
    }
}
