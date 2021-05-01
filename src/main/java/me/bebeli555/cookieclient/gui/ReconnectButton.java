package me.bebeli555.cookieclient.gui;

import me.bebeli555.cookieclient.hud.components.CoordsComponent;
import me.bebeli555.cookieclient.mods.misc.AutoReconnect;
import me.bebeli555.cookieclient.utils.InformationUtil;
import me.bebeli555.cookieclient.utils.Timer;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.multiplayer.GuiConnecting;

public class ReconnectButton extends GuiButton {
	public static Timer timer = new Timer();
	
    public ReconnectButton(int buttonId, int x, int y, String buttonText) {
        super(buttonId, x, y, buttonText);
        timer.reset();
    }

    @Override
    public void drawButton(Minecraft mc, int mouseX, int mouseY, float partialTicks) {
        super.drawButton(mc, mouseX, mouseY, partialTicks);
        
        if (visible) {
        	if (AutoReconnect.module.isOn()) {
        		this.displayString = "AutoReconnect: " + CoordsComponent.decimal((Math.abs(timer.ms + (long)AutoReconnect.delay.doubleValue() * 1000) - System.currentTimeMillis()) / (double)1000, 1);
        	} else {
        		this.displayString = "AutoReconnect";
        	}

            if (AutoReconnect.module.isOn() && timer.hasPassed((int)(AutoReconnect.delay.doubleValue() * 1000)) && !InformationUtil.lastIp.isEmpty() && InformationUtil.lastPort != -1) {
                mc.displayGuiScreen(new GuiConnecting(null, mc, InformationUtil.lastIp, InformationUtil.lastPort));
            }
        }
    }

    public static void clicked() {
    	AutoReconnect.module.toggle();
    	timer.reset();
    }
}