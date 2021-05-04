package me.bebeli555.cookieclient.mods.world;

import me.bebeli555.cookieclient.Mod;
import me.bebeli555.cookieclient.events.bus.EventHandler;
import me.bebeli555.cookieclient.events.bus.Listener;
import me.bebeli555.cookieclient.events.player.PlayerDamageBlockEvent2;
import me.bebeli555.cookieclient.events.player.PlayerUpdateEvent;
import me.bebeli555.cookieclient.gui.Group;
import me.bebeli555.cookieclient.gui.Mode;
import me.bebeli555.cookieclient.gui.Setting;
import me.bebeli555.cookieclient.utils.InventoryUtil;
import me.bebeli555.cookieclient.utils.InventoryUtil.ItemStackUtil;
import me.bebeli555.cookieclient.utils.Timer;
import net.minecraft.block.material.Material;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.init.MobEffects;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.event.ForgeEventFactory;

public class AutoTool extends Mod {
	private Timer timer = new Timer();
	private int oldSlot;
	
	public static Setting switchBack = new Setting(Mode.BOOLEAN, "SwitchBack", true, "Switches back to old slot when done");
	
	public AutoTool() {
		super(Group.WORLD, "AutoTool", "Switches to best tool when breaking blocks");
	}
	
    @EventHandler
    private Listener<PlayerDamageBlockEvent2> onDamageBlock = new Listener<>(event -> {
    	ItemStackUtil best = null;
    	float bestSpeed = -1;
    	
    	for (int i = 0; i < 9; i++) {
    		ItemStack stack = InventoryUtil.getItemStack(i);
    		float speed = getBreakSpeed(event.pos, stack);
    		
    		if (speed > bestSpeed) {
    			bestSpeed = speed;
    			best = new ItemStackUtil(stack, i);
    		}
    	}
    	
    	if (best != null && best.itemStack.getItem() != mc.player.getHeldItemMainhand().getItem() && bestSpeed > getBreakSpeed(event.pos, mc.player.getHeldItemMainhand())) {
    		oldSlot = mc.player.inventory.currentItem;
    		InventoryUtil.switchItem(best.slotId, false);
			mc.playerController.updateController();
    	}
    });
    
    @EventHandler
    private Listener<PlayerUpdateEvent> onPlayerUpdate = new Listener<>(event -> {
    	if (mc.playerController.isHittingBlock) {
    		timer.reset();
    	}
    	
		//Switch to old slot
    	if (!mc.playerController.isHittingBlock && oldSlot != -1 && switchBack.booleanValue() && timer.hasPassed(500)) {
			InventoryUtil.switchItem(oldSlot, false);
			mc.playerController.updateController();
			timer.reset();
			oldSlot = -1;
    	}
    });
    
	//Gets the breaking speed for the blockpos with the given itemstack
    public static float getBreakSpeed(BlockPos pos, ItemStack stack) {
        float f = 1 + stack.getDestroySpeed(mc.world.getBlockState(pos));

        if (f > 1.0F) {
            int i = EnchantmentHelper.getEfficiencyModifier(mc.player);

            if (i > 0 && !stack.isEmpty()) {
                f += (float) (i * i + 1);
            }
        }

        if (mc.player.isPotionActive(MobEffects.HASTE)) {
            f *= 1.0F + (float) (mc.player.getActivePotionEffect(MobEffects.HASTE).getAmplifier() + 1) * 0.2F;
        }

        if (mc.player.isPotionActive(MobEffects.MINING_FATIGUE)) {
            float f1;

            switch (mc.player.getActivePotionEffect(MobEffects.MINING_FATIGUE).getAmplifier()) {
                case 0:
                    f1 = 0.3F;
                    break;
                case 1:
                    f1 = 0.09F;
                    break;
                case 2:
                    f1 = 0.0027F;
                    break;
                case 3:
                default:
                    f1 = 8.1E-4F;
            }

            f *= f1;
        }

        if (mc.player.isInsideOfMaterial(Material.WATER) && !EnchantmentHelper.getAquaAffinityModifier(mc.player)) {
            f /= 5.0F;
        }

        if (!mc.player.onGround) {
            f /= 5.0F;
        }

        f = ForgeEventFactory.getBreakSpeed(mc.player, mc.world.getBlockState(pos), f, pos);
        return (f < 0 ? 0 : f);
    }
}
