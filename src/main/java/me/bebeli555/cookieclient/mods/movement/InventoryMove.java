package me.bebeli555.cookieclient.mods.movement;

import org.lwjgl.input.Keyboard;

import me.bebeli555.cookieclient.Mod;
import me.bebeli555.cookieclient.events.bus.EventHandler;
import me.bebeli555.cookieclient.events.bus.Listener;
import me.bebeli555.cookieclient.events.player.PlayerUpdateMoveStatePostEvent;
import me.bebeli555.cookieclient.gui.Group;
import net.minecraft.client.gui.GuiChat;
import net.minecraft.client.gui.GuiScreenBook;
import net.minecraft.client.gui.inventory.GuiEditSign;
import net.minecraft.client.settings.KeyBinding;

public class InventoryMove extends Mod {
	public InventoryMove() {
		super(Group.MOVEMENT, "InventoryMove", "Allows you to move while having GUI's open");
	}
	
    @EventHandler
    private Listener<PlayerUpdateMoveStatePostEvent> onKeyPress = new Listener<>(event -> {
    	if (mc.currentScreen == null || mc.currentScreen instanceof GuiChat || mc.currentScreen instanceof GuiEditSign || mc.currentScreen instanceof GuiScreenBook) {
    		return;
    	}
    	
        mc.player.movementInput.moveStrafe = 0.0F;
        mc.player.movementInput.moveForward = 0.0F;
        
        KeyBinding.setKeyBindState(mc.gameSettings.keyBindSprint.getKeyCode(), Keyboard.isKeyDown(mc.gameSettings.keyBindSprint.getKeyCode()));
        if (Keyboard.isKeyDown(mc.gameSettings.keyBindSprint.getKeyCode())) {
            mc.player.setSprinting(true);
        }
        
        KeyBinding.setKeyBindState(mc.gameSettings.keyBindForward.getKeyCode(), Keyboard.isKeyDown(mc.gameSettings.keyBindForward.getKeyCode()));
        if (Keyboard.isKeyDown(mc.gameSettings.keyBindForward.getKeyCode())) {
            ++mc.player.movementInput.moveForward;
            mc.player.movementInput.forwardKeyDown = true;
        } else {
            mc.player.movementInput.forwardKeyDown = false;
        }

        KeyBinding.setKeyBindState(mc.gameSettings.keyBindBack.getKeyCode(), Keyboard.isKeyDown(mc.gameSettings.keyBindBack.getKeyCode()));
        if (Keyboard.isKeyDown(mc.gameSettings.keyBindBack.getKeyCode())) {
            --mc.player.movementInput.moveForward;
            mc.player.movementInput.backKeyDown = true;
        } else {
            mc.player.movementInput.backKeyDown = false;
        }

        KeyBinding.setKeyBindState(mc.gameSettings.keyBindLeft.getKeyCode(), Keyboard.isKeyDown(mc.gameSettings.keyBindLeft.getKeyCode()));
        if (Keyboard.isKeyDown(mc.gameSettings.keyBindLeft.getKeyCode())) {
            ++mc.player.movementInput.moveStrafe;
            mc.player.movementInput.leftKeyDown = true;
        } else {
            mc.player.movementInput.leftKeyDown = false;
        }

        KeyBinding.setKeyBindState(mc.gameSettings.keyBindRight.getKeyCode(), Keyboard.isKeyDown(mc.gameSettings.keyBindRight.getKeyCode()));
        if (Keyboard.isKeyDown(mc.gameSettings.keyBindRight.getKeyCode())) {
            --mc.player.movementInput.moveStrafe;
            mc.player.movementInput.rightKeyDown = true;
        } else {
            mc.player.movementInput.rightKeyDown = false;
        }

        KeyBinding.setKeyBindState(mc.gameSettings.keyBindJump.getKeyCode(), Keyboard.isKeyDown(mc.gameSettings.keyBindJump.getKeyCode()));
        mc.player.movementInput.jump = Keyboard.isKeyDown(mc.gameSettings.keyBindJump.getKeyCode());
    });
}
