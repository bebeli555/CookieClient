package me.bebeli555.cookieclient.utils;

import me.bebeli555.cookieclient.events.bus.EventHandler;
import me.bebeli555.cookieclient.events.bus.Listener;
import me.bebeli555.cookieclient.events.other.PacketEvent;
import net.minecraft.network.EnumConnectionState;
import net.minecraft.network.handshake.client.C00Handshake;

/**
 * This is allways subscribed class so the listeners will allways get called
 */
public class InformationUtil {
    public static String lastIp = "";
    public static int lastPort = -1;
    
    @EventHandler
    private Listener<PacketEvent> packetEvent = new Listener<>(event -> {
    	//Save last port and ip. This is used for autoreconnect and possibly other stuff
    	if (event.packet instanceof C00Handshake) {
            C00Handshake packet = (C00Handshake)event.packet;
            if (packet.getRequestedState() == EnumConnectionState.LOGIN) {
                lastIp = packet.ip;
                lastPort = packet.port;
            }
        }
    });
}
