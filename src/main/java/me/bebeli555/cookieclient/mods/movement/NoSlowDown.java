package me.bebeli555.cookieclient.mods.movement;

import me.bebeli555.cookieclient.Mod;
import me.bebeli555.cookieclient.events.bus.EventHandler;
import me.bebeli555.cookieclient.events.bus.Listener;
import me.bebeli555.cookieclient.events.entity.AttackEntityEvent;
import me.bebeli555.cookieclient.events.player.PlayerUpdateMoveStatePostEvent;
import me.bebeli555.cookieclient.gui.Group;
import me.bebeli555.cookieclient.gui.Mode;
import me.bebeli555.cookieclient.gui.Setting;
import me.bebeli555.cookieclient.mods.combat.KillAura;

public class NoSlowDown extends Mod {
	public static Setting items = new Setting(Mode.BOOLEAN, "Items", true, "Doesnt slow u down when using an item");
		public static Setting itemsSpeed = new Setting(items, Mode.DOUBLE, "Speed", 0.2, "Higher = slower", "0.2 = no slow down");
	public static Setting hit = new Setting(Mode.BOOLEAN, "Hit", true, "Doesn't stop u from sprinting when u hit an entity");
		
	public NoSlowDown() {
		super(Group.MOVEMENT, "NoSlowDown", "Doesnt slow u down for certain things", "Also allows you to set the speed so you can", "Play with the values and make them work for different servers");
	}
	
    @EventHandler
    private Listener<PlayerUpdateMoveStatePostEvent> onUpdateMoveState = new Listener<>(event -> {
    	if (items.booleanValue() && mc.player.isHandActive() && !mc.player.isRiding() && !mc.player.isElytraFlying()) {
            mc.player.movementInput.moveForward /= itemsSpeed.doubleValue();
            mc.player.movementInput.moveStrafe /= itemsSpeed.doubleValue();
    	}
    });
    
    @EventHandler
    private Listener<AttackEntityEvent> onAttackEntity = new Listener<>(event -> {
    	if (hit.booleanValue()) {
    		event.cancel();
    		KillAura.attackTargetEntityWithCurrentItem(event.target);
    	}
    });
}
