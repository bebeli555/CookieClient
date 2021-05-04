package me.bebeli555.cookieclient.hud.components;

import me.bebeli555.cookieclient.Mod;
import me.bebeli555.cookieclient.events.bus.EventHandler;
import me.bebeli555.cookieclient.events.bus.Listener;
import me.bebeli555.cookieclient.events.other.PacketEvent;
import me.bebeli555.cookieclient.events.other.PacketServerEvent;
import me.bebeli555.cookieclient.gui.GuiSettings;
import me.bebeli555.cookieclient.hud.HudComponent;
import me.bebeli555.cookieclient.hud.HudEditor;
import net.minecraft.network.play.server.SPacketTimeUpdate;
import net.minecraft.util.math.MathHelper;

public class LagNotifierComponent extends HudComponent {
    private static long prevTime, lastServerPacket;
    private static float[] ticks = new float[20];
    private static int currentTick;
	
	public LagNotifierComponent() {
		super(HudCorner.NONE, "LagNotifier");
		Mod.EVENT_BUS.subscribe(this);
	}
	
	@Override
	public void onRender(float partialTicks) {
		super.onRender(partialTicks);
		
		if (lastServerPacket != -1 && Math.abs(System.currentTimeMillis() - lastServerPacket) > 3500 && !mc.isSingleplayer() || HudEditor.module.isOn()) {
			String seconds = decimal((double)Math.abs(System.currentTimeMillis() - lastServerPacket) / (double)1000, 1);
			String text = g + "Server not responding " + w + seconds + "s";
			drawString(text, mc.displayWidth / 4 - mc.fontRenderer.getStringWidth(text) / 2, 2, -1, true);
		}
	}
	
	@Override
	public boolean shouldRender() {
		return GuiSettings.lagNotifier.booleanValue();
	}
	
	public static double getTps() {
        int tickCount = 0;
        float tickRate = 0.0f;

        for (int i = 0; i < ticks.length; i++) {
            final float tick = ticks[i];

            if (tick > 0.0f) {
                tickRate += tick;
                tickCount++;
            }
        }

        return MathHelper.clamp((tickRate / tickCount), 0.0f, 20.0f);
	}
	
	@EventHandler
	private Listener<PacketEvent> packetEvent = new Listener<>(event -> {
        if (event.packet instanceof SPacketTimeUpdate) {
            if (prevTime != -1) {
                ticks[currentTick % ticks.length] = MathHelper.clamp((20.0f / ((float) (System.currentTimeMillis() - prevTime) / 1000.0f)), 0.0f, 20.0f);
                currentTick++;
            }

            prevTime = System.currentTimeMillis();
        }
	});
	
	@EventHandler
	private Listener<PacketServerEvent> packetServerEvent = new Listener<>(event -> {
		lastServerPacket = System.currentTimeMillis();
	});
}
